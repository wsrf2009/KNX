package com.zyyknx.android.widget; 

import com.zyyknx.android.R;
import com.zyyknx.android.ZyyKNXApp;

import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.text.Spanned;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

/* 使用
 public void showDialogFragment(final String msg) {

 dialogFragment = new CustomPopDialogFragment() {
 @Override
 protected View customContent(LayoutInflater inflater) {
 View view = inflater.inflate(R.layout.tips, null, false);
 TextView txtTips = (TextView) view.findViewById(R.id.txtTips);

 txtTips.setText(msg);
 ImageButton cancelBtn = (ImageButton) view .findViewById(R.id.cancelBtn);
 cancelBtn.setOnClickListener(new OnClickListener() {
 public void onClick(View v) {

 dismiss();
 }
 }); 
 return view;
 }

 @Override
 protected String customTitle() {
 return "业务呼叫";
 }
 };
 dialogFragment.show(getFragmentManager(), "tips");
 }



 CustomPopDialogFragment mCustomPopDialogFragment = new CustomPopDialogFragment();  
 mCustomPopDialogFragment.setTitle("温馨提示");  
 mCustomPopDialogFragment.setMessage("您确退出吗?"); 
 mCustomPopDialogFragment.setIsProgressBarVisable(true);
 mCustomPopDialogFragment.setBackButton("返 回", new DialogInterface.OnClickListener() {  
 @Override  
 public void onClick(DialogInterface arg0, int arg1) {  
 // 关闭对话框  

 }  
 });  
 mCustomPopDialogFragment.setConfirmButton("确 定", new DialogInterface.OnClickListener() {  
 @Override  
 public void onClick(DialogInterface arg0, int arg1) {  
 // 退出电子书  

 }  
 }); 
 */

public class CustomPopDialogFragment extends DialogFragment {
	private View view;
	private View closeBtn;
	private Context context;

	public CustomPopDialogFragment() {
		super();
		this.context = ZyyKNXApp.getInstance().getApplicationContext();
	}

	private double widthPercent;
	private double heightPercent;
	/**
	 * Helper class for creating a custom dialog
	 */
	private String title; // 对话框标题
	private String message; // 对话框内容
	private String canceButtonText; // 对话框返回按钮文本
	private String confirmButtonText; // 对话框确定文本
	private String neutralButtonText; // 对话框确定文本
	private boolean isProgressBarVisable;
	private View contentView;

	// 对话框按钮监听事件
	private DialogInterface.OnClickListener canceButtonClickListener,
			neutralButtonClickListener, confirmButtonClickListener;

	/**
	 * Set the Dialog title from resource
	 * 
	 * @param title
	 * @return
	 */
	public void setTitle(int title) {
		this.title = (String) context.getString(title);
	}

	/**
	 * 使用字符串设置对话框标题信息
	 * 
	 * @param title
	 * @return
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Set the Dialog message from resource
	 * 
	 * @param title
	 * @return
	 */
	public void setMessage(int message) {
		this.message = (String) context.getString(message);
	}

	/**
	 * 使用字符串设置对话框消息
	 * 
	 * @param title
	 * @return
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * 设置自定义的对话框内容
	 * 
	 * @param v
	 * @return
	 */
	public void setContentView(View v) {
		this.contentView = v;
	}

	/**
	 * 设置自定义的对话框内容
	 * 
	 * @param v
	 * @return
	 */
	public void setContentView(int v) {
		final LayoutInflater inflater = LayoutInflater.from(getDialog().getContext());
		//this.contentView = inflater.inflate(R.layout.softupdate_progress, null);
	}

	/**
	 * 设置自定义的对话框内容
	 * 
	 * @param progressBarVisable
	 * @return
	 */
	public void setIsProgressBarVisable(boolean progressBarVisable) {
		this.isProgressBarVisable = progressBarVisable;
	}

	/**
	 * 设置back按钮的事件和文本
	 * 
	 * @param backButtonText
	 * @param listener
	 * @return
	 */
	public void setBackButton(String canceButtonText, DialogInterface.OnClickListener listener) {
		this.canceButtonText = canceButtonText;
		this.canceButtonClickListener = listener;
	}

	/**
	 * 设置确定按钮事件和文本
	 * 
	 * @param negativeButtonText
	 * @param listener
	 * @return
	 */
	public void setConfirmButton(String confirmButtonText, DialogInterface.OnClickListener listener) {
		this.confirmButtonText = confirmButtonText;
		this.confirmButtonClickListener = listener;
	}

	/**
	 * 设置确定按钮事件和文本
	 * 
	 * @param negativeButtonText
	 * @param listener
	 * @return
	 */
	public void setNeutralButton(String neutralButtonText, DialogInterface.OnClickListener listener) {
		this.neutralButtonText = neutralButtonText;
		this.neutralButtonClickListener = listener;
	}

