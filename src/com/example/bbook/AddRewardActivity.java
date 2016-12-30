package com.example.bbook;

import java.io.IOException;
import java.util.UUID;

import com.example.bbook.api.Article;
import com.example.bbook.api.Server;
import com.example.bbook.api.widgets.AvatarView;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AddRewardActivity extends Activity {
	Article article;
	AvatarView avatar;
	TextView txt_author,txt_other;
	Button reward1,reward2,reward3,reward4,reward5,reward6;
	ImageButton ibtn_back;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.activity_addreward);

		Button reward1=(Button)findViewById(R.id.reward1);
		Button reward2=(Button)findViewById(R.id.reward2);
		Button reward3=(Button)findViewById(R.id.reward3);
		Button reward4=(Button)findViewById(R.id.reward4);
		Button reward5=(Button)findViewById(R.id.reward5);
		Button reward6=(Button)findViewById(R.id.reward6);

		avatar=(AvatarView)findViewById(R.id.avatarView1);
		txt_author=(TextView)findViewById(R.id.textView1);
		txt_other=(TextView)findViewById(R.id.textView2);

		article= (Article)getIntent().getSerializableExtra("data");
		avatar.load(Server.serverAdress+article.getAuthorAvatar());
		txt_author.setText(article.getAuthorName());

		ibtn_back=(ImageButton)findViewById(R.id.btn_back);
		ibtn_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
				overridePendingTransition(0, R.anim.slide_out_left);
			}
		});
		reward1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				double sum=2.00;
				reward(sum);
			}
		});
		reward2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				double sum=5.00;
				reward(sum);
			}
		});
		reward3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				double sum=10.00;
				reward(sum);
			}
		});
		reward4.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				double sum=20.00;
				reward(sum);
			}
		});
		reward5.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				double sum=30.00;
				reward(sum);
			}
		});
		reward6.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				double sum=50.00;
				reward(sum);
			}
		});
		txt_other.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				reward();
			}
		});
	}

	void reward(){
		AlertDialog.Builder builder = new Builder(this);
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		final View view = View.inflate(AddRewardActivity.this, R.layout.dialog_item1, null);

		//把填充得来的view对象设置为对话框显示内容
		builder.setView(view);
		builder.setPositiveButton("赞赏", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				EditText edit_num=(EditText)view.findViewById(R.id.edit_num);
				String str = edit_num.getText().toString();
				//				final double num =Double.valueOf(str);
				double num = 0;
				try{
					num = Double.parseDouble(str);	
				}catch(Exception ex){
					ex.printStackTrace();
				}
				reward(num);
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {

			}
		});
		builder.show();
	}

	void reward(final double sum){
		AlertDialog.Builder builder = new Builder(this);
		builder.setIcon(R.drawable.icon_info_article);
		builder.setTitle("请输入支付密码");
		//把布局文件先填充成View对象
		View view = View.inflate(AddRewardActivity.this, R.layout.dialog_item, null);
		TextView txt_money=(TextView)view.findViewById(R.id.txt_money);
		final EditText edt_pwd=(EditText)view.findViewById(R.id.edit_pwd);
		txt_money.setText("￥"+sum);
		//把填充得来的view对象设置为对话框显示内容
		builder.setView(view);
		builder.setPositiveButton("确认支付", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				String str = edt_pwd.getText().toString();
				goCheckPayPassword(str,sum);
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {

			}
		});
		builder.show();
	}


	//检验支付密码
	public void goCheckPayPassword(String string,double sum){
		String payPassword=string;
		final double money=sum;
		OkHttpClient client=Server.getSharedClient();
		MultipartBody.Builder requestBody=new MultipartBody.Builder()
				.addFormDataPart("payPassword", MD5.getMD5(payPassword));

		//		Log.d("1321321", payPassword);
		Request request=Server.requestBuilderWithApi("payPassword")
				.method("post",null)
				.post(requestBody.build())
				.build();

		client.newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				// TODO Auto-generated method stub
				String responseStr=arg1.body().string();
				Boolean checkPayPassword=new ObjectMapper().readValue(responseStr, Boolean.class);
				if(checkPayPassword){
					goPay(money);
				}else{
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(AddRewardActivity.this,"支付密码不正确", Toast.LENGTH_SHORT).show();
						}
					});
				}

			}

			@Override
			public void onFailure(Call arg0, IOException arg1) {
				// TODO Auto-generated method stub

			}
		});
	}

	//支付打赏
	void goPay(double money){
		UUID uuid=UUID.randomUUID();

		OkHttpClient client=Server.getSharedClient();
		MultipartBody.Builder requestBody=new MultipartBody.Builder()
				.addFormDataPart("money", money+"")
				.addFormDataPart("uuid", uuid.toString());

				Request request=Server.requestBuilderWithApi("article/reward")
						.method("post",null)
						.post(requestBody.build())
						.build();

				client.newCall(request).enqueue(new Callback() {

					@Override
					public void onResponse(Call arg0, Response arg1) throws IOException {
						String responseStr=arg1.body().string();
						final Boolean checkPayState=new ObjectMapper().readValue(responseStr, Boolean.class);
						//				Log.d("aaasss",  responseStr);
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if(checkPayState){
									Toast.makeText(AddRewardActivity.this,"打赏成功", Toast.LENGTH_SHORT).show();
								}else{
									Toast.makeText(AddRewardActivity.this,"打赏失败,余额不足", Toast.LENGTH_SHORT).show();
								}
							}
						});

					}

					@Override
					public void onFailure(Call arg0, IOException arg1) {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								Toast.makeText(AddRewardActivity.this,"打赏失败，余额不足", Toast.LENGTH_SHORT).show();
							}
						});
					}
				});
	}
}
