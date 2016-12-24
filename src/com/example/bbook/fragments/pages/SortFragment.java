package com.example.bbook.fragments.pages;

import com.example.bbook.R;
import com.example.bbook.api.widgets.TitleBarFragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SortFragment extends Fragment {

	View view;
	TitleBarFragment fragSortTitleBar;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (view==null) {
			view=inflater.inflate(R.layout.fragment_page_sort, null);
			
			fragSortTitleBar = (TitleBarFragment) getFragmentManager().findFragmentById(R.id.sort_titlebar);
			fragSortTitleBar.setBtnBackState(false);
			fragSortTitleBar.setBtnNextState(false);
			fragSortTitleBar.setSplitLineState(false);
			fragSortTitleBar.setTitleName("分类", 16);
			
		}
		return view;
	}
}
