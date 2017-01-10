package com.example.bbook.api.widgets;

import com.example.bbook.R;
import com.example.bbook.api.entity.Orders;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import util.OrderContent;

public class OrderRefundBottomContent implements OrderContent {
	Orders order;
	Double sum;

	public OrderRefundBottomContent(Orders order, Double sum) {
		this.order = order;
		this.sum = sum;
	}
	@Override
	public int getLayout() {
		return R.layout.list_item_refund_order_bottom;
	}

	@Override
	public boolean isClickable() {
		return false;
	}

	public Orders getOrder() {
		return order;
	}

	@Override
	public View getView(final Context context, View convertView, LayoutInflater inflate) {
		inflate = LayoutInflater.from(context);
		convertView = inflate.inflate(getLayout(), null);
		TextView tvSum = (TextView) convertView.findViewById(R.id.sum);
		TextView tvCheck = (TextView) convertView.findViewById(R.id.check);
		if(order != null) {
			tvSum.setText("合计:￥" + sum);
			switch (order.getOrdersState()) {
			case 6:
				tvCheck.setVisibility(View.GONE);
				break;
			case 7:
				tvCheck.setVisibility(View.VISIBLE);
				break;
			default:
				break;
			}
		}
		return convertView;
	}
	


}
