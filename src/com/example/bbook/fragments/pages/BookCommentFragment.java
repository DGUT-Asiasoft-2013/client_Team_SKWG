package com.example.bbook.fragments.pages;

import com.example.bbook.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
// ÈºÆ∆¿¬€Fragment
import android.widget.ListView;
public class BookCommentFragment extends Fragment {
	ListView commentList;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view =inflater.inflate(R.layout.fragment_book_comment,null);

		commentList=(ListView) view.findViewById(R.id.comment_list);
		commentList.setAdapter(commentAdapter);


		return view;
	}



	BaseAdapter commentAdapter=new BaseAdapter() {

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View view=null;
			if(convertView==null){
				LayoutInflater inflater=LayoutInflater.from(parent.getContext());
				view =inflater.inflate(R.layout.comment_list_item,null);
			}else{
				view=convertView;
			}
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
			return null;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 6;
		}
	};
}
