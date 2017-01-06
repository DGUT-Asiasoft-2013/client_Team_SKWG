package com.example.bbook;

import java.io.IOException;
import java.util.List;

import com.example.bbook.api.Goods;
import com.example.bbook.api.Page;
import com.example.bbook.api.Server;
import com.example.bbook.api.Shop;
import com.example.bbook.api.widgets.AvatarView;
import com.example.bbook.api.widgets.GoodsPicture;
import com.example.bbook.api.widgets.TitleBarFragment;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.R.color;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MystoreActivity extends Activity {

        AvatarView shopImageUseBg;
        TextView shopName, shopDescription, subscribeCount;
        GridView goodsView;
        List<Goods> data;
        GoodsPicture goodsPicture;
        TitleBarFragment fragMyShopTitleBar;
        int page = 0;
        Shop myshop;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                // TODO Auto-generated method stub
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_edit_mystore);

                shopName = (TextView) findViewById(R.id.show_stopName);
                shopImageUseBg = (AvatarView) findViewById(R.id.bgshow_img);
                shopDescription = (TextView) findViewById(R.id.show_stopDescription);

                fragMyShopTitleBar = (TitleBarFragment) getFragmentManager().findFragmentById(R.id.my_shop_titlebar);
                fragMyShopTitleBar.setBtnNextState(false);
                fragMyShopTitleBar.setTitleName("我的商店", 16);
                fragMyShopTitleBar.setOnGoBackListener(new TitleBarFragment.OnGoBackListener() {

                        @Override
                        public void onGoBack() {
                                finish();
                        }
                });

                // 关注
                subscribeCount = (TextView) findViewById(R.id.subscribe_count);

                // 商品列表
                goodsView = (GridView) findViewById(R.id.goods_gridview);
                goodsView.setAdapter(goodsAdapter);
                findViewById(R.id.add_goods).setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                                goAdd();
                        }
                });
                findViewById(R.id.btn_manage).setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						goOrderManage();
					}
				});
        }

        protected void goOrderManage() {
        	Intent itnt = new Intent(MystoreActivity.this, ManageOrderActivity.class);
        	startActivity(itnt);
		}

		protected void goAdd() {
                Intent itnt = new Intent(MystoreActivity.this, AddGoodsActivity.class);
                itnt.putExtra("shop", myshop);
                startActivity(itnt);
        }

        @Override
        protected void onResume() {
                // TODO Auto-generated method stub
                super.onResume();

                OkHttpClient client = Server.getSharedClient();
                Request request = Server.requestBuilderWithApi("shop/myshop").get().build();
                client.newCall(request).enqueue(new Callback() {

                        @Override
                        public void onResponse(final Call arg0, final Response arg1) throws IOException {
                                // TODO Auto-generated method stub
                                final Shop shop = new ObjectMapper().readValue(arg1.body().string(), Shop.class);
                                // Log.d("shopid", shop.getId().toString());
                                runOnUiThread(new Runnable() {
                                        public void run() {
                                                MystoreActivity.this.onResponse(arg0, shop);
                                        }
                                });
                        }

                        @Override
                        public void onFailure(Call arg0, IOException arg1) {
                                // TODO Auto-generated method stub
                                MystoreActivity.this.onFailure(arg0, arg1);
                        }
                });
                GoodsLoad();
              
        }

        void onResponse(Call arg0, Shop shop) {
                myshop = shop;
                shopName.setText("我的店铺:" + shop.getShopName());
                shopDescription.setText(shop.getDescription());
                shopImageUseBg.load(shop);
                reloadSubscribe();
        }

        void onFailure(Call arg0, Exception ex) {
                shopName.setText(ex.getMessage());
                shopName.setTextColor(color.holo_red_dark);
                shopDescription.setText(ex.getMessage());
                shopDescription.setTextColor(color.holo_red_dark);
        }

        BaseAdapter goodsAdapter = new BaseAdapter() {
                @SuppressLint("InflateParams")
                @Override
                public View getView(final int position, View convertView, ViewGroup parent) {
                        View view = null;

                        if (convertView == null) {
                                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                                view = inflater.inflate(R.layout.fragment_picture_name, null);
                        } else {
                                view = convertView;
                        }
                        Goods goods = data.get(position);
                        TextView goodsPrice = (TextView) view.findViewById(R.id.price);
                        goodsPrice.setText("价格：" + goods.getGoodsPrice());
                        TextView goodsName = (TextView) view.findViewById(R.id.id);
                        goodsName.setText(goods.getGoodsName());

                        // 商品图片、点击事件
                        goodsPicture = (GoodsPicture) view.findViewById(R.id.picture);
                        goodsPicture.load(Server.serverAdress + goods.getGoodsImage());
                        goodsPicture.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                        goBookDetailActivity(position);
                                }
                        });

                        return view;
                }

                @Override
                public long getItemId(int position) {
                        // TODO Auto-generated method stub
                        return position;
                }

                @Override
                public Object getItem(int position) {
                        // TODO Auto-generated method stub
                        return data.get(position);
                        // return null;
                }

                @Override
                public int getCount() {
                        // TODO Auto-generated method stub
                        return data == null ? 0 : data.size();
                        // return 6;
                }

                @Override
                public boolean isEnabled(int position) {
                        return false;
                }
        };

        public void GoodsLoad() {

                OkHttpClient client = Server.getSharedClient();

                Request request = Server.requestBuilderWithApi("goods/mygoods").get().build();

                client.newCall(request).enqueue(new Callback() {

                        @Override
                        public void onResponse(Call arg0, final Response arg1) throws IOException {
                                // TODO Auto-generated method stub
                                final String responseStr = arg1.body().string();
                                Log.d("response", responseStr);
                                runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                                // TODO Auto-generated method
                                                // stub
                                                try {
                                                        Page<Goods> data = new ObjectMapper().readValue(responseStr,
                                                                        new TypeReference<Page<Goods>>() {
                                                                        });
                                                        MystoreActivity.this.data = data.getContent();
                                                        MystoreActivity.this.page = data.getNumber();
                                                        goodsAdapter.notifyDataSetInvalidated();
                                                } catch (Exception e) {
                                                        e.printStackTrace();
                                                }
                                        }
                                });
                        }

                        @Override
                        public void onFailure(Call arg0, IOException arg1) {

                        }
                });
        }

        public void goBookDetailActivity(int position) {
                Goods goods = data.get(position);
                Intent intent = new Intent(MystoreActivity.this, BookDetailActivity.class);
                intent.putExtra("goods", goods);
                startActivity(intent);
        }

        // 关注功能

        void reloadSubscribe() {
                Request request = Server.requestBuilderWithApi("shop/" + myshop.getId() + "/subscribe").get().build();
                Server.getSharedClient().newCall(request).enqueue(new Callback() {

                        @Override
                        public void onResponse(Call arg0, Response arg1) throws IOException {
                                try {
                                        final Integer count = new ObjectMapper().readValue(arg1.body().string(),
                                                        Integer.class);
                                        Log.d("count", count.toString());
                                        runOnUiThread(new Runnable() {
                                                public void run() {
                                                        onReloadSubscribeResult(count);
                                                }
                                        });
                                } catch (Exception e) {
                                        runOnUiThread(new Runnable() {
                                                public void run() {
                                                        onReloadSubscribeResult(0);
                                                }
                                        });
                                }
                        }

                        @Override
                        public void onFailure(Call arg0, IOException arg1) {
                                runOnUiThread(new Runnable() {
                                        public void run() {
                                                onReloadSubscribeResult(0);
                                        }
                                });
                        }
                });
        }

        void onReloadSubscribeResult(int count) {
                subscribeCount.setText("关注数:" + count);
        }


}
