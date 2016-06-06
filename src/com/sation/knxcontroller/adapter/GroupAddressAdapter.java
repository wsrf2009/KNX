package com.sation.knxcontroller.adapter;

import java.util.ArrayList;
import java.util.List;

import com.sation.knxcontroller.STKNXControllerConstant;
import com.sation.knxcontroller.adapter.TimingTaskListAdapter.ViewHolder;
import com.sation.knxcontroller.control.TimingTaskItem;
import com.sation.knxcontroller.control.TimingTaskItem.KNXGroupAddressAndAction;
import com.sation.knxcontroller.models.KNXGroupAddress;
import com.sation.knxcontroller.R;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class GroupAddressAdapter extends BaseAdapter {
	private List<KNXGroupAddress> addressList;
	private Context mContext;
	private List<KNXGroupAddress> selectedList;

	public GroupAddressAdapter(Context context, List<KNXGroupAddress> list, List<KNXGroupAddress> selectedList) {
		this.addressList = list;
		this.mContext = context;
		this.selectedList = selectedList;
	}

	@Override
	public int getCount() {
		return addressList.size();
	}

	@Override
	public Object getItem(int position) {
		return addressList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public List<KNXGroupAddress> getSelectedAddress(){
		return this.selectedList;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater mLayoutInflater = LayoutInflater.from(mContext);
		ViewHolder holder;
		if(null == convertView) {
			holder = new ViewHolder();
			
			convertView = mLayoutInflater.inflate(R.layout.group_address_item_layout, null);
			
			holder.groupAddressName = (CheckBox)convertView.findViewById(R.id.checkBoxGroupAddress);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder)convertView.getTag();
		}
		
		holder.groupAddressName.setText(addressList.get(position).getName()/*+"-"+addressList.get(position).getId()*/);
		holder.groupAddressName.setTag(addressList.get(position));
		boolean isExsits = false;
		for(KNXGroupAddress selectedAddress : selectedList) {
			if(addressList.get(position).getId().equals(selectedAddress.getId())) {
				isExsits = true;
				
				break;
			}
		}
		if(isExsits) {
			holder.groupAddressName.setChecked(true);
		} else {
			holder.groupAddressName.setChecked(false);
		}
		
		holder.groupAddressName
//		.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				boolean isContains = false;
//				KNXGroupAddress currentAddress = (KNXGroupAddress)buttonView.getTag();
//				Log.i(ZyyKNXConstant.DEBUG, "name :"+ currentAddress.getName()+" isChecked:"+isChecked);
//				for(KNXGroupAddress selectedAddress : selectedList) {
//					isContains = selectedAddress.getId().equals(currentAddress.getId());
//					if(!isChecked) {
//						/* 移除地址 */
//						if(isContains) { // 若已选地址列表中已包含改地址，则将改地址从已选地址列表中移除
//							selectedList.remove(selectedAddress);
//							
//							break;
//						}
//					}
//				}
//
//				if (isChecked) {
//					/* 添加地址 */
//					if(!isContains) { // 若已选地址列表中若没有该地址，则将改地址加到已选地址列表中
//						selectedList.add(currentAddress);
//					}
//				} 
//			}
//		});
		.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				boolean isContains = false;
				KNXGroupAddress currentAddress = (KNXGroupAddress)buttonView.getTag();
				Log.i(STKNXControllerConstant.DEBUG, "\nname :"+ currentAddress.getName()+" isChecked:"+isChecked);
				for(KNXGroupAddress selectedAddress : selectedList) {
					isContains = selectedAddress.getId().equals(currentAddress.getId());
					if(!isChecked) {
						/* 移除地址 */
						if(isContains) { // 若已选地址列表中已包含改地址，则将改地址从已选地址列表中移除
							selectedList.remove(selectedAddress);
							Log.i(STKNXControllerConstant.DEBUG, " 移除组地址："+selectedAddress.getName()+"-"+selectedAddress.getId());
							break;
						}
					}
					
					if(isChecked) {
						if(isContains) {
							break;
						}
					}
				}

				if (isChecked) {
					/* 添加地址 */
					if(!isContains) { // 若已选地址列表中若没有该地址，则将改地址加到已选地址列表中
						selectedList.add(currentAddress);
						Log.i(STKNXControllerConstant.DEBUG, " 添加组地址："+currentAddress.getName()+"-"+currentAddress.getId());
					}
				} 
			}
		});

		
		return convertView;
	}
	
	public class ViewHolder {
		public CheckBox groupAddressName;
	}

}