package com.example.bbook;

import java.io.IOException;

import com.example.bbook.api.Goods;
import com.example.bbook.api.Server;
import com.example.bbook.api.widgets.GoodsPicture;
import com.example.bbook.api.widgets.TitleBarFragment;
import com.example.bbook.api.widgets.TitleBarFragment.OnGoBackListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
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
	
	Goods goods;
	String ordersId;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addgoodscomment);
		goods = (Goods) getIntent().getSerializableExtra("goods");
		ordersId=getIntent().getStringExtra("ordersId");
		Log.d("orderIdbb", ordersId);
		init();
		setEvent();
	}

	@Override
	protected void onResume() {
		super.onResume();
//		setInfo();
		
	}
	private void init() {
		tvGoodsName = (TextView) findViewById(R.id.goods_name);
		tvGoodsType = (TextView) findViewById(R.id.goods_type);
		btnSubmit = (Button) findViewById(R.id.submit);
//		imgGoods = (GoodsPicture) findViewById(R.id.img_goods);
		etComment = (EditText) findViewById(R.id.comment);
		
		
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
				
				isAddComment();
//				String commentText = AddBookCommentActivity.this.etComment.getText().toString();
//				if(commentText == null && goods != null) {
//					Toast.makeText(AddBookCommentActivity.this, "请输入评价内容", Toast.LENGTH_SHORT).show();;
//				} else {
//				//	Log.d("sdf",goodsDescribe.getRating()+"");
//					
//					addComment(commentText);
//				}
			}
		});
	}
	
	//确定是否评价
	public void isAddComment(){
		AlertDialog.Builder builder=new Builder(this);
		builder.setTitle("是否添加评价");
		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				String commentText = AddBookCommentActivity.this.etComment.getText().toString();
				if(commentText == null && goods != null) {
					Toast.makeText(AddBookCommentActivity.this, "请输入评价内容", Toast.LENGTH_SHORT).show();
					return;
				} else {
				//	Log.d("sdf",goodsDescribe.getRating()+"");
					addComment(commentText);
				}
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

			}
		});
		builder.show();
	}

	protected void addComment(String commentText) {
		int state=1;
		MultipartBody.Builder body = new MultipartBody.Builder()
				.addFormDataPart("commentText", commentText)
				.addFormDataPart("state", state+"")
				.addFormDataPart("orderId", ordersId);
		Log.d("orderId",ordersId);
		
		Log.d("goodsId",goods.getId()+"");
		
		Request request = Server.requestBuilderWithApi("/goods/addcomments")
				.method("post", null).post(body.build()).build();
		
		Server.getSharedClient().newCall(request).enqueue(new Callback() {
			
			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						Toast.makeText(AddBookCommentActivity.this, "发表成功", Toast.LENGTH_SHORT).show();
						goMyOrders();
					}
				});
			}
			
			@Override
			public void onFailure(Call arg0, IOException arg1) {
			}
		});
	}

	//转到我的订单
	public void goMyOrders(){
		new AlertDialog.Builder(this).setMessage("评论成功").setPositiveButton("查看我的订单", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent itnt = new Intent(AddBookCommentActivity.this,MyOrdersActivity.class);
				startActivity(itnt);
				finish();
			}
		}).show();
	}
}
