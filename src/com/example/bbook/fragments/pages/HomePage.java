package com.example.bbook.fragments.pages;

import java.io.IOException;
import java.util.List;

import com.example.bbook.R;
import com.example.bbook.SearchBooksActivity;
import com.example.bbook.api.Goods;
import com.example.bbook.api.Page;
import com.example.bbook.api.Server;
import com.example.bbook.api.widgets.GoodsPicture;
import com.example.bbook.fragments.MyGridView;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import Adapter.MyGridviewAdapter;
import android.R.raw;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomePage extends Fragment implements OnClickListener{
	View view=null;
	
	Goods goods;
	//商品图片、店铺名、价格、商品名、销量
	GoodsPicture goodsPicture;
	TextView textview;
	TextView goodsPrice;
	TextView goodsName;
	TextView goodsSales;
	TextView moreBooks;
	
	int bookPage1=0,bookPage2,bookPage3;
	TextView change1,change2,change3;
	
	MyGridView gridview1,gridview2,gridview3;
	MyGridviewAdapter bookAdapter1,bookAdapter2,bookAdapter3;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if(view!=null){
			return view;
		}
		view=inflater.inflate(R.layout.homepage_fragment,null);
		init();
		return view;
	}
	
	
	
	public void loadBook1(){
		Request request;
		request=Server.requestBuilderWithApi("goods/s")
				.get().build();

		OkHttpClient client=Server.getSharedClient();



		client.newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, final Response arg1) throws IOException {
				final String responseStr=arg1.body().string();
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						try {
							Page<Goods> data=new ObjectMapper()
									.readValue(responseStr, new TypeReference<Page<Goods>>() {
									});							
							bookPage1=data.getNumber();
							bookAdapter1.setData(data.getContent(),bookPage1);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
			}

			@Override
			public void onFailure(Call arg0, IOException arg1) {
				// TODO Auto-generated method stub

			}
		});

	}
	public void loadMoreBook1(){
		Request request;
		request=Server.requestBuilderWithApi("goods/s?page="+(bookPage1+1))
				.get().build();

		OkHttpClient client=Server.getSharedClient();



		client.newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, final Response arg1) throws IOException {
				final String responseStr=arg1.body().string();
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						try {
							Page<Goods> data=new ObjectMapper()
									.readValue(responseStr, new TypeReference<Page<Goods>>() {
									});					
							bookPage1=data.getNumber();
							bookAdapter1.setData(data.getContent(),bookPage1);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
			}

			@Override
			public void onFailure(Call arg0, IOException arg1) {
				// TODO Auto-generated method stub

			}
		});

	}
	
	public void loadBook2(){

		Request request;
		request=Server.requestBuilderWithApi("goods/s")
				.get().build();

		OkHttpClient client=Server.getSharedClient();
		client.newCall(request).enqueue(new Callback() {
			@Override
			public void onResponse(Call arg0, final Response arg1) throws IOException {
				final String responseStr=arg1.body().string();
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						try {
							Page<Goods> data=new ObjectMapper()
									.readValue(responseStr, new TypeReference<Page<Goods>>() {
									});							
							bookPage2=data.getNumber();
							bookAdapter2.setData(data.getContent(),bookPage2);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
			}

			@Override
			public void onFailure(Call arg0, IOException arg1) {
				// TODO Auto-generated method stub

			}
		});
	}
	
	public void loadMoreBook2(){

		Request request;
		request=Server.requestBuilderWithApi("goods/s?page="+(bookPage2+1))
				.get().build();

		OkHttpClient client=Server.getSharedClient();
		client.newCall(request).enqueue(new Callback() {
			@Override
			public void onResponse(Call arg0, final Response arg1) throws IOException {
				final String responseStr=arg1.body().string();
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						try {
							Page<Goods> data=new ObjectMapper()
									.readValue(responseStr, new TypeReference<Page<Goods>>() {
									});							
							bookPage2=data.getNumber();
							bookAdapter2.setData(data.getContent(),bookPage2);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
			}

			@Override
			public void onFailure(Call arg0, IOException arg1) {
				// TODO Auto-generated method stub

			}
		});
	}
	public void loadBook3(){

		Request request;
		request=Server.requestBuilderWithApi("goods/s")
				.get().build();

		OkHttpClient client=Server.getSharedClient();
		client.newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, final Response arg1) throws IOException {
				final String responseStr=arg1.body().string();
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						try {
							Page<Goods> data=new ObjectMapper()
									.readValue(responseStr, new TypeReference<Page<Goods>>() {
									});							
							bookPage3=data.getNumber();
							bookAdapter3.setData(data.getContent(),bookPage3);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
			}
			@Override
			public void onFailure(Call arg0, IOException arg1) {
				// TODO Auto-generated method stub

			}
		});
	}
	public void loadMoreBook3(){

		Request request;
		request=Server.requestBuilderWithApi("goods/s?page="+(bookPage3+1))
				.get().build();

		OkHttpClient client=Server.getSharedClient();
		client.newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, final Response arg1) throws IOException {
				final String responseStr=arg1.body().string();
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						try {
							Page<Goods> data=new ObjectMapper()
									.readValue(responseStr, new TypeReference<Page<Goods>>() {
									});							
							bookPage3=data.getNumber();
							bookAdapter3.setData(data.getContent(),bookPage3);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
			}
			@Override
			public void onFailure(Call arg0, IOException arg1) {
				// TODO Auto-generated method stub

			}
		});
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		loadBook1();
		loadBook2();
		loadBook3();
	}
	
	
	//初始化函数
	void init(){
		gridview1=(MyGridView) view.findViewById(R.id.book_gridView1);
		gridview2=(MyGridView) view.findViewById(R.id.book_gridView2);
		gridview3=(MyGridView) view.findViewById(R.id.book_gridView3);
		
		
		change1=(TextView) view.findViewById(R.id.change1);
		change1.setOnClickListener(this);
		change2=(TextView) view.findViewById(R.id.change2);
		change2.setOnClickListener(this);
		change3=(TextView) view.findViewById(R.id.change3);
		change3.setOnClickListener(this);
		moreBooks=(TextView) view.findViewById(R.id.more_book);
		moreBooks.setOnClickListener(this);		
		
		bookAdapter1=new MyGridviewAdapter(getActivity());
		gridview1.setAdapter(bookAdapter1);
		bookAdapter2=new MyGridviewAdapter(getActivity());
		gridview2.setAdapter(bookAdapter2);
		bookAdapter3=new MyGridviewAdapter(getActivity());
		gridview3.setAdapter(bookAdapter3);
		
		
		
	}



	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.change1:
			loadMoreBook1();
			break;
		case R.id.change2:
			loadMoreBook2();
			break;
		case R.id.change3:
			loadMoreBook3();
			break;
		case R.id.more_book:
			Intent intent=new Intent(getActivity(),SearchBooksActivity.class);
			startActivity(intent);
				
		default:
			break;
		}
	}


}
