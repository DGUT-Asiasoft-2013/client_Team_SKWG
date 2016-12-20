package com.example.bbook;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.widget.EditText;
import inputcells.PictureInputCellFragment;
import inputcells.SimpleTextInputcellFragment;

public class OpenStoreActivity extends Activity {
	SimpleTextInputcellFragment fragInputStoreName;
	PictureInputCellFragment fragStoreImg;
	EditText storeIntroduce;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_openstore);
		
		fragInputStoreName = (SimpleTextInputcellFragment) getFragmentManager().findFragmentById(R.id.input_storename);
		fragStoreImg = (PictureInputCellFragment) getFragmentManager().findFragmentById(R.id.input_storeimg);
		storeIntroduce = (EditText) findViewById(R.id.input_storeintroduce);
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		fragInputStoreName.setLabelText("店名");
		fragInputStoreName.setHintText("为你的新店起个名字吧");
		fragStoreImg.setLabelText("店铺头像");
		fragStoreImg.setHintText("选择图片");
	}
}
