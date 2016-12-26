package com.example.bbook;

import java.io.IOException;

import com.example.bbook.api.Server;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.app.Activity;
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
	String orderId;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pay);
		
		orderId=getIntent().getStringExtra("orderId");
		fragPayPassword=(SimpleTextInputcellFragment) getFragmentManager().findFragmentById(R.id.pay_password);
		findViewById(R.id.btn_pay).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				goCheckPayPassword();

			}
		});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		fragPayPassword.setLabelText("支付密码");
		fragPayPassword.setHintText("请输入支付密码");
		fragPayPassword.setEditText(true);
	}

	public void goCheckPayPassword(){
		String payPassword=fragPayPassword.getText();

		OkHttpClient client=Server.getSharedClient();
		MultipartBody.Builder requestBody=new MultipartBody.Builder()
				.addFormDataPart("payPassword", payPassword);

		Request request=Server.requestBuilderWithApi("payPassword")
				.method("post",null)
				.post(requestBody.build())
				.build();

		client.newCall(request).enqueue(new Callback() {
			
			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				// TODO Auto-generated method stub
				String responseStr=arg1.body().string();
				Boolean checkPayPassword=new ObjectMapper().readValue(responseStr, Boolean.class);
				if(checkPayPassword){
					goPay();
				}
			}
			
			@Override
			public void onFailure(Call arg0, IOException arg1) {
				// TODO Auto-generated method stub
				
			}
		});

	}
	
	public void goPay(){
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
			public void onResponse(Call arg0, Response arg1) throws IOException {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						
						Toast.makeText(PayActivity.this,"支付成功", Toast.LENGTH_SHORT).show();
					}
				});
				
			}
			
			@Override
			public void onFailure(Call arg0, IOException arg1) {
				// TODO Auto-generated method stub
				
			}
		});
	}
}
