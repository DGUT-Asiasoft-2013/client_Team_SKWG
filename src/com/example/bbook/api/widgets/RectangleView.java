package com.example.bbook.api.widgets;

import java.io.IOException;

import com.example.bbook.api.Server;
import com.example.bbook.api.User;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Shader.TileMode;
import android.hardware.Camera.Size;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RectangleView extends View{

	public RectangleView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public RectangleView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public RectangleView(Context context) {
		super(context);
	}

	Paint paint;
	float srcWidth, srcHeight;
	Handler mainThreadHandler = new Handler();

	public void setBitmap(Bitmap bmp) {
		if(bmp==null) {
			paint = new Paint();
			paint.setColor(Color.GRAY);
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeWidth(1);
			paint.setPathEffect(new DashPathEffect(new float[]{5, 10, 15, 20}, 0));
			paint.setAntiAlias(true);
		}else{
			paint = new Paint();
			paint.setShader(new BitmapShader(bmp, TileMode.REPEAT, TileMode.REPEAT));
			paint.setAntiAlias(true);

			srcWidth = bmp.getWidth();
			srcHeight = bmp.getHeight();	
		}

		invalidate();
	}

	public void load(String url) {

		OkHttpClient client = new OkHttpClient();

		Request request = new Request.Builder()
				.url(url)
				.method("get", null)
				.build();

		client.newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				byte[] bytes = arg1.body().bytes();

				final Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

				mainThreadHandler.post(new Runnable() {

					@Override
					public void run() {
						setBitmap(bmp);
					}
				});
			}

			@Override
			public void onFailure(Call arg0, IOException arg1) {

			}
		});
	}

	public void load(User user) {
		load(Server.serverAdress + user.getAvatar());
	}
	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		if(paint!=null){
			canvas.save();

			float dstWidth = getWidth();
			float dstHeight = getHeight();

			float scaleX = srcWidth / dstWidth;
			float scaleY = srcHeight / dstHeight;

			canvas.scale(1/scaleX, 1/scaleY);

			canvas.drawRect(0,0,srcWidth,srcHeight,paint);

			canvas.restore();
		}
	}
//	//  可获取焦点
//	public void focusable(boolean isfocusable){
//		if (isfocusable) {
//			this.setFocusable(false);
//		}
//	}
//
//	//  可点击
//	public void clickable(boolean isclickable){
//		if (isclickable) {
//			this.setClickable(true);
//		}
//	}
}
