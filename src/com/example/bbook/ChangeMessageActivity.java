package com.example.bbook;


import java.io.IOException;

import com.example.bbook.api.Server;
import com.example.bbook.api.widgets.TitleBarFragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.EditText;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChangeMessageActivity extends Activity {
	
	
	TitleBarFragment fragTitleBar;
	EditText editMessage;
	float textSize = 16;
	String value ;
	String type;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_widget_change_message);
		value=getIntent().getStringExtra("value");
		type = getIntent().getStringExtra("type");
		fragTitleBar = (TitleBarFragment) getFragmentManager().findFragmentById(R.id.change_message_titlebar);
//		String value = getIntent().getStringExtra("value");
//		String type = getIntent().getStringExtra("type");
//		if(type == "changeName"){
//			setbar("昵称",type);
//		}else if(type == "changeEmail"){
//			setbar("邮箱",type);
//		}else if(type == "changePhone"){
//			setbar("电话号码",type);
//		}else if(type == "changeAddress"){
//			setbar("地址",type);
//		}
		editMessage=(EditText) findViewById(R.id.edit_change_message);
	}
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//把当前用户所要修改的原有值传入Edit编辑框
		editMessage.setText(value);
		if(type.equals("changeName")){
			setbar("昵称",type);
		}else if(type.equals("changeEmail")){
			setbar("邮箱",type);
		}else if(type.equals("changePhone")){
			setbar("电话号码",type);
		}else if(type.equals("changeAddress")){
			setbar("地址",type);
		}
	}
	
	
	
	
	
	//设置TitleBar内容
	void setbar(String str,final String type){
		
		fragTitleBar.setBtnNextState(true);
		fragTitleBar.setBtnNextText("保存", textSize);
		fragTitleBar.setTitleName(str, textSize);
		fragTitleBar.setSplitLineState(false);
		fragTitleBar.setOnGoBackListener(new TitleBarFragment.OnGoBackListener() {
			
			@Override
			public void onGoBack() {
				// TODO Auto-generated method stub
				finish();
			}
		});
		fragTitleBar.setOnGoNextListener(new TitleBarFragment.OnGoNextListener() {
			
			@Override
			public void onGoNext() {
				// TODO Auto-generated method stub
				value = editMessage.getText().toString();
				OkHttpClient client = Server.getSharedClient();
				MultipartBody.Builder requestBody = new MultipartBody.Builder()
						.addFormDataPart("type", type).addFormDataPart("value", value);
				Request request = Server.requestBuilderWithApi("changeMessage")
						.method("post", null).post(requestBody.build()).build();
				
				client.newCall(request).enqueue(new Callback() {
					
					@Override
					public void onResponse(final Call arg0, Response arg1) throws IOException {
						// TODO Auto-generated method stub
						try {
							final String responseString = arg1.body().string();
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									// TODO Auto-generated method stub
									ChangeMessageActivity.this.onResponse(arg0,responseString);
								}
							});
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							ChangeMessageActivity.this.onFailure(arg0, e);
						}
					}
					
					@Override
					public void onFailure(final Call arg0, IOException arg1) {
						// TODO Auto-generated method stub
						ChangeMessageActivity.this.onFailure(arg0,arg1);
					}
				});
			}
		});
	}
	
	
	void onResponse(Call arg0,String responseString){
		new AlertDialog.Builder(this).setTitle("修改成功").setMessage(responseString)
		.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				finish();
			}
		}).show();
	}
	
	void onFailure(Call arg0 , Exception e){
		new AlertDialog.Builder(this).setTitle("修改失败").setMessage(e.getLocalizedMessage())
		.setNegativeButton("确定", null).show();
	}
}
