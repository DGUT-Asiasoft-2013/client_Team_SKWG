package com.example.bbook.fragments.pages;


import java.io.IOException;
import java.util.List;

import com.example.bbook.BookDetailActivity;
import com.example.bbook.R;
import com.example.bbook.api.Goods;
import com.example.bbook.api.Page;
import com.example.bbook.api.Server;
import com.example.bbook.api.widgets.AvatarView;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.R.raw;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomepageFragment extends Fragment {
//书籍展示页面

	//AvatarAndNameFragment[]  ava=new AvatarAndNameFragment[6];
	GridView bookView;
	//ImageView imageView;
	AvatarView avatar;
	TextView textview;
	
	Goods goods;
	EditText editKeyword;
	Button btnSearch;
	
	List<Goods> data;
	int page=0;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view=inflater.inflate(R.layout.fragment_book_search_page, null);
		
		btnSearch=(Button) view.findViewById(R.id.btn_search);
		editKeyword=(EditText) view.findViewById(R.id.edit_keyword);
		bookView=(GridView) view.findViewById(R.id.book_gridView);
		bookView.setAdapter(bookAdapter);
		
		btnSearch.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SearchBooksByKeyword();
			}
		});
		return view;		
	}
	
	
	BaseAdapter bookAdapter=new BaseAdapter() {
		@SuppressLint("InflateParams")
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View view = null;

			if(convertView==null){
				LayoutInflater inflater = LayoutInflater.from(parent.getContext());
				view = inflater.inflate(R.layout.fragment_avatar_name, null);	
			}else{
				view = convertView;
			}
			textview=(TextView) view.findViewById(R.id.id);
//			imageView=(ImageView) view.findViewById(R.id.picture);
			avatar=(AvatarView) view.findViewById(R.id.picture);
			
			goods=data.get(position);
			textview.setText(goods.getAuthor());
			avatar.load(Server.serverAdress+goods.getGoodsImage());
			
			avatar.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
				//	Toast.makeText(getActivity(), "picture",Toast.LENGTH_SHORT).show();
					goBookDetailActivity( position);
				}
			});
			textview.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Toast.makeText(getActivity(), "ID",Toast.LENGTH_SHORT).show();
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
	
	@Override
		public void onResume() {
			// TODO Auto-generated method stub
			super.onResume();
			
			bookLoad();
			
		}
	
	public void SearchBooksByKeyword(){
		String keyword=editKeyword.getText().toString();
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
	
	
	public void bookLoad(){
		
		OkHttpClient client=Server.getSharedClient();
		
		Request request=Server.requestBuilderWithApi("goods/s")
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
	//
	public void goBookDetailActivity(int position){
		goods=data.get(position);
		Intent intent=new Intent(getActivity(),BookDetailActivity.class);
		intent.putExtra("goods", goods);
		startActivity(intent);
	}
}
