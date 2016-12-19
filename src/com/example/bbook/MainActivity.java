package com.example.bbook;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_firstview);
		
		findViewById(R.id.btn_login).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				goLogin();
			}
		});
	}
	
	void goLogin(){
		Intent itnt = new Intent(this, LoginActivity.class);
		startActivity(itnt);
	}
	
}
