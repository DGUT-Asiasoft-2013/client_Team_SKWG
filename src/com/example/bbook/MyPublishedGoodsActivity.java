package com.example.bbook;

import java.io.IOException;
import java.util.List;

import com.example.bbook.api.Goods;
import com.example.bbook.api.Page;
import com.example.bbook.api.Server;
import com.example.bbook.api.Shop;
import com.example.bbook.api.widgets.GoodsPicture;
import com.example.bbook.api.widgets.TitleBarFragment;
import com.example.bbook.api.widgets.TitleBarFragment.OnGoBackListener;
import com.example.bbook.api.widgets.TitleBarFragment.OnGoNextListener;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MyPublishedGoodsActivity extends Activity {
	TitleBarFragment fragTitleBar;
	ListView list;
	List<Goods> listData;
	Shop shop;
	int page = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_published_goods);
		shop = (Shop) getIntent().getSerializableExtra("shop");
		init();
		setEvent();

	}
	
	private void setEvent() {
		fragTitleBar.setTitleName("已发布的", 16);
		fragTitleBar.setOnGoBackListener(new OnGoBackListener() {
			
			@Override
			public void onGoBack() {
				finish();
			}
		});
		fragTitleBar.setOnGoNextListener(new OnGoNextListener() {
			
			@Override
			public void onGoNext() {
				goAddGoods();
			}
		});
		fragTitleBar.setBtnNextText("添加", 12);
		list.setAdapter(goodsAdapter);
	}

	protected void goAddGoods() {
		Intent itnt = new Intent(MyPublishedGoodsActivity.this, AddGoodsActivity.class);
		itnt.putExtra("shop", shop);
		startActivity(itnt);
	}

	@Override
	protected void onResume() {
		super.onResume();
		load();
	}
	
	private void init() {
		list = (ListView) findViewById(R.id.list);
		fragTitleBar = (TitleBarFragment) getFragmentManager().findFragmentById(R.id.title_bar);
	}

	public void load() {

		OkHttpClient client = Server.getSharedClient();

		Request request = Server.requestBuilderWithApi("goods/mygoods/onsale").get().build();

		client.newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, final Response arg1) throws IOException {
				final String responseStr = arg1.body().string();
				Log.d("response", responseStr);
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						try {
							Page<Goods> data = new ObjectMapper().readValue(responseStr,
									new TypeReference<Page<Goods>>() {
							});
							MyPublishedGoodsActivity.this.listData = data.getContent();
							MyPublishedGoodsActivity.this.page = data.getNumber();
							goodsAdapter.notifyDataSetInvalidated();
						} catch (Exception e) {
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

	private void goOffSale(final Goods goods) {
		new AlertDialog.Builder(MyPublishedGoodsActivity.this).setMessage("确定下架商品?")
		.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				onOffSale(goods);
				
			}
		})
		.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
			
		}).show();
	};
	
	private void onOffSale(Goods goods) {
		Request request = Server.requestBuilderWithApi("goods/setonsale/" + goods.getId() + "?state=" + "false").build();
		Server.getSharedClient().newCall(request).enqueue(new Callback() {
			
			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						MyPublishedGoodsActivity.this.load();
						Toast.makeText(MyPublishedGoodsActivity.this, "商品下架成功", Toast.LENGTH_SHORT).show();
					}
				});
			}
			
			@Override
			public void onFailure(Call arg0, IOException arg1) {
			}
		});
	}
	
	private void goEdit(Goods goods) {
		
	}
	
	private static class GoodsHolder {
		GoodsPicture imgGoods;
		TextView tvGoodsName, tvGoodsPrice, tvGoodsType, tvGoodsCount, tvOffSale, tvEdit;
	}
	
	BaseAdapter goodsAdapter = new BaseAdapter() {
		@SuppressLint("InflateParams")
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View view = null;
			GoodsHolder gHolder;
			if (convertView == null) {
				gHolder = new GoodsHolder();
				LayoutInflater inflater = LayoutInflater.from(parent.getContext());
				view = inflater.inflate(R.layout.list_item_my_goods, null);
				gHolder.imgGoods = (GoodsPicture) view.findViewById(R.id.goods_image);
				gHolder.tvGoodsName = (TextView) view.findViewById(R.id.goods_name);
				gHolder.tvGoodsType = (TextView) view.findViewById(R.id.goods_type);
				gHolder.tvGoodsCount = (TextView) view.findViewById(R.id.goods_count);
				gHolder.tvGoodsPrice = (TextView) view.findViewById(R.id.goods_price);
				gHolder.tvEdit = (TextView) view.findViewById(R.id.edit);
				gHolder.tvOffSale = (TextView) view.findViewById(R.id.offsale);
				view.setTag(gHolder);
			} else {
				view = convertView;
				gHolder = (GoodsHolder) view.getTag();
			}
			final Goods goods = listData.get(position);
			if(goods != null) {
				gHolder.imgGoods.load(Server.serverAdress + goods.getGoodsImage());
				gHolder.tvGoodsName.setText(goods.getGoodsName());
				gHolder.tvGoodsType.setText("类型:" + goods.getGoodsType());
				gHolder.tvGoodsPrice.setText("￥" + goods.getGoodsPrice());
				gHolder.tvGoodsCount.setText("库存:" + goods.getGoodsCount());
				gHolder.tvOffSale.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						goOffSale(goods);
					}
				});
				gHolder.tvEdit.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						goEdit(goods);
					}
				});
			}

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
			return listData == null ? 0 : listData.size();
		}

		@Override
		public boolean isEnabled(int position) {
			return false;
		}
	};
}
