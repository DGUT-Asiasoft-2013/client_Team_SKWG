package com.example.bbook;
import com.example.bbook.R;
import com.example.bbook.api.Server;
import com.example.bbook.api.Shop;
import com.example.bbook.api.widgets.GoodsPicture;
import com.example.bbook.api.widgets.TitleBarFragment;
import com.example.bbook.api.widgets.TitleBarFragment.OnGoBackListener;

import android.app.Activity;
import android.os.Bundle;
import inputcells.SimpleTextInputcellFragment;

public class ShopSettingActivity extends Activity {
	TitleBarFragment fragTitleBar;
	SimpleTextInputcellFragment fragShopName, fragShopDescription, fragDeliverAddress;
	GoodsPicture imgShop;
	Shop shop;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shop_setting);
		init();
		setEvent();
		shop = (Shop) getIntent().getSerializableExtra("shop");
	}
	private void setEvent() {
		fragTitleBar.setTitleName("店铺设置", 16);
		fragTitleBar.setOnGoBackListener(new OnGoBackListener() {
			
			@Override
			public void onGoBack() {
				finish();
			}
		});
		fragTitleBar.setBtnNextState(false);
	}
	private void init() {
		fragTitleBar = (TitleBarFragment) getFragmentManager().findFragmentById(R.id.title_bar);
		fragShopName = (SimpleTextInputcellFragment) getFragmentManager().findFragmentById(R.id.shop_name);
		fragShopDescription = (SimpleTextInputcellFragment) getFragmentManager().findFragmentById(R.id.shop_description);
		fragDeliverAddress = (SimpleTextInputcellFragment) getFragmentManager().findFragmentById(R.id.shop_deliver_address);
		imgShop = (GoodsPicture) findViewById(R.id.shop_image);
	}
	
	
	
	@Override
	protected void onResume() {
		super.onResume();
		fragShopName.setLabelText("商铺名称");
		fragShopName.setHintText("请输入商店名称");
		fragShopDescription.setLabelText("商店描述");
		fragShopDescription.setHintText("请输入店铺描述");
		fragDeliverAddress.setLabelText("发货地址");
		fragDeliverAddress.setHintText("请输入发货地址");
		if(shop != null) {
			fragShopName.setText(shop.getShopName());
			fragShopDescription.setText(shop.getDescription());
			imgShop.load(Server.serverAdress + shop.getShopImage());
		}
	}
	
}
