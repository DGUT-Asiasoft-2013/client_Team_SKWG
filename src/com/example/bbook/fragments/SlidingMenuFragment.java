package com.example.bbook.fragments;

import java.util.ArrayList;
import java.util.List;

import com.example.bbook.R;

import android.R.raw;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

public class SlidingMenuFragment extends Fragment {
	View view;
	GridView classifyView;
	String[] goodsType;
	List<String> typeList;
	String selectType;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if(view!=null){
			return view;
		}
		view=inflater.inflate(R.layout.fragment_sliding_menu, null);
		classifyView=(GridView) view.findViewById(R.id.classify_gridview);
		goodsType=new String[]{
				"全部","青春文学","历史","计算机","小说","建筑","自然科学","哲学","运动","文学","成功励志","保健养生","传记"
		};
		classifyView.setAdapter(typeAdapter);
		typeList=new ArrayList<String>();
		for(int i=0;i<goodsType.length;i++){
			typeList.add(goodsType[i]);
		}
		classifyView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				selectType=goodsType[position];
			//	Log.d("type", selectType);
			}
			
		});
		
		return view;	
	}
	

	
	BaseAdapter typeAdapter=new BaseAdapter() {
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view=null;
			if(convertView==null){
				LayoutInflater inflater=LayoutInflater.from(parent.getContext());
				view =inflater.inflate(R.layout.goodstype_item,null);
			}else{
				view=convertView;
			}
			TextView textView=(TextView) view.findViewById(R.id.goods_type);
			textView.setText(goodsType[position]);
			return view;
		}
		
		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}
		
		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return goodsType[position];
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return goodsType.length;
		}
	};
	
	public String getGoodsType(){
		return selectType;
	}
}
