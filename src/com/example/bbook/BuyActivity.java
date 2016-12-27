package com.example.bbook;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.example.bbook.api.Goods;
import com.example.bbook.api.Server;
import com.example.bbook.api.entity.CommomInfo;
import com.example.bbook.api.widgets.GoodsPicture;
import com.example.bbook.api.widgets.TitleBarFragment;
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
	final static int SELECTED_INFO_CODE = 1;
	EditText goodsCount;
	TextView tvGoodsName, tvGoodsType, tvGoodsPrice, infoName,
	infoAddress, infoTel, infoPostcode, tvSum;
	GoodsPicture goodsImage;
	Button btnSubmit, btnAddInfo;
	Goods goods;
	LinearLayout info;
	List<CommomInfo> dataList;
	CommomInfo defaultInfo;
	CommomInfo selectedInfo;
	TitleBarFragment fragOrderConfirm;

	int count = 0;
	double sum = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_buy);

		fragOrderConfirm = (TitleBarFragment) getFragmentManager().findFragmentById(R.id.order_confirm_titlebar);
		fragOrderConfirm.setTitleName("确认订单", 16);
		fragOrderConfirm.setBtnNextState(false);
		fragOrderConfirm.setOnGoBackListener(new TitleBarFragment.OnGoBackListener() {

			@Override
			public void onGoBack() {
				finish();
			}
		});

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
//		startActivity(itnt);
		startActivityForResult(itnt, SELECTED_INFO_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(SELECTED_INFO_CODE == resultCode) {
			selectedInfo = (CommomInfo) data.getSerializableExtra("selectedInfo");
			setInfo();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		reload();
		// setInfo();
	}

	private void reload() {
		tvGoodsName.setText("书名: " + goods.getGoodsName());
		tvGoodsType.setText("类型: " + goods.getGoodsType());
		tvGoodsPrice.setText("价格: " + goods.getGoodsPrice());
		goodsImage.load(Server.serverAdress + goods.getGoodsImage());
		goodsCount.setText(count + "");
		sum = Double.parseDouble(goods.getGoodsPrice()) * count;
		tvSum.setText(sum + "");
		Request request = Server.requestBuilderWithApi("commominfo/default").get().build();
		Server.getSharedClient().newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, final Response arg1) throws IOException {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						try {
							BuyActivity.this.defaultInfo = new ObjectMapper().readValue(arg1.body().string(),
									CommomInfo.class);
							if(selectedInfo == null) {
								selectedInfo = defaultInfo;
							}
							runOnUiThread(new Runnable() {

								@Override
								public void run() {
									BuyActivity.this.setInfo();
								}
							});
						} catch (JsonParseException e) {
							e.printStackTrace();
						} catch (JsonMappingException e) {
							e.printStackTrace();
						} catch (IOException e) {
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

	void setInfo() {
		infoName.setText("收货人： " + selectedInfo.getName());
		infoAddress.setText("收货地址: " + selectedInfo.getAddress());
		infoTel.setText("联系电话: " + selectedInfo.getTel());
		infoPostcode.setText("邮编：" + selectedInfo.getPostCode());
		Log.d("info", selectedInfo.getName() + "   " + selectedInfo.getAddress() + "    " + selectedInfo.getTel() + "   "
				+ selectedInfo.getPostCode());
	}

	protected void onSubmit() {

		int orderState = 2;
		count = Integer.parseInt(goodsCount.getText().toString());
		final String orderId = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + goods.getId() + count;

		String name = selectedInfo.getName();
		String address = selectedInfo.getAddress();
		String tel = selectedInfo.getTel();
		String postCode = selectedInfo.getPostCode();
		Log.d("orderID", orderId);
		MultipartBody body = new MultipartBody.Builder().addFormDataPart("ordersID", orderId)
				.addFormDataPart("ordersState", orderState + "").addFormDataPart("goodsQTY", count + "")
				.addFormDataPart("goodsSum", (Integer.valueOf(goods.getGoodsPrice()) * count) + "")
				.addFormDataPart("buyerName", name).addFormDataPart("buyerPhoneNum", tel)
				.addFormDataPart("buyerAddress", address).addFormDataPart("postCode", postCode)
				.addFormDataPart("goodsId", goods.getId() + "").build();

		Request request = Server.requestBuilderWithApi("orders").method("post", null).post(body).build();

		Server.getSharedClient().newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(final Call arg0, final Response arg1) throws IOException {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// goPay();
						try {
							// BuyActivity.this.onResponse(arg0,
							// arg1.body().string());
							goPay(orderId);
						} catch (Exception e) {
							// TODO Auto-generated
							// catch block
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

	void onResponse(Call arg0, String responseBody) {
		new AlertDialog.Builder(this).setTitle("成功").setMessage(responseBody).show();
	}

	void goPay(String orderId) {
		Toast.makeText(this, "提交订单成功", Toast.LENGTH_SHORT).show();
		Intent intent = new Intent(BuyActivity.this, PayActivity.class);
		intent.putExtra("orderId", orderId);
		startActivity(intent);
		finish();
	}

	void init() {
		goodsCount = (EditText) findViewById(R.id.count);
		tvGoodsName = (TextView) findViewById(R.id.name);
		tvGoodsType = (TextView) findViewById(R.id.type);
		tvGoodsPrice = (TextView) findViewById(R.id.price);
		tvSum = (TextView) findViewById(R.id.sum);
		goodsImage = (GoodsPicture) findViewById(R.id.goods_image);
		infoName = (TextView) findViewById(R.id.info_name);
		infoAddress = (TextView) findViewById(R.id.info_address);
		infoTel = (TextView) findViewById(R.id.info_tel);
		infoPostcode = (TextView) findViewById(R.id.info_postcode);
		btnSubmit = (Button) findViewById(R.id.submit);
		// btnAddInfo = (Button) findViewById(R.id.add_info);
		info = (LinearLayout) findViewById(R.id.info);
	}
}
