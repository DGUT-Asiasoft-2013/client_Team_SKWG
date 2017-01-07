package com.example.bbook;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.example.bbook.api.Bill;
import com.example.bbook.api.Page;
import com.example.bbook.api.Server;
import com.example.bbook.api.widgets.TitleBarFragment;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class MyBillActivity extends Activity {
	TitleBarFragment billTitleBar;
	ListView myBillList;
	
	View loadMore;
	TextView textLoadMore;
	List<Bill> dataList;
	int page = 0;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_page_bill_list);
		
		billTitleBar = (TitleBarFragment) getFragmentManager().findFragmentById(R.id.mybill_titlebar);
		LayoutInflater inflater = this.getLayoutInflater();
		loadMore = inflater.inflate(R.layout.load_more_button, null);
		textLoadMore = (TextView) loadMore.findViewById(R.id.text);
		myBillList = (ListView) findViewById(R.id.bill_list);
		myBillList.addFooterView(loadMore);
		
		myBillList.setAdapter(ListAdapter);
		
		loadMore.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				loadMore();
			}
		});
		
		myBillList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				onItemClicked(position);
			}
		});
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		load();
	}
	
	
	void loadMore(){
		loadMore.setEnabled(false);
		textLoadMore.setText("加载中...");
		
		Request request = Server.requestBuilderWithApi("GetBill/" + (page+1)).get().build();
		Server.getSharedClient().newCall(request).enqueue(new Callback() {
			
			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						loadMore.setEnabled(true);
						textLoadMore.setText("加载更多");
					}
				});
				try {
					final Page<Bill> data= new ObjectMapper().readValue(arg1.body().string(), new TypeReference<Page<Bill>>(){});
					if(data.getNumber() > page) {
						
						
						runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								if(dataList == null) {
									dataList = data.getContent();
								} else {
									dataList.addAll(data.getContent());
								}
								page = data.getNumber();
								ListAdapter.notifyDataSetChanged();
							}
						});
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			
			
			@Override
			public void onFailure(Call arg0, IOException arg1) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	
	BaseAdapter ListAdapter = new BaseAdapter() {
		@SuppressLint("inflateParams")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = null;
			if(convertView == null) {
				LayoutInflater inflate = LayoutInflater.from(parent.getContext());
				view = inflate.inflate(R.layout.bill_list_item, null);
			} else {
				view = convertView;
			}
			TextView paytype = (TextView) view.findViewById(R.id.billshow_pay_type);
			TextView money = (TextView) view.findViewById(R.id.billshow_money);
			TextView date = (TextView) view.findViewById(R.id.billshow_createtime);
			TextView item = (TextView) view.findViewById(R.id.billshow_item);
			Bill bill = dataList.get(position);
			int state = bill.getBillState();
			paytype.setTextSize(15);
			money.setText("余额:"+bill.getMoney().toString());
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			date.setText(dateFormat.format(bill.getCreateDate()));
			if(state==1){
			item.setText("+"+bill.getItem().toString());
			item.setTextColor(Color.rgb(51, 204, 51));
			}else {item.setText("-"+bill.getItem().toString());
			item.setTextColor(Color.RED);}
			return view;
		}
		
		@Override
		public long getItemId(int position) {
			return position;
		}
		
		@Override
		public Object getItem(int position) {
			return dataList.get(position);
		}
		
		@Override
		public int getCount() {
			return dataList == null ? 0 : dataList.size();
		}
	};
	
	
	void load(){
		billTitleBar.setBtnBackState(true);
		billTitleBar.setBtnNextState(false);
		billTitleBar.setTitleState(true);
		billTitleBar.setTitleName("账单", 16);
		billTitleBar.setOnGoBackListener(new TitleBarFragment.OnGoBackListener() {
			@Override
			public void onGoBack() {
				// TODO Auto-generated method stub
				finish();
			}
		});
		Request request = Server
//				.requestBuilderWithApi(String.format("GetBill/%d",page))
				.requestBuilderWithApi("GetBill/"+page)
				.get().build();
		
		Server.getSharedClient().newCall(request).enqueue(new Callback() {
			
			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				final Page<Bill> data = new ObjectMapper()
						.readValue(arg1.body().string(), new TypeReference<Page<Bill>>() {
				});
				runOnUiThread(new Runnable() {
					public void run() {
						MyBillActivity.this.page = data.getNumber();
						MyBillActivity.this.dataList = data.getContent();
						ListAdapter.notifyDataSetInvalidated();
					}
				});
			}
			
			@Override
			public void onFailure(Call arg0, IOException arg1) {
				onFailture(arg0,arg1);
			}
		});
	}
	void onFailture(Call arg0, Exception arg1) {
		new AlertDialog.Builder(this)
		.setMessage(arg1.getLocalizedMessage())
		.setNegativeButton("OK", null)
		.show();
	}
	
	void onItemClicked(int position){
		int state = dataList.get(position).getBillState();
		
		String moneyType;
		String moneyItem = dataList.get(position).getItem().toString();
		Date createDate = dataList.get(position).getCreateDate();
		String billNumber = dataList.get(position).getBillNumber().toString();
		String remain = dataList.get(position).getMoney().toString();
		String detial = dataList.get(position).getDetial();
		if(state==1){
			if(detial.equals("充值")){
				moneyType = "充值";
			}else {moneyType = "收入";}
		}else{moneyType = "支出";}
		
		Intent itnt =new Intent(MyBillActivity.this,BillItemDetialActivity.class);
		itnt.putExtra("MoneyItem", moneyItem);
		itnt.putExtra("MoneyType", moneyType);
		itnt.putExtra("CreateDate", createDate);
		itnt.putExtra("BillNumber", billNumber);
		itnt.putExtra("Remain", remain);
		itnt.putExtra("Detial",detial);
		
		startActivity(itnt);
		MyBillActivity.this.overridePendingTransition(R.anim.slide_in_right,0);
	}
}
