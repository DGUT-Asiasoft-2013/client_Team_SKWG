package com.example.bbook;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.bbook.api.Page;
import com.example.bbook.api.Server;
import com.example.bbook.api.entity.Orders;
import com.example.bbook.api.widgets.ManageOrderRefundBottom;
import com.example.bbook.api.widgets.ManageOrderRefundBottom.OnCheckClickedListener;
import com.example.bbook.api.widgets.ManageOrderRefundBottom.OnRefundClickedListener;
import com.example.bbook.api.widgets.OrderMiddleContent;
import com.example.bbook.api.widgets.OrderRefundBottomContent;
import com.example.bbook.api.widgets.OrderTopContent;
import com.example.bbook.api.widgets.TitleBarFragment;
import com.example.bbook.api.widgets.TitleBarFragment.OnGoBackListener;
import com.example.bbook.fragments.pages.OrdersAllFragment;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import util.OrderContent;

public class ManageOrderRefundActivity extends Activity {

	TitleBarFragment fragTitleBar;
	ListView list;
	List<Orders> listData;
	int page = 0;


	List<Orders> orderList = new ArrayList<>();
	Map<String , List<Orders>> dataset = new HashMap<>();
	List<OrderContent> orderContents = new ArrayList<>();
	void initDate() {
		for(int i = 0; i < listData.size(); i++) {
			if(orderList == null) {
				orderList = new ArrayList<>();
			} else {
				orderList.add(listData.get(i));
			}
		}
		for(int i = 0; i < orderList.size(); i++) {
			for(int j = orderList.size() - 1; j > i; j--) {
				if(orderList.get(i).getOrdersID().equals(orderList.get(j).getOrdersID())) {
					orderList.remove(j);
				}
			}
		}

		for(int i = 0; i < orderList.size(); i++) {
			Orders order;
			List<Orders> goodsOfOrder = new ArrayList<>();
			for(int j = 0; j < listData.size(); j++) {
				if(listData.get(j).getOrdersID().equals(orderList.get(i).getOrdersID())) {
					goodsOfOrder.add(listData.get(j));
				}
			}
			dataset.put(orderList.get(i).getOrdersID(), goodsOfOrder);
			Log.d("dataSetSize", dataset.size() + "");
		}
	}

	void initOrderContents() {

		for(int i = 0; i < orderList.size(); i++) {
			Double sum = 0.0;
			OrderTopContent topContent = new OrderTopContent(orderList.get(i));	// TOP
			orderContents.add(topContent);
			for(int j = 0; j < dataset.get(orderList.get(i).getOrdersID()).size(); j++) {
				OrderMiddleContent middleContent = new OrderMiddleContent(dataset.get(orderList.get(i).getOrdersID()).get(j));
				orderContents.add(middleContent);
				sum += dataset.get(orderList.get(i).getOrdersID()).get(j).getGoodsSum();
			}
			ManageOrderRefundBottom bottomContent = new ManageOrderRefundBottom(orderList.get(i), sum);
			orderContents.add(bottomContent);
		}
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manage_order_refund);
		init();
		setEvent();
	}

	private void init() {
		fragTitleBar = (TitleBarFragment) getFragmentManager().findFragmentById(R.id.title_bar);
		list = (ListView) findViewById(R.id.list);

	}

	private void setEvent() {
		list.setAdapter(listAdapter);
		fragTitleBar.setOnGoBackListener(new OnGoBackListener() {

			@Override
			public void onGoBack() {
				finish();
				overridePendingTransition(R.anim.none, R.anim.slide_out_right);
			}
		});
		fragTitleBar.setBtnNextState(false);
		fragTitleBar.setTitleName("退货", 13);
	}

	@Override
	protected void onResume() {
		super.onResume();
		reload();
	}

	void reload() {
		orderList = new ArrayList<>();
		dataset = new HashMap<>();
		orderContents = new ArrayList<>();
		load();
	}

	private void load() {

		OkHttpClient client=Server.getSharedClient();
		Request request=Server.requestBuilderWithApi("orders/findall/managerefund/" +  page)
				.get().build();

		client.newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				final String responseStr=arg1.body().string();
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						try {
							Page<Orders> data=new ObjectMapper()
									.readValue(responseStr, new TypeReference<Page<Orders>>() {});
							listData=data.getContent();
							page=data.getNumber();
							{
							ManageOrderRefundActivity.this.initDate();
							ManageOrderRefundActivity.this.initOrderContents();
							}
							{
							listAdapter.notifyDataSetChanged();
							}
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

	BaseAdapter listAdapter = new BaseAdapter() {
		LayoutInflater inflater;
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(orderContents.get(position) instanceof ManageOrderRefundBottom) {
				ManageOrderRefundBottom bottomContent = (ManageOrderRefundBottom) orderContents.get(position);
				final Orders order = bottomContent.getOrder();
				bottomContent.setOnRefundClickedListener(new OnRefundClickedListener() {

					@Override
					public void onRefundClicked() {
						goRefund(order);
					}
				});
				bottomContent.setOnCheckClickedListener(new OnCheckClickedListener() {
					
					@Override
					public void onCheckClicked() {
						goCheckBill();
					}
				});
			}
			return orderContents.get(position).getView(ManageOrderRefundActivity.this, convertView, inflater);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public Object getItem(int position) {
			return orderContents.get(position);
		}

		@Override
		public int getCount() {
			return orderContents.size();
		}

		public boolean isEnabled(int position) {
			return orderContents.get(position).isClickable();
		};
	};

	void goCheckBill() {
		Intent itnt = new Intent(ManageOrderRefundActivity.this, MyBillActivity.class);
		startActivity(itnt);
	}
	
	void goRefund(final Orders order) {
		if(order != null) {
			new AlertDialog.Builder(ManageOrderRefundActivity.this).setMessage("确认退款？")
			.setNegativeButton("取消", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}

			})
			.setPositiveButton("确定", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					onRefund(order);
				}
			}).show();
		}
	}

	// 退款
	void onRefund(Orders order) {
		MultipartBody.Builder body = new MultipartBody.Builder().addFormDataPart("state", 7 + "");
		Request request = Server.requestBuilderWithApi("order/" + order.getOrdersID()).post(body.build()).build();

		Server.getSharedClient().newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						ManageOrderRefundActivity.this.onResume();
						Toast.makeText(ManageOrderRefundActivity.this, "退款成功", Toast.LENGTH_SHORT).show();
					}
				});
			}

			@Override
			public void onFailure(Call arg0, IOException arg1) {
			}
		});
	} 
}
