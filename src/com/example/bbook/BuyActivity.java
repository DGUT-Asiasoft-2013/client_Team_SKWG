package com.example.bbook;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.example.bbook.api.Goods;
import com.example.bbook.api.Server;
import com.example.bbook.api.entity.CommomInfo;
import com.example.bbook.api.widgets.GoodsPicture;
import com.example.bbook.api.widgets.TitleBarFragment;
import com.example.bbook.fragments.NumberPlusAndMinusFrament;
import com.example.bbook.fragments.pages.ShoppingCartFragment;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import inputcells.SimpleTextInputcellFragment;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.Response;

public class BuyActivity extends Activity {
	final static int SELECTED_INFO_CODE = 1;
	EditText goodsCount;
	TextView tvGoodsName, tvGoodsType, tvGoodsPrice, infoName,
	infoAddress, infoTel, infoPostcode, tvSum;
	GoodsPicture goodsImage;
	Button btnSubmit, btnAddInfo;
	ListView list;
	Goods goods;
	LinearLayout info;
	List<CommomInfo> dataList;
	CommomInfo defaultInfo;
	CommomInfo selectedInfo;
	TitleBarFragment fragOrderConfirm;
	List<Goods> selectedGoods;		// 需要下单的商品

	NumberPlusAndMinusFrament fragNumberAndMinus;
	int quantity = 0;
	double sum = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_buy);

		fragOrderConfirm = (TitleBarFragment) getFragmentManager().findFragmentById(R.id.order_confirm_titlebar);
		fragOrderConfirm.setTitleName("确认订单", 16);
		fragOrderConfirm.setBtnNextState(false);
		fragOrderConfirm.setOnGoBackListener(new TitleBarFragment.OnGoBackListener() {

			@Override
			public void onGoBack() {
				finish();
			}
		});
		// 需要下单的商品
		goods = (Goods) getIntent().getSerializableExtra("goods");
		selectedGoods = (List<Goods>) getIntent().getSerializableExtra("selectedGoods");

		if(selectedGoods == null) {
			selectedGoods = new ArrayList<Goods>();
		}
		if(goods!= null) {
			selectedGoods.add(goods);
		}
		init();
		list = (ListView) findViewById(R.id.list);
		info.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				goManageInfo();
			}
		});
		btnSubmit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onSubmit();
				for(int i = 0; i < selectedGoods.size(); i++) {
					onDelete(selectedGoods.get(i).getId());
				}
			}
		});
		list.setAdapter(listAdapter);
	}

	protected void goManageInfo() {
		Intent itnt = new Intent(BuyActivity.this, ManageCommomInfoActivity.class);
		//		startActivity(itnt);
		startActivityForResult(itnt, SELECTED_INFO_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(SELECTED_INFO_CODE == resultCode) {
			selectedInfo = (CommomInfo) data.getSerializableExtra("selectedInfo");
			setInfo();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onResume() {
		super.onResume();
		reload();
		// setInfo();
	}

	private void reload() {
		for(int i = 0; i < selectedGoods.size(); i++) {
			sum += Double.parseDouble(selectedGoods.get(i).getGoodsPrice()) * selectedGoods.get(i).getQuantity();
		}
		tvSum.setText(sum + "");
		Request request = Server.requestBuilderWithApi("commominfo/default").get().build();
		Server.getSharedClient().newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, final Response arg1) throws IOException {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						try {
							BuyActivity.this.defaultInfo = new ObjectMapper().readValue(arg1.body().string(),
									CommomInfo.class);
							if(selectedInfo == null) {
								selectedInfo = defaultInfo;
							}
							runOnUiThread(new Runnable() {

								@Override
								public void run() {
									BuyActivity.this.setInfo();
								}
							});
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

	void setInfo() {
		infoName.setText("收货人： " + selectedInfo.getName());
		infoAddress.setText("收货地址: " + selectedInfo.getAddress());
		infoTel.setText("联系电话: " + selectedInfo.getTel());
		infoPostcode.setText("邮编：" + selectedInfo.getPostCode());
		Log.d("info", selectedInfo.getName() + "   " + selectedInfo.getAddress() + "    " + selectedInfo.getTel() + "   "
				+ selectedInfo.getPostCode());
	}

	protected void onSubmit() {
		for(int i = 0; i < selectedGoods.size(); i++) {
			addOrder(selectedGoods.get(i));
		}

	}

	private void addOrder(Goods goods) {
		int orderState = 2;

		quantity = goods.getQuantity();

		final String orderId = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + goods.getId() + quantity;

		String name = selectedInfo.getName();
		String address = selectedInfo.getAddress();
		String tel = selectedInfo.getTel();
		String postCode = selectedInfo.getPostCode();
		Log.d("orderID", orderId);
		MultipartBody body = new MultipartBody.Builder().addFormDataPart("ordersID", orderId)
				.addFormDataPart("ordersState", orderState + "").addFormDataPart("goodsQTY", quantity + "")
				.addFormDataPart("goodsSum", (Integer.valueOf(goods.getGoodsPrice()) * quantity) + "")
				.addFormDataPart("buyerName", name).addFormDataPart("buyerPhoneNum", tel)
				.addFormDataPart("buyerAddress", address).addFormDataPart("postCode", postCode)
				.addFormDataPart("goodsId", goods.getId() + "").build();

		Request request = Server.requestBuilderWithApi("orders").method("post", null).post(body).build();

		Server.getSharedClient().newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(final Call arg0, final Response arg1) throws IOException {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						try {
							goPay(orderId);
						} catch (Exception e) {
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

	void onResponse(Call arg0, String responseBody) {
		new AlertDialog.Builder(this).setTitle("成功").setMessage(responseBody).show();
	}

	void goPay(String orderId) {
		Toast.makeText(this, "提交订单成功", Toast.LENGTH_SHORT).show();
		Intent intent = new Intent(BuyActivity.this, PayActivity.class);
		intent.putExtra("orderId", orderId);
		startActivity(intent);
		finish();
	}

	private class GoodsHolder {
		GoodsPicture ImgGoods;
		TextView tvName, tvType, tvPrice, tvCount, tvQuantity;
		ImageView btnMinus,btnPlus;
	}

	BaseAdapter listAdapter = new BaseAdapter() {
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final GoodsHolder gHolder;
			if(convertView == null) {
				gHolder = new GoodsHolder();
				LayoutInflater inflater = LayoutInflater.from(BuyActivity.this);
				convertView = inflater.inflate(R.layout.list_item_order_confirm, null);
				gHolder.ImgGoods = (GoodsPicture) convertView.findViewById(R.id.goods_image);
				gHolder.tvName = (TextView) convertView.findViewById(R.id.name);
				gHolder.tvType = (TextView) convertView.findViewById(R.id.type);
				gHolder.tvPrice = (TextView) convertView.findViewById(R.id.price);
				gHolder.tvCount = (TextView) convertView.findViewById(R.id.Count);
				gHolder.tvQuantity = (TextView) convertView.findViewById(R.id.quantity);
				gHolder.btnMinus = (ImageView) convertView.findViewById(R.id.btn_minus);
				gHolder.btnPlus = (ImageView) convertView.findViewById(R.id.btn_plus);
				convertView.setTag(gHolder);
			} else {
				gHolder = (GoodsHolder) convertView.getTag();
			}

			final Goods goods = selectedGoods.get(position);
			if(goods != null) {
				gHolder.ImgGoods.load(Server.serverAdress + goods.getGoodsImage());
				gHolder.tvName.setText(goods.getGoodsName());
				gHolder.tvType.setText(goods.getGoodsType());
				gHolder.tvCount.setText("剩余 " + goods.getGoodsCount() + " 件");
				gHolder.tvPrice.setText(goods.getGoodsPrice());
				Log.d("goods.getQuantity()", goods.getQuantity() + "");
				gHolder.tvQuantity.setText(goods.getQuantity() + "");
				gHolder.btnPlus.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						quantity++;

						sum+= Double.parseDouble(goods.getGoodsPrice());
						tvSum.setText(sum + "");
						goods.setQuantity(quantity);
						gHolder.tvQuantity.setText(quantity + "");
					}
				});
				gHolder.btnMinus.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if(quantity <= 0) {
							return;
						} else {
							quantity--;
							sum-= Double.parseDouble(goods.getGoodsPrice());
							goods.setQuantity(quantity);
							tvSum.setText(sum + "");
							gHolder.tvQuantity.setText(quantity + "");
						}
					}
				});
			}

			return convertView;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public Object getItem(int position) {
			return selectedGoods.get(position);
		}

		@Override
		public int getCount() {
			return selectedGoods == null ? 0 : selectedGoods.size();
		}
	};
	protected void onDelete(int goodsId) {
		Request request = Server.requestBuilderWithApi("shoppingcart/delete/" + goodsId).get().build();
		Server.getSharedClient().newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
			}

			@Override
			public void onFailure(Call arg0, IOException arg1) {

			}
		});
	}
	void init() {
		goodsCount = (EditText) findViewById(R.id.count);
		tvGoodsName = (TextView) findViewById(R.id.name);
		tvGoodsType = (TextView) findViewById(R.id.type);
		tvGoodsPrice = (TextView) findViewById(R.id.price);
		tvSum = (TextView) findViewById(R.id.sum);
		goodsImage = (GoodsPicture) findViewById(R.id.goods_image);
		infoName = (TextView) findViewById(R.id.info_name);
		infoAddress = (TextView) findViewById(R.id.info_address);
		infoTel = (TextView) findViewById(R.id.info_tel);
		infoPostcode = (TextView) findViewById(R.id.info_postcode);
		btnSubmit = (Button) findViewById(R.id.submit);
		info = (LinearLayout) findViewById(R.id.info);
	}
}
