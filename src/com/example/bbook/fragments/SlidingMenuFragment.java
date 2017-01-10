package com.example.bbook.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.bbook.R;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.TextView;

public class SlidingMenuFragment extends Fragment  implements OnItemClickListener,OnClickListener{
	View view;
	GridView classifyView,authorView;
	String[] goodsType,authorArray,parent;
	List<String> typeList,authorList,groupList;
	String selectType;
	ExpandableListView list;
	Map<String ,List<String>> listMap;
	List<String> childList;
	//	List<String> childList;
	boolean isTypeShow=false;
	TextView typeView;

	String typeStr,authorStr;
	boolean isTypeSelected=false,isAuthorSelected=false;
	int gPosition=-1;
	//按金额搜索
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if(view!=null){
			return view;
		}
		view=inflater.inflate(R.layout.fragment_sliding_menu, null);



		list=(ExpandableListView) view.findViewById(R.id.expandableListView);
		listMap=new HashMap<String ,List<String>>();


		goodsType=new String[]{
				"全部","青春文学","历史","计算机","小说","建筑","自然科学","哲学","运动","文学","成功励志","保健养生","传记"
		};
		authorArray=new String[]{
				"全部","a","b","c","d","e","f","g","h","i"	
		};
		parent=new String[]{
				"类型","作者"	
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
		typeList=new ArrayList<String>();


		for(int i=0;i<goodsType.length;i++){
			typeList.add(goodsType[i]);
		}
		list.setAdapter(listAdapter);


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
			textView.setText(groupList.get(groupPosition));
			return view;
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public int getGroupCount() {
			return groupList.size();
		}

		@Override
		public Object getGroup(int groupPosition) {
			return groupList.get(groupPosition);
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return 1;
		}
		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return listMap.get(groupList.get(groupPosition)).get(childPosition);
		}

		@Override
		public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
				ViewGroup parent) {
			View view=null;
			if(convertView==null){
				LayoutInflater inflater = LayoutInflater.from(getActivity());
				view=inflater.inflate(R.layout.child_gridview, null);
			}
			else{
				view=convertView;
			}
			int size =listMap.get(groupList.get(groupPosition)).size();
			childList= new ArrayList<String>();
			for (int i = 0; i < size; i++) {
				childList.add(listMap.get(groupList.get(groupPosition)).get(i));
				Log.d("child", childList.get(i));
			}
			gPosition=groupPosition;
			//Log.d("group", groupPosition+"");
			//	Log.d("child", childPosition+"");

			GridView gridView=(GridView) view.findViewById(R.id.child_view);
			//gridView.setNumColumns(10);
			gridView.setHorizontalSpacing(10);

			LayoutParams lp = gridView.getLayoutParams();
			//			lp.height = (int) ((childAdapter.getCount()/3+1) * (getResources().getDisplayMetrics().density * 20.0));
			lp.height = (int) ((childAdapter.getCount()/3+1) * (getResources().getDisplayMetrics().density * 15.0));
			gridView.setLayoutParams(lp);

			gridView.setAdapter(childAdapter);
			gridView.setOnItemClickListener(SlidingMenuFragment.this);
			return view;
		}


	};
	BaseAdapter childAdapter=new BaseAdapter() {

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView==null){
				LayoutInflater inflater = LayoutInflater.from(parent.getContext());
				convertView = inflater.inflate(R.layout.goodstype_item, null);
			}
			TextView textView=(TextView) convertView.findViewById(R.id.goods_type);
			textView.setText(childList.get(position));
			return convertView;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return childList.get(position);
		}

		@Override
		public int getCount() {
			Log.d("count",childList.size()+"");
			return childList.size();
		}
	};



public void cleanBackColor(int position){
	
}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
	
		
		switch (gPosition) {
		case 0:
			typeStr=childList.get(position);
			isTypeSelected=true;
			isAuthorSelected=false;
			break;
		case 1:
			authorStr=childList.get(position);
			isAuthorSelected=true;
			isTypeSelected=false;
		default:
			break;
		}
	}
	public boolean getTypeSelected(){
		return isTypeSelected;
	}
	public boolean getAuthorSelected(){
		return isAuthorSelected;
	}
	
	public String getTypeStr(){
		return typeStr;
	}
	public String getAuthorStr(){
		return authorStr;
	}

	@Override
	public void onClick(View v) {
	
		// TODO Auto-generated method stub
		
	}
}
