package com.example.bbook;

import java.io.IOException;
import java.util.List;

import com.example.bbook.api.Comment;
import com.example.bbook.api.Page;
import com.example.bbook.api.Server;
import com.example.bbook.api.widgets.AvatarView;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class MyCommentActivity extends Activity{

	Comment comment;

	View btnLoadMore;
	TextView textLoadMore;

	ListView listView;
	List<Comment> data;
	int page =0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mycomment);

		btnLoadMore= LayoutInflater.from(this).inflate(R.layout.load_more_button, null);
		textLoadMore= (TextView)btnLoadMore.findViewById(R.id.text);

		listView = (ListView)findViewById(R.id.list);
		listView.addFooterView(btnLoadMore);
		listView.setAdapter(listAdapter);

		findViewById(R.id.img_return).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		btnLoadMore.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				loadmore();
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
		btnLoadMore.setEnabled(false);
		textLoadMore.setText("载入中…");

		Request request = Server.requestBuilderWithApi("mycomments?page="+(page+1))
				.method("GET", null)
				.build();
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
						btnLoadMore.setEnabled(true);
						textLoadMore.setText("加载更多");
					}
				});
			}
		});
	}
}
