package com.example.bbook.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.GridView;
import android.widget.TextView;

public class SlidingMenuFragment extends Fragment {
	View view;
	GridView classifyView;
	String[] goodsType,authorArray,parent;
	List<String> typeList,authorList,groupList;
	String selectType;
	ExpandableListView list;
//	Map<String ,String[]> listMap;
	Map<String ,List<String>> listMap;
//	List<String> childList;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if(view!=null){
			return view;
		}
		view=inflater.inflate(R.layout.fragment_sliding_menu, null);
	//	classifyView=(GridView) view.findViewById(R.id.classify_gridview);

		list=(ExpandableListView) view.findViewById(R.id.expandableListView);
//		listMap=new HashMap<String ,String[]>();
		listMap=new HashMap<String ,List<String>>();

		//	childList=new ArrayList<String>();


		goodsType=new String[]{
				"全部","青春文学","历史","计算机","小说","建筑","自然科学","哲学","运动","文学","成功励志","保健养生","传记"
		};
		authorArray=new String[]{
				"a","b","c","d","e","f","g","h","i"	
		};
		parent=new String[]{
				"1","2"	
		};

		typeList=new ArrayList<>();
		for (int i = 0; i < goodsType.length; i++) {
			
			typeList.add(goodsType[i]);
		}
		authorList=new ArrayList<>();
		for (int i = 0; i < authorArray.length; i++) {
			authorList.add(authorArray[i]);
		}
		groupList=new ArrayList<>();
		for (int i = 0; i < parent.length; i++) {
			groupList.add(parent[i]);
		}
		listMap.put(groupList.get(0),typeList);
		listMap.put(groupList.get(1), authorList);
		
//		classifyView.setAdapter(typeAdapter);
//		typeList=new ArrayList<String>();
//		for(int i=0;i<goodsType.length;i++){
//			typeList.add(goodsType[i]);
//		}
		list.setAdapter(listAdapter);
		
//		classifyView.setOnItemClickListener(new OnItemClickListener(){
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//				// TODO Auto-generated method stub
//				selectType=goodsType[position];
//				//	Log.d("type", selectType);
//			}
//
//		});

		return view;	
	}

	BaseExpandableListAdapter listAdapter=new BaseExpandableListAdapter() {

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public boolean hasStableIds() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View view=null;
			if(convertView==null){
				LayoutInflater inflater = LayoutInflater.from(getActivity());
				view=inflater.inflate(android.R.layout.simple_list_item_1, null);
			}
			else{
				view=convertView;
			}
			TextView textView=(TextView) view.findViewById(android.R.id.text1);
//				textView.setText(SlidingMenuFragment.this.parent[groupPosition]);
			textView.setText(groupList.get(groupPosition));
			return view;
		}

		@Override
		public long getGroupId(int groupPosition) {
			// TODO Auto-generated method stub
			return groupPosition;
		}

		@Override
		public int getGroupCount() {
			// TODO Auto-generated method stub
			return groupList.size();
		}

		@Override
		public Object getGroup(int groupPosition) {
			// TODO Auto-generated method stub
//			return parent[groupPosition];
			return groupList.get(groupPosition);
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			// TODO Auto-generated method stub
//			return listMap.get(parent[groupPosition]).length;
			return listMap.get(groupList.get(groupPosition)).size();
		}
		

		@Override
		public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
			View view=null;
			if(convertView==null){
				LayoutInflater inflater = LayoutInflater.from(getActivity());
				view=inflater.inflate(R.layout.goodstype_item, null);


			}
			else{
				view=convertView;
			}
			
			TextView textView=(TextView) view.findViewById(R.id.goods_type);
//			textView.setText((listMap.get(SlidingMenuFragment.this.parent[groupPosition]))[childPosition]);
textView.setText(listMap.get(groupList.get(groupPosition)).get(childPosition));

			return view;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return childPosition;
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
//			return (listMap.get(parent[groupPosition]))[childPosition];
			return listMap.get(groupList.get(groupPosition)).get(childPosition);
		}
	};


//	BaseAdapter typeAdapter=new BaseAdapter() {
//
//		@Override
//		public View getView(int position, View convertView, ViewGroup parent) {
//			View view=null;
//			if(convertView==null){
//				LayoutInflater inflater=LayoutInflater.from(parent.getContext());
//				view =inflater.inflate(R.layout.goodstype_item,null);
//			}else{
//				view=convertView;
//			}
//			TextView textView=(TextView) view.findViewById(R.id.goods_type);
//			textView.setText(goodsType[position]);
//			return view;
//		}
//
//		@Override
//		public long getItemId(int position) {
//			// TODO Auto-generated method stub
//			return position;
//		}
//
//		@Override
//		public Object getItem(int position) {
//			// TODO Auto-generated method stub
//			return goodsType[position];
//		}
//
//		@Override
//		public int getCount() {
//			// TODO Auto-generated method stub
//			return goodsType.length;
//		}
//	};

	public String getGoodsType(){
		return selectType;
	}
}
