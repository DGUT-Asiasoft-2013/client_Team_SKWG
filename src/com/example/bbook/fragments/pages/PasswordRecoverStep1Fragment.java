package com.example.bbook.fragments.pages;

import com.example.bbook.R;
import com.example.bbook.api.widgets.TitleBarFragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import inputcells.SimpleTextInputcellFragment;

public class PasswordRecoverStep1Fragment extends Fragment {
        View view;
        SimpleTextInputcellFragment fragEmail;
        TitleBarFragment fragTitleBar;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
                if (view == null) {
                        view = inflater.inflate(R.layout.fragment_password_recover_step1, null);

                        fragEmail = (SimpleTextInputcellFragment) getFragmentManager()
                                        .findFragmentById(R.id.input_email);

                        fragTitleBar = (TitleBarFragment) getFragmentManager()
                                        .findFragmentById(R.id.recover_step1_titlebar);
                        fragTitleBar.setBtnNextText("下一步", 13);
                        fragTitleBar.setTitleName("找回密码", 18);

                        fragTitleBar.setOnGoBackListener(new TitleBarFragment.OnGoBackListener() {

                                @Override
                                public void onGoBack() {
                                        goBack();
                                }
                        });

                        fragTitleBar.setOnGoNextListener(new TitleBarFragment.OnGoNextListener() {

                                @Override
                                public void onGoNext() {
                                        goNext();
                                }
                        });

                }
                return view;
        }

        @Override
        public void onResume() {
                super.onResume();
                fragEmail.setLabelText("用户邮箱");
                fragEmail.setHintText("请输入注册时输入的邮箱");
        }

        public static interface OnGoNextListener {
                void onGoNext();
        }

        OnGoNextListener onGoNextListener;

        public void setOnGoNextListener(OnGoNextListener onGoNextListener) {
                this.onGoNextListener = onGoNextListener;
        }

        void goNext() {
                if (onGoNextListener != null) {
                        onGoNextListener.onGoNext();
                }
        }

        public String getText() {
                return fragEmail.getText();
        }
        
        //监听返回按钮
        public static interface OnGoBackListener {
                void onGoBack();
        }

        OnGoBackListener onGoBackListener;

        public void setOnGoBackListener(OnGoBackListener onGoBackListener) {
                this.onGoBackListener = onGoBackListener;
        }

        void goBack() {
                if (onGoBackListener != null) {
                        onGoBackListener.onGoBack();
                }
        }

}
