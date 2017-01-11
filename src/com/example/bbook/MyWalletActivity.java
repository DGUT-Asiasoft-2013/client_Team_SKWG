package com.example.bbook;

import java.io.IOException;
import java.util.UUID;

import com.example.bbook.api.Server;
import com.example.bbook.api.widgets.TitleBarFragment;
import com.example.bbook.api.widgets.TitleBarFragment.OnGoBackListener;
import com.example.bbook.api.widgets.TitleBarFragment.OnGoNextListener;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.Response;

public class MyWalletActivity extends Activity {
	TitleBarFragment titleBar;
	Button btnCharge;
	TextView tvRemain; // 余额
	String chargeCount;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_wallet);
		
		findViewById(R.id.charge).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				goCharge();
			}
		});
		tvRemain = (TextView) findViewById(R.id.remain);
		titleBar = (TitleBarFragment) getFragmentManager().findFragmentById(R.id.title_bar);
		titleBar.setTitleName("我的钱包", 18);
		titleBar.setBtnNextText("消费记录", 13);
		// 返回
		titleBar.setOnGoBackListener(new OnGoBackListener() {

			@Override
			public void onGoBack() {
				finish();
				overridePendingTransition(R.anim.none, R.anim.slide_out_right);
			}
		});
		titleBar.setOnGoNextListener(new OnGoNextListener() {
			
			@Override
			public void onGoNext() {
				Intent itnt = new Intent(MyWalletActivity.this, MyBillActivity.class);
				startActivity(itnt);
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		reload();
	}
	
	private void reload() {
		Request request = Server.requestBuilderWithApi("user/mywallet/getremain").get().build();
		Server.getSharedClient().newCall(request).enqueue(new Callback() {
			
			@Override
			public void onResponse(Call arg0, final Response arg1) throws IOException {
				final String body = arg1.body().string();
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						try {
							Double remainMoney = new ObjectMapper().readValue(body, Double.class);
							MyWalletActivity.this.setRemain(remainMoney);
						} catch (JsonParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (JsonMappingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
			}
			
			@Override
			public void onFailure(Call arg0, IOException arg1) {
				// TODO Auto-generated method stub
				
			}
		});
	}

	protected void setRemain(Double remainMoney) {
		tvRemain.setText("￥" + remainMoney);
	}

	protected void goCharge() {
		
		AlertDialog.Builder builder=new Builder(this);
		builder.setTitle("请输入充值金额");
		//把布局文件先填充成View对象
		View view = View.inflate(MyWalletActivity.this, R.layout.dialog_number, null);
		final EditText tvChargeCount=(EditText)view.findViewById(R.id.edit_pwd);
		//把填充得来的view对象设置为对话框显示内容
		builder.setView(view);
		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				chargeCount= tvChargeCount.getText().toString();
//				goPay();
				onGoCharge(chargeCount);
//				setBill(1, )
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

			}
		});
		builder.show();

	}

	void setBill(int state, String item, String detail) {
		MultipartBody.Builder body = new MultipartBody.Builder()
				.addFormDataPart("item", item)
				.addFormDataPart("detial", detail)
				.addFormDataPart("state", state + "");
		Request request = Server.requestBuilderWithApi("addIncome")
				.method("post", null)
				.post(body.build())
				.build();
		Server.getSharedClient().newCall(request).enqueue(new Callback() {
			
			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				
			}
			
			@Override
			public void onFailure(Call arg0, IOException arg1) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	protected void onGoCharge(String chargeCount) {
		UUID uuid = UUID.randomUUID();
		MultipartBody.Builder body = new MultipartBody.Builder()
				.addFormDataPart("charge_num", chargeCount)
				.addFormDataPart("uuid", uuid.toString());
		Request request = Server.requestBuilderWithApi("user/mywallet/charge").method("get", null).post(body.build()).build();
		Server.getSharedClient().newCall(request).enqueue(new Callback() {
			
			@Override
			public void onResponse(Call arg0, final Response arg1) throws IOException {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						double remain;
						try {
							remain = new ObjectMapper().readValue(arg1.body().string(), Double.class);
							MyWalletActivity.this.setRemain(remain);
						} catch (JsonParseException e) {
							e.printStackTrace();
						} catch (JsonMappingException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
						
					}
				});
			}
			
			@Override
			public void onFailure(Call arg0, IOException arg1) {
				// TODO Auto-generated method stub
				
			}
		});
	}

	protected void goPay() {
		// TODO Auto-generated method stub
		
	}
}
