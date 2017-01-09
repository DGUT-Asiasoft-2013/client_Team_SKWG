package com.example.bbook.api.widgets;

import java.io.IOException;

import com.example.bbook.R;
import com.example.bbook.api.Server;
import com.example.bbook.api.entity.Orders;
import com.example.bbook.fragments.pages.OrdersAllFragment;
import com.fasterxml.jackson.databind.util.ClassUtil.Ctor;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.Response;
import util.OrderContent;

public class OrderBottomContent implements OrderContent {
	Orders order;
	Double sum;

	public OrderBottomContent(Orders order, Double sum) {
		this.order = order;
		this.sum = sum;
	}
	@Override
	public int getLayout() {
		return R.layout.list_item_order_bottom;
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
		TextView tvConfirm = (TextView) convertView.findViewById(R.id.confirm);
		TextView tvDelete = (TextView) convertView.findViewById(R.id.delete);
		TextView tvPay = (TextView) convertView.findViewById(R.id.pay);
		TextView tvSum = (TextView) convertView.findViewById(R.id.sum);
		TextView tvReject = (TextView) convertView.findViewById(R.id.reject);
		TextView tvComment = (TextView) convertView.findViewById(R.id.comment);
		if(order != null) {
			tvSum.setText("合计:￥" + sum);
			switch (order.getOrdersState()) {
			case 1:
				tvConfirm.setVisibility(View.GONE);
				tvPay.setVisibility(View.GONE);
				tvReject.setVisibility(View.GONE);
				tvDelete.setVisibility(View.VISIBLE);
				tvComment.setVisibility(View.GONE);
				break;
			case 2:
				tvConfirm.setVisibility(View.GONE);
				tvPay.setVisibility(View.VISIBLE);
				tvReject.setVisibility(View.GONE);
				tvDelete.setVisibility(View.VISIBLE);
				tvComment.setVisibility(View.GONE);
				break;
			case 3:
				tvConfirm.setVisibility(View.GONE);
				tvPay.setVisibility(View.GONE);
				tvReject.setVisibility(View.GONE);
				tvDelete.setVisibility(View.GONE);
				tvComment.setVisibility(View.GONE);
				break;
			case 4:
				tvConfirm.setVisibility(View.VISIBLE);
				tvPay.setVisibility(View.GONE);
				tvReject.setVisibility(View.VISIBLE);
				tvDelete.setVisibility(View.GONE);
				tvComment.setVisibility(View.GONE);
				break;
			case 5:
				tvConfirm.setVisibility(View.GONE);
				tvPay.setVisibility(View.GONE);
				tvReject.setVisibility(View.GONE);
				tvDelete.setVisibility(View.GONE);
				tvComment.setVisibility(View.VISIBLE);
				break;
			case 6:
				tvConfirm.setVisibility(View.GONE);
				tvPay.setVisibility(View.GONE);
				tvReject.setVisibility(View.VISIBLE);
				tvDelete.setVisibility(View.GONE);
				tvComment.setVisibility(View.GONE);
				break;
			case 7:
				tvConfirm.setVisibility(View.GONE);
				tvPay.setVisibility(View.GONE);
				tvReject.setVisibility(View.GONE);
				tvDelete.setVisibility(View.GONE);
				tvComment.setVisibility(View.GONE);
				break;
			default:
				break;
			}
			tvConfirm.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					onConfirmClicked();
				}
			});
			tvDelete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					onDeleteClicked();
				}
			});
			tvPay.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					onPayClicked();
				}
			});
			tvComment.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					onCommentClicked();
				}
			});
			tvReject.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					onRejectClicked();
				}
			});
		}
		return convertView;
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

