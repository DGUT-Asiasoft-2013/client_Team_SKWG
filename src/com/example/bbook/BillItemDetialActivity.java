package com.example.bbook;

import com.example.bbook.api.widgets.TitleBarFragment;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class BillItemDetialActivity extends Activity {
TextView txMoneyItem,txMoneyType,txCreateDate,txBillNumber,txRemain,txDetial;
TitleBarFragment titlebar;
float sizeL = 20;
float sizeS = 14;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_bill_list_detial);
		txMoneyItem = (TextView) findViewById(R.id.income_or_cost_moneyitem);
		txMoneyType = (TextView) findViewById(R.id.income_or_cost_type);
		txCreateDate = (TextView) findViewById(R.id.income_or_cost_time);
		txBillNumber = (TextView) findViewById(R.id.income_or_cost_number);
		txRemain = (TextView) findViewById(R.id.income_or_cost_remainmoney);
		txDetial = (TextView) findViewById(R.id.income_or_cost_detial);
		titlebar = (TitleBarFragment) getFragmentManager().findFragmentById(R.id.mybill_detial_titlebar);
		
	}
	
	@Override
		protected void onResume() {
			// TODO Auto-generated method stub
			super.onResume();
			load();
		}
	
	void load(){
		titlebar.setBtnBackState(true);
		titlebar.setBtnNextState(false);
		titlebar.setTitleState(true);
		titlebar.setTitleName("账单详情", 16);
		titlebar.setOnGoBackListener(new TitleBarFragment.OnGoBackListener() {
			@Override
			public void onGoBack() {
				finish();
			}
		});
		
		String moneyItem=getIntent().getStringExtra("MoneyItem");  
		String moneyType=getIntent().getStringExtra("MoneyType");
		String createDate=getIntent().getStringExtra("CreateDate");
		String billNumber=getIntent().getStringExtra("BillNumber");
		String remain=getIntent().getStringExtra("Remain");
		String detial=getIntent().getStringExtra("Detial");
		
		txMoneyItem.setText(moneyItem);
		txMoneyItem.setTextSize(sizeL);
		txMoneyType.setText(moneyType);
		txCreateDate.setText(createDate);
		txBillNumber.setText(billNumber);
		txBillNumber.setTextSize(sizeS);
		txRemain.setText(remain);
		txDetial.setText(detial);
	}
}
