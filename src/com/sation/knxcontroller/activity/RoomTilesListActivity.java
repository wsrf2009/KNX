package com.sation.knxcontroller.activity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import com.sation.knxcontroller.R;
import com.sation.knxcontroller.STKNXControllerApp;
import com.sation.knxcontroller.STKNXControllerConstant;
import com.sation.knxcontroller.adapter.AreaListAdapter;
import com.sation.knxcontroller.adapter.RoomListAdapter;
import com.sation.knxcontroller.adapter.RoomListAdapter.OnItemActionListener;
import com.sation.knxcontroller.models.KNXApp;
import com.sation.knxcontroller.models.KNXArea;
import com.sation.knxcontroller.models.KNXRoom;
import com.sation.knxcontroller.util.KNX0X01Lib;
import com.sation.knxcontroller.util.Log;
import com.sation.knxcontroller.util.StringUtil;
import com.sation.knxcontroller.widget.PromptDialog;
import com.sation.knxcontroller.widget.WrappingSlidingDrawer;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
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
	private GridView mAreasGridView;
	private RelativeLayout mRoomsListLayout;
	private GridView mRoomsGridView;
	private AreaListAdapter mAreaListAdapter;
	private ImageView imgSettting;
	private ImageView imageMenuSetting;
	private TextView txtSystemTime;
	private SharedPreferences settings;
	private ImageView imgRefresh;
	private Handler mHandler;
	private SensorManager mSensorManager;
	private Sensor tSensor;
	private Sensor lSensor;
	private TemperatureListener mTemperatureSensor;
	private LightLisenter mLightListener;
	private TextView txtTemperature;
	private TextView txtLight;
	private WrappingSlidingDrawer mWrappingSlidingDrawer;
	private ImageView mHandlImageView;
	private ImageView mAboutWebView;
	private LinearLayout handle;

	private KNXApp mApp;

	private int currentAreaIndex = -1;
	private int currentRoomIndex = -1;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.room_tiles_list_layout);

        this.rlayout = (RelativeLayout) findViewById(R.id.roomTilesListLayoutRelativeLayout);
		this.mAboutWebView = (ImageView)findViewById(R.id.aboutWebView);
		this.gridView = (GridView) findViewById(R.id.gridView);
		this.txtSystemTime = (TextView)findViewById(R.id.roomTilesListTextViewDateTime);
		this.imgSettting = (ImageView) findViewById(R.id.imgSettting);
		this.imageMenuSetting = (ImageView)findViewById(R.id.imgMenuSettting);
		this.settings = getSharedPreferences(STKNXControllerConstant.SETTING_FILE, android.content.Context.MODE_PRIVATE);
		this.mAreaListAdapter = new AreaListAdapter(this);
		this.imgRefresh = (ImageView)findViewById(R.id.imgRefresh);
		this.txtTemperature = (TextView)findViewById(R.id.roomTilesListTextViewTemperature);
		this.txtLight = (TextView)findViewById(R.id.roomTilesListTextViewLight);
		this.mWrappingSlidingDrawer = (WrappingSlidingDrawer)findViewById(R.id.roomTilesListLayoutWrappingSlidingDrawer);
		this.mHandlImageView = (ImageView)findViewById(R.id.roomTilesListLayoutHandleImage);
		this.mAreasGridView = (GridView)findViewById(R.id.roomTilesListLayoutGridView);
		this.handle = (LinearLayout)findViewById(R.id.handle);

		this.rlayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				closeWrappingSlidingDrawer();
			}
		});
		this.gridView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				closeWrappingSlidingDrawer();
				return false;
			}
		});

		final Animation handleDownUp = AnimationUtils.loadAnimation(RoomTilesListActivity.this, R.anim.translate_bounce_down_up);
		final Animation handleUpDown = AnimationUtils.loadAnimation(RoomTilesListActivity.this, R.anim.translate_up_down);
		this.handle.startAnimation(handleDownUp);
		handleDownUp.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				handle.clearAnimation();
				handle.startAnimation(handleUpDown);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}
		}); // 上往下
		handleUpDown.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				handle.clearAnimation();
				handle.startAnimation(handleDownUp);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}
		}); // 下往上

		this.mWrappingSlidingDrawer.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener() {
			@Override
			public void onDrawerOpened() { // 区域栏已显示
				Animation animation = AnimationUtils.loadAnimation(RoomTilesListActivity.this, R.anim.rotate_clockwise_180_degree);
				handle.startAnimation(animation);
				animation.setAnimationListener(new Animation.AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {

					}

					@Override
					public void onAnimationEnd(Animation animation) {
						handle.clearAnimation();
						mHandlImageView.setImageResource(R.drawable.down);
					}

					@Override
					public void onAnimationRepeat(Animation animation) {

					}
				});

			}
		});
		this.mWrappingSlidingDrawer.setOnDrawerCloseListener(new SlidingDrawer.OnDrawerCloseListener() {
			@Override
			public void onDrawerClosed() { // 区域栏已隐藏
				Animation animation = AnimationUtils.loadAnimation(RoomTilesListActivity.this, R.anim.rotate_anti_clockwise_180_degree);
				handle.startAnimation(animation);
				animation.setAnimationListener(new Animation.AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {

					}

					@Override
					public void onAnimationEnd(Animation animation) {
						handle.clearAnimation();
						mHandlImageView.setImageResource(R.drawable.up);
						handle.startAnimation(handleDownUp);
					}

					@Override
					public void onAnimationRepeat(Animation animation) {

					}
				});
			}
		});

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

		new Thread(this).start();

		/* 传感器暂不可用，在系统级已屏蔽 */
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

		this.mApp = STKNXControllerApp.getInstance().getAppConfig();

		initTiles();
	}
	
	@Override
	protected void onRestart(){
		super.onRestart();

		/* 返回到这个界面时，清除已记录的页面信息 */
//	    if(STKNXControllerApp.getInstance().getRememberLastInterface()) {
	    	final int roomIndex = -1;
	    	final int pageIndex = 0;
	    	STKNXControllerApp.getInstance().setLastRoomIndex(roomIndex);
	    	STKNXControllerApp.getInstance().setLastPageIndex(pageIndex);

			SharedPreferences.Editor editor = settings.edit();
			editor.putInt(STKNXControllerConstant.LAST_ROOM_INDEX, roomIndex);
			editor.putInt(STKNXControllerConstant.LAST_PAGE_INDEX, pageIndex);
			editor.apply();
//		}
	}
	
	@Override
	public void onStart(){
		super.onStart();
	}

	@Override
	public void onResume() {
		super.onResume();

		if(STKNXControllerApp.getInstance().getDisplayTimeFlag()) {
			txtSystemTime.setVisibility(View.VISIBLE);
		} else {
			txtSystemTime.setVisibility(View.INVISIBLE);
		}

		if (STKNXControllerApp.getInstance().getRememberLastInterface()) { // 记住最后所在界面？
			int defaultAreaIndex = STKNXControllerApp.getInstance().getmLastAreaIndex();
			int defaultRoomIndex = STKNXControllerApp.getInstance().getLastRoomIndex();
			if ((defaultAreaIndex >= 0) && (mApp.getAreas().size() > defaultAreaIndex)) {
				KNXArea defaultArea = mApp.getAreas().get(defaultAreaIndex);
				if ((null != defaultArea) && StringUtil.isNullOrEmpty(defaultArea.getPinCode())) {
					if ((defaultRoomIndex >= 0) && (defaultArea.getRooms().size() > defaultRoomIndex)) {
						KNXRoom defaultRoom = defaultArea.getRooms().get(defaultRoomIndex);
						if (null != defaultRoom && StringUtil.isNullOrEmpty(defaultRoom.getPinCode())) {
							this.mAreaListAdapter.setDefaultIndex(defaultAreaIndex);
							initialRoomGridView(defaultArea);

							Intent intent = new Intent(RoomTilesListActivity.this, RoomDetailsActivity.class);
							Bundle bundle = new Bundle();
							bundle.putSerializable(STKNXControllerConstant.REMOTE_PARAM_KEY, defaultRoom);
							intent.putExtras(bundle);
							startActivity(intent);
						}
					}
				}
			}
		}

		this.mSensorManager.registerListener(this.mTemperatureSensor,
				this.tSensor,
				SENSOR_DELAY_NORMAL);
		this.mSensorManager.registerListener(this.mLightListener,
				this.lSensor,
				SENSOR_DELAY_NORMAL);
	}
	
	@Override
	public void onPause() {
		super.onPause();

		this.mSensorManager.unregisterListener(this.mTemperatureSensor, this.tSensor);
		this.mSensorManager.unregisterListener(this.mLightListener, this.lSensor);
	} 
	
	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		this.txtSystemTime = null;
		this.imgSettting = null;
		this.imageMenuSetting = null;
		this.gridView = null;
		this.rlayout = null;
		this.imgRefresh = null;
		
		setContentView(R.layout.view_null);
	}

	@Override
	public void onBackPressed() {
//		if(!android.os.Build.MODEL.equals(STKNXControllerConstant.STKCPLATFORM)) { // 若不是世讯十寸机，则退出程序
//			STKNXControllerApp.getInstance().onDestroy();
			KNX0X01Lib.UCLOSENet();

			super.onBackPressed();

		finish();
//		}
	}

	private void closeWrappingSlidingDrawer() {
		if(this.mWrappingSlidingDrawer.isOpened()) {
			this.mWrappingSlidingDrawer.animateClose();
		}
	}

	public void displaySystemTime() {
        txtSystemTime.setVisibility(View.VISIBLE);
	}
	
	public void undisplaySystemTime() {
        txtSystemTime.setVisibility(View.INVISIBLE);
	}

	@SuppressWarnings("deprecation")
	private void initTiles() {
		if(null == this.mApp) {
			return;
		}

		Drawable back = Drawable.createFromPath(STKNXControllerConstant.ConfigResImgPath + this.mApp.getBackgroundImage());
		if(null != back) { // 主页面背景图片
			this.rlayout.setBackground(back);
		}

		List<KNXArea> areas = new ArrayList<KNXArea>();
		for (int i=0; i<this.mApp.getAreas().size(); i++) {
			KNXArea mArea = this.mApp.getAreas().get(i);
			areas.add(mArea);
		}

		int iWidth = getResources().getDimensionPixelOffset(R.dimen.area_item_width); // 单个区域图标宽度
		int iHeight = getResources().getDimensionPixelOffset(R.dimen.area_item_height); // 单个区域图标高度
		int HorSpace = getResources().getDimensionPixelOffset(R.dimen.area_item_horiz_space); // 区域之间水平间隔
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		float density = dm.density;
		int gridViewWidth = (int)(areas.size() * density * (iWidth+ HorSpace)); // 区域显示视图的总宽度
		int itemWidth = (int)(iWidth *density); // 单个区域的高度

		final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				gridViewWidth, ViewGroup.LayoutParams.WRAP_CONTENT
		);
		this.mAreasGridView.setLayoutParams(layoutParams);
		this.mAreasGridView.setColumnWidth(itemWidth+HorSpace);
		this.mAreasGridView.setHorizontalSpacing(HorSpace);
		this.mAreasGridView.setNumColumns(areas.size()); // 单行显示

		this.mAreaListAdapter.setAreaList(areas);
        this.mAreasGridView.setAdapter(this.mAreaListAdapter);
		this.mAreaListAdapter.setOnItemActionListener(new AreaListAdapter.OnItemActionListener() {
            @Override
            public void onItemClick(final KNXArea mArea, int index) {
				currentAreaIndex = index;

				if(StringUtil.isNullOrEmpty(mArea.getPinCode())) { // 区域没有密码？
					initialRoomGridView(mArea);
				} else {

					final View view = LayoutInflater.from(RoomTilesListActivity.this).inflate(R.layout.password_layout, null);
					new PromptDialog.Builder(RoomTilesListActivity.this)
							.setTitle(getResources().getString(R.string.access_restriction))
							.setIcon(R.mipmap.launcher)
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
									if(txtPassword.getText().toString().trim().equalsIgnoreCase(mArea.getPinCode())) {
										initialRoomGridView(mArea);
									} else {
										Toast.makeText(RoomTilesListActivity.this, getResources().getString(R.string.password_error),
												Toast.LENGTH_SHORT).show();
									}
								}
							})
							.show(); // 弹出密码输入窗
				}
            }
        });

		if(areas.size() > 0) {
			this.mAreaListAdapter.setDefaultIndex(0);
			initialRoomGridView(areas.get(0));

			currentAreaIndex = 0;
		}

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
					 jumpToSettingsActivity();
				 } else {
					 final PromptDialog mPrompDialog = new PromptDialog.Builder(RoomTilesListActivity.this)
							 .setTitle(getResources().getString(R.string.access_restriction))
							 .setIcon(R.mipmap.launcher)
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
										 jumpToSettingsActivity();
									 } else {
										 Toast.makeText(RoomTilesListActivity.this, getResources().getString(R.string.password_error),
												 Toast.LENGTH_SHORT).show();
									 }
								 }
							 })
							 .show(); // 弹出密码输入窗

					 txtPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {

							@Override
							public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
								/*判断是否是“GO”键*/
								if(actionId == EditorInfo.IME_ACTION_GO) { // 密码验证
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

	private void jumpToRoomDetailsActivity(final KNXRoom mRoom) {
		Intent intent = new Intent(RoomTilesListActivity.this, RoomDetailsActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable(STKNXControllerConstant.REMOTE_PARAM_KEY, mRoom);
		intent.putExtras(bundle);
		startActivity(intent); // 跳转到RoomDetail，并传入房间参数

		overridePendingTransition(R.anim.scale_from_center_600,
				R.anim.scale_to_center_600);

		/* 记住最后进入的区域和房间索引 */
		if(STKNXControllerApp.getInstance().getRememberLastInterface()) {
			STKNXControllerApp.getInstance().setmLastAreaIndex(currentAreaIndex);
			STKNXControllerApp.getInstance().setLastRoomIndex(currentRoomIndex);

			new Thread(new Runnable() {

				@Override
				public void run() {
					SharedPreferences.Editor editor = settings.edit();
					editor.putInt(STKNXControllerConstant.LAST_AREA_INDEX, currentAreaIndex);
					editor.putInt(STKNXControllerConstant.LAST_ROOM_INDEX, currentRoomIndex);
					editor.commit();
				}

			}).start();
		}
	}

	private void jumpToSettingsActivity() {
		Intent intent = new Intent(RoomTilesListActivity.this, SettingsActivity.class);
		startActivity(intent);

		overridePendingTransition(R.anim.scale_leftbottom_in, R.anim.scale_righttop_out);
	}

	/* 初始化区域下的Room视图 */
	private void initialRoomGridView(KNXArea area) {
		List<KNXRoom> rooms = new ArrayList<KNXRoom>();
		for (int j = 0; j < area.getRooms().size(); j++) {
			KNXRoom mRoom = area.getRooms().get(j);
			rooms.add(mRoom);
		}

		RoomListAdapter roomAdapter = new RoomListAdapter(this);
		roomAdapter.setRoomList(rooms);
		this.gridView.setAdapter(roomAdapter);
		roomAdapter.setOnItemActionListener(new OnItemActionListener() {

			@SuppressLint("InflateParams")
			public void onItemClick(final KNXRoom mRoom, int index) {
				currentRoomIndex = index;
				closeWrappingSlidingDrawer();

				//进入房间具体设置
				if(StringUtil.isNullOrEmpty(mRoom.getPinCode())) {
					jumpToRoomDetailsActivity(mRoom);
				} else { // 密码验证
					final View view = LayoutInflater.from(RoomTilesListActivity.this).inflate(R.layout.password_layout, null);
					new PromptDialog.Builder(RoomTilesListActivity.this)
					.setTitle(getResources().getString(R.string.access_restriction))
					.setIcon(R.mipmap.launcher)
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
	}

	@Override
	public void run() { // 显示系统时间
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
			Message msg = new Message();
			msg.what = UPDATE_LIGHT;
			msg.obj = String.valueOf(lightDouble) + " lux";
			mHandler.sendMessage(msg);
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {}
	}
}
