package com.example.bbook;

import java.io.IOException;

import com.example.bbook.api.Server;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import inputcells.SimpleTextInputcellFragment;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.Response;

public class AddCommomInfoActivity extends Activity {
	SimpleTextInputcellFragment fragInputName, fragInputAddress, fragInputTel, fragInputPostcode;
	Button btnSubmit;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.actiivty_add_commom_info);
		init();
		btnSubmit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onSubmit();
			}
		});
	}
	
	protected void onSubmit() {
		String name = fragInputName.getText();
		String address = fragInputAddress.getText();
		String tel = fragInputTel.getText();
		String postCode = fragInputPostcode.getText();
		Log.d("name", name);
		Log.d("address", address);
		Log.d("tel", tel);
		Log.d("postCode", postCode);
		MultipartBody body = new MultipartBody.Builder()
				.addFormDataPart("name", name)
				.addFormDataPart("address", address)
				.addFormDataPart("tel", tel)
				.addFormDataPart("postCode", postCode).build();
		
		Request request = Server.requestBuilderWithApi("commominfo/add")
				.method("post", null)
				.post(body).build();
		Server.getSharedClient().newCall(request).enqueue(new Callback() {
			
			@Override
			public void onResponse(final Call arg0, final Response arg1) throws IOException {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						try {
							AddCommomInfoActivity.this.onResponse(arg0, arg1.body().string());
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
			}
			
			@Override
			public void onFailure(Call arg0, IOException arg1) {
				AddCommomInfoActivity.this.onFailure(arg0, arg1);
			}
		});
	}

	void onResponse(Call arg0, String responseBody){
		new AlertDialog.Builder(this).setTitle("成功")
		.setMessage(responseBody).show();
	}
	
	void onFailure(Call arg0, Exception arg1) {
		new AlertDialog.Builder(this)
		.setTitle("失败")
		.setMessage(arg1.getLocalizedMessage())
		.show();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		fragInputName.setLabelText("收货人");
		fragInputName.setHintText("请输入收货人");
		fragInputAddress.setLabelText("收货地址");
		fragInputAddress.setHintText("请输入收货地址");
		fragInputTel.setLabelText("联系电话");
		fragInputTel.setHintText("请输入联系电话");
		fragInputPostcode.setLabelText("邮政编码");
		fragInputPostcode.setHintText("请输入邮政编码");
	}
	
	void init() {
		fragInputName = (SimpleTextInputcellFragment) getFragmentManager().findFragmentById(R.id.input_name);
		fragInputAddress = (SimpleTextInputcellFragment) getFragmentManager().findFragmentById(R.id.input_address);
		fragInputTel = (SimpleTextInputcellFragment) getFragmentManager().findFragmentById(R.id.input_tel);
		fragInputPostcode = (SimpleTextInputcellFragment) getFragmentManager().findFragmentById(R.id.input_postcode);
		btnSubmit = (Button) findViewById(R.id.submit);
	}
}
