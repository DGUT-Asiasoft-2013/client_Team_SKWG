package com.example.bbook;

import java.io.IOException;
import java.util.List;

import com.example.bbook.api.Goods;
import com.example.bbook.api.Page;
import com.example.bbook.api.Server;
import com.example.bbook.api.Shop;
import com.example.bbook.api.widgets.GoodsPicture;
import com.example.bbook.fragments.SlidingMenuFragment;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.PopupMenu.OnMenuItemClickListener;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import util.AutoLoadListener;

public class SearchBooksActivity extends Activity  implements OnClickListener{
	//书籍展示页面

	//滑动窗口
	SlidingMenuFragment slidingMenuFragment=new SlidingMenuFragment();
	//弹出窗口
	PopupMenu popupMenuClassify,popupMenuSort;
	Menu menuClassify,menuSort;

	//	AvatarView avatar;
	//商品图片、店名、价格、商品名、销量
	GoodsPicture goodsPicture;
	TextView shopName;
	TextView goodsPrice;
	TextView goodsName;
	TextView goodsSales;

	String sortStyle="createDate";
	String typeStr="全部",authorStr="全部";
	String keyword;
	boolean isSearched=false,isSorted=false,isClassified=false;
	boolean isTypeSelected=false,isAuthorSelected=false;


	EditText editKeyword;
	ImageView btnSearch;

	Goods goods;
	Button sortByName;
	List<Goods> data;
	int page=0;

	//切换GridView和Listview标志
	boolean isGridview=true;
	ImageView changeView;
	ListView goodsListView;
	GridView goodsGridView;

	//最小金额，最多金额
	EditText minPrice,maxPrice;
	ImageView btnPricce;

