package com.example.bbook;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.example.bbook.api.Chat;
import com.example.bbook.api.Page;
import com.example.bbook.api.Server;
import com.example.bbook.api.Shop;
import com.example.bbook.api.User;
import com.example.bbook.api.widgets.AutoSplitTextView;
import com.example.bbook.api.widgets.AvatarView;
import com.example.bbook.api.widgets.TitleBarFragment;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.Response;

public class ChatActivity extends Activity {

        List<Chat> chatData;
        int page;
        User receiver;
        ListView chatList;
        TitleBarFragment fragChatTitleBar;
        EditText editChat;
        Shop shop;
        String content;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_chat);
                shop = (Shop) getIntent().getSerializableExtra("shop");

                fragChatTitleBar = (TitleBarFragment) getFragmentManager().findFragmentById(R.id.chat_titlebar);
                fragChatTitleBar.setTitleName(shop.getOwner().getName(), 16);
                fragChatTitleBar.setBtnNextState(false);
                fragChatTitleBar.setOnGoBackListener(new TitleBarFragment.OnGoBackListener() {
                        
                        @Override
                        public void onGoBack() {
                                finish();
                        }
                });

                chatList = (ListView) findViewById(R.id.chat_list);
                chatList.setDivider(null);
                chatList.setAdapter(adapter);

                editChat = (EditText) findViewById(R.id.et_chat);
                

                findViewById(R.id.send_chat).setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                                sendChat();
                        }
                });

        }

        class ViewHolderMe {
                AvatarView avatar = null;
                AutoSplitTextView textView = null;
        }

        class ViewHolderOthers {
                AvatarView avatar = null;
                AutoSplitTextView textView = null;
        }

        BaseAdapter adapter = new BaseAdapter() {

                private final int TYPE1 = 0;
                private final int TYPE2 = 1;

                @SuppressLint("InflateParams")
                @Override
                public View getView(int position, View view, ViewGroup parent) {
                        Chat data = chatData.get(position);
                        int type = getItemViewType(position);
                        ViewHolderMe holder1 = null;
                        ViewHolderOthers holder2 = null;

                        if (view == null) {
                                switch (type) {
                                case TYPE1:
                                        view = LayoutInflater.from(parent.getContext())
                                                        .inflate(R.layout.sender_list_item, null);
                                        holder1 = new ViewHolderMe();
                                        holder1.textView = (AutoSplitTextView) view
                                                        .findViewById(R.id.sender_chat_content);
                                        holder1.avatar = (AvatarView) view.findViewById(R.id.avatar_sender);
                                        holder1.textView.setText(data.getContent());
                                        holder1.avatar.load(Server.serverAdress + data.getSender().getAvatar());
                                        view.setTag(holder1);
                                        break;

                                case TYPE2:
                                        view = LayoutInflater.from(parent.getContext())
                                                        .inflate(R.layout.receiver_list_item, null);
                                        holder2 = new ViewHolderOthers();
                                        holder2.textView = (AutoSplitTextView) view
                                                        .findViewById(R.id.receiver_chat_content);
                                        holder2.avatar = (AvatarView) view.findViewById(R.id.avatar_receiver);
                                        holder2.textView.setText(data.getContent());
                                        holder2.avatar.load(Server.serverAdress + data.getReceiver().getAvatar());
                                        view.setTag(holder2);
                                        break;

                                default:
                                        break;
                                }
                        } else {
                                switch (type) {
                                case TYPE1:
                                        holder1 = (ViewHolderMe) view.getTag();
                                        holder1.textView = (AutoSplitTextView) view
                                                        .findViewById(R.id.sender_chat_content);
                                        holder1.avatar = (AvatarView) view.findViewById(R.id.avatar_sender);
                                        holder1.textView.setText(data.getContent());
                                        holder1.avatar.load(Server.serverAdress + data.getSender().getAvatar());
                                        break;

                                case TYPE2:
                                        holder2 = (ViewHolderOthers) view.getTag();
                                        holder2.textView = (AutoSplitTextView) view
                                                        .findViewById(R.id.receiver_chat_content);
                                        holder2.avatar = (AvatarView) view.findViewById(R.id.avatar_receiver);
                                        holder2.textView.setText(data.getContent());
                                        holder2.avatar.load(Server.serverAdress + data.getReceiver().getAvatar());
                                        break;

                                default:
                                        break;
                                }
                        }
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

                @Override
                public int getViewTypeCount() {
                        return 2;
                };

                @Override
                public int getItemViewType(int position) {
                        Chat data = chatData.get(position);
                        if (data.getReceiver().getId().equals(shop.getOwner().getId())) {
                                return TYPE1;
                        } else {
                                return TYPE2;
                        }
                };
        };

        void sendChat() {
                content = editChat.getText().toString();
                if (content.isEmpty()) {
                        Toast.makeText(ChatActivity.this, "内容不能为空", Toast.LENGTH_SHORT).show();
                        return;
                }

                String receiverId = shop.getOwner().getId() + "";

                MultipartBody.Builder body = new MultipartBody.Builder()
                                .addFormDataPart("receiverId", receiverId)
                                .addFormDataPart("content", content);

                Request request = Server.requestBuilderWithApi("chat")
                                .method("post", null)
                                .post(body.build())
                                .build();
                
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

                        }
                });

                editChat.setText("");
        }

        void reload() {
                Request request = Server.requestBuilderWithApi("findchat/" + shop.getOwner().getId())
                                .method("GET", null)
                                .build();
                Server.getSharedClient().newCall(request).enqueue(new Callback() {

                        @Override
                        public void onResponse(Call arg0, Response arg1) throws IOException {
                                try {
                                        final Page<Chat> pageData = new ObjectMapper().readValue(arg1.body().string(),
                                                        new TypeReference<Page<Chat>>() {});
                                        runOnUiThread(new Runnable() {
                                                public void run() {
                                                        ChatActivity.this.page = pageData.getNumber();
//                                                        List<Chat> reverse = new ArrayList<Chat>();
                                                        List<Chat> content = pageData.getContent();
//                                                        for (int i = content.size() - 1; i >= 0; i--) {
//                                                                reverse.add(content.get(i));
//                                                        }
                                                        ChatActivity.this.chatData = content;

                                                        adapter.notifyDataSetInvalidated();
                                                }
                                        });
                                } catch (Exception e) {
                                        e.printStackTrace();
                                }
                        }

                        @Override
                        public void onFailure(Call arg0, IOException arg1) {
                                // TODO Auto-generated method stub

                        }
                });
        }

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
                chatList.setSelection(chatData.size()-1);
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
