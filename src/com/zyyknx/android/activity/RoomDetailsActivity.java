package com.zyyknx.android.activity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.GridLayout.Spec;
import android.support.v7.widget.Space;
import android.util.DisplayMetrics;
import android.util.Log; 
import android.view.Gravity;
import android.view.View; 
import android.view.ViewGroup;  
import android.widget.FrameLayout;
import android.widget.LinearLayout; 
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
   
import com.zyyknx.android.R;
import com.zyyknx.android.ZyyKNXApp;
import com.zyyknx.android.ZyyKNXConstant; 
import com.zyyknx.android.control.*;
import com.zyyknx.android.models.*;
import com.zyyknx.android.util.ByteUtil;
import com.zyyknx.android.util.ImageUtils;
import com.zyyknx.android.util.KNX0X01Lib;
import com.zyyknx.android.widget.*;
import com.zyyknx.android.widget.MyBlinds.OnBlindsListener;

public class RoomDetailsActivity extends BaseActivity implements OnBlindsListener {
	
	int screenWidth = 0;
	int screenHeight = 0; 
	//当前页面所有控件集合
	List<KNXControlBase> currentPageKNXControlBase = new ArrayList<KNXControlBase>();
	Map<String, KNXControlBase> currentPageKNXControlBaseMap = new HashMap<String, KNXControlBase>(); 
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		setContentView(R.layout.room_details_layout);
		
		IntentFilter intentFilter = new IntentFilter();
		//设备状态的广播
		intentFilter.addAction(ZyyKNXConstant.BROADCAST_UPDATE_DEVICE_STATUS); 
		registerReceiver(updateDeviceStateReceiver, intentFilter);

		// 获取参数
		KNXRoom mKNXRoom = (KNXRoom) getIntent().getSerializableExtra(ZyyKNXConstant.REMOTE_PARAM_KEY);

		 
		LinearLayout layContent = (LinearLayout) findViewById(R.id.layContent); // 获取LinearLayout控件
		 
		
		final GridLayout rootGridLayout = (GridLayout) findViewById(R.id.gridLayout); // 获取GridLayout控件  
		int contentViewTop = getBarHeight();  //获取状态栏高度
		
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		screenWidth = dm.widthPixels + 10;    //屏幕宽度
		screenHeight = dm.heightPixels;  // - contentViewTop - txtTitle.getHeight() - txtTitle.getPaddingTop() * 2 - 30; 
		
		System.out.println(String.format("screenWidth:%d screenHeight:%d", screenWidth, screenHeight));;
		
		//行高
		int rowHeight = 0;
		int rowWidth = 0; 

