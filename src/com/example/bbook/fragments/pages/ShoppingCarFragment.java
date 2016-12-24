package com.example.bbook.fragments.pages;

import com.example.bbook.R;
import com.example.bbook.api.widgets.TitleBarFragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ShoppingCarFragment extends Fragment {

	View view;
	TitleBarFragment fragShopCarTitleBar;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (view==null) {
			view=inflater.inflate(R.layout.fragment_page_shoppingcar, null);
			
			fragShopCarTitleBar = (TitleBarFragment) getFragmentManager().findFragmentById(R.id.shoppingcar_titlebar);
			fragShopCarTitleBar.setBtnBackState(false);
			fragShopCarTitleBar.setBtnNextState(false);
			fragShopCarTitleBar.setSplitLineState(false);
			fragShopCarTitleBar.setTitleName("购物车", 16);
		}
		return view;
	}
}
