package com.example.bbook;

import java.io.IOException;

import com.example.bbook.api.Server;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import inputcells.SimpleTextInputcellFragment;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.Response;

public class ChangePasswordActivity extends Activity {
	SimpleTextInputcellFragment fragBeforePassword, fragNewPassword, fragNewPassowrdRepeat;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.avtivity_change_password);
		findViewById(R.id.submit).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				goChange();
			}
		});
		fragBeforePassword = (SimpleTextInputcellFragment) getFragmentManager()
				.findFragmentById(R.id.input_before_passowrd);
		fragNewPassword = (SimpleTextInputcellFragment) getFragmentManager().findFragmentById(R.id.input_new_password);
		fragNewPassowrdRepeat = (SimpleTextInputcellFragment) getFragmentManager()
				.findFragmentById(R.id.input_new_password_repeat);

	}

	@Override
	protected void onResume() {
		super.onResume();
		fragBeforePassword.setLabelText("原密码");
		fragBeforePassword.setHintText("请输入原密码");
		fragBeforePassword.setEditText(true);
		fragNewPassword.setLabelText("新密码");
		fragNewPassword.setHintText("请输入新密码");
		fragBeforePassword.setEditText(true);
		fragNewPassowrdRepeat.setLabelText("重复新密码");
		fragNewPassowrdRepeat.setHintText("请重复输入新密码");
		fragNewPassowrdRepeat.setEditText(true);
	}


	void goChange() {
		String beforePassword = fragBeforePassword.getText();
		String newPassword = fragNewPassword.getText();
		String newPasswordRepeat = fragNewPassowrdRepeat.getText();
		if (newPassword.equals(newPasswordRepeat)) {
			MultipartBody.Builder body = new MultipartBody.Builder()
					.addFormDataPart("passwordHash", MD5.getMD5(beforePassword))
					.addFormDataPart("newPasswordHash", MD5.getMD5(newPassword));

			Request request = Server.requestBuilderWithApi("change").method("post", null).post(body.build()).build();
			Server.getSharedClient().newCall(request).enqueue(new Callback() {

				@Override
				public void onResponse(final Call arg0, final Response arg1) throws IOException {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							boolean isChange;
							try {
								isChange = new ObjectMapper().readValue(arg1.body().string(), Boolean.class);
								if (isChange) {

									ChangePasswordActivity.this.onResponse(arg0, arg1);

								} else {
									onFail();
								}
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
	}

	void onResponse(Call arg0, Response arg1) {
		new AlertDialog.Builder(this).setMessage("修改成功").setPositiveButton("返回登录界面", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				Intent itnt = new Intent(ChangePasswordActivity.this, LoginActivity.class);
				startActivity(itnt);
				finish();
			}
		}).show();
		
		
	}

	void onFail() {
		new AlertDialog.Builder(this).setMessage("修改失败")
		.setNegativeButton("OK",  new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				finish();
			}
		}).show();
	}
}
