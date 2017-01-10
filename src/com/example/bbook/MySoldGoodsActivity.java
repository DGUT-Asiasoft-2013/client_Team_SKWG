package com.example.bbook;
import java.io.IOException;
import java.util.List;

import com.example.bbook.R;
import com.example.bbook.api.Page;
import com.example.bbook.api.Server;
import com.example.bbook.api.entity.Orders;
import com.example.bbook.api.widgets.GoodsPicture;
import com.example.bbook.api.widgets.TitleBarFragment;
import com.example.bbook.api.widgets.TitleBarFragment.OnGoBackListener;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import util.MyListener;
import util.PullToRefreshLayout;
import util.PullableListView;

public class MySoldGoodsActivity extends Activity {
	TitleBarFragment fragTitleBar;
	View view;
	PullableListView list;
	List<Orders> listData;

	TextView orderState;
	ImageView ordersDelete;
	int page = 0;
	PullToRefreshLayout pullToRefreshLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_sold_goods);
		init();
		setEvent();
	}

	private void init() {
		list = (PullableListView) findViewById(R.id.list);
		fragTitleBar = (TitleBarFragment) getFragmentManager().findFragmentById(R.id.title_bar);
		pullToRefreshLayout=(PullToRefreshLayout)findViewById(R.id.refresh_view);
	}

	@Override
	protected void onResume() {
		super.onResume();
		load();
	}

	private void setEvent() {
		fragTitleBar.setOnGoBackListener(new OnGoBackListener() {

			@Override
			public void onGoBack() {
				finish();
			}
		});
		fragTitleBar.setTitleName("已卖出的", 16);
		fragTitleBar.setBtnNextState(false);
		list.setAdapter(listAdapter);

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

	BaseAdapter listAdapter=new BaseAdapter() {
		@SuppressLint("InflateParams")
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View view=null;
			if(convertView==null){
				LayoutInflater inflater=LayoutInflater.from(parent.getContext());
				view=inflater.inflate(R.layout.list_item_my_orders, null);
			}else{
				view =convertView;
			}
			Orders order=listData.get(position);
			TextView orderState=(TextView) view.findViewById(R.id.order_state);
			ordersDelete=(ImageView) view.findViewById(R.id.orders_delete);
			TextView tvOrderId = (TextView) view.findViewById(R.id.order_id);
			TextView tvOrderType = (TextView) view.findViewById(R.id.type);
			TextView goodsPrice=(TextView) view.findViewById(R.id.goods_price);
			TextView goodsCount=(TextView) view.findViewById(R.id.goods_count);
			TextView goodsSum=(TextView) view.findViewById(R.id.orders_sum);
			TextView tvName = (TextView) view.findViewById(R.id.name);
			GoodsPicture goodsPicture=(GoodsPicture) view.findViewById(R.id.goods_picture);
			tvOrderId.setText("订单号:" + order.getOrdersID());
			tvOrderType.setText("类型:" + order.getGoods().getGoodsType());
			tvName.setText(order.getGoods().getGoodsName());
			goodsPrice.setText("￥"+order.getGoods().getGoodsPrice());
			goodsCount.setText("x" + order.getGoodsQTY());
			goodsSum.setText("合计:￥"+order.getGoodsSum());
			goodsPicture.load(Server.serverAdress+order.getGoods().getGoodsImage());
			orderState.setText("交易完成");


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

	public void load(){
		OkHttpClient client=Server.getSharedClient();

		Request request=Server.requestBuilderWithApi("orders/findall/5?page=0")
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

	//加载更多
	public void loadmore(){
		OkHttpClient client=Server.getSharedClient();

		Request request=Server.requestBuilderWithApi("orders/findall/5?page="+(page+1))
				.get().build();

		client.newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				try {
					final Page<Orders> data=new ObjectMapper().readValue(arg1.body().string(), new TypeReference<Page<Orders>>() {});
					if(data.getNumber()>page){
						runOnUiThread(new Runnable() {
							public void run() {
								if(listData==null) {
									listData=data.getContent();
								}else{
									listData.addAll(data.getContent());
								}
								page=data.getNumber();
								listAdapter.notifyDataSetInvalidated();
							}
						});
					}
				}catch (Exception ex) {
					ex.printStackTrace();
				}
			}

			@Override
			public void onFailure(Call arg0, IOException arg1) {

			}
		});
	}
}
