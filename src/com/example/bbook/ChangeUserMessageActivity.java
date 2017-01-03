package com.example.bbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.example.bbook.api.Server;
import com.example.bbook.api.User;
import com.example.bbook.api.widgets.AvatarView;
import com.example.bbook.api.widgets.ChangeItemFragment;
import com.example.bbook.api.widgets.TitleBarFragment;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChangeUserMessageActivity extends Activity {
	private static final int REQUESTCODE_ALBUM = 1;
	private static final int REQUESTCODE_CAMERA = 2;
	byte[] pngData;
	ChangeItemFragment changeName,changeEmail,changeAddress,changePhone;
	AvatarView avatar;
	TitleBarFragment fragTitleBar;
	TextView txtake,txpick,txcancel;
	private PopupWindow P;
	float textSize = 16;
	int color = Color.GRAY;
	private String value = null;
	private Context mycontext = null;
	View parentView=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		parentView=View.inflate(this, R.layout.activity_change_usermessage,null);
		setContentView(parentView);
		FragmentManager fg=getFragmentManager();
		changeName=(ChangeItemFragment) fg.findFragmentById(R.id.change_Name);
		changeEmail=(ChangeItemFragment) fg.findFragmentById(R.id.change_Email);
		changeAddress=(ChangeItemFragment) fg.findFragmentById(R.id.change_Address);
		changePhone=(ChangeItemFragment) fg.findFragmentById(R.id.change_Phone);
		avatar=(AvatarView) findViewById(R.id.avatar);
		
		
		fragTitleBar = (TitleBarFragment) fg.findFragmentById(R.id.changeUserMessage_titlebar);
		fragTitleBar.setBtnNextState(false);
		fragTitleBar.setTitleName("我的资料", textSize);
		fragTitleBar.setSplitLineState(false);
		fragTitleBar.setOnGoBackListener(new TitleBarFragment.OnGoBackListener() {
			
			@Override
			public void onGoBack() {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		//更改昵称
		findViewById(R.id.change_Name).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String value = changeName.getText();
				String change = "changeName";
				goChange(change,value);
			}
		});
		//更改邮箱
		findViewById(R.id.change_Email).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String value = changeEmail.getText();
				String change = "changeEmail";
				goChange(change,value);
			}
		});
		//更改电话号码
		findViewById(R.id.change_Phone).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String value = changeEmail.getText();
				String change = "changePhone";
				goChange(change,value);
			}
		});
		//更改地址
		findViewById(R.id.change_Address).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String change = "changeAddress";
				String value = changeAddress.getText();
				goChange(change,value);
			}
		});
		
		//修改头像(未完成)
		findViewById(R.id.chang_avatar).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showWindow();
			}
		});
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		changeName.setmessageTitleText("昵称:", textSize);
		changeEmail.setmessageTitleText("邮箱:", textSize);
		changeAddress.setmessageTitleText("地址:", textSize);
		changePhone.setmessageTitleText("手机号码:", textSize);
		
		OkHttpClient client = Server.getSharedClient();
		Request request = Server.requestBuilderWithApi("me").method("get", null).build();
		
		client.newCall(request).enqueue(new Callback() {
			
			@Override
			public void onResponse(final Call arg0, Response arg1) throws IOException {
				// TODO Auto-generated method stub
				final User user = new ObjectMapper().readValue(arg1.body().bytes(), User.class);
				runOnUiThread(new Runnable() {
					public void run() {
						ChangeUserMessageActivity.this.onResponse(arg0,user);
					}
				});
//				changeName.setmessageEditText(user.getName(), textSize, color);
//				changeEmail.setmessageEditText(user.getEmail(), textSize, color);
//				changeAddress.setmessageEditText(user.getAddress(), textSize, color);
//				changePhone.setmessageEditText(user.getPhoneNum(), textSize, color);
			}
			
			@Override
			public void onFailure(Call arg0, IOException arg1) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	void onResponse(Call arg0 , User user){
		avatar.load(user);
		changeName.setmessageEditText(user.getName(), textSize, color);
		changeEmail.setmessageEditText(user.getEmail(), textSize, color);
		changeAddress.setmessageEditText(user.getAddress(), textSize, color);
		changePhone.setmessageEditText(user.getPhoneNum(), textSize, color);
	}
	
	//设置POP弹窗
	void showWindow(){
		mycontext = this;
		View myview = View.inflate(mycontext, R.layout.popupwindows_change_avatar, null);
		txtake = (TextView) myview.findViewById(R.id.change_useravatar_takephoto);
		txpick = (TextView) myview.findViewById(R.id.change_useravatar_pick);
		txcancel = (TextView) myview.findViewById(R.id.chang_useravatar_cancel);
		P = new PopupWindow(myview);
		P.setBackgroundDrawable(new ColorDrawable(0));
		P.setWidth(LayoutParams.MATCH_PARENT);           
		P.setHeight(LayoutParams.WRAP_CONTENT);
		P.setFocusable(true);
		P.setAnimationStyle(R.style.anim_menu_bottombar);
		P.setOutsideTouchable(true);
		P.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss() {
				backgroundAlpha(1);
			}
		});
		P.showAtLocation(parentView,Gravity.BOTTOM, 0, 0);
		backgroundAlpha(0.5f);
		
		//拍照按钮监听事件
		txtake.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				takePhoto();
			}
		});
		//从相册选择按钮监听事件
		txpick.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				pickFromAlbum();
			}
		});
		//取消按钮
		txcancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				P.dismiss();
			}
		});
	}
	
	/** 
     * 设置添加屏幕的背景透明度 
     */  
    public void backgroundAlpha(float bgAlpha)  
    {  
        WindowManager.LayoutParams lp = getWindow().getAttributes();  
        lp.alpha = bgAlpha; //0.0-1.0  
        getWindow().setAttributes(lp);  
    }  
    
    
    
	void pickFrmCamera() {
		Intent itnt = new Intent(Intent.ACTION_GET_CONTENT);
		itnt.setType("image/*");
		startActivityForResult(itnt, REQUESTCODE_ALBUM);
	}
	
	void takePhoto() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(intent, REQUESTCODE_CAMERA);
	}
	
	void pickFromAlbum() {
		Intent itnt = new Intent(Intent.ACTION_GET_CONTENT);
		itnt.setType("image/*");
		startActivityForResult(itnt, REQUESTCODE_ALBUM);
	}
	
	void saveBitmap(Bitmap bmp) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bmp.compress(CompressFormat.PNG, 100, baos);
		pngData = baos.toByteArray();
		upload(pngData);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
