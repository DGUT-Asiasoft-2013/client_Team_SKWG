package com.example.bbook;

import java.io.IOException;
import java.util.List;

import org.w3c.dom.Text;

import com.example.bbook.api.Page;
import com.example.bbook.api.Server;
import com.example.bbook.api.entity.Orders;
import com.example.bbook.api.widgets.GoodsPicture;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore.Video;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MyOrdersActivity extends Activity {
	ListView ordersList;
	List<Orders> ordersData;
	int Page=0;
	TextView orderState;
	TextView shopName;
	ImageView ordersDelete;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_orders);	
		ordersList=(ListView) findViewById(R.id.orders_list);
		ordersList.setAdapter(listAdapter);



	}

	BaseAdapter listAdapter=new BaseAdapter() {
		@SuppressLint("InflateParams")
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View view=null;
			if(convertView==null){
				LayoutInflater inflater=LayoutInflater.from(parent.getContext());
				view=inflater.inflate(R.layout.my_orders_list_item, null);
			}else{
				view =convertView;
			}
			Orders order=ordersData.get(position);
			orderState=(TextView) view.findViewById(R.id.order_state);
			ordersDelete=(ImageView) view.findViewById(R.id.orders_delete);
			shopName=(TextView) view.findViewById(R.id.shop_name);
			TextView goodsPrice=(TextView) view.findViewById(R.id.goods_price);
			TextView goodsCount=(TextView) view.findViewById(R.id.goods_count);
			TextView goodsSum=(TextView) view.findViewById(R.id.orders_sum);
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
			default:
				break;
			}

			goodsPrice.setText("￥"+order.getGoods().getGoodsPrice());
			shopName.setText(order.getGoods().getShop().getShopName());
			goodsCount.setText(order.getGoodsQTY());
			goodsSum.setText("￥"+order.getGoodsSum()+"");
			goodsPicture.load(Server.serverAdress+order.getGoods().getGoodsImage());
			
			if(order.getOrdersState()==1){
				orderState.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						
					}
				});
			}
			shopName.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					goShopActivity(position);
				}
			});
			ordersDelete.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					//删除订单
				//	goOrderDelete(position);
				}
			});
			
			
			return view;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return ordersData.get(position);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return ordersData==null?0:ordersData.size();
		}
	};


	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		LoadMyOrders();

	}
	public void LoadMyOrders(){
		OkHttpClient client=Server.getSharedClient();

		Request request=Server.requestBuilderWithApi("orders/findall")
				.get().build();

		client.newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				// TODO Auto-generated method stub
				final String responseStr=arg1.body().string();
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							Page<Orders> data=new ObjectMapper()
									.readValue(responseStr, new TypeReference<Page<Orders>>() {});
							ordersData=data.getContent();
							Page=data.getNumber();
							listAdapter.notifyDataSetInvalidated();
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
	
	public void goShopActivity(int position){
		Intent intent=new Intent(MyOrdersActivity.this,ShopActivity.class);
		intent.putExtra("shop",ordersData.get(position).getGoods().getShop());
		startActivity(intent);
	}
	
}
