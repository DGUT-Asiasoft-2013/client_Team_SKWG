package com.example.bbook;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.example.bbook.api.Server;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import inputcells.PictureInputCellFragment;
import inputcells.SimpleTextInputcellFragment;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddGoodsActivity extends Activity{
	Spinner spinner;
	List<String> type_list;
	ArrayAdapter<String> type_adapter;
	String selectedType;

	SimpleTextInputcellFragment fragGoodsName,
	fragGoodsType,fragGoodsPrice,fragGoodsCount,
	fragGoodsPublisher,fragGoodsAuthor,fragGoodsPubDate,
	fragGoodsPritime;
	PictureInputCellFragment fragGoodsImage;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_goods);
		fragGoodsName = (SimpleTextInputcellFragment) getFragmentManager().findFragmentById(R.id.input_goods_name);
		//		fragGoodsType =  (SimpleTextInputcellFragment) getFragmentManager().findFragmentById(R.id.input_goods_type);
		fragGoodsPrice = (SimpleTextInputcellFragment) getFragmentManager().findFragmentById(R.id.input_goods_price);
		fragGoodsPrice.setEditNum(true);    //设置价格输入为整型
		fragGoodsCount = (SimpleTextInputcellFragment) getFragmentManager().findFragmentById(R.id.input_goods_count);
		fragGoodsCount.setEditNum(true);    //设置数量输入为整型
		fragGoodsPublisher = (SimpleTextInputcellFragment) getFragmentManager().findFragmentById(R.id.input_goods_publisher);
		fragGoodsAuthor = (SimpleTextInputcellFragment) getFragmentManager().findFragmentById(R.id.input_goods_author);
		fragGoodsPubDate = (SimpleTextInputcellFragment) getFragmentManager().findFragmentById(R.id.input_goods_pubdate);
		fragGoodsPritime = (SimpleTextInputcellFragment) getFragmentManager().findFragmentById(R.id.input_goods_pritime);
		fragGoodsImage = (PictureInputCellFragment) getFragmentManager().findFragmentById(R.id.input_goods_image);

		spinner=(Spinner)findViewById(R.id.spinner_type);
		//种类下拉框数据 "青春文学","历史","计算机","小说","建筑","自然科学","哲学","运动","文学"
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

		//下拉框适配器
		type_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, type_list);
		//设置样式
		type_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		//加载适配器
		spinner.setAdapter(type_adapter);
		//
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			// parent： 为控件Spinner view：显示文字的TextView position：下拉选项的位置从0开始 
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				//获取Spinner选择position的值
				selectedType=type_adapter.getItem(position);
			}
			//没有选中时的处理
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		findViewById(R.id.submit).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onSubmit();
			}
		});
	}

	protected void onSubmit() {
		String goodsName = fragGoodsName.getText();
		String goodsType = selectedType;
		String goodsPrice = fragGoodsPrice.getText();
		String goodsCount = fragGoodsCount.getText();
		String goodsPublisher = fragGoodsPublisher.getText();
		String goodsAuthor = fragGoodsAuthor.getText();
		String goodsPubDate = fragGoodsPubDate.getText();
		String goodsPritime = fragGoodsPritime.getText();
//		isNulltips(goodsName, "请输入商品名称!");
//		isNulltips(goodsPrice, "请输入商品价格!");
//		isNulltips(goodsCount, "请输入商品库存!");
//		isNulltips(goodsPublisher, "请输入商品出版社!");
//		isNulltips(goodsAuthor, "请输入商品作者!");

		MultipartBody.Builder body = new MultipartBody.Builder()
				.addFormDataPart("goodsName", goodsName)
				.addFormDataPart("goodsType", goodsType)
				.addFormDataPart("goodsPrice", goodsPrice)
				.addFormDataPart("goodsCount", goodsCount)
				.addFormDataPart("publisher", goodsPublisher)
				.addFormDataPart("author", goodsAuthor)
				.addFormDataPart("pubDate", goodsPubDate)
				.addFormDataPart("pritime", goodsPritime);
		if(fragGoodsImage.getPngData() != null) {
			body.addFormDataPart("goodsImage", "goodsImage",
					RequestBody.create(MediaType.parse("image/png"), fragGoodsImage.getPngData()));
		}

		Request request = Server.requestBuilderWithApi("goods")
				.method("post",null)
				.post(body.build())
				.build();

		Server.getSharedClient().newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(final Call arg0, Response arg1) throws IOException {
				try{	     
					final String responString = arg1.body().toString();
					runOnUiThread(new Runnable() {
						public void run() {
							AddGoodsActivity.this.onResponse(arg0,responString);
						}
					});
				}catch (final Exception e) {
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
	}

	@Override
	protected void onResume() {
		super.onResume();
		fragGoodsName.setLabelText("商品名称");
		fragGoodsName.setHintText("请输入商品名称");
		//		fragGoodsType.setLabelText("商品种类");
		//		fragGoodsType.setHintText("请输入商品种类");
		fragGoodsPrice.setLabelText("价格");
		fragGoodsPrice.setHintText("请输入价格");
		fragGoodsCount.setLabelText("商品库存");
		fragGoodsCount.setHintText("请输入商品库存");
		fragGoodsPublisher.setLabelText("出版社");
		fragGoodsPublisher.setHintText("请输入出版社");
		fragGoodsAuthor.setLabelText("作者");
		fragGoodsAuthor.setHintText("请输入作者");
		fragGoodsPubDate.setLabelText("出版时间");
		fragGoodsPubDate.setHintText("请输入出版时间");
		fragGoodsPritime.setLabelText("印刷时间");
		fragGoodsPritime.setHintText("请输入印刷时间");
	}

	void onResponse(Call arg0, String responseBody) {
		new AlertDialog.Builder(this)
		.setTitle("发布成功")
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
		new AlertDialog.Builder(this)
		.setTitle("发布失败")
		.setMessage(arg1.getLocalizedMessage())
		.setNegativeButton("OK", null)
		.show();
	}

//	void isNulltips(String str,String tips){
//		if(str==null||str.isEmpty()){
//			new AlertDialog.Builder(getParent())
//			.setMessage(tips)
//			.setIcon(android.R.drawable.ic_dialog_alert)
//			.setPositiveButton("OK",null)
//			.show();
//			return;
//		}
//	}
}
