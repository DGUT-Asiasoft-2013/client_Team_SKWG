package com.example.bbook;
import java.io.IOException;
import java.util.List;

import com.example.bbook.R;
import com.example.bbook.api.Goods;
import com.example.bbook.api.Page;
import com.example.bbook.api.Server;
import com.example.bbook.api.widgets.GoodsPicture;
import com.example.bbook.api.widgets.TitleBarFragment;
import com.example.bbook.api.widgets.TitleBarFragment.OnGoBackListener;
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
import util.MyListener;
import util.PullToRefreshLayout;
import util.PullableListView;

public class MyOffSaleGoodsActivity extends Activity {
	TitleBarFragment fragTitleBar;
	PullableListView list;
	List<Goods> listData;

	int page = 0;
	PullToRefreshLayout pullToRefreshLayout;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_offsale_goods);
		init();
		setEvent();

	}


	@Override
	protected void onResume() {
		super.onResume();
		load();
	}
	private void init() {
		list = (PullableListView) findViewById(R.id.list);
		fragTitleBar = (TitleBarFragment) getFragmentManager().findFragmentById(R.id.title_bar);
		pullToRefreshLayout=(PullToRefreshLayout)findViewById(R.id.refresh_view);

	}

	private void setEvent() {
		fragTitleBar.setTitleName("已下架的", 16);
		fragTitleBar.setOnGoBackListener(new OnGoBackListener() {

			@Override
			public void onGoBack() {
				finish();
				overridePendingTransition(R.anim.none, R.anim.slide_out_right);
			}
		});
		fragTitleBar.setBtnNextState(false);
		list.setAdapter(goodsAdapter);

		//自定义布局上拉下拉操作监听
		pullToRefreshLayout.setOnRefreshListener(new MyListener(){
			//下拉刷新操作
			@Override
			public void onRefresh(final PullToRefreshLayout pullToRefreshLayout){
				load();
				pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
			}
			//上拉加载更多操作
			@Override
			public void onLoadMore(final PullToRefreshLayout pullToRefreshLayout)
			{
				loadmore();
				pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
			}
		});
	}

	public void load() {

		OkHttpClient client = Server.getSharedClient();

		Request request = Server.requestBuilderWithApi("goods/mygoods/offsale").get().build();

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
							MyOffSaleGoodsActivity.this.listData = data.getContent();
							MyOffSaleGoodsActivity.this.page = data.getNumber();
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

	//加载更多
	public void loadmore(){
		Request request = Server.requestBuilderWithApi("goods/mygoods/offsale?page="+(page+1)).get().build();
		Server.getSharedClient().newCall(request).enqueue(new Callback() {
			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				try{
					final Page<Goods> goods = new ObjectMapper().readValue(arg1.body().string(), new TypeReference<Page<Goods>>() {});
					if(goods.getNumber()>page){

						runOnUiThread(new Runnable() {
							public void run() {
								if(listData==null){
									listData = goods.getContent();
								}else{
									listData.addAll(goods.getContent());
								}
								page = goods.getNumber();
								goodsAdapter.notifyDataSetChanged();
							}
						});
					}
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}

			@Override
			public void onFailure(Call arg0, IOException arg1) {

			}
		});
	}

	private void goOnSale(final Goods goods) {
		new AlertDialog.Builder(MyOffSaleGoodsActivity.this).setMessage("确定上架商品?")
		.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				onOnSale(goods);

			}
		})
		.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}

		}).show();
	};

	private void onOnSale(Goods goods) {
		Request request = Server.requestBuilderWithApi("goods/setonsale/" + goods.getId() + "?state=" + "true").build();
		Server.getSharedClient().newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						MyOffSaleGoodsActivity.this.load();
						Toast.makeText(MyOffSaleGoodsActivity.this, "商品上架架成功", Toast.LENGTH_SHORT).show();
					}
				});
			}

			@Override
			public void onFailure(Call arg0, IOException arg1) {
			}
		});
	}

	private void goEdit(Goods goods) {
		Intent itnt = new Intent(this,ChangeGoodsInfoActivity.class);
		itnt.putExtra("goods", goods);
		startActivity(itnt);
	}

	private static class GoodsHolder {
		GoodsPicture imgGoods;
		TextView tvGoodsName, tvGoodsPrice, tvGoodsType, tvGoodsCount, tvOnSale, tvEdit;
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
				gHolder.tvOnSale = (TextView) view.findViewById(R.id.offsale);
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
				gHolder.tvOnSale.setText("上架");
				gHolder.tvOnSale.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						goOnSale(goods);
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
