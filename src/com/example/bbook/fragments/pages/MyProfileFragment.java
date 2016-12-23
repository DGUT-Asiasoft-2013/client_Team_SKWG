package com.example.bbook.fragments.pages;

import java.io.IOException;

import com.example.bbook.ChangePasswordActivity;
import com.example.bbook.LoginActivity;
import com.example.bbook.MystoreActivity;
import com.example.bbook.OpenStoreActivity;
import com.example.bbook.R;
import com.example.bbook.api.Server;
import com.example.bbook.api.User;
import com.example.bbook.api.widgets.AvatarView;
import com.example.bbook.fragments.pages.PasswordRecoverStep1Fragment.OnGoNextListener;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.R.color;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MyProfileFragment extends Fragment {

	View view;
	TextView txAccount,txName;
	AvatarView avatar;
	Button creatStore,myStore;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (view==null) {
			view=inflater.inflate(R.layout.fragment_page_myprofile, null);
			txAccount = (TextView) view.findViewById(R.id.showAccount);
			txName = (TextView) view.findViewById(R.id.showName);
			avatar = (AvatarView) view.findViewById(R.id.avatar);
			creatStore = (Button) view.findViewById(R.id.btn_openStore);
			myStore = (Button) view.findViewById(R.id.btn_myStore);
		}
		
		view.findViewById(R.id.btn_openStore).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent itnt = new Intent(getActivity(), OpenStoreActivity.class);
				startActivity(itnt);
				
			}
		});
		view.findViewById(R.id.btn_myStore).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent itnt = new Intent(getActivity(), MystoreActivity.class);
				startActivity(itnt);
			}
		});
		view.findViewById(R.id.changePassWord).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent itnt = new Intent(getActivity(), ChangePasswordActivity.class);
				startActivity(itnt);
			}
		});
		view.findViewById(R.id.btn_exit).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				exit();
			}
		});
		
		
		return view;
	}
	@Override
	public void onResume() {
		super.onResume();
		creatStore.setVisibility(View.GONE);
		myStore.setVisibility(View.GONE);
		OkHttpClient client = Server.getSharedClient();
		Request request = Server.requestBuilderWithApi("me")
				.method("get", null).build();
		client.newCall(request).enqueue(new Callback() {
			
			@Override
			public void onResponse(final Call arg0, Response arg1) throws IOException {
				// TODO Auto-generated method stub
				final User user = new ObjectMapper().readValue(arg1.body().bytes(), User.class);
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						MyProfileFragment.this.onResponse(arg0,user);
					}
				});
			}
			
			@Override
			public void onFailure(final Call arg0, final IOException arg1) {
				// TODO Auto-generated method stub
				getActivity().runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						MyProfileFragment.this.onFailure(arg0, arg1);
					}
				});
			}
		});
	}
	void onResponse(Call arg0,User user){
		txAccount.setVisibility(View.VISIBLE);
		txName.setVisibility(View.VISIBLE);
		txAccount.setText("用户:"+user.getAccount());
		txName.setText("你好:"+user.getName());
		avatar.load(user);
		if(user.getIsStore().equals("0")){
			creatStore.setVisibility(View.VISIBLE);
		}else myStore.setVisibility(View.VISIBLE);
	}
	void onFailure(Call arg0,Exception ex){
		txAccount.setVisibility(View.VISIBLE);
		txName.setVisibility(View.VISIBLE);
		txAccount.setTextColor(color.holo_red_dark);
		txName.setTextColor(color.holo_red_dark);
		txAccount.setText(ex.getMessage());
		txName.setText(ex.getMessage());
	}
	
	
	void exit(){
		
		OkHttpClient client = Server.getSharedClient();
		Request request = Server.requestBuilderWithApi("exit")
				.build();
		client.newCall(request).enqueue(new Callback() {
			
			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				// TODO Auto-generated method stub
				Intent itnt = new Intent(getActivity(), LoginActivity.class);
				itnt.setFlags(itnt.FLAG_ACTIVITY_CLEAR_TASK | itnt.FLAG_ACTIVITY_NEW_TASK);
				startActivity(itnt);
			}
			
			@Override
			public void onFailure(Call arg0, IOException arg1) {
				// TODO Auto-generated method stub
				
			}
		});
		

	}
}
