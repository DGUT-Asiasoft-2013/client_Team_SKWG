package com.example.bbook.api.widgets;

import com.example.bbook.R;
import com.example.bbook.api.entity.Orders;

import android.content.Context;
import android.text.InputFilter.LengthFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import util.OrderContent;

public class OrderTopContent implements OrderContent {

	Orders order;
	
	public OrderTopContent(Orders order) {
		this.order = order;
	} 
	
	@Override
	public int getLayout() {
		return R.layout.list_item_order_top;
	}

	@Override
	public boolean isClickable() {
		return true;
	}

	@Override
	public View getView(Context context, View convertView, LayoutInflater inflate) {
		inflate = LayoutInflater.from(context);
		convertView = inflate.inflate(getLayout(), null);
		TextView tvOrderId = (TextView) convertView.findViewById(R.id.order_id);
		TextView tvOrderState = (TextView) convertView.findViewById(R.id.order_state);
		if(order != null) {
			tvOrderId.setText(order.getOrdersID());
			tvOrderState.setText(order.getOrdersState() + "");
		}
		return convertView;
	}

}
