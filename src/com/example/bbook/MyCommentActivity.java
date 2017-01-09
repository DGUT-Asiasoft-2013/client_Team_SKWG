package com.example.bbook;

import java.io.IOException;
import java.util.List;

import com.example.bbook.api.Article;
import com.example.bbook.api.Comment;
import com.example.bbook.api.Page;
import com.example.bbook.api.Server;
import com.example.bbook.api.widgets.AvatarView;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import util.MyListener;
import util.PullToRefreshLayout;
import util.PullableListView;

public class MyCommentActivity extends Activity{

	Comment comment;

	//	View btnLoadMore;
	//	TextView textLoadMore;
	ImageButton ibtn_back;

	ListView listView;
	List<Comment> data;
	int page =0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mycomment);

		//		btnLoadMore= LayoutInflater.from(this).inflate(R.layout.load_more_button, null);
		//		textLoadMore= (TextView)btnLoadMore.findViewById(R.id.text);

		PullToRefreshLayout pullToRefreshLayout=(PullToRefreshLayout)findViewById(R.id.refresh_view);   //获取自定义layout

		listView =(PullableListView)findViewById(R.id.content_view);
		listView.setAdapter(listAdapter);

		ibtn_back=(ImageButton)findViewById(R.id.btn_back);
		ibtn_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
				overridePendingTransition(0, R.anim.slide_out_right);
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

		listView.setOnItemClickListener(new  AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				onItemClicked(position);
			}
		});
	}
	@Override
	protected void onResume() {
		super.onResume();
		reload();
	}

	BaseAdapter listAdapter = new BaseAdapter() {

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = null;
			if(convertView==null){
				LayoutInflater inflater= LayoutInflater.from(parent.getContext());
				view =inflater.inflate(R.layout.reply_list_item, null);
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

	void reload(){
		Request request = Server.requestBuilderWithApi("mycomments")
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
							MyCommentActivity.this.page = data.getNumber();
							MyCommentActivity.this.data = data.getContent();
							listAdapter.notifyDataSetInvalidated();
						}
					});
				}catch (final Exception e) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							new AlertDialog.Builder(MyCommentActivity.this)
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
						new AlertDialog.Builder(MyCommentActivity.this)
						.setMessage(e.getMessage())
						.show();
					}
				}); 
			}
		});
	}

	//加载更多
	void loadmore(){

		Request request = Server.requestBuilderWithApi("mycomments?page="+(page+1))
				.method("GET", null)
				.build();
		Server.getSharedClient().newCall(request).enqueue(new Callback() {
			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				runOnUiThread(new Runnable() {
					public void run() {
						//						btnLoadMore.setEnabled(true);
						//						textLoadMore.setText("加载更多");
					}
				});

				try{
					final Page<Comment> datas = new ObjectMapper()
							.readValue(arg1.body().string(), new TypeReference<Page<Comment>>(){});
					if(datas.getNumber()>page){

						runOnUiThread(new Runnable() {
							public void run() {
								if(data==null){
									data = datas.getContent();
								}else{
									data.addAll(datas.getContent());
								}
								page = datas.getNumber();
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
						//						btnLoadMore.setEnabled(true);
						//						textLoadMore.setText("加载更多");
					}
				});
			}
		});
	}

	//点击listview删除评论提示
	public void onItemClicked(final int position){
		AlertDialog.Builder builder=new Builder(this);
		builder.setMessage("确定删除该评论？");
		builder.setNegativeButton("取消",null);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				deleteComment(position);
			}
		});
		builder.show();
	}

	//delete评论
	void deleteComment(int position){
		Comment comment = data.get(position);

		Request request = Server.requestBuilderWithApi("deletecomment/"+comment.getId())
				.method("delete", null)
				.build();

		Server.getSharedClient().newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(final Call arg0, Response arg1) throws IOException {

				try{	     
					final String responString = arg1.body().toString();
					runOnUiThread(new Runnable() {
						public void run() {
							MyCommentActivity.this.onResponsed(arg0,responString);
						}
					});
				}catch (final Exception e) {
					runOnUiThread(new Runnable() {
						public void run() {
							MyCommentActivity.this.onFailured(arg0, e);
						}
					});
				}
			}
			@Override
			public void onFailure(final Call arg0, final IOException arg1) {

				runOnUiThread(new Runnable() {
					public void run() {
						MyCommentActivity.this.onFailured(arg0, arg1);
					}
				});

			}
		});


	}
	void onResponsed(Call arg0, String responseBody) {
		new AlertDialog.Builder(this)
		.setTitle("删除成功")
		.setPositiveButton("OK",new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				reload();
			}
		})
		.show();
	}

	void onFailured(Call arg0, Exception arg1) {
		new AlertDialog.Builder(this)
		.setTitle("删除失败")
		.setMessage(arg1.getLocalizedMessage())
		.setNegativeButton("OK", null)
		.show();
	}
}

