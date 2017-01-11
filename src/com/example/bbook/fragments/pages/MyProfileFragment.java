package com.example.bbook.fragments.pages;

import java.io.IOException;

import com.example.bbook.ChangeUserMessageActivity;
import com.example.bbook.MessageActivity;
import com.example.bbook.MyBillActivity;
import com.example.bbook.MyOrdersActivity;
import com.example.bbook.MySubscribeActivity;
import com.example.bbook.MyWalletActivity;
import com.example.bbook.MystoreActivity;
import com.example.bbook.OpenStoreActivity;
import com.example.bbook.R;
import com.example.bbook.SettingsActivity;
import com.example.bbook.api.Server;
import com.example.bbook.api.User;
import com.example.bbook.api.widgets.AvatarView;
import com.example.bbook.api.widgets.ItemFragment;
import com.example.bbook.api.widgets.ItemFragment.OnDetailedListener;
import com.example.bbook.api.widgets.TitleBarFragment;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.R.color;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MyProfileFragment extends Fragment {

        User me;
        ImageView message;
        PopupMenu popupMenuMe;
        View view, menu;
        TextView txName;
        AvatarView avatar;
        TitleBarFragment fragMeTitleBar;
        PopupWindow bill;
        ItemFragment itemBtnExit, itemBtnOrder, itemBtnChange, itemOpenStore, itemMyStore, itemBill, itemWallet,
                        itemMySubscribe, itemMyAddress, itemSettings;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
                if (view == null) {
                        view = inflater.inflate(R.layout.fragment_page_myprofile, null);
                        txName = (TextView) view.findViewById(R.id.showName);
                        avatar = (AvatarView) view.findViewById(R.id.avatar);

                        fragMeTitleBar = (TitleBarFragment) getFragmentManager().findFragmentById(R.id.me_titlebar);
                        fragMeTitleBar.setBtnBackState(false);
                        fragMeTitleBar.setSplitLineState(false);
                        fragMeTitleBar.setBtnNextState(false);
                        fragMeTitleBar.setTitleName("我的", 16);

                        // 我的消息
                        message = (ImageView) view.findViewById(R.id.menu_me);
                        message.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                        goLookMessage();
                                }
                        });

                        // 我要开店
                        itemOpenStore = (ItemFragment) getFragmentManager().findFragmentById(R.id.btn_openStore);
                        itemOpenStore.setItemText("我要开店");
                        itemOpenStore.setItemImage(R.drawable.icon_shop);
                        itemOpenStore.setOnDetailedListener(new ItemFragment.OnDetailedListener() {

                                @Override
                                public void onDetailed() {
                                        Intent itnt = new Intent(getActivity(), OpenStoreActivity.class);
                                        startActivity(itnt);
                                        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.none);
                                }
                        });

                        // 我的店铺
                        itemMyStore = (ItemFragment) getFragmentManager().findFragmentById(R.id.btn_myStore);
                        itemMyStore.setItemText("我的店铺");
                        itemMyStore.setItemImage(R.drawable.icon_shop);
                        itemMyStore.setOnDetailedListener(new ItemFragment.OnDetailedListener() {

                                @Override
                                public void onDetailed() {
                                        Intent itnt = new Intent(getActivity(), MystoreActivity.class);
                                        startActivity(itnt);
                                        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.none);
                                }
                        });

                        // 账单
                        itemBill = (ItemFragment) getFragmentManager().findFragmentById(R.id.btn_bill);
                        itemBill.setItemText("账单");
                        // 这个图标先用着之后找到了再改
                        itemBill.setItemImage(R.drawable.icon_bill);
                        itemBill.setOnDetailedListener(new ItemFragment.OnDetailedListener() {

                                @Override
                                public void onDetailed() {
                                        Intent intent = new Intent(getActivity(), MyBillActivity.class);
                                        startActivity(intent);
                                        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.none);
                                }
                        });

                        // 我的钱包
                        itemWallet = (ItemFragment) getFragmentManager().findFragmentById(R.id.btn_wallet);
                        itemWallet.setItemText("我的钱包");
                        itemWallet.setItemImage(R.drawable.icon_wallet);
                        itemWallet.setOnDetailedListener(new OnDetailedListener() {

                                @Override
                                public void onDetailed() {
                                        Intent itnt = new Intent(getActivity(), MyWalletActivity.class);
                                        startActivity(itnt);
                                        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.none);
                                }
                        });

                        // 我的订单
                        itemBtnOrder = (ItemFragment) getFragmentManager().findFragmentById(R.id.btn_order);
                        itemBtnOrder.setItemText("我的订单");
                        itemBtnOrder.setItemImage(R.drawable.icon_order);
                        itemBtnOrder.setOnDetailedListener(new ItemFragment.OnDetailedListener() {

                                @Override
                                public void onDetailed() {
                                        Intent intent = new Intent(getActivity(), MyOrdersActivity.class);
                                        startActivity(intent);
                                        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.none);
                                }
                        });

                        // 我的关注
                        itemMySubscribe = (ItemFragment) getFragmentManager().findFragmentById(R.id.btn_mysubscribe);
                        itemMySubscribe.setItemImage(R.drawable.icon_good);
                        itemMySubscribe.setItemText("我的关注");
                        itemMySubscribe.setOnDetailedListener(new ItemFragment.OnDetailedListener() {

                                @Override
                                public void onDetailed() {
                                        goMySubscribe();
                                }
                        });

                        // 设置
                        itemSettings = (ItemFragment) getFragmentManager().findFragmentById(R.id.btn_settings);
                        itemSettings.setItemText("设置");
                        itemSettings.setItemImage(R.drawable.icon_setting);
                        itemSettings.setOnDetailedListener(new ItemFragment.OnDetailedListener() {

                                public void onDetailed() {
                                        Intent itnt = new Intent(getActivity(), SettingsActivity.class);
                                        startActivity(itnt);
                                        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.none);
                                }
                        });
                }

                view.findViewById(R.id.btn_change_userMessage).setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                                Intent itnt = new Intent(getActivity(), ChangeUserMessageActivity.class);
                                startActivity(itnt);
                        }
                });
                return view;
        }

        @Override
        public void onResume() {
                super.onResume();
                itemOpenStore.setItemState(false);
                itemMyStore.setItemState(false);
                OkHttpClient client = Server.getSharedClient();
                Request request = Server.requestBuilderWithApi("me").method("get", null).build();
                client.newCall(request).enqueue(new Callback() {

                        @Override
                        public void onResponse(final Call arg0, Response arg1) throws IOException {
                                final User user = new ObjectMapper().readValue(arg1.body().bytes(), User.class);
                                getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                                MyProfileFragment.this.onResponse(arg0, user);
                                        }
                                });
                        }

                        @Override
                        public void onFailure(final Call arg0, final IOException arg1) {
                                getActivity().runOnUiThread(new Runnable() {

                                        @Override
                                        public void run() {
                                                MyProfileFragment.this.onFailure(arg0, arg1);
                                        }
                                });
                        }
                });
        }

        void onResponse(Call arg0, User user) {
                me = user;
                txName.setVisibility(View.VISIBLE);
                txName.setText("你好，" + user.getName());
                avatar.load(user);
                if (user.getIsStore().equals("0")) {
                        itemOpenStore.setItemState(true);
                } else
                        itemMyStore.setItemState(true);
        }

        void onFailure(Call arg0, Exception ex) {
                txName.setVisibility(View.VISIBLE);
                txName.setTextColor(color.holo_red_dark);
                txName.setText(ex.getMessage());
        }

        void goLookMessage() {
                Intent itnt = new Intent(getActivity(), MessageActivity.class);
                itnt.putExtra("user", me);
                startActivity(itnt);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.none);
        }

        void goMySubscribe() {
                Intent itnt = new Intent(getActivity(), MySubscribeActivity.class);
                startActivity(itnt);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.none);
        }

}
