/**
 * 
 */
package com.zyyknx.android.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.zyyknx.android.R;
import com.zyyknx.android.control.KNXControlBase;
import com.zyyknx.android.models.KNXGroupAddress;

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
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater mLayoutInflater = LayoutInflater.from(mContext);
		convertView = mLayoutInflater.inflate(R.layout.task_item, null);
		if (null != convertView) {
			TextView mTextView = (TextView)convertView.findViewById(R.id.task_item_title);
			mTextView.setText(mList.get(position).getName());
		}
		
		return convertView;
	}

}
