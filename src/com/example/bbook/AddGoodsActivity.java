package com.example.bbook;

import java.io.IOException;
import java.util.List;

import com.example.bbook.api.Page;
import com.example.bbook.api.Server;
import com.example.bbook.api.Shop;
import com.example.bbook.api.User;
import com.example.bbook.api.entity.Subscribe;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
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

public class AddGoodsActivity extends Activity{
	SimpleTextInputcellFragment fragGoodsName,
	fragGoodsType,fragGoodsPrice,fragGoodsCount,
	fragGoodsPublisher,fragGoodsAuthor,fragGoodsPubDate,
	fragGoodsPritime;
	PictureInputCellFragment fragGoodsImage;
	Shop shop;
	List<Subscribe> subscribeData;
	Integer userId;
	
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
	
		shop = (Shop) getIntent().getSerializableExtra("shop");
		
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
			        runOnUiThread(new Runnable() {
                        
                        @Override
                        public void run() {
                                
                        }
                });
			}
			
			@Override
			public void onFailure(Call arg0, IOException arg1) {
				
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
                                                 Log.d("size", AddGoodsActivity.this.subscribeData.size() +"");
                                                for(int i = 0; i < AddGoodsActivity.this.subscribeData.size(); i++) {
                                                        userId = subscribeData.get(i).getId().getUser().getId();
                                                        Log.d("userID", userId +"");
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

	protected void onSend() {
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
