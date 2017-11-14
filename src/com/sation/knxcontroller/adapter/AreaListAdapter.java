package com.sation.knxcontroller.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sation.knxcontroller.R;
import com.sation.knxcontroller.STKNXControllerConstant;
import com.sation.knxcontroller.models.KNXArea;
import com.sation.knxcontroller.util.ImageUtils;

import java.util.List;

public class AreaListAdapter extends BaseAdapter {
	private final String TAG = "AreaListAdapter";

	public List<KNXArea> list;
	private LayoutInflater inflater;
	public Handler parentHandler;
	private SharedPreferences settings;
    private Context mContext;
	private ViewHolder mCurItem;
	private int defaultIndex = -1;
//	private int itemWidth;

	public AreaListAdapter(Context context) {
        this.mContext = context;
		this.inflater = LayoutInflater.from(context);
		this.settings = context.getSharedPreferences(
				STKNXControllerConstant.SETTING_FILE, Context.MODE_PRIVATE);
	}

	public AreaListAdapter(Context context, List<KNXArea> list) {
		this(context);
		
		this.list = list;
	}
	
	public void setAreaList(List<KNXArea> list) {
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

	public void setDefaultIndex(int index) {
		this.defaultIndex = index;
	}
//	public int getItemWidth() {
//		return this.itemWidth;
//	}

	@SuppressLint("InflateParams")
	@SuppressWarnings("deprecation")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.list_item, null);
			holder.area_name = (TextView) convertView.findViewById(R.id.name);
			holder.area_icon = (ImageView) convertView.findViewById(R.id.icon);
			holder.area_list_item = (LinearLayout) convertView.findViewById(R.id.list_item);

			if((position == defaultIndex) && (null == mCurItem)) {
//				holder.area_list_item.setBackgroundColor(mContext.getResources().getColor(R.color.area_item_background));
				holder.area_list_item.setBackground(mContext.getResources().getDrawable(R.drawable.area_list_item_background));
//				mCurItem = holder.area_list_item;
				mCurItem = holder;
//				Log.i(TAG, "mCurItem:"+mCurItem+" holder:"+holder);
			}

			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					mContext.getResources().getDimensionPixelOffset(R.dimen.area_item_icon_width),
					mContext.getResources().getDimensionPixelOffset(R.dimen.area_item_icon_height));
//			lp.setMargins(30, 30, 30, 30);
			holder.area_icon.setLayoutParams(lp);

//			AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(
//					AbsListView.LayoutParams.WRAP_CONTENT, AbsListView.LayoutParams.WRAP_CONTENT);
//			convertView.setLayoutParams(layoutParams);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final KNXArea mArea = list.get(position);
		if (mArea == null) {
			return convertView;
		}
		mArea.TitleFont.configTextPaint(holder.area_name.getPaint());
		holder.area_name.setText(mArea.getTitle());
		holder.area_name.setTextColor(mArea.TitleFont.getColor());

		String roomIcon = STKNXControllerConstant.ConfigResImgPath + mArea.getSymbol();
		Bitmap icon = ImageUtils.getDiskBitmap(roomIcon);
		if(null != icon) {
			holder.area_icon.setBackgroundDrawable(new BitmapDrawable(icon));
		} else {
			holder.area_icon.setImageResource(R.mipmap.launcher);
		}

//		holder.area_list_item.setBackgroundColor(Color.BLUE);
		holder.area_list_item.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//		holder.area_list_item.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//				Log.i(TAG, "mCurItem:"+mCurItem+" holder:"+holder);
				if(null != mCurItem) {
					mCurItem.area_list_item.setBackgroundColor(Color.TRANSPARENT);
				}
//				holder.area_list_item.setBackgroundColor(mContext.getResources().getColor(R.color.area_item_background));
				holder.area_list_item.setBackground(mContext.getResources().getDrawable(R.drawable.area_list_item_background));
				mCurItem = holder;

				final Animation zoomOut = AnimationUtils.loadAnimation(mContext, R.anim.scale_zoom_out_100);
				holder.area_icon.startAnimation(zoomOut);


//				AnimationUtil.AnimationAlgorithm(v, event, mContext);
//				if (AnimationUtil.downChildViewId == AnimationUtil.upChildViewId) {
					if (onItemActionListener != null) {
//						if(STKNXControllerApp.getInstance().getRememberLastInterface()) {
//							STKNXControllerApp.getInstance().setmLastAreaIndex(position);
//
//							new Thread(new Runnable() {
//
//								@Override
//								public void run() {
//									SharedPreferences.Editor editor = settings.edit();
//									editor.putInt(STKNXControllerConstant.LAST_AREA_INDEX, position);
//									editor.commit();
//								}
//
//							}).start();
//
//						}

						onItemActionListener.onItemClick(mArea, position);
					}
//				}
//				return false;
			}
        });

		holder.area_list_item.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				return false;
			}
		});

//		itemWidth = convertView.getWidth();
		return convertView;
	}

	public OnItemActionListener onItemActionListener; 
	public void setOnItemActionListener(OnItemActionListener l) {
		this.onItemActionListener = l;
	}

	public interface OnItemActionListener {
		public void onItemClick(KNXArea mRoom, int index);
	}

	class ViewHolder {  
		private ImageView area_icon;
		private TextView area_name;
		private LinearLayout area_list_item;
	}
}
