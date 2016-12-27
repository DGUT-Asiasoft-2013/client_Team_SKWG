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

public class SetPayPasswordActivity extends Activity {
	SimpleTextInputcellFragment  fragNewPassword, fragNewPassowrdRepeat;
	TitleBarFragment fragChangeTitleBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.avtivity_set_paypassword);
		
		fragChangeTitleBar = (TitleBarFragment) getFragmentManager().findFragmentById(R.id.change_titlebar);
		fragChangeTitleBar.setBtnNextState(false);
		fragChangeTitleBar.setTitleName("设置支付密码", 16);
		fragChangeTitleBar.setOnGoBackListener(new TitleBarFragment.OnGoBackListener() {
                
                @Override
                public void onGoBack() {
                        finish();
                }
        });
		
		findViewById(R.id.submit).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				goSetPayPassword();
			}
		});
		fragNewPassword = (SimpleTextInputcellFragment) getFragmentManager().findFragmentById(R.id.input_new_pay_password);
		fragNewPassowrdRepeat = (SimpleTextInputcellFragment) getFragmentManager()
				.findFragmentById(R.id.input_new_pay_password_repeat);

	}

	@Override
	protected void onResume() {
		super.onResume();
		fragNewPassword.setLabelText("新密码");
		fragNewPassword.setHintText("请输入新密码");
		fragNewPassword.setEditText(true);
		fragNewPassowrdRepeat.setLabelText("重复新密码");
		fragNewPassowrdRepeat.setHintText("请重复输入新密码");
		fragNewPassowrdRepeat.setEditText(true);
	}


	void goSetPayPassword() {
		String newPassword = fragNewPassword.getText();
		String newPasswordRepeat = fragNewPassowrdRepeat.getText();
		if (newPassword.equals(newPasswordRepeat)) {
			MultipartBody.Builder body = new MultipartBody.Builder()
					.addFormDataPart("payPassword", MD5.getMD5(newPassword));

			Request request = Server.requestBuilderWithApi("user/setPayPassword")
					.method("post", null)
					.post(body.build())
					.build();
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

									SetPayPasswordActivity.this.onResponse(arg0, arg1);

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
		new AlertDialog.Builder(this).setMessage("设置成功").setPositiveButton("返回支付页面", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
//				Intent itnt = new Intent(SetPayPasswordActivity.this, PayActivity.class);
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
