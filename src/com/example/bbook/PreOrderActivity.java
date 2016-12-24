package com.example.bbook;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.example.bbook.api.Goods;
import com.example.bbook.api.Server;
import com.example.bbook.api.widgets.GoodsPicture;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.Response;

public class PreOrderActivity extends Activity{
	Goods goods;
	TextView tvGoodsName, tvGoodsType, tvGoodsPrice;
	EditText goodsCount;
	GoodsPicture goodsImage;
	int count = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_preorder);
		goods = (Goods) getIntent().getSerializableExtra("goods");
		count = getIntent().getIntExtra("number", 0);
		init();
		findViewById(R.id.submit).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onSubmit();
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		tvGoodsName.setText("书名: " + goods.getGoodsName());
		tvGoodsType.setText("类型: " + goods.getGoodsType());
		tvGoodsPrice.setText("价格: " + goods.getGoodsPrice());
		goodsImage.load(Server.serverAdress + goods.getGoodsImage());
		goodsCount.setText(count + "");
	}
	private void init() {
		goodsCount = (EditText) findViewById(R.id.count);
		goodsImage = (GoodsPicture) findViewById(R.id.goods_image);
		tvGoodsName = (TextView) findViewById(R.id.name);
		tvGoodsType = (TextView) findViewById(R.id.type);
		tvGoodsPrice = (TextView) findViewById(R.id.price);
	}
	protected void onSubmit() {
		int count = Integer.parseInt(goodsCount.getText().toString());
		String ordersId = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + goods.getId() + count;
		MultipartBody body = new MultipartBody.Builder()
				.addFormDataPart("goodsId", goods.getId() + "")
				.addFormDataPart("count", count + "")
				.addFormDataPart("ordersID", ordersId).build();
				
		
		Request request = Server.requestBuilderWithApi("orders/preorder")
				.method("post", null).post(body).build();
		
		Server.getSharedClient().newCall(request).enqueue(new Callback() {
			
			@Override
			public void onResponse(final Call arg0, final Response arg1) throws IOException {
				final String body = arg1.body().string();
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						PreOrderActivity.this.onResponse(arg0,body);						
					}
				});
			}
			
			@Override
			public void onFailure(final Call arg0, final IOException arg1) {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						PreOrderActivity.this.onFailture(arg0,arg1);
					}
				});
			}
		});
	}
	
	
	void onResponse(Call arg0, String responseBody){
		new AlertDialog.Builder(this).setTitle("成功")
		.setMessage(responseBody).show();
	}
	
	void onFailture(Call arg0, Exception arg1) {
		new AlertDialog.Builder(this)
		.setTitle("失败")
		.setMessage(arg1.getLocalizedMessage())
		.show();
	}
}
