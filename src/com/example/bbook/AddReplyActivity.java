package com.example.bbook;

import java.io.IOException;

import com.example.bbook.api.Article;
import com.example.bbook.api.Comment;
import com.example.bbook.api.Server;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AddReplyActivity extends Activity {
	EditText editText;
	Article article;
	Comment comment;
	ImageButton ibtn_back,ibtn_next;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addreply);
		editText=(EditText)findViewById(R.id.editText1);
		ibtn_back=(ImageButton)findViewById(R.id.btn_back);
		ibtn_next=(ImageButton)findViewById(R.id.btn_next);
		
		article= (Article)getIntent().getSerializableExtra("data");
		comment= (Comment)getIntent().getSerializableExtra("comment");
		String content="//@"+comment.getAuthorName()+":"+comment.getText();
		editText.setText(content);
		
		ibtn_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
				overridePendingTransition(0, R.anim.slide_out_right);
			}
		});
		ibtn_next.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				addreply();
			}
		});
		
	}

	void addreply(){
		String text = editText.getText().toString();

		OkHttpClient client = Server.getSharedClient();

		MultipartBody requestBuilder = new MultipartBody.Builder()
				.addFormDataPart("text", text)
				.build();

		Request request = Server.requestBuilderWithApi("article/"+article.getId()+"/comments")
				.method("post", null)
				.post(requestBuilder)
				.build();

		client.newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(final Call arg0, final Response arg1) throws IOException {
				try{	     
					final String responString = arg1.body().toString();
					runOnUiThread(new Runnable() {
						public void run() {
							AddReplyActivity.this.onResponse(arg0,responString);
						}
					});
				}catch (final Exception e) {
					runOnUiThread(new Runnable() {
						public void run() {
							AddReplyActivity.this.onFailure(arg0, e);
						}
					});
				}
			}

			@Override
			public void onFailure(final Call arg0, final IOException arg1) {
				runOnUiThread(new Runnable() {
					public void run() {
						AddReplyActivity.this.onFailure(arg0, arg1);
					}
				});

			}
		});
	}

	void onResponse(Call arg0, String responseBody) {
		new AlertDialog.Builder(this)
		.setTitle("回复成功")
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
				overridePendingTransition(0,R.anim.slide_out_right);

			}
		})
		.show();
	}

	void onFailure(Call arg0, Exception arg1) {
//		new AlertDialog.Builder(this)
//		.setTitle("回复失败")
//		.setMessage(arg1.getLocalizedMessage())
//		.setNegativeButton("OK", null)
//		.show();
	}
	
}
