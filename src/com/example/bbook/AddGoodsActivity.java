package com.example.bbook;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.example.bbook.api.Page;
import com.example.bbook.api.Server;
import com.example.bbook.api.Shop;
import com.example.bbook.api.entity.PublishInfo;
import com.example.bbook.api.entity.Subscribe;
import com.example.bbook.api.widgets.ItemFragment;
import com.example.bbook.api.widgets.TitleBarFragment;
import com.example.bbook.api.widgets.TitleBarFragment.OnGoBackListener;
import com.example.bbook.api.widgets.TitleBarFragment.OnGoNextListener;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.R.string;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import inputcells.PictureInputCellFragment;
import inputcells.SimpleTextInputcellFragment;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddGoodsActivity extends Activity {
	TitleBarFragment fragTitleBar;
	Spinner spinner;
	List<String> type_list;
	ArrayAdapter<String> type_adapter;
	String selectedType,str_detail;

	Shop shop;
	PublishInfo publishinfo;
	List<Subscribe> subscribeData;
	Integer userId;
	ItemFragment itempublish,itemdetail;
	private static final int RequestCode_Publish=1;
	private static final int RequestCode_Detail=2;
	SimpleTextInputcellFragment fragGoodsName, fragGoodsType, fragGoodsPrice, fragGoodsCount;
	PictureInputCellFragment fragGoodsImage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_goods);
		fragGoodsName = (SimpleTextInputcellFragment) getFragmentManager()
				.findFragmentById(R.id.input_goods_name);
		fragGoodsPrice = (SimpleTextInputcellFragment) getFragmentManager()
				.findFragmentById(R.id.input_goods_price);
		fragGoodsPrice.setEditNum(true); // 设置价格输入为整型
		fragGoodsCount = (SimpleTextInputcellFragment) getFragmentManager()
		.findFragmentById(R.id.input_goods_count);
		fragGoodsCount.setEditNum(true); // 设置数量输入为整型

		fragGoodsImage = (PictureInputCellFragment) getFragmentManager()
				.findFragmentById(R.id.input_goods_image);

		fragTitleBar = (TitleBarFragment) getFragmentManager().findFragmentById(R.id.title_bar);
		fragTitleBar.setTitleName("发布商品", 16);
		fragTitleBar.setOnGoBackListener(new OnGoBackListener() {
			
			@Override
			public void onGoBack() {
				finish();
				overridePendingTransition(R.anim.none, R.anim.slide_out_right);
			}
		});
		fragTitleBar.setOnGoNextListener(new OnGoNextListener() {
			
			@Override
			public void onGoNext() {
				onSubmit();
			}
		});
		fragTitleBar.setBtnNextText("发布", 12);

		//初始化publishinfo、宝贝描述
		publishinfo=new PublishInfo();
		publishinfo.setGoodsAuthor("");
		publishinfo.setGoodsPublisher("");
		publishinfo.setGoodsPubDate("");
		publishinfo.setGoodsPritime("");
		str_detail="";

		shop = (Shop) getIntent().getSerializableExtra("shop");

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

	}

	protected void onSubmit() {
		String goodsName = fragGoodsName.getText();
		if(goodsName.isEmpty()){
			Toast toast = Toast.makeText(AddGoodsActivity.this, "商品名称不能为空", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			return;
		}
		String goodsType = selectedType;
		String goodsPrice = fragGoodsPrice.getText();
		if(goodsPrice.isEmpty()){
			Toast toast = Toast.makeText(AddGoodsActivity.this, "商品价格不能为空", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			return;
		}
		String goodsCount = fragGoodsCount.getText();
		if(goodsCount.isEmpty()){
			Toast toast = Toast.makeText(AddGoodsActivity.this, "商品库存不能为空", Toast.LENGTH_SHORT);
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
		if (fragGoodsImage.getPngData() != null) {
			body.addFormDataPart("goodsImage", "goodsImage",
					RequestBody.create(MediaType.parse("image/png"), fragGoodsImage.getPngData()));
		}

		Request request = Server.requestBuilderWithApi("goods").method("post", null).post(body.build()).build();

		Server.getSharedClient().newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(final Call arg0, Response arg1) throws IOException {
				try {
					final String responString = arg1.body().toString();
					runOnUiThread(new Runnable() {
						public void run() {
							AddGoodsActivity.this.onResponse(arg0, responString);
						}
					});
				} catch (final Exception e) {
					runOnUiThread(new Runnable() {
						public void run() {
							AddGoodsActivity.this.onFailure(arg0, e);
						}
					});
				}
			}

			@Override
			public void onFailure(final Call arg0, final IOException arg1) {
				runOnUiThread(new Runnable() {
					public void run() {
						AddGoodsActivity.this.onFailure(arg0, arg1);
					}
				});
			}
		});

		//通过shopId找到user
		Request request2 = Server.requestBuilderWithApi("shop/" + shop.getId() +"/findsubscribe").get().build();
		Server.getSharedClient().newCall(request2).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, final Response arg1) throws IOException {
				try {
					final Page<Subscribe> data = new ObjectMapper().readValue(arg1.body().string(), new TypeReference<Page<Subscribe>>(){});; 
					runOnUiThread(new Runnable() {
						@Override
						public void run() {

							AddGoodsActivity.this.subscribeData = data.getContent();
							for(int i = 0; i < AddGoodsActivity.this.subscribeData.size(); i++) {
								userId = subscribeData.get(i).getId().getUser().getId();
								AddGoodsActivity.this.onSend();
							}
						}
					});
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(Call arg0, IOException arg1) {
				// TODO Auto-generated method stub

			}
		});

	}

	void onSend() {
		MultipartBody body = new MultipartBody.Builder()
				.addFormDataPart("shopId", shop.getId() +"")
				.addFormDataPart("receiverId", userId + "")
				.addFormDataPart("content", shop.getShopName() + "商店发布了新书，点击查看详情").build();

		Request request3 = Server.requestBuilderWithApi("push").method("post", null).post(body).build();
		Server.getSharedClient().newCall(request3).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {

			}

			@Override
			public void onFailure(Call arg0, IOException arg1) {

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

	void onResponse(Call arg0, String responseBody) {
		new AlertDialog.Builder(this).setTitle("发布成功")
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
				overridePendingTransition(0, R.anim.slide_out_right);
			}
		}).show();
	}

	void onFailure(Call arg0, Exception arg1) {
		new AlertDialog.Builder(this).setTitle("发布失败").setMessage(arg1.getLocalizedMessage())
		.setNegativeButton("OK", null).show();
	}

	//跳转到添加出版信息
	void publish(){
		Intent itnt=new Intent(this,AddPublishActivity.class);
		itnt.putExtra("publishinfo", publishinfo);
		startActivityForResult(itnt, RequestCode_Publish);
	}

	//跳转到添加宝贝描述页面
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
	}

}
