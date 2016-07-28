package com.sation.knxcontroller.activity;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.sation.knxcontroller.STKNXControllerApp;
import com.sation.knxcontroller.STKNXControllerConstant;
import com.sation.knxcontroller.util.Log;
import com.sation.knxcontroller.util.PreferenceHelper;
import com.sation.knxcontroller.R;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

public class BaseActivity extends FragmentActivity {

	public static final int FLAG_HOMEKEY_DISPATCHED = 0x80000000; // 需要自己定义标志

	private static final int TOAST_DURATION = Toast.LENGTH_SHORT;
	private static final int LODING_PROGRESS_ID = 2;
	// APP当前样式
	public int currentTheme = R.style.DefaultTheme;

	protected boolean isLandscape = false;
	protected int lastConfigurationOrientation = -1;

	WakeLock wakeLock = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// 读取主题 如果读取失败，则设置为系统默认的主题
		if (savedInstanceState == null) {
			currentTheme = PreferenceHelper.getTheme(this);
		} else {
			currentTheme = savedInstanceState.getInt("theme");
		}
		// 设定主题
		setTheme(currentTheme);
		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                             WindowManager.LayoutParams.FLAG_FULLSCREEN);
//		this.getWindow().setFlags(FLAG_HOMEKEY_DISPATCHED,
//				FLAG_HOMEKEY_DISPATCHED);// 关键代码

		STKNXControllerApp.getInstance().pushActivity(this);

		 
		// 获取屏幕尺寸及
		DisplayMetrics dm = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(dm);

		Display display = ((WindowManager) getSystemService(WINDOW_SERVICE))
				.getDefaultDisplay();
		if (display != null
				&& (display.getRotation() == Surface.ROTATION_0 || display
						.getRotation() == Surface.ROTATION_180)) {
			isLandscape = true;
			lastConfigurationOrientation = Configuration.ORIENTATION_LANDSCAPE;
		} else {
			isLandscape = false;
			lastConfigurationOrientation = Configuration.ORIENTATION_PORTRAIT;
		}
		// initOrientationListener();
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	}

	private void initOrientationListener() {
		OrientationEventListener orientationListener = new OrientationEventListener(
				this) {
			@Override
			public void onOrientationChanged(int orientation) {
				if (orientation > 315 || orientation < 45
						|| (orientation > 135 && orientation < 225)) {
					// portrait
					if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
						setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
					}
				} else if ((orientation > 225 && orientation < 315)
						|| (orientation > 45 && orientation < 135)) {
					// landscape
					if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
						setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
					}
				}
			}
		};
		orientationListener.enable();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);
		if (getIntent().hasExtra(STKNXControllerConstant.MENU_DRAWER_TRIGGER_KEY)) {

		} else {
			overridePendingTransition(R.anim.push_right_in,
					R.anim.push_right_out);
		}
	}

	public void startActivityForResult(Intent intent, int requestCode) {
		super.startActivityForResult(intent, requestCode);
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) { 
		super.onTouchEvent(event);
		Log.i(STKNXControllerConstant.DEBUG, "getAction:"+event.getAction());
		
		return true;
	}

	@Override
	public void onBackPressed() {

		if (this.getClass().getSimpleName()
				.equalsIgnoreCase("PushMessagesActivity")
				|| this.getClass().getSimpleName()
						.equalsIgnoreCase("MyProfileActivity")) {
			this.finish();
//			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		int key = event.KEYCODE_HOME;
		if (keyCode == event.KEYCODE_HOME) {
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public final void onLowMemory() {
		super.onLowMemory();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (currentTheme != PreferenceHelper.getTheme(this)) {
			reload();
		}
		
		//获取锁，保持屏幕亮度
		//acquireWakeLock();
		//startTimer();
	}
	
	@Override
	protected void onPause() { 
		  super.onPause();
		  //releaseWakeLock();
    }

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("theme", currentTheme);
	}

	protected void reload() {
		Intent intent = getIntent();
		overridePendingTransition(0, 0);
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		finish();
		overridePendingTransition(0, 0);
		startActivity(intent);
	}

	protected Response.ErrorListener createMyReqErrorListener() {
		return new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {

			}
		};
	}

	/**
	 * 遍历布局，并禁用所有子控件
	 * 
	 * @param viewGroup
	 *            布局对象
	 */
	public void disableSubControls(ViewGroup viewGroup) {
		for (int i = 0; i < viewGroup.getChildCount(); i++) {
			View v = viewGroup.getChildAt(i);
			if (v instanceof ViewGroup) {
				if (v instanceof Spinner) {
					Spinner spinner = (Spinner) v;
					spinner.setClickable(false);
					spinner.setEnabled(false);

					Log.i("A Spinner is unabled");
				} else if (v instanceof ListView) {
					((ListView) v).setClickable(false);
					((ListView) v).setEnabled(false);

					Log.i("A ListView is unabled");
				} else {
					disableSubControls((ViewGroup) v);
				}
			} else if (v instanceof EditText) {
				((EditText) v).setEnabled(false);
				((EditText) v).setClickable(false);

				Log.i("A EditText is unabled");
			} else if (v instanceof Button) {
				((Button) v).setEnabled(false);

				Log.i("A Button is unabled");
			}
		}
	}

	/**
	 * @param resId
	 *            resource id
	 */
	public void displayToast(int resId) {
		Toast.makeText(this, resId, TOAST_DURATION).show();
	}

	/**
	 * @param text
	 *            desplay text
	 */
	public void displayToast(CharSequence text) {
		Toast.makeText(this, text, TOAST_DURATION).show();
	}

	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {
		return null;
	}

	/**
	 * 监听是否点击了home键将客户端推到后台
	 */
	private BroadcastReceiver mHomeKeyEventReceiver = new BroadcastReceiver() {
		String SYSTEM_HOME_KEY = "homekey";

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
				Toast.makeText(getApplicationContext(), "home", 1).show();
			}
		}
	};

	// 获取电源锁，保持该服务在屏幕熄灭时仍然获取CPU时，保持运行
	private void acquireWakeLock() {
		if (wakeLock != null) {
			PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
			wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK| PowerManager.ON_AFTER_RELEASE, "PostLocationService");
			if (wakeLock != null) {
				wakeLock.acquire();
			}
		}
	}

	// 释放设备电源锁
	private void releaseWakeLock() {
		if (wakeLock != null) {
			wakeLock.release();
			wakeLock = null;
		}
	} 
}
