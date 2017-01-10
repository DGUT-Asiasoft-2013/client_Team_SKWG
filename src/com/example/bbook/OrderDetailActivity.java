package com.example.bbook;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import com.example.bbook.api.Goods;
import com.example.bbook.api.Page;
import com.example.bbook.api.Server;
import com.example.bbook.api.entity.Orders;
import com.example.bbook.api.widgets.AvatarView;
import com.example.bbook.api.widgets.GoodsPicture;
import com.example.bbook.api.widgets.TitleBarFragment;
import com.example.bbook.api.widgets.TitleBarFragment.OnGoBackListener;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.Response;

public class OrderDetailActivity extends Activity {
	TitleBarFragment fragTitleBar;
	ListView list;
	TextView tvState, tvName, tvTel, tvAddress, tvShopName, tvSum, tvOrderId, tvCreateDate, tvPayDate, tvDeliverDate,tvCompleteData;
	AvatarView shopAvatar;
	List<Orders> listData; 
	String orderId;
	int page;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_detail);
		init();
		setEvent();
		orderId = getIntent().getStringExtra("orderId");
		Log.d("id", orderId);
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if(orderId != null) {
			setData(orderId);
		}
		if(listData != null) {
			setText();
		}
		
	}
	
	void setText() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		tvState.setText(listData.get(0).getOrdersState() + "");
		tvName.setText(listData.get(0).getBuyerName());
		tvTel.setText(listData.get(0).getBuyerPhoneNum());
		tvAddress.setText(listData.get(0).getBuyerAddress());
		tvShopName.setText(listData.get(0).getGoods().getShop().getShopName());
		Double sum = 0.0;
		for(int i = 0; i < listData.size(); i++) {
			sum += listData.get(i).getGoodsSum();
		}
		tvSum.setText("合计： ￥ " + sum);
		tvOrderId.setText("订单编号:  " + listData.get(0).getOrdersID());
		tvCreateDate.setText("创建时间:  " + format.format(listData.get(0).getCreateDate()));
		if(listData.get(0).getPayDate() != null) {
			tvPayDate.setText("付款时间:  " + format.format(listData.get(0).getPayDate()));
		}
		if(listData.get(0).getDeliverDate() != null) {
			tvDeliverDate.setText("发货时间:  " + format.format(listData.get(0).getDeliverDate()));
		}
		if(listData.get(0).getCompleteDate() != null) {
			tvCompleteData.setText("成交时间:  " + format.format(listData.get(0).getCompleteDate())); 
		}
		shopAvatar.load(Server.serverAdress + listData.get(0).getGoods().getShop().getShopImage());
	}
	void init() {
		list = (ListView) findViewById(R.id.list);
		fragTitleBar = (TitleBarFragment) getFragmentManager().findFragmentById(R.id.title_bar);
		tvState = (TextView) findViewById(R.id.state);
		tvName = (TextView) findViewById(R.id.name);
		tvTel = (TextView) findViewById(R.id.tel);
		tvAddress = (TextView) findViewById(R.id.address);
		tvShopName = (TextView) findViewById(R.id.shop_name);
		tvSum = (TextView) findViewById(R.id.sum);
		tvOrderId = (TextView) findViewById(R.id.order_id);
		tvCreateDate = (TextView) findViewById(R.id.create_date);
		tvPayDate = (TextView) findViewById(R.id.pay_date);
		tvDeliverDate = (TextView) findViewById(R.id.delever_date);
		tvCompleteData = (TextView) findViewById(R.id.complete_date);
		shopAvatar = (AvatarView) findViewById(R.id.shop_avatar);
	}
	
	void setData(String orderId) {
		Request request = Server.requestBuilderWithApi("orders/getordersofid/" + orderId + "?page=" + page).build();
		Server.getSharedClient().newCall(request).enqueue(new Callback() {
			
			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				final String body = arg1.body().string();
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						Page<Orders> data;
						try {
							data = new ObjectMapper().readValue(body, new TypeReference<Page<Orders>>() {
							});
							OrderDetailActivity.this.listData = data.getContent();
							OrderDetailActivity.this.page = data.getNumber();
							OrderDetailActivity.this.setText();
							listAdapter.notifyDataSetChanged();
						} catch (JsonParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (JsonMappingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
							
							
					}
				});
			}
			
			@Override
			public void onFailure(Call arg0, IOException arg1) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	void setEvent() {
		fragTitleBar.setTitleName("订单详情", 16);
		fragTitleBar.setOnGoBackListener(new OnGoBackListener() {
			
			@Override
			public void onGoBack() {
				finish();
			}
		});
		fragTitleBar.setBtnNextState(false);
		list.setAdapter(listAdapter);
	}
	
	private static class ViewHolder {
		GoodsPicture imgGoods;
		TextView tvGoodsName, tvGoodsType, tvGoodsQuantity, tvGoodsPrice;
	}
	
	BaseAdapter listAdapter = new BaseAdapter() {
		ViewHolder viewHolder;
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null) {
				LayoutInflater inflater = LayoutInflater.from(parent.getContext());
				convertView = inflater.inflate(R.layout.list_item_order_detail_goods, null);
				viewHolder = new ViewHolder();
				viewHolder.imgGoods = (GoodsPicture) convertView.findViewById(R.id.goods_image);
				viewHolder.tvGoodsName = (TextView) convertView.findViewById(R.id.goods_name);
				viewHolder.tvGoodsType = (TextView) convertView.findViewById(R.id.goods_type);
				viewHolder.tvGoodsQuantity = (TextView) convertView.findViewById(R.id.goods_quantity);
				viewHolder.tvGoodsPrice = (TextView) convertView.findViewById(R.id.goods_price);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			
			Orders order = listData.get(position);
			if(order != null) {
				viewHolder.imgGoods.load(Server.serverAdress + order.getGoods().getGoodsImage());
				viewHolder.tvGoodsName.setText(order.getGoods().getGoodsName());
				viewHolder.tvGoodsType.setText(order.getGoods().getGoodsType());
				viewHolder.tvGoodsQuantity.setText(order.getGoodsQTY());
				viewHolder.tvGoodsPrice.setText("¥" + order.getGoods().getGoodsPrice());
			}
			return convertView;
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
			return listData == null ? 0 : listData.size();
		}
	};
}
