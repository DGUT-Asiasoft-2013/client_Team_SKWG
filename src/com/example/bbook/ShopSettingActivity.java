package com.example.bbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.example.bbook.R;
import com.example.bbook.api.Server;
import com.example.bbook.api.Shop;
import com.example.bbook.api.User;
import com.example.bbook.api.widgets.ChangeItemFragment;
import com.example.bbook.api.widgets.GoodsPicture;
import com.example.bbook.api.widgets.TitleBarFragment;
import com.example.bbook.api.widgets.TitleBarFragment.OnGoBackListener;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow.OnDismissListener;
import inputcells.SimpleTextInputcellFragment;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ShopSettingActivity extends Activity {
	private static final int REQUESTCODE_ALBUM = 1;
	private static final int REQUESTCODE_CAMERA = 2;
	byte[] pngData;
	private PopupWindow P;
	private Context mycontext = null;
	View parentView=null;
	TextView txtake,txpick,txcancel;
	TitleBarFragment fragTitleBar;
	ChangeItemFragment fragShopName, fragShopDescription;
	GoodsPicture imgShop;
	float textSize = 16;
	int color = Color.GRAY;
	Shop shop;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		parentView=View.inflate(this, R.layout.activity_shop_setting,null);
		setContentView(parentView);
		init();
		setEvent();
		shop = (Shop) getIntent().getSerializableExtra("shop");

		// 更改店名
		findViewById(R.id.edit_shop_name).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String value = fragShopName.getText();
				String change = "changeShopName";
				goChange(change, value);
			}
		});
		
		
		// 更改店铺详情
		findViewById(R.id.edit_shop_description).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String value = fragShopDescription.getText();
				String change = "changeShopDescription";
				goChange(change, value);
			}
		});
		
		
		//修改头像
				findViewById(R.id.chang_shop_avatar).setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						showWindow();
					}
				});
	}

	// 设置TitleTar
	private void setEvent() {
		fragTitleBar.setTitleName("店铺设置", 16);
		fragTitleBar.setOnGoBackListener(new OnGoBackListener() {

			@Override
			public void onGoBack() {
				finish();
			}
		});
		fragTitleBar.setBtnNextState(false);
	}

	private void init() {
		fragTitleBar = (TitleBarFragment) getFragmentManager().findFragmentById(R.id.title_bar);
		fragShopName = (ChangeItemFragment) getFragmentManager().findFragmentById(R.id.edit_shop_name);
		fragShopDescription = (ChangeItemFragment) getFragmentManager().findFragmentById(R.id.edit_shop_description);
		imgShop = (GoodsPicture) findViewById(R.id.shop_avatar);
	}

	@Override
	protected void onResume() {
		super.onResume();
		fragShopName.setmessageTitleText("商铺名称", textSize);
		fragShopDescription.setmessageTitleText("店铺详情", textSize);
		if(shop!=null){
			imgShop.load(shop);
			fragShopName.setmessageEditText(shop.getShopName(), textSize, color);
			fragShopDescription.setmessageEditText(shop.getDescription(), textSize, color);
		}
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

			if (requestCode == REQUESTCODE_CAMERA) {
				Bitmap bmp = (Bitmap) data.getExtras().get("data");
				saveBitmap(bmp);
			} else if (requestCode == REQUESTCODE_ALBUM) {
				try {
					Bitmap bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
					saveBitmap(bmp);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		void upload(byte[] pngData){
			if(pngData!=null){
				OkHttpClient client = Server.getSharedClient();
				MultipartBody.Builder requestbody = new MultipartBody.Builder();
				requestbody.addFormDataPart("shopImage", "shopImage",
						RequestBody.create(MediaType.parse("image/png"), pngData));
				Request request = Server.requestBuilderWithApi("changeShopAvatar")
						.method("post", null).post(requestbody.build()).build();
				client.newCall(request).enqueue(new Callback() {

					@Override
					public void onResponse(final Call arg0, final Response arg1) throws IOException {
						final Shop shop;
						try {
							shop = new ObjectMapper().readValue(arg1.body().bytes(), Shop.class);
							runOnUiThread(new Runnable() {
								@Override

								public void run() {
									ShopSettingActivity.this.onResponseAvatar(arg0, shop);

								}
							});
						} catch (final Exception e1) {
							e1.printStackTrace();
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									ShopSettingActivity.this.onFailure(arg0, e1);

								}
							});
						}
					}

					@Override
					public void onFailure(final Call arg0, final IOException arg1) {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								ShopSettingActivity.this.onFailure(arg0, arg1);
							}
						});
					}
				});
			}
		}
		
		protected void onResponseAvatar(Call arg0,final Shop shop) {
			new AlertDialog.Builder(this).setTitle("修改成功")
			.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					imgShop.load(shop);
					P.dismiss();
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
