package com.sation.knxcontroller.widget;

import com.sation.knxcontroller.R;

import android.annotation.SuppressLint;
import android.content.Context; 
 
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button; 
import android.widget.TextView;

public class CustomPopDialogFragment2 extends DialogFragment implements View.OnClickListener {

	Context context;
	private TextView content_tv;
	private TextView title_tv;
	private String content = "确实要离开吗？";
	private String title = "确认";
 
	/*
	public CustomPopDialogFragment(String content_tv, String title) {  
		this.content = content_tv;
		this.title = title; 
	}
	*/
	public CustomPopDialogFragment2() {
		super(); 
	}

	/**
	 * 也可以在外部调用
	 * 
	 * @param title
	 * @param content
	 */
	public void setDialog(String title, String content) {
		title_tv.setText(title);
		content_tv.setText(content);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(STYLE_NO_TITLE, 0);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		context = getActivity();
		View view = inflater.inflate(R.layout.customer_dialog_layout, null);

		Button common_dialog_btn_ok = (Button) view.findViewById(R.id.common_dialog_btn_ok);
		Button common_dialog_btn_cancel = (Button) view.findViewById(R.id.common_dialog_btn_cancel);
		title_tv = (TextView) view.findViewById(R.id.common_dialog_title_text);
		content_tv = (TextView) view
				.findViewById(R.id.common_dialog_content_tv);
		setDialog(title, content);
		common_dialog_btn_cancel.setOnClickListener(this);
		common_dialog_btn_ok.setOnClickListener(this);
		return view;
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.common_dialog_btn_ok: 
			this.dismiss();
			break;

		default:
			this.dismiss();
			break;
		}

	}

}
