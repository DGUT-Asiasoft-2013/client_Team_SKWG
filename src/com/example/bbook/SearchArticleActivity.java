package com.example.bbook;

import java.io.IOException;
import java.util.List;

import com.example.bbook.R;
import com.example.bbook.api.Article;
import com.example.bbook.api.Page;
import com.example.bbook.api.Server;
import com.example.bbook.api.widgets.AvatarView;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class SearchArticleActivity extends Activity {

	EditText searchText;
	ListView listView;

	View btnLoadMore;
	TextView textLoadMore;

	int page= 0;
	List<Article>find;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_searcharticle);

		btnLoadMore= LayoutInflater.from(this).inflate(R.layout.load_more_button, null);
		textLoadMore= (TextView)btnLoadMore.findViewById(R.id.text);

		listView = (ListView)findViewById(R.id.list);
		listView.addFooterView(btnLoadMore);
		listView.setAdapter(listAdapter);

		searchText=(EditText)findViewById(R.id.keyword_txt);
		findViewById(R.id.img_return).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();

			}
		});
		findViewById(R.id.img_search).setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				searchKeyword();
			}
		});

		btnLoadMore.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				loadmore();
			}
		});

		listView.setOnItemClickListener(new  AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				onItemClicked(position);
			}
		});

	}

	//给listview适配搜索到的文章
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

			Article article = find.get(position);
			//			Log.d("111", article.getAuthorAvatar().toString());
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
			return find.get(position);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return find==null?0:find.size();
		}
	};

	//点击item跳转传参
	public void onItemClicked( int position){
		String title = find.get(position).getTitle();
		String text = find.get(position).getText();
		String authorName = find.get(position).getAuthorName();
		String date = DateFormat.format("yyyy-MM-dd hh:mm", find.get(position).getCreateDate()).toString();
		String authorAvatar = find.get(position).getAuthorAvatar();
		Article content = find.get(position);

		Intent itnt =new Intent(SearchArticleActivity.this,ForumContentActivity.class);
		itnt.putExtra("Text", text);
		itnt.putExtra("Title", title);
		itnt.putExtra("AuthorName", authorName);
		itnt.putExtra("Date", date);
		itnt.putExtra("AuthorAvatar", authorAvatar);
		itnt.putExtra("Data",content);

		startActivity(itnt);
	}

	//按关键字搜索
	void searchKeyword(){
		String keywords = searchText.getText().toString();
		if(keywords==null||keywords.isEmpty()){
			 new AlertDialog.Builder(SearchArticleActivity.this)
			.setMessage("请输入要搜索的关键字!")
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setPositiveButton("OK",null)
			.show();
			 return;
		}

		//强制隐藏自带软键盘
		InputMethodManager inputMethodManager = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(searchText.getWindowToken(), 0);

		Request request = Server.requestBuilderWithApi("article/s/"+keywords)
				.get()
				.build();

		Server.getSharedClient().newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0,final Response arg1) throws IOException {
				try{
					final Page<Article> data = new ObjectMapper()
							.readValue(arg1.body().string(), new TypeReference<Page<Article>>(){});
					runOnUiThread(new Runnable() {
						public void run() {
							SearchArticleActivity.this.page = data.getNumber();
							SearchArticleActivity.this.find = data.getContent();
							listAdapter.notifyDataSetInvalidated();
						}
					});
				}catch (final Exception e) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							new AlertDialog.Builder(SearchArticleActivity.this)
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
						new AlertDialog.Builder(SearchArticleActivity.this)
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

		String keywords = searchText.getText().toString();
		Request request = Server.requestBuilderWithApi("article/s/"+keywords+"?page="+(page+1)).get().build();
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
					final Page<Article> searchs = new ObjectMapper().readValue(arg1.body().string(), new TypeReference<Page<Article>>() {});
					if(searchs.getNumber()>page){

						runOnUiThread(new Runnable() {
							public void run() {
								if(find==null){
									find = searchs.getContent();
								}else{
									find.addAll(searchs.getContent());
								}
								page = searchs.getNumber();
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
