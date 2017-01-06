package com.example.bbook.fragments.pages;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.example.bbook.AddNoteActivity;
import com.example.bbook.CommentTomeActivity;
import com.example.bbook.ForumContentActivity;
import com.example.bbook.MyArticleActivity;
import com.example.bbook.MyCommentActivity;
import com.example.bbook.R;
import com.example.bbook.SearchArticleActivity;
import com.example.bbook.BrowseImgActivity;
import com.example.bbook.api.Article;
import com.example.bbook.api.Page;
import com.example.bbook.api.Server;
import com.example.bbook.api.widgets.AvatarView;
import com.example.bbook.api.widgets.RectangleView;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import util.AutoLoadListener;
import util.MyListener;
import util.PullToRefreshLayout;
import util.PullableListView;

public class ForumFragment extends Fragment {

	PopupMenu myMenu;
	Menu menu;

	int page=0;
	List<Article>data;


	//	View btnLoadMore;
	//	TextView textLoadMore;
	PullToRefreshLayout pullToRefreshLayout;
	PullableListView listView;
	View view;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (view==null) {
			view=inflater.inflate(R.layout.fragment_page_forum, null);
			//			btnLoadMore= inflater.inflate(R.layout.load_more_button, null);
			//			textLoadMore= (TextView)btnLoadMore.findViewById(R.id.text);

			myMenu=new PopupMenu(getActivity(),view.findViewById(R.id.img_aboutme));
			menu=myMenu.getMenu();
			getActivity().getMenuInflater().inflate(R.menu.menu_article, menu);

			pullToRefreshLayout=(PullToRefreshLayout)view.findViewById(R.id.refresh_view);   //获取自定义layout

			listView =(PullableListView)view.findViewById(R.id.content_view);
			listView.setAdapter(listAdapter);
			//向下滚动加载更多
			//			AutoLoadListener autoLoadListener = new AutoLoadListener(callBack);  
			//			listView.setOnScrollListener(autoLoadListener);  
			//			listView.addFooterView(btnLoadMore);

			ImageView img_myart=(ImageView)view.findViewById(R.id.img_aboutme);
			ImageView img_addnote=(ImageView)view.findViewById(R.id.img_addnote);
			ImageView img_search=(ImageView)view.findViewById(R.id.img_search);

			myMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {

				@Override
				public boolean onMenuItemClick(MenuItem item) {
					switch (item.getItemId()) {
					case R.id.myArticle:
						gomyart();
						break;
					case R.id.commentTome:
						gocommentTome();
						break;
					case R.id.myComment:
						gomycomment();
						break;
					default:
						break;
					}
					return false;
				}

			});
			img_search.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent itnt =new Intent(getActivity(),SearchArticleActivity.class);
					startActivity(itnt);
					getActivity().overridePendingTransition(R.anim.slide_in_buttom,0);

				}
			});

			img_myart.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					myMenu.show();
				}
			});

			img_addnote.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					goaddnote();
				}
			});

			listView.setOnItemClickListener(new  AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					onItemClicked(position);
				}
			});

			//自定义布局上拉下拉操作监听
			pullToRefreshLayout.setOnRefreshListener(new MyListener(){
				//下拉刷新操作
				@Override
				public void onRefresh(final PullToRefreshLayout pullToRefreshLayout){
					reload();
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
		return view;
	}

	//刷新帖子
	@Override
	public void onResume() {
		super.onResume();
		reload();
	}

	//	//向下滚动加载
	//	AutoLoadListener.AutoLoadCallBack callBack = new AutoLoadListener.AutoLoadCallBack() {  
	//
	//		public void execute() {  
	//			//            Utils.showToast("已经拖动至底部");  
	//			loadmore();//这段代码是用来请求下一页数据的  
	//		}  
	//
	//	}; 

	//适配器为listview填充data文章内容
	BaseAdapter listAdapter = new BaseAdapter() {

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = null;
			if(convertView==null){
				LayoutInflater inflater= LayoutInflater.from(parent.getContext());
				view =inflater.inflate(R.layout.forum_list_item, null);
			}else{
				view = convertView;
			}
			TextView txt_author = (TextView)view.findViewById(R.id.text1);  //帖子作者
			TextView txt_title = (TextView)view.findViewById(R.id.text2);   //帖子标题
			TextView txt_text = (TextView)view.findViewById(R.id.text3);    //帖子内容
			TextView txt_date = (TextView)view.findViewById(R.id.text4);    //帖子日期
			TextView txt_comNum = (TextView)view.findViewById(R.id.text5);	//帖子评论数
			AvatarView avatar = (AvatarView)view.findViewById(R.id.avatar_fra);  //作者头像	
			RectangleView img1=(RectangleView)view.findViewById(R.id.img1);   //第一个图片
			RectangleView img2=(RectangleView)view.findViewById(R.id.img2);   //第二个图片
			RectangleView img3=(RectangleView)view.findViewById(R.id.img3);   //第三个图片
			LinearLayout imgLayout=(LinearLayout)view.findViewById(R.id.imglayout);
			img1.setClickable(true);
			img1.setFocusable(false);        //设置可点击不可获取焦点
			img2.setClickable(true);
			img2.setFocusable(false);        //设置可点击不可获取焦点
			img3.setClickable(true);
			img3.setFocusable(false);	     //设置可点击不可获取焦点
			Article article = data.get(position);

			//图片不为空显示图片
			if(article.getArticlesImage()==null||article.getArticlesImage().length()<=0){	//没有图片		
				imgLayout.setVisibility(LinearLayout.GONE);
			}else{       
				imgLayout.setVisibility(LinearLayout.VISIBLE);                                   //有图片
//				final String Img=article.getArticlesImage();   //传参
				final String[] articleImg = article.getArticlesImage().split("\\|");
				if(articleImg.length==3){                    //有三张图片
					for(int i=0;i<articleImg.length;i++){
						RectangleView[] imgs = new RectangleView[]{img1,img2,img3};
						imgs[i].setVisibility(RectangleView.VISIBLE);
						imgs[i].load(Server.serverAdress+articleImg[i]);
					}
					img1.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(getActivity(),BrowseImgActivity.class);
							intent.putExtra("Img", articleImg[0]);
							startActivity(intent);
						}
					});
					img2.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(getActivity(),BrowseImgActivity.class);
							intent.putExtra("Img", articleImg[1]);
							startActivity(intent);
						}
					});
					img3.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(getActivity(),BrowseImgActivity.class);
							intent.putExtra("Img", articleImg[2]);
							startActivity(intent);
						}
					});
				}else if (articleImg.length==2) {              //两张图片
					img3.setVisibility(RectangleView.GONE);
					for(int i=0;i<articleImg.length;i++){
						RectangleView[] imgs = new RectangleView[]{img1,img2};
						imgs[i].setVisibility(RectangleView.VISIBLE);
						imgs[i].load(Server.serverAdress+articleImg[i]);
					}
					img1.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(getActivity(),BrowseImgActivity.class);
							intent.putExtra("Img", articleImg[0]);
							startActivity(intent);
						}
					});
					img2.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(getActivity(),BrowseImgActivity.class);
							intent.putExtra("Img", articleImg[1]);
							startActivity(intent);
						}
					});
				}else{                     						//一张图片
					img1.setVisibility(RectangleView.VISIBLE);
					img1.load(Server.serverAdress+articleImg[0]);
					img2.setVisibility(RectangleView.GONE);
					img3.setVisibility(RectangleView.GONE);
					img1.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(getActivity(),BrowseImgActivity.class);
							intent.putExtra("Img", articleImg[0]);
							startActivity(intent);
						}
					});
				}
			
			}

			avatar.load(Server.serverAdress + article.getAuthorAvatar());
			txt_author.setText(article.getAuthorName());
			txt_title.setText(article.getTitle());
			txt_text.setText(article.getText());
			String dateStr = DateFormat.format("yyyy-MM-dd hh:mm", article.getCreateDate()).toString();
			txt_date.setText(dateStr);
			txt_comNum.setText(article.getCommentNum()+"");
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
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return data==null?0:data.size();
		}
	};

	//从服务器端获取解析数据到客户端data
	void reload(){
		Request request = Server.requestBuilderWithApi("forums")
				.method("GET", null)
				.build();

		Server.getSharedClient().newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0,final Response arg1) throws IOException {
				try{
					final Page<Article> data = new ObjectMapper()
							.readValue(arg1.body().string(), new TypeReference<Page<Article>>(){});
					getActivity().runOnUiThread(new Runnable() {
						public void run() {
							ForumFragment.this.page = data.getNumber();
							ForumFragment.this.data = data.getContent();
							listAdapter.notifyDataSetInvalidated();
						}
					});
				}catch (final Exception e) {
					e.printStackTrace();
				}
			}


			@Override
			public void onFailure(Call arg0,final IOException e) {
				e.printStackTrace();
			}
		});
	}

	//加载更多
	void loadmore(){
		//		btnLoadMore.setEnabled(false);
		//		textLoadMore.setText("载入中…");

		Request request = Server.requestBuilderWithApi("forums/"+(page+1)).get().build();
		Server.getSharedClient().newCall(request).enqueue(new Callback() {
			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				getActivity().runOnUiThread(new Runnable() {
					public void run() {
						//						btnLoadMore.setEnabled(true);
						//						textLoadMore.setText("加载更多");
					}
				});

				try{
					final Page<Article> forums = new ObjectMapper().readValue(arg1.body().string(), new TypeReference<Page<Article>>() {});
					if(forums.getNumber()>page){

						getActivity().runOnUiThread(new Runnable() {
							public void run() {
								if(data==null){
									data = forums.getContent();
								}else{
									data.addAll(forums.getContent());
								}
								page = forums.getNumber();
								listAdapter.notifyDataSetChanged();
							}
						});
					}
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}

			@Override
			public void onFailure(Call arg0, IOException arg1) {
				getActivity().runOnUiThread(new Runnable() {
					public void run() {
						//						btnLoadMore.setEnabled(true);
						//						textLoadMore.setText("加载更多");
					}
				});
			}
		});
	}

	//点击listview跳转到这条帖子的详情并传参
	public void onItemClicked( int position){
		String title = data.get(position).getTitle();
		String text = data.get(position).getText();
		String authorName = data.get(position).getAuthorName();
		String date = DateFormat.format("yyyy-MM-dd hh:mm", data.get(position).getCreateDate()).toString();
		String authorAvatar = data.get(position).getAuthorAvatar();
		Article content = data.get(position);

		Intent itnt =new Intent(getActivity(),ForumContentActivity.class);
		itnt.putExtra("Text", text);
		itnt.putExtra("Title", title);
		itnt.putExtra("AuthorName", authorName);
		itnt.putExtra("Date", date);
		itnt.putExtra("AuthorAvatar", authorAvatar);
		itnt.putExtra("Data",content);

		startActivity(itnt);
		getActivity().overridePendingTransition(R.anim.slide_in_right,0);
	}

	//跳到发帖子页面
	void goaddnote(){
		Intent itnt =new Intent(getActivity(),AddNoteActivity.class);
		startActivity(itnt);
		getActivity().overridePendingTransition(R.anim.slide_in_right,0);
	}

	//跳到我的帖子页面
	void gomyart(){
		Intent itnt =new Intent(getActivity(),MyArticleActivity.class);
		startActivity(itnt);
		getActivity().overridePendingTransition(R.anim.slide_in_left,0);

	}
	//跳到对我的帖子的评论页面
	void gocommentTome(){
		Intent itnt =new Intent(getActivity(),CommentTomeActivity.class);
		startActivity(itnt);
		getActivity().overridePendingTransition(R.anim.slide_in_left,0);

	}
	//跳到我发出的评论页面
	void gomycomment(){
		Intent itnt =new Intent(getActivity(),MyCommentActivity.class);
		startActivity(itnt);
		getActivity().overridePendingTransition(R.anim.slide_in_left,0);

	}
}
