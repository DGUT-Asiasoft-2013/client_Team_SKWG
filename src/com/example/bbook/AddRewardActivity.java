package com.example.bbook;

import com.example.bbook.api.Article;
import com.example.bbook.api.Server;
import com.example.bbook.api.widgets.AvatarView;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class AddRewardActivity extends Activity {
	Article article;
	AvatarView avatar;
	TextView txt_author,txt_other;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.activity_addreward);

		avatar=(AvatarView)findViewById(R.id.avatarView1);
		txt_author=(TextView)findViewById(R.id.textView1);
		txt_other=(TextView)findViewById(R.id.textView2);

		article= (Article)getIntent().getSerializableExtra("data");
		avatar.load(Server.serverAdress+article.getAuthorAvatar());
		txt_author.setText(article.getAuthorName());

		findViewById(R.id.img_return).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
				overridePendingTransition(0, R.anim.slide_out_left);
			}
		});
		findViewById(R.id.reward1).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				reward();
			}
		});
		findViewById(R.id.reward2).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				reward();
			}
		});
		findViewById(R.id.reward3).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				reward();
			}
		});
		findViewById(R.id.reward4).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				reward();
			}
		});
		findViewById(R.id.reward5).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				reward();
			}
		});
		findViewById(R.id.reward6).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				reward();
			}
		});
		txt_other.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

			}
		});
	}
	void reward(){

	}
}
