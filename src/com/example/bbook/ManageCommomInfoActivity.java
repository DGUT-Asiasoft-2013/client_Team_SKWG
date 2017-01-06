package com.example.bbook;

import java.io.IOException;
import java.util.List;

import com.example.bbook.api.Article;
import com.example.bbook.api.Page;
import com.example.bbook.api.Server;
import com.example.bbook.api.entity.CommomInfo;
import com.example.bbook.api.widgets.AvatarView;
import com.example.bbook.api.widgets.TitleBarFragment;
import com.example.bbook.api.widgets.TitleBarFragment.OnGoBackListener;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class ManageCommomInfoActivity extends Activity{
	TitleBarFragment fragTitleBar;
	final static int SELECTED_INFO_CODE = 1;
	List<CommomInfo> dataList;
	ListView infoList;
	TextView infoName, infoAddress, infoTel;
	CommomInfo defaultInfo;
	CommomInfo selectedInfo;
	int page = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_info_manage);
		init();
		infoList.setAdapter(listAdapter);
		setEvent();
		
	}

	private void setEvent() {
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
		fragTitleBar.setOnGoBackListener(new OnGoBackListener() {
			
			@Override
			public void onGoBack() {
				finish();
			}
		});
		fragTitleBar.setTitleName("管理收货地址", 16);
		fragTitleBar.setBtnNextState(false);
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		getDefault();
		reload();
	}

	void getDefault() {
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

	private static class InfoHolder {
		TextView tvName, tvAddress, tvTel, tvEdit, tvDelete;
		CheckBox cbSetDefault;
	}
	BaseAdapter listAdapter = new BaseAdapter() {

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = null;
			InfoHolder infoHolder;
			if(convertView==null){
				infoHolder = new InfoHolder();
				LayoutInflater inflater= LayoutInflater.from(parent.getContext());
				view =inflater.inflate(R.layout.list_item_commom_info, null);
				infoHolder.tvName = (TextView) view.findViewById(R.id.name);
				infoHolder.tvAddress = (TextView) view.findViewById(R.id.address);
				infoHolder.tvTel = (TextView) view.findViewById(R.id.tel);
				infoHolder.tvEdit = (TextView) view.findViewById(R.id.edit);
				infoHolder.tvDelete = (TextView) view.findViewById(R.id.delete);
				infoHolder.cbSetDefault = (CheckBox) view.findViewById(R.id.set_default);
				view.setTag(infoHolder);
			}else{
				view = convertView;
				infoHolder = (InfoHolder) view.getTag();
			}

			final CommomInfo info = dataList.get(position);
			infoHolder.tvName.setText("收货人： " + info.getName());
			infoHolder.tvAddress.setText("收货地址: " + info.getAddress());
			infoHolder.tvTel.setText("联系电话: " + info.getTel());
			infoHolder.tvDelete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					onDelete(info);
				}
			});
			infoHolder.tvEdit.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					onEdit(info);
				}
			});
			infoHolder.cbSetDefault.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					setDefault(info);
				}
			});
			infoHolder.cbSetDefault.setChecked(info.isDefaultInfo());
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

	void setInfo() {
		if(defaultInfo != null) {
			infoName.setText("收货人： " + defaultInfo.getName());
			infoAddress.setText("收货地址: " + defaultInfo.getAddress());
			infoTel.setText("联系电话: " + defaultInfo.getTel());
			Log.d("info", defaultInfo.getName() + "   " + defaultInfo.getAddress() + "    " + defaultInfo.getTel() + "   " + defaultInfo.getPostCode());
		}
	}
	protected void goAdd() {
		Intent itnt = new Intent(ManageCommomInfoActivity.this, AddCommomInfoActivity.class);
		startActivity(itnt);
	}

	void setDefault(CommomInfo info) {
		Request request = Server.requestBuilderWithApi("/commominfo/setdefault/" + info.getId()).get().build();
		Server.getSharedClient().newCall(request).enqueue(new Callback() {
			
			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						ManageCommomInfoActivity.this.reload();
					}
				});
			}
			
			@Override
			public void onFailure(Call arg0, IOException arg1) {
				
			}
		});
	}

	void onDelete(CommomInfo info) {
		Request request = Server.requestBuilderWithApi("/commominfo/delete/" + info.getId()).get().build();
		Server.getSharedClient().newCall(request).enqueue(new Callback() {
			
			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						ManageCommomInfoActivity.this.reload();
					}
				});
			}
			
			@Override
			public void onFailure(Call arg0, IOException arg1) {
				
			}
		});
	}

	void onEdit(CommomInfo info) {
		Intent itnt = new Intent(ManageCommomInfoActivity.this, EditCommomInfoActivity.class);
		startActivity(itnt);
	}
}
