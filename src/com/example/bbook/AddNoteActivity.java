package com.example.bbook;
import java.io.IOException;

import com.example.bbook.R;
import com.example.bbook.api.Server;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import inputcells.PictureInputCellFragment;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddNoteActivity extends Activity {
	PictureInputCellFragment fragArticlesImage;

	EditText articleTitle;
	EditText articleText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addnote);
		articleTitle= (EditText)findViewById(R.id.editText1);
		articleText= (EditText)findViewById(R.id.editText2);
		fragArticlesImage = (PictureInputCellFragment) getFragmentManager().findFragmentById(R.id.input_articles_image);
		
		findViewById(R.id.btn_return).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		findViewById(R.id.btn_addnote).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				sendNote();
			}
		});
	}


	void sendNote(){

		String title = articleTitle.getText().toString();
		String text = articleText.getText().toString();

		OkHttpClient client = Server.getSharedClient();

		MultipartBody.Builder requestBuilder = new MultipartBody.Builder()
				.addFormDataPart("title", title)
				.addFormDataPart("text", text);

		if(fragArticlesImage.getPngData() != null) {
			requestBuilder.addFormDataPart("articlesImage", "articlesImage",
					RequestBody.create(MediaType.parse("image/png"), fragArticlesImage.getPngData()));
		}

		Request request = Server.requestBuilderWithApi("article")
				.method("post", null)
				.post(requestBuilder.build())
				.build();

		client.newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(final Call arg0, final Response arg1) throws IOException {
				try{	     
					final String responString = arg1.body().toString();
					runOnUiThread(new Runnable() {
						public void run() {
							AddNoteActivity.this.onResponse(arg0,responString);
						}
					});
				}catch (final Exception e) {
					runOnUiThread(new Runnable() {
						public void run() {
							AddNoteActivity.this.onFailure(arg0, e);
						}
					});
				}
			}

			@Override
			public void onFailure(final Call arg0, final IOException arg1) {
				runOnUiThread(new Runnable() {
					public void run() {
						AddNoteActivity.this.onFailure(arg0, arg1);
					}
				});
			}
		});

	}

	void onResponse(Call arg0, String responseBody) {
		new AlertDialog.Builder(this)
		.setTitle("发表成功")
		.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		})
		.show();
	}

	void onFailure(Call arg0, Exception arg1) {
		new AlertDialog.Builder(this)
		.setTitle("发表失败")
		.setMessage(arg1.getLocalizedMessage())
		.setNegativeButton("OK", null)
		.show();
	}

}
