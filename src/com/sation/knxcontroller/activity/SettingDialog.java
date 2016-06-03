package com.sation.knxcontroller.activity; 

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.sation.knxcontroller.STKNXControllerApp;
import com.sation.knxcontroller.STKNXControllerConstant;
import com.sation.knxcontroller.services.RestartService;
import com.sation.knxcontroller.util.NetWorkUtil;
import com.sation.knxcontroller.widget.CustomPopDialogFragment;
import com.sation.knxcontroller.widget.PromptDialog;
import com.sation.knxcontroller.widget.settingview.ImageItemView;
import com.sation.knxcontroller.widget.settingview.SettingButton;
import com.sation.knxcontroller.widget.settingview.SettingData;
import com.sation.knxcontroller.widget.settingview.SettingViewItemData;
import com.sation.knxcontroller.widget.settingview.SettingButton.onSettingButtonClickListener;
import com.sation.knxcontroller.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
 

public class SettingDialog {
	private SharedPreferences settings;
	protected Context mContext;
	private SettingButton settingKNXConnect = null;
	private SettingButton settingPhysicalAddress = null; 
//	private SettingButton settingDeviceReflashSpan = null; 
	private SettingButton settingScreenOffSpan = null; 
	private SettingButton settingLanguage = null;
	private SettingButton systemStatus = null;
	private SettingData mItemData = null;
	private SettingViewItemData mItemViewData = null;
	private List<SettingViewItemData> mListData = new ArrayList<SettingViewItemData>();
	
