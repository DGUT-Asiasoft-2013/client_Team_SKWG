package com.example.bbook;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class AddBookDetailActivity extends Activity {
	EditText edt_detail;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addbookdetail);
		edt_detail=(EditText)findViewById(R.id.editText1);
		String bookdetail=(String)getIntent().getStringExtra("bookdetail");
		edt_detail.setText(bookdetail);  //初始赋值
		
		findViewById(R.id.btn_back).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
				overridePendingTransition(0, R.anim.slide_out_left);
			}
		});
		
		findViewById(R.id.btn_next).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				back();
			}
		});
	}
	
	void back(){                               //跳回发布商品页面
		String goodsDetail=edt_detail.getText().toString();         //宝贝描述
		if((goodsDetail==null||goodsDetail.length()<=0)){             //宝贝描述为空
        	new AlertDialog.Builder(this)
        	.setTitle("温馨提示")
        	.setMessage("输入不能为空，取消编辑请左上角返回")
        	.setPositiveButton("好的", null)
        	.show();
        	return;
        }
        Intent intent=new Intent();  
        intent.putExtra("goodsDetail", goodsDetail);  
        setResult(RESULT_OK, intent);  
        finish();  
	}
}
