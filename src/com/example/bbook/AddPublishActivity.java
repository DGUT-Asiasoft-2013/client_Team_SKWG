package com.example.bbook;

import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import inputcells.SimpleTextInputcellFragment;

public class AddPublishActivity extends Activity {
	SimpleTextInputcellFragment fragGoodsPublisher,fragGoodsAuthor;
	EditText pubDate, priTime;
	ImageButton btn_back;
	Button btn_enter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addpublish);

		fragGoodsPublisher = (SimpleTextInputcellFragment) getFragmentManager()
				.findFragmentById(R.id.input_goods_publisher);
		fragGoodsAuthor = (SimpleTextInputcellFragment) getFragmentManager()
				.findFragmentById(R.id.input_goods_author);
		ImageButton btn_back=(ImageButton)findViewById(R.id.btn_back);
		Button btn_enter=(Button)findViewById(R.id.btn_next);
		
		btn_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
				overridePendingTransition(0, R.anim.slide_out_left);
			}
		});
		btn_enter.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				back();
			}
		});
		// 出版时间
		pubDate = (EditText) findViewById(R.id.edit_pubdate);
		pubDate.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					showPubDatePickDlg();
					return true;
				}
				return false;
			}
		});

		// 印刷时间
		priTime = (EditText) findViewById(R.id.edit_pritime);
		priTime.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					showPriDatePickDlg();
					return true;
				}
				return false;
			}
		});
		
	}

    @Override
    protected void onResume() {
            super.onResume();
            fragGoodsPublisher.setLabelText("出版社");
            fragGoodsPublisher.setHintText("请输入出版社");
            fragGoodsAuthor.setLabelText("作者");
            fragGoodsAuthor.setHintText("请输入作者");
    }
	// 选择出版日期对话框
	protected void showPubDatePickDlg() {
		Calendar calendar = Calendar.getInstance();
		DatePickerDialog datePickerDialog = new DatePickerDialog(AddPublishActivity.this,
				new OnDateSetListener() {

			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				AddPublishActivity.this.pubDate.setText(
						year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
			}
		}, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH));
		datePickerDialog.show();

	}

	// 选择印刷日期对话框
	protected void showPriDatePickDlg() {
		Calendar calendar = Calendar.getInstance();
		DatePickerDialog datePickerDialog = new DatePickerDialog(AddPublishActivity.this,
				new OnDateSetListener() {

			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				AddPublishActivity.this.priTime.setText(
						year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
			}
		}, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH));
		datePickerDialog.show();
	}
	
	void back(){                               //跳回发布商品页面
        String goodsPublisher = fragGoodsPublisher.getText();
        String goodsAuthor = fragGoodsAuthor.getText();
        String goodsPubDate = pubDate.getText().toString();
        String goodsPritime = priTime.getText().toString();
        Intent intent=new Intent();  
        intent.putExtra("goodsPublisher", goodsPublisher);  
        intent.putExtra("goodsAuthor", goodsAuthor);  
        intent.putExtra("goodsPubDate", goodsPubDate);
        intent.putExtra("goodsPritime", goodsPritime);  
        setResult(RESULT_OK, intent);  
        finish();  
	}
}
