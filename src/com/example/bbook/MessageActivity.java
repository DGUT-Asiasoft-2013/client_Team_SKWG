package com.example.bbook;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.example.bbook.api.Chat;
import com.example.bbook.api.Page;
import com.example.bbook.api.Server;
import com.example.bbook.api.Shop;
import com.example.bbook.api.User;
import com.example.bbook.api.entity.Push;
import com.example.bbook.api.widgets.AvatarView;
import com.example.bbook.api.widgets.ItemFragment;
import com.example.bbook.api.widgets.TitleBarFragment;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jauker.widget.BadgeView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class MessageActivity extends Activity {

        Boolean isOwn;
        AvatarView avatar;
        int page, temp, othersId;
        List<Chat> chatData, check=null;
        TitleBarFragment fragMessageTitleBar;
        ItemFragment fragMessageItem, fragMySubscribe;
        ListView chatList;
        User me;
        Shop shop;
        TextView receiverName, messageContent, editTime;
        List<Integer> userList;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_message);
                me = (User) getIntent().getSerializableExtra("user");

                fragMessageTitleBar = (TitleBarFragment) getFragmentManager().findFragmentById(R.id.message_titlebar);
                fragMessageTitleBar.setBtnNextState(false);
                fragMessageTitleBar.setTitleName("消息", 16);
                fragMessageTitleBar.setOnGoBackListener(new TitleBarFragment.OnGoBackListener() {

                        @Override
                        public void onGoBack() {
                                finish();
                        }
                });

                fragMessageItem = (ItemFragment) getFragmentManager().findFragmentById(R.id.btn_push_message);
                fragMessageItem.setItemImage(R.drawable.icon_push);
                fragMessageItem.setItemText("推送");
                fragMessageItem.setOnDetailedListener(new ItemFragment.OnDetailedListener() {

                        @Override
                        public void onDetailed() {
                                goPushMessage();
                        }
                });
                
                
