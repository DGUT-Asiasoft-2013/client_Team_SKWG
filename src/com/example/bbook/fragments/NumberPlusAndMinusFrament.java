package com.example.bbook.fragments;

import com.example.bbook.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class NumberPlusAndMinusFrament extends Fragment{
	ImageView btnPlus, btnMinus;
	TextView tvQuantity;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_widget_number_plus_sub, null);
		btnPlus = (ImageView) view.findViewById(R.id.btn_plus);
		btnMinus = (ImageView) view.findViewById(R.id.btn_minus);
		tvQuantity = (TextView) view.findViewById(R.id.tv_quantity);
		btnPlus.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onPlusClicked();
			}
		});
		
		btnMinus.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onMinusClicked();
			}
		});
		return view;
	}
	
	public void setQuantityText(String quantityText) {
		tvQuantity.setText(quantityText);
	}
	
	public String getQuantityText() {
		return tvQuantity.getText().toString();
	}
	public static interface OnPlusClickListener {
		void onPlusClicked();
	}
	
	OnPlusClickListener onPlusClickListener;
	
	public void setOnPlusClickListener(OnPlusClickListener onPlusClickListener) {
		this.onPlusClickListener = onPlusClickListener;
	}
	
	void onPlusClicked() {
		if(onPlusClickListener != null) {
			onPlusClickListener.onPlusClicked();
		}
	}
	
	public static interface OnMinusClickListener {
		void onMinusClicked();
	}
	
	OnMinusClickListener onMinusClickListener;
	
	public void setOnMinusClickListener(OnMinusClickListener onMinusClickListener) {
		this.onMinusClickListener = onMinusClickListener;
	}
	
	void onMinusClicked() {
		if(onMinusClickListener != null) {
			onMinusClickListener.onMinusClicked();
		}
	}
	
}
