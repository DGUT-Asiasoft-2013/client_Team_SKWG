package Adapter;

import java.util.List;

import com.example.bbook.R;
import com.example.bbook.api.Goods;
import com.example.bbook.api.Server;
import com.example.bbook.api.widgets.GoodsPicture;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MyGridviewAdapter extends BaseAdapter {
	List<Goods> data;
	int page=0;
	Context context;

	Goods goods;
	//商品图片、店铺名、价格、商品名、销量
	GoodsPicture goodsPicture;
	TextView textview;
	TextView goodsPrice;
	TextView goodsName;
	TextView goodsSales;

	public void setData(List<Goods> data,int page){
		this.data = data;
		this.page=page;
		notifyDataSetChanged();
	}
	

	public MyGridviewAdapter(Context context) {
		// TODO Auto-generated constructor stub
		this.context=context;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data == null? 0 : data.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;

		if(convertView==null){
			LayoutInflater inflater = LayoutInflater.from(parent.getContext());
			view = inflater.inflate(R.layout.goods_grid_item, null);	

		}else{
			view = convertView;
		}
		textview=(TextView) view.findViewById(R.id.id);
		goodsPrice=(TextView) view.findViewById(R.id.price);
		goodsPicture=(GoodsPicture) view.findViewById(R.id.picture);
		goodsName=(TextView) view.findViewById(R.id.goods_name);
		goodsSales=(TextView) view.findViewById(R.id.goods_sales);

		goods=data.get(position);
		textview.setText("商家:"+goods.getShop().getShopName());
		goodsPrice.setText("价格："+goods.getGoodsPrice());
		goodsPicture.load(Server.serverAdress+goods.getGoodsImage());
		goodsName.setText("书名:"+goods.getGoodsName());
		goodsSales.setText("销量:"+goods.getGoodsSales());
	
		return view;
	}


}
