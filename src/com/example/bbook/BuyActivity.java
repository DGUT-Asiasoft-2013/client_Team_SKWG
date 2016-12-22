package com.example.bbook;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.example.bbook.api.Goods;
import com.example.bbook.api.Server;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import inputcells.SimpleTextInputcellFragment;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.Response;

public class BuyActivity extends Activity {
	
	SimpleTextInputcellFragment fragInputName, fragInputAddress, fragInputTel, fragInputPostCode;
	EditText goodsCount;
	TextView tvGoodsName, tvGoodsType, tvGoodsPrice;
	ImageView ivGoodsImage;
	Button btnSubmit;
	Goods goods;
	
	int count = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_buy);
		goods = (Goods) getIntent().getSerializableExtra("goods");
		init();

		btnSubmit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onSubmit();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		fragInputName.setLabelText("收货人");
		fragInputName.setHintText("请输入收货人");
		fragInputAddress.setLabelText("收获地址");
		fragInputAddress.setHintText("请输入收货地址");
		fragInputTel.setLabelText("联系电话");
		fragInputTel.setHintText("请输入联系电话");
		fragInputPostCode.setLabelText("邮政编码");
		fragInputPostCode.setHintText("请输入邮政编码");
		reload();
	}
	
	
	
	private void reload() {
		tvGoodsName.setText(goods.getGoodsName());
		tvGoodsType.setText(goods.getGoodsType());
		tvGoodsPrice.setText(goods.getGoodsPrice());
	}

	protected void onSubmit() {
		
		int orderState = 2;
		count = Integer.parseInt(goodsCount.getText().toString());
		String orderId = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + goods.getId() + count;
		
		String name = fragInputName.getText();
		String address = fragInputAddress.getText();
		String tel = fragInputTel.getText();
		String postCode = fragInputPostCode.getText();
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
		
		Request request = Server.requestBuilderWithApi("goods/" + goods.getId() + "/orders")
				.method("post", null).post(body)
				.build();
		
		Server.getSharedClient().newCall(request).enqueue(new Callback() {
			
			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						goPay();
					}
				});
			}
			
			@Override
			public void onFailure(Call arg0, IOException arg1) {
				
			}
		});
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
		btnSubmit = (Button) findViewById(R.id.submit);
		fragInputName = (SimpleTextInputcellFragment) getFragmentManager().findFragmentById(R.id.input_name);
		fragInputAddress = (SimpleTextInputcellFragment) getFragmentManager().findFragmentById(R.id.input_address);
		fragInputTel = (SimpleTextInputcellFragment) getFragmentManager().findFragmentById(R.id.input_tel);
		fragInputPostCode = (SimpleTextInputcellFragment) getFragmentManager().findFragmentById(R.id.input_postcode);
	}
}
