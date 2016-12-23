package com.example.bbook.fragments.pages;

import com.example.bbook.R;
import com.example.bbook.api.Goods;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
//书籍详情Fragment
public class BookDetailFragment extends Fragment {
Goods goods;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view=inflater.inflate(R.layout.fragment_book_detail, null)	;
		goods=(Goods) getActivity().getIntent().getSerializableExtra("goods");
	
		TextView bookAuthor=(TextView) view.findViewById(R.id.book_author);
		bookAuthor.setText("作者:"+goods.getAuthor());
		TextView bookType=(TextView) view.findViewById(R.id.book_type);
		bookType.setText("类型:"+goods.getGoodsType());
		
		TextView bookPublisher=(TextView) view.findViewById(R.id.book_publisher);
		bookPublisher.setText("出版社:"+goods.getPublisher());
		TextView bookPubDate=(TextView) view.findViewById(R.id.book_pubdate);
		bookPubDate.setText("出版时间:"+goods.getPubDate());
		TextView bookPriTime=(TextView) view.findViewById(R.id.book_pritime);
		bookPriTime.setText("印刷时间:"+goods.getPritime());
		
		return view;
	}
}
