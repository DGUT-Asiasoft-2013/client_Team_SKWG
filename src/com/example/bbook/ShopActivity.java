package com.example.bbook;

import java.io.IOException;
import java.util.List;

import com.example.bbook.api.Goods;
import com.example.bbook.api.Page;
import com.example.bbook.api.Server;
import com.example.bbook.api.Shop;
import com.example.bbook.api.widgets.GoodsPicture;
import com.example.bbook.fragments.pages.HomepageFragment;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.annotation.SuppressLint;
import android.app.Activity;
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
//商家页面
public class ShopActivity extends Activity {

	GridView goodsView;
	List<Goods> data;
	int page=0;
	Shop shop;
	GoodsPicture shopPicture;

	GoodsPicture goodsPicture;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shop);


		shop=(Shop) getIntent().getSerializableExtra("shop");
		//店家图片
		shopPicture=(GoodsPicture) findViewById(R.id.shop_picture);
		shopPicture.load(Server.serverAdress+shop.getShopImage());
		//店名、店主
		((TextView)findViewById(R.id.shop_name)).setText("店名:"+shop.getShopName());
		((TextView)findViewById(R.id.shop_owner)).setText("店主:"+shop.getOwner().getName());
		//商品列表
		goodsView=(GridView) findViewById(R.id.goods_gridview);
		goodsView.setAdapter(goodsAdapter);
		

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		GoodsLoad();

	}
	public void GoodsLoad(){

		OkHttpClient client=Server.getSharedClient();

		Request request=Server.requestBuilderWithApi("goods/get/"+shop.getId())
				.get().build();

		client.newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, final Response arg1) throws IOException {
				// TODO Auto-generated method stub
				final String responseStr=arg1.body().string();
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							Page<Goods> data=new ObjectMapper()
									.readValue(responseStr, new TypeReference<Page<Goods>>() {
									});
							ShopActivity.this.data=data.getContent();
							ShopActivity.this.page=data.getNumber();
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

	BaseAdapter goodsAdapter=new BaseAdapter() {
		@SuppressLint("InflateParams")
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View view = null;

			if(convertView==null){
				LayoutInflater inflater = LayoutInflater.from(parent.getContext());
				view = inflater.inflate(R.layout.fragment_picture_name, null);	
			}else{
				view = convertView;
			}
			TextView	goodsPrice=(TextView) view.findViewById(R.id.price);
			goodsPicture=(GoodsPicture) view.findViewById(R.id.picture);

			Goods	goods=data.get(position);
			goodsPrice.setText("价格："+goods.getGoodsPrice());
			//商品图片、点击事件
			goodsPicture.load(Server.serverAdress+goods.getGoodsImage());
			goodsPicture.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View v) {
			
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
			return data.get(position);
			//			return null;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return data==null?0:data.size();
			//			return 6;
		}
		@Override
		public boolean isEnabled(int position){
			return false;
		}
	};

}
