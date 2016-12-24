package com.example.bbook;

import java.io.IOException;
import java.util.List;

import com.example.bbook.api.Article;
import com.example.bbook.api.Comment;
import com.example.bbook.api.Page;
import com.example.bbook.api.Server;
import com.example.bbook.api.widgets.AvatarView;
import com.example.bbook.api.widgets.RectangleView;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.Response;

public class ForumContentActivity extends Activity {
	Comment comment;
	Article article;
	Button btn_like,btn_send;
	ListView listView;
	TextView count_like;
	ImageView img_like,img_comment;

	View btnLoadMore;
	TextView textLoadMore;

	List<Comment> data;
	int page =0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forumcontent);

		btnLoadMore=LayoutInflater.from(this).inflate(R.layout.load_more_button, null);
		textLoadMore= (TextView)btnLoadMore.findViewById(R.id.text);
		count_like =(TextView)findViewById(R.id.count_like);
		img_like=(ImageView)findViewById(R.id.img_like);

		listView = (ListView)findViewById(R.id.list_comment);
		listView.addFooterView(btnLoadMore);
		listView.setAdapter(listAdapter);

		String title=getIntent().getStringExtra("Title");  
		String text=getIntent().getStringExtra("Text");
		String authorName=getIntent().getStringExtra("AuthorName");
		String date=getIntent().getStringExtra("Date");

		article = (Article)getIntent().getSerializableExtra("Data");

		TextView txt_title=(TextView)findViewById(R.id.txt_title);
		TextView txt_author=(TextView)findViewById(R.id.txt_author);
		TextView txt_text=(TextView)findViewById(R.id.txt_text);
		TextView txt_date=(TextView)findViewById(R.id.txt_date);
		AvatarView avatar = (AvatarView)findViewById(R.id.avatar);
		avatar.load(Server.serverAdress+getIntent().getStringExtra("AuthorAvatar"));

		//图片不为空显示图片
		RectangleView articleImage = (RectangleView)findViewById(R.id.articleImage);
		if(article.getArticlesImage()!=null){
			articleImage.setVisibility(RectangleView.VISIBLE);
			articleImage.load(Server.serverAdress+article.getArticlesImage());
		}

		txt_title.setText(title);         //文章标题
		txt_author.setText(authorName);   //文章作者
		txt_text.setText(text);           //文章内容
		txt_date.setText(date);           //文章日期

		img_comment=(ImageView)findViewById(R.id.img_comment);
		img_comment.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
				//发表本帖子的评论
				goaddcomment();

			}
		});


		img_like.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				toggleLikes();
			}
		});

		btnLoadMore.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				loadmore();
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
			if(convertView==null){
				LayoutInflater inflater= LayoutInflater.from(parent.getContext());
				// 自定义listitem显示回复内容
				view =inflater.inflate(R.layout.comment_list_item, null);
			}else{
				view = convertView;
			}
			TextView text1 = (TextView)view.findViewById(R.id.text1); //作者
			TextView text2 = (TextView)view.findViewById(R.id.text2); //内容
			TextView text3 = (TextView)view.findViewById(R.id.text3); //日期
			AvatarView avatar = (AvatarView)view.findViewById(R.id.avatar); //评论者头像

			comment = data.get(position);
			text1.setText(comment.getAuthorName());
			text2.setText(comment.getText());
			String dateStr = DateFormat.format("yyyy-MM-dd hh:mm", comment.getCreateDate()).toString();
			text3.setText(dateStr);
			avatar.load(Server.serverAdress + comment.getAuthorAvatar());

//			btn_send=(Button)view.findViewById(R.id.btn_reply);
//			btn_send.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					//					goaddreply(comment);
//
//				}
//			});

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


	//	//回复楼层
	//	void goaddreply(Comment comment){
	//		Intent itnt =new Intent(ForumContentActivity.this,AddReplyActivity.class);
	//		itnt.putExtra("data",article);
	//		itnt.putExtra("comment",comment);
	//		startActivity(itnt);
	//	}

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
							listAdapter.notifyDataSetInvalidated();
						}
					});
				}catch (final Exception e) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							new AlertDialog.Builder(ForumContentActivity.this)
							.setMessage(e.getMessage())
							.show();
						}
					});  
				}
			}


			@Override
			public void onFailure(Call arg0,final IOException e) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						new AlertDialog.Builder(ForumContentActivity.this)
						.setMessage(e.getMessage())
						.show();
					}
				}); 
			}
		});
	}

	//加载更多
	void loadmore(){

		btnLoadMore.setEnabled(false);
		textLoadMore.setText("载入中…");

		Request request = Server.requestBuilderWithApi("/article/"+article.getId()+"/comments/"+(page+1)).get().build();
		Server.getSharedClient().newCall(request).enqueue(new Callback() {
			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				runOnUiThread(new Runnable() {
					public void run() {
						btnLoadMore.setEnabled(true);
						textLoadMore.setText("加载更多");
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
						btnLoadMore.setEnabled(true);
						textLoadMore.setText("加载更多");
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
			count_like.setVisibility(TextView.VISIBLE);
			count_like.setText(""+count);
		}else{
			count_like.setVisibility(TextView.GONE);
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
	}
}
