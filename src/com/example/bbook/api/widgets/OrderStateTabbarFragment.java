package com.example.bbook.api.widgets;

import com.example.bbook.R;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class OrderStateTabbarFragment extends Fragment {
	View tabAll, tabToBePay, tabToBeSend, tabToBeCheck, tabToBeComment;
	View[] tabs;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_widget_order_select_bar, null);

		tabAll = view.findViewById(R.id.tab_all);
		tabToBePay = view.findViewById(R.id.tab_to_be_pay);
		tabToBeSend = view.findViewById(R.id.tab_to_be_send);
		tabToBeCheck = view.findViewById(R.id.tab_to_be_check);
		tabToBeComment = view.findViewById(R.id.tab_to_be_comment);
		tabs = new View[] {
				tabAll, tabToBePay, tabToBeSend, tabToBeCheck, tabToBeComment
		};

		for(final View tab : tabs) {
			tab.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					onTabClicked(tab);
				}
			});
		}

		return view;
	}


	public static interface OnTabSelectedListener{
		void OnTabSelected(int index);
	}

	OnTabSelectedListener onTabSelectedListener;

	public void setOnTabSelectedListener(OnTabSelectedListener onTabSelectedListener) {
		this.onTabSelectedListener = onTabSelectedListener;
	}

	public void setSelectedItem(int index){
		if(index>=0 && index<tabs.length){
			onTabClicked(tabs[index]);
		}
	}

	public int getSelectedIndex(){
		for(int i=0;i<tabs.length;i++){
			if (tabs[i].isSelected()) {
				return i;
			}	
		}
		return -1;
	}

	void onTabClicked(View tab){
		int selectedIndex = -1;
		for(int i=0;i<tabs.length;i++){
			View otherTab = tabs[i];
			if(otherTab == tab){
				otherTab.setSelected(true);
				selectedIndex = i;
			}else{
				otherTab.setSelected(false);
			}
		}

		if(onTabSelectedListener!=null&&selectedIndex>=0){
			onTabSelectedListener.OnTabSelected(selectedIndex);
		}
	}

}
