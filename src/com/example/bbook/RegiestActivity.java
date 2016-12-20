package com.example.bbook;

import android.app.Activity;
import android.os.Bundle;
import inputcells.PictureInputCellFragment;
import inputcells.SimpleTextInputcellFragment;

public class RegiestActivity extends Activity {
	SimpleTextInputcellFragment fragAccount,fragPassword,fragEmail,fragAddress,fragPhoneNum,fragRepeatPassword;
	PictureInputCellFragment fragImg;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_regist);
		
		fragAccount = (SimpleTextInputcellFragment) getFragmentManager().findFragmentById(R.id.input_account);
		fragPassword = (SimpleTextInputcellFragment) getFragmentManager().findFragmentById(R.id.input_password);
		fragRepeatPassword = (SimpleTextInputcellFragment) getFragmentManager().findFragmentById(R.id.input_repeat_password);
		fragEmail = (SimpleTextInputcellFragment) getFragmentManager().findFragmentById(R.id.input_email);
		fragAddress = (SimpleTextInputcellFragment) getFragmentManager().findFragmentById(R.id.input_address);
		fragPhoneNum = (SimpleTextInputcellFragment) getFragmentManager().findFragmentById(R.id.input_phoneNum);
		
		fragImg = (PictureInputCellFragment) getFragmentManager().findFragmentById(R.id.input_img);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		fragAccount.setLabelText("用户名:");
		fragAccount.setHintText("请输入用户名");
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

	}
}
