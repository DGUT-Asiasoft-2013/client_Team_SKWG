package com.example.bbook;

import java.io.IOException;

import com.example.bbook.api.Article;
import com.example.bbook.api.Server;
import com.example.bbook.api.widgets.AvatarView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ModifyArticleActivity extends Activity {
	Article article;
	TextView modify_title,modify_text;
	Button btn_delete;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modifyarticle);

		modify_title=(TextView)findViewById(R.id.editText1);
		modify_text=(TextView)findViewById(R.id.editText2);

		btn_delete=(Button)findViewById(R.id.btn_delete);

		String title = getIntent().getStringExtra("Title");
		String text = getIntent().getStringExtra("Text");
		article =(Article)getIntent().getSerializableExtra("Data");

		modify_title.setText(title);
		modify_text.setText(text);

		btn_delete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				delete();
			}
		});

		findViewById(R.id.img_return).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();

			}
		});

		findViewById(R.id.img_send).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				modify();
			}
		});
	}

	//删除包含评论、赞的文章
	void delete(){
		deleteLike();
		deleteComment();
		deleteArticle();
	}

	//删除评论
	void deleteComment(){
		Request request = Server.requestBuilderWithApi("article/"+article.getId()+"/deletecomment")
				.method("delete", null)
				.build();

		Server.getSharedClient().newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(final Call arg0, Response arg1) throws IOException {}

			@Override
			public void onFailure(final Call arg0, final IOException arg1) {}
		});
	}

	//删除赞
	void deleteLike(){
		Request request = Server.requestBuilderWithApi("article/"+article.getId()+"/deletelike")
				.method("delete", null)
				.build();

		Server.getSharedClient().newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(final Call arg0, Response arg1) throws IOException {}

			@Override
			public void onFailure(final Call arg0, final IOException arg1) {}
		});
	}

	//删除帖子
	void deleteArticle(){
		Request request = Server.requestBuilderWithApi("article/"+article.getId()+"/delete")
				.method("delete", null)
				.build();

		Server.getSharedClient().newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(final Call arg0, Response arg1) throws IOException {
				try{	     
					final String responString = arg1.body().toString();
					runOnUiThread(new Runnable() {
						public void run() {
							ModifyArticleActivity.this.onResponsed(arg0,responString);
						}
					});
				}catch (final Exception e) {
					runOnUiThread(new Runnable() {
						public void run() {
							ModifyArticleActivity.this.onFailured(arg0, e);
						}
					});
				}

			}

			@Override
			public void onFailure(final Call arg0, final IOException arg1) {
				runOnUiThread(new Runnable() {
					public void run() {
						ModifyArticleActivity.this.onFailure(arg0, arg1);
					}
				});
			}
		});

	}

	//修改帖子内容
	void modify(){

		String title = modify_title.getText().toString();
		String text = modify_text.getText().toString();

		OkHttpClient client = Server.getSharedClient();

		MultipartBody requestBuilder = new MultipartBody.Builder()
				.addFormDataPart("title", title)
				.addFormDataPart("text", text)
				.build();

		Request request = Server.requestBuilderWithApi("modify/"+article.getId())
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
							ModifyArticleActivity.this.onResponse(arg0,responString);
						}
					});
				}catch (final Exception e) {
					runOnUiThread(new Runnable() {
						public void run() {
							ModifyArticleActivity.this.onFailure(arg0, e);
						}
					});
				}
			}

			@Override
			public void onFailure(final Call arg0, final IOException arg1) {
				runOnUiThread(new Runnable() {
					public void run() {
						ModifyArticleActivity.this.onFailure(arg0, arg1);
					}
				});
			}
		});

	}

	void onResponse(Call arg0, String responseBody) {
		new AlertDialog.Builder(this)
		.setTitle("修改成功")
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
		.setTitle("修改失败")
		.setMessage(arg1.getLocalizedMessage())
		.setNegativeButton("OK", null)
		.show();
	}
	void onResponsed(Call arg0, String responseBody) {
		new AlertDialog.Builder(this)
		.setTitle("删除成功")
		.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		})
		.show();
	}

	void onFailured(Call arg0, Exception arg1) {
		new AlertDialog.Builder(this)
		.setTitle("删除失败")
		.setMessage(arg1.getLocalizedMessage())
		.setNegativeButton("OK", null)
		.show();
	}
}
