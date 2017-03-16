package com.sation.knxcontroller.activity;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import com.sation.knxcontroller.R;
import com.sation.knxcontroller.STKNXControllerApp;
import com.sation.knxcontroller.STKNXControllerConstant;
import com.sation.knxcontroller.adapter.RoomListAdapter;
import com.sation.knxcontroller.adapter.RoomListAdapter.OnItemActionListener;
import com.sation.knxcontroller.models.KNXApp;
import com.sation.knxcontroller.models.KNXArea;
import com.sation.knxcontroller.models.KNXRoom;
import com.sation.knxcontroller.services.RestartService;
import com.sation.knxcontroller.util.ImageUtils;
import com.sation.knxcontroller.util.KNX0X01Lib;
import com.sation.knxcontroller.util.Log;
import com.sation.knxcontroller.util.StringUtil;
import com.sation.knxcontroller.widget.PromptDialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import static android.hardware.SensorManager.SENSOR_DELAY_NORMAL;

public class RoomTilesListActivity extends BaseActivity implements Runnable {
	private final String TAG = "RoomTilesListActivity";
	private static final int UPDATE_SYSTEM_TIME = 1;
	private static final int UPDATE_TEMPERATURE = 2;
	private static final int UPDATE_LIGHT = 3;

	private RelativeLayout rlayout;
	private GridView gridView;
	private RoomListAdapter mRoomListAdapter;
	private ImageView imgSettting;
	private ImageView imageMenuSetting;
	private TextView txtSystemTime;
	private SharedPreferences settings;
	private List<KNXRoom> mRoomList;
	private ImageView imgRefresh;
	private Handler mHandler;
	private SensorManager mSensorManager;
	private Sensor tSensor;
	private Sensor lSensor;
	private TemperatureListener mTemperatureSensor;
	private LightLisenter mLightListener;
	private TextView txtTemperature;
	private TextView txtLight;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.room_tiles_list_layout);

		Log.i(TAG, "");
		
		this.rlayout = (RelativeLayout) findViewById(R.id.roomTilesListLayoutRelativeLayout);
		this.gridView = (GridView) findViewById(R.id.gridView);
		this.txtSystemTime = (TextView)findViewById(R.id.roomTilesListTextViewDateTime);
		this.imgSettting = (ImageView) findViewById(R.id.imgSettting);
		this.imageMenuSetting = (ImageView)findViewById(R.id.imgMenuSettting);
		this.settings = getSharedPreferences(STKNXControllerConstant.SETTING_FILE, android.content.Context.MODE_PRIVATE);
		this.mRoomList = new ArrayList<KNXRoom>();
		this.mRoomListAdapter = new RoomListAdapter(this);
		this.imgRefresh = (ImageView)findViewById(R.id.imgRefresh);
		this.txtTemperature = (TextView)findViewById(R.id.roomTilesListTextViewTemperature);
		this.txtLight = (TextView)findViewById(R.id.roomTilesListTextViewLight);

		this.mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);

				if(UPDATE_SYSTEM_TIME == msg.what) {
					String time = (String)msg.obj;
					if((null != time) && (null != txtSystemTime)) {
						txtSystemTime.setText(time);
					}
				} else if (UPDATE_TEMPERATURE == msg.what) {
					String temp = (String)msg.obj;
					if((null != temp) && (null != txtTemperature)) {
						txtTemperature.setText(temp);
					}
				} else if (UPDATE_LIGHT == msg.what) {
					String light = (String)msg.obj;
					if ((null != light) && (null != txtLight)) {
						txtLight.setText(light);
					}
				}
			}
		};

		initTiles();

		new Thread(this).start();

		this.mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
		List<Sensor> deviceSensors = this.mSensorManager.getSensorList(Sensor.TYPE_ALL);
		for (Sensor sensor : deviceSensors) {
			Log.i(TAG, "------------------------");
			Log.i(TAG, sensor.getName());
			Log.i(TAG, sensor.getVendor());
			Log.i(TAG, Integer.toString(sensor.getType()));
			Log.i(TAG, "------------------------");
		}
		this.tSensor = this.mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
		this.mTemperatureSensor = new TemperatureListener();

		this.lSensor = this.mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
		this.mLightListener = new LightLisenter();
	}
	
	@Override
	protected void onRestart(){
		super.onRestart();
		
		Log.i(TAG, "");
		
	    if(STKNXControllerApp.getInstance().getRememberLastInterface()) {
	    	final int roomIndex = -1;
	    	final int pageIndex = 0;
	    	STKNXControllerApp.getInstance().setLastRoomIndex(roomIndex);
	    	STKNXControllerApp.getInstance().setLastPageIndex(pageIndex);

			SharedPreferences.Editor editor = settings.edit();
			editor.putInt(STKNXControllerConstant.LAST_ROOM_INDEX, roomIndex);
			editor.putInt(STKNXControllerConstant.LAST_PAGE_INDEX, pageIndex);
			editor.apply();
		}
	}
	
	@Override
	public void onStart(){
		super.onStart();
		
		Log.i(TAG, "");
		
//		IntentFilter filter = new IntentFilter(STKNXControllerConstant.BROADCASTRECEIVER_REFRESH_SYSTEM_TIME);
//	    registerReceiver(refreshSystemTimeReceiver, filter);
	}

	@Override
	public void onResume() {
		super.onResume();
		
		Log.i(TAG, "");
		
		if(STKNXControllerApp.getInstance().getDisplayTimeFlag()) {
			txtSystemTime.setVisibility(View.VISIBLE);
		} else {
			txtSystemTime.setVisibility(View.INVISIBLE);
		}
		
		if(STKNXControllerApp.getInstance().getRememberLastInterface()) {
			int defaultRoomIndex = STKNXControllerApp.getInstance().getLastRoomIndex();
			if(defaultRoomIndex >= 0 && mRoomList.size() > defaultRoomIndex) {
				KNXRoom defaultRoom = mRoomList.get(defaultRoomIndex);
				if(null != defaultRoom) {
					Intent intent = new Intent(RoomTilesListActivity.this, RoomDetailsActivity.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable(STKNXControllerConstant.REMOTE_PARAM_KEY, defaultRoom);
					intent.putExtras(bundle);
					startActivity(intent);
				}
			}
		}

//		this.mSensorManager.registerListener(this.mTemperatureSensor,
//				this.tSensor,
//				SENSOR_DELAY_NORMAL);
//		this.mSensorManager.registerListener(this.mLightListener,
//				this.lSensor,
//				SENSOR_DELAY_NORMAL);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		
		Log.i(TAG, "");

//		this.mSensorManager.unregisterListener(this.mTemperatureSensor, this.tSensor);
//		this.mSensorManager.unregisterListener(this.mLightListener, this.lSensor);
	} 
	
	@Override
	public void onStop() {
		super.onStop();
		
		Log.i(TAG, "");
		
//		unregisterReceiver(refreshSystemTimeReceiver);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		Log.i(TAG, "");
		
		this.txtSystemTime = null;
		this.imgSettting = null;
		this.imageMenuSetting = null;
		this.mRoomListAdapter = null;
		this.gridView = null;
		this.rlayout = null;
		this.mRoomList = null;
		this.imgRefresh = null;
		
		setContentView(R.layout.view_null);
	}

	public void displaySystemTime() {
        txtSystemTime.setVisibility(View.VISIBLE);
	}
	
	public void undisplaySystemTime() {
        txtSystemTime.setVisibility(View.INVISIBLE);
	}

	@SuppressWarnings("deprecation")
	private void initTiles() {   
		KNXApp mControlEditor = STKNXControllerApp.getInstance().getAppConfig();
		if(null == mControlEditor) {
//			restartThisApp();
			return;
		}

		if(!StringUtil.isEmpty(mControlEditor.getBackgroundImage())) {
			Bitmap backImg = ImageUtils.getDiskBitmap(STKNXControllerConstant.ConfigResImgPath + mControlEditor.getBackgroundImage());
			if(null != backImg) {
				this.rlayout.setBackground(new BitmapDrawable(backImg));
			}
		}
			
		this.mRoomList.clear();
		for (int i = 0; i < mControlEditor.getAreas().size(); i++) {
			KNXArea mArea = mControlEditor.getAreas().get(i);
			for (int j = 0; j < mArea.getRooms().size(); j++) {
				KNXRoom mRoom = mArea.getRooms().get(j);
				this.mRoomList.add(mRoom);
			}
		}
 
		this.mRoomListAdapter.setRoomList(this.mRoomList);
		this.gridView.setAdapter(mRoomListAdapter); 
		this.mRoomListAdapter.setOnItemActionListener(new OnItemActionListener() {
			
			@SuppressLint("InflateParams")
			public void onItemClick(final KNXRoom mRoom) { 
				//进入房间具体设置
				if(StringUtil.isEmpty(mRoom.getPinCode())) {
					jumpToRoomDetailsActivity(mRoom);
				} else {
					
					final View view = LayoutInflater.from(RoomTilesListActivity.this).inflate(R.layout.password_layout, null);  
					new PromptDialog.Builder(RoomTilesListActivity.this)
					.setTitle(getResources().getString(R.string.access_restriction)) 
					.setIcon(R.drawable.launcher)
					.setViewStyle(PromptDialog.VIEW_STYLE_NORMAL)
					.setView(view)
					.setButton1(getResources().getString(R.string.cancel),  new PromptDialog.OnClickListener() {
								
						@Override
						public void onClick(Dialog dialog, int which) {
							dialog.dismiss(); 
						}
					})
					.setButton2(getResources().getString(R.string.go), new PromptDialog.OnClickListener() {
								
						@Override
						public void onClick(Dialog dialog, int which) {
						EditText txtPassword = (EditText) view.findViewById(R.id.txtPassword);
						dialog.dismiss();
						if(txtPassword.getText().toString().trim().equalsIgnoreCase(mRoom.getPinCode())) {
							jumpToRoomDetailsActivity(mRoom);
						} else {
							Toast.makeText(RoomTilesListActivity.this, getResources().getString(R.string.password_error), 
										Toast.LENGTH_SHORT).show();
						}
					}
				})
				.show(); 
				}
			}
		});
		
//		this.imgSettting.setImageResource(R.drawable.menu_setting_icon);
		this.imgSettting.setOnClickListener(new OnClickListener() {

			@SuppressLint("InflateParams")
			@Override
			public void onClick(View v) {
				 final View view = LayoutInflater.from(RoomTilesListActivity.this).inflate(R.layout.password_layout, null);
				 final EditText txtPassword = (EditText) view.findViewById(R.id.txtPassword);
				 txtPassword.setImeOptions(EditorInfo.IME_ACTION_GO);
				 
				 final String pw = settings.getString(STKNXControllerConstant.SYSTEM_SETTING_PASSWORD, 
							STKNXControllerConstant.SYSTEM_SETTING_PASSWORD_VALUE).trim();
				 if(pw.isEmpty()) {
//					 new SettingDialog(RoomTilesListActivity.this).Show();
					 jumpToSettingsActivity();
				 } else {
					 final PromptDialog mPrompDialog = new PromptDialog.Builder(RoomTilesListActivity.this)
							 .setTitle(getResources().getString(R.string.access_restriction)) 
							 .setIcon(R.drawable.launcher)
							 .setViewStyle(PromptDialog.VIEW_STYLE_NORMAL)
							 .setView(view)
							 .setButton1(getResources().getString(R.string.cancel),  new PromptDialog.OnClickListener() {
											
								 @Override
								 public void onClick(Dialog dialog, int which) {
									 dialog.dismiss(); 
								 }
							 })
							 .setButton2(getResources().getString(R.string.go), new PromptDialog.OnClickListener() {
											
								 @Override
								 public void onClick(Dialog dialog, int which) {
												
									 if(txtPassword.getText().toString().trim().equals(pw)) {  
										 dialog.dismiss();
//										 new SettingDialog(RoomTilesListActivity.this).Show();
										 jumpToSettingsActivity();
									 } else {
										 Toast.makeText(RoomTilesListActivity.this, getResources().getString(R.string.password_error), 
												 Toast.LENGTH_SHORT).show();
									 }
								 }
							 })
							 .show();
					 
					 txtPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {  

							@Override
							public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
								/*判断是否是“GO”键*/  
								if(actionId == EditorInfo.IME_ACTION_GO) {  
					                /*隐藏软键盘*/  
					            	InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(  
					                                    Context.INPUT_METHOD_SERVICE);  

					               	if(txtPassword.getText().toString().trim().equals(pw)) {
					               		if (imm.isActive()) {  
						                 	imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);  
						               	}
					               		if(null != mPrompDialog) {
					               			mPrompDialog.dismiss();
					               		}
					               		
//					               		new SettingDialog(RoomTilesListActivity.this).Show();
										jumpToSettingsActivity();
					               	} else {
					               		Toast.makeText(RoomTilesListActivity.this, getResources().getString(R.string.password_error),
					               				Toast.LENGTH_SHORT).show();
					               	}

					              	return true;  
								}  
								return false;
							}  
						 });
				 }
			 }
		});
		
		this.imageMenuSetting.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent mIntent = new Intent();
				ComponentName comp = new ComponentName("com.android.settings", 
				"com.android.settings.LanguageSettings");
				mIntent.setComponent(comp);
				mIntent.setAction("android.intent.action.VIEW");
				startActivity(mIntent);
			}
		});
	
		this.imgRefresh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//重启启动
				restartThisApplication();
			}
			
		});
	}

	@Override
	public void onBackPressed() {
		if(!android.os.Build.MODEL.equals(STKNXControllerConstant.STKCPLATFORM)) { // 若不是世讯十寸机，则退出程序
			STKNXControllerApp.getInstance().onDestroy();
			KNX0X01Lib.UCLOSENet();

			super.onBackPressed();
		}
	}

	private void jumpToRoomDetailsActivity(final KNXRoom mRoom) {
		Intent intent = new Intent(RoomTilesListActivity.this, RoomDetailsActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable(STKNXControllerConstant.REMOTE_PARAM_KEY, mRoom);
		intent.putExtras(bundle);
		startActivity(intent);

		overridePendingTransition(R.anim.scale_from_center,
				R.anim.scale_to_center);
	}

	private void jumpToSettingsActivity() {
		Intent intent = new Intent(RoomTilesListActivity.this, SettingsActivity.class);
		startActivity(intent);

		overridePendingTransition(R.anim.scale_leftbottom_in, R.anim.scale_righttop_out);
	}

	@Override
	public void run() {
		do {
			try {
				/* 刷新主界面时间 */
				if(STKNXControllerApp.getInstance().getDisplayTimeFlag()) {
					final Calendar c = Calendar.getInstance();
					int year = c.get(Calendar.YEAR);
					int month = c.get(Calendar.MONTH)+1;
					int day = c.get(Calendar.DAY_OF_MONTH);
					int hour = c.get(Calendar.HOUR_OF_DAY); // 24小时制的时间
					int minute = c.get(Calendar.MINUTE);
					int second = c.get(Calendar.SECOND);
					int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
					String weekday = "";

					if (1 == dayOfWeek) { // 周日
						weekday = this.getResources().getString(R.string.sunday);
					} else if (2 == dayOfWeek) { // 周一
						weekday = this.getResources().getString(R.string.monday);
					} else if (3 == dayOfWeek) { // 周二
						weekday = this.getResources().getString(R.string.tuesday);
					} else if (4 == dayOfWeek) { // 周三
						weekday = this.getResources().getString(R.string.wednesday);
					} else if (5 == dayOfWeek) { // 周四
						weekday = this.getResources().getString(R.string.thursday);
					} else if (6 == dayOfWeek) { // 周五
						weekday = this.getResources().getString(R.string.friday);
					} else if (7 == dayOfWeek) { // 周六
						weekday = this.getResources().getString(R.string.saturday);
					}

					String languange = STKNXControllerApp.getInstance().getLanguage();
					String time;
					if ((null != languange) && languange.equals(Locale.SIMPLIFIED_CHINESE.toString())) {
						time = String.format(Locale.getDefault(), "%04d/%02d/%02d  %02d:%02d:%02d  %s",
								year, month, day, hour, minute, second, weekday);
					} else {
						time = String.format(Locale.getDefault(), "%02d/%02d/%04d  %02d:%02d:%02d  %s",
								month, day, year, hour, minute, second, weekday);
					}

					Message msg = new Message();
					msg.what = UPDATE_SYSTEM_TIME;
					msg.obj = time;
					this.mHandler.sendMessage(msg);
				}

				Thread.sleep(1000);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} while (true);
	}

	private class TemperatureListener implements SensorEventListener {
		@Override
		public void onSensorChanged(SensorEvent event) {
			float tempArray = event.values[0];
			BigDecimal tempBD = new BigDecimal(tempArray);
			double tempDouble = tempBD.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			Log.i(TAG, "temperature changed==>" + tempDouble);
			Message msg = new Message();
			msg.what = UPDATE_TEMPERATURE;
			msg.obj = String.valueOf(tempDouble) + " ℃";
			mHandler.sendMessage(msg);
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			Log.i(TAG, "onAccuracyChanged");
		}
	}

	private class LightLisenter implements SensorEventListener {
		@Override
		public void onSensorChanged(SensorEvent event) {
			float lightArray = event.values[0];
			BigDecimal lightBD = new BigDecimal(lightArray);
			double lightDouble = lightBD.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			Log.i(TAG, "light changed ==> " + lightDouble);
			Message msg = new Message();
			msg.what = UPDATE_LIGHT;
			msg.obj = String.valueOf(lightDouble) + " lux";
			mHandler.sendMessage(msg);
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {}
	}
}
