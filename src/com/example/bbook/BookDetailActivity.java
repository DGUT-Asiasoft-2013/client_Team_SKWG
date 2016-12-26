package com.example.bbook;

import com.example.bbook.api.Goods;
import com.example.bbook.api.Server;
import com.example.bbook.api.widgets.GoodsPicture;
import com.example.bbook.api.widgets.TitleBarFragment;
import com.example.bbook.fragments.pages.BookCommentFragment;
import com.example.bbook.fragments.pages.BookDetailFragment;
import com.example.bbook.fragments.pages.HomepageFragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
//书籍详情Activity
import android.widget.TextView;
import android.widget.Toast;
public class BookDetailActivity extends Activity {

	BookDetailFragment bookDetailFragment=new BookDetailFragment();
	BookCommentFragment bookCommentFragment=new BookCommentFragment();
	HomepageFragment mainPageFragment=new HomepageFragment();
	Goods goods;
	GoodsPicture goodsPicture;
	TitleBarFragment fragBookDetail;
	int selectedIndex=0;
	int num=0;

	TextView detailLabel;
	TextView commentLabel;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_book_detail);

		fragBookDetail = (TitleBarFragment) getFragmentManager().findFragmentById(R.id.book_detail_titlebar);
		fragBookDetail.setBtnNextState(false);
		fragBookDetail.setTitleState(false);
		fragBookDetail.setSplitLineState(false);
		fragBookDetail.setOnGoBackListener(new TitleBarFragment.OnGoBackListener() {
                
                @Override
                public void onGoBack() {
                        finish();
                }
        });
		
		goods=(Goods) getIntent().getSerializableExtra("goods");
		detailLabel=(TextView) findViewById(R.id.book_detail);
		commentLabel=(TextView) findViewById(R.id.book_comment);

		TextView bookName=(TextView) findViewById(R.id.book_name);
		bookName.setText("书名:"+goods.getGoodsName());

		TextView bookPrice=(TextView) findViewById(R.id.book_price);
		bookPrice.setText("价格:"+goods.getGoodsPrice());
		
		TextView bookCount=(TextView) findViewById(R.id.book_count);
		bookCount.setText("库存:"+goods.getGoodsCount());
		
		goodsPicture=(GoodsPicture) findViewById(R.id.picture);
		goodsPicture.load(Server.serverAdress+goods.getGoodsImage());

		final EditText numEdit=(EditText) findViewById(R.id.edit_number);
		numEdit.setText("0");
		findViewById(R.id.btn_minus).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(num<=0){
					return;
				}
				num--;
				numEdit.setText(num+"");
			}
		});

		findViewById(R.id.btn_plus).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(num>=Integer.parseInt(goods.getGoodsCount())){
					return ;
				}
				num++;
				numEdit.setText(num+"");
			}
		});

		detailLabel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				selectedIndex=0;
				Intent intent=new Intent(BookDetailActivity.this,BookDetailFragment.class);
				intent.putExtra("goods", goods);
				changeFragmentContent(selectedIndex);
			}
		});


		commentLabel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				selectedIndex=1;
				//				String string=goods.getGoodsName();
				Intent intent=new Intent(BookDetailActivity.this,BookCommentFragment.class);
				intent.putExtra("goods", goods);
				changeFragmentContent(selectedIndex);
			}
		});

		findViewById(R.id.buy).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				num=Integer.parseInt(numEdit.getText().toString());
				if(num<=0){
					Toast.makeText(BookDetailActivity.this, "请输入购买数量", Toast.LENGTH_SHORT).show();
					return;
				}
				goBuy(num);
			}
		});

		findViewById(R.id.btn_preorder).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				num=Integer.parseInt(numEdit.getText().toString());
				if(num<=0){
					Toast.makeText(BookDetailActivity.this, "请输入购买数量", Toast.LENGTH_SHORT).show();
					return;
				}
				goPreorder(num);
			}
		});
	}


	protected void goPreorder(int num) {
		Intent itnt = new Intent(BookDetailActivity.this, AddToCartActivity.class);
		itnt.putExtra("number", num);
		itnt.putExtra("goods", goods);
		startActivity(itnt);
	}


	protected void goBuy(int num) {
		Intent itnt = new Intent(BookDetailActivity.this, BuyActivity.class);
		itnt.putExtra("number", num);
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
