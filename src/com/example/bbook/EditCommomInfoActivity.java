package com.example.bbook;
import java.io.IOException;

import com.example.bbook.api.Server;
import com.example.bbook.api.entity.CommomInfo;
import com.example.bbook.api.widgets.TitleBarFragment;
import com.example.bbook.api.widgets.TitleBarFragment.OnGoBackListener;
import com.example.bbook.api.widgets.TitleBarFragment.OnGoNextListener;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.Response;

public class EditCommomInfoActivity extends Activity{
	TitleBarFragment fragTitleBar;
	EditText etName, etAddress, etTel, etPostCode;
	CommomInfo info;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_commominfo);
		info = (CommomInfo) getIntent().getSerializableExtra("info");
		init();
		setEvent();
		setInfo(info);
	}
	private void setEvent() {
		fragTitleBar.setOnGoBackListener(new OnGoBackListener() {
			
			@Override
			public void onGoBack() {
				finish();
			}
		});
		fragTitleBar.setOnGoNextListener(new OnGoNextListener() {
			
			@Override
			public void onGoNext() {
				onSubmit();
			}
		});
	}
	protected void onSubmit() {
		String name = etName.getText().toString();
		String address = etAddress.getText().toString();
		String tel = etTel.getText().toString();
		String postCode = etPostCode.getText().toString();
		MultipartBody body = new MultipartBody.Builder()
				.addFormDataPart("name", name)
				.addFormDataPart("address", address)
				.addFormDataPart("tel", tel)
				.addFormDataPart("postCode", postCode).build();
		Request request = Server.requestBuilderWithApi("/commominfo/change/" + info.getId())
				.method("get", null).post(body).build();
		Server.getSharedClient().newCall(request).enqueue(new Callback() {
			
			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						EditCommomInfoActivity.this.finish();
					}
				});
			}
			
			@Override
			public void onFailure(Call arg0, IOException arg1) {
				
			}
		});
	}
	private void setInfo(CommomInfo info) {
		if(info != null) {
			etName.setText(info.getName());
			etAddress.setText(info.getAddress());
			etTel.setText(info.getTel());
			etPostCode.setText(info.getPostCode());
		}
	}
	private void init() {
		fragTitleBar = (TitleBarFragment) getFragmentManager().findFragmentById(R.id.title_bar);
		fragTitleBar.setTitleName("编辑", 16);
		fragTitleBar.setBtnNextText("确定", 15);
		etName = (EditText) findViewById(R.id.name);
		etAddress = (EditText) findViewById(R.id.address);
		etTel = (EditText) findViewById(R.id.tel);
		etPostCode = (EditText) findViewById(R.id.post_code);
	}
	
	

}