		if (mKNXRoom.getPages() != null && mKNXRoom.getPages().size() > 0 ) { 
			rowHeight = (int) (screenHeight / mKNXRoom.getPages().get(0).getRowCount());
			rowWidth= (int) (screenWidth / mKNXRoom.getPages().get(0).getColumnCount());  
			
			System.out.println(String.format("rowWidth:%d rowHeight:%d", rowWidth, rowHeight));;
			
			layContent.setBackgroundDrawable(new BitmapDrawable(ImageUtils.getDiskBitmap("zyyknxandroid/.nomedia/res/img/" + mKNXRoom.getPages().get(0).getBackgroudImage())));
			
			setGridLayoutAllSpace(rootGridLayout,mKNXRoom.getPages().get(0).getRowCount(),mKNXRoom.getPages().get(0).getColumnCount(), rowHeight, rowWidth, false); 
			
			for (int i = 0; i < mKNXRoom.getPages().size(); i++) {

			   KNXPage mKNXPage = mKNXRoom.getPages().get(i);
			   //增加页面级别的控件
			   for (int x = 0; x < mKNXPage.getControls().size(); x++) {
					KNXControlBase mKNXControlBase = mKNXPage.getControls().get(x);
					//增加到当前页面所有控件集合
					currentPageKNXControlBase.add(mKNXControlBase);
					if(mKNXControlBase.getReadAddressId() != null && mKNXControlBase.getReadAddressId().size() > 0) {
						currentPageKNXControlBaseMap.put(getFirstOrNull(mKNXControlBase.getReadAddressId()).getId(), mKNXControlBase);
					}
					
					 
					Spec rowSpecPageControl = GridLayout.spec(mKNXControlBase.getRow(), mKNXControlBase.getRowSpan()); 
					Spec columnSpecPageControl = GridLayout.spec(mKNXControlBase.getColumn(), mKNXControlBase.getColumnSpan()); 
					GridLayout.LayoutParams pageControlGridLayoutParams = new GridLayout.LayoutParams(rowSpecPageControl, columnSpecPageControl);
					pageControlGridLayoutParams.height = rowHeight * mKNXControlBase.getRowSpan();
					pageControlGridLayoutParams.width = rowWidth * mKNXControlBase.getColumnSpan();
					pageControlGridLayoutParams.leftMargin = getResources().getInteger(R.integer.margin_xlarge);
					pageControlGridLayoutParams.setGravity(Gravity.CENTER_VERTICAL);
					
					//生成控件
					ComponentView componentView = KNXControlBase.buildWithControl(this, mKNXControlBase);
					if (componentView != null) {
						LayoutParams pageLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT); 
						//componentView.setGravity(Gravity.CENTER_VERTICAL);
						pageLayoutParams.height = rowHeight * mKNXControlBase.getRowSpan();
						pageLayoutParams.width = rowWidth * mKNXControlBase.getColumnSpan();
						//pageLayoutParams.leftMargin = R.dimen.margin_large;
						componentView.setLayoutParams(pageLayoutParams);
						rootGridLayout.addView(componentView, pageControlGridLayoutParams);
			        }
					 
			   } 
				
				List<KNXGrid> mKNXGridList = mKNXPage.getGrids();  
				for (int j = 0; j < mKNXGridList.size(); j++) {
					KNXGrid mKNXGrid = mKNXGridList.get(j); 
			        
			        //每个分组的名字
					FrameLayout frameLayoutGroup = new FrameLayout(this, null, R.attr.styleGroupItem);
					LinearLayout.LayoutParams lpGroup = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);  
					lpGroup.height = rowHeight * mKNXGrid.getRowSpan(); 
					lpGroup.width = rowWidth * mKNXGrid.getColumnSpan();
					lpGroup.leftMargin = getResources().getInteger(R.integer.margin_xlarge);
					frameLayoutGroup.setLayoutParams(lpGroup);//设置布局参数   
					//frameLayoutGroup.setBackgroundResource(R.drawable.group_bg); 
					frameLayoutGroup.setBackgroundResource(R.drawable.shixun_group_bg);
					 
				    
				    Spec rowSpecGrid  = GridLayout.spec(mKNXGrid.getRow(), mKNXGrid.getRowSpan()); 
					Spec columnSpecGrid = GridLayout.spec(mKNXGrid.getColumn(), mKNXGrid.getColumnSpan());  
					 
					GridLayout.LayoutParams groupGridLayoutParams = new GridLayout.LayoutParams(rowSpecGrid, columnSpecGrid);
					groupGridLayoutParams.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
					groupGridLayoutParams.width = rowWidth * mKNXGrid.getColumnSpan() - 2 * getResources().getInteger(R.integer.margin_xlarge);
					groupGridLayoutParams.height = rowHeight * mKNXGrid.getRowSpan()- 2 * getResources().getInteger(R.integer.margin_xlarge);
					rootGridLayout.addView(frameLayoutGroup, groupGridLayoutParams);
				    
					//每个页面子Grid的高度
					int pageGridHeight = rowHeight * mKNXGrid.getRowSpan()- 2 * getResources().getInteger(R.integer.margin_xlarge);
					int pageGridWidth = rowWidth * mKNXGrid.getColumnSpan() - 2 * getResources().getInteger(R.integer.margin_xlarge) - 20;
					//每个页面子Grid的行高和行宽
					int controlGridRowHeight = pageGridHeight / mKNXGrid.getRowCount();
					int controlGridRowWidth = pageGridWidth  / mKNXGrid.getColumnCount();
					
					
					// 动态创建GridLayout
					GridLayout mGridLayoutGroup = new GridLayout(this);  
					mGridLayoutGroup.setRowCount(mKNXGrid.getRowCount());
					mGridLayoutGroup.setColumnCount(mKNXGrid.getColumnCount());
					LayoutParams groupLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT); 
					groupLayoutParams.height = pageGridHeight;
					groupLayoutParams.width = pageGridWidth;
					mGridLayoutGroup.setLayoutParams(groupLayoutParams);
					
					setGridLayoutAllSpace(mGridLayoutGroup, mKNXGrid.getRowCount(), mKNXGrid.getColumnCount(), controlGridRowHeight, controlGridRowWidth, false); 
					
