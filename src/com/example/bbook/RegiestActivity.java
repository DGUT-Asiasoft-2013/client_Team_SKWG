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
		
		fragAccount.setLabelText("�û���:");
		fragAccount.setHintText("�������û���");
		fragPassword.setLabelText("����:");
		fragPassword.setHintText("����������");
		fragPassword.setEditText(true);
		fragRepeatPassword.setLabelText("�ظ�����:");
		fragRepeatPassword.setHintText("���ظ���������");
		fragRepeatPassword.setEditText(true);
		fragEmail.setLabelText("����:");
		fragEmail.setHintText("����������");
		fragAddress.setLabelText("��ַ:");
		fragAddress.setHintText("���������ĵ�ַ");
		fragPhoneNum.setLabelText("��ϵ�绰:");
		fragPhoneNum.setHintText("������������ϵ�绰");

	}
}
