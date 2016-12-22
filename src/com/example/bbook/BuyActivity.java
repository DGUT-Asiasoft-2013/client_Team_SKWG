package com.example.bbook;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class BuyActivity extends Activity {
	EditText goodsCount;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_buy);
		goodsCount.findViewById(R.id.count);
		findViewById(R.id.submit).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				confirmOrder();
			}
		});
	}

	protected void confirmOrder() {
		
	}
}
