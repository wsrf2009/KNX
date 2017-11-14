<<<<<<< HEAD
package com.sation.knxcontroller.adapter;

import java.util.List;

import com.sation.knxcontroller.R;
import com.sation.knxcontroller.STKNXControllerConstant;
import com.sation.knxcontroller.models.KNXRoom;
import com.sation.knxcontroller.util.ImageUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RoomListAdapter extends BaseAdapter {
	private final String TAG = "RoomListAdapter";
	
	public List<KNXRoom> list;
	private LayoutInflater inflater;
//	public Handler parentHandler;
	private SharedPreferences settings;
    private Context mContext;
//	private LinearLayout mCurItem;

	public RoomListAdapter(Context context) {
        this.mContext = context;
		this.inflater = LayoutInflater.from(context);
		this.settings = context.getSharedPreferences(
				STKNXControllerConstant.SETTING_FILE, android.content.Context.MODE_PRIVATE);
	}
	
	public RoomListAdapter(Context context, List<KNXRoom> list) {
		this(context);
		
		this.list = list;
	}
	
	public void setRoomList(List<KNXRoom> list) {
		this.list = list;
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

	@SuppressLint("InflateParams")
	@SuppressWarnings("deprecation")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.list_item, null);
			holder.room_name = (TextView) convertView.findViewById(R.id.name);
			holder.room_icon = (ImageView) convertView.findViewById(R.id.icon);
			holder.room_list_item = (LinearLayout) convertView.findViewById(R.id.list_item);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final KNXRoom mRoom = list.get(position);
		if (mRoom == null) {
			return convertView;
		}
		mRoom.TitleFont.configTextPaint(holder.room_name.getPaint());
		holder.room_name.setText(mRoom.getTitle());
		holder.room_name.setTextColor(mRoom.TitleFont.getColor());

		String roomIcon = STKNXControllerConstant.ConfigResImgPath + mRoom.getSymbol();
		Bitmap icon = ImageUtils.getDiskBitmap(roomIcon);
		if(null != icon) {
			holder.room_icon.setBackgroundDrawable(new BitmapDrawable(icon)); 
		} else {
			holder.room_icon.setImageResource(R.mipmap.launcher);
		}

//		holder.room_list_item.setBackgroundColor(Color.BLUE);
		holder.room_list_item.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//		holder.room_list_item.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//				if(null != mCurItem) {
//					mCurItem.setBackgroundColor(Color.TRANSPARENT);
//				}
//				holder.room_list_item.setBackgroundColor(mContext.getResources().getColor(R.color.area_item_background));
//				mCurItem = holder.room_list_item;

				final Animation zoomOut = AnimationUtils.loadAnimation(mContext, R.anim.scale_zoom_out_100);
				holder.room_icon.startAnimation(zoomOut);
				zoomOut.setAnimationListener(new Animation.AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {

					}

					@Override
					public void onAnimationEnd(Animation animation) {
						if (onItemActionListener != null) {
							onItemActionListener.onItemClick(mRoom, position);
						}
					}

					@Override
					public void onAnimationRepeat(Animation animation) {

					}
				});
//				AnimationUtil.AnimationAlgorithm(v, event, mContext);
//				Log.i(TAG, "down:" + AnimationUtil.downChildViewId + " up:"+AnimationUtil.upChildViewId);
//				if (AnimationUtil.downChildViewId == AnimationUtil.upChildViewId) {
//					if (onItemActionListener != null) {
//						if(STKNXControllerApp.getInstance().getRememberLastInterface()) {
//							STKNXControllerApp.getInstance().setLastRoomIndex(position);
//
//							new Thread(new Runnable() {
//
//								@Override
//								public void run() {
//									SharedPreferences.Editor editor = settings.edit();
//									editor.putInt(STKNXControllerConstant.LAST_ROOM_INDEX, position);
//									editor.commit();
//								}
//
//							}).start();
//
//						}

//						onItemActionListener.onItemClick(mRoom, position);
//					}
//				}
//				return false;
			}
        });

		holder.room_list_item.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				return false;
			}
		});
		
		return convertView;
	}

	public OnItemActionListener onItemActionListener; 
	public void setOnItemActionListener(OnItemActionListener l) {
		this.onItemActionListener = l;
	}

	public interface OnItemActionListener {
		public void onItemClick(KNXRoom mRoom, int index);
	}

	class ViewHolder {  
		private ImageView room_icon;
		private TextView room_name;
		private LinearLayout room_list_item;
	}
}
=======
package com.sation.knxcontroller.adapter;

