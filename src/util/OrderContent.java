package util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

public interface OrderContent {

	public int getLayout();
	public boolean isClickable();
	public View getView(Context context, View convertView, LayoutInflater inflate);
}
