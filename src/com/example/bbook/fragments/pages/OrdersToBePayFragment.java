package com.example.bbook.fragments.pages;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.example.bbook.BuyActivity;
import com.example.bbook.MD5;
import com.example.bbook.MyOrdersActivity;
import com.example.bbook.PayActivity;
import com.example.bbook.R;
import com.example.bbook.SetPayPasswordActivity;
import com.example.bbook.api.Page;
import com.example.bbook.api.Server;
import com.example.bbook.api.entity.Orders;
import com.example.bbook.api.widgets.GoodsPicture;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OrdersToBePayFragment extends Fragment {
	View view;
	ListView list;
	List<Orders> listData;
	
//	ImageView ordersDelete;
	List<Orders> toBePayOrders;
	int page = 0;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if(view == null) {
			view = inflater.inflate(R.layout.fragment_page_to_be_pay, null);
			list = (ListView) view.findViewById(R.id.list);
		}
		
		list.setAdapter(listAdapter);
		list.setDivider(null);
		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		LoadMyOrders();
	}
	
	private class OrdersHolder {
		TextView tvName, tvType, tvQuantity, tvPrice, tvSum, tvOrderId, tvPay, tvCancel;
		GoodsPicture imgGoods;
	}
	
	BaseAdapter listAdapter=new BaseAdapter() {
		@SuppressLint("InflateParams")
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View view=null;
			OrdersHolder oHolder;
			if(convertView==null){
				oHolder = new OrdersHolder();
				LayoutInflater inflater=LayoutInflater.from(parent.getContext());
				view=inflater.inflate(R.layout.list_item_to_be_pay, null);
				oHolder.imgGoods = (GoodsPicture) view.findViewById(R.id.goods_image);
				oHolder.tvName = (TextView) view.findViewById(R.id.name);
				oHolder.tvPrice = (TextView) view.findViewById(R.id.price);
				oHolder.tvQuantity = (TextView) view.findViewById(R.id.quantity);
				oHolder.tvType = (TextView) view.findViewById(R.id.type);
				oHolder.tvSum = (TextView) view.findViewById(R.id.sum);
				oHolder.tvOrderId = (TextView) view.findViewById(R.id.order_id);
				oHolder.tvPay = (TextView) view.findViewById(R.id.pay);
				oHolder.tvCancel = (TextView) view.findViewById(R.id.cancel);
				view.setTag(oHolder);
			}else{
				view = convertView;
				oHolder = (OrdersHolder) view.getTag();
			}
			final Orders order=listData.get(position);

			oHolder.tvPrice.setText("￥"+order.getGoods().getGoodsPrice());
			oHolder.tvQuantity.setText("x" + order.getGoodsQTY());
			oHolder.tvSum.setText("合计:￥"+order.getGoodsSum()+"");
			oHolder.imgGoods.load(Server.serverAdress + order.getGoods().getGoodsImage());
			oHolder.tvName.setText(order.getGoods().getGoodsName());
			oHolder.tvType.setText(order.getGoods().getGoodsType());
			oHolder.tvOrderId.setText("订单号:" + order.getOrdersID());
			oHolder.tvPay.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					onPay(order);
//				checkPayPasswordIsExisted(order);
				}
			});
			oHolder.tvCancel.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					onCancel();
				}
			});
			return view;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public Object getItem(int position) {
			return listData.get(position);
		}

		@Override
		public int getCount() {
			return listData==null?0:listData.size();
		}
	};
	
	public void onPay(Orders order) {
		if(toBePayOrders == null) {
			toBePayOrders = new ArrayList<Orders>();
		}
		toBePayOrders.add(order);
		Intent itnt = new Intent(getActivity(), PayActivity.class);
		itnt.putExtra("toBePayOrders", (Serializable)toBePayOrders);
		startActivity(itnt);
	}
	
	public void onCancel() {
		
	}
	
	public void LoadMyOrders(){
		OkHttpClient client=Server.getSharedClient();
		Request request=Server.requestBuilderWithApi("orders/findall/2?page=" + page)
				.get().build();

		client.newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				final String responseStr=arg1.body().string();
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						try {
							Page<Orders> data=new ObjectMapper()
									.readValue(responseStr, new TypeReference<Page<Orders>>() {});
							listData=data.getContent();
							page=data.getNumber();
							listAdapter.notifyDataSetInvalidated();
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
	
//	//
//	
//	//输入支付密码
//		public void  goInputPayPassword(Orders order) {
//			AlertDialog.Builder builder=new Builder(getActivity());
//			builder.setTitle("请输入支付密码");
//			//把布局文件先填充成View对象
//			View view = View.inflate(getActivity(), R.layout.dialog_password, null);
//			final EditText edt_pwd=(EditText)view.findViewById(R.id.edit_pwd);
//			//把填充得来的view对象设置为对话框显示内容
//			builder.setView(view);
//			builder.setPositiveButton("确认支付", new DialogInterface.OnClickListener() {
//				public void onClick(DialogInterface dialog, int which) {
//					String string= edt_pwd.getText().toString();
//					goCheckPayPassword(string,order);
//				}
//			});
//			builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//				public void onClick(DialogInterface dialog, int which) {
//
//				}
//			});
//			builder.show();
//		}
//		
//		//检查支付密码是否存在
//		public void checkPayPasswordIsExisted(final Orders order){
//			OkHttpClient client=Server.getSharedClient();
//			Request request=Server.requestBuilderWithApi("user/PayPasswordIsExist").get().build();
//			client.newCall(request).enqueue(new Callback() {
//				@Override
//				public void onResponse(Call arg0, Response arg1) throws IOException {
//					final String responseStr=arg1.body().string();
//					getActivity().runOnUiThread(new Runnable() {
//						@Override
//						public void run() {
//						
//						try {
//							 Boolean isExisted = new ObjectMapper().readValue(responseStr,Boolean.class);
//							if(isExisted){
//								//	goCheckPayPassword();
//								goInputPayPassword(order);
//							}else{
//								goSetPayPassword();
//							}
//						} catch (JsonParseException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						} catch (JsonMappingException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						} catch (IOException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//						}
//					});
//				}
//				@Override
//				public void onFailure(Call arg0, IOException arg1) {
//				}
//			});
//		}
//	//检查支付密码是否正确
//		public void goCheckPayPassword(String payPassword,Orders order){
//
//		//String payPassword=fragPayPassword.getText();
//			OkHttpClient client=Server.getSharedClient();
//			MultipartBody.Builder requestBody=new MultipartBody.Builder()
//					.addFormDataPart("payPassword", MD5.getMD5(payPassword));
//			Request request=Server.requestBuilderWithApi("payPassword")
//					.method("post",null)
//					.post(requestBody.build())
//					.build();
//
//			client.newCall(request).enqueue(new Callback() {
//				@Override
//				public void onResponse(Call arg0, Response arg1) throws IOException {
//					final String responseStr=arg1.body().string();
//					getActivity().runOnUiThread(new Runnable() {
//
//						@Override
//						public void run() {
//
//							Boolean checkPayPassword;
//							try {
//								checkPayPassword = new ObjectMapper().readValue(responseStr, Boolean.class);
//								if(checkPayPassword){
//									
//										goPay(orders);
//									
//								}
//							} catch (JsonParseException e) {
//								e.printStackTrace();
//							} catch (JsonMappingException e) {
//								e.printStackTrace();
//							} catch (IOException e) {
//								e.printStackTrace();
//							}
//						}
//					});
//				}
//				@Override
//				public void onFailure(Call arg0, IOException arg1) {
//				}
//			});
//		}
//		
//		//设置支付密码
//		public void goSetPayPassword(){
//			Intent intent=new Intent(getActivity(),SetPayPasswordActivity.class);
//			startActivity(intent);
//		}
//		//支付订单，并修改状态
//		public void goPay(String orderId){
//			int state=3;
//			UUID uuid=UUID.randomUUID();
//			OkHttpClient client=Server.getSharedClient();
//			MultipartBody.Builder requestBody=new MultipartBody.Builder()
//					.addFormDataPart("state", state+"")
//					.addFormDataPart("uuid", uuid.toString());
//			
//			Request request=Server.requestBuilderWithApi("order/payfor/"+orderId)
//					.method("post",null)
//					.post(requestBody.build())
//					.build();
//
//			client.newCall(request).enqueue(new Callback() {
//
//				@Override
//				public void onResponse(Call arg0, Response arg1) throws IOException {
//					String responseStr=arg1.body().string();
//					final Boolean checkPayState=new ObjectMapper().readValue(responseStr, Boolean.class);
//				//	Log.d("aaasss",  responseStr);
//					getActivity().runOnUiThread(new Runnable() {
//						@Override
//						public void run() {
//							if(checkPayState){
//								Toast.makeText(getActivity(),"支付成功", Toast.LENGTH_SHORT).show();
//								goMyOrders();
//							}
//						}
//					});
//
//				}
//
//				@Override
//				public void onFailure(Call arg0, IOException arg1) {
//
//				}
//			});
//		}
//		public void goMyOrders(){
//			new AlertDialog.Builder(getActivity()).setMessage("支付成功").setPositiveButton("查看我的订单", new DialogInterface.OnClickListener() {
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					Intent itnt = new Intent(getActivity(),MyOrdersActivity.class);
//					startActivity(itnt);
////					finish();
//				}
//			}).show();
//		}
	
}