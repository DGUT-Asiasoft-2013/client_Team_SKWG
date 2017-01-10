package com.example.bbook;

import java.io.IOException;
import java.util.List;

import org.w3c.dom.Text;

import com.example.bbook.api.Page;
import com.example.bbook.api.Server;
import com.example.bbook.api.entity.Orders;
import com.example.bbook.api.widgets.GoodsPicture;
import com.example.bbook.api.widgets.OrderStateTabbarFragment;
import com.example.bbook.api.widgets.OrderStateTabbarFragment.OnTabSelectedListener;
import com.example.bbook.api.widgets.TitleBarFragment.OnGoBackListener;
import com.example.bbook.api.widgets.TitleBarFragment.OnGoNextListener;
import com.example.bbook.api.widgets.TitleBarFragment;
import com.example.bbook.fragments.pages.OrdersAllFragment;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore.Video;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MyOrdersActivity extends Activity {
	TitleBarFragment titleBar;
	OrderStateTabbarFragment tabbar;
	OrdersAllFragment contentAll = new OrdersAllFragment();
	OrdersAllFragment contentToBePay = new  OrdersAllFragment(2);
	OrdersAllFragment contentToBeSend = new  OrdersAllFragment(3);
	OrdersAllFragment contentToBeCheck = new  OrdersAllFragment(4);
	OrdersAllFragment contentToBeComment = new  OrdersAllFragment(5);
	List<Orders> ordersData;
	int Page=0;
	ListView list;
	TextView orderState;
	TextView shopName;
	ImageView ordersDelete;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_orders);	
		init();
		setEvent();

	}

	private void setEvent() {
		titleBar = (TitleBarFragment) getFragmentManager().findFragmentById(R.id.title_bar);
		titleBar.setTitleName("我的订单", 18);
		titleBar.setBtnNextText("退货/售后", 13);
		titleBar.setOnGoNextListener(new OnGoNextListener() {
			
			@Override
			public void onGoNext() {
				Intent itnt = new Intent(MyOrdersActivity.this, MyRefundOrderActivity.class);
				startActivity(itnt);
			}
		});
		// 返回
		titleBar.setOnGoBackListener(new OnGoBackListener() {

			@Override
			public void onGoBack() {
				finish();
			}
		});

		tabbar.setOnTabSelectedListener(new OnTabSelectedListener() {

			@Override
			public void OnTabSelected(int index) {
				changeContentFragment(index);
			}
		});
	}

	private void init() {
		list = (ListView) findViewById(R.id.list);
		tabbar = (OrderStateTabbarFragment) getFragmentManager().findFragmentById(R.id.frag_tabbar);
		titleBar = (TitleBarFragment) getFragmentManager().findFragmentById(R.id.title_bar);		
	}

	protected void changeContentFragment(int index) {
		Fragment newFrag = null;

		switch(index) {
		case 0 : newFrag = contentAll;break;
		case 1 : newFrag = contentToBePay;break;
		case 2 : newFrag = contentToBeSend; break;
		case 3 : newFrag = contentToBeCheck; break;
		case 4 : newFrag = contentToBeComment; break;
		default: 
			break;
		}
		if(newFrag == null) return;

		getFragmentManager()
		.beginTransaction()
		.replace(R.id.container, newFrag)
		.commit();
	}

	static void text(Context context) {
		Toast.makeText(context, "aaa", Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (tabbar.getSelectedIndex() < 0) {
			tabbar.setSelectedItem(0);
		}
		//		LoadMyOrders();
	}
	public void LoadMyOrders(){
		OkHttpClient client=Server.getSharedClient();

		Request request=Server.requestBuilderWithApi("orders/findall")
				.get().build();

		client.newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				final String responseStr=arg1.body().string();
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						try {
							Page<Orders> data=new ObjectMapper()
									.readValue(responseStr, new TypeReference<Page<Orders>>() {});
							ordersData=data.getContent();
							Page=data.getNumber();
//							listAdapter.notifyDataSetInvalidated();
						} catch (JsonParseException e) {
							e.printStackTrace();
						} catch (JsonMappingException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				});

			}

			@Override
			public void onFailure(Call arg0, IOException arg1) {

			}
		});
	}

	public void goShopActivity(int position){
		Intent intent=new Intent(MyOrdersActivity.this,ShopActivity.class);
		intent.putExtra("shop",ordersData.get(position).getGoods().getShop());
		startActivity(intent);
	}
	public void goOrderDelete(final int position){
		AlertDialog.Builder builder=new Builder(this);
		builder.setMessage("是否删除订单？");
		builder.setNegativeButton("取消",null);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				deleteOrder(position);
			}
		});
		builder.show();
	}

	public void deleteOrder(int position){
		OkHttpClient client=Server.getSharedClient();
		Request request=Server.requestBuilderWithApi("orders/delete/"+ordersData.get(position).getOrdersID())
				.get().build();
		client.newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				String responseStr=arg1.body().string();
				final Boolean isDeleted=new ObjectMapper().readValue(responseStr, Boolean.class);
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if(isDeleted){
							Toast.makeText(MyOrdersActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
							LoadMyOrders();
						}
					}
				});
			}

			@Override
			public void onFailure(Call arg0, IOException arg1) {

			}
		});
	}

}
