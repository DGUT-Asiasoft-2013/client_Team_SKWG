package com.example.bbook;


import com.example.bbook.api.widgets.MainTabbarFragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;

public class HelloWorldActivity extends Activity {

	MainTabbarFragment tabbar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_helloworld);

		tabbar = (MainTabbarFragment) getFragmentManager().findFragmentById(R.id.frag_tabbar);
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (tabbar.getSelectedIndex() < 0) {
			tabbar.setSelectedItem(0);
		}

	}

	
	void changeContent(int index) {
		Fragment newFrag = new Fragment();
		switch (index) {
		case 0:
			break;
		case 1:
			break;
		case 2:
			break;
		case 3:
			break;
		default:
			break;
		}

		if (newFrag == null) {
			return;
		}
		getFragmentManager().beginTransaction().replace(R.id.contain, newFrag).commit();
	}

}
