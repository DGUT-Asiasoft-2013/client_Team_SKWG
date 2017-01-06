package com.example.bbook.fragments;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.example.bbook.R;

import android.R.raw;
import android.app.Fragment;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewPagerFragment extends Fragment {
	ViewPager viewPager;
	List<ImageView> imgList;
	List<View>  matBar;
	int cursorIndex=0;
	int dPosition=0;
	
	int bmpWidth=0;
	int offset=0;
ImageView cursor;
	
	
	int [] imageId=new int[]{
			R.drawable.aaa,
			R.drawable.bbb,
			R.drawable.ccc,
			R.drawable.ddd,
			R.drawable.eee
	};
	

	View view;
	ScheduledExecutorService scheduledExecutorService;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view=inflater.inflate(R.layout.fragment_viewpager, null);

		viewPager=(ViewPager) view.findViewById(R.id.viewpager);
	//	cursor=(ImageView) view.findViewById(R.id.cursor);
		imgList=new ArrayList<ImageView>();
		for(int i=0;i<imageId.length;i++){
			ImageView imageView=new ImageView(getActivity());
			imageView.setImageResource(imageId[i]);
			imgList.add(imageView);
		}
		
		matBar=new ArrayList<View>();
		matBar.add(view.findViewById(R.id.dot_0));
		matBar.add( view.findViewById(R.id.dot_1));
		matBar.add( view.findViewById(R.id.dot_2));
		matBar.add( view.findViewById(R.id.dot_3));
		matBar.add( view.findViewById(R.id.dot_4));
		
		
		viewPager.setAdapter(pagerAdapter);
		
		// initCursorPos();  

		 
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

//			int one = offset * 2 + bmpWidth;// 页卡1 -> 页卡2 偏移量  
//			int two = one * 2;// 页卡1 -> 页卡3 偏移量  
			@Override
			public void onPageSelected(int arg0) {
			
				matBar.get(arg0).setBackgroundResource(R.drawable.icon_dot_blue);
				matBar.get(dPosition).setBackgroundResource(R.drawable.icon_dot_black);
				dPosition=arg0;
				cursorIndex=arg0;
				
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});

		return view;
	}
      
	

	 
	PagerAdapter pagerAdapter=new PagerAdapter() {

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0==arg1;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return imgList.size();
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(imgList.get(position));
			return imgList.get(position);
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(imgList.get(position));
		}
	};

    private class MyOnClickListener implements OnClickListener{  
        private int index=0;  
        public MyOnClickListener(int i){  
            index=i;  
        }  
        public void onClick(View v) {  
            viewPager.setCurrentItem(index);              
        }  
          
    }  
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		scheduledExecutorService=Executors.newSingleThreadScheduledExecutor();
		scheduledExecutorService.scheduleWithFixedDelay(new ViewpageChange(), 3, 3,TimeUnit.SECONDS);

	}
	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
			if (scheduledExecutorService != null) {
				scheduledExecutorService.shutdown();
				scheduledExecutorService=null;
			}
	
	}
	public class ViewpageChange implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			cursorIndex = (cursorIndex + 1 ) % imageId.length;
			handler.sendEmptyMessage(0);
		}

	}
	private Handler handler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			viewPager.setCurrentItem(cursorIndex);
		};
	};
}
