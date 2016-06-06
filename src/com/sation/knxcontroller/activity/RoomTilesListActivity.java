package com.sation.knxcontroller.activity;

import java.util.ArrayList;
import java.util.List;

import com.sation.knxcontroller.STKNXControllerApp;
import com.sation.knxcontroller.STKNXControllerConstant;
import com.sation.knxcontroller.adapter.RoomListAdapter;
import com.sation.knxcontroller.adapter.RoomListAdapter.OnItemActionListener;
import com.sation.knxcontroller.models.KNXApp;
import com.sation.knxcontroller.models.KNXArea;
import com.sation.knxcontroller.models.KNXRoom;
import com.sation.knxcontroller.util.Log;
import com.sation.knxcontroller.util.StringUtil;
import com.sation.knxcontroller.util.SystemUtil;
import com.sation.knxcontroller.widget.PromptDialog;
import com.sation.knxcontroller.R;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
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
import android.widget.TextView;
import android.widget.Toast;

public class RoomTilesListActivity  extends BaseActivity { 
	
	private GridView gridView;
	private RoomListAdapter mRoomListAdapter;
	private ImageView imgSettting;
	private RefreshSystemTimeReceiver mRefreshSystemTimeReceiver;
	private static TextView txtSystemTime;
	private SharedPreferences settings;
	private boolean displaySystemTime;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.room_tiles_list_layout);
		txtSystemTime = (TextView)findViewById(R.id.roomTilesListTextViewDateTime);
		settings = getSharedPreferences(STKNXControllerConstant.SETTING_FILE, android.content.Context.MODE_PRIVATE);

		init();
		
//		SystemUtil.getMountPathList();
	}
	
	@Override
	protected void onStart(){
		super.onStart();
	}

	@Override
	protected void onRestart(){
		super.onRestart();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		displaySystemTime = settings.getBoolean(STKNXControllerConstant.SYSTEMSTATUS_DISPLAY_SYSTEM_TIME, true);
		if(displaySystemTime) {
			txtSystemTime.setVisibility(View.VISIBLE);
		} else {
			txtSystemTime.setVisibility(View.INVISIBLE);
		}
		mRefreshSystemTimeReceiver = new RefreshSystemTimeReceiver();
	    IntentFilter filter = new IntentFilter(STKNXControllerConstant.BROADCASTRECEIVER_REFRESH_SYSTEM_TIME);
	    registerReceiver(mRefreshSystemTimeReceiver, filter);  
	}
	@Override
	public void onPause() {
		super.onPause();

		unregisterReceiver(mRefreshSystemTimeReceiver);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy(){
		super.onDestroy();
	}
	
	public void displaySystemTime() {
        txtSystemTime.setVisibility(View.VISIBLE);
	}
	
	public void undisplaySystemTime() {
        txtSystemTime.setVisibility(View.INVISIBLE);
	}
	
	private void init() {   
		
		Log.i("RoomTilesListActivity", "init()====>>>");
		
		gridView = (GridView) findViewById(R.id.gridView);
		imgSettting = (ImageView) findViewById(R.id.imgSettting);
		imgSettting.setOnClickListener(new OnClickListener() {

			 @Override
			 public void onClick(View v) {
				 final View view = LayoutInflater.from(RoomTilesListActivity.this).inflate(R.layout.password_layout, null);
				 final EditText txtPassword = (EditText) view.findViewById(R.id.txtPassword);
				 txtPassword.setImeOptions(EditorInfo.IME_ACTION_GO);

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
									
						 if(systemSettingPasswordIsCorrect(txtPassword.getText().toString().trim())) {  
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

			               	if(systemSettingPasswordIsCorrect(txtPassword.getText().toString().trim())) {
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
		});
		
		List<KNXRoom> mRoomList = new ArrayList<KNXRoom>();
		KNXApp mControlEditor = STKNXControllerApp.getInstance().getAppConfig();
		for (int i = 0; i < mControlEditor.getAreas().size(); i++) { 
			KNXArea mArea = mControlEditor.getAreas().get(i);
			for (int j = 0; j < mArea.getRooms().size(); j++) { 
				KNXRoom mRoom = mArea.getRooms().get(j);
//				Log.i("RoomTilesListActivity", "symbol:"+mRoom.getSymbol());
//				if(StringUtil.isEmpty(mRoom.getSymbol())) {
//					mRoom.setSymbol(STKNXControllerConstant.ConfigResImgPath+"liveroom_background.jpg");
//				} else {
//					mRoom.setSymbol(STKNXControllerConstant.ConfigResImgPath + mRoom.getSymbol());
//				}
//				Log.i("RoomTilesListActivity", "ConfigResImgPath:"+STKNXControllerConstant.ConfigResImgPath+" symbol:"+mRoom.getSymbol());
				mRoomList.add(mRoom);
			}
		}
		mRoomListAdapter = new RoomListAdapter(RoomTilesListActivity.this, mRoomList);
		gridView.setAdapter(mRoomListAdapter); 
		mRoomListAdapter.setOnItemActionListener(new OnItemActionListener() {
			
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
		
		ImageView imageMenuSetting = (ImageView)findViewById(R.id.imgMenuSettting);
		imageMenuSetting.setOnClickListener(new OnClickListener() {

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
	}

	@Override
	public void onBackPressed() {
		 return;
	}
	
	private class RefreshSystemTimeReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, final Intent intent) {

			if(intent.getAction().equals(STKNXControllerConstant.BROADCASTRECEIVER_REFRESH_SYSTEM_TIME)) {
				displaySystemTime = settings.getBoolean(STKNXControllerConstant.SYSTEMSTATUS_DISPLAY_SYSTEM_TIME, true);
				if(displaySystemTime) {
					txtSystemTime.post(new Runnable(){  
			            @Override  
			            public void run() {  
			            	txtSystemTime.setText(intent.getStringExtra(STKNXControllerConstant.SYSTEMTIME));
			            }
					});
				} 
			}
		}
	}
	
	private boolean systemSettingPasswordIsCorrect(String password) {
		if(password.trim().equalsIgnoreCase("654321")) {  
			 return true;
		 } else {
			 return false;
		 }
	}
}
