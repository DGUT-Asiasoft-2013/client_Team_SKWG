package com.example.bbook;

import com.example.bbook.api.Server;
import com.example.bbook.api.widgets.RectangleView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class BrowseImgActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_browseimg);
		RectangleView img1=(RectangleView)findViewById(R.id.img1);
		ImageButton back=(ImageButton)findViewById(R.id.btn_back);
		String Img = getIntent().getStringExtra("Img");
//		String[] articleImg = Img.split("\\|");
		img1.load(Server.serverAdress+Img);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
				overridePendingTransition(0,R.anim.slide_out_left);
			}
		});
	}
}
