package com.example.bbook.fragments.pages;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.example.bbook.PayActivity;
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
import android.content.Intent;
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
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OrdersToBePayFragment extends Fragment {
	View view;
	ListView list;
	List<Orders> listData;
	
//	ImageView ordersDelete;
	List<Orders> toBePayOrders;
	int page = 0;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if(view == null) {
			view = inflater.inflate(R.layout.fragment_page_to_be_pay, null);
			list = (ListView) view.findViewById(R.id.list);
		}
		
		list.setAdapter(listAdapter);
		list.setDivider(null);
		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		LoadMyOrders();
	}
	
	private class OrdersHolder {
		TextView tvName, tvType, tvQuantity, tvPrice, tvSum, tvOrderId, tvPay, tvCancel;
		GoodsPicture imgGoods;
	}
	
	BaseAdapter listAdapter=new BaseAdapter() {
		@SuppressLint("InflateParams")
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View view=null;
			OrdersHolder oHolder;
			if(convertView==null){
				oHolder = new OrdersHolder();
				LayoutInflater inflater=LayoutInflater.from(parent.getContext());
				view=inflater.inflate(R.layout.list_item_to_be_pay, null);
				oHolder.imgGoods = (GoodsPicture) view.findViewById(R.id.goods_image);
				oHolder.tvName = (TextView) view.findViewById(R.id.name);
				oHolder.tvPrice = (TextView) view.findViewById(R.id.price);
				oHolder.tvQuantity = (TextView) view.findViewById(R.id.quantity);
				oHolder.tvType = (TextView) view.findViewById(R.id.type);
				oHolder.tvSum = (TextView) view.findViewById(R.id.sum);
				oHolder.tvOrderId = (TextView) view.findViewById(R.id.order_id);
				oHolder.tvPay = (TextView) view.findViewById(R.id.pay);
				oHolder.tvCancel = (TextView) view.findViewById(R.id.cancel);
				view.setTag(oHolder);
			}else{
				view = convertView;
				oHolder = (OrdersHolder) view.getTag();
			}
			final Orders order=listData.get(position);

			oHolder.tvPrice.setText("￥"+order.getGoods().getGoodsPrice());
			oHolder.tvQuantity.setText("x" + order.getGoodsQTY());
			oHolder.tvSum.setText("合计:￥"+order.getGoodsSum()+"");
			oHolder.imgGoods.load(Server.serverAdress + order.getGoods().getGoodsImage());
			oHolder.tvName.setText(order.getGoods().getGoodsName());
			oHolder.tvType.setText(order.getGoods().getGoodsType());
			oHolder.tvOrderId.setText("订单号:" + order.getOrdersID());
			oHolder.tvPay.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					onPay(order);
				}
			});
			oHolder.tvCancel.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					onCancel();
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
	
	public void onPay(Orders order) {
		if(toBePayOrders == null) {
			toBePayOrders = new ArrayList<Orders>();
		}
		toBePayOrders.add(order);
		Intent itnt = new Intent(getActivity(), PayActivity.class);
		itnt.putExtra("toBePayOrders", (Serializable)toBePayOrders);
		startActivity(itnt);
	}
	
	public void onCancel() {
		
	}
	
	public void LoadMyOrders(){
		OkHttpClient client=Server.getSharedClient();
		Request request=Server.requestBuilderWithApi("orders/findall/2?page=" + page)
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
}