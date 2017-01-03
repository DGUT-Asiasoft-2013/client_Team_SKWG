package com.example.bbook;

import java.io.IOException;
import java.util.concurrent.RunnableFuture;

import com.example.bbook.api.Server;
import com.example.bbook.api.widgets.TitleBarFragment;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.nsd.NsdManager.RegistrationListener;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import inputcells.PictureInputCellFragment;
import inputcells.SimpleTextInputcellFragment;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends Activity {
	SimpleTextInputcellFragment fragAccount, fragPassword, fragEmail, fragAddress, fragPhoneNum, fragRepeatPassword,
	fragName;
	PictureInputCellFragment fragImg;

	TitleBarFragment fragTitleBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		fragAccount = (SimpleTextInputcellFragment) getFragmentManager().findFragmentById(R.id.input_account);
		fragPassword = (SimpleTextInputcellFragment) getFragmentManager().findFragmentById(R.id.input_password);
		fragRepeatPassword = (SimpleTextInputcellFragment) getFragmentManager()
				.findFragmentById(R.id.input_repeat_password);
		fragEmail = (SimpleTextInputcellFragment) getFragmentManager().findFragmentById(R.id.input_email);
		fragAddress = (SimpleTextInputcellFragment) getFragmentManager().findFragmentById(R.id.input_address);
		fragPhoneNum = (SimpleTextInputcellFragment) getFragmentManager().findFragmentById(R.id.input_phoneNum);
		fragPhoneNum.setEditNum(true);
		fragName = (SimpleTextInputcellFragment) getFragmentManager().findFragmentById(R.id.input_name);

		fragImg = (PictureInputCellFragment) getFragmentManager().findFragmentById(R.id.input_img);

		fragTitleBar = (TitleBarFragment) getFragmentManager().findFragmentById(R.id.register_titlebar);
		fragTitleBar.setTitleState(false);
		fragTitleBar.setNullViewState(true);
		fragTitleBar.setSplitLineState(false);
		fragTitleBar.setBtnNextText("注册", 16);
		fragTitleBar.setOnGoBackListener(new TitleBarFragment.OnGoBackListener() {

			@Override
			public void onGoBack() {
				finish();
			}
		});

		fragTitleBar.setOnGoNextListener(new TitleBarFragment.OnGoNextListener() {

			@Override
			public void onGoNext() {
				submit();
			}
		});

		//		findViewById(R.id.submit).setOnClickListener(new OnClickListener() {
		//			@Override
		//			public void onClick(View v) {
		//				submit();
		//			}
		//		});
	}

	@Override
	protected void onResume() {
		super.onResume();

		fragAccount.setLabelText("用户名:");
		fragAccount.setHintText("请输入用户名");
		fragName.setLabelText("昵称");
		fragName.setHintText("请输入昵称");
		fragPassword.setLabelText("密码:");
		fragPassword.setHintText("请输入密码");
		fragPassword.setEditText(true);
		fragRepeatPassword.setLabelText("重复密码:");
		fragRepeatPassword.setHintText("请重复输入密码");
		fragRepeatPassword.setEditText(true);
		fragEmail.setLabelText("邮箱:");
		fragEmail.setHintText("请输入邮箱");
		fragAddress.setLabelText("地址:");
		fragAddress.setHintText("请输入您的地址");
		fragPhoneNum.setLabelText("联系电话:");
		fragPhoneNum.setHintText("请输入您的联系电话");
		fragImg.setLabelText("图片");
		fragImg.setHintText("请选择图片");
	}

	void submit() {
		isUser();   		//判断用户是否存在
	}

	//判断用户是否存在
	void isUser(){
		String account = fragAccount.getText();
		if(account.isEmpty()){
			Toast toast = Toast.makeText(RegisterActivity.this, "用户名不能为空", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
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
							if(result==true){			//用户名已存在
								RegisterActivity.this.onIsUserd(arg0, responseString);
							}
							else{
								isEmail();
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

	//判断邮箱是否被使用
	void isEmail(){
		String email = fragEmail.getText();
		if(email.isEmpty()){
			Toast toast = Toast.makeText(RegisterActivity.this, "邮箱不能为空", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			return;
		}
		MultipartBody.Builder requestBody=new MultipartBody.Builder()
				.addFormDataPart("email", email);

		Request request=Server.requestBuilderWithApi("/isemail")
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
							if(result==true){			//邮箱已存在
								RegisterActivity.this.onIsEmail(arg0, responseString);
							}
							else{
								register();
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

	void register(){
		String password = fragPassword.getText();
		String passwordrepeat = fragRepeatPassword.getText();
		if(password.isEmpty()){
			Toast toast = Toast.makeText(RegisterActivity.this, "密码不能为空", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			return;
		}
		if (!password.equals(passwordrepeat)) {
			Toast toast=Toast.makeText(RegisterActivity.this, "两次密码输入不一致", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			return;
		}
		password = MD5.getMD5(password);

		String account = fragAccount.getText();

		String name = fragName.getText();
		String email = fragEmail.getText();
		String address = fragAddress.getText();
		String phoneNum = fragPhoneNum.getText();
		if(address.isEmpty()){
			Toast toast = Toast.makeText(RegisterActivity.this, "地址不能为空", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			return;
		}

		if(phoneNum.isEmpty()){
			Toast toast = Toast.makeText(RegisterActivity.this, "联系电话不能为空", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			return;
		}

		OkHttpClient client = Server.getSharedClient();
		// 构造发送内容
		MultipartBody.Builder requestbody = new MultipartBody.Builder().addFormDataPart("account", account)
				.addFormDataPart("name", name).addFormDataPart("email", email).addFormDataPart("address", address)
				.addFormDataPart("phoneNum", phoneNum).addFormDataPart("passwordHash", password);

		if (fragImg.getPngData() != null) {
			requestbody.addFormDataPart("avatar", "avatar",
					RequestBody.create(MediaType.parse("image/png"), fragImg.getPngData()));
		}
		// 发送请求到服务器
		Request request = Server.requestBuilderWithApi("register")
				.method("post", null).post(requestbody.build()).build();

		final ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this);
		progressDialog.setMessage("请稍侯");
		progressDialog.setCancelable(false);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.show();

		client.newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(final Call arg0, final Response arg1) throws IOException {
				final String responseString;
				try {
					responseString = arg1.body().string();
					runOnUiThread(new Runnable() {
						@Override

						public void run() {
							progressDialog.dismiss();
							RegisterActivity.this.onResponse(arg0, responseString);

						}
					});
				} catch (final Exception e1) {
					e1.printStackTrace();
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							RegisterActivity.this.onFailure(arg0, e1);

						}
					});
				}
			}

			@Override
			public void onFailure(final Call arg0, final IOException arg1) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						progressDialog.dismiss();
						RegisterActivity.this.onFailure(arg0, arg1);
					}
				});
			}
		});

	}

	protected void onResponse(Call arg0, String responseBody) {
		new AlertDialog.Builder(this).setTitle("注册成功")
		.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		}).show();

	}

	protected void onFailure(Call arg0, Exception arg1) {
		new AlertDialog.Builder(this).setTitle("注册失败")
		.setNegativeButton("确定", null).show();
	}

	protected void onIsUserd(Call arg0, String responseBody) {
		new AlertDialog.Builder(this).setTitle("温馨提示")
		.setMessage("当前输入用户名已被使用,请重新输入")
		.setNegativeButton("确定", null).show();
	}

	protected void onIsEmail(Call arg0, String responseBody) {
		new AlertDialog.Builder(this).setTitle("温馨提示")
		.setMessage("当前输入邮箱已被使用,请重新输入")
		.setNegativeButton("确定", null).show();
	}
}