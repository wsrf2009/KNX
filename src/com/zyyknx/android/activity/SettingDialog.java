package com.zyyknx.android.activity; 

import java.util.ArrayList;
import java.util.List;
 
import com.zyyknx.android.R;
import com.zyyknx.android.ZyyKNXApp;
import com.zyyknx.android.ZyyKNXConstant;
import com.zyyknx.android.services.RestartService;
import com.zyyknx.android.widget.CustomPopDialogFragment;
import com.zyyknx.android.widget.PromptDialog;
import com.zyyknx.android.widget.settingview.BasicItemViewH;
import com.zyyknx.android.widget.settingview.CheckItemViewV;
import com.zyyknx.android.widget.settingview.ImageItemView;
import com.zyyknx.android.widget.settingview.SettingButton;
import com.zyyknx.android.widget.settingview.SettingData;
import com.zyyknx.android.widget.settingview.SettingView;
import com.zyyknx.android.widget.settingview.SettingViewItemData;
import com.zyyknx.android.widget.settingview.SwitchItemView;
import com.zyyknx.android.widget.settingview.SettingButton.onSettingButtonClickListener;
import com.zyyknx.android.widget.settingview.SettingButton.onSettingButtonSwitchListener;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View; 
import android.widget.EditText;
import android.widget.Toast;
 

public class SettingDialog {
	
	private SharedPreferences settings;

	protected Context mContext;
	
	//private SettingView mSettingView1 = null;
	//private SettingView mSettingView2 = null; 
	private SettingButton settingKNXConnect = null;
	private SettingButton settingPhysicalAddress = null; 
	private SettingButton settingDeviceReflashSpan = null; 
	private SettingData mItemData = null;
	private SettingViewItemData mItemViewData = null;
	private List<SettingViewItemData> mListData = new ArrayList<SettingViewItemData>();
	
