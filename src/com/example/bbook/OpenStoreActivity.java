package com.example.bbook;

import java.io.IOException;

import com.example.bbook.api.Server;
import com.example.bbook.api.Shop;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;
import inputcells.PictureInputCellFragment;
import inputcells.SimpleTextInputcellFragment;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OpenStoreActivity extends Activity {
	int isCreate = 0;

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

		findViewById(R.id.createstore).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				createStore();
			}
		});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		fragInputStoreName.setLabelText("店名");
		fragInputStoreName.setHintText("为你的新店起个名字吧");
		fragInputStoreName.setBackGround(true);
		fragStoreImg.setLabelText("店铺头像");
		fragStoreImg.setHintText("选择图片");
	}

	void createStore() {
		String shopName = fragInputStoreName.getText();
		String description = storeIntroduce.getText().toString();
		if (shopName.isEmpty()) {
			Toast.makeText(OpenStoreActivity.this, "店名不能为空", Toast.LENGTH_LONG).show();
			return;
		}
		if(description.isEmpty()){
			Toast.makeText(OpenStoreActivity.this, "总要写点什么介绍一下吧", Toast.LENGTH_LONG).show();
			return;
		}
		OkHttpClient client = Server.getSharedClient();
		MultipartBody.Builder body = new MultipartBody.Builder().addFormDataPart("shopName", shopName)
				.addFormDataPart("description", description);

		
		if (fragStoreImg.getPngData() != null) {
			body.addFormDataPart("shopImage", "shopImage",
					RequestBody.create(MediaType.parse("image/png"), fragStoreImg.getPngData()));
		} else {
			Toast.makeText(OpenStoreActivity.this, "头像不能为空", Toast.LENGTH_LONG).show();
			return;
		}

		Request request = Server.requestBuilderWithApi("openshop").method("post", null).post(body.build()).build();
		client.newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(final Call arg0, final Response arg1) throws IOException {
				try {
					final String responseBody = arg1.body().string();
					Log.d("open store result", responseBody);

					final Shop shop = new ObjectMapper().readValue(responseBody, Shop.class);
					if (!shop.getDescription().equals(storeIntroduce.getText().toString())) {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								OpenStoreActivity.this.onFailure(arg0, shop.getDescription());
							}
						});
					} else{

						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								OpenStoreActivity.this.onResponse(arg0, shop.getOwner().getAccount());
							}
						});
					}
				}
				catch (final Exception e) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							OpenStoreActivity.this.onFailure(arg0, e);
						}
					});
				}
				
				
			}
				

			@Override
			public void onFailure(final Call arg0, final IOException arg1) {

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						OpenStoreActivity.this.onFailure(arg0, arg1);
					}
				});
			}
		});
	}

	void onResponse(Call arg0, String response) {
		new AlertDialog.Builder(this).setTitle("开店成功!").setMessage(response + ",恭喜您成功的开了一家新店!祝您财源滚进!")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

						isCreate = 1;
						becomeSeller();
					}
				}).show();
	}

	void onFailure(Call arg0, final Exception e1) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				new AlertDialog.Builder(OpenStoreActivity.this).setTitle("开店失败").setMessage(e1.getMessage())
						.setNegativeButton("OK", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								finish();
							}
						}).show();
			}
		});
	}

	void onFailure(Call arg0, final String response) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				new AlertDialog.Builder(OpenStoreActivity.this).setTitle("开店失败").setMessage(response)
						.setNegativeButton("OK", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								finish();
							}
						}).show();
			}
		});
	}

	void becomeSeller() {
		OkHttpClient client = Server.getSharedClient();
		if (isCreate == 1) {
			Request request = Server.requestBuilderWithApi("becomeshop").method("post", null).build();
			client.newCall(request).enqueue(new Callback() {

				@Override
				public void onResponse(Call arg0, Response arg1) throws IOException {
					finish();
				}

				@Override
				public void onFailure(Call arg0, IOException arg1) {

				}
			});

		}
	}
}
