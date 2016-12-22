package com.example.bbook;

import com.example.bbook.api.Goods;
import com.example.bbook.fragments.pages.BookCommentFragment;
import com.example.bbook.fragments.pages.BookDetailFragment;
import com.example.bbook.fragments.pages.HomepageFragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
//书籍详情Activity
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
		bookName.setText("书名:"+goods.getGoodsName());
		TextView bookAuthor=(TextView) findViewById(R.id.book_author);
		bookAuthor.setText("作者:"+goods.getAuthor());
		TextView bookPublisher=(TextView) findViewById(R.id.book_publisher);
		bookPublisher.setText("出版社:"+goods.getPublisher());
		TextView bookPrice=(TextView) findViewById(R.id.book_price);
		bookPrice.setText("价格:"+goods.getGoodsPrice());

		
		
		detailLabel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				selectedIndex=0;
				
				changeFragmentContent(selectedIndex);
			}
		});


		commentLabel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				selectedIndex=1;
				String string=goods.getGoodsName();
				Intent intent=new Intent(BookDetailActivity.this,BookCommentFragment.class);
				intent.putExtra("goods", goods);
				changeFragmentContent(selectedIndex);
			}
		});
		
		findViewById(R.id.buy).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				goBuy();
			}
		});
		
		findViewById(R.id.btn_preorder).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				goPreorder();
			}
		});
	}


	protected void goPreorder() {
		Intent itnt = new Intent(BookDetailActivity.this, PreOrderActivity.class);
		itnt.putExtra("goods", goods);
		startActivity(itnt);
	}


	protected void goBuy() {
		Intent itnt = new Intent(BookDetailActivity.this, BuyActivity.class);
		itnt.putExtra("goods", goods);
		startActivity(itnt);
	}


	@Override
	protected void onResume() {
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
			break;
		default:
			break;
		}
		if(newFrag==null) return;

		getFragmentManager().beginTransaction().replace(R.id.content, newFrag).commit();

	}
	
	
}
