package com.example.bbook.fragments.pages;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.example.bbook.AddBookCommentActivity;
import com.example.bbook.BuyActivity;
import com.example.bbook.MD5;
import com.example.bbook.MyOrdersActivity;
import com.example.bbook.MyWalletActivity;
import com.example.bbook.OrderDetailActivity;
import com.example.bbook.R;
import com.example.bbook.SetPayPasswordActivity;
import com.example.bbook.api.Goods;
import com.example.bbook.api.Page;
import com.example.bbook.api.Server;
import com.example.bbook.api.entity.Orders;
import com.example.bbook.api.widgets.GoodsPicture;
import com.example.bbook.api.widgets.OrderBottomContent;
import com.example.bbook.api.widgets.OrderBottomContent.OnCommentClickedListener;
import com.example.bbook.api.widgets.OrderBottomContent.OnConfirmClickedListener;
import com.example.bbook.api.widgets.OrderBottomContent.OnDeleteClickedListener;
import com.example.bbook.api.widgets.OrderBottomContent.OnPayClickedListener;
import com.example.bbook.api.widgets.OrderBottomContent.OnRejectClickedListener;
import com.example.bbook.api.widgets.OrderMiddleContent;
import com.example.bbook.api.widgets.OrderTopContent;
import com.example.bbook.api.widgets.OrderTopContent.OnTabClickedListener;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import util.AutoLoadListener;
import util.AutoLoadListener.AutoLoadCallBack;
import util.OrderContent;

public class OrdersAllFragment extends Fragment {
	boolean isFindAll = false;
	int state;
	public OrdersAllFragment(int state) {
		this.state = state;
	}
	public OrdersAllFragment() {
		isFindAll = true;
	}
	View view;
	ListView list;
	List<Orders> listData;
	List<Orders> toBePayOrders = new ArrayList<>();
	int page = 0;
	List<Orders> orderList = new ArrayList<>();
	Map<String , List<Orders>> dataset = new HashMap<>();
	List<OrderContent> orderContents = new ArrayList<>();
	void initDate() {
		for(int i = 0; i < listData.size(); i++) {
			if(orderList == null) {
				orderList = new ArrayList<>();
			} else {
				orderList.add(listData.get(i));
			}
		}
		for(int i = 0; i < orderList.size(); i++) {
			for(int j = orderList.size() - 1; j > i; j--) {
				if(orderList.get(i).getOrdersID().equals(orderList.get(j).getOrdersID())) {
					orderList.remove(j);
				}
			}
		}
		
		for(int i = 0; i < orderList.size(); i++) {
			Orders order;
			List<Orders> goodsOfOrder = new ArrayList<>();
			for(int j = 0; j < listData.size(); j++) {
				if(listData.get(j).getOrdersID().equals(orderList.get(i).getOrdersID())) {
					goodsOfOrder.add(listData.get(j));
				}
			}
			dataset.put(orderList.get(i).getOrdersID(), goodsOfOrder);
			Log.d("dataSetSize", dataset.size() + "");
		}
	}
	
	void initOrderContents() {
	
		for(int i = 0; i < orderList.size(); i++) {
			Double sum = 0.0;
			OrderTopContent topContent = new OrderTopContent(orderList.get(i));	// TOP
			orderContents.add(topContent);
			for(int j = 0; j < dataset.get(orderList.get(i).getOrdersID()).size(); j++) {
				OrderMiddleContent middleContent = new OrderMiddleContent(dataset.get(orderList.get(i).getOrdersID()).get(j));
				orderContents.add(middleContent);
				sum += dataset.get(orderList.get(i).getOrdersID()).get(j).getGoodsSum();
			}
			OrderBottomContent bottomContent = new OrderBottomContent(orderList.get(i), sum);
			orderContents.add(bottomContent);
		}
	}
	
	protected void goConfirm(final Orders order) {
		if(order != null) {
			new AlertDialog.Builder(getActivity()).setMessage("确认收货？")
			.setNegativeButton("取消", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
				
			})
			.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					onConfirm(order.getOrdersID());
				}
			}).show();
		}
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if(view == null) {
			view = inflater.inflate(R.layout.fragment_page_all_orders, null);
			list = (ListView) view.findViewById(R.id.list);
		}
//		list.setOnScrollListener(new AutoLoadListener(callback));
		list.setAdapter(listAdapter);
		list.setDivider(null);
