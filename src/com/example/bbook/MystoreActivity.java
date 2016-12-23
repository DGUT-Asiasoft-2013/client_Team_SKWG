package com.example.bbook;

import java.io.IOException;

import com.example.bbook.api.Server;
import com.example.bbook.api.Shop;
import com.example.bbook.api.widgets.AvatarView;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.R.color;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MystoreActivity extends Activity {

	AvatarView shopImageUseBg;
	TextView shopName,shopDescription;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_edit_mystore);

		shopName = (TextView) findViewById(R.id.show_stopName);
		shopImageUseBg = (AvatarView) findViewById(R.id.bgshow_img);
		shopDescription = (TextView) findViewById(R.id.show_stopDescription);
		findViewById(R.id.add_goods).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				goAdd();
			}
		});
	}

	protected void goAdd() {
		Intent itnt = new Intent(MystoreActivity.this, AddGoodsActivity.class);
		startActivity(itnt);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		OkHttpClient client = Server.getSharedClient();
		Request request = Server.requestBuilderWithApi("shop/myshop").get().build();
		client.newCall(request).enqueue(new Callback() {
			
			@Override
			public void onResponse(final Call arg0, final Response arg1) throws IOException {
				// TODO Auto-generated method stub
				final Shop shop = new ObjectMapper().readValue(arg1.body().string(), Shop.class);
			
				runOnUiThread(new Runnable() {
					public void run() {
						MystoreActivity.this.onResponse(arg0,shop);
					}
				});
			}
			
			@Override
			public void onFailure(Call arg0, IOException arg1) {
				// TODO Auto-generated method stub
				MystoreActivity.this.onFailure(arg0, arg1);
			}
		});
		
	}
	void onResponse(Call arg0,Shop shop){
		shopName.setText("我的店铺:"+shop.getShopName());
		shopDescription.setText(shop.getDescription());
		shopImageUseBg.load(shop);
	}
	void onFailure(Call arg0,Exception ex){
		shopName.setText(ex.getMessage());
		shopName.setTextColor(color.holo_red_dark);
		shopDescription.setText(ex.getMessage());
		shopDescription.setTextColor(color.holo_red_dark);
	}
}
