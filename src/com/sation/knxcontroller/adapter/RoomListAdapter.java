package com.sation.knxcontroller.adapter;

import java.util.List;

import com.sation.knxcontroller.R;
import com.sation.knxcontroller.STKNXControllerConstant;
import com.sation.knxcontroller.models.KNXRoom;
import com.sation.knxcontroller.util.ImageUtils;
import com.sation.knxcontroller.util.StringUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
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
			String roomIcon = null;
			if(StringUtil.isEmpty(mRoom.getSymbol())) {
				roomIcon = STKNXControllerConstant.ConfigResImgPath+"liveroom_background.jpg";
			} else {
				roomIcon = STKNXControllerConstant.ConfigResImgPath + mRoom.getSymbol();
			}
			Bitmap icon = ImageUtils.getDiskBitmap(roomIcon);
			if(null != icon) {
				holder.room_icon.setBackgroundDrawable(new BitmapDrawable(icon)); 
			} else {
				holder.room_icon.setImageResource(R.drawable.launcher); 
			}
		} else { 
			holder.room_icon.setImageResource(R.drawable.launcher);    
		}  
		holder.room_list_item.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
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
