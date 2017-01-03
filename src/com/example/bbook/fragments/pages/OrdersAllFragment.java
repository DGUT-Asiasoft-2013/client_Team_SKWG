package com.example.bbook.fragments.pages;

import java.io.IOException;
import java.util.List;

import com.example.bbook.MyOrdersActivity;
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
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import util.AutoLoadListener;
import util.AutoLoadListener.AutoLoadCallBack;

public class OrdersAllFragment extends Fragment {
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
		list.setOnScrollListener(new AutoLoadListener(callback));
		list.setAdapter(listAdapter);
		list.setDivider(null);
		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		LoadMyOrders();
	}
	
	BaseAdapter listAdapter=new BaseAdapter() {
		@SuppressLint("InflateParams")
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View view=null;
			if(convertView==null){
				LayoutInflater inflater=LayoutInflater.from(parent.getContext());
				view=inflater.inflate(R.layout.list_item_my_orders, null);
			}else{
				view =convertView;
			}
			Orders order=listData.get(position);
			orderState=(TextView) view.findViewById(R.id.order_state);
			ordersDelete=(ImageView) view.findViewById(R.id.orders_delete);
			TextView tvOrderId = (TextView) view.findViewById(R.id.order_id);
			TextView tvOrderType = (TextView) view.findViewById(R.id.type);
			TextView goodsPrice=(TextView) view.findViewById(R.id.goods_price);
			TextView goodsCount=(TextView) view.findViewById(R.id.goods_count);
			TextView goodsSum=(TextView) view.findViewById(R.id.orders_sum);
			TextView tvName = (TextView) view.findViewById(R.id.name);
			GoodsPicture goodsPicture=(GoodsPicture) view.findViewById(R.id.goods_picture);
			switch (order.getOrdersState()) {
			case 0:
				orderState.setText("订单取消");
				break;
			case 1:
				orderState.setText("订单完成");
				break;
			case 2:
				orderState.setText("待付款");
				break;
			case 3:
				orderState.setText("已付款");
				break;
			case 4:
				orderState.setText("已发货");
				break;
			case 5:
				orderState.setText("已收货");
				break;
			case 6:
				orderState.setText("退货中");
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
			
			if(order.getOrdersState()==1){
				orderState.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						
					}
				});
			}
//			shopName.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					goShopActivity(position);
//				}
//			});
			ordersDelete.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					//删除订单
					goOrderDelete(position);
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
	
	public void goOrderDelete(final int position){
		AlertDialog.Builder builder=new Builder(getActivity());
		builder.setMessage("是否删除订单？");
		builder.setNegativeButton("取消",null);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				deleteOrder(position);
			}
		});
		builder.show();
	}
	
	public void deleteOrder(int position){
		OkHttpClient client=Server.getSharedClient();
		Request request=Server.requestBuilderWithApi("orders/delete/"+listData.get(position).getOrdersID())
				.get().build();
		client.newCall(request).enqueue(new Callback() {
			
			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				String responseStr=arg1.body().string();
				final Boolean isDeleted=new ObjectMapper().readValue(responseStr, Boolean.class);
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if(isDeleted){
							Toast.makeText(getActivity(), "删除成功", Toast.LENGTH_SHORT).show();
							LoadMyOrders();
						}
					}
				});
			}
			
			@Override
			public void onFailure(Call arg0, IOException arg1) {
				
			}
		});
	}
	
	// 下滑加载更多
	AutoLoadListener.AutoLoadCallBack callback = new AutoLoadCallBack() {
		
		@Override
		public void execute() {
			loadMore();
		}
	};
	
	
	
	public void LoadMyOrders(){
		OkHttpClient client=Server.getSharedClient();

		Request request=Server.requestBuilderWithApi("orders/findall")
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
	
	private void loadMore() {
		OkHttpClient client=Server.getSharedClient();

		Request request=Server.requestBuilderWithApi("orders/findall?page=" + (page+1))
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
							if(data.getNumber() > page) {
								if(listData == null) {
									listData=data.getContent();
								} else {
									listData.addAll(data.getContent());
								}
							}
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
}