	public SettingDialog(Context context) { 
		mContext = context;
		settings = mContext.getSharedPreferences(ZyyKNXConstant.SETTING_FILE, android.content.Context.MODE_PRIVATE);
	}

 
	public void Show() {
		
		
		final LayoutInflater inflater = LayoutInflater.from(mContext);
		final View mView = inflater.inflate(R.layout.setting_layout, null);

		final CustomPopDialogFragment mCustomPopDialogFragment = new CustomPopDialogFragment();
		mCustomPopDialogFragment.setTitle("系统设置");
		mCustomPopDialogFragment.setContentView(mView);
		mCustomPopDialogFragment.show(((FragmentActivity) mContext).getSupportFragmentManager(), "TelephoneMonitoring");
		mCustomPopDialogFragment.setWindowSize(0.6, 0.6);
		
		//mSettingView1 = (SettingView) mView.findViewById(R.id.qq_style_setting_view_01);
		//mSettingView2 = (SettingView) mView.findViewById(R.id.qq_style_setting_view_02); 
		settingKNXConnect = (SettingButton) mView.findViewById(R.id.settingKNXConnect);
		settingPhysicalAddress = (SettingButton) mView.findViewById(R.id.settingPhysicalAddress); 
		settingDeviceReflashSpan = (SettingButton) mView.findViewById(R.id.settingDeviceReflashSpan); 

		settingKNXConnect.setOnSettingButtonClickListener(new onSettingButtonClickListener() {

			@Override
			public void onSettingButtonClick() { 
				// TODO Auto-generated method stub
				final View view = LayoutInflater.from(mContext).inflate(R.layout.knx_gateway_setting, null);  
				final EditText txtIP = (EditText) view.findViewById(R.id.txtIP);
				final EditText txtPort = (EditText) view.findViewById(R.id.txtPort);
				
				String mKNXGetWayIP = settings.getString(ZyyKNXConstant.KNX_GETEWAY_IP, "192.168.1.222");
				int mKNXGetWayPort = settings.getInt(ZyyKNXConstant.KNX_GETEWAY_Port, 3671);
				txtIP.setText(mKNXGetWayIP);
				txtPort.setText(String.valueOf(mKNXGetWayPort));
				
				new PromptDialog.Builder(mContext)
				.setTitle("KXN网关设置") 
				.setIcon(R.drawable.launcher)
				//.setViewStyle(PromptDialog.VIEW_STYLE_TITLEBAR_SKYBLUE)
				.setViewStyle(PromptDialog.VIEW_STYLE_NORMAL)
				//.setMessage("请输入密码")
				.setView(view)
				.setButton1("取消",  new PromptDialog.OnClickListener() {
							
							@Override
							public void onClick(Dialog dialog, int which) {
								dialog.dismiss(); 
							}
						})
				.setButton2("确认", new PromptDialog.OnClickListener() {
							
							@Override
							public void onClick(Dialog dialog, int which) { 
								SharedPreferences.Editor editor = settings.edit(); 
								editor.putString(ZyyKNXConstant.KNX_GETEWAY_IP, txtIP.getText().toString());
								editor.putInt(ZyyKNXConstant.KNX_GETEWAY_Port, Integer.valueOf(txtPort.getText().toString()));
								editor.commit(); 
								dialog.dismiss(); 
								//重启启动
								mContext.startService(new Intent(mContext, RestartService.class));
								((Activity) mContext).finish();
							}
						})
				.show(); 
				
			}
		});

		settingPhysicalAddress.setOnSettingButtonClickListener(new onSettingButtonClickListener() {

			@Override
			public void onSettingButtonClick() {
				
				final View view = LayoutInflater.from(mContext).inflate(R.layout.physical_address_setting, null);  
				final EditText txtFirst = (EditText) view.findViewById(R.id.txtFirst);
				final EditText txtSecond = (EditText) view.findViewById(R.id.txtSecond);
				final EditText txtThree = (EditText) view.findViewById(R.id.txtThree);
				
				int physicalAddressFirst = settings.getInt(ZyyKNXConstant.KNX_PHYSICAL_ADDRESS_FIRST, 0);
				int physicalAddressSecond = settings.getInt(ZyyKNXConstant.KNX_PHYSICAL_ADDRESS_SECOND, 0);
				int physicalAddressThree = settings.getInt(ZyyKNXConstant.KNX_PHYSICAL_ADDRESS_THREE, 0);
				txtFirst.setText(String.valueOf(physicalAddressFirst));
				txtSecond.setText(String.valueOf(physicalAddressSecond));
				txtThree.setText(String.valueOf(physicalAddressThree));
				
				// TODO Auto-genera 	  {
				new PromptDialog.Builder(mContext)
				.setTitle("物理地址设置") 
				.setIcon(R.drawable.launcher)
				//.setViewStyle(PromptDialog.VIEW_STYLE_TITLEBAR_SKYBLUE)
				.setViewStyle(PromptDialog.VIEW_STYLE_NORMAL)
				//.setMessage("请输入密码")
				.setView(view)
				.setButton1("取消",  new PromptDialog.OnClickListener() {
							
							@Override
							public void onClick(Dialog dialog, int which) {
								dialog.dismiss(); 
							}
						})
				.setButton2("确认", new PromptDialog.OnClickListener() {
							
							@Override
							public void onClick(Dialog dialog, int which) { 
								SharedPreferences.Editor editor = settings.edit(); 
								editor.putInt(ZyyKNXConstant.KNX_PHYSICAL_ADDRESS_FIRST, Integer.valueOf(txtFirst.getText().toString()));
								editor.putInt(ZyyKNXConstant.KNX_PHYSICAL_ADDRESS_SECOND, Integer.valueOf(txtSecond.getText().toString()));
								editor.putInt(ZyyKNXConstant.KNX_PHYSICAL_ADDRESS_THREE, Integer.valueOf(txtThree.getText().toString()));
								editor.commit();
								dialog.dismiss(); 
								//重启启动
								mContext.startService(new Intent(mContext, RestartService.class));
								((Activity) mContext).finish();
								
							}
						})
				.show(); 
				 
			}
		});
		
		settingDeviceReflashSpan.setOnSettingButtonClickListener(new onSettingButtonClickListener() {

			@Override
			public void onSettingButtonClick() { 
				// TODO Auto-genera 	  {
				final View view = LayoutInflater.from(mContext).inflate(R.layout.knx_refresh_status_setting, null);  
				final EditText txtTimeSpan = (EditText) view.findViewById(R.id.txtTimeSpan);
				
				int knxRefreshStatusTimespan = settings.getInt(ZyyKNXConstant.KNX_REFRESH_STATUS_TIMESPAN, 1000); 
				txtTimeSpan.setText(String.valueOf(knxRefreshStatusTimespan));
				
				new PromptDialog.Builder(mContext)
				.setTitle("设置获取设备状态时间间隔") 
				.setIcon(R.drawable.launcher)
				//.setViewStyle(PromptDialog.VIEW_STYLE_TITLEBAR_SKYBLUE)
				.setViewStyle(PromptDialog.VIEW_STYLE_NORMAL)
				//.setMessage("请输入密码")
				.setView(view)
				.setButton1("取消",  new PromptDialog.OnClickListener() {
							
							@Override
							public void onClick(Dialog dialog, int which) {
								dialog.dismiss(); 
							}
						})
				.setButton2("确认", new PromptDialog.OnClickListener() {
							
							@Override
							public void onClick(Dialog dialog, int which) { 
								SharedPreferences.Editor editor = settings.edit(); 
								editor.putInt(ZyyKNXConstant.KNX_REFRESH_STATUS_TIMESPAN, Integer.valueOf(txtTimeSpan.getText().toString()));
								editor.commit();
								dialog.dismiss();
								//重启启动
								ZyyKNXApp.getInstance().startGetKNXResponseService();
								mContext.startService(new Intent(mContext, RestartService.class));
								((Activity) mContext).finish();
								
							}
						})
				.show(); 
				 
			}
		});

		initView(); 
		
	 
		mCustomPopDialogFragment.setBackButton("关闭",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// 关闭对话框
						mCustomPopDialogFragment.dismiss();
					}
				});
		mCustomPopDialogFragment.setConfirmButton("确认",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) { 
						 
						mCustomPopDialogFragment.dismiss();
					}
				});
	}
	
	private void initView() {
		/* ==========================SettingView1========================== */
		/*
		mItemViewData = new SettingViewItemData();
		mItemData = new SettingData();
		mItemData.setTitle("流量更新");

		mItemViewData.setData(mItemData);
		mItemViewData.setItemView(new BasicItemViewH(mContext));
		mListData.add(mItemViewData);

		mItemViewData = new SettingViewItemData();
		mItemData = new SettingData();
		mItemData.setTitle("2G/3G/4G下自动接收图片");
		mItemData.setChecked(true);

		mItemViewData.setData(mItemData);
		mItemViewData.setItemView(new SwitchItemView(mContext));
		mListData.add(mItemViewData);

		mItemViewData = new SettingViewItemData();
		mItemData = new SettingData();
		mItemData.setTitle("2G/3G/4G下自动接收魔法动画");
		mItemData.setChecked(false);

		mItemViewData.setData(mItemData);
		mItemViewData.setItemView(new SwitchItemView(mContext));
		mListData.add(mItemViewData);

		mItemViewData = new SettingViewItemData();
		mItemData = new SettingData();
		mItemData.setTitle("Wifi下自动在后台下载新版本");
		mItemData.setChecked(true);

		mItemViewData.setData(mItemData);
		mItemViewData.setItemView(new SwitchItemView(mContext));
		mListData.add(mItemViewData);

		mSettingView1.setAdapter(mListData);
		*/
		/* ==========================SettingView1========================== */
  
		/* ==========================SettingButton1========================== */
		mItemViewData = new SettingViewItemData();
		mItemData = new SettingData();
		mItemData.setTitle("KXN网关设置");
		//mItemData.setInfo(mContext.getResources().getDrawable(R.drawable.icon03));

		mItemViewData.setData(mItemData);
		mItemViewData.setItemView(new ImageItemView(mContext));
		settingKNXConnect.setAdapter(mItemViewData);
		/* ==========================SettingButton1========================== */
		
		/* ==========================SettingButton2========================== */
		mItemViewData = new SettingViewItemData();
		mItemData = new SettingData();
		mItemData.setTitle("物理地址设置");
		//mItemData.setInfo(mContext.getResources().getDrawable(R.drawable.icon10));

		mItemViewData.setData(mItemData);
		mItemViewData.setItemView(new ImageItemView(mContext));
		settingPhysicalAddress.setAdapter(mItemViewData);
		/* ==========================SettingButton2========================== */ 
		
		mItemViewData = new SettingViewItemData();
		mItemData = new SettingData();
		mItemData.setTitle("设置获取设备状态时间间隔");
		//mItemData.setInfo(mContext.getResources().getDrawable(R.drawable.icon10));

		mItemViewData.setData(mItemData);
		mItemViewData.setItemView(new ImageItemView(mContext));
		settingDeviceReflashSpan.setAdapter(mItemViewData);
	}
}
