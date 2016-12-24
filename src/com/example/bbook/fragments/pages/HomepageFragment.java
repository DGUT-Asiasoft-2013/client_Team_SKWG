package com.example.bbook.fragments.pages;


import java.io.IOException;
import java.util.List;

import com.example.bbook.BookDetailActivity;
import com.example.bbook.R;
import com.example.bbook.ShopActivity;
import com.example.bbook.api.Goods;
import com.example.bbook.api.Page;
import com.example.bbook.api.Server;
import com.example.bbook.api.Shop;
import com.example.bbook.api.widgets.AvatarView;
import com.example.bbook.api.widgets.GoodsPicture;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.R.raw;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomepageFragment extends Fragment {
	//书籍展示页面

	PopupMenu popupMenuClassify,popupMenuSort;
	Menu menuClassify,menuSort;

	//	View btnLoadMore;
	TextView textLoadMore;
	Button btnLoadMore;
	GridView bookView;
	//ImageView imageView;
	AvatarView avatar;
	GoodsPicture goodsPicture;
	TextView textview;
	TextView goodsPrice;
	String sortStyle;
	boolean sortState=false;

	Goods goods;
	EditText editKeyword;
	ImageView btnSearch;
	Button sortByName;
	List<Goods> data;
	int page=0;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view=inflater.inflate(R.layout.fragment_home_page, null);

		btnSearch=(ImageView) view.findViewById(R.id.btn_search);
		editKeyword=(EditText) view.findViewById(R.id.edit_keyword);
		bookView=(GridView) view.findViewById(R.id.book_gridView);
		bookView.setAdapter(bookAdapter);

		//	btnLoadMore=inflater.inflate(R.layout.load_more_btn,null);
		//	textLoadMore=(TextView) btnLoadMore.findViewById(R.id.load_more_text);
		//bookView.addView(btnLoadMore, TRIM_MEMORY_UI_HIDDEN);

		//bookView.addView(btnLoadMore,0 );
//		view.findViewById(R.id.btn_laodmore).setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				LoadMore();
//			}
//		});
		popupMenuClassify=new PopupMenu(getActivity(),view.findViewById(R.id.pop_menu_classify));
		menuClassify=popupMenuClassify.getMenu();
		getActivity().getMenuInflater().inflate(R.menu.menu_classify, menuClassify);
		
		popupMenuSort=new PopupMenu(getActivity(),view.findViewById(R.id.pop_menu_sort));
		menuSort=popupMenuSort.getMenu();
		getActivity().getMenuInflater().inflate(R.menu.menu_sort, menuSort);

		popupMenuSort.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				// TODO Auto-generated method stub
				switch (item.getItemId()) {
				case R.id.a:
					sortStyle="goodsPrice";
					SortStyle(sortStyle);
					break;
				case R.id.b:
					sortStyle="goodsName";
					SortStyle(sortStyle);
					break;
				case R.id.c:

					break;
			
				default:
					break;
				}
				
				
				return false;
			}
		});
		popupMenuClassify.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				// TODO Auto-generated method stub

				switch (item.getItemId()) {
				case R.id.a:
					
					break;
				case R.id.b:
					
					break;
				case R.id.c:

					break;
				case R.id.d:

					break;
				case R.id.e:

					break;
				default:
					break;
				}
				return false;
			}
		});

		view.findViewById(R.id.pop_menu_classify).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				popupMenuClassify.show();
			}
		});
		
		view.findViewById(R.id.pop_menu_sort).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				popupMenuSort.show();
			}
		});
		btnSearch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SearchBooksByKeyword();
			}
		});

		return view;		
	}

	public void SortStyle(String sortStyle){
		String keyword=editKeyword.getText().toString();
		OkHttpClient client=Server.getSharedClient();

		Request request=Server.requestBuilderWithApi("goods/sort/"+keyword+"/"+sortStyle)
				.get().build();

		client.newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				// TODO Auto-generated method stub
				final String responseStr=arg1.body().string();

				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						try {
							Page<Goods> data=new ObjectMapper()
									.readValue(responseStr, new TypeReference<Page<Goods>>() {
									});
							HomepageFragment.this.data=data.getContent();
							HomepageFragment.this.page=data.getNumber();
							bookAdapter.notifyDataSetInvalidated();
							sortState=true;
						} catch (Exception e) {
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
	BaseAdapter bookAdapter=new BaseAdapter() {
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
			textview=(TextView) view.findViewById(R.id.id);
			goodsPrice=(TextView) view.findViewById(R.id.price);
			goodsPicture=(GoodsPicture) view.findViewById(R.id.picture);

			goods=data.get(position);
			textview.setText("商家:"+goods.getShop().getShopName());
			goodsPrice.setText("价格："+goods.getGoodsPrice());
			goodsPicture.load(Server.serverAdress+goods.getGoodsImage());

			goodsPicture.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View v) {
					goBookDetailActivity( position);
				}
			});
			textview.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Toast.makeText(getActivity(), "ID",Toast.LENGTH_SHORT).show();
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

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(!sortState){
			bookLoad();
		}
		else {
			SortStyle(sortStyle);
		}
	}

	public void SearchBooksByKeyword(){
		String keyword=editKeyword.getText().toString();
		//Log.d("aaa", keyword);
		OkHttpClient client=Server.getSharedClient();

		Request request=Server.requestBuilderWithApi("goods/search/"+keyword)
				.get().build();

		client.newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				// TODO Auto-generated method stub
				final String responseStr=arg1.body().string();
				Log.d("aaa", responseStr);
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub

						// TODO Auto-generated method stub
						try {
							Page<Goods> data=new ObjectMapper()
									.readValue(responseStr, new TypeReference<Page<Goods>>() {
									});
							//Log.d("aaa",data.toString());
							HomepageFragment.this.data=data.getContent();
							HomepageFragment.this.page=data.getNumber();
							bookAdapter.notifyDataSetInvalidated();
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


	public void bookLoad(){

		OkHttpClient client=Server.getSharedClient();

		Request request=Server.requestBuilderWithApi("goods/s")
				.get().build();

		client.newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, final Response arg1) throws IOException {
				// TODO Auto-generated method stub
				final String responseStr=arg1.body().string();


				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							Page<Goods> data=new ObjectMapper()
									.readValue(responseStr, new TypeReference<Page<Goods>>() {
									});
							//Log.d("aaa",data.toString());
							HomepageFragment.this.data=data.getContent();

							HomepageFragment.this.page=data.getNumber();
							bookAdapter.notifyDataSetInvalidated();
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
	//
	public void LoadMore(){


		btnLoadMore.setEnabled(false);
		textLoadMore.setText("加载更多");

		Request request=Server.requestBuilderWithApi("goods/s?page="+(page+1)).get().build();
		Server.getSharedClient().newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, final Response arg1) throws IOException {
				// TODO Auto-generated method stub
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						btnLoadMore.setEnabled(true);
						textLoadMore.setText("加载中");
						try {
							Page<Goods> feeds=new ObjectMapper()
									.readValue(arg1.body().string(),
											new TypeReference<Page<Goods>>() {
									});

							if(feeds.getNumber()>page){
								if(data==null){
									data=feeds.getContent();
								}else{
									data.addAll(feeds.getContent());
								}
								page=feeds.getNumber();
								getActivity().runOnUiThread(new Runnable() {

									@Override
									public void run() {
										// TODO Auto-generated method stub
										bookAdapter.notifyDataSetChanged();	
									}
								});
							}
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
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						btnLoadMore.setEnabled(true);
						textLoadMore.setText("���ظ���");
					}
				});
			}
		});


	}


	public void goBookDetailActivity(int position){
		goods=data.get(position);
		Intent intent=new Intent(getActivity(),BookDetailActivity.class);
		intent.putExtra("goods", goods);
		startActivity(intent);
	}

	public void goShopActivity(int position){
		Shop shop=data.get(position).getShop();
		Intent intent=new Intent(getActivity(),ShopActivity.class);
		intent.putExtra("shop", shop);
		startActivity(intent);
	}


}