import java.util.List;

import com.sation.knxcontroller.R;
import com.sation.knxcontroller.STKNXControllerConstant;
import com.sation.knxcontroller.models.KNXRoom;
import com.sation.knxcontroller.util.ImageUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RoomListAdapter extends BaseAdapter {
	private final String TAG = "RoomListAdapter";
	
	public List<KNXRoom> list;
	private LayoutInflater inflater;
//	public Handler parentHandler;
	private SharedPreferences settings;
    private Context mContext;
//	private LinearLayout mCurItem;

	public RoomListAdapter(Context context) {
        this.mContext = context;
		this.inflater = LayoutInflater.from(context);
		this.settings = context.getSharedPreferences(
				STKNXControllerConstant.SETTING_FILE, android.content.Context.MODE_PRIVATE);
	}
	
	public RoomListAdapter(Context context, List<KNXRoom> list) {
		this(context);
		
		this.list = list;
	}
	
	public void setRoomList(List<KNXRoom> list) {
		this.list = list;
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

	@SuppressLint("InflateParams")
	@SuppressWarnings("deprecation")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.list_item, null);
			holder.room_name = (TextView) convertView.findViewById(R.id.name);
			holder.room_icon = (ImageView) convertView.findViewById(R.id.icon);
			holder.room_list_item = (LinearLayout) convertView.findViewById(R.id.list_item);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final KNXRoom mRoom = list.get(position);
		if (mRoom == null) {
			return convertView;
		}
		mRoom.TitleFont.configTextPaint(holder.room_name.getPaint());
		holder.room_name.setText(mRoom.getTitle());
		holder.room_name.setTextColor(mRoom.TitleFont.getColor());

		String roomIcon = STKNXControllerConstant.ConfigResImgPath + mRoom.getSymbol();
		Bitmap icon = ImageUtils.getDiskBitmap(roomIcon);
		if(null != icon) {
			holder.room_icon.setBackgroundDrawable(new BitmapDrawable(icon)); 
		} else {
			holder.room_icon.setImageResource(R.mipmap.launcher);
		}

//		holder.room_list_item.setBackgroundColor(Color.BLUE);
		holder.room_list_item.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//		holder.room_list_item.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//				if(null != mCurItem) {
//					mCurItem.setBackgroundColor(Color.TRANSPARENT);
//				}
//				holder.room_list_item.setBackgroundColor(mContext.getResources().getColor(R.color.area_item_background));
//				mCurItem = holder.room_list_item;

				final Animation zoomOut = AnimationUtils.loadAnimation(mContext, R.anim.scale_zoom_out_100);
				holder.room_icon.startAnimation(zoomOut);
				zoomOut.setAnimationListener(new Animation.AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {

					}

					@Override
					public void onAnimationEnd(Animation animation) {
						if (onItemActionListener != null) {
							onItemActionListener.onItemClick(mRoom, position);
						}
					}

					@Override
					public void onAnimationRepeat(Animation animation) {

					}
				});
//				AnimationUtil.AnimationAlgorithm(v, event, mContext);
//				Log.i(TAG, "down:" + AnimationUtil.downChildViewId + " up:"+AnimationUtil.upChildViewId);
//				if (AnimationUtil.downChildViewId == AnimationUtil.upChildViewId) {
//					if (onItemActionListener != null) {
//						if(STKNXControllerApp.getInstance().getRememberLastInterface()) {
//							STKNXControllerApp.getInstance().setLastRoomIndex(position);
//
//							new Thread(new Runnable() {
//
//								@Override
//								public void run() {
//									SharedPreferences.Editor editor = settings.edit();
//									editor.putInt(STKNXControllerConstant.LAST_ROOM_INDEX, position);
//									editor.commit();
//								}
//
//							}).start();
//
//						}

//						onItemActionListener.onItemClick(mRoom, position);
//					}
//				}
//				return false;
			}
        });

		holder.room_list_item.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				return false;
			}
		});
		
		return convertView;
	}

	public OnItemActionListener onItemActionListener; 
	public void setOnItemActionListener(OnItemActionListener l) {
		this.onItemActionListener = l;
	}

	public interface OnItemActionListener {
		public void onItemClick(KNXRoom mRoom, int index);
	}

	class ViewHolder {  
		private ImageView room_icon;
		private TextView room_name;
		private LinearLayout room_list_item;
	}
}
>>>>>>> SationCentralControl(10inch)
