package com.example.bbook.api.widgets;

import com.example.bbook.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ItemFragment extends Fragment {

        View item;
        ImageView itemImage;
        TextView itemText;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
                View view = inflater.inflate(R.layout.fragment_widget_item, null);

                item = view.findViewById(R.id.item);
                itemImage = (ImageView) view.findViewById(R.id.item_image);
                itemText = (TextView) view.findViewById(R.id.item_text);

                item.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                                onDetailed();
                        }
                });
                return view;
        }

        // 监听点击事件
        public static interface OnDetailedListener {
                void onDetailed();
        }

        OnDetailedListener onDetailedListener;

        public void setOnDetailedListener(OnDetailedListener onDetailedListener) {
                this.onDetailedListener = onDetailedListener;
        }

        void onDetailed() {
                if (onDetailedListener != null) {
                        onDetailedListener.onDetailed();
                }
        }

        // 设置图片(R.drawable.xx)
        public void setItemImage(int resource) {
                itemImage.setImageResource(resource);
        }

        // 设置内容
        public void setItemText(String text) {
                itemText.setText(text);
        }

        // 设置控件是否可见
        public void setItemState(boolean is) {
                if (is) {
                        item.setVisibility(View.GONE);
                } else {
                        item.setVisibility(View.VISIBLE);
                }
        }
}
