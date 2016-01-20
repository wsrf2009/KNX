package com.zyyknx.android.activity;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.widget.Toast;

import com.zyyknx.android.R; 
import com.zyyknx.android.widget.settingview.*; 
import com.zyyknx.android.widget.settingview.SettingButton.onSettingButtonClickListener;
import com.zyyknx.android.widget.settingview.SettingButton.onSettingButtonSwitchListener;

public class SetttingActivity  extends BaseActivity {
	
	private SettingView mSettingView1 = null;
	private SettingView mSettingView2 = null;
	private SettingView mSettingView3 = null;
	private SettingButton mSettingButton1 = null;
	private SettingButton mSettingButton2 = null;
	private SettingButton mSettingButton3 = null;

	private SettingData mItemData = null;
	private SettingViewItemData mItemViewData = null;
	private List<SettingViewItemData> mListData = new ArrayList<SettingViewItemData>();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		setContentView(R.layout.setting_layout);
		
		mSettingView1 = (SettingView) findViewById(R.id.qq_style_setting_view_01);
		mSettingView2 = (SettingView) findViewById(R.id.qq_style_setting_view_02);
		mSettingView3 = (SettingView) findViewById(R.id.qq_style_setting_view_03);
		mSettingButton1 = (SettingButton) findViewById(R.id.qq_style_setting_button1);
		mSettingButton2 = (SettingButton) findViewById(R.id.qq_style_setting_button2);
		mSettingButton3 = (SettingButton) findViewById(R.id.qq_style_setting_button3);

		mSettingButton1.setOnSettingButtonClickListener(new onSettingButtonClickListener() {

			@Override
			public void onSettingButtonClick() {
				// TODO Auto-generated method stub
				Toast.makeText(SetttingActivity.this, "点击了SettingButton1", Toast.LENGTH_SHORT).show();
			}
		});

		mSettingButton2.setOnSettingButtonSwitchListener(new onSettingButtonSwitchListener() {

			@Override
			public void onSwitchChanged(boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					Toast.makeText(SetttingActivity.this, "SettingButton2开启", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(SetttingActivity.this, "SettingButton2关闭", Toast.LENGTH_SHORT).show();
				}
			}
		});

