/**
 * 
 */
package com.sation.knxcontroller.adapter;

import java.util.List;

import com.sation.knxcontroller.models.KNXGroupAddressAction;
import com.sation.knxcontroller.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

/**
 * @author wangchunfeng
 *
 */
public class AddressActionsAdapter extends BaseAdapter {
	private List<KNXGroupAddressAction> actionList;
	private Context mContext;

	/**
	 * 
	 */
	public AddressActionsAdapter(Context context, List<KNXGroupAddressAction> list) {
		this.mContext = context;
		this.actionList = list;
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		return actionList.size();
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {
		return actionList.get(position);
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
		if (null == convertView) {
			holder = new ViewHolder();
			
			convertView = mLayoutInflater.inflate(R.layout.action_item_layout, null);
			
			holder.tvActionName = (TextView)convertView.findViewById(R.id.textViewActionItemActionName);
			holder.rbSelected = (RadioButton)convertView.findViewById(R.id.radioButtonActionIsSelected);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder)convertView.getTag();
		}
		
		holder.tvActionName.setText(actionList.get(position).getName());
		
		
		return convertView;
	}
	
	public class ViewHolder {
		TextView tvActionName;
		RadioButton rbSelected;
	}
}
