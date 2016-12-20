package com.example.bbook;

import java.io.IOException;

import com.example.bbook.api.Server;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
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
		fragGoodsType =  (SimpleTextInputcellFragment) getFragmentManager().findFragmentById(R.id.input_goods_type);
		fragGoodsPrice = (SimpleTextInputcellFragment) getFragmentManager().findFragmentById(R.id.input_goods_price);
		fragGoodsCount = (SimpleTextInputcellFragment) getFragmentManager().findFragmentById(R.id.input_goods_count);
		fragGoodsPublisher = (SimpleTextInputcellFragment) getFragmentManager().findFragmentById(R.id.input_goods_publisher);
		fragGoodsAuthor = (SimpleTextInputcellFragment) getFragmentManager().findFragmentById(R.id.input_goods_author);
		fragGoodsPubDate = (SimpleTextInputcellFragment) getFragmentManager().findFragmentById(R.id.input_goods_pubdate);
		fragGoodsPritime = (SimpleTextInputcellFragment) getFragmentManager().findFragmentById(R.id.input_goods_pritime);
		fragGoodsImage = (PictureInputCellFragment) getFragmentManager().findFragmentById(R.id.input_goods_image);
	
		findViewById(R.id.submit).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onSubmit();
			}
		});
	}
	
	protected void onSubmit() {
		String goodsName = fragGoodsName.getText();
		String goodsType = fragGoodsType.getText();
		String goodsPrice = fragGoodsPrice.getText();
		String goodsCount = fragGoodsCount.getText();
		String goodsPublisher = fragGoodsPublisher.getText();
		String goodsAuthor = fragGoodsAuthor.getText();
		String goodsPubDate = fragGoodsPubDate.getText();
		String goodsPritime = fragGoodsPritime.getText();
		
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
		fragGoodsType.setLabelText("商品种类");
		fragGoodsType.setHintText("请输入商品种类");
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
	
	
}