					ViewGroup.LayoutParams vlpGroup = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT); 
					mGridLayoutGroup.setLayoutParams(vlpGroup);
					frameLayoutGroup.addView(mGridLayoutGroup);

					//Log.d("Test",String.format("%s的位置：row: %d;Column: %d;RowSpan: %d;ColumnSpan: %d", mKNXGrid.getText(),  mKNXGrid.getRow(), mKNXGrid.getColumn(), mKNXGrid.getRowSpan(), mKNXGrid.getColumnSpan()));
					 //增加Grid级别的控件
					for (int k = 0; k < mKNXGrid.getControls().size(); k++) {
						KNXControlBase mKNXControlBase = mKNXGrid.getControls().get(k); 
						//当前页面所有控件集合
						currentPageKNXControlBase.add(mKNXControlBase);
						if(mKNXControlBase.getReadAddressId() != null && mKNXControlBase.getReadAddressId().size() > 0) {
							currentPageKNXControlBaseMap.put(getFirstOrNull(mKNXControlBase.getReadAddressId()).getId(), mKNXControlBase);
						}

						Spec rowSpecControl = GridLayout.spec(mKNXControlBase.getRow(), mKNXControlBase.getRowSpan()); 
						Spec columnSpecControl = GridLayout.spec(mKNXControlBase.getColumn(), mKNXControlBase.getColumnSpan());
						 

						GridLayout.LayoutParams controlGridLayoutParams = new GridLayout.LayoutParams(rowSpecControl, columnSpecControl);
						controlGridLayoutParams.setGravity(Gravity.FILL_HORIZONTAL | Gravity.FILL_VERTICAL | Gravity.CENTER);  
						controlGridLayoutParams.height = controlGridRowHeight * mKNXControlBase.getRowSpan();
						controlGridLayoutParams.width = controlGridRowHeight * mKNXControlBase.getColumnSpan() - 2 * getResources().getInteger(R.integer.margin_xlarge);
						
						//Log.d("Test", String.format("%s的位置：row: %d;Column: %d;RowSpan: %d;ColumnSpan: %d", mKNXControlBase.getText(), mKNXControlBase.getRow(), mKNXControlBase.getColumn(), mKNXControlBase.getRowSpan(), mKNXControlBase.getColumnSpan()));

						ComponentView componentView = KNXControlBase.buildWithControl(this, mKNXControlBase);
						if (componentView != null) {
							LayoutParams controlLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT); 
							controlLayoutParams.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL | Gravity.CENTER;  
							componentView.setLayoutParams(controlLayoutParams);
							mGridLayoutGroup.addView(componentView, controlGridLayoutParams); 
							 
				        } 
					}
				}
			}
		}
		//筛选出场景按钮能控制的按钮
		Map<Integer,KNXControlBase> mKNXControls = getSceneButtonControl(currentPageKNXControlBase);
		ZyyKNXApp.getInstance().setCurrentPageKNXControlBaseMap(mKNXControls);
	}
	
	private Map<Integer,KNXControlBase> getSceneButtonControl(List<KNXControlBase> currentPageKNXControlBase) {
		Map<Integer, KNXControlBase> allBaseControl = new HashMap<Integer, KNXControlBase>();
		for (int i = 0; i < currentPageKNXControlBase.size(); i++) {
			
			KNXSelectedAddress mKNXSelectedAddress = getFirstOrNull(currentPageKNXControlBase.get(i).getReadAddressId());
			int currentIndex = 0;
			int currentValue = -1;
			if(mKNXSelectedAddress != null) { 
				String mETSId = mKNXSelectedAddress.getId(); 
				currentIndex = ZyyKNXApp.getInstance().getGroupAddressIndexMap().get(mETSId); 
				
				byte[] contentBytes = new byte[32];
				byte[] length  = new byte[1];
				KNX0X01Lib.UTestAndCopyObject(currentIndex, contentBytes, length);
				currentValue = ByteUtil.getInt(contentBytes);
			} 
			
			if(currentPageKNXControlBase.get(i) instanceof KNXButton) {
				allBaseControl.put(currentPageKNXControlBase.get(i).getId(), currentPageKNXControlBase.get(i)); 
				
			} else if(currentPageKNXControlBase.get(i) instanceof KNXImageButton) {
				allBaseControl.put(currentPageKNXControlBase.get(i).getId(), currentPageKNXControlBase.get(i));
			} else if(currentPageKNXControlBase.get(i) instanceof KNXBlinds) {
				allBaseControl.put(currentPageKNXControlBase.get(i).getId(), currentPageKNXControlBase.get(i));
			} else if(currentPageKNXControlBase.get(i) instanceof KNXSwitch) {
				allBaseControl.put(currentPageKNXControlBase.get(i).getId(), currentPageKNXControlBase.get(i));
				
				if(currentIndex >0) {
					MySwitchButton mMySwitchButton = (MySwitchButton) findViewById(currentPageKNXControlBase.get(i).getId()); // 获取LinearLayout控件 
					mMySwitchButton.setValue(currentValue);
				}
				
			} else if(currentPageKNXControlBase.get(i) instanceof KNXSlider) {
				allBaseControl.put(currentPageKNXControlBase.get(i).getId(), currentPageKNXControlBase.get(i));
			} else if(currentPageKNXControlBase.get(i) instanceof KNXSliderSwitch) {
				allBaseControl.put(currentPageKNXControlBase.get(i).getId(), currentPageKNXControlBase.get(i));
				
				if(currentIndex >0) {
					MySliderSwitch mMySliderSwitch = (MySliderSwitch) findViewById(currentPageKNXControlBase.get(i).getId()); // 获取LinearLayout控件 
					mMySliderSwitch.setProgress(currentValue);
				}
			}  
		}
		return allBaseControl;
	}
	
	
	private void setGridLayoutAllSpace(GridLayout mGridLayout, int rowCount, int columnCount,int rowHeight, int rowWidth, boolean isDebugger) { 
		mGridLayout.setRowCount(rowCount);
		mGridLayout.setColumnCount(columnCount);
		for (int i = 0; i < rowCount; i++) {
			for (int j = 0; j < columnCount; j++) { 
				View mSpace = null;
				//isDebugger = true;
				if(isDebugger) {
					 
				} else {
					mSpace = new Space(this);
				} 
				
				LayoutParams spaceLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);  
				spaceLayoutParams.height = rowHeight; 
				spaceLayoutParams.width = rowWidth;
				mSpace.setLayoutParams(spaceLayoutParams);
				
				Spec rowSpecSpace = GridLayout.spec(i, 1); 
				Spec columnSpecSpace = GridLayout.spec(j, 1); 
				GridLayout.LayoutParams spaceGridLayoutParams = new GridLayout.LayoutParams(rowSpecSpace, columnSpecSpace);
				spaceGridLayoutParams.width = rowWidth;
				spaceGridLayoutParams.height = rowHeight;
				  
				mGridLayout.addView(mSpace, spaceGridLayoutParams);
			}
		}
	}
	
	private BroadcastReceiver updateDeviceStateReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(final Context context, Intent intent)
		{
			// TODO 041291 Auto-generated method stub
			if(intent.getAction().equals(ZyyKNXConstant.BROADCAST_UPDATE_DEVICE_STATUS)){ 
				KNXResponse mKNXResponse = (KNXResponse) intent.getExtras().getSerializable("value");
				Log.d("Test", "当前的值得为："+ mKNXResponse.ControlValue +"");
				try {
					KNXControlBase mKNXControlBase = currentPageKNXControlBaseMap.get(mKNXResponse.getKNXId());
					int controlId = mKNXControlBase.getId(); 
					if(mKNXControlBase instanceof KNXButton) {
						 
					} else if(mKNXControlBase instanceof KNXImageButton) {
						 
					} else if(mKNXControlBase instanceof KNXBlinds) {
						 
					} else if(mKNXControlBase instanceof KNXSwitch) { 
						MySwitchButton mMySwitchButton = (MySwitchButton) findViewById(controlId); // 获取LinearLayout控件 
						mMySwitchButton.setValue(mKNXResponse.getControlValue());
						 
					} else if(mKNXControlBase instanceof KNXSlider) {
						 
					} else if(mKNXControlBase instanceof KNXSliderSwitch) {
						MySliderSwitch mMySliderSwitch = (MySliderSwitch) findViewById(controlId); // 获取LinearLayout控件 
						mMySliderSwitch.setProgress(mKNXResponse.getControlValue());
					}
				} catch (Exception e) { 
					
				}
				 
			}
		}
	};
	
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy(); 
		unregisterReceiver(updateDeviceStateReceiver); 
	}
	
	/** 获取状态栏的高度 */
	private int getBarHeight(){
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, sbar = 38;//默认为38，貌似大部分是这样的
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            sbar = getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return sbar;
    } 

	@Override
	public void onLeftClick(int result) {
		displayToast(result + "");
	}

	@Override
	public void onRightClick(int result) {
		displayToast(result + "");
	}

	@Override
	public void onLeftLongClick(int result) {
		displayToast(result + "");
	}

	@Override
	public void onRightLongClick(int result) {
		displayToast(result + "");
	}
	
	/**
     * 获取map中第一个数据值
     *
     * @param <K> Key的类型
     * @param <V> Value的类型
     * @param map 数据源
     * @return 返回的值
     */
    public static <K, V> V getFirstOrNull(Map<K, V> map) {
        V obj = null;
        for (Entry<K, V> entry : map.entrySet()) {
            obj = entry.getValue();
            if (obj != null) {
                break;
            }
        }
        return obj;
    }

}
