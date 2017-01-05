package com.example.bbook;

import java.io.IOException;
import java.util.List;

import com.example.bbook.api.Page;
import com.example.bbook.api.Server;
import com.example.bbook.api.entity.Push;
import com.example.bbook.api.widgets.AvatarView;
import com.example.bbook.api.widgets.TitleBarFragment;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class PushMessageActivity extends Activity {

        TitleBarFragment fragTitleBar;
        TextView shopName, messageContent, editTime;
        ListView messageList;
        int page;
        List<Push> pushData;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_pushmessage);

                // TitleBar
                fragTitleBar = (TitleBarFragment) getFragmentManager().findFragmentById(R.id.push_titlebar);
                fragTitleBar.setBtnNextState(false);
                fragTitleBar.setTitleName("推送信息", 16);
                fragTitleBar.setOnGoBackListener(new TitleBarFragment.OnGoBackListener() {

                        @Override
                        public void onGoBack() {
                                finish();
                        }
                });

                // list
                messageList = (ListView) findViewById(R.id.pushmessage_list);
                messageList.setAdapter(adapter);
        }
        
        
        @Override
        protected void onResume() {
                super.onResume();
                reload();
        }

        BaseAdapter adapter = new BaseAdapter() {

                @SuppressLint("InflateParams")
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                        View view = null;
                        if(convertView == null){
                                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                                view = inflater.inflate(R.layout.list_item_chat, null);
                        }else{
                                view = convertView;
                        }
                        
                        Push data = pushData.get(position);
                        
                        shopName = (TextView) view.findViewById(R.id.tv_list_title);
                        shopName.setText(data.getShop().getShopName() + "商店");
                        
                        editTime = (TextView) view.findViewById(R.id.editTime);
                        String dateStr = DateFormat.format("MM月dd日", data.getCreateDate()).toString();
                        editTime.setText(dateStr);
                        
                        messageContent = (TextView) view.findViewById(R.id.tv_list_content);
                        messageContent.setText(data.getContent());
                        
                        AvatarView avatar = (AvatarView) view.findViewById(R.id.avatar);
                        avatar.load(Server.serverAdress + data.getShop().getShopImage());
                        
                        return view;
                }

                @Override
                public long getItemId(int position) {
                        return position;
                }

                @Override
                public Object getItem(int position) {
                        return pushData.get(position);
                }

                @Override
                public int getCount() {
                        if (pushData == null) {
                                return 0;
                        } else {
                                return pushData.size();
                        }
                }
        };
        
        void reload(){
                Request request = Server.requestBuilderWithApi("findpush").get().build();
                
                Server.getSharedClient().newCall(request).enqueue(new Callback() {
                        
                        @Override
                        public void onResponse(Call arg0, Response arg1) throws IOException {
                                try {
                                        final Page<Push> pageData = new ObjectMapper()
                                                        .readValue(arg1.body().string(), new TypeReference<Page<Push>>() { });
                                        runOnUiThread(new Runnable() {
                                                
                                                @Override
                                                public void run() {
                                                        PushMessageActivity.this.page = pageData.getNumber();
                                                        PushMessageActivity.this.pushData = pageData.getContent();
                                                        adapter.notifyDataSetInvalidated();
                                                }
                                        });
                                } catch (Exception e) {
                                        e.printStackTrace();
                                }
                       
                        }
                        
                        @Override
                        public void onFailure(Call arg0, IOException arg1) {
                                
                        }
                });
        }
      
        
}
