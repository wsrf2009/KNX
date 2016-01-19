package com.zyyknx.android.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.zyyknx.android.R;
import com.zyyknx.android.ZyyKNXApp;
import com.zyyknx.android.ZyyKNXConstant;
import com.zyyknx.android.control.TimingTaskItem;
import com.zyyknx.android.util.Log;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TimingTaskListAdapter extends BaseAdapter {
	private Context mContext;
	private LayoutInflater mInflater;
	private List<TimingTaskItem> timingTaskList;
	private int selectedPosition = -1;// 选中的位置  

	public TimingTaskListAdapter(Context context, List<TimingTaskItem> list) {
		mContext = context;
		this.timingTaskList = list;
		this.mInflater = LayoutInflater.from(context);
	}
	
	public void addTimingTask(TimingTaskItem task) {
		this.timingTaskList.add(0, task);
	}
	
	public void removeTimingTask(TimingTaskItem task) {
		this.timingTaskList.remove(task);
	}
	
	public List<TimingTaskItem> getTimingTaskList() {
		return this.timingTaskList;
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
	
	public void setSelectedPosition(int position) {  
        selectedPosition = position;  
    }  

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TimingTaskItem item = timingTaskList.get(position);
		ViewHolder holder;
		if(null == convertView) {
			holder = new ViewHolder();
			
			convertView = mInflater.inflate(R.layout.listview_item_layout_subtitle_disclosure_indicator, null);
			
			holder.title = (TextView)convertView.findViewById(R.id.listviewItemLayoutSubtitleDisclosureIndicatorTitle);
			holder.subtitle = (TextView)convertView.findViewById(R.id.listviewItemLayoutSubtitleDisclosureIndicatorSubtitle);
			holder.background = (RelativeLayout)convertView.findViewById(R.id.listviewItemLayoutSubtitleDisclosureIndicator);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder)convertView.getTag();
		}
		
		if(item.isMonthlySelected()) {
			holder.title.setText(item.getTimeWithoutSecond());
			holder.subtitle.setText(mContext.getResources().getString(R.string.monthly) +item.getDay()+ 
					mContext.getResources().getString(R.string.day));
		} else if(item.isWeeklySelected()) {
			holder.title.setText(item.getTimeWithoutSecond());
			holder.subtitle.setText(item.getWeekly(mContext));
		} else if(item.isEverydaySelected()) {
			holder.title.setText(item.getTimeWithoutSecond());
			holder.subtitle.setText(mContext.getResources().getString(R.string.everyday));
		} else if(item.isLoopSelected()) {
			holder.title.setText(item.getTimeWithSecond());
			holder.subtitle.setText(String.format("%s: %d%s%d%s%d%s", mContext.getResources().getString(R.string.cycle_interval), 
					item.getIntervalHour(), mContext.getResources().getString(R.string.unit_hour), 
					item.getIntervalMinute(), mContext.getResources().getString(R.string.unit_minute), 
					item.getIntervalSecond(), mContext.getResources().getString(R.string.second)));
		} else if(item.isOneOffSelected()) {
			holder.title.setText(item.getTimeWithoutSecond());
			holder.subtitle.setText(item.getDate());
		}
		
		if (selectedPosition == position) {  
			holder.title.setTextColor(Color.GREEN);
			holder.subtitle.setTextColor(Color.GREEN);
			holder.background.setBackgroundColor(0xFF2A91CF);  
        } else {  
        	holder.title.setTextColor(Color.BLACK); 
        	holder.subtitle.setTextColor(Color.BLACK); 
        	holder.background.setBackgroundColor(Color.TRANSPARENT);     

        }  
		
		return convertView;
	}
	
	public class ViewHolder {
		public TextView title;
		public TextView subtitle;
		public RelativeLayout background;
	}

}
