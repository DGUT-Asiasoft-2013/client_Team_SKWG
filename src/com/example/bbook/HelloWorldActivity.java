package com.example.bbook;


import com.example.bbook.api.widgets.MainTabbarFragment;
import com.example.bbook.api.widgets.MainTabbarFragment.OnTabSelectedListener;
import com.example.bbook.fragments.pages.ForumFragment;
import com.example.bbook.fragments.pages.HomepageFragment;
import com.example.bbook.fragments.pages.MyProfileFragment;
import com.example.bbook.fragments.pages.ShoppingCartFragment;
import com.example.bbook.fragments.pages.SortFragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;

public class HelloWorldActivity extends Activity {

	HomepageFragment contentHomepage = new HomepageFragment();
	SortFragment contentSort = new SortFragment();
	ForumFragment contentForum= new ForumFragment();
	ShoppingCartFragment contentShopping = new ShoppingCartFragment();
	MyProfileFragment contentMyProfile = new MyProfileFragment();
	MainTabbarFragment tabbar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_helloworld);

		tabbar = (MainTabbarFragment) getFragmentManager().findFragmentById(R.id.frag_tabbar);
		tabbar.setOnTabSelectedListener(new OnTabSelectedListener() {

			@Override
			public void OnTabSelected(int index) {
				changeContentFragment(index);

			}
		}); 
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (tabbar.getSelectedIndex() < 0) {
			tabbar.setSelectedItem(0);
		}

	}


	void changeContentFragment(int index) {
		Fragment newFrag = null;
		
		switch (index) {
		case 0:newFrag = contentHomepage;break;
		case 1:newFrag = contentSort;break;
		case 2:newFrag = contentForum;break;
		case 3:newFrag = contentShopping;break;
		case 4:newFrag = contentMyProfile;break;
		
		default:
			break;
		}
		if(newFrag==null) return;
		
		getFragmentManager()
		.beginTransaction()
		.replace(R.id.contain, newFrag)
		.commit();
	}

}
