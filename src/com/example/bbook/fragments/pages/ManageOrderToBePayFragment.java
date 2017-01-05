package com.example.bbook.fragments.pages;

import java.io.IOException;
import java.util.List;

import com.example.bbook.OrderDetailActivity;
import com.example.bbook.R;
import com.example.bbook.api.Page;
import com.example.bbook.api.Server;
import com.example.bbook.api.entity.Orders;
import com.example.bbook.api.widgets.GoodsPicture;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ManageOrderToBePayFragment extends Fragment{
	View view;
	ListView list;
	List<Orders> listData;
	TextView orderState;
	ImageView ordersDelete;
	
	int page = 0;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if(view == null) {
			view = inflater.inflate(R.layout.fragment_page_all_orders, null);
			list = (ListView) view.findViewById(R.id.list);
		}
		list.setAdapter(listAdapter);
		list.setDivider(null);
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent itnt = new Intent(getActivity(), OrderDetailActivity.class);
				itnt.putExtra("order", listData.get(position));
				startActivity(itnt);
			}
		});
		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		load();
	}
	

	public void load(){
		OkHttpClient client=Server.getSharedClient();

		Request request=Server.requestBuilderWithApi("orders/ordersOfSeller/2")
				.get().build();

		client.newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				final String responseStr=arg1.body().string();
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						try {
							Page<Orders> data=new ObjectMapper()
									.readValue(responseStr, new TypeReference<Page<Orders>>() {});
							listData=data.getContent();
							page=data.getNumber();
							listAdapter.notifyDataSetInvalidated();
						} catch (JsonParseException e) {
							e.printStackTrace();
						} catch (JsonMappingException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				});

			}

			@Override
			public void onFailure(Call arg0, IOException arg1) {

			}
		});
	}
	
	void onSend(Orders order) {
		MultipartBody.Builder body = new MultipartBody.Builder().addFormDataPart("state", 4 + "");
		Request request = Server.requestBuilderWithApi("order/" + order.getOrdersID()).post(body.build()).build();

		Server.getSharedClient().newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(getActivity(), "已发货，等待买家确认收货", Toast.LENGTH_SHORT).show();
						ManageOrderToBePayFragment.this.load();
					}
				});
			}

			@Override
			public void onFailure(Call arg0, IOException arg1) {
			}
		});
	}
	
	BaseAdapter listAdapter=new BaseAdapter() {
		@SuppressLint("InflateParams")
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View view=null;
			if(convertView==null){
				LayoutInflater inflater=LayoutInflater.from(parent.getContext());
				view=inflater.inflate(R.layout.list_item_manage_order, null);
			}else{
				view =convertView;
			}
			final Orders order=listData.get(position);
			orderState=(TextView) view.findViewById(R.id.order_state);
			ordersDelete=(ImageView) view.findViewById(R.id.orders_delete);
			TextView tvOrderId = (TextView) view.findViewById(R.id.order_id);
			TextView tvOrderType = (TextView) view.findViewById(R.id.type);
			TextView goodsPrice=(TextView) view.findViewById(R.id.goods_price);
			TextView goodsCount=(TextView) view.findViewById(R.id.goods_count);
			TextView goodsSum=(TextView) view.findViewById(R.id.orders_sum);
			TextView tvName = (TextView) view.findViewById(R.id.name);
			TextView tvSend = (TextView) view.findViewById(R.id.send);
			TextView tvRefund = (TextView) view .findViewById(R.id.refund);
			GoodsPicture goodsPicture=(GoodsPicture) view.findViewById(R.id.goods_picture);
			
			switch (order.getOrdersState()) {
			case 0:
				orderState.setText("订单取消");
				tvSend.setVisibility(View.GONE);
				tvRefund.setVisibility(View.GONE);
				break;
			case 1:
				orderState.setText("订单完成");
				tvSend.setVisibility(View.GONE);
				tvRefund.setVisibility(View.GONE);
				break;
			case 2:
				orderState.setText("待付款");
				tvSend.setVisibility(View.GONE);
				tvRefund.setVisibility(View.GONE);
				break;
			case 3:
				orderState.setText("待发货");
				tvSend.setVisibility(View.VISIBLE);
				tvRefund.setVisibility(View.GONE);
				break;
			case 4:
				orderState.setText("待收货");
				tvSend.setVisibility(View.GONE);
				tvRefund.setVisibility(View.GONE);
				break;
			case 5:
				orderState.setText("已收货");
				tvSend.setVisibility(View.GONE);
				tvRefund.setVisibility(View.GONE);
				break;
			case 6:
				orderState.setText("退货中");
				tvSend.setVisibility(View.GONE);
				tvRefund.setVisibility(View.GONE);
			default:
				break;
			}
			tvOrderId.setText("订单号:" + order.getOrdersID());
			tvOrderType.setText("类型:" + order.getGoods().getGoodsType());
			tvName.setText(order.getGoods().getGoodsName());
			goodsPrice.setText("￥"+order.getGoods().getGoodsPrice());
			goodsCount.setText("x" + order.getGoodsQTY());
			goodsSum.setText("合计:￥"+order.getGoodsSum());
			goodsPicture.load(Server.serverAdress+order.getGoods().getGoodsImage());
			tvSend.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					onSend(order);
				}
			});
			if(order.getOrdersState()==1){
				orderState.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						
					}
				});
			}
			ordersDelete.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					//删除订单
//					goOrderDelete(position);
				}
			});
			
			
			return view;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public Object getItem(int position) {
			return listData.get(position);
		}

		@Override
		public int getCount() {
			return listData==null?0:listData.size();
		}
	};
}
