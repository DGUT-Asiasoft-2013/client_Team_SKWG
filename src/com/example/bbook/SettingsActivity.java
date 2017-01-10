package com.example.bbook;

import java.io.IOException;

import com.example.bbook.api.Server;
import com.example.bbook.api.widgets.ItemFragment;
import com.example.bbook.api.widgets.TitleBarFragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SettingsActivity extends Activity {
        TitleBarFragment titlebar;
        ItemFragment itemBtnExit, itemLoginPassword, itemMyAddress, itemPayPassword;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_settings);

                titlebar = (TitleBarFragment) getFragmentManager().findFragmentById(R.id.settings_titlebar);
                titlebar.setBtnNextState(false);
                titlebar.setTitleName("设置", 16);
                titlebar.setOnGoBackListener(new TitleBarFragment.OnGoBackListener() {

                        @Override
                        public void onGoBack() {
                                finish();
                        }
                });

                // 修改登录密码
                itemLoginPassword = (ItemFragment) getFragmentManager()
                                .findFragmentById(R.id.btn_change_login_password);
                itemLoginPassword.setItemText("修改登录密码");
                itemLoginPassword.setItemImage(R.drawable.icon_password);
                itemLoginPassword.setOnDetailedListener(new ItemFragment.OnDetailedListener() {

                        public void onDetailed() {
                                Intent itnt = new Intent(SettingsActivity.this, ChangePasswordActivity.class);
                                startActivity(itnt);
                        }
                });

                // 修改支付密码
                itemPayPassword = (ItemFragment) getFragmentManager().findFragmentById(R.id.btn_change_pay_password);
                itemPayPassword.setItemText("修改支付密码");
                itemPayPassword.setItemImage(R.drawable.icon_pay_password);
                itemPayPassword.setOnDetailedListener(new ItemFragment.OnDetailedListener() {

                        public void onDetailed() {
                        	Intent itnt = new Intent(SettingsActivity.this, ChangePayPasswordActivity.class);
                            startActivity(itnt);
                        }
                });

                // 我的地址
                itemMyAddress = (ItemFragment) getFragmentManager().findFragmentById(R.id.btn_myaddress);
                itemMyAddress.setItemText("我的地址");
                itemMyAddress.setItemImage(R.drawable.icon_address);
                itemMyAddress.setOnDetailedListener(new ItemFragment.OnDetailedListener() {

                        @Override
                        public void onDetailed() {
                                Intent intent = new Intent(SettingsActivity.this, ManageCommomInfoActivity.class);
                                startActivity(intent);
                        }
                });

                // 注销
                itemBtnExit = (ItemFragment) getFragmentManager().findFragmentById(R.id.btn_exit);
                itemBtnExit.setItemText("注销");
                itemBtnExit.setItemImage(R.drawable.icon_exit);
                itemBtnExit.setOnDetailedListener(new ItemFragment.OnDetailedListener() {

                        @Override
                        public void onDetailed() {
                                exit();
                        }
                });

        }

        void exit() {

                OkHttpClient client = Server.getSharedClient();
                Request request = Server.requestBuilderWithApi("exit").build();
                client.newCall(request).enqueue(new Callback() {

                        @Override
                        public void onResponse(final Call arg0, Response arg1) throws IOException {
                                SettingsActivity.this.runOnUiThread(new Runnable() {

                                        @Override
                                        public void run() {
                                                SettingsActivity.this.onResponse(arg0);
                                        }
                                });

                        }

                        @Override
                        public void onFailure(Call arg0, IOException arg1) {

                        }
                });

        }

        void onResponse(Call arg0) {
                new AlertDialog.Builder(SettingsActivity.this).setTitle("提示").setMessage("是否要注销退出")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                                // TODO Auto-generated method
                                                // stub
                                                Intent itnt = new Intent(SettingsActivity.this, LoginActivity.class);
                                                itnt.setFlags(itnt.FLAG_ACTIVITY_CLEAR_TASK
                                                                | itnt.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(itnt);
                                        }
                                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                                // TODO Auto-generated method
                                                // stub
                                                dialog.dismiss();
                                        }
                                }).show();
        }

}
