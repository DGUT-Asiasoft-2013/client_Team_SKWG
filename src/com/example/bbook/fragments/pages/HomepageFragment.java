package com.example.bbook.fragments.pages;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.cookie.CookieIdentityComparator;

import com.example.bbook.BookDetailActivity;
import com.example.bbook.BuyActivity;
import com.example.bbook.R;
import com.example.bbook.ShopActivity;
import com.example.bbook.api.Goods;
import com.example.bbook.api.Page;
import com.example.bbook.api.Server;
import com.example.bbook.api.Shop;
import com.example.bbook.api.widgets.AvatarView;
import com.example.bbook.api.widgets.GoodsPicture;
import com.example.bbook.fragments.SlidingMenuFragment;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.R.raw;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.text.style.UpdateLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import util.AutoLoadListener;

public class HomepageFragment extends Fragment implements OnClickListener{
	//书籍展示页面

	SlidingMenuFragment slidingMenuFragment=new SlidingMenuFragment();
	
	PopupMenu popupMenuClassify,popupMenuSort;
	Menu menuClassify,menuSort;

	TextView textLoadMore;
	Button btnLoadMore;
	GridView bookView;
	AvatarView avatar;
	GoodsPicture goodsPicture;
	TextView textview;
	TextView goodsPrice;

	String sortStyle="createDate";
	String goodsType;
	String keyword;
	boolean isSearched=false,isSorted=false,isClassified=false;


	Goods goods;
	EditText editKeyword;
	ImageView btnSearch;
	Button sortByName;
	List<Goods> data;
	int page=0;
	
	//切换GridView和Listview标志
	boolean isGridview=true;
	ImageView changeView;
	ListView listView;
	
	//
	EditText minPrice,maxPrice;
	ImageView btnPricce;
	List<View> viewList;

	//viewPage
	View view1,view2,view3;
	ViewPager viewPager;
	ImageView cursor;
	int bmpWidth=0;
	int offset=0;
	int curIndex=0;
	View view;
	
	//侧拉菜单
	private DrawerLayout mDrawerLayout = null;
	private ImageView bt1;
	private Button bt2;
	private Button bt3;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if(view!=null){
			return view;
		}
		view=inflater.inflate(R.layout.fragment_home_page, null);
		btnSearch=(ImageView) view.findViewById(R.id.btn_search);
		editKeyword=(EditText) view.findViewById(R.id.edit_keyword);
		slidingMenuFragment=(SlidingMenuFragment) getFragmentManager().findFragmentById(R.id.sliding_menu);
		
		//侧拉菜单

