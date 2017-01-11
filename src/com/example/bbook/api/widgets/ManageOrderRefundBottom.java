package com.example.bbook.api.widgets;

import com.example.bbook.R;
import com.example.bbook.api.entity.Orders;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import util.OrderContent;

public class ManageOrderRefundBottom implements OrderContent{
	Orders order;
	Double sum;

	public ManageOrderRefundBottom(Orders order, Double sum) {
		this.order = order;
		this.sum = sum;
	}
	@Override
	public int getLayout() {
		return R.layout.list_item_manage_order_refund_bottom;
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
		TextView tvRefund = (TextView) convertView.findViewById(R.id.refund);
		if(order != null) {
			tvSum.setText("合计:￥" + sum);
			switch (order.getOrdersState()) {
			case 6:
				tvCheck.setVisibility(View.GONE);
				tvRefund.setVisibility(View.VISIBLE);
				break;
			case 7:
				tvCheck.setVisibility(View.VISIBLE);
				tvRefund.setVisibility(View.GONE);
				break;
			default:
				break;
			}
			
			tvRefund.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					onRefundClicked();
				}
			});
			
			tvCheck.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					onCheckClicked();
				}
			});
		}
		return convertView;
	}
	
	public static interface OnRefundClickedListener {
		void onRefundClicked();
	}
	
	OnRefundClickedListener onRefundClickedListener;
	
	public void setOnRefundClickedListener(OnRefundClickedListener onRefundClickedListener) {
		this.onRefundClickedListener = onRefundClickedListener;
	}
	
	void onRefundClicked() {
		if(onRefundClickedListener != null) {
			onRefundClickedListener.onRefundClicked();
		}
	}
	
	public static interface OnCheckClickedListener {
		void onCheckClicked();
	}
	
	OnCheckClickedListener onCheckClickedListener;
	
	public void setOnCheckClickedListener(OnCheckClickedListener onCheckClickedListener) {
		this.onCheckClickedListener = onCheckClickedListener;
	}
	
	void onCheckClicked() {
		if(onCheckClickedListener != null) {
			onCheckClickedListener.onCheckClicked();
		}
	}


}
