package com.example.bbook.api.widgets;

import com.example.bbook.R;

import android.R.color;
import android.app.Fragment;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
/**
 * 
 * @author 刘世杰
 *	分类左视图控件
 */
public class ClassifyLeftViewFragment extends Fragment {
	TextView item;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		View view = inflater.inflate(R.layout.fragment_wedget_classify_leftview, null);
		item = (TextView) view.findViewById(R.id.classify_item);
		
		//这里添加点击事件,就是一个LeftViewItem控制一个RightView
		item.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	//设置内容
	public void setText(String text){
		item.setText(text);
	}
	//设置字体颜色,设置为true则变成红色,false则为黑色
	public void setTextColor(boolean click){
		if(click!=true){
			item.setTextColor(color.background_dark);
		}else item.setTextColor(color.holo_red_light);
	}
}
