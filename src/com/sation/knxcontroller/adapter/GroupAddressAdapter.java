package com.sation.knxcontroller.adapter;

import java.util.List;

import com.sation.knxcontroller.R;
import com.sation.knxcontroller.models.KNXGroupAddress;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

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

	@SuppressLint("InflateParams")
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
		
		holder.groupAddressName.setText(addressList.get(position).getName()+"-"+addressList.get(position).getStringKnxAddress());
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
		.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				boolean isContains = false;
				KNXGroupAddress currentAddress = (KNXGroupAddress)buttonView.getTag();
				for(KNXGroupAddress selectedAddress : selectedList) {
					isContains = selectedAddress.getId().equals(currentAddress.getId());
					if(!isChecked) {
						/* 移除地址 */
						if(isContains) { // 若已选地址列表中已包含改地址，则将改地址从已选地址列表中移除
							selectedList.remove(selectedAddress);
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
