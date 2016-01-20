package com.zyyknx.android.adapter;

import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.zyyknx.android.R; 
import com.zyyknx.android.control.KNXControlBase; 

public class ScenneListAdapter extends BaseAdapter {

	@SuppressWarnings("unused")
	private Context context;
	
	public List<KNXControlBase> list;
	private LayoutInflater inflater;
	public Handler parentHandler;

	public ScenneListAdapter(Context context, List<KNXControlBase> mKNXControlBaseList) {
		this.context = context;
		this.list = mKNXControlBaseList;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.scene_control_item, null); 
			holder.room_select = (CheckBox) convertView.findViewById(R.id.list_select); 
			holder.room_name = (TextView) convertView.findViewById(R.id.list_name); 
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final KNXControlBase mKNXControlBase = list.get(position);
		if (mKNXControlBase == null) {
			return convertView;
		}
  
		holder.room_name.setText(mKNXControlBase.getText());
		holder.room_name.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (onItemActionListener != null) {
					onItemActionListener.onItemClick(mKNXControlBase);
				}
			}
		});
		return convertView;
	}

	public OnItemActionListener onItemActionListener; 
	public void setOnItemActionListener(OnItemActionListener l) {
		this.onItemActionListener = l;
	}

	public interface OnItemActionListener {
		public void onItemClick(KNXControlBase mKNXControlBase); 
	}

	class ViewHolder {  
		private CheckBox room_select;
		private TextView room_name; 
	}
}
