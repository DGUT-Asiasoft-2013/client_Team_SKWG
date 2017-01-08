package com.example.bbook.api.widgets;

import java.util.List;

import com.example.bbook.R;
import com.example.bbook.api.Server;
import com.example.bbook.api.entity.Orders;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import util.OrderContent;

public class OrderMiddleContent implements OrderContent {

	Orders order;
	
	public OrderMiddleContent(Orders order) {
		this.order = order;
	}
	
	@Override
	public int getLayout() {
		return R.layout.list_item_order_middle;
	}

	@Override
	public boolean isClickable() {
		return true;
	}

	@Override
	public View getView(Context context, View convertView, LayoutInflater inflate) {
		inflate = LayoutInflater.from(context);
		convertView = inflate.inflate(getLayout(), null);
		GoodsPicture imgGoods = (GoodsPicture) convertView.findViewById(R.id.goods_image);
		TextView tvGoodsName = (TextView) convertView.findViewById(R.id.goods_name);
		TextView tvGoodsType = (TextView) convertView.findViewById(R.id.goods_type);
		TextView tvGoodsPrice = (TextView) convertView.findViewById(R.id.goods_price);
		TextView tvGoodsCount = (TextView) convertView.findViewById(R.id.goods_count);
		if(order != null) {
			tvGoodsName.setText(order.getGoods().getGoodsName());
			tvGoodsType.setText(order.getGoods().getGoodsType());
			tvGoodsPrice.setText(order.getGoods().getGoodsPrice());
			tvGoodsCount.setText("x" + order.getGoodsQTY());
			imgGoods.load(Server.serverAdress + order.getGoods().getGoodsImage());
		}
		
		return convertView;
	}

}
