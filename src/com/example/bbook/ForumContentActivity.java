package com.example.bbook;

import java.io.IOException;
import java.util.List;

import com.example.bbook.api.Article;
import com.example.bbook.api.Comment;
import com.example.bbook.api.Page;
import com.example.bbook.api.Server;
import com.example.bbook.api.widgets.AvatarView;
import com.example.bbook.api.widgets.RectangleView;
import com.example.bbook.api.widgets.TitleBarFragment;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.Response;
import util.MyListener;
import util.PullToRefreshLayout;
import util.PullableListView;

public class ForumContentActivity extends Activity {
	Comment comment;
	Article article;
	Button btn_like,btn_reply;
	ListView listView;
	TextView count_like;
	FrameLayout fraLike,fraComment,fraReward;
	ImageView img_like;

	TitleBarFragment fragTitleBar;
	View listViewHead;
	//	View btnLoadMore;
	//	TextView textLoadMore;

	List<Comment> data;
	int page =0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forumcontent);

		fragTitleBar = (TitleBarFragment) getFragmentManager().findFragmentById(R.id.me_titlebar);
		fragTitleBar.setSplitLineState(true);
		fragTitleBar.setBtnNextState(false);
		fragTitleBar.setTitleName("详情", 16);

		//		btnLoadMore=LayoutInflater.from(this).inflate(R.layout.load_more_button, null);
		//		textLoadMore= (TextView)btnLoadMore.findViewById(R.id.text);

		LayoutInflater inflater = LayoutInflater.from(ForumContentActivity.this);
		listViewHead = inflater.inflate(R.layout.forum_top_item, null);//这是头部View

		count_like =(TextView)listViewHead.findViewById(R.id.count_like);		//这里可以获取头部的组件
		img_like=(ImageView)listViewHead.findViewById(R.id.img_like);

		PullToRefreshLayout pullToRefreshLayout=(PullToRefreshLayout)findViewById(R.id.refresh_view);   //获取自定义layout

		listView =(PullableListView)findViewById(R.id.content_view);
		listView.addHeaderView(listViewHead);//添加进去
		listView.setAdapter(listAdapter);
		//		setListViewHeightBasedOnChildren(listView);

		String title=getIntent().getStringExtra("Title");  
		String text=getIntent().getStringExtra("Text");
		String authorName=getIntent().getStringExtra("AuthorName");
		String date=getIntent().getStringExtra("Date");

		article = (Article)getIntent().getSerializableExtra("Data");

		TextView txt_title=(TextView)listViewHead.findViewById(R.id.txt_title);        //头部item标题
		TextView txt_author=(TextView)listViewHead.findViewById(R.id.txt_author); 
		TextView txt_text=(TextView)listViewHead.findViewById(R.id.txt_text);
		TextView txt_date=(TextView)listViewHead.findViewById(R.id.txt_date);
		AvatarView avatar = (AvatarView)listViewHead.findViewById(R.id.avatar);
		avatar.load(Server.serverAdress+getIntent().getStringExtra("AuthorAvatar"));

		RectangleView img1=(RectangleView)listViewHead.findViewById(R.id.img1);   //第一个图片
		RectangleView img2=(RectangleView)listViewHead.findViewById(R.id.img2);   //第二个图片
		RectangleView img3=(RectangleView)listViewHead.findViewById(R.id.img3);   //第三个图片

		//图片不为空显示图片
		if(article.getArticlesImage()==null||article.getArticlesImage().length()<=0){   //没有图片
			img1.setVisibility(RectangleView.GONE);
			img2.setVisibility(RectangleView.GONE);
			img3.setVisibility(RectangleView.GONE);	
		}else{          														//有图片
			String[] articleImg = article.getArticlesImage().split("\\|");
			if(articleImg.length==3){                    //有三张图片
				for(int i=0;i<articleImg.length;i++){
					RectangleView[] imgs = new RectangleView[]{img1,img2,img3};
					imgs[i].setVisibility(RectangleView.VISIBLE);
					imgs[i].load(Server.serverAdress+articleImg[i]);
				}
			}else if (articleImg.length==2) {              //两张图片
				for(int i=0;i<articleImg.length;i++){
					RectangleView[] imgs = new RectangleView[]{img1,img2};
					imgs[i].setVisibility(RectangleView.VISIBLE);
					imgs[i].load(Server.serverAdress+articleImg[i]);
					img3.setVisibility(RectangleView.GONE);
				}
			}else{                     						//一张图片
				img1.setVisibility(RectangleView.VISIBLE);
				img1.load(Server.serverAdress+articleImg[0]);
				img2.setVisibility(RectangleView.GONE);
				img3.setVisibility(RectangleView.GONE);
			}

		}

		txt_title.setText(title);         //帖子标题
		txt_author.setText(authorName);   //帖子作者
		txt_text.setText(text);           //帖子内容
		txt_date.setText(date);           //帖子日期

		fraLike=(FrameLayout)listViewHead.findViewById(R.id.fra_like);
		fraComment=(FrameLayout)listViewHead.findViewById(R.id.fra_comment);
		fraReward=(FrameLayout)listViewHead.findViewById(R.id.fra_reward);
		fraComment.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
				//发表本帖子的评论
				goaddcomment();
			}
		});


		fraLike.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				toggleLikes();
			}
		});

		fraReward.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				goaddreward();
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

		fragTitleBar.setOnGoBackListener(new TitleBarFragment.OnGoBackListener() {

			@Override
			public void onGoBack() {
				finish();
				overridePendingTransition(0, R.anim.slide_out_right);
			}
		});
	}

  

	//重新显示回复的条目
	@Override
	protected void onResume() {
		super.onResume();
		reload();
	}

	//适配器为listview填充评论内容
	BaseAdapter listAdapter = new BaseAdapter() {

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = null;
			MyListener1 myListener=null;
			if(convertView==null){
				LayoutInflater inflater= LayoutInflater.from(parent.getContext());
				// 自定义listitem显示回复内容
				view =inflater.inflate(R.layout.comment_list_item, null);
			}else{
				view = convertView;
			}
			myListener = new MyListener1(position);

			TextView text1 = (TextView)view.findViewById(R.id.text1); //作者
			TextView text2 = (TextView)view.findViewById(R.id.text2); //内容
			TextView text3 = (TextView)view.findViewById(R.id.text3); //日期
			AvatarView avatar = (AvatarView)view.findViewById(R.id.avatar); //评论者头像
			btn_reply=(Button)view.findViewById(R.id.btn_reply);        //回复按钮

			comment = data.get(position);
			text1.setText(comment.getAuthorName());
			text2.setText(comment.getText());
			String dateStr = DateFormat.format("yyyy-MM-dd hh:mm", comment.getCreateDate()).toString();
			text3.setText(dateStr);
			avatar.load(Server.serverAdress + comment.getAuthorAvatar());

			btn_reply.setTag(position);
			btn_reply.setOnClickListener(myListener);

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

	private class MyListener1 implements OnClickListener{
		int mPosition;
		public MyListener1(int inPosition){
			mPosition=inPosition;
		}
		@Override
		public void onClick(View v){
			Intent itnt =new Intent(ForumContentActivity.this,AddReplyActivity.class);
			Comment comment=data.get(mPosition);
			itnt.putExtra("data",article);
			itnt.putExtra("comment",comment);
			startActivity(itnt);
			overridePendingTransition(R.anim.slide_in_right,0);
		}
	}


	//更新点赞和评论的显示
	void reload(){
		reloadLikes();
		checkLiked();
		Request request = Server.requestBuilderWithApi("article/"+article.getId()+"/comments")
				.method("GET", null)
				.build();

		Server.getSharedClient().newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0,final Response arg1) throws IOException {
				try{
					final Page<Comment> data = new ObjectMapper()
							.readValue(arg1.body().string(), new TypeReference<Page<Comment>>(){});

					runOnUiThread(new Runnable() {
						public void run() {
							ForumContentActivity.this.page = data.getNumber();
							ForumContentActivity.this.data = data.getContent();
							listAdapter.notifyDataSetChanged();
						}
					});
				}catch (final Exception e) {
					e.printStackTrace();
					//					runOnUiThread(new Runnable() {
					//						@Override
					//						public void run() {
					//							new AlertDialog.Builder(ForumContentActivity.this)
					//							.setMessage(e.getMessage())
					//							.show();
					//						}
					//					});  
				}
			}


			@Override
			public void onFailure(Call arg0,final IOException e) {
				//				runOnUiThread(new Runnable() {
				//					@Override
				//					public void run() {
				//						new AlertDialog.Builder(ForumContentActivity.this)
				//						.setMessage(e.getMessage())
				//						.show();
				//					}
				//				}); 
				e.printStackTrace();
			}
		});
	}

	//加载更多
	void loadmore(){

		Request request = Server.requestBuilderWithApi("/article/"+article.getId()+"/comments/"+(page+1)).get().build();
		Server.getSharedClient().newCall(request).enqueue(new Callback() {
			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				runOnUiThread(new Runnable() {
					public void run() {
					}
				});

				try{
					final Page<Comment> comments = new ObjectMapper().readValue(arg1.body().string(), new TypeReference<Page<Comment>>() {});
					if(comments.getNumber()>page){

						runOnUiThread(new Runnable() {
							public void run() {
								if(data==null){
									data = comments.getContent();
								}else{
									data.addAll(comments.getContent());
								}
								page = comments.getNumber();
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
				runOnUiThread(new Runnable() {
					public void run() {
					}
				});
			}
		});


	}

	private boolean isLiked;

	//判断赞按钮是否被选中
	void checkLiked(){
		Request request = Server.requestBuilderWithApi("article/"+article.getId()+"/isliked").get().build();
		Server.getSharedClient().newCall(request).enqueue(new Callback() {
			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				try{
					final String responseString = arg1.body().string();
					final Boolean result = new ObjectMapper().readValue(responseString, Boolean.class);

					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							onCheckLikedResult(result);
						}
					});
				}catch(final Exception e){
					e.printStackTrace();
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							onCheckLikedResult(false);
						}
					});
				}
			}

			@Override
			public void onFailure(Call arg0, IOException e) {
				e.printStackTrace();
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						onCheckLikedResult(false);
					}
				});				
			}
		});
	}

	//根据赞按钮是否被选中返回结果改变赞的图片
	void onCheckLikedResult(boolean result){
		isLiked =result;
		if(result==true){
			img_like.setImageResource(R.drawable.icon_like_red);
		}else{
			img_like.setImageResource(R.drawable.icon_like_none);
		}
	}

	//提取返回赞的数量
	void reloadLikes(){
		Request request =Server.requestBuilderWithApi("article/"+article.getId()+"/likes").get().build();
		Server.getSharedClient().newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				try{
					String responseString = arg1.body().string();
					final Integer count = new ObjectMapper().readValue(responseString, Integer.class);

					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							onReloadLikesResult(count);
						}
					});
				}catch (Exception e) {
					e.printStackTrace();
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							onReloadLikesResult(0);
						}
					});
				}

			}

			@Override
			public void onFailure(Call arg0, IOException e) {
				e.printStackTrace();
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						onReloadLikesResult(0);
					}
				});

			}
		});
	}

	//根据返回的赞的数量改变点赞按钮的数量显示
	void onReloadLikesResult(int count){
		if(count>0){
			count_like.setText(""+count);
		}else{
			count_like.setText("");
		}
	}

	//判断当前用户是否点赞
	void toggleLikes(){
		MultipartBody body = new MultipartBody.Builder()
				.addFormDataPart("likes", String.valueOf(!isLiked))
				.build(); 

		Request request = Server.requestBuilderWithApi("article/"+article.getId()+"/likes")
				.post(body).build();

		Server.getSharedClient().newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				runOnUiThread(new Runnable() {
					public void run() {
						reload();
					}
				});
			}

			@Override
			public void onFailure(Call arg0, IOException arg1) {
				runOnUiThread(new Runnable() {
					public void run() {
						reload();
					}
				});
			}
		});
	}

	//跳转到该文章的发表评论页面
	void goaddcomment(){
		Intent itnt = new Intent(ForumContentActivity.this,AddCommentActivity.class);
		itnt.putExtra("data",article);
		startActivity(itnt);
		overridePendingTransition(R.anim.slide_in_right,0);
	}

	//跳转到该文章的打赏页面
	void goaddreward(){
		Intent itnt = new Intent(ForumContentActivity.this,AddRewardActivity.class);
		itnt.putExtra("data",article);
		startActivity(itnt);
		overridePendingTransition(R.anim.slide_in_right,0);
	}
}
