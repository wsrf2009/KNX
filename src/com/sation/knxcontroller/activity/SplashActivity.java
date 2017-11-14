package com.sation.knxcontroller.activity;

import java.util.Locale;

import com.sation.knxcontroller.R;
import com.sation.knxcontroller.STKNXControllerApp;
import com.sation.knxcontroller.STKNXControllerConstant;
import com.sation.knxcontroller.util.CompressStatus;
import com.sation.knxcontroller.util.Log;
import com.sation.knxcontroller.widget.PromptDialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import static android.widget.Toast.LENGTH_LONG;

public class SplashActivity extends BaseActivity {
	private static final String TAG = "SplashActivity";

	private SharedPreferences settings;
	private Button buttonProjectFile;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);

		ImageView splashabout = (ImageView) findViewById(R.id.splashabout);
		splashabout.setImageResource(R.drawable.companylogo);

		this.buttonProjectFile = (Button) findViewById(R.id.buttonProjectFile);
		this.buttonProjectFile.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				upgradeProject();
			}
		});

		settings = getSharedPreferences(STKNXControllerConstant.SETTING_FILE, MODE_PRIVATE);

		Resources resources = getResources();//获得res资源对象
		Configuration config = resources.getConfiguration();//获得设置对象
		DisplayMetrics dm = resources.getDisplayMetrics();//获得屏幕参数：主要是分辨率，像素等。
		String language = settings.getString(STKNXControllerConstant.APP_APPEARANCE_LANGUAGE, "");
		Log.i(TAG, "language:" + language+"locale:"+Locale.CHINA);
		if(language.isEmpty()) {
			/* 若APP尚未配置语言 */
		    String locLanguage = config.locale.getLanguage();
		    SharedPreferences.Editor editor = settings.edit();
		    if(locLanguage.equals(Locale.CHINESE.toString())) {
		    	/* 系统设置为中文 */
		    	config.locale = Locale.SIMPLIFIED_CHINESE; //简体中文  
			    editor.putString(STKNXControllerConstant.APP_APPEARANCE_LANGUAGE, Locale.SIMPLIFIED_CHINESE.toString());
				STKNXControllerApp.getInstance().setLanguage(Locale.SIMPLIFIED_CHINESE.toString());
		    } else {
		    	/* 系统设置为其他语言 */
		    	config.locale = Locale.US; // 美式英语
		    	editor.putString(STKNXControllerConstant.APP_APPEARANCE_LANGUAGE, Locale.US.toString());
				STKNXControllerApp.getInstance().setLanguage(Locale.US.toString());
		    }
		    editor.apply();
		} else if(language.equals(Locale.SIMPLIFIED_CHINESE.toString())) {
		    config.locale = Locale.SIMPLIFIED_CHINESE; // 简体中文
			STKNXControllerApp.getInstance().setLanguage(Locale.SIMPLIFIED_CHINESE.toString());
		} else {
		    config.locale = Locale.US; // 美式英语
			STKNXControllerApp.getInstance().setLanguage(Locale.US.toString());
		}
		resources.updateConfiguration(config, dm);

		STKNXControllerApp.getInstance().getProject(handler);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		System.gc();
	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				/* 没有发现工程文件 */
				case CompressStatus.ERROR_NO_FILE:
					TextView textView = new TextView(SplashActivity.this);
					textView.setText(getResources().getString(R.string.no_project_file));
					new PromptDialog.Builder(SplashActivity.this)
							.setTitle(getResources().getString(R.string.project_file_error))
							.setViewStyle(PromptDialog.VIEW_STYLE_TITLEBAR)
							.setView(textView)
							.setButton1(getResources().getString(R.string.confirm), new PromptDialog.OnClickListener() {

								@Override
								public void onClick(Dialog dialog, int which) {
									dialog.dismiss();

									buttonProjectFile.setVisibility(View.VISIBLE);
									upgradeProject();
								}
							})
							.show();
					break;

				/* 工程文件错误 */
				case CompressStatus.ERROR_UNZIP:
				case CompressStatus.ERROR_PARSE:
				case CompressStatus.ERROR_INVALID_FILE:
					textView = new TextView(SplashActivity.this);
					textView.setText(getResources().getString(R.string.project_file_corrupted));
					new PromptDialog.Builder(SplashActivity.this)
							.setTitle(getResources().getString(R.string.project_file_error))
							.setViewStyle(PromptDialog.VIEW_STYLE_TITLEBAR)
							.setView(textView)
							.setButton1(getResources().getString(R.string.confirm), new PromptDialog.OnClickListener() {

								@Override
								public void onClick(Dialog dialog, int which) {
									dialog.dismiss();

									buttonProjectFile.setVisibility(View.VISIBLE);
									upgradeProject();
								}
							})
							.show();
					break;

				/* 工程文件版本过低 */
				case CompressStatus.ERROR_EDITORVERSION_215:
				case CompressStatus.ERROR_EDITORVERSION_218:
				case CompressStatus.ERROR_EDITORVERSION_252:
				case CompressStatus.ERROR_EDITORVERSION_253:
				case CompressStatus.ERROR_EDITORVERSION_254:
				case CompressStatus.ERROR_EDITORVERSION_256:
				case CompressStatus.ERROR_EDITORVERSION_257:
					new PromptDialog.Builder(SplashActivity.this)
							.setTitle(getResources().getString(R.string.project_file_error))
							.setViewStyle(PromptDialog.VIEW_STYLE_TITLEBAR)
							.setMessage(getResources().getString(R.string.editor_version_error))
							.setButton1(getResources().getString(R.string.confirm), new PromptDialog.OnClickListener() {

								@Override
								public void onClick(Dialog dialog, int which) {
									dialog.dismiss();

									buttonProjectFile.setVisibility(View.VISIBLE);
									upgradeProject();
								}
							})
							.show();
					break;

				/* 工程文件无误 */
				case CompressStatus.PARSE_COMPLETED:
					jumpActivity();
					break;

				default:
					break;
			}
		}
	};

	private void jumpActivity() {
		STKNXControllerApp.getInstance().loadLibrary();

		Intent intent = new Intent(SplashActivity.this, RoomTilesListActivity.class);
		startActivity(intent);
		finish();
	}
}
