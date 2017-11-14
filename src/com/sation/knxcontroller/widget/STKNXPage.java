package com.sation.knxcontroller.widget;

import com.sation.knxcontroller.STKNXControllerApp;
import com.sation.knxcontroller.STKNXControllerConstant;
import com.sation.knxcontroller.activity.RoomDetailsActivity;
import com.sation.knxcontroller.activity.TimingTaskActivity;
import com.sation.knxcontroller.control.KNXControlBase;
import com.sation.knxcontroller.control.KNXGroupBox;
import com.sation.knxcontroller.control.KNXTimerButton;
import com.sation.knxcontroller.models.KNXContainer;
import com.sation.knxcontroller.models.KNXGroupAddress;
import com.sation.knxcontroller.models.KNXPage;
import com.sation.knxcontroller.util.ImageUtils;
import com.sation.knxcontroller.util.Log;
import com.sation.knxcontroller.util.StringUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.view.Gravity;
import android.view.View;

public class STKNXPage extends STKNXViewContainer {
	private final String TAG = "STKNXPage";

	private Context mContext;
	private SharedPreferences settings;
	private KNXPage mKNXPage;

    private boolean isControlCreated = false;

	public STKNXPage(Context context, KNXPage page) {
		super(context, page);

		this.mContext = context;
		this.settings = context.getSharedPreferences(
				STKNXControllerConstant.SETTING_FILE, android.content.Context.MODE_PRIVATE);

		this.mKNXPage = page;
		this.setId(this.mKNXPage.getId());

		if (null != this.mKNXPage.getBackgroundImage()) {
			Drawable back = Drawable.createFromPath(
					STKNXControllerConstant.ConfigResImgPath + this.mKNXPage.getBackgroundImage());
			if (null != back) {
				this.setBackground(back);
			} else {
				this.setBackgroundColor(Color.parseColor(this.mKNXPage.BackgroundColor));
			}
		} else {
			this.setBackgroundColor(Color.parseColor(this.mKNXPage.BackgroundColor));
		}

		//增加页面级别的控件
//		createSTKNXControlAndDisplay(this, mKNXPage);
	}

	@Override
	public void onSuspend() { // 页面挂起
		int count = getChildCount();
		for(int i=0; i<count; i++) {
			View v = getChildAt(i);
			if(v instanceof STKNXView) {
				STKNXView sv = (STKNXView)v;
				sv.onSuspend();
			}
		}
	}

	@Override
	public void onResume() { // 页面恢复
		int count = getChildCount();
		for(int i=0; i<count; i++) {
			View v = getChildAt(i);
			if(v instanceof STKNXView) {
				STKNXView sv = (STKNXView)v;
				sv.onResume();
			}
		}

		if(STKNXControllerApp.getInstance().getRememberLastInterface()) { // 是否记住最后界面
			String defaultTimerId = STKNXControllerApp.getInstance().getLastTimerId();
			if(!StringUtil.isNullOrEmpty(defaultTimerId)) { // 最后一个界面是否为定时器界面
				boolean bContains = isContainsTimer(this.mKNXPage, defaultTimerId);
				if (bContains) {
					final String timerId = "";
					STKNXControllerApp.getInstance().setLastTimerId(timerId);

					SharedPreferences.Editor editor = settings.edit();
					editor.putString(STKNXControllerConstant.LAST_TIMER_ID, timerId);
					editor.apply();

					// 跳转到 ID 为 defaultTimerId 的定时器界面
					Intent intent = new Intent(this.mContext, TimingTaskActivity.class);
					intent.putExtra(STKNXControllerConstant.CONTROL_ID, defaultTimerId);
					this.mContext.startActivity(intent);
				}
			}
		}

		if (!isControlCreated) { // 根据需要显示
            createSTKNXControlAndDisplay(this, this.mKNXPage);

            isControlCreated = true;
        }
	}

	@Override
	public void onDestroy() { // 页面销毁
		super.onDestroy();

		Log.i(TAG, "");
		int count = getChildCount();
		for(int i=0; i<count; i++) {
			View v = (View)getChildAt(i);
			if(v instanceof STKNXView) {
				STKNXView sv = (STKNXView)v;
				sv.onDestroy();
			}
		}
	}

	/* 页面内控件状态更新 */
	public void addressStatusUpdate(int asp, KNXGroupAddress address) { // 对象状态更新
		int count = getChildCount();
		for(int i=0; i<count; i++) {
			View v = getChildAt(i);
			if(v instanceof STKNXControl) {
				STKNXControl sv = (STKNXControl)v;
				sv.statusUpdate(asp, address);
			}
		}
	}

	private void createSTKNXControlAndDisplay(STKNXView parentView, KNXContainer knxContainer) {
		for (int x = 0; x < knxContainer.getControls().size(); x++) { // 遍历 Page 或 GroupBox下的控件
			KNXControlBase mKNXControlBase = knxContainer.getControls().get(x);
			STKNXView view = KNXControlBase.buildWithControl(this.mContext, mKNXControlBase); // 创建控件
			if (view != null) {
				LayoutParams pageLayoutParams = new LayoutParams(view.getWidth(), view.getHeight());
				pageLayoutParams.leftMargin = view.getLeft();
				pageLayoutParams.topMargin = view.getTop();
				view.setLayoutParams(pageLayoutParams); // 控件布局
				parentView.addView(view);

				if(mKNXControlBase instanceof KNXGroupBox) { // 遍历GroupBox下的控件，并创建之
					KNXGroupBox mKNXGroupBox = (KNXGroupBox)mKNXControlBase;
					createSTKNXControlAndDisplay(view, mKNXGroupBox);
				}
			}
		}
	}

	private boolean isContainsTimer(KNXContainer knxContainer, String timerId) {
		boolean retval = false;

		for (int x = 0; x < knxContainer.getControls().size(); x++) { // 遍历 Page 或 GroupBox 下的 定时器 控件
			KNXControlBase mKNXControlBase = knxContainer.getControls().get(x);

			if (mKNXControlBase instanceof KNXTimerButton) {
				String id = String.valueOf(mKNXControlBase.getId());
				if(id.equals(timerId)) { // 通过比对ID，判定是否为要找的定时器
					retval = true;
					break;
				}
			} else if(mKNXControlBase instanceof KNXGroupBox) { // 遍历 GroupBox下的控件
				KNXGroupBox mKNXGroupBox = (KNXGroupBox)mKNXControlBase;
				retval = isContainsTimer(mKNXGroupBox, timerId);
				if(retval) {
					break;
				}
			}
		}

		return retval;
	}
}
