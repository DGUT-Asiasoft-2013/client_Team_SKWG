package com.example.bbook;

import java.io.IOException;
import java.util.List;

import com.example.bbook.api.Article;
import com.example.bbook.api.Page;
import com.example.bbook.api.Server;
import com.example.bbook.api.entity.CommomInfo;
import com.example.bbook.api.widgets.AvatarView;
import com.example.bbook.fragments.pages.ForumFragment;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class ManageCommomInfoActivity extends Activity{
	final static int SELECTED_INFO_CODE = 1;
	List<CommomInfo> dataList;
	ListView infoList;
	TextView infoName, infoAddress, infoTel, infoPostcode;
	CommomInfo defaultInfo;
	CommomInfo selectedInfo;
	int page = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_info_manage);
		init();
		infoList.setAdapter(listAdapter);
		findViewById(R.id.add_info).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				goAdd();
			}
		});
		infoList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				selectedInfo = dataList.get(position);
				if(selectedInfo != null) {
					Intent data = new Intent();
					data.putExtra("selectedInfo", selectedInfo);
					setResult(SELECTED_INFO_CODE, data);
					finish();
				}
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		Request request = Server.requestBuilderWithApi("commominfo/default").get().build();
		Server.getSharedClient().newCall(request).enqueue(new Callback() {
			
			@Override
			public void onResponse(Call arg0, final Response arg1) throws IOException {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						try {
							ManageCommomInfoActivity.this.defaultInfo = new ObjectMapper()
									.readValue(arg1.body().string(), CommomInfo.class);
							runOnUiThread(new Runnable() {
								
								@Override
								public void run() {
									ManageCommomInfoActivity.this.setInfo();
								}
							});
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
		
		reload();
	}
	
	void reload() {
		Request request = Server.requestBuilderWithApi("commominfo/myinfo/" + page)
				.get()
				.build();

		Server.getSharedClient().newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0,final Response arg1) throws IOException {
				try{
					final Page<CommomInfo> data = new ObjectMapper()
							.readValue(arg1.body().string(), new TypeReference<Page<CommomInfo>>(){});
					runOnUiThread(new Runnable() {
						public void run() {
							ManageCommomInfoActivity.this.page = data.getNumber();
							ManageCommomInfoActivity.this.dataList = data.getContent();
							listAdapter.notifyDataSetInvalidated();
						}
					});
				}catch (final Exception e) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							new AlertDialog.Builder(ManageCommomInfoActivity.this)
							.setMessage(e.getMessage())
							.show();
						}
					});  
				}
			}


			@Override
			public void onFailure(Call arg0,final IOException e) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						new AlertDialog.Builder(ManageCommomInfoActivity.this)
						.setMessage(e.getMessage())
						.show();
					}
				}); 
			}
		});
	}
	
	BaseAdapter listAdapter = new BaseAdapter() {

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = null;
			if(convertView==null){
				LayoutInflater inflater= LayoutInflater.from(parent.getContext());
				view =inflater.inflate(R.layout.list_item_commom_info, null);
			}else{
				view = convertView;
			}
			TextView name = (TextView)view.findViewById(R.id.name);
			TextView address = (TextView)view.findViewById(R.id.address);
			TextView tel = (TextView)view.findViewById(R.id.tel);
			TextView postcode = (TextView)view.findViewById(R.id.postcode);

			CommomInfo info = dataList.get(position);
			name.setText("收货人： " + info.getName());
			address.setText("收货地址: " + info.getAddress());
			tel.setText("联系电话: " + info.getTel());
			postcode.setText("邮编：" + info.getPostCode());
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
	
	void init() {
		infoName = (TextView)findViewById(R.id.info_name);
		infoAddress = (TextView) findViewById(R.id.info_address);
		infoTel = (TextView) findViewById(R.id.info_tel);
		infoPostcode = (TextView) findViewById(R.id.info_postcode);
		infoList = (ListView) findViewById(R.id.list);
	}
	
	void setInfo() {
		infoName.setText("收货人： " + defaultInfo.getName());
		infoAddress.setText("收货地址: " + defaultInfo.getAddress());
		infoTel.setText("联系电话: " + defaultInfo.getTel());
		infoPostcode.setText("邮编：" + defaultInfo.getPostCode());
		Log.d("info", defaultInfo.getName() + "   " + defaultInfo.getAddress() + "    " + defaultInfo.getTel() + "   " + defaultInfo.getPostCode());
	}
	protected void goAdd() {
		Intent itnt = new Intent(ManageCommomInfoActivity.this, AddCommomInfoActivity.class);
		startActivity(itnt);
	}
}