		initView();
	} 
	
	private void initView() {
		/* ==========================SettingView1========================== */
		mItemViewData = new SettingViewItemData();
		mItemData = new SettingData();
		mItemData.setTitle("流量更新");

		mItemViewData.setData(mItemData);
		mItemViewData.setItemView(new BasicItemViewH(SetttingActivity.this));
		mListData.add(mItemViewData);

		mItemViewData = new SettingViewItemData();
		mItemData = new SettingData();
		mItemData.setTitle("2G/3G/4G下自动接收图片");
		mItemData.setChecked(true);

		mItemViewData.setData(mItemData);
		mItemViewData.setItemView(new SwitchItemView(SetttingActivity.this));
		mListData.add(mItemViewData);

		mItemViewData = new SettingViewItemData();
		mItemData = new SettingData();
		mItemData.setTitle("2G/3G/4G下自动接收魔法动画");
		mItemData.setChecked(false);

		mItemViewData.setData(mItemData);
		mItemViewData.setItemView(new SwitchItemView(SetttingActivity.this));
		mListData.add(mItemViewData);

		mItemViewData = new SettingViewItemData();
		mItemData = new SettingData();
		mItemData.setTitle("Wifi下自动在后台下载新版本");
		mItemData.setChecked(true);

		mItemViewData.setData(mItemData);
		mItemViewData.setItemView(new SwitchItemView(SetttingActivity.this));
		mListData.add(mItemViewData);

		mSettingView1.setAdapter(mListData);
		/* ==========================SettingView1========================== */

		/* ==========================SettingView2========================== */
		mListData.clear();
		mItemViewData = new SettingViewItemData();
		mItemData = new SettingData();
		mItemData.setTitle("消息通知");

		mItemViewData.setData(mItemData);
		mItemViewData.setItemView(new BasicItemViewH(SetttingActivity.this));
		mListData.add(mItemViewData);

		mItemViewData = new SettingViewItemData();
		mItemData = new SettingData();
		mItemData.setTitle("聊天记录");

		mItemViewData.setData(mItemData);
		mItemViewData.setItemView(new BasicItemViewH(SetttingActivity.this));
		mListData.add(mItemViewData);

		mSettingView2.setAdapter(mListData);
		/* ==========================SettingView2========================== */
		/* ==========================SettingView3========================== */
		mListData.clear();
		mItemViewData = new SettingViewItemData();
		mItemData = new SettingData();
		mItemData.setTitle("空间动态");
		mItemData.setDrawable(getResources().getDrawable(R.drawable.icon10));

		mItemViewData.setData(mItemData);
		mItemViewData.setItemView(new BasicItemViewH(SetttingActivity.this));
		mListData.add(mItemViewData);

		mItemViewData = new SettingViewItemData();
		mItemData = new SettingData();
		mItemData.setTitle("文件助手");
		mItemData.setDrawable(getResources().getDrawable(R.drawable.icon03));

		mItemViewData.setData(mItemData);
		mItemViewData.setItemView(new BasicItemViewH(SetttingActivity.this));
		mListData.add(mItemViewData);

		mItemViewData = new SettingViewItemData();
		mItemData = new SettingData();
		mItemData.setTitle("扫一扫");
		mItemData.setDrawable(getResources().getDrawable(R.drawable.icon10));

		mItemViewData.setData(mItemData);
		mItemViewData.setItemView(new BasicItemViewH(SetttingActivity.this));
		mListData.add(mItemViewData);

		mItemViewData = new SettingViewItemData();
		mItemData = new SettingData();
		mItemData.setTitle("附近的人");
		mItemData.setDrawable(getResources().getDrawable(R.drawable.icon03));

		mItemViewData.setData(mItemData);
		mItemViewData.setItemView(new BasicItemViewH(SetttingActivity.this));
		mListData.add(mItemViewData);

		mItemViewData = new SettingViewItemData();
		mItemData = new SettingData();
		mItemData.setTitle("游戏");
		mItemData.setDrawable(getResources().getDrawable(R.drawable.icon10));

		mItemViewData.setData(mItemData);
		mItemViewData.setItemView(new BasicItemViewH(SetttingActivity.this));
		mListData.add(mItemViewData);

		mSettingView3.setAdapter(mListData);
		/* ==========================SettingView3========================== */
		/* ==========================SettingButton1========================== */
		mItemViewData = new SettingViewItemData();
		mItemData = new SettingData();
		mItemData.setTitle("账号管理");
		mItemData.setInfo(getResources().getDrawable(R.drawable.icon03));

		mItemViewData.setData(mItemData);
		mItemViewData.setItemView(new ImageItemView(SetttingActivity.this));
		mSettingButton1.setAdapter(mItemViewData);
		/* ==========================SettingButton1========================== */
		/* ==========================SettingButton2========================== */
		mItemViewData = new SettingViewItemData();
		mItemData = new SettingData();
		mItemData.setTitle("接收消息");
		mItemData.setInfo(getResources().getDrawable(R.drawable.icon10));

		mItemViewData.setData(mItemData);
		mItemViewData.setItemView(new SwitchItemView(SetttingActivity.this));
		mSettingButton2.setAdapter(mItemViewData);
		/* ==========================SettingButton2========================== */
		/* ==========================SettingButton3========================== */
		mItemViewData = new SettingViewItemData();
		mItemData = new SettingData();
		mItemData.setTitle("强迫症患者");
		mItemData.setSubTitle("1076559197");
		mItemData.setDrawable(getResources().getDrawable(R.drawable.icon03));
		mItemData.setChecked(true);

		mItemViewData.setData(mItemData);
		mItemViewData.setItemView(new CheckItemViewV(SetttingActivity.this));

		mSettingButton3.setAdapter(mItemViewData);
		/* ==========================SettingButton3========================== */
	}
}
