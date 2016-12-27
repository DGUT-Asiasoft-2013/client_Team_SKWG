package com.example.bbook;

import java.io.IOException;

import com.example.bbook.api.Server;
import com.example.bbook.api.User;
import com.example.bbook.api.widgets.AvatarView;
import com.example.bbook.api.widgets.ChangeItemFragment;
import com.example.bbook.api.widgets.TitleBarFragment;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ChangeUserMessageActivity extends Activity {
	ChangeItemFragment changeName,changeEmail,changeAddress,changePhone;
	AvatarView avatar;
	TitleBarFragment fragTitleBar;
	float textSize = 16;
	int color = Color.GRAY;
	private String value = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_usermessage);
		FragmentManager fg=getFragmentManager();
		changeName=(ChangeItemFragment) fg.findFragmentById(R.id.change_Name);
		changeEmail=(ChangeItemFragment) fg.findFragmentById(R.id.change_Email);
		changeAddress=(ChangeItemFragment) fg.findFragmentById(R.id.change_Address);
		changePhone=(ChangeItemFragment) fg.findFragmentById(R.id.change_Phone);
		avatar=(AvatarView) findViewById(R.id.avatar);
		
		fragTitleBar = (TitleBarFragment) fg.findFragmentById(R.id.changeUserMessage_titlebar);
		fragTitleBar.setBtnNextState(false);
		fragTitleBar.setTitleName("我的资料", textSize);
		fragTitleBar.setSplitLineState(false);
		fragTitleBar.setOnGoBackListener(new TitleBarFragment.OnGoBackListener() {
			
			@Override
			public void onGoBack() {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		//更改昵称
		findViewById(R.id.change_Name).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String value = changeName.getText();
				String change = "changeName";
				goChange(change,value);
			}
		});
		//更改邮箱
		findViewById(R.id.change_Email).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String value = changeEmail.getText();
				String change = "changeEmail";
				goChange(change,value);
			}
		});
		//更改电话号码
		findViewById(R.id.change_Phone).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String value = changeEmail.getText();
				String change = "changePhone";
				goChange(change,value);
			}
		});
		//更改地址
		findViewById(R.id.change_Address).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String change = "changeAddress";
				String value = changeAddress.getText();
				goChange(change,value);
			}
		});
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		changeName.setmessageTitleText("昵称:", textSize);
		changeEmail.setmessageTitleText("邮箱:", textSize);
		changeAddress.setmessageTitleText("地址:", textSize);
		changePhone.setmessageTitleText("手机号码:", textSize);
		
		OkHttpClient client = Server.getSharedClient();
		Request request = Server.requestBuilderWithApi("me").method("get", null).build();
		
		client.newCall(request).enqueue(new Callback() {
			
			@Override
			public void onResponse(final Call arg0, Response arg1) throws IOException {
				// TODO Auto-generated method stub
				final User user = new ObjectMapper().readValue(arg1.body().bytes(), User.class);
				runOnUiThread(new Runnable() {
					public void run() {
						ChangeUserMessageActivity.this.onResponse(arg0,user);
					}
				});
//				changeName.setmessageEditText(user.getName(), textSize, color);
//				changeEmail.setmessageEditText(user.getEmail(), textSize, color);
//				changeAddress.setmessageEditText(user.getAddress(), textSize, color);
//				changePhone.setmessageEditText(user.getPhoneNum(), textSize, color);
			}
			
			@Override
			public void onFailure(Call arg0, IOException arg1) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	void onResponse(Call arg0 , User user){
		avatar.load(user);
		changeName.setmessageEditText(user.getName(), textSize, color);
		changeEmail.setmessageEditText(user.getEmail(), textSize, color);
		changeAddress.setmessageEditText(user.getAddress(), textSize, color);
		changePhone.setmessageEditText(user.getPhoneNum(), textSize, color);
	}
	
	
	
	
	
	
	
	//自定义一个带value的监听器
    void goChange(String change,String value){
    	Intent itnt = new Intent(this, ChangeMessageActivity.class);
    	itnt.putExtra("type", change);
    	itnt.putExtra("value", value);
		startActivity(itnt);
    }
}
