package com.example.bbook.fragments.pages;

import com.example.bbook.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class AvatarAndNameFragment extends Fragment {

	ImageView pictureView;
	TextView idView;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view =inflater.inflate(R.layout.fragment_avatar_name,null);
		
//		pictureView=(ImageView) view.findViewById(R.id.picture);
//		idView=(TextView) view.findViewById(R.id.id);
//		
//		pictureView.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				Toast.makeText(getActivity(), "picture",Toast.LENGTH_SHORT).show();
//			}
//		});
//		
//		idView.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				Toast.makeText(getActivity(), "ID",Toast.LENGTH_SHORT).show();;
//			}
//		});
		return view;
	}
}
