package com.example.bbook.fragments.pages;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import com.example.bbook.BookDetailActivity;
import com.example.bbook.R;
import com.example.bbook.api.BookComment;
import com.example.bbook.api.Goods;
import com.example.bbook.api.Page;
import com.example.bbook.api.Server;
import com.example.bbook.api.widgets.AvatarView;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
//书本评论Fragment
import android.widget.ListView;
import android.widget.TextView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
public class BookCommentFragment extends Fragment {
	ListView commentList;
	List<BookComment> bookCommentData;
	int page=0;
	Goods goods;
	int goodsId;
	AvatarView avatar;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view =inflater.inflate(R.layout.fragment_book_comment,null);
		
		goods=(Goods) getActivity().getIntent().getSerializableExtra("goods");
		commentList=(ListView) view.findViewById(R.id.comment_list);
		commentList.setAdapter(commentAdapter);
		
//		Bundle bundle=getArguments();
//		goodsId=bundle.getInt("goodsId");
		return view;
	}



	BaseAdapter commentAdapter=new BaseAdapter() {

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View view=null;
			if(convertView==null){
				LayoutInflater inflater=LayoutInflater.from(parent.getContext());
				view =inflater.inflate(R.layout.book_comment_list_item,null);
			}else{
				view=convertView;
			}
			
			BookComment bookComment=bookCommentData.get(position);
			TextView bookCommentView=(TextView) view.findViewById(R.id.book_comment);
			bookCommentView.setText(bookComment.getText());
			
			
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
			return bookCommentData.get(position);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return bookCommentData==null?0:bookCommentData.size();
		}
	};
@Override
public void onResume() {
	// TODO Auto-generated method stub
	super.onResume();
	LoadGoodsComments();
}

	public void LoadGoodsComments(){
	
		Log.d("goodid",goods.getId().toString());
		OkHttpClient client=Server.getSharedClient();
		Request request=Server.requestBuilderWithApi("goods/"+goods.getId()+"/comments")
				.get().build();

		client.newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				// TODO Auto-generated method stub
				final String responseStr=arg1.body().string();
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							Page<BookComment> data=new ObjectMapper()
									.readValue(responseStr, new TypeReference<Page<BookComment>>() {
									});
							BookCommentFragment.this.bookCommentData=data.getContent();
							BookCommentFragment.this.page=data.getNumber();
							commentAdapter.notifyDataSetInvalidated();
						}  catch (Exception e) {
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
}
