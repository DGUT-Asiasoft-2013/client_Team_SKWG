package com.example.bbook;

import java.io.IOException;

import com.example.bbook.api.Goods;
import com.example.bbook.api.Server;
import com.example.bbook.api.widgets.GoodsPicture;
import com.example.bbook.api.widgets.TitleBarFragment;
import com.example.bbook.api.widgets.TitleBarFragment.OnGoBackListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.Response;

public class AddBookCommentActivity extends Activity {
	TitleBarFragment fragTitleBar;
	GoodsPicture imgGoods;
	TextView tvGoodsName, tvGoodsType;
	EditText etComment;
	Button btnSubmit;
	RatingBar goodsDescribe,sellerAttitute,sendSpeed;
	
	Goods goods;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addgoodscomment);
		goods = (Goods) getIntent().getSerializableExtra("goods");
		init();
		setEvent();
	}

	@Override
	protected void onResume() {
		super.onResume();
		setInfo();
		
	}
	private void init() {
		tvGoodsName = (TextView) findViewById(R.id.goods_name);
		tvGoodsType = (TextView) findViewById(R.id.goods_type);
		btnSubmit = (Button) findViewById(R.id.submit);
		imgGoods = (GoodsPicture) findViewById(R.id.img_goods);
		etComment = (EditText) findViewById(R.id.comment);
		
		goodsDescribe=(RatingBar) findViewById(R.id.goods_describe);
		sellerAttitute=(RatingBar) findViewById(R.id.seller_attitute);
		sendSpeed=(RatingBar) findViewById(R.id.send_speed);
		
		fragTitleBar = (TitleBarFragment) getFragmentManager().findFragmentById(R.id.title_bar);
		fragTitleBar.setBtnNextState(false);
		fragTitleBar.setOnGoBackListener(new OnGoBackListener() {
			
			@Override
			public void onGoBack() {
				finish();
			}
		});
		fragTitleBar.setTitleName("发表评价", 16);

	}
	
	void setInfo() {
		if(goods != null) {
			imgGoods.load(Server.serverAdress + goods.getGoodsImage());
			tvGoodsName.setText(goods.getGoodsName());
			tvGoodsType.setText(goods.getGoodsType());
		}
	}
	
	void setEvent() {
		btnSubmit.setOnClickListener(new OnClickListener() {
		
			
			@Override
			public void onClick(View v) {
				String commentText = AddBookCommentActivity.this.etComment.getText().toString();
				if(commentText == null && goods != null) {
					Toast.makeText(AddBookCommentActivity.this, "请输入评价内容", Toast.LENGTH_SHORT).show();;
				} else {
					Log.d("sdf",goodsDescribe.getRating()+"");
					addComment(commentText);
				}
			}
		});
	}

	protected void addComment(String commentText) {
		MultipartBody.Builder body = new MultipartBody.Builder()
				.addFormDataPart("commentText", commentText);
		Request request = Server.requestBuilderWithApi("goods/" + goods.getId() + "/addcomments")
				.method("post", null).post(body.build()).build();
		
		Server.getSharedClient().newCall(request).enqueue(new Callback() {
			
			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						Toast.makeText(AddBookCommentActivity.this, "发表成功", Toast.LENGTH_SHORT).show();
					}
				});
			}
			
			@Override
			public void onFailure(Call arg0, IOException arg1) {
			}
		});
	}
}
