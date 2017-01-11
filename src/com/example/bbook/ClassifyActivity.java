package com.example.bbook;

import java.io.IOException;
import java.util.List;

import com.example.bbook.api.Goods;
import com.example.bbook.api.Page;
import com.example.bbook.api.Server;
import com.example.bbook.api.Shop;
import com.example.bbook.api.widgets.GoodsPicture;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ClassifyActivity extends Activity {
	String goodsType;
	List<Goods> goodsList;
	GridView goodsView;
	int page=0;
	Goods goods;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_classify);
//		getGoodsType(getIntent().getStringExtra("title"));
		
		goodsType=getIntent().getStringExtra("title");
		goodsView=(GridView) findViewById(R.id.classify_gridview);
		goodsView.setAdapter(goodsAdapter);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		classifyGoods();
	}

//	public String getGoodsType(String typeStr){
//		String goodsType;
//		
//		switch (typeStr) {
//		case "小说":
//			goodsType="novel";
//			break;
//
//		default:
//			break;
//		}
//	}

	BaseAdapter goodsAdapter=new BaseAdapter() {

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View view=null;
			if(convertView==null){
				LayoutInflater inflater=LayoutInflater.from(parent.getContext());
//				view=inflater.inflate(R.layout.fragment_picture_name, null);
				view=inflater.inflate(R.layout.goods_grid_item, null);
			}else {
				view=convertView;
			}
//			TextView textview=(TextView) view.findViewById(R.id.id);
//			TextView goodsPrice=(TextView) view.findViewById(R.id.price);
//			GoodsPicture	 goodsPicture=(GoodsPicture) view.findViewById(R.id.picture);
//
			goods=goodsList.get(position);
//			textview.setText("商家:"+goods.getShop().getShopName());
//			goodsPrice.setText("价格："+goods.getGoodsPrice());
//			goodsPicture.load(Server.serverAdress+goods.getGoodsImage());

			
			TextView shopName=(TextView) view.findViewById(R.id.id);
			TextView goodsPrice=(TextView) view.findViewById(R.id.price);
			GoodsPicture goodsPicture=(GoodsPicture) view.findViewById(R.id.picture);
		TextView	goodsName=(TextView) view.findViewById(R.id.goods_name);
		TextView	goodsSales=(TextView) view.findViewById(R.id.goods_sales);

			shopName.setText("商家:"+goods.getShop().getShopName());
			goodsPrice.setText("￥："+goods.getGoodsPrice());
			goodsPicture.load(Server.serverAdress+goods.getGoodsImage());
			goodsName.setText(goods.getGoodsName());
			goodsSales.setText("销量:"+goods.getGoodsSales());
			goodsPicture.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View v) {
						goBookDetailActivity( position);
				}
			});
			shopName.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
				//	Toast.makeText(ClassifyActivity.this, "ID",Toast.LENGTH_SHORT).show();
						goShopActivity(position);
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
			return goodsList.get(position);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return goodsList==null?0:goodsList.size();
		}
	};

	public void classifyGoods(){
		OkHttpClient client=Server.getSharedClient();
		Request request=Server.requestBuilderWithApi("goods/classify/"+goodsType)
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
						Page<Goods> data;
						try {
							data = new ObjectMapper()
									.readValue(responseStr, new TypeReference<Page<Goods>>() {
									});
							goodsList=data.getContent();
							page=data.getNumber();
							goodsAdapter.notifyDataSetInvalidated();
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
						//	Log.d("aaa",data.toString());
					}
				});

			}

			@Override
			public void onFailure(Call arg0, IOException arg1) {
				// TODO Auto-generated method stub

			}
		});
	}

	public void goBookDetailActivity(int position){
		goods=goodsList.get(position);
		Intent intent=new Intent(ClassifyActivity.this,BookDetailActivity.class);
		intent.putExtra("goods", goods);
		startActivity(intent);
	}
	
	public void goShopActivity(int position){
		Shop shop=goodsList.get(position).getShop();
		Intent intent=new Intent(ClassifyActivity.this,ShopActivity.class);
		intent.putExtra("shop", shop);
		startActivity(intent);
	}
}
