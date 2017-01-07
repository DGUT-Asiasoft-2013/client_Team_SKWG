package com.example.bbook;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.example.bbook.api.widgets.TitleBarFragment;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class BillItemDetialActivity extends Activity {
TextView txMoneyItem,txMoneyType,txCreateDate,txBillNumber,txRemain,txDetial;
TitleBarFragment titlebar;
float sizeL = 20;
float sizeS = 14;
String billNum;

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
		Date createDate=(Date) getIntent().getSerializableExtra("CreateDate");
		String billNumber=getIntent().getStringExtra("BillNumber");
		String remain=getIntent().getStringExtra("Remain");
		String detial=getIntent().getStringExtra("Detial");
		
		
		//将获取的交易单号分割，并转换成10进制；
		String[] temp = null;
		temp = billNumber.split("-");
		String num = "";
		for(int i=0; i<temp.length; i++){
		        num = num + temp[i];
		}
                try {
                        billNum = new BigInteger(num, 16).toString();
                } catch (NumberFormatException e) {
                        e.printStackTrace();
                }
		
		//设置时间格式
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd  hh:mm");
		
		
		txMoneyItem.setText(moneyItem);
		txMoneyItem.setTextSize(sizeL);
		txMoneyType.setText(moneyType);
		txCreateDate.setText(dateFormat.format(createDate));
		txBillNumber.setText(billNum);
		txBillNumber.setLines(1);
		txBillNumber.setTextSize(sizeS);
		txRemain.setText(remain);
		txDetial.setText(detial);
	}
}
