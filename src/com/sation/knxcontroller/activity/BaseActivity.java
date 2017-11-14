package com.sation.knxcontroller.activity;

import com.sation.knxcontroller.R;
import com.sation.knxcontroller.STKNXControllerConstant;
import com.sation.knxcontroller.services.RestartService;
import com.sation.knxcontroller.util.FileUtils;
import com.sation.knxcontroller.util.Log;
import com.sation.knxcontroller.util.PreferenceHelper;
import com.sation.knxcontroller.util.SystemUtil;
import com.sation.knxcontroller.util.uikit.ApkUtils;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.WindowManager;
import android.widget.Toast;

import static com.sation.knxcontroller.util.FileUtils.CopyStatus.COPY_SUCCESSFUL;
import static com.sation.knxcontroller.util.FileUtils.CopyStatus.SAME_FILE;
import static com.sation.knxcontroller.util.FileUtils.CopyStatus.SAME_PATH;

public class BaseActivity extends FragmentActivity {
	private final String TAG = "BaseActivity";
	public int currentTheme = R.style.DefaultTheme;
	protected boolean isLandscape = false;
	protected int lastConfigurationOrientation = -1;

	private final static int APKFILE = 1001;
	private final static int PROJECTFILE = 1002;

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

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		System.gc();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);

		return true;
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();

		Log.w(TAG, "onLowMemory()");
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (currentTheme != PreferenceHelper.getTheme(this)) {
			reload();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
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

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	protected void restartThisApplication() {
		startService(new Intent(this, RestartService.class));
		finish();
	}

	/**
	 * 浏览APK文件
	 */
	protected void upgradeSoftware() {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_GET_CONTENT);
		intent.setType("*/*");
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		startActivityForResult(intent, APKFILE);
	}

	/**
	 * 浏览工程文件
	 */
	protected void upgradeProject() {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_GET_CONTENT);
		intent.setType("*/*");
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		startActivityForResult(intent, PROJECTFILE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (RESULT_OK != resultCode) {
		} else if (APKFILE == requestCode) { // 选中的是APK文件
			String mFilePath = Uri.decode(data.getDataString());
			mFilePath = mFilePath.substring(7, mFilePath.length());

			try
			{
				if (mFilePath.endsWith("apk")) {
					String pn = ApkUtils.getPackageNameWithFileName(this, mFilePath);
					if ((null != pn) && pn.equals(SystemUtil.getPackageName(this))) {
						ApkUtils.install(this, mFilePath);
					} else {
						Toast.makeText(this, getResources().getString(R.string.invalid_apk), Toast.LENGTH_LONG).show();
					}
				} else {
					Toast.makeText(this, getResources().getString(R.string.not_a_apk), Toast.LENGTH_LONG).show();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else if (PROJECTFILE == requestCode) { // 选中的是工程文件
			String mFilePath = Uri.decode(data.getDataString());
			mFilePath = mFilePath.substring(7, mFilePath.length());

			try {
				if (mFilePath.endsWith(STKNXControllerConstant.SuffixConfigFile)) {
					FileUtils.CopyStatus status = FileUtils.copyFile(mFilePath, STKNXControllerConstant.ConfigFilePath, true);
					if ((COPY_SUCCESSFUL == status) || (SAME_PATH == status)) {
						Log.i(TAG, "restart this Application");
						restartThisApplication();
					} else if (SAME_FILE == status) {
						Log.e(TAG, getResources().getString(R.string.same_project_file));
						Toast.makeText(this, getResources().getString(R.string.same_project_file), Toast.LENGTH_LONG).show();
					} else {
						Log.e(TAG, getResources().getString(R.string.copy_project_file_failed));
						Toast.makeText(this, getResources().getString(R.string.copy_project_file_failed), Toast.LENGTH_LONG).show();
					}
				} else {
					Log.e(TAG, getResources().getString(R.string.project_file_invalid));
					Toast.makeText(this, getResources().getString(R.string.project_file_invalid), Toast.LENGTH_LONG).show();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
}
