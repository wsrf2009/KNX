package com.zyyknx.android.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Dialog;
import android.content.Context; 
import android.content.DialogInterface;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View; 
import android.widget.GridView;
import android.widget.ListView;

import com.zyyknx.android.R;
import com.zyyknx.android.ZyyKNXApp;
import com.zyyknx.android.adapter.RoomListAdapter;
import com.zyyknx.android.adapter.ScenneListAdapter;
import com.zyyknx.android.control.KNXControlBase;
import com.zyyknx.android.models.KNXApp;
import com.zyyknx.android.util.StringUtil;
import com.zyyknx.android.widget.CustomPopDialogFragment;

public class SceneRecordDialog {
	 
	protected Context mContext;
	private ListView listView;
	private ScenneListAdapter mScenneListAdapter;
	
	public SceneRecordDialog(Context context,int currentControlId) { 
		mContext = context;
	 }

 
	public void Show() { 
		final LayoutInflater inflater = LayoutInflater.from(mContext);
		final View mView = inflater.inflate(R.layout.scene_record_layout, null); 
		
		List<KNXControlBase> mKNXControlBaseList = new ArrayList<KNXControlBase>();
		Map<Integer, KNXControlBase> currentPageKNXControlBaseMap = ZyyKNXApp.getInstance().getCurrentPageKNXControlBaseMap();
		for (Integer key : currentPageKNXControlBaseMap.keySet()) {   
			KNXControlBase mKNXControlBase = currentPageKNXControlBaseMap.get(key); 
			mKNXControlBaseList.add(mKNXControlBase); 
		}  
		
		listView = (ListView) mView.findViewById(R.id.listView);
		
		mScenneListAdapter = new ScenneListAdapter(mContext, mKNXControlBaseList);
		listView.setAdapter(mScenneListAdapter); 

		final CustomPopDialogFragment mCustomPopDialogFragment = new CustomPopDialogFragment();
		mCustomPopDialogFragment.setTitle("场景记录");
		mCustomPopDialogFragment.setContentView(mView);
		mCustomPopDialogFragment.show(((FragmentActivity) mContext).getSupportFragmentManager(), "SceneRecord");
		//mCustomPopDialogFragment.setWindowSize(0.6, 0.6); 
		
		
		mCustomPopDialogFragment.setBackButton("记录",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// 关闭对话框
						mCustomPopDialogFragment.dismiss();
					}
				});
		mCustomPopDialogFragment.setConfirmButton("删除",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) { 
						 
						mCustomPopDialogFragment.dismiss();
					}
				});
		
	} 
	
}
