package com.example.bbook;

import java.io.IOException;
import java.util.concurrent.RunnableFuture;

import com.example.bbook.api.Server;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.nsd.NsdManager.RegistrationListener;
import android.os.Bundle;
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

public class RegiestActivity extends Activity {
	SimpleTextInputcellFragment fragAccount, fragPassword, fragEmail, fragAddress, fragPhoneNum, fragRepeatPassword,
			fragName;
	PictureInputCellFragment fragImg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_regist);

		fragAccount = (SimpleTextInputcellFragment) getFragmentManager().findFragmentById(R.id.input_account);
		fragPassword = (SimpleTextInputcellFragment) getFragmentManager().findFragmentById(R.id.input_password);
		fragRepeatPassword = (SimpleTextInputcellFragment) getFragmentManager()
				.findFragmentById(R.id.input_repeat_password);
		fragEmail = (SimpleTextInputcellFragment) getFragmentManager().findFragmentById(R.id.input_email);
		fragAddress = (SimpleTextInputcellFragment) getFragmentManager().findFragmentById(R.id.input_address);
		fragPhoneNum = (SimpleTextInputcellFragment) getFragmentManager().findFragmentById(R.id.input_phoneNum);
		fragName = (SimpleTextInputcellFragment) getFragmentManager().findFragmentById(R.id.input_name);

		fragImg = (PictureInputCellFragment) getFragmentManager().findFragmentById(R.id.input_img);

		findViewById(R.id.submit).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				submit();
			}
		});
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
		String password = fragPassword.getText();
		String passwordrepeat = fragRepeatPassword.getText();
		if (!password.equals(passwordrepeat)) {
			Toast.makeText(RegiestActivity.this, "两次密码输入不一致", Toast.LENGTH_LONG).show();
			return;
		}
		password = MD5.getMD5(password);

		String account = fragAccount.getText();
		String name = fragName.getText();
		String email = fragEmail.getText();
		String address = fragAddress.getText();
		String phoneNum = fragPhoneNum.getText();

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

		final ProgressDialog progressDialog = new ProgressDialog(RegiestActivity.this);
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
								RegiestActivity.this.onResponse(arg0, responseString);
							
						}
					});
				} catch (final Exception e1) {
					e1.printStackTrace();
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							RegiestActivity.this.onFailure(arg0, e1);
							
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
						RegiestActivity.this.onFailure(arg0, arg1);
					}
				});
			}
		});
	}

	protected void onResponse(Call arg0, String responseBody) {
		new AlertDialog.Builder(this).setTitle("注册成功").setMessage(responseBody)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				}).show();

	}

	protected void onFailure(Call arg0, Exception arg1) {
		new AlertDialog.Builder(this).setTitle("注册失败").setMessage(arg1.getLocalizedMessage())
				.setNegativeButton("确定", null).show();
	}
}