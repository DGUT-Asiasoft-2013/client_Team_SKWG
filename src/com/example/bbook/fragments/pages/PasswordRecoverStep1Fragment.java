package com.example.bbook.fragments.pages;


import com.example.bbook.R;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import inputcells.SimpleTextInputcellFragment;

public class PasswordRecoverStep1Fragment extends Fragment{
	View view;
	SimpleTextInputcellFragment fragEmail;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if(view == null) {
			view = inflater.inflate(R.layout.fragment_password_recover_step1, null);
			
			fragEmail = (SimpleTextInputcellFragment) getFragmentManager().findFragmentById(R.id.input_email);
			
			view.findViewById(R.id.btn_next).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					goNext();
				}
			});
			
		}
		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		fragEmail.setLabelText("用户邮箱");
		fragEmail.setHintText("请输入注册时输入的邮箱");
	}
	
	public static interface OnGoNextListener{
		void onGoNext();
	}
	
	OnGoNextListener onGoNextListener;
		
		public void setOnGoNextListener(OnGoNextListener onGoNextListener) {
			this.onGoNextListener = onGoNextListener;
	}
		
	public String getText() {
		return fragEmail.getText();
	}
		
	void goNext(){
		if(onGoNextListener!=null){
			onGoNextListener.onGoNext();
		}
	}
	
	
}