	@Override
	public void onStart() {
		super.onStart();
		// getDialog().getWindow().setWindowAnimations(R.style.dialogWindowAnim);
		int dialogWidth = getDialog().getContext().getResources()
				.getDisplayMetrics().widthPixels;
		int dialogHeight = getDialog().getContext().getResources()
				.getDisplayMetrics().heightPixels;
		// getDialog().getWindow().setLayout((int)(dialogWidth * 0.8),
		// (int)(dialogHeight * 0.8));

		WindowManager.LayoutParams lp = getDialog().getWindow().getAttributes();
		if (this.widthPercent != 0) {
			lp.width = (int) (dialogWidth * widthPercent);
		} else {
			lp.width = (int) (dialogWidth * 0.9);
		}
		if (this.heightPercent != 0) {
			lp.height = (int) (dialogHeight * heightPercent);
		} else {
			lp.height = LayoutParams.WRAP_CONTENT;
		}
		// lp.width =(int)(dialogWidth * 0.9);
		// lp.height = LayoutParams.WRAP_CONTENT;
		getDialog().getWindow().setAttributes(lp);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setStyle(STYLE_NORMAL, android.R.style.Theme_Light_Panel);
		setStyle(STYLE_NORMAL, R.style.dialog);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getDialog().requestWindowFeature(STYLE_NO_TITLE);
		getDialog().setCanceledOnTouchOutside(false);
		view = inflater.inflate(R.layout.my_dialog, container, false);

		ViewGroup viewGroup = (ViewGroup) view.findViewById(R.id.dialogContent);
		if (title != null) {
			TextView titleView = (TextView) view.findViewById(R.id.dialogTitle);
			Spanned sp = Html.fromHtml(title);
			titleView.setText(sp);
		}
		// 设置对话框内容
		if (message != null) {
			TextView dialogTips = (TextView) view.findViewById(R.id.dialogTips);
			Spanned sp = Html.fromHtml(message);
			dialogTips.setText(sp);
		} else {
			view.findViewById(R.id.dialogTips).setVisibility(View.GONE);
		}
		if (isProgressBarVisable) {
			view.findViewById(R.id.dialogProgressBar).setVisibility(
					View.VISIBLE);
		} else {
			view.findViewById(R.id.dialogProgressBar).setVisibility(View.GONE);
		}
		if (contentView != null) { // 如果没有设置对话框内容，添加contentView到对话框主体
			view.findViewById(R.id.dialogTips).setVisibility(View.GONE);
			view.findViewById(R.id.dialogProgressBar).setVisibility(View.GONE);
			viewGroup.removeAllViews();
			viewGroup.addView(contentView);
		}

		// 设定取消按钮事件和文本
		if (canceButtonText != null) {
			Button dialogCancelBtn = ((Button) view
					.findViewById(R.id.dialogCancelBtn));
			dialogCancelBtn.setText(canceButtonText);

			if (canceButtonClickListener != null) {
				dialogCancelBtn.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						dismiss();
						canceButtonClickListener.onClick(getDialog(),
								DialogInterface.BUTTON_POSITIVE);
					}
				});
			}
		} else {
			view.findViewById(R.id.dialogCancelBtn).setVisibility(View.GONE);
		}

		// 设置 neutral按钮事件和文本
		if (neutralButtonText != null) {
			Button dialogNeutralBtn = ((Button) view
					.findViewById(R.id.dialogNeutralBtn));
			dialogNeutralBtn.setText(neutralButtonText);

			if (neutralButtonClickListener != null) {
				dialogNeutralBtn.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						neutralButtonClickListener.onClick(getDialog(),
								DialogInterface.BUTTON_NEUTRAL);
					}
				});
			}
		} else {
			view.findViewById(R.id.dialogNeutralBtn).setVisibility(View.GONE);
			view.findViewById(R.id.iv_divider_line_right).setVisibility(
					View.GONE);
		}

		// 设置确定按钮事件和文本
		if (confirmButtonText != null) {
			Button dialogConfirmBtn = ((Button) view
					.findViewById(R.id.dialogConfirmBtn));
			dialogConfirmBtn.setText(confirmButtonText);

			if (confirmButtonClickListener != null) {
				dialogConfirmBtn.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						confirmButtonClickListener.onClick(getDialog(),
								DialogInterface.BUTTON_NEGATIVE);
					}
				});
			}
		} else {
			view.findViewById(R.id.dialogConfirmBtn).setVisibility(View.GONE);
			view.findViewById(R.id.iv_divider_line_left).setVisibility(
					View.GONE);
		}
		if (canceButtonText == null && neutralButtonText == null
				&& confirmButtonText == null) {
			view.findViewById(R.id.dialog_bottom).setVisibility(View.GONE);
		}

		/*
		 * View content = customContent(inflater); if (content != null) {
		 * contentView.addView(content); }
		 * 
		 * TextView titleView = (TextView) view.findViewById(R.id.dialogTitle);
		 * titleView.setText(customTitle());
		 */

		closeBtn = view.findViewById(R.id.closeBtn);
		closeBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dismiss();
			}
		});
		closeBtn.setVisibility(View.GONE);

		return view;
	}

	public void setWindowSize(double widthPercent, double heightPercent) {
		this.widthPercent = widthPercent;
		this.heightPercent = heightPercent;
	}

	/*
	 * protected abstract View customContent(LayoutInflater inflater);
	 * 
	 * protected abstract String customTitle();
	 */

}
