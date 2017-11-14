/**
 * 
 */
package com.sation.knxcontroller.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sation.knxcontroller.R;
import com.sation.knxcontroller.models.KNXGroupAddress;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

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
	@SuppressLint({ "ViewHolder", "InflateParams" })
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		KNXGroupAddress address = mList.get(position);
		LayoutInflater mLayoutInflater = LayoutInflater.from(mContext);

		convertView = mLayoutInflater.inflate(R.layout.task_item, null);
		TextView tvAddressName = (TextView)convertView.findViewById(R.id.task_item_title);
		tvAddressName.setText(address.getName());
		
		return convertView;
	}

}