	public SettingDialog(Context context) { 
		mContext = context;
		settings = mContext.getSharedPreferences(STKNXControllerConstant.SETTING_FILE, android.content.Context.MODE_PRIVATE);
	}

 
	public void Show() {
		final LayoutInflater inflater = LayoutInflater.from(mContext);
		final View mView = inflater.inflate(R.layout.setting_layout, null);
		final CustomPopDialogFragment mCustomPopDialogFragment = new CustomPopDialogFragment();
		
		mCustomPopDialogFragment.setTitle(mContext.getResources().getString(R.string.setting));
		mCustomPopDialogFragment.setContentView(mView);
		mCustomPopDialogFragment.show(((FragmentActivity) mContext).getSupportFragmentManager(), "TelephoneMonitoring");
		mCustomPopDialogFragment.setWindowSize(0.6, 0.6);

		settingKNXConnect = (SettingButton) mView.findViewById(R.id.settingKNXConnect);
		settingPhysicalAddress = (SettingButton) mView.findViewById(R.id.settingPhysicalAddress); 
//		settingDeviceReflashSpan = (SettingButton) mView.findViewById(R.id.settingDeviceReflashSpan); 
		settingScreenOffSpan = (SettingButton) mView.findViewById(R.id.settingScreenOffSpan);
		settingLanguage = (SettingButton) mView.findViewById(R.id.settingLanguage);
		systemStatus = (SettingButton) mView.findViewById(R.id.systemStatus);

		settingKNXConnect.setOnSettingButtonClickListener(new onSettingButtonClickListener() {

			@Override
			public void onSettingButtonClick() { 
				final View view = LayoutInflater.from(mContext).inflate(R.layout.knx_gateway_setting, null);  
				final EditText txtIP = (EditText) view.findViewById(R.id.txtIP);
				txtIP.setImeOptions(EditorInfo.IME_ACTION_NEXT);
				final EditText txtPort = (EditText) view.findViewById(R.id.txtPort);
				txtPort.setImeOptions(EditorInfo.IME_ACTION_DONE);
				final RadioButton rbGroupBroadcast = (RadioButton) view.findViewById(R.id.radioButtonUDPGroupBroadcast);
				final RadioButton rbPeerToPeer = (RadioButton) view.findViewById(R.id.radioButtonUDPPeerToPeer);
				rbGroupBroadcast.setOnCheckedChangeListener(new OnCheckedChangeListener(){
					
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						if(isChecked) {
							rbPeerToPeer.setChecked(false);
						}
					}
				});
				rbPeerToPeer.setOnCheckedChangeListener(new OnCheckedChangeListener(){
					
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						if(isChecked) {
							rbGroupBroadcast.setChecked(false);
						}
					}
				});
				
				String mKNXGatewayIP = settings.getString(STKNXControllerConstant.KNX_GATEWAY_IP, STKNXControllerConstant.KNX_GATEWAY_DEFAULT);
				int mKNXGatewayPort = settings.getInt(STKNXControllerConstant.KNX_GATEWAY_PORT, STKNXControllerConstant.KNX_GATEWAY_PORT_DEFAULT);
				int mKNXUDPWorkWay = settings.getInt(STKNXControllerConstant.KNX_UDP_WORK_WAY, STKNXControllerConstant.KNX_UDP_WORK_WAY_DEFAULT);
				txtIP.setText(mKNXGatewayIP);
				txtPort.setText(String.valueOf(mKNXGatewayPort));
				if(STKNXControllerConstant.KNX_UDP_WORK_WAY_GROUP_BROADCAST == mKNXUDPWorkWay) {
					rbGroupBroadcast.setChecked(true);
					rbPeerToPeer.setChecked(false);
				} else if (STKNXControllerConstant.KNX_UDP_WORK_WAY_PEER_TO_PEER == mKNXUDPWorkWay) {
					rbGroupBroadcast.setChecked(false);
					rbPeerToPeer.setChecked(true);
				} else {
					rbGroupBroadcast.setChecked(false);
					rbPeerToPeer.setChecked(false);
				}
				
				final PromptDialog mPromptDialog = new PromptDialog.Builder(mContext)
				.setTitle(mContext.getResources().getString(R.string.setting_knx_gateway)) 
				.setIcon(R.drawable.launcher)
				.setViewStyle(PromptDialog.VIEW_STYLE_NORMAL)
				.setView(view)
				.setButton1(mContext.getResources().getString(R.string.cancel),  new PromptDialog.OnClickListener() {
							
					@Override
					public void onClick(Dialog dialog, int which) {
						dialog.dismiss(); 
					}
				})
				.setButton2(mContext.getResources().getString(R.string.save), new PromptDialog.OnClickListener() {
							
					@Override
					public void onClick(Dialog dialog, int which) { 
						SharedPreferences.Editor editor = settings.edit(); 
						editor.putString(STKNXControllerConstant.KNX_GATEWAY_IP, txtIP.getText().toString());
						editor.putInt(STKNXControllerConstant.KNX_GATEWAY_PORT, Integer.valueOf(txtPort.getText().toString()));
						if(rbGroupBroadcast.isChecked()) { // 选中组播的工作方式
							editor.putInt(STKNXControllerConstant.KNX_UDP_WORK_WAY, 
									STKNXControllerConstant.KNX_UDP_WORK_WAY_GROUP_BROADCAST);
						} else if (rbPeerToPeer.isChecked()) { // 选中点对点的工作方式
							editor.putInt(STKNXControllerConstant.KNX_UDP_WORK_WAY,
									STKNXControllerConstant.KNX_UDP_WORK_WAY_PEER_TO_PEER);
						} else { // 默认情况：谁都没选中
							editor.putInt(STKNXControllerConstant.KNX_UDP_WORK_WAY, 
									STKNXControllerConstant.KNX_UDP_WORK_WAY_GROUP_BROADCAST);
						}
						editor.commit(); 
						dialog.dismiss();
						
						//重启启动
						mContext.startService(new Intent(mContext, RestartService.class));
						((Activity) mContext).finish();
					}
				})
				.show(); 
				
				txtPort.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					
					@Override
					public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
						
						/*判断是否是“DONE”键*/  
						if(actionId == EditorInfo.IME_ACTION_DONE) {
							/* 隐藏软键盘 */ 
							InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(  
                                Context.INPUT_METHOD_SERVICE); 
							if (imm.isActive()) {  
								imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);  
							}
						
							SharedPreferences.Editor editor = settings.edit(); 
							editor.putString(STKNXControllerConstant.KNX_GATEWAY_IP, txtIP.getText().toString());
							editor.putInt(STKNXControllerConstant.KNX_GATEWAY_PORT, Integer.valueOf(txtPort.getText().toString()));
							if(rbGroupBroadcast.isChecked()) { // 选中组播的工作方式
								editor.putInt(STKNXControllerConstant.KNX_UDP_WORK_WAY, 
									STKNXControllerConstant.KNX_UDP_WORK_WAY_GROUP_BROADCAST);
							} else if (rbPeerToPeer.isChecked()) { // 选中点对点的工作方式
								editor.putInt(STKNXControllerConstant.KNX_UDP_WORK_WAY,
									STKNXControllerConstant.KNX_UDP_WORK_WAY_PEER_TO_PEER);
							} else { // 默认情况：谁都没选中
								editor.putInt(STKNXControllerConstant.KNX_UDP_WORK_WAY, 
									STKNXControllerConstant.KNX_UDP_WORK_WAY_GROUP_BROADCAST);
							}
							editor.commit(); 
							mPromptDialog.dismiss();
							//重启启动
							mContext.startService(new Intent(mContext, RestartService.class));
							((Activity) mContext).finish();
						
							return true;
						}
						
						return false;
					}
				});
			}
		});

		settingPhysicalAddress.setOnSettingButtonClickListener(new onSettingButtonClickListener() {

			@Override
			public void onSettingButtonClick() {
				
				final View view = LayoutInflater.from(mContext).inflate(R.layout.physical_address_setting, null);  
				final EditText txtFirst = (EditText) view.findViewById(R.id.txtFirst);
				txtFirst.setImeOptions(EditorInfo.IME_ACTION_NEXT);
				final EditText txtSecond = (EditText) view.findViewById(R.id.txtSecond);
				txtSecond.setImeOptions(EditorInfo.IME_ACTION_NEXT);
				final EditText txtThree = (EditText) view.findViewById(R.id.txtThree);
				txtThree.setImeOptions(EditorInfo.IME_ACTION_DONE);
				
				int physicalAddressFirst = settings.getInt(STKNXControllerConstant.KNX_PHYSICAL_ADDRESS_FIRST, 0);
				int physicalAddressSecond = settings.getInt(STKNXControllerConstant.KNX_PHYSICAL_ADDRESS_SECOND, 0);
				int physicalAddressThree = settings.getInt(STKNXControllerConstant.KNX_PHYSICAL_ADDRESS_THREE, 0);
				txtFirst.setText(String.valueOf(physicalAddressFirst));
				txtSecond.setText(String.valueOf(physicalAddressSecond));
				txtThree.setText(String.valueOf(physicalAddressThree));
				
				final PromptDialog mPromptDialog = new PromptDialog.Builder(mContext)
				.setTitle(mContext.getResources().getString(R.string.setting_physical_address)) 
				.setIcon(R.drawable.launcher)
				.setViewStyle(PromptDialog.VIEW_STYLE_NORMAL)
				.setView(view)
				.setButton1(mContext.getResources().getString(R.string.cancel),  new PromptDialog.OnClickListener() {
							
					@Override
					public void onClick(Dialog dialog, int which) {
						dialog.dismiss(); 
					}
				})
				.setButton2(mContext.getResources().getString(R.string.save), new PromptDialog.OnClickListener() {
							
					@Override
					public void onClick(Dialog dialog, int which) { 
						SharedPreferences.Editor editor = settings.edit(); 
						editor.putInt(STKNXControllerConstant.KNX_PHYSICAL_ADDRESS_FIRST, 
										Integer.valueOf(txtFirst.getText().toString()));
						editor.putInt(STKNXControllerConstant.KNX_PHYSICAL_ADDRESS_SECOND, 
										Integer.valueOf(txtSecond.getText().toString()));
						editor.putInt(STKNXControllerConstant.KNX_PHYSICAL_ADDRESS_THREE,
										Integer.valueOf(txtThree.getText().toString()));
						editor.commit();
						dialog.dismiss(); 
						//重启启动
						mContext.startService(new Intent(mContext, RestartService.class));
						((Activity) mContext).finish();
								
					}
				})
				.show(); 
				 
				txtThree.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					
					@Override
					public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
						/*判断是否是“DONE”键*/  
						if(actionId == EditorInfo.IME_ACTION_DONE) {
							/* 隐藏软键盘 */ 
							InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(  
                                Context.INPUT_METHOD_SERVICE); 
							if (imm.isActive()) {  
								imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);  
							}
						
							SharedPreferences.Editor editor = settings.edit(); 
							editor.putInt(STKNXControllerConstant.KNX_PHYSICAL_ADDRESS_FIRST, 
									Integer.valueOf(txtFirst.getText().toString()));
							editor.putInt(STKNXControllerConstant.KNX_PHYSICAL_ADDRESS_SECOND, 
									Integer.valueOf(txtSecond.getText().toString()));
							editor.putInt(STKNXControllerConstant.KNX_PHYSICAL_ADDRESS_THREE,
									Integer.valueOf(txtThree.getText().toString()));
							editor.commit();
							mPromptDialog.dismiss(); 
							//重启启动
							mContext.startService(new Intent(mContext, RestartService.class));
							((Activity) mContext).finish();
						
							return true;
						}
						
						return false;
					}
				});
			}
		});
		
