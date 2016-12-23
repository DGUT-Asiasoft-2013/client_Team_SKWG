package com.example.bbook.api.widgets;

import java.util.zip.Inflater;

import com.example.bbook.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class TitleBarFragment extends Fragment {

        Button  btnNext;
        TextView titleName;
        ImageButton btnBack;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
                View view = inflater.inflate(R.layout.fragment_widget_titlebar, null);

                btnBack = (ImageButton) view.findViewById(R.id.btn_back);
                titleName = (TextView) view.findViewById(R.id.title_name);
                btnNext = (Button) view.findViewById(R.id.btn_next);

                btnBack.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                                onGoBack();
                        }
                });
                
                btnNext.setOnClickListener(new View.OnClickListener() {
                        
                        @Override
                        public void onClick(View v) {
                                onGoNext();
                        }
                });
                return view;
        }

        
        //更改按钮状态，true为不可见，false为课可见
        public void setBtnBackState(boolean is){
                if(is){
                        btnBack.setVisibility(View.GONE);
                }else{
                        btnBack.setVisibility(View.VISIBLE);
                }
        }
        
        public void setBtnNextState(boolean is){
                if(is){
                        btnNext.setVisibility(View.GONE);
                }else{
                        btnNext.setVisibility(View.VISIBLE);
                }
        }
        
        public void setTitleState(boolean is){
                if(is){
                        titleName.setVisibility(View.GONE);
                }else{
                        titleName.setVisibility(View.VISIBLE);
                }
        }

        //更改next按钮内容
        public void setBtnNextText(String btnNextText, float textSize){
                btnNext.setTextSize(textSize);
                btnNext.setText(btnNextText);
        }

        // 更改标题内容
        public void setTitleName(String titleText) {
                titleName.setText(titleText);
        }

        
        //btnBack监听器
        public static interface OnGoBackListener {
                void onGoBack();
        }

        OnGoBackListener onGoBackListener;

        public void setOnGoBackListener(OnGoBackListener onGoBackListener) {
                this.onGoBackListener = onGoBackListener;
        }

        void onGoBack() {
                if (onGoBackListener != null) {
                        onGoBackListener.onGoBack();
                }
        }
        
        //btnNext监听器
        public static interface OnGoNextListener {
                void onGoNext();
        }
        
        OnGoNextListener onGoNextListener;

        public void setOnGoNextListener(OnGoNextListener onGoNextListener) {
                this.onGoNextListener = onGoNextListener;
        }

        void onGoNext() {
                if (onGoNextListener != null) {
                        onGoNextListener.onGoNext();
                }
        }
}
