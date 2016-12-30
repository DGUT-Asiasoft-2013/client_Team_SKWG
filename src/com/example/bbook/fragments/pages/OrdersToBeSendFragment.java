package com.example.bbook.fragments.pages;

import java.io.IOException;
import java.util.List;

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

public class OrdersToBeSendFragment extends Fragment{
	View view;
	ListView list;
	List<Orders> listData;
	
	int page = 0;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if(view == null) {
			view = inflater.inflate(R.layout.fragment_page_to_be_send, null);
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
		TextView tvName, tvType, tvQuantity, tvPrice, tvSum, tvOrderId;
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
				view=inflater.inflate(R.layout.list_item_to_be_send, null);
				oHolder.imgGoods = (GoodsPicture) view.findViewById(R.id.goods_image);
				oHolder.tvName = (TextView) view.findViewById(R.id.name);
				oHolder.tvPrice = (TextView) view.findViewById(R.id.price);
				oHolder.tvQuantity = (TextView) view.findViewById(R.id.quantity);
				oHolder.tvType = (TextView) view.findViewById(R.id.type);
				oHolder.tvSum = (TextView) view.findViewById(R.id.sum);
				oHolder.tvOrderId = (TextView) view.findViewById(R.id.order_id);
				view.setTag(oHolder);
			}else{
				view = convertView;
				oHolder = (OrdersHolder) view.getTag();
			}
			Orders order=listData.get(position);

			oHolder.tvPrice.setText("￥"+order.getGoods().getGoodsPrice());
			oHolder.tvQuantity.setText("x" + order.getGoodsQTY());
			oHolder.tvSum.setText("合计:￥"+order.getGoodsSum()+"");
			oHolder.imgGoods.load(Server.serverAdress + order.getGoods().getGoodsImage());
			oHolder.tvName.setText(order.getGoods().getGoodsName());
			oHolder.tvType.setText(order.getGoods().getGoodsType());
			oHolder.tvOrderId.setText("订单号:" + order.getOrdersID());
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
	
	
	
	public void LoadMyOrders(){
		OkHttpClient client=Server.getSharedClient();
		Request request=Server.requestBuilderWithApi("orders/findall/3?page=" + page)
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
