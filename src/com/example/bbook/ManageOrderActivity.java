package com.example.bbook;

import com.example.bbook.api.widgets.OrderStateTabbarFragment;
import com.example.bbook.api.widgets.TitleBarFragment;
import com.example.bbook.api.widgets.TitleBarFragment.OnGoBackListener;
import com.example.bbook.api.widgets.OrderStateTabbarFragment.OnTabSelectedListener;
import com.example.bbook.fragments.pages.ManageOrderAllFragment;
import com.example.bbook.fragments.pages.OrdersAllFragment;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.PopupWindow.OnDismissListener;

public class ManageOrderActivity extends Activity {
	TitleBarFragment titleBar;
	OrderStateTabbarFragment tabbar;
	PopupWindow pWindow;
	ManageOrderAllFragment contentAll = new ManageOrderAllFragment();
	ManageOrderAllFragment contentToBePay = new ManageOrderAllFragment(2);
	ManageOrderAllFragment contentToBeSend = new ManageOrderAllFragment(3);
	ManageOrderAllFragment contentToBeCheck = new ManageOrderAllFragment(4);
	ManageOrderAllFragment contentToBeComment = new ManageOrderAllFragment(5);
	View view;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		view = View.inflate(this, R.layout.activity_manage_order, null);
		setContentView(view);
		init();
		setEvent();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (tabbar.getSelectedIndex() < 0) {
			tabbar.setSelectedItem(0);
		}
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
		case 4 : showPopWindow(); break;
		default: 
			break;
		}
		if(newFrag == null) return;

		getFragmentManager()
		.beginTransaction()
		.replace(R.id.container, newFrag)
		.commit();
	}

	private void showPopWindow() {
		
		LayoutInflater inflater = LayoutInflater.from(this);
		View view = inflater.inflate(R.layout.popwindow_order_state, null);
		TextView tvToBeComment = (TextView) view.findViewById(R.id.to_be_comment);
		TextView tvToBeRefunded = (TextView) view.findViewById(R.id.to_be_refunded);
		pWindow = new PopupWindow(this);
		pWindow.setHeight(LayoutParams.WRAP_CONTENT);
		pWindow.setWidth(LayoutParams.WRAP_CONTENT);
		pWindow.setFocusable(true);
		pWindow.setOutsideTouchable(true);
		pWindow.showAsDropDown(findViewById(R.id.more));


	}
}