//                fragMySubscribe = (ItemFragment) getFragmentManager().findFragmentById(R.id.btn_mysubscribe);
//                fragMySubscribe.setItemImage(R.drawable.ic_launcher);
//                fragMySubscribe.setItemText("我的关注");
//                fragMySubscribe.setOnDetailedListener(new ItemFragment.OnDetailedListener() {
//
//                        @Override
//                        public void onDetailed() {
//                                goMySubscribe();
//                        }
//                });
                

                chatList = (ListView) findViewById(R.id.chat_list);
                chatList.setAdapter(adapter);
                chatList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                onItemClicked(position);
                        }
                }); 
        }


        BaseAdapter adapter = new BaseAdapter() {
                @SuppressLint("InflateParams")
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                        View view = null;
                        if (convertView == null) {
                                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                                view = inflater.inflate(R.layout.list_item_chat, null);
                        } else {
                                view = convertView;
                        }

                        Chat data = chatData.get(position);
                        receiverName = (TextView) view.findViewById(R.id.tv_list_title);

                        if (data.getSender().getId().equals(me.getId())) {
                                receiverName.setText(data.getReceiver().getName());
                        } else {
                                receiverName.setText(data.getSender().getName());
                        }

                        messageContent = (TextView) view.findViewById(R.id.tv_list_content);
                        messageContent.setText(data.getContent());
                        
                        editTime = (TextView) view.findViewById(R.id.editTime);
                        String dateStr = DateFormat.format("hh:mm", data.getCreateDate()).toString();
                        editTime.setText(dateStr);

                        if (data.getSender().getId().equals(me.getId())) {
                                avatar = (AvatarView) view.findViewById(R.id.avatar);
                                avatar.load(Server.serverAdress + data.getReceiver().getAvatar());
                        } else {
                                avatar = (AvatarView) view.findViewById(R.id.avatar);
                                avatar.load(Server.serverAdress + data.getSender().getAvatar());
                        }

                        //设置数字提醒(未实现)
//                        BadgeView bv = new BadgeView(MessageActivity.this);
//                        bv.setTargetView(avatar);
//                        bv.setBadgeCount(1);
//                        bv.setBackground(10, Color.RED);
//                        bv.setBadgeGravity(Gravity.END);
//                        
                        return view;
                }

                @Override
                public long getItemId(int position) {
                        return position;
                }

                @Override
                public Object getItem(int position) {
                        return chatData.get(position);
                }

                @Override
                public int getCount() {
                        if (chatData == null) {
                                return 0;
                        } else {
                                return chatData.size();
                        }
                }
        };

        void reload() {
                Request request = Server.requestBuilderWithApi("findmychat").get().build();

                Server.getSharedClient().newCall(request).enqueue(new Callback() {

                        @Override
                        public void onResponse(Call arg0, Response arg1) throws IOException {
                                try {
                                        final Page<Chat> pageData = new ObjectMapper().readValue(arg1.body().string(),
                                                        new TypeReference<Page<Chat>>() {
                                                        });
                                        runOnUiThread(new Runnable() {

                                                @Override
                                                public void run() {
                                                        MessageActivity.this.page = pageData.getNumber();
                                                        List<Chat> reverse = new ArrayList<Chat>();
                                                        List<Chat> content = pageData.getContent();

                                                        userList = new ArrayList<Integer>();

                                                        for (int i = 0; i < content.size(); i++) {
                                                                if (userList == null) {
                                                                        userList.add(me.getId());
                                                                        
                                                                        if(content.get(i).getSender().getId().equals(me.getId())){
                                                                                temp = content.get(i).getReceiver().getId();
                                                                                userList.add(temp);
                                                                                reverse.add(content.get(i));
                                                                        }else{
                                                                                temp = content.get(i).getSender().getId();
                                                                                userList.add(temp);
                                                                                reverse.add(content.get(i));
                                                                        }
                                                                        
                                                                } else {
                                                                        boolean isSame = false;
                                                                       if(content.get(i).getSender().getId().equals(me.getId()))
                                                                       {
                                                                               temp = content.get(i).getReceiver().getId();
                                                                               for (int j = 0; j < userList.size(); j++) {
                                                                                       if (temp == userList.get(j)) {
                                                                                               isSame = true;
                                                                                               break;
                                                                                       }
                                                                               }
                                                                       }else{
                                                                                temp = content.get(i).getSender().getId();
                                                                                for (int j = 0; j < userList.size(); j++) {
                                                                                        if (temp == userList.get(j)) {
                                                                                                isSame = true;
                                                                                                break;
                                                                                        }
                                                                                }
                                                                        }
                                                                       
                                                                       if (isSame == false) {
                                                                               userList.add(temp);
                                                                               reverse.add(content.get(i));
                                                                       }
                                                                }
                                                        }

                                                        if(check == null){
                                                                check = reverse;
                                                        }
                                                        MessageActivity.this.chatData = reverse;
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

        
        //点击对话内容跳转至对应的ChatActivity；
        void onItemClicked(int position) {
                Chat data = chatData.get(position);
                if (data.getSender().getId().equals(me.getId())) {
                        othersId = data.getReceiver().getId();
                } else {
                        othersId = data.getSender().getId();
                }
                
                Request request = Server.requestBuilderWithApi("shop/"+ othersId +"/findshop").get().build();
                Server.getSharedClient().newCall(request).enqueue(new Callback() {
                        
                        @Override
                        public void onResponse(Call arg0, Response arg1) throws IOException {
                                try {
                                        final Shop shopData = new ObjectMapper().readValue(arg1.body().string(), Shop.class);
                                        runOnUiThread(new Runnable() {
                                                public void run() {
                                                        shop = shopData;
                                                        goChat();
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
        
        void goChat(){
                Intent itnt = new Intent(MessageActivity.this, ChatActivity.class);
                itnt.putExtra("shop", shop);
                startActivity(itnt);
        }
        
        void goPushMessage() {
                Intent itnt = new Intent(MessageActivity.this, PushMessageActivity.class);
                startActivity(itnt);
        }
        
//        void goMySubscribe(){
//                Intent itnt = new Intent(MessageActivity.this, MySubscribeActivity.class);
//                startActivity(itnt);
//        }
        
        //刷新
        Handler handler = new Handler();
        boolean isVisible = false;

        @Override
        protected void onResume() {
                super.onResume();
                isVisible = true;
                refresh();
        }

        @Override
        protected void onPause() {
                super.onPause();
                isVisible = false;
        }

        void refresh() {
                reload();
                if (isVisible) {
                        handler.postDelayed(new Runnable() {

                                @Override
                                public void run() {
                                        refresh();
                                }
                        }, 1000);
                }
        }

}
