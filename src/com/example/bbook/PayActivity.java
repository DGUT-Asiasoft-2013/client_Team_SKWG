package com.example.bbook;

import java.io.IOException;
import java.util.List;

import com.example.bbook.api.Server;
import com.fasterxml.jackson.annotation.ObjectIdGenerators.StringIdGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import inputcells.SimpleTextInputcellFragment;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PayActivity extends Activity {
	SimpleTextInputcellFragment fragPayPassword;
	List<String> toBePayOrders;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pay);
		toBePayOrders = (List<String>) getIntent().getSerializableExtra("toBePayOrders");
		fragPayPassword=(SimpleTextInputcellFragment) getFragmentManager().findFragmentById(R.id.pay_password);
		findViewById(R.id.btn_pay).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				goCheckPayPassword();

			}
		});
		checkPayPasswordIsExisted();
//		findViewById(R.id.text_set_paypassword).setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				goSetPayPassword();
//			}
//		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		fragPayPassword.setLabelText("支付密码");
		fragPayPassword.setHintText("请输入支付密码");
		fragPayPassword.setEditText(true);
	}

	public void checkPayPasswordIsExisted(){
		OkHttpClient client=Server.getSharedClient();
		Request request=Server.requestBuilderWithApi("user/PayPasswordIsExist").get().build();
		client.newCall(request).enqueue(new Callback() {
			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				final String responseStr=arg1.body().string();
				final Boolean isExisted=new ObjectMapper().readValue(responseStr,Boolean.class);
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if(isExisted){
					//		goCheckPayPassword();
						}else{
							goSetPayPassword();
						}
					}
				});
			}
			@Override
			public void onFailure(Call arg0, IOException arg1) {
			}
		});
	}
	public void goCheckPayPassword(){
		String payPassword=fragPayPassword.getText();
		OkHttpClient client=Server.getSharedClient();
		MultipartBody.Builder requestBody=new MultipartBody.Builder()
				.addFormDataPart("payPassword", MD5.getMD5(payPassword));
		Request request=Server.requestBuilderWithApi("payPassword")
				.method("post",null)
				.post(requestBody.build())
				.build();

		client.newCall(request).enqueue(new Callback() {
			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				final String responseStr=arg1.body().string();
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						
						Boolean checkPayPassword;
						try {
							checkPayPassword = new ObjectMapper().readValue(responseStr, Boolean.class);
							if(checkPayPassword){
								for(int i = 0; i < toBePayOrders.size(); i++) {
									goPay(toBePayOrders.get(i));
								}
							}
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

	public void goPay(String orderId){
		int state=3;
		OkHttpClient client=Server.getSharedClient();
		MultipartBody.Builder requestBody=new MultipartBody.Builder()
				.addFormDataPart("state", state+"");
		Request request=Server.requestBuilderWithApi("order/payfor/"+orderId)
				.method("post",null)
				.post(requestBody.build())
				.build();

		client.newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(final Call arg0, Response arg1) throws IOException {
				String responseStr=arg1.body().string();
				Log.e("OK",  responseStr);
				final boolean checkPayState=responseStr.equalsIgnoreCase("true");
				
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if(checkPayState==true){
							Toast.makeText(PayActivity.this,"支付成功", Toast.LENGTH_SHORT).show();
							goHomePage();
						}else {
							goonFailure();
							}
					}
				});

			}

			@Override
			public void onFailure(Call arg0, IOException arg1) {
				
			}
		});
	}

	public void goSetPayPassword(){
		Intent intent=new Intent(PayActivity.this,SetPayPasswordActivity.class);
		startActivity(intent);
	}
	public void goHomePage(){
		new AlertDialog.Builder(this).setMessage("支付成功").setPositiveButton("返回主页", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent itnt = new Intent(PayActivity.this,HelloWorldActivity.class);
				startActivity(itnt);
				finish();
			}
		}).show();
	}
	
	public void goonFailure() {
		new AlertDialog.Builder(this).setMessage("余额不足").setPositiveButton("马上充值", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent itnt = new Intent(PayActivity.this,MyWalletActivity.class);
				startActivity(itnt);
				finish();
			}
		}).show();
	}
}
