package com.example.bbook.fragments.pages;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.bbook.BuyActivity;
import com.example.bbook.MyOffSaleGoodsActivity;
import com.example.bbook.R;
import com.example.bbook.api.Goods;
import com.example.bbook.api.Page;
import com.example.bbook.api.Server;
import com.example.bbook.api.Shop;
import com.example.bbook.api.entity.ShoppingCart;
import com.example.bbook.api.entity.ShoppingCart.Key;
import com.example.bbook.api.widgets.AvatarView;
import com.example.bbook.api.widgets.GoodsPicture;
import com.example.bbook.api.widgets.TitleBarFragment;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class ShoppingCartFragment extends Fragment {

	View view;
	TitleBarFragment titleBar;
	ExpandableListView list;
	CheckBox selectAll;
	TextView tvSum;
	Button btnPay;
	int page = 0;
	int selectedCount = 0;
	double sum = 00.00;
	boolean isAllSelected;
	List<Goods> selectedGoods;
	List<ShoppingCart>  listData = new ArrayList<ShoppingCart>();
	List<Shop> groupList = new ArrayList<Shop>();
	//	List<ShoppingCart> childrenList;
	Map<String,List<ShoppingCart>> dataset = new HashMap<String,List<ShoppingCart>>();
	//	ExpandableListView exList;
	BaseExpandableListAdapter exlistAdapter;
	void initData() {
		for(int i = 0; i < listData.size(); i++) {
			if(groupList == null) {
				groupList = new ArrayList<Shop>();
			} else {
				Shop shop = listData.get(i).getId().getGoods().getShop();
				shop.setSelected(false);
				groupList.add(shop);
			}
		}
		for(int i = 0; i < groupList.size(); i++) {
			for(int j = groupList.size() - 1; j > i; j--) {
				if(groupList.get(i).getShopName().equals(groupList.get(j).getShopName())) {
					groupList.remove(j);
					Log.d("removegroupListSize", groupList.size() + "");
				}
			}
		}
		for(int i = 0; i < groupList.size(); i++) {
			ShoppingCart cart = null;
			List<ShoppingCart> childrenList = new ArrayList<ShoppingCart>();
			for(int j = 0; j < listData.size(); j++) {
				if(listData.get(j).getId().getGoods().getShop().getShopName().equals(groupList.get(i).getShopName())) {
					cart = listData.get(j);
					if(cart != null) {
						cart.getId().getGoods().setSelected(false);
						childrenList.add(cart);
						Log.d("cL", childrenList.size() + "");
					}
				}
			}
			dataset.put(groupList.get(i).getShopName(), childrenList);
			Log.d("datasetSize", dataset.size() + "");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (view==null) {
			view=inflater.inflate(R.layout.fragment_page_shopping_cart, null);

			titleBar = (TitleBarFragment) getFragmentManager().findFragmentById(R.id.title_bar);
			titleBar.setBtnBackState(false);
			titleBar.setBtnNextState(false);
			titleBar.setSplitLineState(false);
			titleBar.setTitleName("购物车", 16);
		}
		exlistAdapter = new myAdapter();
		
		list =  (ExpandableListView) view.findViewById(R.id.list);
		list.setGroupIndicator(null);
		list.setDivider(null);

		selectAll = (CheckBox) view.findViewById(R.id.select_all);
		tvSum = (TextView) view.findViewById(R.id.sum);
		btnPay = (Button) view.findViewById(R.id.pay);
		tvSum.setText(sum + "");
		
		btnPay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				goBuy();
			}
		});
		selectAll.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				isAllSelected = ((CheckBox)v).isChecked();
				for(int i = 0; i < groupList.size(); i++) {
					Log.d("isAllSelected", "i=" + i + "  " + isAllSelected);
					groupList.get(i).setSelected(((CheckBox)v).isChecked());
					selectShop(i, ((CheckBox)v).isChecked());
				}
				Log.d("isAll", isAllSelected + "");
				//				for(int i = 0; i < groupList.size(); i++) {
				//					
				//				}
				exlistAdapter.notifyDataSetChanged();
			}
		});

		return view;
	}

	protected void goBuy() {
		if(selectedCount == 0) {
			Toast.makeText(getActivity(), "请选择要购买的商品", Toast.LENGTH_SHORT).show();
		} else {
			Intent itnt = new Intent(getActivity(), BuyActivity.class);
			itnt.putExtra("selectedGoods", (Serializable)selectedGoods);

			startActivity(itnt);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		isAllSelected = false;
		selectAll.setChecked(isAllSelected);
		groupList = new ArrayList<>();
		dataset = new HashMap<>();
//		initData();
		reload();
		list.setAdapter(exlistAdapter);
		list.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
				return true;
			}
		});
		calculate();
	}

	void expand() {
		int count = exlistAdapter.getGroupCount();
		Log.d("listCOunt", count + "");
		for(int i = 0; i < count; i++) {
			list.expandGroup(i);
		}
	};
	
	void collapse() {
		int count = exlistAdapter.getGroupCount();
		Log.d("listCOunt", count + "");
		for(int i = 0; i < count; i++) {
			list.collapseGroup(i);
		}
	}


	private void init(View view) {
		//		list = (ListView) view.findViewById(R.id.list);
		selectAll = (CheckBox) view.findViewById(R.id.select_all);
		tvSum = (TextView) view.findViewById(R.id.sum);
		btnPay = (Button) view.findViewById(R.id.pay);
	}

	private void setViewText() {
		tvSum.setText(sum + "");
		btnPay.setText("结算(" + selectedCount + ")");
	}

	private void calculate() {
		sum = 0;
		selectedCount = 0;
		selectedGoods = new ArrayList<>();
		for(int i = 0; i < groupList.size(); i++) {
			List<ShoppingCart> cartList = dataset.get(groupList.get(i).getShopName());
			for(int j = 0; j < cartList.size(); j++) {
				ShoppingCart cart = cartList.get(j);
				Goods goods = cart.getId().getGoods();
				int quantity = cartList.get(j).getQuantity();
				if(goods.isSelected()) {
					goods.setQuantity(quantity);
					selectedCount++;
					sum+=(Double.parseDouble(goods.getGoodsPrice())*quantity);
					selectedGoods.add(goods);
				}
			}
		}
		tvSum.setText(sum + "");
		btnPay.setText("结算(" + selectedCount + ")");
	}


	private void reload() {
		Request request = Server.requestBuilderWithApi("shoppingcart/get/" + page).get().build();

		Server.getSharedClient().newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				try {
					final Page<ShoppingCart> data = new ObjectMapper()
							.readValue(arg1.body().string(), new TypeReference<Page<ShoppingCart>>() {
							});
					getActivity().runOnUiThread(new Runnable() {

						@Override
						public void run() {
							ShoppingCartFragment.this.listData = data.getContent();
							ShoppingCartFragment.this.page = data.getNumber();
							ShoppingCartFragment.this.initData();
							ShoppingCartFragment.this.exlistAdapter.notifyDataSetChanged();
//							ShoppingCartFragment.this.collapse();
							ShoppingCartFragment.this.expand();
						}
					});
				} catch (final Exception e) {
					getActivity().runOnUiThread(new Runnable() {

						@Override
						public void run() {
							new AlertDialog.Builder(getActivity())
							.setMessage(e.getMessage())
							.show();
						}
					});
				}
			}

			@Override
			public void onFailure(Call arg0, IOException arg1) {
				// TODO Auto-generated method stub

			}
		});
	}

	private static class GoodsInfoHolder{
		GoodsPicture imgGoods;
		TextView tvGoodsName;
		TextView tvGoodsType;
		TextView tvGoodsPrice;
		TextView tvGoodsQuantity;
		CheckBox cbCheck;
		ImageView ivDelete;
	}

	private static class ShopInfoHolder {
		CheckBox cbShopAll;
		TextView tvShopName;
		AvatarView imgAvatar;
	}

	public class myAdapter extends BaseExpandableListAdapter {
		View shopView = null;
		View goodsView = null;
		@Override
		public int getGroupCount() {
			return groupList.size();
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return dataset.get(groupList.get(groupPosition).getShopName()).size();
		}

		@Override
		public Object getGroup(int groupPosition) {
			return dataset.get(groupList.get(groupPosition).getShopName());
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return dataset.get(groupList.get(groupPosition).getShopName()).get(childPosition);
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, final ViewGroup parent) {
			ShopInfoHolder sHolder;
			if(convertView == null) {
				sHolder = new ShopInfoHolder();
				LayoutInflater inflater = LayoutInflater.from(getActivity());
				shopView = inflater.inflate(R.layout.list_item_cart_shop, null);
				sHolder.cbShopAll = (CheckBox) shopView.findViewById(R.id.select_shop_all);
				sHolder.tvShopName = (TextView) shopView.findViewById(R.id.label_shop_name);
				sHolder.imgAvatar = (AvatarView) shopView.findViewById(R.id.avatar);
				shopView.setTag(sHolder);
			} else {
				shopView = convertView;
				sHolder = (ShopInfoHolder) shopView.getTag();
			}
			Log.d("position", groupPosition + "");
			final Shop shop = groupList.get(groupPosition);
			if(shop != null) {
				sHolder.tvShopName.setText(groupList.get(groupPosition).getShopName());
				//				sHolder.imgAvatar.load(Server.serverAdress + groupList.get(groupPosition).getShopImage());
				sHolder.cbShopAll.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						View view = null;
						shop.setSelected(((CheckBox)v).isChecked());
						selectShop(groupPosition, ((CheckBox)v).isChecked());
						Log.d("goupSelect", ((CheckBox)v).isChecked() + "");
						Log.d("isAllSelectedAAA", isAllSelected + "");
						if(isAllSelected == false) {

							selectAll.setChecked(isAllSelected);
						}
					}
				});
				sHolder.cbShopAll.setChecked(shop.isSelected());
			}
			return shopView;
		}

		@Override
		public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView,
				ViewGroup parent) {
			final GoodsInfoHolder gHolder;
			if(convertView == null) {
				gHolder = new GoodsInfoHolder();
				LayoutInflater inflater = LayoutInflater.from(getActivity());
				goodsView = inflater.inflate(R.layout.list_item_cart_goods_info, null);
				gHolder.imgGoods = (GoodsPicture) goodsView.findViewById(R.id.goods_image);
				gHolder.tvGoodsName = (TextView) goodsView.findViewById(R.id.goods_name);
				gHolder.tvGoodsType = (TextView) goodsView.findViewById(R.id.goods_type);
				gHolder.tvGoodsQuantity = (TextView) goodsView.findViewById(R.id.goods_quantity);
				gHolder.tvGoodsPrice = (TextView) goodsView.findViewById(R.id.goods_price);
				gHolder.cbCheck = (CheckBox) goodsView.findViewById(R.id.check_eatch);
				gHolder.ivDelete = (ImageView) goodsView.findViewById(R.id.delete);
				goodsView.setTag(gHolder);
			} else {
				goodsView = convertView;
				gHolder = (GoodsInfoHolder) goodsView.getTag();
			}


			final ShoppingCart cart = ShoppingCartFragment.this.dataset.get(ShoppingCartFragment.this.groupList.get(groupPosition).getShopName()).get(childPosition);

			gHolder.imgGoods.load(Server.serverAdress + cart.getId().getGoods().getGoodsImage());
			gHolder.tvGoodsName.setText(cart.getId().getGoods().getGoodsName());
			gHolder.tvGoodsType.setText("类型： " + cart.getId().getGoods().getGoodsType());
			gHolder.tvGoodsPrice.setText("￥" + cart.getId().getGoods().getGoodsPrice());
			gHolder.tvGoodsQuantity.setText("×" + cart.getQuantity());
			gHolder.cbCheck.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					cart.getId().getGoods().setSelected(((CheckBox)v).isChecked());
					gHolder.cbCheck.setChecked(((CheckBox)v).isChecked());
					selectGoods(groupPosition, childPosition, ((CheckBox)v).isChecked());
				}
			});
			gHolder.cbCheck.setChecked(cart.getId().getGoods().isSelected());
			gHolder.ivDelete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					goDelete(cart.getId().getGoods().getId());
				}
			});

			return goodsView;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return false;
		}

	}

	void selectGoods(int groupPosition, int childPosition, boolean isSelected) {
		boolean isAllSameState = true;
		boolean isAllGroupSameState = true;
		List<ShoppingCart> cartList = dataset.get(groupList.get(groupPosition).getShopName());
		for(int i = 0; i < cartList.size(); i++) {
			if(isSelected != cartList.get(i).getId().getGoods().isSelected()) {
				isAllSameState = false;
				break;
			}
		}
		Log.d("isAllS", isAllSameState + "");
		Log.d("isAllISSelec", isSelected + "");
		if(isAllSameState) {
			groupList.get(groupPosition).setSelected(isSelected);
			for(int i = 0; i < groupList.size(); i++) {
				if(groupList.get(i).isSelected() != true) {
					isAllGroupSameState = false;
				}
			}
			if(isAllGroupSameState) {
				isAllSelected = true;
				selectAll.setChecked(isAllSelected);
			} else {
				isAllSelected = false;
				selectAll.setChecked(isAllSelected);
			}
		} else {
			groupList.get(groupPosition).setSelected(false);
			isAllSelected = false;
			selectAll.setChecked(false);
		}
		exlistAdapter.notifyDataSetChanged();
		calculate();
	}

	void selectShop(int groupPosition, boolean isSelected) {
		boolean isAllGroupSameState = true;
		Shop shop = groupList.get(groupPosition);
		List<ShoppingCart> cartList = dataset.get(shop.getShopName());
		for(int i = 0; i < cartList.size(); i++) {
			cartList.get(i).getId().getGoods().setSelected(isSelected);
			Log.d("cartIsSelect", isSelected + "");
		}
		for(int i = 0; i < groupList.size(); i++) {
			if(groupList.get(i).isSelected() != true) {
				isAllGroupSameState = false;
			}
		}
		if(isAllGroupSameState) {
			isAllSelected = true;
			selectAll.setChecked(true);
		} else {
			isAllSelected = false;
		}
		exlistAdapter.notifyDataSetChanged();
		calculate();
	}

	void goDelete(final int goodsId) {
		new AlertDialog.Builder(getActivity()).setMessage("确定移除商品?")
		.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				onDelete(goodsId);
			}
		})
		.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}

		}).show();
	}
	
	protected void onDelete(int goodsId) {
		Request request = Server.requestBuilderWithApi("shoppingcart/delete/" + goodsId).get().build();
		Server.getSharedClient().newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(getActivity(), "删除成功", Toast.LENGTH_SHORT).show();
						ShoppingCartFragment.this.reload();
					}
				});
			}

			@Override
			public void onFailure(Call arg0, IOException arg1) {
			}
		});
	}
}
