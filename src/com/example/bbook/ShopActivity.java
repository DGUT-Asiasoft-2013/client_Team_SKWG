package com.example.bbook;

import java.io.IOException;
import java.util.List;

import com.example.bbook.api.Goods;
import com.example.bbook.api.Page;
import com.example.bbook.api.Server;
import com.example.bbook.api.Shop;
import com.example.bbook.api.widgets.GoodsPicture;
import com.example.bbook.api.widgets.TitleBarFragment;
import com.example.bbook.fragments.pages.HomepageFragment;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

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
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

//商家页面
public class ShopActivity extends Activity {

        boolean issubscribed;
        TitleBarFragment fragShopTitleBar;
        TextView subscribeCount, btnSubscribe;
        GridView goodsView;
        List<Goods> data;
        int page = 0;
        Shop shop;
        GoodsPicture shopPicture;
        GoodsPicture goodsPicture;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                // TODO Auto-generated method stub
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_shop);

                shop = (Shop) getIntent().getSerializableExtra("shop");
                // 店家图片
                shopPicture = (GoodsPicture) findViewById(R.id.shop_picture);
                shopPicture.load(Server.serverAdress + shop.getShopImage());
                // 店名、店主
                ((TextView) findViewById(R.id.shop_name)).setText("店名:" + shop.getShopName());
                ((TextView) findViewById(R.id.shop_owner)).setText("店主:" + shop.getOwner().getName());
                // 商品列表
                goodsView = (GridView) findViewById(R.id.goods_gridview);
                goodsView.setAdapter(goodsAdapter);

                // 联系卖家
                findViewById(R.id.contact_seller).setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                                contactSeller();
                        }
                });
                
                fragShopTitleBar = (TitleBarFragment) getFragmentManager().findFragmentById(R.id.shop_titlebar);
                fragShopTitleBar.setBtnNextState(false);
                fragShopTitleBar.setTitleName(shop.getOwner().getName() + " 的商店", 16);
                fragShopTitleBar.setOnGoBackListener(new TitleBarFragment.OnGoBackListener() {
                        
                        @Override
                        public void onGoBack() {
                                finish();
                        }
                });

                subscribeCount = (TextView) findViewById(R.id.subscribe_count);
                btnSubscribe = (TextView) findViewById(R.id.subscribe);
                btnSubscribe.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                                toggleSubscribe();
                        }
                });
                
        }

        @Override
        protected void onResume() {
                // TODO Auto-generated method stub
                super.onResume();
                reload();
                GoodsLoad();

        }

        public void GoodsLoad() {

                OkHttpClient client = Server.getSharedClient();

                Request request = Server.requestBuilderWithApi("goods/get/" + shop.getId()).get().build();

                client.newCall(request).enqueue(new Callback() {

                        @Override
                        public void onResponse(Call arg0, final Response arg1) throws IOException {
                                // TODO Auto-generated method stub
                                final String responseStr = arg1.body().string();
                                runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                                // TODO Auto-generated method
                                                // stub
                                                try {
                                                        Page<Goods> data = new ObjectMapper().readValue(responseStr,
                                                                        new TypeReference<Page<Goods>>() {
                                                                        });
                                                        ShopActivity.this.data = data.getContent();
                                                        ShopActivity.this.page = data.getNumber();
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

        public void goBookDetailActivity(int position) {
                Goods goods = data.get(position);
                Intent intent = new Intent(ShopActivity.this, BookDetailActivity.class);
                intent.putExtra("goods", goods);
                startActivity(intent);
        }

        public void contactSeller() {
                Intent itnt = new Intent(ShopActivity.this, ChatActivity.class);
                itnt.putExtra("shop", shop);
                startActivity(itnt);
        }
        
        void reload() {
                reloadSubscribe();
                checkSubscribe();
        }

        void reloadSubscribe() {
                Request request = Server.requestBuilderWithApi("shop/" + shop.getId() + "/subscribe").get().build();
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

        void checkSubscribe() {
                Request request = Server.requestBuilderWithApi("shop/" + shop.getId() + "/issubscribed").get().build();
                Server.getSharedClient().newCall(request).enqueue(new Callback() {

                        @Override
                        public void onResponse(Call arg0, Response arg1) throws IOException {
                                final boolean result = new ObjectMapper().readValue(arg1.body().string(),
                                                Boolean.class);
                                runOnUiThread(new Runnable() {
                                        public void run() {
                                                onCheckSubscribedResult(result);
                                        }
                                });
                        }

                        @Override
                        public void onFailure(Call arg0, IOException arg1) {
                                runOnUiThread(new Runnable() {
                                        public void run() {
                                                onCheckSubscribedResult(false);
                                        }
                                });
                        }
                });
        }

        void onCheckSubscribedResult(boolean result) {
                issubscribed = result;
                
                btnSubscribe.setText(result ? "已关注" : "关注");
        }

        
        void toggleSubscribe() {
                MultipartBody body = new MultipartBody.Builder()
                                .addFormDataPart("subscribe", String.valueOf(!issubscribed)).build();

                Request request = Server.requestBuilderWithApi("shop/" + shop.getId() + "/subscribe").post(body).build();
                Server.getSharedClient().newCall(request).enqueue(new Callback() {

                        @Override
                        public void onResponse(Call arg0, Response arg1) throws IOException {
                                runOnUiThread(new Runnable() {
                                        public void run() {
                                                reload();
                                        }
                                });
                        }

                        @Override
                        public void onFailure(Call arg0, IOException arg1) {
                                runOnUiThread(new Runnable() {
                                        public void run() {
                                                reload();
                                        }
                                });
                        }
                });
        }
}
