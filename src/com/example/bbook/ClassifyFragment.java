package com.example.bbook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.bbook.api.widgets.GoodsPicture;

import android.R.integer;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class ClassifyFragment extends Fragment{
	GridView classifyView;
	List<Map<String, Object>> listItems;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view=inflater.inflate(R.layout.fragment_classify, null);
		classifyView=(GridView) view.findViewById(R.id.classify_gridview);

		int[] imageId=new int[] {R.drawable.icon_youoth,R.drawable.icon_history,R.drawable.icon_computers,
				R.drawable.icon_novel,R.drawable.icon_building,R.drawable.icon_nature,R.drawable.icon_philosophy
				,R.drawable.icon_sport,R.drawable.icon_literature,R.drawable.icon_success,R.drawable.icon_health,R.drawable.icon_biography};

		String[] title=new String[]{
				"青春文学","历史","计算机","小说","建筑","自然科学","哲学","运动","文学","成功励志","保健养生","传记"
		};

		listItems=new ArrayList<Map<String,Object>>();

		for(int i=0;i<imageId.length;i++){
			Map<String,Object> map=new HashMap<String, Object>();
			map.put("image", imageId[i]);
			map.put("title", title[i]);
			listItems.add(map);
		}
//		BaseAdapter classifyAdapter=new SimpleAdapter(getActivity(), listItems, R.layout.classify_item,
//				new String[]{"title","iamge"},new int[]{R.id.id,R.id.picture} );
		classifyView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				onClickedItem(position);
			}
			
		});
		classifyView.setAdapter(classifyAdapter);
		return view;
	}
	
	BaseAdapter classifyAdapter =new BaseAdapter() {
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			View view=null;
			if(convertView==null){
				LayoutInflater inflater=LayoutInflater.from(parent.getContext());
				view =inflater.inflate(R.layout.classify_item,null);
			}else{
				view=convertView;
			}
       TextView classifyName= (TextView) view.findViewById(R.id.id);
       ImageView classifyPicture=(ImageView) view.findViewById(R.id.picture);
       Map<String,Object> map=listItems.get(position);
       
       classifyName.setText(map.get("title").toString());
       classifyPicture.setImageDrawable(getResources().getDrawable(Integer.parseInt(map.get("image").toString())));
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
			return listItems.get(position);
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return listItems.size();
		}
	};
	
	public void onClickedItem(int position){
		Intent intent=new Intent(getActivity(),ClassifyActivity.class);
		Map<String,Object> map=listItems.get(position);
		String title=map.get("title").toString();
		intent.putExtra("title",title);
		startActivity(intent);
	}
}
