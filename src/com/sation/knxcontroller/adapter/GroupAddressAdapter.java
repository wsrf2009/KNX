package com.sation.knxcontroller.adapter;

import java.util.List;

import com.sation.knxcontroller.R;
import com.sation.knxcontroller.models.KNXGroupAddress;
import com.sation.knxcontroller.util.Log;

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
	private static final String TAG = "GroupAddressAdapter";
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

		try {
			if(null == convertView) {
				holder = new ViewHolder();

				convertView = mLayoutInflater.inflate(R.layout.group_address_item_layout, null);

				holder.groupAddressName = (CheckBox)convertView.findViewById(R.id.checkBoxGroupAddress);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder)convertView.getTag();
			}

			KNXGroupAddress addr = addressList.get(position);
			String name = addr.getName();
			String saddr = addr.getStringKnxAddress();
			holder.groupAddressName.setText(name + "-" + saddr);
			holder.groupAddressName.setTag(addr);
			boolean isExsits = false;
			for (KNXGroupAddress selectedAddress : selectedList) {
				if (addr.getId().equals(selectedAddress.getId())) {
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
							KNXGroupAddress currentAddress = (KNXGroupAddress)buttonView.getTag();
							boolean isContains = false;
							KNXGroupAddress sAddr = null;

							if(null == currentAddress) {
								return;
							}

							for(KNXGroupAddress selectedAddress : selectedList) {
								isContains = selectedAddress.getId().equals(currentAddress.getId());
								if(isContains) {
									sAddr = selectedAddress;
									break;
								}
							}

							if (!isChecked && isContains) {
								selectedList.remove(sAddr);
							} else if (isChecked && !isContains) {
								selectedList.add(currentAddress);
							}

//							for(KNXGroupAddress selectedAddress : selectedList) {
//								isContains = selectedAddress.getId().equals(currentAddress.getId());
//								if(!isChecked) { // 移除地址
//									if(isContains) { // 若已选地址列表中已包含该地址，则将该地址从已选地址列表中移除
//										selectedList.remove(selectedAddress);
//										break;
//									}
//								}
//
//								if(isChecked) {
//									if(isContains) {
//										break;
//									}
//								}
//							}
//
//							if (isChecked) { // 添加地址
//								if(!isContains) { // 若已选地址列表中若没有该地址，则将改地址加到已选地址列表中
//									selectedList.add(currentAddress);
//								}
//							}
						}
					});
		}
		catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			return convertView;
		}
	}
	
	public class ViewHolder {
		public CheckBox groupAddressName;
	}

}
