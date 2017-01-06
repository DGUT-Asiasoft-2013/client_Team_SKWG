package com.example.bbook;

import java.io.IOException;
import java.util.List;

import com.example.bbook.api.Page;
import com.example.bbook.api.Server;
import com.example.bbook.api.entity.CommomInfo;
import com.example.bbook.api.widgets.TitleBarFragment;
import com.example.bbook.api.widgets.TitleBarFragment.OnGoBackListener;
import com.example.bbook.api.widgets.TitleBarFragment.OnGoNextListener;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class SelectCommomInfoActivity extends Activity {
	TitleBarFragment fragTitleBar;
	final static int SELECTED_INFO_CODE = 1;
	List<CommomInfo> dataList;
	ListView infoList;
	TextView infoName, infoAddress, infoTel;
	CommomInfo selectedInfo;
	int page = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_commominfo);
		init();
		infoList.setAdapter(listAdapter);
		infoList.setDivider(null);
		setEvent();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
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
							SelectCommomInfoActivity.this.page = data.getNumber();
							SelectCommomInfoActivity.this.dataList = data.getContent();
							listAdapter.notifyDataSetInvalidated();
						}
					});
				}catch (final Exception e) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							new AlertDialog.Builder(SelectCommomInfoActivity.this)
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
						new AlertDialog.Builder(SelectCommomInfoActivity.this)
						.setMessage(e.getMessage())
						.show();
					}
				}); 
			}
		});
	}
	
	private static class InfoHolder {
		TextView tvName, tvAddress, tvTel;
	}
	BaseAdapter listAdapter = new BaseAdapter() {

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = null;
			InfoHolder infoHolder;
			if(convertView==null){
				infoHolder = new InfoHolder();
				LayoutInflater inflater= LayoutInflater.from(parent.getContext());
				view =inflater.inflate(R.layout.list_item_select_commominfo, null);
				infoHolder.tvName = (TextView) view.findViewById(R.id.name);
				infoHolder.tvAddress = (TextView) view.findViewById(R.id.address);
				infoHolder.tvTel = (TextView) view.findViewById(R.id.tel);
				view.setTag(infoHolder);
			}else{
				view = convertView;
				infoHolder = (InfoHolder) view.getTag();
			}

			final CommomInfo info = dataList.get(position);
			infoHolder.tvName.setText("收货人： " + info.getName());
			infoHolder.tvAddress.setText("收货地址: " + info.getAddress());
			infoHolder.tvTel.setText("联系电话: " + info.getTel());
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
		infoName = (TextView)findViewById(R.id.name);
		infoAddress = (TextView) findViewById(R.id.address);
		infoTel = (TextView) findViewById(R.id.tel);
		infoList = (ListView) findViewById(R.id.list);
		fragTitleBar = (TitleBarFragment) getFragmentManager().findFragmentById(R.id.title_bar);
	}
	private void setEvent() {
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
		fragTitleBar.setOnGoBackListener(new OnGoBackListener() {
			
			@Override
			public void onGoBack() {
				finish();
			}
		});
		fragTitleBar.setOnGoNextListener(new OnGoNextListener() {
			
			@Override
			public void onGoNext() {
				Intent itnt = new Intent(SelectCommomInfoActivity.this, ManageCommomInfoActivity.class);
				startActivity(itnt);
			}
		});
		fragTitleBar.setTitleName("选择收货地址", 16);
		fragTitleBar.setBtnNextText("管理", 13);
	}
}
