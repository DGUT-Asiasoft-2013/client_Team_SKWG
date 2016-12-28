package com.example.bbook.fragments.pages;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.example.bbook.BuyActivity;
import com.example.bbook.R;
import com.example.bbook.api.Goods;
import com.example.bbook.api.Page;
import com.example.bbook.api.Server;
import com.example.bbook.api.entity.ShoppingCart;
import com.example.bbook.api.entity.ShoppingCart.Key;
import com.example.bbook.api.widgets.GoodsPicture;
import com.example.bbook.api.widgets.TitleBarFragment;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
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
	ListView list;
	CheckBox selectAll;
	TextView tvSum;
	Button btnPay;
	int page = 0;
	int selectedCount = 0;
	double sum = 00.00;;
	boolean isAllSelected;
	List<Goods> selectedGoods = new ArrayList<Goods>();
	List<ShoppingCart>  listData;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (view==null) {
			view=inflater.inflate(R.layout.fragment_page_shoppingcart, null);

			titleBar = (TitleBarFragment) getFragmentManager().findFragmentById(R.id.shoppingcart_titlebar);
			titleBar.setBtnBackState(false);
			titleBar.setBtnNextState(false);
			titleBar.setSplitLineState(false);
			titleBar.setTitleName("购物车", 16);
		}
		list = (ListView) view.findViewById(R.id.list);
		list.setDivider(null);
		selectAll = (CheckBox) view.findViewById(R.id.select_all);
		tvSum = (TextView) view.findViewById(R.id.sum);
		btnPay = (Button) view.findViewById(R.id.pay);
		tvSum.setText(sum + "");
		list.setAdapter(listAdapter);
		btnPay.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				goBuy();
			}
		});
		selectAll.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked == true) {
					for(int i = 0; i < list.getChildCount(); i++) {
						View view = list.getChildAt(i);
						 CheckBox cbCheckEatch = (CheckBox) view.findViewById(R.id.check_eatch);
						 cbCheckEatch.setChecked(true);
						 isAllSelected = true;
					}
				} else if (isChecked == false && isAllSelected == true){
					for(int i = 0; i < list.getChildCount(); i++) {
						 View view = list.getChildAt(i);
						 CheckBox cbCheckEatch = (CheckBox) view.findViewById(R.id.check_eatch);
						 cbCheckEatch.setChecked(false);
						 isAllSelected = false;
					}
				} else {
					
				}
			}
		});
		
		return view;
	}

	protected void goBuy() {
		Intent itnt = new Intent(getActivity(), BuyActivity.class);
		itnt.putExtra("selectedGoods", (Serializable)selectedGoods);
		
		startActivity(itnt);
		
	}

	@Override
	public void onResume() {
		super.onResume();
//		sum = 00.00;
//		selectedCount = 0;
//		setViewText();
//		selectAll.setChecked(false);
		reload();
	}

	private void init(View view) {
		list = (ListView) view.findViewById(R.id.list);
		selectAll = (CheckBox) view.findViewById(R.id.select_all);
		tvSum = (TextView) view.findViewById(R.id.sum);
		btnPay = (Button) view.findViewById(R.id.pay);
	}
	
	private void setViewText() {
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
							listAdapter.notifyDataSetChanged();
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

	private class GoodsInfoHolder{
		GoodsPicture imgGoods;
		TextView tvGoodsName;
		TextView tvGoodsType;
		TextView tvGoodsPrice;
		TextView tvGoodsQuantity;
		CheckBox cbCheck;
		ImageView ivDelete;
	}

	BaseAdapter listAdapter = new BaseAdapter() {
		View view = null;
		GoodsInfoHolder infoHolder;
		HashMap<Integer, Boolean> isSelected;
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null) {
				infoHolder = new GoodsInfoHolder();
				LayoutInflater inflater= LayoutInflater.from(parent.getContext());
				view =inflater.inflate(R.layout.list_item_cart_goods_info, null);
				infoHolder.imgGoods = (GoodsPicture) view.findViewById(R.id.goods_image);
				infoHolder.tvGoodsName = (TextView) view.findViewById(R.id.goods_name);
				infoHolder.tvGoodsType = (TextView) view.findViewById(R.id.goods_type);
				infoHolder.tvGoodsQuantity = (TextView) view.findViewById(R.id.goods_quantity);
				infoHolder.tvGoodsPrice = (TextView) view.findViewById(R.id.goods_price);
				infoHolder.cbCheck = (CheckBox) view.findViewById(R.id.check_eatch);
				infoHolder.ivDelete = (ImageView) view.findViewById(R.id.delete);
				view.setTag(infoHolder);
			} else {
				view = convertView;
				infoHolder = (GoodsInfoHolder) view.getTag();
			}
			final ShoppingCart cart = listData.get(position);
			if(cart != null) {
				infoHolder.imgGoods.load(Server.serverAdress + cart.getId().getGoods().getGoodsImage());
				infoHolder.tvGoodsName.setText(cart.getId().getGoods().getGoodsName());
				infoHolder.tvGoodsType.setText("类型： " + cart.getId().getGoods().getGoodsType());
				infoHolder.tvGoodsPrice.setText("￥" + cart.getId().getGoods().getGoodsPrice());
				infoHolder.tvGoodsQuantity.setText("×" + cart.getQuantity());
				infoHolder.ivDelete.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						ShoppingCartFragment.this.onDelete(cart.getId().getGoods().getId());
					}
				});
				infoHolder.cbCheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					boolean isCheck = false;
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						if(isCheck) {
							sum -= Double.parseDouble(cart.getId().getGoods().getGoodsPrice()) * cart.getQuantity();
							isCheck = !isCheck;
							selectedCount -= 1;
							ShoppingCartFragment.this.isAllSelected = false;
							ShoppingCartFragment.this.selectAll.setChecked(false);
							Goods goods = cart.getId().getGoods();
							goods.setQuantity(cart.getQuantity());
							ShoppingCartFragment.this.selectedGoods.add(goods);
							setViewText();
						} else {
							isCheck= !isCheck;
							selectedCount +=1;
							sum += Double.parseDouble(cart.getId().getGoods().getGoodsPrice()) * cart.getQuantity();
							Goods goods = cart.getId().getGoods();
							goods.setQuantity(cart.getQuantity());
							ShoppingCartFragment.this.selectedGoods.add(goods);
							if(selectedCount == ShoppingCartFragment.this.listData.size()) {
								ShoppingCartFragment.this.selectAll.setChecked(true);
							}
							setViewText();
						}
					}
				});
			}
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
			return listData == null ? 0 : listData.size();
		}

		public HashMap<Integer, Boolean> getIsSelected() {
			return isSelected;
		}

		public void setIsSelected(HashMap<Integer, Boolean> isSelected) {
			this.isSelected = isSelected;
		}
		
		
	};
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
				// TODO Auto-generated method stub
				
			}
		});
	}
}
