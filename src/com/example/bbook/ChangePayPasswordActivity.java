package com.example.bbook;

import java.io.IOException;

import com.example.bbook.api.Server;
import com.example.bbook.api.widgets.TitleBarFragment;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import inputcells.SimpleTextInputcellFragment;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.Response;

public class ChangePayPasswordActivity extends Activity {
	SimpleTextInputcellFragment fragBeforePassword, fragNewPassword, fragNewPassowrdRepeat;
	TitleBarFragment fragChangeTitleBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.avtivity_change_password);
		
		fragChangeTitleBar = (TitleBarFragment) getFragmentManager().findFragmentById(R.id.change_titlebar);
		fragChangeTitleBar.setBtnNextState(false);
		fragChangeTitleBar.setTitleName("修改支付密码", 16);
		fragChangeTitleBar.setOnGoBackListener(new TitleBarFragment.OnGoBackListener() {
                
                @Override
                public void onGoBack() {
                        finish();
                        overridePendingTransition(R.anim.none, R.anim.slide_out_right);
                }
        });
		
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
		fragBeforePassword.setLabelText("原支付密码");
		fragBeforePassword.setHintText("请输入支付原密码");
		fragBeforePassword.setEditText(true);
		fragNewPassword.setLabelText("新支付密码");
		fragNewPassword.setHintText("请输入支付新密码");
		fragNewPassword.setEditText(true);
		fragNewPassowrdRepeat.setLabelText("重复新支付密码");
		fragNewPassowrdRepeat.setHintText("请重复输入新支付密码");
		fragNewPassowrdRepeat.setEditText(true);
	}


	void goChange() {
		String beforePassword = fragBeforePassword.getText();
		String newPassword = fragNewPassword.getText();
		String newPasswordRepeat = fragNewPassowrdRepeat.getText();
		if (newPassword.equals(newPasswordRepeat)) {
			if(newPassword.isEmpty()){
				Toast toast = Toast.makeText(ChangePayPasswordActivity.this, "新支付密码不能为空", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;
			}
			MultipartBody.Builder body = new MultipartBody.Builder()
					.addFormDataPart("passwordHash", MD5.getMD5(beforePassword))
					.addFormDataPart("newPasswordHash", MD5.getMD5(newPassword));

			Request request = Server.requestBuilderWithApi("changepaypassword").method("post", null).post(body.build()).build();
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

									ChangePayPasswordActivity.this.onResponse(arg0, arg1);

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
		}else{
			new AlertDialog.Builder(this)
			.setTitle("温馨提示")
			.setMessage("新支付密码输入不一致,请重新输入")
			.setPositiveButton("好的", null)
			.show();
		}
	}

	void onResponse(Call arg0, Response arg1) {
		new AlertDialog.Builder(this).setMessage("修改成功").setPositiveButton("返回", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
//				Intent itnt = new Intent(ChangePayPasswordActivity.this, LoginActivity.class);
//				startActivity(itnt);
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
