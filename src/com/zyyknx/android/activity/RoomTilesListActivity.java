package com.zyyknx.android.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.zyyknx.android.R;
import com.zyyknx.android.ZyyKNXApp;
import com.zyyknx.android.ZyyKNXConstant;
import com.zyyknx.android.adapter.RoomListAdapter;
import com.zyyknx.android.adapter.RoomListAdapter.OnItemActionListener;
import com.zyyknx.android.models.KNXApp;
import com.zyyknx.android.models.KNXArea;
import com.zyyknx.android.models.KNXRoom;
import com.zyyknx.android.util.StringUtil;
import com.zyyknx.android.widget.PromptDialog;

public class RoomTilesListActivity  extends BaseActivity { 
	
	private GridView gridView;
	private RoomListAdapter mRoomListAdapter;
	private ImageView imgSettting;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.room_tiles_list_layout);
		
		init();
	} 
	private void init() {   
		//WebView aboutWebView = (WebView) findViewById(R.id.aboutWebView);
		//aboutWebView.loadData(FileUtils.getSDFileContent("zyyknxandroid/.nomedia/res/html/about.html"), "text/html", "UTF-8"); 
		gridView = (GridView) findViewById(R.id.gridView);
		imgSettting = (ImageView) findViewById(R.id.imgSettting);
		imgSettting.setOnClickListener(new OnClickListener(){
			
			 @Override
			 public void onClick(View v) {  
				 final View view = LayoutInflater.from(RoomTilesListActivity.this).inflate(R.layout.password_layout, null);  
					new PromptDialog.Builder(RoomTilesListActivity.this)
					.setTitle("访问限制") 
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
					.setButton2("继续", new PromptDialog.OnClickListener() {
								
								@Override
								public void onClick(Dialog dialog, int which) {
									EditText txtPassword = (EditText) view.findViewById(R.id.txtPassword);
									if(txtPassword.getText().toString().trim().equalsIgnoreCase("654321")) {  
										dialog.dismiss();
										new SettingDialog(RoomTilesListActivity.this).Show(); 
									} else {
										Toast.makeText(RoomTilesListActivity.this, "密码不正确", Toast.LENGTH_SHORT).show();
									}
								}
							})
					.show();  
				 
			 }
		});
		
		List<KNXRoom> mRoomList = new ArrayList<KNXRoom>();
		
		KNXApp mControlEditor = ZyyKNXApp.getInstance().getAppConfig();
		for (int i = 0; i < mControlEditor.getAreas().size(); i++) { 
			KNXArea mArea = mControlEditor.getAreas().get(i);
			for (int j = 0; j < mArea.getRooms().size(); j++) { 
				KNXRoom mRoom = mArea.getRooms().get(j);
				//mRoom.setIcon("zyyknxandroid/.nomedia/" + mRoom.getIcon());
				if(StringUtil.isEmpty(mRoom.getSymbol())) {
					mRoom.setSymbol("zyyknxandroid/.nomedia/res/img/liveroom_background.jpg");
				} else {
					mRoom.setSymbol("zyyknxandroid/.nomedia/res/img/" + mRoom.getSymbol());
				}
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
					bundle.putSerializable(ZyyKNXConstant.REMOTE_PARAM_KEY, mRoom);
					intent.putExtras(bundle);
					startActivity(intent);
				} else {
					
					final View view = LayoutInflater.from(RoomTilesListActivity.this).inflate(R.layout.password_layout, null);  
					new PromptDialog.Builder(RoomTilesListActivity.this)
					.setTitle("访问限制") 
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
					.setButton2("继续", new PromptDialog.OnClickListener() {
								
								@Override
								public void onClick(Dialog dialog, int which) {
									EditText txtPassword = (EditText) view.findViewById(R.id.txtPassword);
									dialog.dismiss();
									if(txtPassword.getText().toString().trim().equalsIgnoreCase(mRoom.getPinCode())) { 
										
										Intent intent = new Intent(RoomTilesListActivity.this, RoomDetailsActivity.class);
										Bundle bundle = new Bundle();
										bundle.putSerializable(ZyyKNXConstant.REMOTE_PARAM_KEY, mRoom);
										intent.putExtras(bundle);
										startActivity(intent);
									} else {
										Toast.makeText(RoomTilesListActivity.this, "密码不正确", Toast.LENGTH_SHORT).show();
									}
								}
							})
					.show(); 
					 
				}
			}
		});
	}
	
	 
	@Override
	public void onBackPressed() {
		 return;
	} 
}
