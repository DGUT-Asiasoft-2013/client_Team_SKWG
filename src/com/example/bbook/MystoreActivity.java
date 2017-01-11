package com.example.bbook;

import java.io.IOException;
import java.util.List;

import com.example.bbook.api.Goods;
import com.example.bbook.api.Page;
import com.example.bbook.api.Server;
import com.example.bbook.api.Shop;
import com.example.bbook.api.widgets.AvatarView;
import com.example.bbook.api.widgets.GoodsPicture;
import com.example.bbook.api.widgets.ItemFragment;
import com.example.bbook.api.widgets.TitleBarFragment;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.R.color;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MystoreActivity extends Activity {

        ImageView shopImageUseBg;
        TextView shopName, shopDescription, subscribeCount;
        GridView goodsView;
        List<Goods> data;
        GoodsPicture goodsPicture;
        TitleBarFragment fragMyShopTitleBar;
        int page = 0;
        Shop myshop;

        TextView tvShopName;
        GoodsPicture avatarShop;
        ItemFragment itemOnSale, itemOffSale, itemSold, itemOrderManage, itemSetting;
        
        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_my_store);

//                shopName = (TextView) findViewById(R.id.show_stopName);
                tvShopName = (TextView) findViewById(R.id.shop_name);
                avatarShop = (GoodsPicture) findViewById(R.id.shop_avatar);
//                shopImageUseBg = (AvatarView) findViewById(R.id.bgshow_img);
//                shopDescription = (TextView) findViewById(R.id.show_stopDescription);

                fragMyShopTitleBar = (TitleBarFragment) getFragmentManager().findFragmentById(R.id.title_bar);
                fragMyShopTitleBar.setBtnNextState(false);
                fragMyShopTitleBar.setTitleName("我的商店", 16);
                fragMyShopTitleBar.setOnGoBackListener(new TitleBarFragment.OnGoBackListener() {

                        @Override
                        public void onGoBack() {
                                finish();
                                overridePendingTransition(R.anim.none, R.anim.slide_out_right);
                        }
                });
                // 我发布的商品
                itemOnSale = (ItemFragment) getFragmentManager().findFragmentById(R.id.onsale);
                itemOnSale.setItemText("已发布的");
                itemOnSale.setItemImage(R.drawable.icon_onsale);
                itemOnSale.setOnDetailedListener(new ItemFragment.OnDetailedListener() {

                        @Override
                        public void onDetailed() {
                        	goMyPublishedGoods();
                        }
                });
                
                // 已下架的商品
                itemOffSale = (ItemFragment) getFragmentManager().findFragmentById(R.id.offsale);
                itemOffSale.setItemText("已下架的");
                itemOffSale.setItemImage(R.drawable.icon_offsale);
                itemOffSale.setOnDetailedListener(new ItemFragment.OnDetailedListener() {

                        @Override
                        public void onDetailed() {
                        	goMyOffSaleGoods();
                        }
                });
                
                // 我卖出的商品
                itemSold = (ItemFragment) getFragmentManager().findFragmentById(R.id.sold);
                itemSold.setItemText("我卖出的");
                itemSold.setItemImage(R.drawable.icon_sold);
                itemSold.setOnDetailedListener(new ItemFragment.OnDetailedListener() {

                        @Override
                        public void onDetailed() {
                        	goMySoldGoods();
                        }
                });
                
                // 订单管理
                itemOrderManage = (ItemFragment) getFragmentManager().findFragmentById(R.id.order_manage);
                itemOrderManage.setItemText("订单管理");
                itemOrderManage.setItemImage(R.drawable.icon_order_manage);
                itemOrderManage.setOnDetailedListener(new ItemFragment.OnDetailedListener() {

                        @Override
                        public void onDetailed() {
                        	goOrderManage();
                        }
                });
                
                // 设置
                itemSetting = (ItemFragment) getFragmentManager().findFragmentById(R.id.setting);
                itemSetting.setItemText("设置");
                itemSetting.setItemImage(R.drawable.icon_setting);
                itemSetting.setOnDetailedListener(new ItemFragment.OnDetailedListener() {

                        @Override
                        public void onDetailed() {
                        	goShopSetting();
                        }
                });
                // 关注
//                subscribeCount = (TextView) findViewById(R.id.subscribe_count);

                // 商品列表
//                goodsView = (GridView) findViewById(R.id.goods_gridview);
//                goodsView.setAdapter(goodsAdapter);
        }

        protected void goShopSetting() {
        	Intent itnt = new Intent(MystoreActivity.this, ShopSettingActivity.class);
        	itnt.putExtra("shop", myshop);
        	startActivity(itnt);			
        	overridePendingTransition(R.anim.slide_in_right, R.anim.none);
		}

		protected void goMySoldGoods() {
        	Intent itnt = new Intent(MystoreActivity.this, MySoldGoodsActivity.class);
        	startActivity(itnt);
        	overridePendingTransition(R.anim.slide_in_right, R.anim.none);
		}

		protected void goMyOffSaleGoods() {
        	Intent itnt = new Intent(MystoreActivity.this, MyOffSaleGoodsActivity.class);
        	startActivity(itnt);
        	overridePendingTransition(R.anim.slide_in_right, R.anim.none);
		}

		protected void goMyPublishedGoods() {
        	Intent itnt = new Intent(MystoreActivity.this, MyPublishedGoodsActivity.class);
        	itnt.putExtra("shop", myshop);
        	startActivity(itnt);
        	overridePendingTransition(R.anim.slide_in_right, R.anim.none);
		}

		protected void goOrderManage() {
        	Intent itnt = new Intent(MystoreActivity.this, ManageOrderActivity.class);
        	startActivity(itnt);
        	overridePendingTransition(R.anim.slide_in_right, R.anim.none);
		}

		protected void goAdd() {
                Intent itnt = new Intent(MystoreActivity.this, AddGoodsActivity.class);
                itnt.putExtra("shop", myshop);
                startActivity(itnt);
                overridePendingTransition(R.anim.slide_in_right, R.anim.none);
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
              
        }

        void onResponse(Call arg0, Shop shop) {
                myshop = shop;
                tvShopName.setText(shop.getShopName());
                avatarShop.load(Server.serverAdress + shop.getShopImage());
//                reloadSubscribe();
        }

        void onFailure(Call arg0, Exception ex) {
                shopName.setText(ex.getMessage());
                shopName.setTextColor(color.holo_red_dark);
                shopDescription.setText(ex.getMessage());
                shopDescription.setTextColor(color.holo_red_dark);
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