//		settingDeviceReflashSpan.setOnSettingButtonClickListener(new onSettingButtonClickListener() {
//
//			@Override
//			public void onSettingButtonClick() { 
//				// TODO Auto-genera 	  {
//				final View view = LayoutInflater.from(mContext).inflate(R.layout.knx_refresh_status_setting, null);  
//				final EditText txtTimeSpan = (EditText) view.findViewById(R.id.txtTimeSpan);
////				txtTimeSpan.setInputType(InputType.TYPE_CLASS_DATETIME);
//				
//				int knxRefreshStatusTimespan = settings.getInt(ZyyKNXConstant.KNX_REFRESH_STATUS_TIMESPAN, 1000); 
//				txtTimeSpan.setText(String.valueOf(knxRefreshStatusTimespan));
//				
//				new PromptDialog.Builder(mContext)
//				.setTitle("设置获取设备状态时间间隔") 
//				.setIcon(R.drawable.launcher)
//				//.setViewStyle(PromptDialog.VIEW_STYLE_TITLEBAR_SKYBLUE)
//				.setViewStyle(PromptDialog.VIEW_STYLE_NORMAL)
//				//.setMessage("请输入密码")
//				.setView(view)
//				.setButton1("取消",  new PromptDialog.OnClickListener() {
//							
//							@Override
//							public void onClick(Dialog dialog, int which) {
//								dialog.dismiss(); 
//							}
//						})
//				.setButton2("确认", new PromptDialog.OnClickListener() {
//							
//							@Override
//							public void onClick(Dialog dialog, int which) { 
//								SharedPreferences.Editor editor = settings.edit(); 
//								editor.putInt(ZyyKNXConstant.KNX_REFRESH_STATUS_TIMESPAN, Integer.valueOf(txtTimeSpan.getText().toString()));
//								editor.commit();
//								dialog.dismiss();
//								//重启启动
//								ZyyKNXApp.getInstance().startGetKNXResponseService();
//								mContext.startService(new Intent(mContext, RestartService.class));
//								((Activity) mContext).finish();
//								
//							}
//						})
//				.show(); 
//				 
//			}
//		});

		settingScreenOffSpan.setOnSettingButtonClickListener(new onSettingButtonClickListener() {

			@Override
			public void onSettingButtonClick() { 
				final View view = LayoutInflater.from(mContext).inflate(R.layout.knx_refresh_status_setting, null);  
				final EditText txtTimeSpan = (EditText) view.findViewById(R.id.txtTimeSpan);
				
				int knxSettingScreenOffTimespan = settings.getInt(STKNXControllerConstant.KNX_SETTING_SCRRENOFF_TIMESPAN, 30); 
				txtTimeSpan.setText(String.valueOf(knxSettingScreenOffTimespan));
				
				new PromptDialog.Builder(mContext)
				.setTitle(mContext.getResources().getString(R.string.setting_back_light_time)) 
				.setIcon(R.drawable.launcher)
				//.setViewStyle(PromptDialog.VIEW_STYLE_TITLEBAR_SKYBLUE)
				.setViewStyle(PromptDialog.VIEW_STYLE_NORMAL)
				//.setMessage("请输入密码")
				.setView(view)
				.setButton1(mContext.getResources().getString(R.string.cancel),  new PromptDialog.OnClickListener() {
							
							@Override
							public void onClick(Dialog dialog, int which) {
								dialog.dismiss(); 
							}
						})
				.setButton2(mContext.getResources().getString(R.string.save), new PromptDialog.OnClickListener() {
							
							@Override
							public void onClick(Dialog dialog, int which) { 
								SharedPreferences.Editor editor = settings.edit(); 
								editor.putInt(STKNXControllerConstant.KNX_SETTING_SCRRENOFF_TIMESPAN, Integer.valueOf(txtTimeSpan.getText().toString()));
								editor.commit();
								dialog.dismiss();
								
								Settings.System.putInt(mContext.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, 1000 * Integer.valueOf(txtTimeSpan.getText().toString()));
							}
						})
				.show(); 
				 
			}
		});
		
		settingLanguage.setOnSettingButtonClickListener(new onSettingButtonClickListener() {

			@Override
			public void onSettingButtonClick() {
				final View view = LayoutInflater.from(mContext).inflate(R.layout.setting_language, null);
				final RadioButton chinese = (RadioButton) view.findViewById(R.id.settingLanguageRadioButtonChinese);
				final RadioButton english = (RadioButton) view.findViewById(R.id.settingLanguageRadioButtonEnglish);
				
				String language = settings.getString(STKNXControllerConstant.APP_APPEARANCE_LANGUAGE, Locale.US.toString());
				if(language.equals(Locale.SIMPLIFIED_CHINESE.toString())) {
					chinese.setChecked(true);
					english.setChecked(false);
				} else {
					chinese.setChecked(false);
					english.setChecked(true);
				}
				
				chinese.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						if(isChecked) {
							english.setChecked(false);
						}
					}
				});
				
				english.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						if(isChecked) {
							chinese.setChecked(false);
						}
					}
				});
				
				new PromptDialog.Builder(mContext)
				.setTitle(mContext.getResources().getString(R.string.setting_language))
				.setIcon(R.drawable.launcher)
				.setViewStyle(PromptDialog.VIEW_STYLE_NORMAL)
				.setView(view)
				.setButton1(mContext.getResources().getString(R.string.cancel), new PromptDialog.OnClickListener() {
					
					@Override
					public void onClick(Dialog dialog, int which) {
						dialog.dismiss();
					}
				})
				.setButton2(mContext.getResources().getString(R.string.save), new PromptDialog.OnClickListener() {
					
					@Override
					public void onClick(Dialog dialog, int which) {
						SharedPreferences.Editor editor = settings.edit();
						if(chinese.isChecked()) {
							Resources resources = mContext.getResources();//获得res资源对象  
						    Configuration config = resources.getConfiguration();//获得设置对象  
						    DisplayMetrics dm = resources .getDisplayMetrics();//获得屏幕参数：主要是分辨率，像素等。  
						    config.locale = Locale.SIMPLIFIED_CHINESE; //简体中文  
						    resources.updateConfiguration(config, dm);
						    
							editor.putString(STKNXControllerConstant.APP_APPEARANCE_LANGUAGE, Locale.SIMPLIFIED_CHINESE.toString());
						} else {
							Resources resources = mContext.getResources();//获得res资源对象  
						    Configuration config = resources.getConfiguration();//获得设置对象  
						    DisplayMetrics dm = resources .getDisplayMetrics();//获得屏幕参数：主要是分辨率，像素等。  
						    config.locale = Locale.US; //简体中文  
						    resources.updateConfiguration(config, dm);
						    
							editor.putString(STKNXControllerConstant.APP_APPEARANCE_LANGUAGE, Locale.US.toString());
						}
						editor.commit();
						dialog.dismiss();
						
						mContext.startService(new Intent(mContext, RestartService.class));
						((Activity) mContext).finish();
					}
				})
				.show();
			}
			
		});
		
		systemStatus.setOnSettingButtonClickListener(new onSettingButtonClickListener(){
			
			@Override
			public void onSettingButtonClick(){
				final View view = LayoutInflater.from(mContext).inflate(R.layout.system_status, null);
				final TextView txtLoacalIP = (TextView) view.findViewById(R.id.systemStatusTextViewLocalIP);
				txtLoacalIP.setText(NetWorkUtil.getIpAddress());
				
				final CheckBox cbDisplaySystemTime = (CheckBox) view.findViewById(R.id.systemStatusCheckBoxDisplaySystemTime);
				boolean displaySystemTime = settings.getBoolean(STKNXControllerConstant.SYSTEMSTATUS_DISPLAY_SYSTEM_TIME, true);
				cbDisplaySystemTime.setChecked(displaySystemTime);
				
				new PromptDialog.Builder(mContext)
				.setTitle(mContext.getResources().getString(R.string.system_status))
				.setIcon(R.drawable.launcher)
				.setViewStyle(PromptDialog.VIEW_STYLE_NORMAL)
				.setView(view)
				.setButton1(mContext.getResources().getString(R.string.cancel), new PromptDialog.OnClickListener() {
					
					@Override
					public void onClick(Dialog dialog, int which) {
						dialog.dismiss();
					}
				})
				.setButton2(mContext.getResources().getString(R.string.save), new PromptDialog.OnClickListener() {
					
					@Override
					public void onClick(Dialog dialog, int which) {
						RoomTilesListActivity roomTiles = (RoomTilesListActivity)mContext;
						if(cbDisplaySystemTime.isChecked()) {
							roomTiles.displaySystemTime();
						} else {
							roomTiles.undisplaySystemTime();
						}
						SharedPreferences.Editor editor = settings.edit();
						editor.putBoolean(STKNXControllerConstant.SYSTEMSTATUS_DISPLAY_SYSTEM_TIME, cbDisplaySystemTime.isChecked());
						editor.commit();
						dialog.dismiss();
					}
				})
				.show();
			}
		});

		initView();
	 
		mCustomPopDialogFragment.setBackButton(mContext.getResources().getString(R.string.close), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// 关闭对话框
				mCustomPopDialogFragment.dismiss();
			}
		});
	}
	
	private void initView() {
		mItemViewData = new SettingViewItemData();
		mItemData = new SettingData();
		mItemData.setTitle(mContext.getResources().getString(R.string.setting_knx_gateway));
		mItemViewData.setData(mItemData);
		mItemViewData.setItemView(new ImageItemView(mContext));
		settingKNXConnect.setAdapter(mItemViewData);
		
		mItemViewData = new SettingViewItemData();
		mItemData = new SettingData();
		mItemData.setTitle(mContext.getResources().getString(R.string.setting_physical_address));
		mItemViewData.setData(mItemData);
		mItemViewData.setItemView(new ImageItemView(mContext));
		settingPhysicalAddress.setAdapter(mItemViewData);
		
//		mItemViewData = new SettingViewItemData();
//		mItemData = new SettingData();
//		mItemData.setTitle("设置获取设备状态时间间隔");
//		mItemViewData.setData(mItemData);
//		mItemViewData.setItemView(new ImageItemView(mContext));
//		settingDeviceReflashSpan.setAdapter(mItemViewData);
		
		mItemViewData = new SettingViewItemData();
		mItemData = new SettingData();
		mItemData.setTitle("设置背光时间");
		mItemViewData.setData(mItemData);
		mItemViewData.setItemView(new ImageItemView(mContext));
		settingScreenOffSpan.setAdapter(mItemViewData);
		
		mItemViewData = new SettingViewItemData();
		mItemData = new SettingData();
		mItemData.setTitle(mContext.getResources().getString(R.string.setting_language));
		mItemViewData.setData(mItemData);
		mItemViewData.setItemView(new ImageItemView(mContext));
		settingLanguage.setAdapter(mItemViewData);
		
		mItemViewData = new SettingViewItemData();
		mItemData = new SettingData();
		mItemData.setTitle(mContext.getResources().getString(R.string.system_status));
		mItemViewData.setData(mItemData);
		mItemViewData.setItemView(new ImageItemView(mContext));
		systemStatus.setAdapter(mItemViewData);
	}
}
