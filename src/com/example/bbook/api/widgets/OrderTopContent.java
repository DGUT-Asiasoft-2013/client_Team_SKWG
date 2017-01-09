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
			tvOrderId.setText("订单号:" + order.getOrdersID());
			int orderState = order.getOrdersState();
			switch (orderState) {
			case 1:
				tvOrderState.setText("已评价");
				break;
			case 2:
				tvOrderState.setText("待付款");
				break;
			case 3:
				tvOrderState.setText("待发货");
				break;
			case 4:
				tvOrderState.setText("待收货");
				break;
			case 5:
				tvOrderState.setText("待评价");
				break;
			case 6:
				tvOrderState.setText("待退款");
				break;
			case 7:
				tvOrderState.setText("已退款");
				break;
			default:
				break;
			}
		}
		return convertView;
	}

}
