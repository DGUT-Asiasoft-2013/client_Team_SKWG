package com.example.bbook.fragments;

import com.example.bbook.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

public class SlidingMenuFragment extends Fragment{
	View view;


	ExpandableListView list;
	
	String[] goodsType = {
			"全部","青春文学","历史","计算机","小说","建筑","自然科学","哲学","运动","文学","成功励志","保健养生","传记"
	};
	
	String[] authorArray = {
			"全部","a","b","c","d","e","f","g","h","i"	
	};
	
	String[] groups = {
			"类型","作者"	
	};
	
	//	List<String> childList;
	boolean isTypeShow=false;
	TextView typeView;

	String typeStr,authorStr;
	boolean isTypeSelected=false,isAuthorSelected=false;
	boolean isSelected=false;
	//按金额搜索
	private int sign = -1;// 控制列表的展开
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if(view!=null){
			return view;
		}
		view=inflater.inflate(R.layout.fragment_sliding_menu, null);



		list=(ExpandableListView) view.findViewById(R.id.expandableListView);
		list.setAdapter(groupsAdapter);

//		list.setOnGroupClickListener(new OnGroupClickListener() {
//
//			@Override
//			public boolean onGroupClick(ExpandableListView parent, View v,
//					int groupPosition, long id) {
//				if (sign == -1) {
//					// 展开被选的group
//					list.expandGroup(groupPosition);
//					sign = groupPosition;
//				} else if (sign == groupPosition) {
//					list.collapseGroup(sign);
//					sign = -1;
//				} else {
//					list.collapseGroup(sign);
//					// 展开被选的group
//					list.expandGroup(groupPosition);
//					sign = groupPosition;
//				}
//				return true;
//			}
//		});

		return view;	
	}

	BaseExpandableListAdapter groupsAdapter=new BaseExpandableListAdapter() {
		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public boolean hasStableIds() {
			// TODO Auto-generated method stub
			return true;
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
			textView.setText(groups[groupPosition]);
			return view;
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public int getGroupCount() {
			return groups.length;
		}

		@Override
		public Object getGroup(int groupPosition) {
			return groups[groupPosition];
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
			switch (groupPosition) {
			case 0:
				return goodsType[childPosition];

			case 1:
				return authorArray[childPosition];
				
			default:
				return null;
			}
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


			int size = 0;
			BaseAdapter childAdapter = null;
			
			switch (groupPosition) {
			case 0:
				size = goodsType.length;
				childAdapter = goodsAdapter;
				break;
			
			case 1:
				size = authorArray.length;
				childAdapter = authorAdapter;
				break;

			default:
				size = 0;
				break;
			}

			final int gPosition = groupPosition;
			//Log.d("group", groupPosition+"");
			//	Log.d("child", childPosition+"");

			
			
			GridView gridView=(GridView) view.findViewById(R.id.child_view);
			//gridView.setNumColumns(10);
			gridView.setHorizontalSpacing(10);

			LayoutParams lp = gridView.getLayoutParams();
			lp.height = (int) ((childAdapter.getCount()/3+1) * (getResources().getDisplayMetrics().density * 15.0));
			gridView.setLayoutParams(lp);

			gridView.setAdapter(childAdapter);
			gridView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					SlidingMenuFragment.this.onItemClick(gPosition, position);
				}
			});
			return view;
		}


	};

	BaseAdapter goodsAdapter=new BaseAdapter() {
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView==null){
				LayoutInflater inflater = LayoutInflater.from(parent.getContext());
				convertView = inflater.inflate(R.layout.goodstype_item, null);
			}
			TextView textView=(TextView) convertView.findViewById(R.id.goods_type);
			textView.setText(goodsType[position]);
			return convertView;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public Object getItem(int position) {
			return goodsType[position];
		}

		@Override
		public int getCount() {
			return goodsType.length;
		}
	};

	BaseAdapter authorAdapter=new BaseAdapter() {
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView==null){
				LayoutInflater inflater = LayoutInflater.from(parent.getContext());
				convertView = inflater.inflate(R.layout.goodstype_item, null);
			}
			TextView textView=(TextView) convertView.findViewById(R.id.goods_type);
			textView.setText(authorArray[position]);
			return convertView;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public Object getItem(int position) {
			return authorArray[position];
		}

		@Override
		public int getCount() {
			return authorArray.length;
		}
	};

	public void cleanBackColor(int position){

	}

	public void onItemClick(int gPosition, int position) {
		// TODO Auto-generated method stub


		switch (gPosition) {
		case 0:
			typeStr=goodsType[position];
			getActivity().runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					Toast.makeText(getActivity(), typeStr, Toast.LENGTH_SHORT).show();
				}
			});
			isSelected=true;
			isTypeSelected=true;
			break;
		case 1:
			authorStr=authorArray[position];
			getActivity().runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					Toast.makeText(getActivity(), authorStr, Toast.LENGTH_SHORT).show();
				}
			});
			isSelected=true;
			isAuthorSelected=true;
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
	public boolean getIsSelected(){
		return isSelected;
	}
	public String getTypeStr(){
		return typeStr;
	}
	public String getAuthorStr(){
		return authorStr;
	}
}
