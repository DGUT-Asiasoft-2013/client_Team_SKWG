package com.example.bbook;

import com.example.bbook.api.widgets.OrderStateTabbarFragment;
import com.example.bbook.api.widgets.TitleBarFragment;
import com.example.bbook.api.widgets.TitleBarFragment.OnGoBackListener;
import com.example.bbook.api.widgets.OrderStateTabbarFragment.OnTabSelectedListener;
import com.example.bbook.fragments.pages.ManageOrderAllFragment;
import com.example.bbook.fragments.pages.ManageOrderToBeCheckFragment;
import com.example.bbook.fragments.pages.ManageOrderToBeCommentFragment;
import com.example.bbook.fragments.pages.ManageOrderToBePayFragment;
import com.example.bbook.fragments.pages.ManageOrderToBeSendFragment;
import com.example.bbook.fragments.pages.OrdersAllFragment;
import com.example.bbook.fragments.pages.OrdersToBeCommentFragment;
import com.example.bbook.fragments.pages.OrdersToBePayFragment;
import com.example.bbook.fragments.pages.OrdersToBeSendFragment;
import com.example.bbook.fragments.pages.OrdersToBoCheckFragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

public class ManageOrderActivity extends Activity {
	TitleBarFragment titleBar;
	OrderStateTabbarFragment tabbar;
	ManageOrderAllFragment contentAll = new ManageOrderAllFragment();
	ManageOrderToBePayFragment contentToBePay = new ManageOrderToBePayFragment();
	ManageOrderToBeSendFragment contentToBeSend = new ManageOrderToBeSendFragment();
	ManageOrderToBeCheckFragment contentToBeCheck = new ManageOrderToBeCheckFragment();
	ManageOrderToBeCommentFragment contentToBeComment = new ManageOrderToBeCommentFragment();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manage_order);
		init();
		setEvent();
	}
	private void setEvent() {
		tabbar.setOnTabSelectedListener(new OnTabSelectedListener() {

			@Override
			public void OnTabSelected(int index) {
				changeContentFragment(index);
			}
		});	
		titleBar.setTitleName("订单管理", 16);
		titleBar.setOnGoBackListener(new OnGoBackListener() {
			
			@Override
			public void onGoBack() {
				finish();
			}
		});
		titleBar.setBtnNextState(false);
	}
	private void init() {
		titleBar = (TitleBarFragment) getFragmentManager().findFragmentById(R.id.title_bar);
		tabbar = (OrderStateTabbarFragment) getFragmentManager().findFragmentById(R.id.tabbar);

	}

	protected void changeContentFragment(int index) {
		Fragment newFrag = null;

		switch(index) {
		case 0 : newFrag = contentAll;break;
		case 1 : newFrag = contentToBePay;break;
		case 2 : newFrag = contentToBeSend; break;
		case 3 : newFrag = contentToBeCheck; break;
		case 4 : newFrag = contentToBeComment; break;
		default: 
			break;
		}
		if(newFrag == null) return;

		getFragmentManager()
		.beginTransaction()
		.replace(R.id.container, newFrag)
		.commit();
	}
}
