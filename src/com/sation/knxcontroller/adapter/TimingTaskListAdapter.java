package com.sation.knxcontroller.adapter;

import java.util.List;

import com.sation.knxcontroller.R;
import com.sation.knxcontroller.control.TimingTaskItem;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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

	@SuppressLint("InflateParams")
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
		
		if(null != item.getName()) {
			holder.title.setText(item.getName());
		}
		
		if(item.isMonthlySelected()) {
//			holder.title.setText(item.getTimeWithoutSecond());
//			holder.subtitle.setText(mContext.getResources().getString(R.string.monthly) +item.getDay()+ 
//					mContext.getResources().getString(R.string.day));
			holder.subtitle.setText(String.format("%s%s%s %s", mContext.getResources().getString(R.string.monthly),
					item.getDay(), mContext.getResources().getString(R.string.day), item.getTimeWithoutSecond()));
		} else if(item.isWeeklySelected()) {
//			holder.title.setText(item.getTimeWithoutSecond());
//			holder.subtitle.setText(item.getWeekly(mContext));
			holder.subtitle.setText(String.format("%s %s", item.getWeekly(mContext), item.getTimeWithoutSecond()));
		} else if(item.isEverydaySelected()) {
//			holder.title.setText(item.getTimeWithoutSecond());
//			holder.subtitle.setText(mContext.getResources().getString(R.string.everyday));
			holder.subtitle.setText(String.format("%s %s", mContext.getResources().getString(R.string.everyday), item.getTimeWithoutSecond()));
		} else if(item.isLoopSelected()) {
//			holder.title.setText(item.getTimeWithSecond());
//			holder.subtitle.setText(String.format("%s: %d%s%d%s%d%s", mContext.getResources().getString(R.string.cycle_interval), 
//					item.getIntervalHour(), mContext.getResources().getString(R.string.unit_hour), 
//					item.getIntervalMinute(), mContext.getResources().getString(R.string.unit_minute), 
//					item.getIntervalSecond(), mContext.getResources().getString(R.string.second)));
			holder.subtitle.setText(String.format("%s %s: %d%s%d%s%d%s", item.getTimeWithSecond(),
					mContext.getResources().getString(R.string.cycle_interval), 
					item.getIntervalHour(), mContext.getResources().getString(R.string.unit_hour), 
					item.getIntervalMinute(), mContext.getResources().getString(R.string.unit_minute), 
					item.getIntervalSecond(), mContext.getResources().getString(R.string.second)));
		} else if(item.isOneOffSelected()) {
//			holder.title.setText(item.getTimeWithoutSecond());
//			holder.subtitle.setText(item.getDate());
			holder.subtitle.setText(String.format("%s %s", item.getDate(), item.getTimeWithoutSecond()));
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
