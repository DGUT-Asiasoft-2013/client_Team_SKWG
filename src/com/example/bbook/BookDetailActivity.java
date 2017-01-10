package com.example.bbook;

import java.io.IOException;

import com.example.bbook.api.Goods;
import com.example.bbook.api.Server;
import com.example.bbook.api.Shop;
import com.example.bbook.api.widgets.GoodsPicture;
import com.example.bbook.api.widgets.TitleBarFragment;
import com.example.bbook.fragments.NumberPlusAndMinusFrament;
import com.example.bbook.fragments.NumberPlusAndMinusFrament.OnMinusClickListener;
import com.example.bbook.fragments.NumberPlusAndMinusFrament.OnPlusClickListener;
import com.example.bbook.fragments.pages.BookCommentFragment;
import com.example.bbook.fragments.pages.BookDetailFragment;
import com.example.bbook.fragments.pages.HomepageFragment;

import android.app.Activity;
import android.app.AlertDialog;
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
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.Response;
public class BookDetailActivity extends Activity {
	NumberPlusAndMinusFrament fragNumberPlusAndMinus;
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
		fragNumberPlusAndMinus = (NumberPlusAndMinusFrament) getFragmentManager().findFragmentById(R.id.number_plus_and_minus);
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
		
		fragNumberPlusAndMinus.setOnMinusClickListener(new OnMinusClickListener() {
			
			@Override
			public void onMinusClicked() {
				if(num <= 0) {
					return;
				} else {
					num--;
					fragNumberPlusAndMinus.setQuantityText(num + "");
				}
			}
		});
		
		fragNumberPlusAndMinus.setOnPlusClickListener(new OnPlusClickListener() {
			
			@Override
			public void onPlusClicked() {
				num++;
				fragNumberPlusAndMinus.setQuantityText(num + "");
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
		
		TextView bookSales=(TextView) findViewById(R.id.goods_sales);
		bookSales.setText("销量:"+goods.getGoodsSales());
		
		View connectSeller=findViewById(R.id.connect_seller);
		View goStore=findViewById(R.id.go_store);		
		
		goodsPicture=(GoodsPicture) findViewById(R.id.picture);
		goodsPicture.load(Server.serverAdress+goods.getGoodsImage());

		goStore.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				goShopActivity();
			}
		});
		
		connectSeller.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				   contactSeller();
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
				Intent intent=new Intent(BookDetailActivity.this,BookCommentFragment.class);
				intent.putExtra("goods", goods);
				changeFragmentContent(selectedIndex);
			}
		});

		findViewById(R.id.buy).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				num=Integer.parseInt(fragNumberPlusAndMinus.getQuantityText());
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
				num=Integer.parseInt(fragNumberPlusAndMinus.getQuantityText());
				if(num<=0){
					Toast.makeText(BookDetailActivity.this, "请输入购买数量", Toast.LENGTH_SHORT).show();
					return;
				}
				goPreorder(num);
			}
		});
	}


	protected void goPreorder(int num) {
//		Intent itnt = new Intent(BookDetailActivity.this, AddToCartActivity.class);
//		goods.setQuantity(num);
//		itnt.putExtra("goods", goods);
//		startActivity(itnt);
		
		int quantity = num;
		MultipartBody body = new MultipartBody.Builder()
				.addFormDataPart("quantity", quantity + "").build();
				
		
		Request request = Server.requestBuilderWithApi("shoppingcart/add/" + goods.getId())
				.method("post", null).post(body).build();
		
		Server.getSharedClient().newCall(request).enqueue(new Callback() {
			
			@Override
			public void onResponse(final Call arg0, final Response arg1) throws IOException {
				final String body = arg1.body().string();
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						BookDetailActivity.this.onResponse(arg0, body);
					}
				});
			}
			
			@Override
			public void onFailure(final Call arg0, final IOException arg1) {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						onFailture(arg0, arg1);
					}
				});
			}
		});
	}
	void onResponse(Call arg0, String responseBody){
		Toast.makeText(BookDetailActivity.this, "加入购物车成功", Toast.LENGTH_SHORT).show();;
	}
	
	void onFailture(Call arg0, Exception arg1) {
		new AlertDialog.Builder(this)
		.setTitle("失败")
		.setMessage(arg1.getLocalizedMessage())
		.show();
	}

	protected void goBuy(int num) {
		Intent itnt = new Intent(BookDetailActivity.this, BuyActivity.class);
		goods.setQuantity(num);
		itnt.putExtra("goods", goods);
		startActivity(itnt);
	}


	@Override
	protected void onResume() {
		super.onResume();
		changeFragmentContent(selectedIndex);

	}
//选择书本详情或者书本评论
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
//进入卖家店铺
	public void goShopActivity(){
		Shop shop=goods.getShop();
		Intent intent=new Intent(BookDetailActivity.this,ShopActivity.class);
		intent.putExtra("shop", shop);
		startActivity(intent);
	}
	
	//联系卖家
    public void contactSeller() {
    	Shop shop=goods.getShop();
        Intent itnt = new Intent(BookDetailActivity.this, ChatActivity.class);
        itnt.putExtra("shop", shop);
        startActivity(itnt);
}
}
