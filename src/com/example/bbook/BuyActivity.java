package com.example.bbook;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.example.bbook.api.Goods;
import com.example.bbook.api.Server;
import com.example.bbook.api.entity.CommomInfo;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import inputcells.SimpleTextInputcellFragment;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.Response;

public class BuyActivity extends Activity {
	
	EditText goodsCount;
	TextView tvGoodsName, tvGoodsType, tvGoodsPrice, infoName, infoAddress, infoTel, infoPostcode;
	ImageView ivGoodsImage;
	Button btnSubmit, btnAddInfo;
	Goods goods;
	LinearLayout info;
	List<CommomInfo> dataList;
	CommomInfo defaultInfo;
	int count = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_buy);
		goods = (Goods) getIntent().getSerializableExtra("goods");
		count = getIntent().getIntExtra("number", 0);
		init();
		info.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				goManageInfo();
			}
		});
		btnSubmit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onSubmit();
			}
		});
		
	}

	protected void goManageInfo() {
		Intent itnt = new Intent(BuyActivity.this, ManageCommomInfoActivity.class);
		startActivity(itnt);
	}


	@Override
	protected void onResume() {
		super.onResume();
		reload();
//		setInfo();
	}
	
	
	
	private void reload() {
		tvGoodsName.setText("书名: " + goods.getGoodsName());
		tvGoodsType.setText("类型: " + goods.getGoodsType());
		tvGoodsPrice.setText("价格: " + goods.getGoodsPrice());
		goodsCount.setText(count + "");
		Request request = Server.requestBuilderWithApi("commominfo/default").get().build();
		Server.getSharedClient().newCall(request).enqueue(new Callback() {
			
			@Override
			public void onResponse(Call arg0, final Response arg1) throws IOException {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						try {
							BuyActivity.this.defaultInfo = new ObjectMapper()
									.readValue(arg1.body().string(), CommomInfo.class);
							runOnUiThread(new Runnable() {
								
								@Override
								public void run() {
									BuyActivity.this.setInfo();
								}
							});
						} catch (JsonParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (JsonMappingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
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

	void setInfo() {
		infoName.setText("收货人： " + defaultInfo.getName());
		infoAddress.setText("收货地址: " + defaultInfo.getAddress());
		infoTel.setText("联系电话: " + defaultInfo.getTel());
		infoPostcode.setText("邮编：" + defaultInfo.getPostCode());
		Log.d("info", defaultInfo.getName() + "   " + defaultInfo.getAddress() + "    " + defaultInfo.getTel() + "   " + defaultInfo.getPostCode());
	}
	
	protected void onSubmit() {
		
		int orderState = 2;
		count = Integer.parseInt(goodsCount.getText().toString());
		String orderId = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + goods.getId() + count;
		
		String name = defaultInfo.getName();
		String address = defaultInfo.getAddress();
		String tel = defaultInfo.getTel();
		String postCode = defaultInfo.getPostCode();
		Log.d("orderID", orderId);
		MultipartBody body = new MultipartBody.Builder()
				.addFormDataPart("ordersID", orderId)
				.addFormDataPart("ordersState", orderState + "")
				.addFormDataPart("goodsQTY", count + "")
				.addFormDataPart("goodsSum", (Integer.valueOf(goods.getGoodsPrice()) * count) + "")
				.addFormDataPart("buyerName", name)
				.addFormDataPart("buyerPhoneNum", tel)
				.addFormDataPart("buyerAddress", address)
				.addFormDataPart("postCode", postCode)
				.addFormDataPart("goodsId", goods.getId() + "")
				.build();
		
		Request request = Server.requestBuilderWithApi("orders")
				.method("post", null).post(body)
				.build();
		
		Server.getSharedClient().newCall(request).enqueue(new Callback() {
			
			@Override
			public void onResponse(final Call arg0, final Response arg1) throws IOException {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
//						goPay();
						try {
							BuyActivity.this.onResponse(arg0, arg1.body().string());
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
			}
			
			@Override
			public void onFailure(Call arg0, IOException arg1) {
				
			}
		});
	}

	void onResponse(Call arg0, String responseBody){
		new AlertDialog.Builder(this).setTitle("成功")
		.setMessage(responseBody).show();
	}
	void goPay() {
		Toast.makeText(this, "提交订单成功", Toast.LENGTH_SHORT).show();
		finish();
	}
	
	void init() {
		goodsCount = (EditText) findViewById(R.id.count);
		tvGoodsName = (TextView) findViewById(R.id.name);
		tvGoodsType = (TextView) findViewById(R.id.type);
		tvGoodsPrice = (TextView) findViewById(R.id.price);
		ivGoodsImage = (ImageView) findViewById(R.id.goods_image);
		infoName = (TextView)findViewById(R.id.info_name);
		infoAddress = (TextView) findViewById(R.id.info_address);
		infoTel = (TextView) findViewById(R.id.info_tel);
		infoPostcode = (TextView) findViewById(R.id.info_postcode);
		btnSubmit = (Button) findViewById(R.id.submit);
//		btnAddInfo = (Button) findViewById(R.id.add_info);
		info = (LinearLayout) findViewById(R.id.info);
	}
}
