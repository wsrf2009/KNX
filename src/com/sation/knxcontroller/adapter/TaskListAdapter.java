/**
 * 
 */
package com.sation.knxcontroller.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sation.knxcontroller.STKNXControllerConstant;
import com.sation.knxcontroller.control.KNXControlBase;
import com.sation.knxcontroller.models.KNXGroupAddress;
import com.sation.knxcontroller.models.KNXGroupAddressAction;
import com.sation.knxcontroller.util.Log;
import com.sation.knxcontroller.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author wangchunfeng
 *
 */
public class TaskListAdapter extends BaseAdapter {
	private List<KNXGroupAddress> mList = new ArrayList<KNXGroupAddress>();
	private Context mContext;

	/**
	 * 
	 */
	public TaskListAdapter(Context context, Map<Integer, KNXGroupAddress> map) {
		this.mContext = context;

		mList.clear();
		
		for(KNXGroupAddress control : map.values()) {
			if(null != control) {
				mList.add(control);
			}
		}
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		return mList.size();
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		return position;
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		KNXGroupAddress address = mList.get(position);
		LayoutInflater mLayoutInflater = LayoutInflater.from(mContext);
		
//		if((address.getKnxMainNumber() == 1) && (address.getKnxSubNumber() == 1)) {
//			convertView = mLayoutInflater.inflate(R.layout.datapoint_type_1_1, null);
//
//			CheckBox cbAddress = (CheckBox)convertView.findViewById(R.id.checkBoxName);
//			cbAddress.setText(address.getName());
//			Spinner mSpinner = (Spinner)convertView.findViewById(R.id.spinnerActions);
//			// 建立数据源
//			ArrayList<String> list = new ArrayList<String>();
//			
//			// 建立Adapter并且绑定数据源
//			ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_dropdown_item);
//			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//			for(KNXGroupAddressAction action : address.getAddressAction()) {
//				adapter.add(action.getName());
//			}
//			//绑定 Adapter到控件
//			mSpinner .setAdapter(adapter);
//			mSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
//				@Override
//				public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
//					// TODO Auto-generated method stub
//			        Log.i(ZyyKNXConstant.DEBUG, "\narg2:"+arg2);
//				}
//				@Override
//				public void onNothingSelected(AdapterView<?> arg0) {
//					// TODO Auto-generated method stub
//					
//				}
//			});
//			
//			cbAddress.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//
//				@Override
//				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//					
//				}
//				
//			});
//		} else {
			convertView = mLayoutInflater.inflate(R.layout.task_item, null);
			TextView tvAddressName = (TextView)convertView.findViewById(R.id.task_item_title);
			tvAddressName.setText(address.getName());
//		}
		
		return convertView;
	}

}
