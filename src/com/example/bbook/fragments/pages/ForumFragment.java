package com.example.bbook.fragments.pages;

import java.io.IOException;
import java.util.List;

import com.example.bbook.AddNoteActivity;
import com.example.bbook.CommentTomeActivity;
import com.example.bbook.ForumContentActivity;
import com.example.bbook.MyArticleActivity;
import com.example.bbook.MyCommentActivity;
import com.example.bbook.R;
import com.example.bbook.SearchArticleActivity;
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
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class ForumFragment extends Fragment {

	PopupMenu myMenu;
	Menu menu;

	int page=0;
	List<Article>data;

	View btnLoadMore;
	TextView textLoadMore;

	ListView listView;
	View view;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (view==null) {
			view=inflater.inflate(R.layout.fragment_page_forum, null);
			btnLoadMore= inflater.inflate(R.layout.load_more_button, null);
			textLoadMore= (TextView)btnLoadMore.findViewById(R.id.text);

			myMenu=new PopupMenu(getActivity(),view.findViewById(R.id.img_aboutme));
			menu=myMenu.getMenu();
			getActivity().getMenuInflater().inflate(R.menu.menu_article, menu);

			listView =(ListView)view.findViewById(R.id.list);
			listView.setAdapter(listAdapter);
			listView.addFooterView(btnLoadMore);

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

			btnLoadMore.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					loadmore();
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
			TextView txt_author = (TextView)view.findViewById(R.id.text1);
			TextView txt_title = (TextView)view.findViewById(R.id.text2);
			TextView txt_text = (TextView)view.findViewById(R.id.text3);
			TextView txt_date = (TextView)view.findViewById(R.id.text4);
			AvatarView avatar = (AvatarView)view.findViewById(R.id.avatar_fra);
			RectangleView articleImage = (RectangleView)view.findViewById(R.id.articleImage);

			Article article = data.get(position);

			//图片不为空显示图片
			if(article.getArticlesImage()!=null){
				articleImage.setVisibility(RectangleView.VISIBLE);
				articleImage.load(Server.serverAdress+article.getArticlesImage());
			}else{
				articleImage.setVisibility(RectangleView.GONE);
			}

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
					getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							new AlertDialog.Builder(getActivity())
							.setMessage(e.getMessage())
							.show();
						}
					});  
				}
			}


			@Override
			public void onFailure(Call arg0,final IOException e) {
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						new AlertDialog.Builder(getActivity())
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

		Request request = Server.requestBuilderWithApi("forums/"+(page+1)).get().build();
		Server.getSharedClient().newCall(request).enqueue(new Callback() {
			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				getActivity().runOnUiThread(new Runnable() {
					public void run() {
						btnLoadMore.setEnabled(true);
						textLoadMore.setText("加载更多");
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
						btnLoadMore.setEnabled(true);
						textLoadMore.setText("加载更多");
					}
				});
			}
		});
	}



	//点击listview跳转到这条文章的详情并传参
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
	}

	//跳到发帖子页面
	void goaddnote(){
		Intent itnt =new Intent(getActivity(),AddNoteActivity.class);
		startActivity(itnt);
	}

	//跳到我的帖子页面
	void gomyart(){
		Intent itnt =new Intent(getActivity(),MyArticleActivity.class);
		startActivity(itnt);
	}
	//跳到对我的文章的评论页面
	void gocommentTome(){
		Intent itnt =new Intent(getActivity(),CommentTomeActivity.class);
		startActivity(itnt);
	}
	//跳到我发出的评论页面
	void gomycomment(){
		Intent itnt =new Intent(getActivity(),MyCommentActivity.class);
		startActivity(itnt);
	}
}
