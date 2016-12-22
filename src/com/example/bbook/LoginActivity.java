package com.example.bbook;

import java.io.IOException;

import com.example.bbook.api.Server;
import com.example.bbook.api.User;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import inputcells.SimpleTextInputcellFragment;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends Activity {
	
	SimpleTextInputcellFragment fragAccount,fragPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		fragAccount = (SimpleTextInputcellFragment) getFragmentManager().findFragmentById(R.id.input_account );
		fragPassword = (SimpleTextInputcellFragment) getFragmentManager().findFragmentById(R.id.input_password);
		//登陆按钮
		findViewById(R.id.btn_log).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				goHello();
			}
		});
		//忘记密码按钮
		findViewById(R.id.btn_fotpassword).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				goFotpassword();
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		fragAccount.setLabelText("用户名");
		fragAccount.setHintText("请输入用户名");
		fragPassword.setLabelText("密码");
		fragPassword.setHintText("请输入密码");
		fragPassword.setEditText(true);
	}
	
	//登陆
	void goHello(){
		String account = fragAccount.getText();
		String password = fragPassword.getText();
		password = MD5.getMD5(password);
		
		OkHttpClient client = Server.getSharedClient();
		
		MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder().addFormDataPart("account", account)
				.addFormDataPart("passwordHash", password);
		Request request = Server.requestBuilderWithApi("login").method("post", null)
				.post(requestBodyBuilder.build()).build();
		final ProgressDialog dlg = new ProgressDialog(this);
		dlg.setCancelable(false);
		dlg.setCanceledOnTouchOutside(false);
		dlg.setMessage("正在登陆");
		dlg.show();
		
		client.newCall(request).enqueue(new Callback() {
			
			@Override
			public void onResponse(final Call arg0, final Response arg1) throws IOException {
//				ObjectMapper mapper = new ObjectMapper();
//				String response = arg1.body().toString();
//				final User user = mapper.readValue(response, User.class);
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						
						try {
							ObjectMapper mapper = new ObjectMapper();
							String response = arg1.body().string();
							Log.e("OK", response);
							final User user = mapper.readValue(response, User.class);
							LoginActivity.this.onResponse(arg0, user.getAccount());
						} catch (IOException e) {
							e.printStackTrace();
							LoginActivity.this.onFailture(arg0, e);
						}
					}
				});
				
			}
			
			@Override
			public void onFailure(Call arg0, IOException arg1) {
				onFailture(arg0,arg1);
			}
		});
	}
	void onResponse(Call arg0, String responseBody){
		new AlertDialog.Builder(this).setTitle("登陆成功")
		.setMessage(responseBody).setPositiveButton("确定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				Intent itnt = new Intent(LoginActivity.this, HelloWorldActivity.class);
				startActivity(itnt);
				finish();
			}
		}).show();
		
	}
	void onFailture(Call arg0, Exception arg1) {
		new AlertDialog.Builder(this)
		.setTitle("登陆失败")
		.setMessage(arg1.getLocalizedMessage())
		.setNegativeButton("OK", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		})
		.show();
	}
	
	
	void goFotpassword(){
		Intent itnt = new Intent(this, PasswordRecoverActivity.class);
		startActivity(itnt);
	}
}
