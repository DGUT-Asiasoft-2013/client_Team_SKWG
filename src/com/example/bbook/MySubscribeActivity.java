package com.example.bbook;

import java.io.IOException;
import java.util.List;
import java.util.zip.Inflater;

import com.example.bbook.api.Page;
import com.example.bbook.api.Server;
import com.example.bbook.api.entity.Subscribe;
import com.example.bbook.api.widgets.AvatarView;
import com.example.bbook.api.widgets.TitleBarFragment;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.Response;

public class MySubscribeActivity extends Activity {

        int page;
        boolean istrue =true;
        List<Subscribe> dataList;
        private TitleBarFragment titlebar;
        private ListView listMS;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_mysubscribe);

                titlebar = (TitleBarFragment) getFragmentManager().findFragmentById(R.id.title_bar);
                titlebar.setBtnNextState(false);
                titlebar.setTitleName("我的关注", 16);
                titlebar.setOnGoBackListener(new TitleBarFragment.OnGoBackListener() {

                        @Override
                        public void onGoBack() {
                                finish();
                                overridePendingTransition(R.anim.none, R.anim.slide_out_right);
                        }
                });

                listMS = (ListView) findViewById(R.id.list_mysubscribe);
                listMS.setAdapter(adapter);
                listMS.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                goShop(position);
                        }
                });
        }

        @Override
        protected void onResume() {
                super.onResume();
                reload();
        }

        BaseAdapter adapter = new BaseAdapter() {

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                        View view;
                        if (convertView == null) {
                                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                                view = inflater.inflate(R.layout.list_item_mysubscribe, null);
                        } else {
                                view = convertView;
                        }

                        Subscribe data = dataList.get(position);
                        AvatarView avatar = (AvatarView) view.findViewById(R.id.avatar);
                        avatar.load(Server.serverAdress + data.getId().getShop().getShopImage());

                        TextView shopName = (TextView) view.findViewById(R.id.mysubscribe_shop_name);
                        shopName.setText(data.getId().getShop().getShopName());

                        TextView shopDescription = (TextView) view.findViewById(R.id.mysubscribe_shop_description);
                        shopDescription.setText(data.getId().getShop().getDescription());

                        final int shopId = data.getId().getShop().getId();

                        ImageView btnDelect = (ImageView) view.findViewById(R.id.btn_delect);
                        btnDelect.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                        delect(shopId);
                                }
                        });

                        return view;
                }

                @Override
                public long getItemId(int position) {
                        return position;
                }

                @Override
                public Object getItem(int position) {
                        return dataList.get(position);
                }

                @Override
                public int getCount() {
                        return dataList == null ? 0 : dataList.size();
                }
        };

        void reload() {
                Request request = Server.requestBuilderWithApi("shop/mysubscribe").get().build();
                Server.getSharedClient().newCall(request).enqueue(new Callback() {

                        @Override
                        public void onResponse(Call arg0, Response arg1) throws IOException {
                                final Page<Subscribe> data = new ObjectMapper().readValue(arg1.body().string(),
                                                new TypeReference<Page<Subscribe>>() {
                                                });
                                runOnUiThread(new Runnable() {
                                        public void run() {
                                                MySubscribeActivity.this.page = data.getNumber();
                                                MySubscribeActivity.this.dataList = data.getContent();
                                                adapter.notifyDataSetInvalidated();
                                        }
                                });
                        }

                        @Override
                        public void onFailure(Call arg0, IOException arg1) {

                        }
                });
        }

        void delect(final int shopId) {
                new AlertDialog.Builder(MySubscribeActivity.this).setTitle("是否取消关注").setPositiveButton("是", new DialogInterface.OnClickListener() {
                        
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                                MultipartBody.Builder body = new MultipartBody.Builder().addFormDataPart("subscribe", !istrue +"");
                                
                                Request request = Server.requestBuilderWithApi("shop/" + shopId + "/subscribe").post(body.build()).build();
                                Server.getSharedClient().newCall(request).enqueue(new Callback() {

                                        @Override
                                        public void onResponse(Call arg0, Response arg1) throws IOException {
                                                runOnUiThread(new Runnable() {

                                                        @Override
                                                        public void run() {
                                                                Toast.makeText(MySubscribeActivity.this, "已取消关注", Toast.LENGTH_SHORT).show();
                                                                reload();
                                                        }
                                                });
                                        }

                                        @Override
                                        public void onFailure(Call arg0, IOException arg1) {

                                        }
                                });
                                
                        }
                }) .setNegativeButton("否", null).show();
                
        }

        void goShop(int position) {
                Intent itnt = new Intent(MySubscribeActivity.this, ShopActivity.class);
                itnt.putExtra("shop", dataList.get(position).getId().getShop());
                startActivity(itnt);
        }

}
