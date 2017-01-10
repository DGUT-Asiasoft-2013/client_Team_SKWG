package com.example.bbook.api.widgets;

import com.example.bbook.R;
import com.example.bbook.api.entity.Orders;
import com.example.bbook.api.widgets.OrderBottomContent.OnCommentClickedListener;
import com.example.bbook.api.widgets.OrderBottomContent.OnConfirmClickedListener;
import com.example.bbook.api.widgets.OrderBottomContent.OnDeleteClickedListener;
import com.example.bbook.api.widgets.OrderBottomContent.OnPayClickedListener;
import com.example.bbook.api.widgets.OrderBottomContent.OnRejectClickedListener;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import util.OrderContent;

public class ManageOrderBottomContent implements OrderContent {
	Orders order;
	Double sum;

	public ManageOrderBottomContent(Orders order, Double sum) {
		this.order = order;
		this.sum = sum;
	}
	@Override
	public int getLayout() {
		return R.layout.list_item_manage_order_bottom;
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
		//		TextView tvConfirm = (TextView) convertView.findViewById(R.id.confirm);
		TextView tvDelete = (TextView) convertView.findViewById(R.id.delete);
		//		TextView tvPay = (TextView) convertView.findViewById(R.id.pay);
		TextView tvSum = (TextView) convertView.findViewById(R.id.sum);
		TextView tvDeliver = (TextView) convertView.findViewById(R.id.deliver);
		TextView tvRefund = (TextView) convertView.findViewById(R.id.refund);
		//		TextView tvReject = (TextView) convertView.findViewById(R.id.reject);
		//		TextView tvComment = (TextView) convertView.findViewById(R.id.comment);
		if(order != null) {
			tvSum.setText("合计:￥" + sum);
			switch (order.getOrdersState()) {
			case 1:
				tvRefund.setVisibility(View.GONE);
				tvDeliver.setVisibility(View.GONE);
				break;
			case 2:
				tvRefund.setVisibility(View.GONE);
				tvDelete.setVisibility(View.GONE);
				tvDeliver.setVisibility(View.GONE);
				break;
			case 3:
				tvRefund.setVisibility(View.GONE);
				tvDelete.setVisibility(View.GONE);
				tvDeliver.setVisibility(View.VISIBLE);
				break;
			case 4:
				tvRefund.setVisibility(View.GONE);
				tvDelete.setVisibility(View.GONE);
				tvDeliver.setVisibility(View.GONE);
				break;
			case 5:
				tvRefund.setVisibility(View.GONE);
				tvDelete.setVisibility(View.VISIBLE);
				tvDeliver.setVisibility(View.GONE);
				break;
			case 6:
				tvRefund.setVisibility(View.GONE);
				tvDelete.setVisibility(View.GONE);
				tvDeliver.setVisibility(View.GONE);
				break;
			case 7:
				tvRefund.setVisibility(View.GONE);
				tvDeliver.setVisibility(View.GONE);
				tvDelete.setVisibility(View.GONE);
				break;
			default:
				break;
			}
			tvDelete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					onDeleteClicked();
				}
			});
			tvRefund.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					onRefundClicked();
				}
			});
			tvDeliver.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					onDeliverClicked();
				}
			});
		}
		return convertView;
	}
	
	public static interface OnDeliverClickedListener {
		void onDeliverClicked();
	}
	
	OnDeliverClickedListener onDeliverClickedListener;
	
	public void setOnDeliverClickedListener(OnDeliverClickedListener onDeliverClickedListener) {
		this.onDeliverClickedListener = onDeliverClickedListener;
	}
	
	void onDeliverClicked() {
		if(onDeliverClickedListener != null) {
			onDeliverClickedListener.onDeliverClicked();
		} 
	}
	
	
	// 退款点击接口
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

	// 确认收货点击接口
	public static interface  OnConfirmClickedListener {
		void onConfirmClicked();
	}

	OnConfirmClickedListener onConfirmClickedListener;

	public void setOnConfirmClickedListener(OnConfirmClickedListener onConfirmClickedListener) {
		this.onConfirmClickedListener = onConfirmClickedListener;
	}

	void onConfirmClicked() {
		if(onConfirmClickedListener != null) {
			onConfirmClickedListener.onConfirmClicked();
		}
	}

	// 支付按钮点击接口
	public static interface OnPayClickedListener {
		void onPayClicked();
	}

	OnPayClickedListener onPayClickedListener;

	public void setOnPayClickedListener(OnPayClickedListener onPayClickedListener) {
		this.onPayClickedListener = onPayClickedListener;
	}

	void onPayClicked() {
		if(onPayClickedListener != null) {
			onPayClickedListener.onPayClicked();
		}
	}

	// 评论按钮点击接口
	public static interface OnCommentClickedListener {
		void onCommentClicked();
	}

	OnCommentClickedListener onCommentClickedListener;

	public void setOnCommentClickedListener(OnCommentClickedListener onCommentClickedListener) {
		this.onCommentClickedListener = onCommentClickedListener;
	}

	void onCommentClicked() {
		if(onCommentClickedListener != null) {
			onCommentClickedListener.onCommentClicked();
		}
	}

	// 删除按钮点击接口
	public static interface OnDeleteClickedListener {
		void onDeleteClicked();
	}

	OnDeleteClickedListener onDeleteClickedListener;

	public void setOnDeleteClickedListener(OnDeleteClickedListener onDeleteClickedListener) {
		this.onDeleteClickedListener = onDeleteClickedListener;
	}

	void onDeleteClicked() {
		if(onDeleteClickedListener != null) {
			onDeleteClickedListener.onDeleteClicked();
		}
	}

	// 退货按钮点击接口
	public static interface OnRejectClickedListener {
		void onRejectClicked();
	}

	OnRejectClickedListener onRejectClickedListener;

	public void setOnRejectClickedListener(OnRejectClickedListener onRejectClickedListener) {
		this.onRejectClickedListener = onRejectClickedListener;
	}

	void onRejectClicked() {
		if(onRejectClickedListener != null) {
			onRejectClickedListener.onRejectClicked();
		}
	}

}
