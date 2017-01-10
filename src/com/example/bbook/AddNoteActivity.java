package com.example.bbook;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import com.example.bbook.R;
import com.example.bbook.api.Server;
import com.fasterxml.jackson.databind.util.ByteBufferBackedInputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleAdapter.ViewBinder;
import inputcells.PictureInputCellFragment;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddNoteActivity extends Activity {
	ImageButton ibtn_back,ibtn_next;
	EditText articleTitle;
	EditText articleText;
	private GridView gridView1;                   //网格显示缩略图
	private final int IMAGE_OPEN = 0x000002;        //打开图片标记
	private static final int TAKE_PICTURE = 0x000001;   //拍照
	private String pathImage;                       //选择图片路径
	private Bitmap bmp;                               //导入临时图片
	private ArrayList<HashMap<String, Bitmap>> imageItem;
	private SimpleAdapter simpleAdapter;     //适配器
	private File tempfile;
	byte[] pngData;

	private View parentView;
	private PopupWindow pop = null;
	private LinearLayout ll_popup;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		parentView = getLayoutInflater().inflate(R.layout.activity_addnote, null);
		setContentView(parentView);

		articleTitle= (EditText)findViewById(R.id.editText1);
		articleText= (EditText)findViewById(R.id.editText2);


		ibtn_back=(ImageButton)findViewById(R.id.btn_back);
		ibtn_next=(ImageButton)findViewById(R.id.btn_next);

		ibtn_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
				overridePendingTransition(0, R.anim.slide_out_right);
			}
		});
		ibtn_next.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				sendNote();
			}
		});

		Init();
	}

	//gridview and popup
	void Init(){
		pop = new PopupWindow(AddNoteActivity.this);

		View view = getLayoutInflater().inflate(R.layout.item_popupwindows, null);

		ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);

		pop.setWidth(LayoutParams.MATCH_PARENT);
		pop.setHeight(LayoutParams.WRAP_CONTENT);
		pop.setBackgroundDrawable(new BitmapDrawable());
		pop.setFocusable(true);
		pop.setOutsideTouchable(true);
		pop.setContentView(view);

		RelativeLayout parent = (RelativeLayout) view.findViewById(R.id.parent);
		Button bt1 = (Button) view
				.findViewById(R.id.item_popupwindows_camera);
		Button bt2 = (Button) view
				.findViewById(R.id.item_popupwindows_Photo);
		Button bt3 = (Button) view
				.findViewById(R.id.item_popupwindows_cancel);
		parent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				pop.dismiss();
				ll_popup.clearAnimation();
			}
		});
		bt1.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				photo();
				pop.dismiss();
				ll_popup.clearAnimation();
			}
		});
		bt2.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				//选择图片
				Intent intent = new Intent(Intent.ACTION_PICK,       
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);  
				startActivityForResult(intent, IMAGE_OPEN);  
				overridePendingTransition(R.anim.activity_translate_in, R.anim.activity_translate_out);
				pop.dismiss();
				ll_popup.clearAnimation();
			}
		});
		bt3.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				pop.dismiss();
				ll_popup.clearAnimation();
			}
		});

		//获取控件对象
		gridView1 = (GridView) findViewById(R.id.noScrollgridview);

		/*
		 * 载入默认图片添加图片加号
		 * 通过适配器实现
		 * SimpleAdapter参数imageItem为数据源 R.layout.griditem_addpic为布局
		 */
		bmp = BitmapFactory.decodeResource(getResources(), R.drawable.icon_addpic_focused); //加号
		imageItem = new ArrayList<HashMap<String, Bitmap>>();
		HashMap<String, Bitmap> map = new HashMap<String, Bitmap>();
		map.put("itemImage", bmp);
		imageItem.add(map);
		simpleAdapter = new SimpleAdapter(this, 
				imageItem, R.layout.griditem_addpic, 
				new String[] { "itemImage"}, new int[] { R.id.imageView1}); 

		simpleAdapter.setViewBinder(new ViewBinder() {  
			@Override  
			public boolean setViewValue(View view, Object data,  
					String textRepresentation) {  
				// TODO Auto-generated method stub  
				if(view instanceof ImageView && data instanceof Bitmap){  
					ImageView i = (ImageView)view;  
					i.setImageBitmap((Bitmap) data);  
					return true;  
				}  
				return false;  
			}
		});  
		gridView1.setAdapter(simpleAdapter);

		gridView1.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id)
			{

				if(position == 0) { //点击图片位置为+ 0对应0张图片
					if( imageItem.size() == 4) { //第一张为默认图片
						Toast.makeText(AddNoteActivity.this, "图片数3张已满", Toast.LENGTH_SHORT).show();
					}
					if(imageItem.size()<4){
						// 隐藏软键盘
						InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0); 

						//弹出pop窗口
						ll_popup.startAnimation(AnimationUtils.loadAnimation(AddNoteActivity.this,R.anim.activity_translate_in));
						pop.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);

					}
				}
				else {
					dialog(position);
					//Toast.makeText(MainActivity.this, "点击第" + (position + 1) + " 号图片", 
					//		Toast.LENGTH_SHORT).show();
				}

			}
		});

	}

	//拍照
	public void photo() {
		File DatalDir = Environment.getExternalStorageDirectory();
		File myDir = new File(DatalDir, "/DCIM/Camera");
		myDir.mkdirs();
		String mDirectoryname = DatalDir.toString() + "/DCIM/Camera";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-hhmmss",
				Locale.SIMPLIFIED_CHINESE);
		tempfile = new File(mDirectoryname, sdf.format(new Date())
				+ ".jpg");
		if (tempfile.isFile())
			tempfile.delete();
		Uri Imagefile = Uri.fromFile(tempfile);

		Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Imagefile);
		startActivityForResult(cameraIntent, TAKE_PICTURE);
	}

	//获取图片路径 响应startActivityForResult  
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
		super.onActivityResult(requestCode, resultCode, data);        
		switch (requestCode) {
		case IMAGE_OPEN:
			if(resultCode==RESULT_OK){
				Uri uri = data.getData();  
				if (!TextUtils.isEmpty(uri.getAuthority())) {  
					//查询选择图片  
					Cursor cursor = getContentResolver().query(  
							uri,  
							new String[] { MediaStore.Images.Media.DATA },  
							null,   
							null,   
							null);  
					//返回 没找到选择图片  
					if (null == cursor) {  
						return;  
					}  
					//光标移动至开头 获取图片路径  
					cursor.moveToFirst();  
					pathImage = cursor.getString(cursor  
							.getColumnIndex(MediaStore.Images.Media.DATA));
					//					Log.d("lujing", pathImage);
				}
			}
			break;

		case TAKE_PICTURE:
			if(resultCode==RESULT_OK){
				pathImage=tempfile.getPath();
				//				Log.d("lujing", pathImage);
			}
			break;

		default:
			break;
		}
	}

	//刷新图片
	@Override
	protected void onResume() {
		super.onResume();
		if(!TextUtils.isEmpty(pathImage)){
			Bitmap addbmp=BitmapFactory.decodeFile(pathImage);
			HashMap<String, Bitmap> map = new HashMap<String, Bitmap>();
			map.put("itemImage", addbmp);
			imageItem.add(map);
			Log.d("123", imageItem.toString());
			for(int i=0;i<imageItem.size();i++){
				Log.d("bb", imageItem.get(i).values().toString());
			}
			simpleAdapter = new SimpleAdapter(this, 
					imageItem, R.layout.griditem_addpic, 
					new String[] { "itemImage"}, new int[] { R.id.imageView1}); 
			simpleAdapter.setViewBinder(new ViewBinder() {  
				@Override  
				public boolean setViewValue(View view, Object data,  
						String textRepresentation) {  
					// TODO Auto-generated method stub  
					if(view instanceof ImageView && data instanceof Bitmap){  
						ImageView i = (ImageView)view;  
						i.setImageBitmap((Bitmap) data);  
						return true;  
					}  
					return false;  
				}
			}); 
			gridView1.setAdapter(simpleAdapter);
			simpleAdapter.notifyDataSetChanged();
			//刷新后释放防止手机休眠后自动添加
			pathImage = null;
		}
	}

	/*
	 * Dialog对话框提示用户删除操作
	 * position为删除图片位置
	 */
	protected void dialog(final int position) {
		AlertDialog.Builder builder = new Builder(AddNoteActivity.this);
		builder.setMessage("确认移除已添加图片吗？");
		builder.setTitle("提示");
		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				imageItem.remove(position);
				simpleAdapter.notifyDataSetChanged();
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}

	void sendNote(){

		String title = articleTitle.getText().toString();
		String text = articleText.getText().toString();
		String articleImgName = new SimpleDateFormat("MMddHHmmss").format(new Date());
		if(title==null||title.isEmpty()){
			new AlertDialog.Builder(this)
			.setMessage("请输入标题!")
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setPositiveButton("OK",null)
			.show();
			return;
		}
		if(text==null||text.isEmpty()){
			new AlertDialog.Builder(this)
			.setMessage("请输入内容!")
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setPositiveButton("OK",null)
			.show();
			return;
		}

		OkHttpClient client = Server.getSharedClient();

		MultipartBody.Builder requestBuilder = new MultipartBody.Builder()
				.addFormDataPart("title", title)
				.addFormDataPart("text", text)
				.addFormDataPart("articleImgName", articleImgName);

		//上传图片
		for (int i = 1; i < imageItem.size(); i++) {
			Bitmap bitmap=imageItem.get(i).get("itemImage");
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bitmap.compress(CompressFormat.PNG, 100, baos);
			pngData = baos.toByteArray();
			requestBuilder.addFormDataPart("listImage", "listImage"+i, RequestBody.create(MediaType.parse("image/png"), pngData));
		}

		Request request = Server.requestBuilderWithApi("article")
				.method("post", null)
				.post(requestBuilder.build())
				.build();

		client.newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(final Call arg0, final Response arg1) throws IOException {
				try{	     
					final String responString = arg1.body().toString();
					runOnUiThread(new Runnable() {
						public void run() {
							AddNoteActivity.this.onResponse(arg0,responString);
						}
					});
				}catch (final Exception e) {
					runOnUiThread(new Runnable() {
						public void run() {
							AddNoteActivity.this.onFailure(arg0, e);
						}
					});
				}
			}

			@Override
			public void onFailure(final Call arg0, final IOException arg1) {
				runOnUiThread(new Runnable() {
					public void run() {
						AddNoteActivity.this.onFailure(arg0, arg1);
					}
				});
			}
		});

	}

	void onResponse(Call arg0, String responseBody) {
		new AlertDialog.Builder(this)
		.setTitle("发布成功")
		.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
				overridePendingTransition(0,R.anim.slide_out_right);
			}
		})
		.show();
	}

	void onFailure(Call arg0, Exception arg1) {
		new AlertDialog.Builder(this)
		.setTitle("发布失败")
		.setMessage(arg1.getLocalizedMessage())
		.setNegativeButton("OK", null)
		.show();
	}

}