//		list.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//				Intent itnt = new Intent(getActivity(), OrderDetailActivity.class);
//				itnt.putExtra("order", listData.get(position));
//				startActivity(itnt);
//			}
//		});
		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		reload();
	}
	
	void reload() {
		orderList = new ArrayList<>();
		dataset = new HashMap<>();
		orderContents = new ArrayList<>();
		if(isFindAll) {
			LoadMyOrders();
			} else {
				load(state);
			}
	}
	
	BaseAdapter listAdapter = new BaseAdapter() {
		LayoutInflater inflater;
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(orderContents.get(position) instanceof OrderTopContent) {
				OrderTopContent topContent = (OrderTopContent) orderContents.get(position);
				final Orders order = topContent.getOrder();
				topContent.setonTabClickedListener(new OnTabClickedListener() {
					
					@Override
					public void onTabClicked() {
						goOrderDetail(order.getOrdersID());
					}
				});
			}
			if(orderContents.get(position) instanceof OrderBottomContent) {
				OrderBottomContent bottomContent = (OrderBottomContent) orderContents.get(position);
				final Orders order = bottomContent.getOrder();
				bottomContent.setOnConfirmClickedListener(new OnConfirmClickedListener() {
					
					@Override
					public void onConfirmClicked() {
						Toast.makeText(getActivity(), "aaa", Toast.LENGTH_SHORT).show();
						goConfirm(order);
					}
				});
				bottomContent.setOnPayClickedListener(new OnPayClickedListener() {					
					@Override
					public void onPayClicked() {
						toBePayOrders.add(order);
						checkPayPasswordIsExisted();
					}
				});
				bottomContent.setOnCommentClickedListener(new OnCommentClickedListener() {
					
					@Override
					public void onCommentClicked() {
						goComment(order);
					}
				});
				bottomContent.setOnRejectClickedListener(new OnRejectClickedListener() {
					
					@Override
					public void onRejectClicked() {
						goReject(order);
					}
				});
				bottomContent.setOnDeleteClickedListener(new OnDeleteClickedListener() {
					
					@Override
					public void onDeleteClicked() {
						goDelete(order);
					}
				});
			}
			return orderContents.get(position).getView(getActivity(), convertView, inflater);
		}
		
		@Override
		public long getItemId(int position) {
			return position;
		}
		
		@Override
		public Object getItem(int position) {
			return orderContents.get(position);
		}
		
		@Override
		public int getCount() {
			return orderContents.size();
		}
		
		public boolean isEnabled(int position) {
			return orderContents.get(position).isClickable();
		};
	};
	
	public void goOrderDetail(String orderId) {
		Intent itnt = new Intent(getActivity(), OrderDetailActivity.class);
		itnt.putExtra("orderId", orderId);
		startActivity(itnt);
	}
	
	public void goDelete(final Orders order) {
		new AlertDialog.Builder(getActivity()).setMessage("确认删除订单？")
		.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
			
		})
		.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				onDelete(order.getOrdersID());
			}
		}).show();
	}
	
	// 确认退货
	public void goReject(final Orders order) {
		new AlertDialog.Builder(getActivity()).setMessage("确认退货？")
		.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
			
		})
		.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				onReject(order.getOrdersID());
			}
		}).show();
	}
	
	public void onReject(String orderId) {
		MultipartBody.Builder body = new MultipartBody.Builder().addFormDataPart("state", 6 + "");
		Request request = Server.requestBuilderWithApi("order/" + orderId).post(body.build()).build();

		Server.getSharedClient().newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(getActivity(), "已申请退货，请与卖家联系退款。", Toast.LENGTH_SHORT).show();
						OrdersAllFragment.this.reload();
					}
				});
			}

			@Override
			public void onFailure(Call arg0, IOException arg1) {
			}
		});
	}
	
		
	public void onDelete(String orderId){
		OkHttpClient client=Server.getSharedClient();
		Request request=Server.requestBuilderWithApi("orders/delete/"+orderId)
				.get().build();
		client.newCall(request).enqueue(new Callback() {
			
			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				String responseStr=arg1.body().string();
				final Boolean isDeleted=new ObjectMapper().readValue(responseStr, Boolean.class);
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if(isDeleted){
							Toast.makeText(getActivity(), "删除成功", Toast.LENGTH_SHORT).show();
							OrdersAllFragment.this.reload();
						}
					}
				});
			}
			
			@Override
			public void onFailure(Call arg0, IOException arg1) {
				
			}
		});
	}
	
	
	// 根据状态的加载订单
	private void load(int state) {
		
		OkHttpClient client=Server.getSharedClient();
		Request request=Server.requestBuilderWithApi("orders/findall/" + state + "?page=" + page)
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
							OrdersAllFragment.this.initDate();
							OrdersAllFragment.this.initOrderContents();
							listAdapter.notifyDataSetChanged();
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
	
	
	// 加载全部订单
	public void LoadMyOrders(){
		OkHttpClient client=Server.getSharedClient();

		Request request=Server.requestBuilderWithApi("orders/findall")
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
							OrdersAllFragment.this.initDate();
							OrdersAllFragment.this.initOrderContents();
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
	
	// 确认收货
	public void onConfirm(String orderId) {
	MultipartBody.Builder body = new MultipartBody.Builder().addFormDataPart("state", 5 + "");
	Request request = Server.requestBuilderWithApi("order/" + orderId).post(body.build()).build();

	Server.getSharedClient().newCall(request).enqueue(new Callback() {

		@Override
		public void onResponse(Call arg0, Response arg1) throws IOException {
			getActivity().runOnUiThread(new Runnable() {

				@Override
				public void run() {
					Toast.makeText(getActivity(), "确认收货成功", Toast.LENGTH_SHORT).show();
					OrdersAllFragment.this.reload();
				}
			});
		}

		@Override
		public void onFailure(Call arg0, IOException arg1) {
		}
	});
}
	// 商品评论跳转
	public void goComment(Orders order) {
		Intent itnt = new Intent(getActivity(), AddBookCommentActivity.class);
		itnt.putExtra("ordersId", order.getOrdersID());
		itnt.putExtra("goods", order.getGoods());
		startActivity(itnt);
	}
	
	//检查支付密码是否存在
		public void checkPayPasswordIsExisted(){
			OkHttpClient client=Server.getSharedClient();
			Request request=Server.requestBuilderWithApi("user/PayPasswordIsExist").get().build();
			client.newCall(request).enqueue(new Callback() {
				@Override
				public void onResponse(Call arg0, Response arg1) throws IOException {
					final String responseStr=arg1.body().string();
					getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {

							try {
								Boolean isExisted = new ObjectMapper().readValue(responseStr,Boolean.class);
								if(isExisted){
									goInputPayPassword();
								}else{
									goSetPayPassword();
								}
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
	
		//设置支付密码
		public void goSetPayPassword(){
			Intent intent=new Intent(getActivity(),SetPayPasswordActivity.class);
			startActivity(intent);
		}
		
	//输入支付密码
		public void  goInputPayPassword() {
			AlertDialog.Builder builder=new Builder(getActivity());
			builder.setTitle("请输入支付密码");
			//把布局文件先填充成View对象
			View view = View.inflate(getActivity(), R.layout.dialog_password, null);
			final EditText edt_pwd=(EditText)view.findViewById(R.id.edit_pwd);
			//把填充得来的view对象设置为对话框显示内容
			builder.setView(view);
			builder.setPositiveButton("确认支付", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					String string= edt_pwd.getText().toString();
					goCheckPayPassword(string);
				}
			});
			builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {

				}
			});
			builder.show();
		}
	
		//检查支付密码是否正确
		public void goCheckPayPassword(String payPassword){

			//String payPassword=fragPayPassword.getText();
			OkHttpClient client=Server.getSharedClient();
			MultipartBody.Builder requestBody=new MultipartBody.Builder()
					.addFormDataPart("payPassword", MD5.getMD5(payPassword));
			Request request=Server.requestBuilderWithApi("payPassword")
					.method("post",null)
					.post(requestBody.build())
					.build();

			client.newCall(request).enqueue(new Callback() {
				@Override
				public void onResponse(Call arg0, Response arg1) throws IOException {
					final String responseStr=arg1.body().string();
					getActivity().runOnUiThread(new Runnable() {

						@Override
						public void run() {

							Boolean checkPayPassword;
							try {
								checkPayPassword = new ObjectMapper().readValue(responseStr, Boolean.class);
								if(checkPayPassword){
									for(int i = 0; i < toBePayOrders.size(); i++) {
										goPay(toBePayOrders.get(i).getOrdersID());
									}
								}
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
		
		
		//支付订单，并修改状态
		public void goPay(String orderId){
			int state=3;
			UUID uuid=UUID.randomUUID();
			OkHttpClient client=Server.getSharedClient();
			MultipartBody.Builder requestBody=new MultipartBody.Builder()
					.addFormDataPart("state", state+"")
					.addFormDataPart("uuid", uuid.toString());

			Request request=Server.requestBuilderWithApi("order/payfor/"+orderId)
					.method("post",null)
					.post(requestBody.build())
					.build();

			client.newCall(request).enqueue(new Callback() {

				@Override
				public void onResponse(Call arg0, Response arg1) throws IOException {
					String responseStr=arg1.body().string();
					final Boolean checkPayState=new ObjectMapper().readValue(responseStr, Boolean.class);
					getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if(checkPayState){
								Toast.makeText(getActivity(),"支付成功", Toast.LENGTH_SHORT).show();
								OrdersAllFragment.this.reload();
							}else{
								goOnFailure();
							}
						}
					});

				}

				@Override
				public void onFailure(Call arg0, IOException arg1) {

				}
			});
		}
		
		// 余额不足
		public void goOnFailure() {
			new AlertDialog.Builder(getActivity()).setMessage("余额不足").setPositiveButton("马上充值", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent itnt = new Intent(getActivity(),MyWalletActivity.class);
					startActivity(itnt);
				}
			}).show();
		}
		
	private void loadMore() {
		OkHttpClient client=Server.getSharedClient();

		Request request=Server.requestBuilderWithApi("orders/findall?page=" + (page+1))
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
							if(data.getNumber() > page) {
								if(listData == null) {
									listData=data.getContent();
								} else {
									listData.addAll(data.getContent());
								}
							}
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
}
