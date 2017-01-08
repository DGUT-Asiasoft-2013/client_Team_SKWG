package com.example.bbook.api.widgets;

import com.example.bbook.R;
import com.example.bbook.api.entity.Orders;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;
import util.OrderContent;

public class OrderBottomContent implements OrderContent {

	Orders order;

	public OrderBottomContent(Orders order) {
		this.order = order;
	}
	@Override
	public int getLayout() {
		return R.layout.list_item_order_bottom;
	}

	@Override
	public boolean isClickable() {
		return false;
	}

	@Override
	public View getView(final Context context, View convertView, LayoutInflater inflate) {
		inflate = LayoutInflater.from(context);
		convertView = inflate.inflate(getLayout(), null);
		TextView tvConfirm = (TextView) convertView.findViewById(R.id.confirm);
		TextView tvDelete = (TextView) convertView.findViewById(R.id.delete);
		TextView tvPay = (TextView) convertView.findViewById(R.id.pay);

		if(order != null) {
			tvConfirm.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Toast.makeText(context, "confirm", Toast.LENGTH_SHORT).show();
				}
			});
			tvDelete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Toast.makeText(context, "tvDelete", Toast.LENGTH_SHORT).show();
				}
			});
			tvPay.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Toast.makeText(context, "tvPay", Toast.LENGTH_SHORT).show();
				}
			});
		}
		return convertView;
	}

}
