package com.zyyknx.android.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler; 
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener; 
import android.widget.BaseAdapter; 
import android.widget.ImageView; 
import android.widget.LinearLayout; 
import android.widget.TextView;
 
import com.zyyknx.android.R;
import com.zyyknx.android.models.KNXRoom;
import com.zyyknx.android.util.ImageUtils;
import com.zyyknx.android.util.StringUtil;

public class RoomListAdapter extends BaseAdapter {

	@SuppressWarnings("unused")
	private Context context;
	
	public List<KNXRoom> list;
	private LayoutInflater inflater;
	public Handler parentHandler;

	public RoomListAdapter(Context context, List<KNXRoom> list) {
		this.context = context;
		this.list = list;
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
			convertView = inflater.inflate(R.layout.room_list_item, null); 
			holder.room_name = (TextView) convertView.findViewById(R.id.room_name); 
			holder.room_icon = (ImageView) convertView.findViewById(R.id.room_icon); 
			holder.room_list_item = (LinearLayout) convertView.findViewById(R.id.room_list_item); 

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final KNXRoom mRoom = list.get(position);
		if (mRoom == null) {
			return convertView;
		}
  
		holder.room_name.setText(mRoom.getText()); 
		if (!StringUtil.isEmpty(mRoom.getSymbol())) {
			//holder.room_icon.setImageBitmap(ImageUtils.getDiskBitmap(mRoom.getSymbol()));
			holder.room_icon.setBackgroundDrawable(new BitmapDrawable(ImageUtils.getDiskBitmap(mRoom.getSymbol()))); 
//			holder.room_icon.setImageBitmap(BitmapFactory.decodeFile(pathName));
		} else { 
			holder.room_icon.setImageResource(R.drawable.launcher);    
		}  
		holder.room_list_item.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (onItemActionListener != null) {
					onItemActionListener.onItemClick(mRoom);
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
		public void onItemClick(KNXRoom mRoom); 
	}

	class ViewHolder {  
		private ImageView room_icon;
		private TextView room_name;
		private LinearLayout room_list_item;
	}
}
