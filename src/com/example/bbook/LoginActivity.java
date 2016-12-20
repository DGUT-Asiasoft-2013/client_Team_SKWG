package com.example.bbook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class LoginActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		findViewById(R.id.btn_log).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				goHello();
			}
		});
	}
	void goHello(){
		Intent itnt = new Intent(this, HelloWorldActivity.class);
		startActivity(itnt);
	}
}
