package com.example.bbook.fragments.pages;

import java.io.IOException;

import com.example.bbook.ChangePasswordActivity;
import com.example.bbook.ChangeUserMessageActivity;
import com.example.bbook.LoginActivity;
import com.example.bbook.MyOrdersActivity;
import com.example.bbook.MystoreActivity;
import com.example.bbook.OpenStoreActivity;
import com.example.bbook.R;
import com.example.bbook.api.Server;
import com.example.bbook.api.User;
import com.example.bbook.api.widgets.AvatarView;
import com.example.bbook.api.widgets.ItemFragment;
import com.example.bbook.api.widgets.TitleBarFragment;
import com.example.bbook.fragments.pages.PasswordRecoverStep1Fragment.OnGoNextListener;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.R.color;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MyProfileFragment extends Fragment {

	View view;
	TextView txAccount, txName;
	AvatarView avatar;
	TitleBarFragment fragMeTitleBar;
	ItemFragment itemBtnExit, itemBtnOrder, itemBtnChange, itemOpenStore, itemMyStore;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (view == null) {
			view = inflater.inflate(R.layout.fragment_page_myprofile, null);
			txAccount = (TextView) view.findViewById(R.id.showAccount);
			txName = (TextView) view.findViewById(R.id.showName);
			avatar = (AvatarView) view.findViewById(R.id.avatar);

			fragMeTitleBar = (TitleBarFragment) getFragmentManager().findFragmentById(R.id.me_titlebar);
			fragMeTitleBar.setBtnBackState(false);
			fragMeTitleBar.setSplitLineState(false);
			fragMeTitleBar.setBtnNextState(false);
			fragMeTitleBar.setTitleName("我的", 16);

			// 我要开店
			itemOpenStore = (ItemFragment) getFragmentManager().findFragmentById(R.id.btn_openStore);
			itemOpenStore.setItemText("我要开店");
			itemOpenStore.setItemImage(R.drawable.icon_shop);
			itemOpenStore.setOnDetailedListener(new ItemFragment.OnDetailedListener() {

				@Override
				public void onDetailed() {
					Intent itnt = new Intent(getActivity(), OpenStoreActivity.class);
					startActivity(itnt);
				}
			});

			// 我的店铺
			itemMyStore = (ItemFragment) getFragmentManager().findFragmentById(R.id.btn_myStore);
			itemMyStore.setItemText("我的店铺");
			itemMyStore.setItemImage(R.drawable.icon_shop);
			itemMyStore.setOnDetailedListener(new ItemFragment.OnDetailedListener() {

				@Override
				public void onDetailed() {
					Intent itnt = new Intent(getActivity(), MystoreActivity.class);
					startActivity(itnt);
				}
			});

			// 修改密码
			itemBtnChange = (ItemFragment) getFragmentManager().findFragmentById(R.id.btn_change_password);
			itemBtnChange.setItemText("修改密码");
			itemBtnChange.setItemImage(R.drawable.icon_password);
			itemBtnChange.setOnDetailedListener(new ItemFragment.OnDetailedListener() {

				@Override
				public void onDetailed() {
					Intent itnt = new Intent(getActivity(), ChangePasswordActivity.class);
					startActivity(itnt);
				}
			});

			// 我的订单(没实现)
			itemBtnOrder = (ItemFragment) getFragmentManager().findFragmentById(R.id.btn_order);
			itemBtnOrder.setItemText("我的订单");
			itemBtnOrder.setItemImage(R.drawable.icon_order);
			itemBtnOrder.setOnDetailedListener(new ItemFragment.OnDetailedListener() {

				@Override
				public void onDetailed() {
					Intent intent=new Intent(getActivity(),MyOrdersActivity.class);
					startActivity(intent);
				}
			});

			// 注销
			itemBtnExit = (ItemFragment) getFragmentManager().findFragmentById(R.id.btn_exit);
			itemBtnExit.setItemText("注销");
			itemBtnExit.setItemImage(R.drawable.icon_exit);
			itemBtnExit.setOnDetailedListener(new ItemFragment.OnDetailedListener() {

				@Override
				public void onDetailed() {
					exit();
				}
			});

		}

		// view.findViewById(R.id.btn_openStore).setOnClickListener(new
		// OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// Intent itnt = new Intent(getActivity(),
		// OpenStoreActivity.class);
		// startActivity(itnt);
		//
		// }
		// });
		// view.findViewById(R.id.btn_myStore).setOnClickListener(new
		// OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		// Intent itnt = new Intent(getActivity(),
		// MystoreActivity.class);
		// startActivity(itnt);
		// }
		// });

		view.findViewById(R.id.btn_change_userMessage).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent itnt = new Intent(getActivity(),ChangeUserMessageActivity.class);
				startActivity(itnt);
			}
		});
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		itemOpenStore.setItemState(true);
		itemMyStore.setItemState(true);
		OkHttpClient client = Server.getSharedClient();
		Request request = Server.requestBuilderWithApi("me").method("get", null).build();
		client.newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(final Call arg0, Response arg1) throws IOException {
				// TODO Auto-generated method stub
				final User user = new ObjectMapper().readValue(arg1.body().bytes(), User.class);
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						MyProfileFragment.this.onResponse(arg0, user);
					}
				});
			}

			@Override
			public void onFailure(final Call arg0, final IOException arg1) {
				// TODO Auto-generated method stub
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method
						// stub
						MyProfileFragment.this.onFailure(arg0, arg1);
					}
				});
			}
		});
	}

	void onResponse(Call arg0, User user) {
		txAccount.setVisibility(View.VISIBLE);
		txName.setVisibility(View.VISIBLE);
		txAccount.setText("用户:" + user.getAccount());
		txName.setText("你好:" + user.getName());
		avatar.load(user);
		if (user.getIsStore().equals("0")) {
			itemOpenStore.setItemState(false);
		} else
			itemMyStore.setItemState(false);
	}

	void onFailure(Call arg0, Exception ex) {
		txAccount.setVisibility(View.VISIBLE);
		txName.setVisibility(View.VISIBLE);
		txAccount.setTextColor(color.holo_red_dark);
		txName.setTextColor(color.holo_red_dark);
		txAccount.setText(ex.getMessage());
		txName.setText(ex.getMessage());
	}

	void exit() {

		OkHttpClient client = Server.getSharedClient();
		Request request = Server.requestBuilderWithApi("exit").build();
		client.newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(final Call arg0, Response arg1) throws IOException {
				// TODO Auto-generated method stub
//				Intent itnt = new Intent(getActivity(), LoginActivity.class);
//				itnt.setFlags(itnt.FLAG_ACTIVITY_CLEAR_TASK | itnt.FLAG_ACTIVITY_NEW_TASK);
//				startActivity(itnt);
				getActivity().runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						MyProfileFragment.this.onResponse(arg0);
					}
				});
				
			}

			@Override
			public void onFailure(Call arg0, IOException arg1) {
				// TODO Auto-generated method stub

			}
		});

	}
	
	void onResponse(Call arg0){
		new AlertDialog.Builder(getActivity()).setTitle("提示").setMessage("是否要注销退出")
		.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				Intent itnt = new Intent(getActivity(), LoginActivity.class);
				itnt.setFlags(itnt.FLAG_ACTIVITY_CLEAR_TASK | itnt.FLAG_ACTIVITY_NEW_TASK);
				startActivity(itnt);
			}
		}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		}).show();
	}
}