	private DrawerLayout mDrawerLayout = null;
	private ImageView bt1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_books);

		init();
	}

	//初始化函数
	public void init(){

		btnSearch=(ImageView) findViewById(R.id.btn_search);
		btnSearch.setOnClickListener(this);
		editKeyword=(EditText) findViewById(R.id.edit_keyword);

		//侧拉菜单
		bt1 = (ImageView)findViewById(R.id.more_choice);
		bt1.setOnClickListener(this);
		slidingMenuFragment=(SlidingMenuFragment) getFragmentManager().findFragmentById(R.id.sliding_menu);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

		//按价格搜索
		minPrice=(EditText) findViewById(R.id.min_price);
		maxPrice=(EditText) findViewById(R.id.max_price);
		btnPricce =(ImageView) findViewById(R.id.btn_price);
		btnPricce.setOnClickListener(this);


		mDrawerLayout.setDrawerListener(new DrawerListener() {

			@Override
			public void onDrawerStateChanged(int arg0) {
				Log.d("David", "onDrawerStateChanged arg0 = " + arg0);
			}

			@Override
			public void onDrawerSlide(View arg0, float arg1) {
				Log.d("David", "onDrawerSlide arg1 = " + arg1);
			}

			@Override
			public void onDrawerOpened(View arg0) {
				Log.d("David", "onDrawerOpened");
			}

			@Override
			public void onDrawerClosed(View arg0) {
				Log.d("David", "onDrawerClosed");

				if(slidingMenuFragment.getIsSelected()){
					if (slidingMenuFragment.getAuthorSelected()) {
						authorStr=slidingMenuFragment.getAuthorStr();
						
						if(authorStr.equals("全部")){
							isAuthorSelected=false;
						}else{
							isAuthorSelected=true;
							isClassified=true;
						}
					}
					if (slidingMenuFragment.getTypeSelected()) {
						typeStr=slidingMenuFragment.getTypeStr();
						if(typeStr.equals("全部")){
							isTypeSelected=false;
						}else {
							isTypeSelected=true;
							isClassified=true;
						}
					}
					bookLoad();
				}
				//				if(slidingMenuFragment.getTypeSelected()&&slidingMenuFragment.getAuthorSelected()){
				//					typeStr=slidingMenuFragment.getTypeStr();
				//					authorStr=slidingMenuFragment.getAuthorStr();
				//				}
				//				if (slidingMenuFragment.getAuthorSelected()) {
				//					authorStr=slidingMenuFragment.getAuthorStr();
				//					
				//				}else if (slidingMenuFragment.getTypeSelected()) {
				//					typeStr=slidingMenuFragment.getTypeStr();
				//				}

			}
		});

		goodsGridView=(GridView)findViewById(R.id.book_gridView);
		goodsListView=(ListView) findViewById(R.id.book_listview);

		//向下滚动加载更多
		AutoLoadListener autoLoadListener = new AutoLoadListener(callBack);  
		goodsGridView.setOnScrollListener(autoLoadListener);  

		changeView=(ImageView) findViewById(R.id.change_view);
		changeView.setOnClickListener(this);
		
		popupMenuSort=new PopupMenu(this,findViewById(R.id.pop_menu_sort));
		menuSort=popupMenuSort.getMenu();
		findViewById(R.id.pop_menu_sort).setOnClickListener(this);
		//-------------------------
		getMenuInflater().inflate(R.menu.menu_sort, menuSort);
		//-------------------

		popupMenuSort.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				// TODO Auto-generated method stub
				switch (item.getItemId()) {
				case R.id.a:
					sortStyle="goodsPrice";
					//SortStyle(sortStyle);
					isSorted=true;
					bookLoad();
					break;
				case R.id.b:
					sortStyle="goodsName";
					//	SortStyle(sortStyle);
					isSorted=true;
					bookLoad();
					break;
				case R.id.c:
					sortStyle="goodsSales";
					isSorted=true;
					bookLoad();
					break;

				default:
					break;
				}
				return false;
			}
		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.more_choice:
			mDrawerLayout.openDrawer(Gravity.RIGHT);
			break;
		case R.id.btn_price:
			String min=minPrice.getText().toString();
			String max=maxPrice.getText().toString();
			goodsLoad(min,max);
			break;
		case R.id.change_view:
			isGridview=!isGridview;
			updateLayout();
			break;
		case R.id.pop_menu_sort:
			popupMenuSort.show();
			break;

		case R.id.btn_search:
			keyword=editKeyword.getText().toString();
			isSearched=true;
			bookLoad();
			break;
		default:
			break;
		}

	}

	//按金额搜索
	public void goodsLoad(String min,String max){
		Request request=Server.requestBuilderWithApi("goods/search/"+min+"/"+max)
				.get().build();
		OkHttpClient client=Server.getSharedClient();

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
							//Log.d("aaa",data.toString());
							SearchBooksActivity.this.data=data.getContent();
							SearchBooksActivity.this.page=data.getNumber();
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


	BaseAdapter bookAdapter=new BaseAdapter() {
		@SuppressLint("InflateParams")
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View view = null;

			if(convertView==null){
				LayoutInflater inflater = LayoutInflater.from(parent.getContext());
				if(isGridview){
					view = inflater.inflate(R.layout.goods_grid_item, null);	
				}
				else {
					view=inflater.inflate(R.layout.goods_list_item,null);
				}
			}else{
				view = convertView;
			}
			shopName=(TextView) view.findViewById(R.id.id);
			goodsPrice=(TextView) view.findViewById(R.id.price);
			goodsPicture=(GoodsPicture) view.findViewById(R.id.picture);
			goodsName=(TextView) view.findViewById(R.id.goods_name);
			goodsSales=(TextView) view.findViewById(R.id.goods_sales);

			goods=data.get(position);
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
					//Toast.makeText(getActivity(), "ID",Toast.LENGTH_SHORT).show();
					goShopActivity(position);
				}
			});

			view.findViewById(R.id.shopping_car).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					//chooseNum(position);
					addToShopCar(position);
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

	public void goBookDetailActivity(int position){
		goods=data.get(position);
		Intent intent=new Intent(SearchBooksActivity.this,BookDetailActivity.class);
		intent.putExtra("goods", goods);
		startActivity(intent);
	}
	public void goShopActivity(int position){
		Shop shop=data.get(position).getShop();
		Intent intent=new Intent(SearchBooksActivity.this,ShopActivity.class);
		intent.putExtra("shop", shop);
		startActivity(intent);
	}

	public void addToShopCar(int position){
		int quantity=1;
		goods=data.get(position);
		MultipartBody body = new MultipartBody.Builder()
				.addFormDataPart("quantity", quantity + "").build();


		Request request = Server.requestBuilderWithApi("shoppingcart/add/" + goods.getId())
				.method("post", null).post(body).build();

		Server.getSharedClient().newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(final Call arg0, final Response arg1) throws IOException {
				final String body = arg1.body().string();
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						SearchBooksActivity.this.onResponse(arg0, body);
					}
				});
			}

			@Override
			public void onFailure(final Call arg0, final IOException arg1) {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						onFailture(arg0, arg1);
					}
				});
			}
		});
	}


	void onResponse(Call arg0, String responseBody){
		Toast.makeText(SearchBooksActivity.this, "加入购物车成功", Toast.LENGTH_SHORT).show();;
	}

	void onFailture(Call arg0, Exception arg1) {
		new AlertDialog.Builder(this)
		.setTitle("失败")
		.setMessage(arg1.getLocalizedMessage())
		.show();
	}


	//加载书本
	public void bookLoad(){
		Request request;
		request=Server.requestBuilderWithApi("goods/s")
				.get().build();
		if(isSearched){
			request=Server.requestBuilderWithApi("goods/search/"+keyword)
					.get().build();
		}
		if(isClassified){
			if(isAuthorSelected&&isTypeSelected){
				request=Server.requestBuilderWithApi("goods/search/"+ authorStr+"/classify/"+typeStr)
						.get().build();
			}
			else if(isTypeSelected){
				request=Server.requestBuilderWithApi("goods/classify/"+typeStr)
						.get().build();
			}else if(isAuthorSelected){
				request=Server.requestBuilderWithApi("goods/search/"+authorStr)
						.get().build();
			}
			
		}
		if(isSorted){
			request=Server.requestBuilderWithApi("goods/sort/"+sortStyle)
					.get().build();
		}
		if(isSearched&&isClassified){
			request=Server.requestBuilderWithApi("goods/search/"+keyword+"/classify/"+typeStr)
					.get().build();
		}
		if(isSearched&&isSorted){
			request=Server.requestBuilderWithApi("goods/search/"+keyword+"/sort/"+sortStyle)
					.get().build();
		}
		if(isClassified&&isSorted){
			request=Server.requestBuilderWithApi("goods/classify/"+typeStr+"/sort/"+sortStyle)
					.get().build();

		}
		if(isSearched&&isClassified&&isSorted){
			request=Server.requestBuilderWithApi("goods/search/"+keyword+"/classify/"+typeStr+"/sort/"+sortStyle)
					.get().build();
		}

		OkHttpClient client=Server.getSharedClient();



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
							//Log.d("aaa",data.toString());
							SearchBooksActivity.this.data=data.getContent();
							SearchBooksActivity.this.page=data.getNumber();
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


	//向下滚动加载
	AutoLoadListener.AutoLoadCallBack callBack = new AutoLoadListener.AutoLoadCallBack() {  

		public void execute() {  
			//            Utils.showToast("已经拖动至底部");  
			LoadMore();//这段代码是用来请求下一页数据的  
		}  

	}; 

	public void LoadMore(){
		//	btnLoadMore.setEnabled(false);
		//	textLoadMore.setText("加载更多");
		Request request;
		request=Server.requestBuilderWithApi("goods/s?page="+(page+1)).get().build();
		if(isSearched){
			request=Server.requestBuilderWithApi("goods/search/"+keyword+"?page="+(page+1))
					.get().build();
		}
		if(isSorted){
			request=Server.requestBuilderWithApi("goods/sort/"+sortStyle+"?page="+(page+1))
					.get().build();
		}
//		if(isClassified){
//			if(!(typeStr.equals("全部"))){
//				request=Server.requestBuilderWithApi("goods/classify/"+typeStr)
//						.get().build();
//			}
//			else	if(!(authorStr.equals("全部"))){
//				request=Server.requestBuilderWithApi("goods/search/"+authorStr)
//						.get().build();
//			}else{
//				request=Server.requestBuilderWithApi("goods/classify/"+authorStr)
//						.get().build();
//			}
//
//		}
		if(isClassified){
			if(isAuthorSelected&&isTypeSelected){
				request=Server.requestBuilderWithApi("goods/search/"+ authorStr+"/classify/"+typeStr)
						.get().build();
			}
			else if(isTypeSelected){
				request=Server.requestBuilderWithApi("goods/classify/"+typeStr)
						.get().build();
			}else if(isAuthorSelected){
				request=Server.requestBuilderWithApi("goods/search/"+authorStr)
						.get().build();
			}
			
		}
		if(isSearched&&isSorted){
			request=Server.requestBuilderWithApi("goods/search/"+keyword+"/sort/"+sortStyle+"?page="+(page+1))
					.get().build();
		}
		Server.getSharedClient().newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, final Response arg1) throws IOException {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
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
								runOnUiThread(new Runnable() {

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
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
					}
				});
			}
		});
	}

	public void updateLayout(){

		AutoLoadListener autoLoadListener = new AutoLoadListener(callBack);  
		if(isGridview){
			goodsGridView.setOnScrollListener(autoLoadListener);  
			changeView.setImageResource(R.drawable.icon_list_view);
			goodsGridView.setVisibility(View.VISIBLE);
			goodsGridView.setAdapter(bookAdapter);
			goodsListView.setVisibility(View.GONE);
		}else {
			goodsListView.setOnScrollListener(autoLoadListener);
			changeView.setImageResource(R.drawable.icon_grid_view);
			goodsListView.setVisibility(View.VISIBLE);
			goodsListView.setAdapter(bookAdapter);
			goodsGridView.setVisibility(View.GONE);
		}

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		bookLoad();
		updateLayout();
	}

}
