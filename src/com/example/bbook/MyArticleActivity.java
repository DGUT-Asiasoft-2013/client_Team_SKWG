package com.example.bbook;

import java.io.IOException;
import java.util.List;

import com.example.bbook.api.Article;
import com.example.bbook.api.Page;
import com.example.bbook.api.Server;
import com.example.bbook.api.widgets.AvatarView;
import com.example.bbook.fragments.pages.ForumFragment;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class MyArticleActivity extends Activity {
	ListView listView;

	int page=0;
	List<Article>data;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myart);

		listView =(ListView)findViewById(R.id.list);
		listView.setAdapter(listAdapter);

		findViewById(R.id.btn_return).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
				
			}
		});
		listView.setOnItemClickListener(new  AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				onItemClicked(position);
			}
		});
	}

	//刷新我的帖子
	@Override
	public void onResume() {
		super.onResume();
		reload();
	}

	//从服务器端获取解析我的文章到客户端data
	void reload(){
		Request request = Server.requestBuilderWithApi("myarticles")
				.method("GET", null)
				.build();

		Server.getSharedClient().newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0,final Response arg1) throws IOException {
				try{
					final Page<Article> data = new ObjectMapper()
							.readValue(arg1.body().string(), new TypeReference<Page<Article>>(){});
					runOnUiThread(new Runnable() {
						public void run() {
							MyArticleActivity.this.page = data.getNumber();
							MyArticleActivity.this.data = data.getContent();
							listAdapter.notifyDataSetInvalidated();
						}
					});
				}catch (final Exception e) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							new AlertDialog.Builder(MyArticleActivity.this)
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
						new AlertDialog.Builder(MyArticleActivity.this)
						.setMessage(e.getMessage())
						.show();
					}
				}); 
			}
		});
	}


	//适配器为listview填充我的文章内容
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
			TextView txt_author = (TextView)view.findViewById(R.id.text1);
			TextView txt_title = (TextView)view.findViewById(R.id.text2);
			TextView txt_text = (TextView)view.findViewById(R.id.text3);
			TextView txt_date = (TextView)view.findViewById(R.id.text4);
			AvatarView avatar = (AvatarView)view.findViewById(R.id.avatar_fra);

			Article article = data.get(position);
			//				Log.d("111", article.getAuthorAvatar().toString());
			avatar.load(Server.serverAdress + article.getAuthorAvatar());
			txt_author.setText(article.getAuthorName());
			txt_title.setText(article.getTitle());
			txt_text.setText(article.getText());
			String dateStr = DateFormat.format("yyyy-MM-dd hh:mm", article.getCreateDate()).toString();
			txt_date.setText(dateStr);

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

	//点击listview跳转到修改文章并传参
	public void onItemClicked( int position){
		String title = data.get(position).getTitle();
		String text = data.get(position).getText();
//		String authorName = data.get(position).getAuthorName();
//		String date = DateFormat.format("yyyy-MM-dd hh:mm", data.get(position).getCreateDate()).toString();
//		String authorAvatar = data.get(position).getAuthorAvatar();
		Article content = data.get(position);

		Intent itnt =new Intent(MyArticleActivity.this,ModifyArticleActivity.class);
		itnt.putExtra("Text", text);
		itnt.putExtra("Title", title);
//		itnt.putExtra("AuthorName", authorName);
//		itnt.putExtra("Date", date);
//		itnt.putExtra("AuthorAvatar", authorAvatar);
		itnt.putExtra("Data",content);

		startActivity(itnt);
	}

}