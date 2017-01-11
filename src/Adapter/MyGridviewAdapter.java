package Adapter;

import java.io.IOException;
import java.util.List;

import com.example.bbook.BookDetailActivity;
import com.example.bbook.R;
import com.example.bbook.SearchBooksActivity;
import com.example.bbook.ShopActivity;
import com.example.bbook.api.Goods;
import com.example.bbook.api.Server;
import com.example.bbook.api.Shop;
import com.example.bbook.api.widgets.GoodsPicture;
import com.example.bbook.fragments.pages.HomePage;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.Response;

public class MyGridviewAdapter extends BaseAdapter{
	List<Goods> data;
	int page=0;
	Context context;

	Goods goods;
	//商品图片、店铺名、价格、商品名、销量
	GoodsPicture goodsPicture;
	TextView shopName;
	TextView goodsPrice;
	TextView goodsName;
	TextView goodsSales;
	ImageView addToShopCar;
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = null;

		if(convertView==null){
			LayoutInflater inflater = LayoutInflater.from(parent.getContext());
			view = inflater.inflate(R.layout.goods_grid_item, null);	

		}else{
			view = convertView;
		}
		goodsPicture=(GoodsPicture) view.findViewById(R.id.picture);
		goodsName=(TextView) view.findViewById(R.id.goods_name);
		goodsPrice=(TextView) view.findViewById(R.id.price);
		shopName=(TextView) view.findViewById(R.id.id);
		goodsSales=(TextView) view.findViewById(R.id.goods_sales);
		addToShopCar =(ImageView) view.findViewById(R.id.shopping_car);
		
		goods=data.get(position);
		goodsPicture.load(Server.serverAdress+goods.getGoodsImage());
		goodsName.setText(goods.getGoodsName());
		goodsPrice.setText("￥"+goods.getGoodsPrice());
		shopName.setText("商家:"+goods.getShop().getShopName());
		goodsSales.setText("销量:"+goods.getGoodsSales());
		
		addToShopCar.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				addToShopCar(position);
			}
		});
		
		goodsPicture.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				goBookDetailActivity( position);
				
			}
		});
		shopName.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				goShopActivity(position);
			}
		});
		
	
		return view;
	}
	
	
	
	public void goBookDetailActivity(int position){
		goods=data.get(position);
		Intent intent=new Intent(context,BookDetailActivity.class);
		intent.putExtra("goods", goods);
		context.startActivity(intent);
	}
	public void goShopActivity(int position){
		Shop shop=data.get(position).getShop();
		Intent intent=new Intent(context,ShopActivity.class);
		intent.putExtra("shop", shop);
		context.startActivity(intent);
	}
	
	public void addToShopCar(int position){
		int quantity=1;
		goods=data.get(position);
		MultipartBody body = new MultipartBody.Builder()
				.addFormDataPart("quantity", quantity + "").build();


		Request request = Server.requestBuilderWithApi("shoppingcart/add/" + goods.getId())
				.method("post", null).post(body).build();

		Server.getSharedClient().newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(final Call arg0, final Response arg1) throws IOException {
				final String body = arg1.body().string();
				((Activity) context).runOnUiThread(new Runnable() {

					@Override
					public void run() {
						successed(arg0, body);
					}
				});
			}

			@Override
			public void onFailure(final Call arg0, final IOException arg1) {
				((Activity) context).runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Failture(arg0, arg1);
					}
				});
			}
		});
	}
	void successed(Call arg0, String responseBody){
		Toast.makeText(context, "加入购物车成功", Toast.LENGTH_SHORT).show();;
	}
	void Failture(Call arg0, Exception arg1) {
		new AlertDialog.Builder(context)
		.setTitle("失败")
		.setMessage(arg1.getLocalizedMessage())
		.show();
	}

}
