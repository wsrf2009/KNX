/**
 * 
 */
package com.zyyknx.android.adapter;

import java.util.ArrayList;
import java.util.List;

import com.zyyknx.android.R;
import com.zyyknx.android.adapter.GroupAddressAdapter.ViewHolder;
import com.zyyknx.android.control.TimingTaskItem.KNXGroupAddressAndAction;
import com.zyyknx.android.models.KNXGroupAddress;
import com.zyyknx.android.models.KNXGroupAddressAction;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

/**
 * @author wangchunfeng
 *
 */
public class AddressAndActionAdapter extends BaseAdapter {
	private List<KNXGroupAddressAndAction> addressAndActionList;
	private Context mContext;

	/**
	 * 
	 */
	public AddressAndActionAdapter(Context context/*, List<KNXGroupAddressAndAction> list*/) {
//		this.addressList = list;
//		this.addressAndActionList = new ArrayList<KNXGroupAddressAndAction>();
		this.mContext = context;
	}
	
	public void setAddressAndActionList(List<KNXGroupAddressAndAction> list) {
		this.addressAndActionList = list;
	}
	
	public void clearAddressAndActionList() {
		this.addressAndActionList.clear();
	}
	
	public void addAddressAndAction(KNXGroupAddressAndAction addressAndAction) {
		addressAndActionList.add(addressAndAction);
	}
	
	public List<KNXGroupAddressAndAction> getAddressAndActionList() {
		return this.addressAndActionList;
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		if(null != this.addressAndActionList) {
			return this.addressAndActionList.size();
		} else {
			return 0;
		}
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {
		return this.addressAndActionList.get(position);
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
		LayoutInflater mLayoutInflater = LayoutInflater.from(mContext);
		ViewHolder holder;
		if(null == convertView) {
			holder = new ViewHolder();
			
			convertView = mLayoutInflater.inflate(R.layout.listview_item_layout_detail, null);
			
			holder.textViewAddress = (TextView)convertView.findViewById(R.id.listviewItemLayoutDetailTitle);
			holder.textViewAction = (TextView)convertView.findViewById(R.id.listviewItemLayoutDetailDetails);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder)convertView.getTag();
		}
		
		KNXGroupAddressAndAction addressAndAction = this.addressAndActionList.get(position);
		holder.textViewAddress.setText(addressAndAction.getAddress().getName());
		KNXGroupAddressAction action = addressAndAction.getAction();
		String actionText = mContext.getResources().getString(R.string.execute_action)+":  ";
		String actionContent = "";
		if(addressAndAction.getIsRead()) {
			actionContent = mContext.getResources().getString(R.string.get_status);
		} else if(null != action) {
			actionContent = action.getName();
		} else {
			actionContent = mContext.getResources().getString(R.string.nothing);
		}
		
		holder.textViewAction.setText(String.format("%s:  %s", actionText, actionContent));
		
//		holder.textViewAddress.setTag(addressList.get(position));

		return convertView;
	}
	
	public class ViewHolder {
		TextView textViewAddress;
		TextView textViewAction;
	}

}