		bt1 = (ImageView) view.findViewById(R.id.more_choice);
		bt2 = (Button) view.findViewById(R.id.btn1);
		bt3 = (Button) view.findViewById(R.id.btn2);
		bt1.setOnClickListener(this);
		bt2.setOnClickListener(this);
		bt3.setOnClickListener(this);
		mDrawerLayout = (DrawerLayout) view.findViewById(R.id.drawer_layout);
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
				
//				goodsType=slidingMenuFragment.getGoodsType();
//				if(goodsType.equals("全部")){
//					isClassified=false;
//				}else{
//					isClassified=true;
//				}
//				Log.d("goodstype", goodsType);
//				bookLoad();
			}
		});
	
		
		
		minPrice=(EditText) view.findViewById(R.id.min_price);
		maxPrice=(EditText) view.findViewById(R.id.max_price);
		btnPricce =(ImageView) view.findViewById(R.id.btn_price);
		btnPricce.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String min=minPrice.getText().toString();
				String max=maxPrice.getText().toString();
				goodsLoad(min,max);
			}
		});
		
		
		
		bookView=(GridView) view.findViewById(R.id.book_gridView);
		//bookView.setAdapter(bookAdapter);
		listView=(ListView) view.findViewById(R.id.book_listview);
		
		//向下滚动加载更多
		AutoLoadListener autoLoadListener = new AutoLoadListener(callBack);  
		bookView.setOnScrollListener(autoLoadListener);  
		
		changeView=(ImageView) view.findViewById(R.id.change_view);
		changeView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isGridview=!isGridview;
				updateLayout();
			}
		});
		

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
					sortStyle="shopName";
					isSorted=true;
					bookLoad();
					break;

				default:
					break;
				}


				return false;
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
				//	SearchBooksByKeyword();
				keyword=editKeyword.getText().toString();
				isSearched=true;
				bookLoad();
			}
		});

		return view;		
	}

	
	
	public void SortStyle(String sortStyle){
		String keyword=editKeyword.getText().toString();
		OkHttpClient client=Server.getSharedClient();
		Request request;
		//		if(editKeyword.getText().equals("")){
		//			request=Server.requestBuilderWithApi("goods/s/"+sortStyle)
		//					.get().build();
		//		}else{

		request=Server.requestBuilderWithApi("goods/sort/"+keyword+"/"+sortStyle)
				.get().build();
		//	}

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
							isSorted=true;
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
				if(isGridview){
					view = inflater.inflate(R.layout.goods_grid_item, null);	
				}
				else {
					view=inflater.inflate(R.layout.goods_list_item,null);
				}
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
					textview.setTextColor(Color.RED);
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

	//向下滚动加载
	AutoLoadListener.AutoLoadCallBack callBack = new AutoLoadListener.AutoLoadCallBack() {  

		public void execute() {  
			//            Utils.showToast("已经拖动至底部");  
			LoadMore();//这段代码是用来请求下一页数据的  
		}  

	};  
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//		if(!sortState){
		bookLoad();
		updateLayout();
		
		//		}
		//		else {
		//			SortStyle(sortStyle);
		//		}
	}

	public void SearchBooksByKeyword(){
		keyword=editKeyword.getText().toString();
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

	public  void Classify(String goodsType){
		OkHttpClient client=Server.getSharedClient();
		Request request=Server.requestBuilderWithApi("goods/classify/"+goodsType)
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
			request=Server.requestBuilderWithApi("goods/classify/"+goodsType)
					.get().build();
		}
		if(isSorted){
			request=Server.requestBuilderWithApi("goods/sort/"+sortStyle)
					.get().build();
		}
		if(isSearched&&isClassified){
			request=Server.requestBuilderWithApi("goods/search/"+keyword+"/classify/"+goodsType)
					.get().build();
		}
		if(isSearched&&isSorted){
			request=Server.requestBuilderWithApi("goods/search/"+keyword+"/sort/"+sortStyle)
					.get().build();
		}
		if(isClassified&&isSorted){
			request=Server.requestBuilderWithApi("goods/classify/"+goodsType+"/sort/"+sortStyle)
					.get().build();

		}
		if(isSearched&&isClassified&&isSorted){
			request=Server.requestBuilderWithApi("goods/search/"+keyword+"/classify/"+goodsType+"/sort/"+sortStyle)
					.get().build();
		}
		OkHttpClient client=Server.getSharedClient();



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
		if(isSearched&&isSorted){
			request=Server.requestBuilderWithApi("goods/search/"+keyword+"/sort/"+sortStyle+"?page="+(page+1))
					.get().build();
		}
		Server.getSharedClient().newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, final Response arg1) throws IOException {
				getActivity().runOnUiThread(new Runnable() {

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
					}
				});
			}
		});


	}
	
	//--------------------------------------------
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


	public void updateLayout(){
	
		AutoLoadListener autoLoadListener = new AutoLoadListener(callBack);  
		if(isGridview){
			bookView.setOnScrollListener(autoLoadListener);  
			changeView.setImageResource(R.drawable.icon_list_view);
			bookView.setVisibility(View.VISIBLE);
			bookView.setAdapter(bookAdapter);
			listView.setVisibility(View.GONE);
		}else {
			listView.setOnScrollListener(autoLoadListener);
			changeView.setImageResource(R.drawable.icon_grid_view);
			listView.setVisibility(View.VISIBLE);
			listView.setAdapter(bookAdapter);
			bookView.setVisibility(View.GONE);
		}
	
	}

//	public void chooseNum(final int position){
//		
//		AlertDialog.Builder builder=new Builder(getActivity());
//		builder.setTitle("请输入支付密码");
//		//把布局文件先填充成View对象
//		View view = View.inflate(getActivity(), R.layout.fragment_widget_number_plus_sub, null);
//		final TextView chooseNum=(TextView) view.findViewById(R.id.quantity);
//		//把填充得来的view对象设置为对话框显示内容
//		builder.setView(view);
//		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
//			public void onClick(DialogInterface dialog, int which) {
//				String string= chooseNum.getText().toString();
//				int quantity=Integer.parseInt(string);
//				addToShopCar(quantity, position);
//			}
//		});
//		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//			public void onClick(DialogInterface dialog, int which) {
//
//			}
//		});
//		builder.show();
//	//	int quantity=1;
//
//}
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
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						HomepageFragment.this.onResponse(arg0, body);
					}
				});
			}

			@Override
			public void onFailure(final Call arg0, final IOException arg1) {
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						onFailture(arg0, arg1);
					}
				});
			}
		});
	}
void onResponse(Call arg0, String responseBody){
	Toast.makeText(getActivity(), "加入购物车成功", Toast.LENGTH_SHORT).show();;
}

void onFailture(Call arg0, Exception arg1) {
	new AlertDialog.Builder(getActivity())
	.setTitle("失败")
	.setMessage(arg1.getLocalizedMessage())
	.show();
}



@Override
public void onClick(View v) {

	switch (v.getId()) {
	case R.id.more_choice:
		mDrawerLayout.openDrawer(Gravity.RIGHT);
		Toast.makeText(getActivity(), "bt1111111111", Toast.LENGTH_LONG).show();
		break;
	case R.id.btn1:
		Toast.makeText(getActivity(), "bt2222222222", Toast.LENGTH_LONG).show();
		break;
	case R.id.btn2:
		Toast.makeText(getActivity(), "bt33333333333", Toast.LENGTH_LONG).show();
		break;
	default:
		break;
	}

	
}
}