//		if (resultCode == Activity.RESULT_CANCELED)
//			return;

		if (requestCode == REQUESTCODE_CAMERA) {
			Bitmap bmp = (Bitmap) data.getExtras().get("data");
			saveBitmap(bmp);
//			imageView.setImageBitmap(bmp);
		} else if (requestCode == REQUESTCODE_ALBUM) {
			try {
				Bitmap bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
				saveBitmap(bmp);
//				imageView.setImageBitmap(bmp);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	void upload(byte[] pngData){
		if(pngData!=null){
			OkHttpClient client = Server.getSharedClient();
			MultipartBody.Builder requestbody = new MultipartBody.Builder();
			requestbody.addFormDataPart("avatar", "avatar",
					RequestBody.create(MediaType.parse("image/png"), pngData));
			Request request = Server.requestBuilderWithApi("changeAvatar")
					.method("post", null).post(requestbody.build()).build();
			client.newCall(request).enqueue(new Callback() {

				@Override
				public void onResponse(final Call arg0, final Response arg1) throws IOException {
					final String responseString;
					try {
						responseString = arg1.body().string();
						runOnUiThread(new Runnable() {
							@Override

							public void run() {
								ChangeUserMessageActivity.this.onResponse(arg0, responseString);

							}
						});
					} catch (final Exception e1) {
						e1.printStackTrace();
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								ChangeUserMessageActivity.this.onFailure(arg0, e1);

							}
						});
					}
				}

				@Override
				public void onFailure(final Call arg0, final IOException arg1) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							ChangeUserMessageActivity.this.onFailure(arg0, arg1);
						}
					});
				}
			});
		}
	}
	
	protected void onResponse(Call arg0, String responseBody) {
		new AlertDialog.Builder(this).setTitle("修改成功")
		.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		}).show();

	}

	protected void onFailure(Call arg0, Exception arg1) {
		new AlertDialog.Builder(this).setTitle("修改失败")
		.setNegativeButton("确定", null).show();
	}
	
	
	//自定义一个带value的监听器
    void goChange(String change,String value){
    	Intent itnt = new Intent(this, ChangeMessageActivity.class);
    	itnt.putExtra("type", change);
    	itnt.putExtra("value", value);
		startActivity(itnt);
    }
}
