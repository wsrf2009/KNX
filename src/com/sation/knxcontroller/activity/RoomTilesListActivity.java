package com.sation.knxcontroller.activity;

import java.util.ArrayList;
import java.util.List;

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
import com.sation.knxcontroller.util.SystemUtil;
import com.sation.knxcontroller.widget.PromptDialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
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

public class RoomTilesListActivity extends BaseActivity {
	private final String TAG = "RoomTilesListActivity";
	
	private RelativeLayout rlayout;
	private GridView gridView;
	private RoomListAdapter mRoomListAdapter;
	private ImageView imgSettting;
	private ImageView imageMenuSetting;
	private TextView txtSystemTime;
	private SharedPreferences settings;
	private List<KNXRoom> mRoomList;
	private ImageView imgRefresh;
	
//	private boolean displaySystemTime;
//	private boolean bRecordLastInterface;
	
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

		initTiles();
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
	    	
	    	new Thread(new Runnable() {

				@Override
				public void run() {
					SharedPreferences.Editor editor = settings.edit(); 
					editor.putInt(STKNXControllerConstant.LAST_ROOM_INDEX, roomIndex);
					editor.putInt(STKNXControllerConstant.LAST_PAGE_INDEX, pageIndex);
					editor.commit();
				}
	    		
	    	}).start();
			
		}
	}
	
	@Override
	protected void onStart(){
		super.onStart();
		
		Log.i(TAG, "");
		
		IntentFilter filter = new IntentFilter(STKNXControllerConstant.BROADCASTRECEIVER_REFRESH_SYSTEM_TIME);
	    registerReceiver(refreshSystemTimeReceiver, filter);
	    
//		this.bRecordLastInterface = settings.getBoolean(STKNXControllerConstant.RECORD_LAST_INTERFACE,
//	    		STKNXControllerConstant.RECORD_LAST_INTERFACE_VALUE);
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
	}
	
	@Override
	public void onPause() {
		super.onPause();
		
		Log.i(TAG, "");
	} 
	
	@Override
	protected void onStop() {
		super.onStop();
		
		Log.i(TAG, "");
		
		unregisterReceiver(refreshSystemTimeReceiver);
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
		if(null != mControlEditor) {
			if(!StringUtil.isEmpty(mControlEditor.BackgroundImage)) {
				Bitmap backImg = ImageUtils.getDiskBitmap(STKNXControllerConstant.ConfigResImgPath +mControlEditor.BackgroundImage);
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
		} else { // restart the app
			Log.i(TAG, "restart this app");
//			this.imgRefresh.callOnClick();
			restartThisApp();
		}
 
		this.mRoomListAdapter.setRoomList(this.mRoomList);
		this.gridView.setAdapter(mRoomListAdapter); 
		this.mRoomListAdapter.setOnItemActionListener(new OnItemActionListener() {
			
			@SuppressLint("InflateParams")
			public void onItemClick(final KNXRoom mRoom) { 
				//进入房间具体设置
				if(StringUtil.isEmpty(mRoom.getPinCode())) {
					Intent intent = new Intent(RoomTilesListActivity.this, RoomDetailsActivity.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable(STKNXControllerConstant.REMOTE_PARAM_KEY, mRoom);
					intent.putExtras(bundle);
					startActivity(intent);
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
										
							Intent intent = new Intent(RoomTilesListActivity.this, RoomDetailsActivity.class);
							Bundle bundle = new Bundle();
							bundle.putSerializable(STKNXControllerConstant.REMOTE_PARAM_KEY, mRoom);
							intent.putExtras(bundle);
							startActivity(intent);
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
					 new SettingDialog(RoomTilesListActivity.this).Show();
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
										 new SettingDialog(RoomTilesListActivity.this).Show(); 
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
					               		
					               		new SettingDialog(RoomTilesListActivity.this).Show(); 
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
				restartThisApp();
			}
			
		});
	}

	@Override
	public void onBackPressed() {
		if(android.os.Build.MODEL.equals(STKNXControllerConstant.STKCPLATFORM)) { // 若是世讯十寸机
			return;
		} else {
			STKNXControllerApp.getInstance().onDestroy();
			KNX0X01Lib.UCLOSENet();

			super.onBackPressed();
		}
	}
	
	private BroadcastReceiver refreshSystemTimeReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, final Intent intent) {

			if(intent.getAction().equals(STKNXControllerConstant.BROADCASTRECEIVER_REFRESH_SYSTEM_TIME)/* &&
					SystemUtil.isForeground(RoomTilesListActivity.this, "com.sation.knxcontroller.activity.RoomTilesListActivity")*/) {
				if(STKNXControllerApp.getInstance().getDisplayTimeFlag()) {
					if(null != txtSystemTime) {
						try {
							Log.i(TAG, "1");
			            	txtSystemTime.setText(intent.getStringExtra(STKNXControllerConstant.SYSTEMTIME));
			            	Log.i(TAG, "2");
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}
			}
		}
	};

	private void restartThisApp() {
		RoomTilesListActivity.this.startService(new Intent(RoomTilesListActivity.this, RestartService.class));
		((Activity) RoomTilesListActivity.this).finish();
	}
}
