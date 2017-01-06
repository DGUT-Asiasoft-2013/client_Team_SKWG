package com.example.bbook;

import java.text.SimpleDateFormat;

import com.example.bbook.api.Server;
import com.example.bbook.api.entity.Orders;
import com.example.bbook.api.widgets.AvatarView;
import com.example.bbook.api.widgets.GoodsPicture;
import com.example.bbook.api.widgets.TitleBarFragment;
import com.example.bbook.api.widgets.TitleBarFragment.OnGoBackListener;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class OrderDetailActivity extends Activity {
	TitleBarFragment fragTitleBar;
	TextView tvState, tvName, tvTel, tvAddress, tvShopName, tvGoodsName, tvGoodsType, tvGoodsPrice,
	tvGoodsQuantity, tvSum, tvOrderId, tvCreateDate, tvPayDate, tvDeliverDate,tvCompleteData;
	GoodsPicture imgGoods;
	AvatarView shopAvatar;
	
	Orders order;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_detail);
		init();
		setEvent();
		order = (Orders) getIntent().getSerializableExtra("order");
		if(order != null) {
			setText();
		}
	}
	void setText() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		tvState.setText(order.getOrdersState() + "");
		tvName.setText(order.getBuyerName());
		tvTel.setText(order.getBuyerPhoneNum());
		tvAddress.setText(order.getBuyerAddress());
		tvShopName.setText(order.getGoods().getShop().getShopName());
		tvGoodsName.setText(order.getGoods().getGoodsName());
		tvGoodsType.setText(order.getGoods().getGoodsType());
		tvGoodsPrice.setText("￥" + order.getGoods().getGoodsPrice());
		tvGoodsQuantity.setText("x" + order.getGoodsQTY());
		tvSum.setText("合计： ￥ " + Double.parseDouble(order.getGoods().getGoodsPrice()) * Integer.parseInt(order.getGoodsQTY()));
		tvOrderId.setText("订单编号:  " + order.getOrdersID());
		tvCreateDate.setText("创建时间:  " + format.format(order.getCreateDate()));
		if(order.getPayDate() != null) {
			tvPayDate.setText("付款时间:  " + format.format(order.getPayDate()));
		}
		if(order.getDeliverDate() != null) {
			tvDeliverDate.setText("发货时间:  " + format.format(order.getDeliverDate()));
		}
		if(order.getCompleteDate() != null) {
			tvCompleteData.setText("成交时间:  " + format.format(order.getCompleteDate())); 
		}
		shopAvatar.load(Server.serverAdress + order.getGoods().getShop().getShopImage());
		imgGoods.load(Server.serverAdress + order.getGoods().getGoodsImage());
	}
	void init() {
		fragTitleBar = (TitleBarFragment) getFragmentManager().findFragmentById(R.id.title_bar);
		tvState = (TextView) findViewById(R.id.state);
		tvName = (TextView) findViewById(R.id.name);
		tvTel = (TextView) findViewById(R.id.tel);
		tvAddress = (TextView) findViewById(R.id.address);
		tvShopName = (TextView) findViewById(R.id.shop_name);
		tvGoodsName = (TextView) findViewById(R.id.goods_name);
		tvGoodsType = (TextView) findViewById(R.id.goods_type);
		tvGoodsPrice = (TextView) findViewById(R.id.goods_price);
		tvGoodsQuantity = (TextView) findViewById(R.id.goods_quantity);
		tvSum = (TextView) findViewById(R.id.sum);
		tvOrderId = (TextView) findViewById(R.id.order_id);
		tvCreateDate = (TextView) findViewById(R.id.create_date);
		tvPayDate = (TextView) findViewById(R.id.pay_date);
		tvDeliverDate = (TextView) findViewById(R.id.delever_date);
		tvCompleteData = (TextView) findViewById(R.id.complete_date);
		imgGoods = (GoodsPicture) findViewById(R.id.goods_image);
		shopAvatar = (AvatarView) findViewById(R.id.shop_avatar);
	}
	
	void setEvent() {
		fragTitleBar.setTitleName("订单详情", 16);
		fragTitleBar.setOnGoBackListener(new OnGoBackListener() {
			
			@Override
			public void onGoBack() {
				finish();
			}
		});
		fragTitleBar.setBtnNextState(false);
	}
}
