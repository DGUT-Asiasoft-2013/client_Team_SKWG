package com.example.bbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.example.bbook.api.Goods;
import com.example.bbook.api.Page;
import com.example.bbook.api.Server;
import com.example.bbook.api.entity.PublishInfo;
import com.example.bbook.api.entity.Subscribe;
import com.example.bbook.api.widgets.GoodsPicture;
import com.example.bbook.api.widgets.ItemFragment;
import com.example.bbook.api.widgets.TitleBarFragment;
import com.example.bbook.api.widgets.TitleBarFragment.OnGoBackListener;
import com.example.bbook.api.widgets.TitleBarFragment.OnGoNextListener;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow.OnDismissListener;
import inputcells.SimpleTextInputcellFragment;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChangeGoodsInfoActivity extends Activity {
	TitleBarFragment fragTitleBar;
	Spinner spinner;
	List<String> type_list;
	ArrayAdapter<String> type_adapter;
	String selectedType,str_detail;

	Goods goods;
	GoodsPicture goodsimg;
	SimpleTextInputcellFragment fragGoodsName, fragGoodsType, fragGoodsPrice, fragGoodsCount;
	PublishInfo publishinfo;
	ItemFragment itempublish,itemdetail;
	private static final int RequestCode_Publish=1;
	private static final int RequestCode_Detail=2;
	private static final int REQUESTCODE_ALBUM = 3;
	private static final int REQUESTCODE_CAMERA = 4;

	private Context mycontext = null;
	TextView txtake,txpick,txcancel;
	private PopupWindow P;
	View parentView=null;
	byte[] pngData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		parentView=View.inflate(this, R.layout.activity_change_goodsinfo, null);
		setContentView(parentView);
		fragGoodsName = (SimpleTextInputcellFragment) getFragmentManager()
				.findFragmentById(R.id.input_goods_name);
		fragGoodsPrice = (SimpleTextInputcellFragment) getFragmentManager()
				.findFragmentById(R.id.input_goods_price);
		fragGoodsPrice.setEditNum(true); // 设置价格输入为整型
		fragGoodsCount = (SimpleTextInputcellFragment) getFragmentManager()
				.findFragmentById(R.id.input_goods_count);
		fragGoodsCount.setEditNum(true); // 设置数量输入为整型

		fragTitleBar = (TitleBarFragment) getFragmentManager().findFragmentById(R.id.title_bar);
		fragTitleBar.setTitleName("修改商品信息", 16);
		fragTitleBar.setOnGoBackListener(new OnGoBackListener() {

			@Override
			public void onGoBack() {
				finish();
			}
		});
		fragTitleBar.setOnGoNextListener(new OnGoNextListener() {

			@Override
			public void onGoNext() {
				onSubmit();
			}
		});
		fragTitleBar.setBtnNextText("修改", 12);

		goods =(Goods)getIntent().getSerializableExtra("goods");
		fragGoodsName.setText(goods.getGoodsName());
		fragGoodsPrice.setText(goods.getGoodsPrice());
		fragGoodsCount.setText(goods.getGoodsCount());
		goodsimg=(GoodsPicture)findViewById(R.id.goodsimg);
		goodsimg.load(Server.serverAdress+goods.getGoodsImage());

		//赋值publishinfo、宝贝描述
		publishinfo=new PublishInfo();
		publishinfo.setGoodsAuthor(goods.getAuthor());
		publishinfo.setGoodsPublisher(goods.getPublisher());
		publishinfo.setGoodsPubDate(goods.getPubDate());
		publishinfo.setGoodsPritime(goods.getPritime());
		if(goods.getGoodsDetail()==null||goods.getGoodsDetail().length()<=0){
			str_detail="";
		}else{
			str_detail=goods.getGoodsDetail();
		}
		// 跳转到书本出版信息
		itempublish = (ItemFragment) getFragmentManager().findFragmentById(R.id.btn_publish);
		itempublish.setItemText(" 出版信息 ");
		itempublish.setItemImageState(false);
		itempublish.setOnDetailedListener(new ItemFragment.OnDetailedListener() {
			@Override
			public void onDetailed() {
				publish();
			}
		});

		// 跳转到书本描述
		itemdetail = (ItemFragment) getFragmentManager().findFragmentById(R.id.btn_detail);
		itemdetail.setItemText(" 宝贝描述");
		itemdetail.setItemImageState(false);
		itemdetail.setOnDetailedListener(new ItemFragment.OnDetailedListener() {
			@Override
			public void onDetailed() {
				detail();
			}
		});

		spinner = (Spinner) findViewById(R.id.spinner_type);

		// 种类下拉框数据
		// "青春文学","历史","计算机","小说","建筑","自然科学","哲学","运动","文学","成功励志","保健养生","传记"
		type_list = new ArrayList<String>();
		type_list.add("青春文学");
		type_list.add("历史");
		type_list.add("计算机");
		type_list.add("小说");
		type_list.add("建筑");
		type_list.add("自然科学");
		type_list.add("哲学");
		type_list.add("运动");
		type_list.add("文学");
		type_list.add("成功励志");
		type_list.add("保健养生");
		type_list.add("传记");

		// 下拉框适配器
		type_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, type_list);
		// 设置样式
		type_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// 加载适配器
		spinner.setAdapter(type_adapter);

		//设置下拉框默认值
		int k= type_adapter.getCount();
		for(int i=0;i<k;i++){
			if((goods.getGoodsType()).equals(type_adapter.getItem(i).toString())){
				spinner.setSelection(i,true);// 默认选中项
				selectedType = type_adapter.getItem(i);
			}
		}
		//
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			// parent： 为控件Spinner view：显示文字的TextView
			// position：下拉选项的位置从0开始
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				// 获取Spinner选择position的值
				selectedType = type_adapter.getItem(position);
			}

			// 没有选中时的处理
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		//修改商品图片
		findViewById(R.id.chang_picture).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showWindow();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		fragGoodsName.setLabelText("商品名称");
		fragGoodsName.setHintText("请输入商品名称");
		fragGoodsPrice.setLabelText("价格");
		fragGoodsPrice.setHintText("请输入价格");
		fragGoodsCount.setLabelText("商品库存");
		fragGoodsCount.setHintText("请输入商品库存");

	}
	protected void onSubmit() {
		String goodsName = fragGoodsName.getText();
		if(goodsName.isEmpty()){
			Toast toast = Toast.makeText(ChangeGoodsInfoActivity.this, "商品名称不能为空", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			return;
		}
		String goodsType = selectedType;
		String goodsPrice = fragGoodsPrice.getText();
		if(goodsPrice.isEmpty()){
			Toast toast = Toast.makeText(ChangeGoodsInfoActivity.this, "商品价格不能为空", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			return;
		}
		String goodsCount = fragGoodsCount.getText();
		if(goodsCount.isEmpty()){
			Toast toast = Toast.makeText(ChangeGoodsInfoActivity.this, "商品库存不能为空", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			return;
		}
		String goodsPublisher = publishinfo.getGoodsPublisher();                       //从addpublishactivity中返回的参数
		String goodsAuthor = publishinfo.getGoodsAuthor();
		String goodsPubDate = publishinfo.getGoodsPubDate();
		String goodsPritime = publishinfo.getGoodsPritime();
		String goodsDetail = str_detail;                       //从addBookdetailactivity中返回的参数

		MultipartBody.Builder body = new MultipartBody.Builder().addFormDataPart("goodsName", goodsName)
				.addFormDataPart("goodsType", goodsType).addFormDataPart("goodsPrice", goodsPrice)
				.addFormDataPart("goodsCount", goodsCount).addFormDataPart("publisher", goodsPublisher)
				.addFormDataPart("author", goodsAuthor).addFormDataPart("pubDate", goodsPubDate)
				.addFormDataPart("pritime", goodsPritime).addFormDataPart("goodsDetail", goodsDetail);
		if (pngData != null) {
			body.addFormDataPart("goodsImage", "goodsImage",
					RequestBody.create(MediaType.parse("image/png"), pngData));
		}

		Request request = Server.requestBuilderWithApi("goods/change/"+goods.getId()).method("post", null).post(body.build()).build();

		Server.getSharedClient().newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(final Call arg0, Response arg1) throws IOException {
				try {
					final String responString = arg1.body().toString();
					runOnUiThread(new Runnable() {
						public void run() {
							ChangeGoodsInfoActivity.this.onResponse(arg0, responString);
						}
					});
				} catch (final Exception e) {
					runOnUiThread(new Runnable() {
						public void run() {
							ChangeGoodsInfoActivity.this.onFailure(arg0, e);
						}
					});
				}
			}

			@Override
			public void onFailure(final Call arg0, final IOException arg1) {
				runOnUiThread(new Runnable() {
					public void run() {
						ChangeGoodsInfoActivity.this.onFailure(arg0, arg1);
					}
				});
			}
		});

	}

	void onResponse(Call arg0, String responseBody) {
		new AlertDialog.Builder(this).setTitle("修改成功")
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
				overridePendingTransition(0, R.anim.slide_out_right);
			}
		}).show();
	}

	void onFailure(Call arg0, Exception arg1) {
		new AlertDialog.Builder(this).setTitle("修改失败").setMessage(arg1.getLocalizedMessage())
		.setNegativeButton("OK", null).show();
	}

	//跳转到修改出版信息
	void publish(){
		Intent itnt=new Intent(this,AddPublishActivity.class);
		itnt.putExtra("publishinfo", publishinfo);
		startActivityForResult(itnt, RequestCode_Publish);
	}

	//跳转到修改宝贝描述页面
	void detail(){
		Intent itnt=new Intent(this,AddBookDetailActivity.class);
		itnt.putExtra("bookdetail", str_detail);
		startActivityForResult(itnt, RequestCode_Detail);
	}

	//返回出版信息、宝贝描述
	@Override  
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
		// TODO Auto-generated method stub  
		super.onActivityResult(requestCode, resultCode, data);  
		//requestCode标示请求的标示   resultCode表示有数据  
		if (requestCode == RequestCode_Publish && resultCode == RESULT_OK) {
			itempublish.setItemText(" 出版信息                             已编辑");
			publishinfo.setGoodsPublisher(data.getStringExtra("goodsPublisher"));
			publishinfo.setGoodsAuthor(data.getStringExtra("goodsAuthor"));  
			publishinfo.setGoodsPubDate(data.getStringExtra("goodsPubDate")); 
			publishinfo.setGoodsPritime(data.getStringExtra("goodsPritime")); 
		}
		if (requestCode == RequestCode_Detail && resultCode == RESULT_OK) {
			itemdetail.setItemText(" 宝贝描述                             已编辑");
			str_detail = data.getStringExtra("goodsDetail"); 
		}
		if (requestCode == REQUESTCODE_CAMERA) {
			Bitmap bmp = (Bitmap) data.getExtras().get("data");
			saveBitmap(bmp);
			goodsimg.setBitmap(bmp);

		}
		if (requestCode == REQUESTCODE_ALBUM) {
			try {
				Bitmap bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
				saveBitmap(bmp);
				goodsimg.setBitmap(bmp);
			} catch (Exception e) {
				e.printStackTrace();
			}
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
				P.dismiss();
			}
		});
		//从相册选择按钮监听事件
		txpick.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				pickFromAlbum();
				P.dismiss();
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


//
//	void pickFrmCamera() {
//		Intent itnt = new Intent(Intent.ACTION_GET_CONTENT);
//		itnt.setType("image/*");
//		startActivityForResult(itnt, REQUESTCODE_ALBUM);
//	}

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
	}

}
