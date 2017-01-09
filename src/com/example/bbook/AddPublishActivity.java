package com.example.bbook;

import java.util.Calendar;

import com.example.bbook.api.entity.CommomInfo;
import com.example.bbook.api.entity.PublishInfo;

import android.app.Activity;
import android.app.AlertDialog;
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
	PublishInfo publishinfo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addpublish);
	
		fragGoodsPublisher = (SimpleTextInputcellFragment) getFragmentManager()
				.findFragmentById(R.id.input_goods_publisher);
		fragGoodsAuthor = (SimpleTextInputcellFragment) getFragmentManager()
				.findFragmentById(R.id.input_goods_author);
		pubDate = (EditText) findViewById(R.id.edit_pubdate);
		priTime = (EditText) findViewById(R.id.edit_pritime);

		publishinfo = (PublishInfo) getIntent().getSerializableExtra("publishinfo");

//		if (publishinfo.getGoodsPublisher()!= null || publishinfo.getGoodsPublisher().length() > 0) {   //出版社不为空
			fragGoodsPublisher.setText(publishinfo.getGoodsPublisher());
//		}
//		if (publishinfo.getGoodsAuthor()!= null || publishinfo.getGoodsAuthor().length() > 0) {          //作者不为空
			fragGoodsAuthor.setText(publishinfo.getGoodsAuthor());
//		}
//		if (publishinfo.getGoodsPubDate()!= null || publishinfo.getGoodsPubDate().length() > 0) {          //出版日期不为空
			pubDate.setText(publishinfo.getGoodsPubDate());
//		}
//		if (publishinfo.getGoodsPritime()!= null || publishinfo.getGoodsPritime().length() > 0) {          //印刷日期不为空
			priTime.setText(publishinfo.getGoodsPritime());
//		}

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
        if((goodsPublisher==null||goodsPublisher.length()<=0)&&(goodsAuthor==null||goodsAuthor.length()<=0)
        		&&(goodsPubDate==null||goodsPubDate.length()<=0)&&(goodsPritime==null||goodsPritime.length()<=0)){             //输入都为空
        	new AlertDialog.Builder(this)
        	.setTitle("温馨提示")
        	.setMessage("输入不能全部为空，取消编辑请左上角返回")
        	.setPositiveButton("好的", null)
        	.show();
        	return;
        }else{
        Intent intent=new Intent();  
        intent.putExtra("goodsPublisher", goodsPublisher);  
        intent.putExtra("goodsAuthor", goodsAuthor);  
        intent.putExtra("goodsPubDate", goodsPubDate);
        intent.putExtra("goodsPritime", goodsPritime);
        setResult(RESULT_OK, intent);  
        finish();  
        }
	}
}
