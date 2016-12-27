package com.example.bbook.api.widgets;

import com.example.bbook.R;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ChangeItemFragment extends Fragment {

	TextView messageTitle, messageEdit;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_widget_change_textview, null);

		messageTitle = (TextView) view.findViewById(R.id.tx_input);
		messageEdit = (TextView) view.findViewById(R.id.tx_edit);
		return view;
	}

	// 更改左标题内容
	public void setmessageTitleText(String messagetitle, float textSize) {
		messageTitle.setTextSize(textSize);
		messageTitle.setText(messagetitle);
	}

	// 更改右标题内容及颜色
	public void setmessageEditText(String messageedit, float textSize, int color) {
		messageEdit.setTextSize(textSize);
		messageEdit.setText(messageedit);
		messageEdit.setTextColor(color);
	}

	public String getText() {
		return messageEdit.getText().toString();
	}
}
