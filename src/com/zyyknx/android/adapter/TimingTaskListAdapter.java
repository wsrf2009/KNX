package com.zyyknx.android.adapter;

import java.util.HashMap;
import java.util.List;

import com.zyyknx.android.R;
import com.zyyknx.android.control.TimingTaskItem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TimingTaskListAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private List<TimingTaskItem> timingTaskList;

	public TimingTaskListAdapter(Context context, List<TimingTaskItem> list) {
		this.timingTaskList = list;
		this.mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return timingTaskList.size();
	}

	@Override
	public Object getItem(int position) {
		return timingTaskList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(null == convertView) {
			holder = new ViewHolder();
			
			convertView = mInflater.inflate(R.layout.timing_task_item_layout, null);
			
			holder.time = (TextView)convertView.findViewById(R.id.textViewTime);
			holder.action = (TextView)convertView.findViewById(R.id.textViewAction);
			holder.date = (TextView)convertView.findViewById(R.id.textViewDate);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder)convertView.getTag();
		}
		
		holder.time.setText(timingTaskList.get(position).getTime());
		holder.date.setText(timingTaskList.get(position).getDate());
		holder.action.setText(timingTaskList.get(position).getAction());
		
		return convertView;
	}
	
	public class ViewHolder {
		public TextView time;
		public TextView action;
		public TextView date;
	}

}
