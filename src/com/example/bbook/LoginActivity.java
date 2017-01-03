package com.example.bbook;

import java.io.IOException;

import com.example.bbook.api.Server;
import com.example.bbook.api.User;
import com.example.bbook.api.widgets.TitleBarFragment;
import com.example.bbook.api.widgets.TitleBarFragment.OnGoBackListener;
import com.example.bbook.api.widgets.TitleBarFragment.OnGoNextListener;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
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

public class LoginActivity extends Activity {

	SimpleTextInputcellFragment fragAccount, fragPassword;
	TitleBarFragment fragTitleBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		fragAccount = (SimpleTextInputcellFragment) getFragmentManager().findFragmentById(R.id.input_account);
		fragPassword = (SimpleTextInputcellFragment) getFragmentManager().findFragmentById(R.id.input_password);

		fragTitleBar = (TitleBarFragment) getFragmentManager().findFragmentById(R.id.login_titlebar);
		fragTitleBar.setTitleName("登录", 18);
		fragTitleBar.setBtnNextText("忘记密码", 13);
		// 返回
		fragTitleBar.setOnGoBackListener(new OnGoBackListener() {

			@Override
			public void onGoBack() {
				finish();
			}
		});

		fragTitleBar.setOnGoNextListener(new OnGoNextListener() {

			@Override
			public void onGoNext() {
				goFotpassword();
			}
		});

		// 登陆按钮
		findViewById(R.id.btn_log).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				isUser();
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

	//判断用户是否存在
	void isUser(){
		String account = fragAccount.getText();
		if(account.isEmpty()){
			Toast toast = Toast.makeText(LoginActivity.this, "用户名不能为空", Toast.LENGTH_SHORT);
			toast.show();
			return;
		}
		MultipartBody.Builder requestBody=new MultipartBody.Builder()
				.addFormDataPart("account",account);

		Request request=Server.requestBuilderWithApi("/isuser")
				.method("post",null)
				.post(requestBody.build())
				.build();

		Server.getSharedClient().newCall(request).enqueue(new Callback() {
			@Override
			public void onResponse(final Call arg0, Response arg1) throws IOException {
				try{
					final String responseString = arg1.body().string();
					final Boolean result = new ObjectMapper().readValue(responseString, Boolean.class);
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if(result==true){			 //用户名存在,跳转判断用户密码是否一致
								isMatch();
							}
							else{						//用户名不存在
								LoginActivity.this.onIsUserd(arg0, responseString);
							}
						}
					});
				}catch(final Exception e){
					e.printStackTrace();

				}
			}

			@Override
			public void onFailure(Call arg0, IOException e) {
				e.printStackTrace();			
			}
		});

	}

	//判断用户名密码是否匹配
	void isMatch(){
		String account = fragAccount.getText();
		String password = fragPassword.getText();
		if(password.isEmpty()){
			Toast toast = Toast.makeText(LoginActivity.this, "密码不能为空", Toast.LENGTH_SHORT);
			toast.show();
			return;
		}

		password = MD5.getMD5(password);

		MultipartBody.Builder requestBody=new MultipartBody.Builder()
				.addFormDataPart("account",account)
				.addFormDataPart("passwordHash",password);

		Request request=Server.requestBuilderWithApi("/ismatch")
				.method("post",null)
				.post(requestBody.build())
				.build();

		Server.getSharedClient().newCall(request).enqueue(new Callback() {
			@Override
			public void onResponse(final Call arg0, Response arg1) throws IOException {
				try{
					final String responseString = arg1.body().string();
					final Boolean result = new ObjectMapper().readValue(responseString, Boolean.class);
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if(result==true){			//用户名密码匹配一致
								goHello();
							}
							else{						//用户名密码不一致
								LoginActivity.this.onIsMatch(arg0, responseString);
							}
						}
					});
				}catch(final Exception e){
					e.printStackTrace();

				}
			}

			@Override
			public void onFailure(Call arg0, IOException e) {
				e.printStackTrace();			
			}
		});

	}

	// 登陆
	void goHello() {
		String account = fragAccount.getText();
		String password = fragPassword.getText();

		password = MD5.getMD5(password);

		OkHttpClient client = Server.getSharedClient();

		MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder().addFormDataPart("account", account)
				.addFormDataPart("passwordHash", password);
		Request request = Server.requestBuilderWithApi("login").method("post", null).post(requestBodyBuilder.build())
				.build();
		final ProgressDialog dlg = new ProgressDialog(this);
		dlg.setCancelable(false);
		dlg.setCanceledOnTouchOutside(false);
		dlg.setMessage("正在登陆");
		dlg.show();

		client.newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(final Call arg0, final Response arg1) throws IOException {
				// ObjectMapper mapper = new ObjectMapper();
				// String response = arg1.body().toString();
				// final User user = mapper.readValue(response,
				// User.class);
				dlg.dismiss();
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
				dlg.dismiss();
				onFailture(arg0, arg1);
			}
		});
	}

	void onResponse(Call arg0, String responseBody) {
		// new
		// AlertDialog.Builder(this).setTitle("登陆成功").setMessage(responseBody)
		// .setPositiveButton("确定", new DialogInterface.OnClickListener() {
		//
		// @Override
		// public void onClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method
		// stub
		//		Toast.makeText(LoginActivity.this, "欢迎使用BBook", Toast.LENGTH_SHORT).show();
		Intent itnt = new Intent(LoginActivity.this, HelloWorldActivity.class);
		startActivity(itnt);
		MainActivity.mInstace.finish();
		Toast.makeText(LoginActivity.this, "欢迎使用BBook", Toast.LENGTH_SHORT).show();
		finish();
		// }
		// }).show();

	}

	void onFailture(Call arg0, Exception arg1) {
		new AlertDialog.Builder(this).setTitle("登陆失败").setMessage(arg1.getLocalizedMessage())
		.setNegativeButton("OK", null).show();
	}

	void goFotpassword() {
		Intent itnt = new Intent(this, PasswordRecoverActivity.class);
		startActivity(itnt);
		overridePendingTransition(R.anim.slide_in_right, R.anim.none);
	}

	protected void onIsUserd(Call arg0, String responseBody) {
		new AlertDialog.Builder(this).setTitle("温馨提示")
		.setMessage("该用户名不存在,请检查输入是否正确")
		.setNegativeButton("确定", null).show();
	}
	
	protected void onIsMatch(Call arg0, String responseBody) {
		new AlertDialog.Builder(this).setTitle("温馨提示")
		.setMessage("密码输入错误,请检查输入是否正确")
		.setNegativeButton("确定", null).show();
	}
}
