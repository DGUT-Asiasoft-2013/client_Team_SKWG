package com.example.bbook;

import com.example.bbook.api.Goods;
import com.example.bbook.fragments.pages.BookCommentFragment;
import com.example.bbook.fragments.pages.BookDetailFragment;
import com.example.bbook.fragments.pages.HomepageFragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
//�鼮����Activity
import android.widget.TextView;
public class BookDetailActivity extends Activity {

	BookDetailFragment bookDetailFragment=new BookDetailFragment();
	BookCommentFragment bookCommentFragment=new BookCommentFragment();
	HomepageFragment mainPageFragment=new HomepageFragment();
	Goods goods;

	int selectedIndex=0;

	TextView detailLabel;
	TextView commentLabel;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_book_detail);

		goods=(Goods) getIntent().getSerializableExtra("goods");
		detailLabel=(TextView) findViewById(R.id.book_detail);
		commentLabel=(TextView) findViewById(R.id.book_comment);

		TextView bookName=(TextView) findViewById(R.id.book_name);
		bookName.setText("����:"+goods.getGoodsName());
		TextView bookAuthor=(TextView) findViewById(R.id.book_author);
		bookAuthor.setText("����:"+goods.getAuthor());
		TextView bookPublisher=(TextView) findViewById(R.id.book_publisher);
		bookPublisher.setText("������:"+goods.getPublisher());
		TextView bookPrice=(TextView) findViewById(R.id.book_price);
		bookPrice.setText("�۸�:"+goods.getGoodsPrice());
		
		detailLabel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				selectedIndex=0;
				changeFragmentContent(selectedIndex);
			}
		});


		commentLabel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				selectedIndex=1;
				changeFragmentContent(selectedIndex);
			}
		});
	}


	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		changeFragmentContent(selectedIndex);

	}

	public void changeFragmentContent(int selectedIndex){
		Fragment newFrag=null;
		switch (selectedIndex) {
		case 0:
			newFrag=bookDetailFragment;
			break;
		case 1:
			newFrag=bookCommentFragment;
			//	newFrag=mainPageFragment;
			break;
		default:
			break;
		}
		if(newFrag==null) return;

		getFragmentManager().beginTransaction().replace(R.id.content, newFrag).commit();

	}
}
