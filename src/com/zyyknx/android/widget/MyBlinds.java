package com.zyyknx.android.widget;
 
import com.zyyknx.android.R; 
import com.zyyknx.android.control.ControlSymbol;
import com.zyyknx.android.control.KNXBlinds;  
import com.zyyknx.android.control.MediaButtonType;
import com.zyyknx.android.util.StringUtil;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable; 
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button; 
import android.widget.ImageView;
import android.widget.TextView; 

public class MyBlinds extends ControlView { /* extends FrameLayout { */
	
	private KNXBlinds mKNXBlinds; 
	private Button btnLeft;
	private Button btnRight;
	private TextView txtBlindsText;
	private ImageView imgLeft;
	private ImageView imgRight; 

    private OnBlindsListener mOnBlindsListener; 
    //当前值
    private int currentValue = 2; 
    //最小值
    private int mMinValue;
    //最大值
    private int mMaxValue;
 
	private String blindsButtonText; 
	private int textColor; 
	private Drawable buttonBackground;

	public MyBlinds(Context context) {
		this(context, null, null);
	} 
	
	public MyBlinds(Context context, AttributeSet attrs, int defStyle) {
		this(context, attrs, null);
	} 

	public MyBlinds(Context context, AttributeSet attrs , KNXBlinds mKNXBlinds) {
		super(context, attrs);
		this.setId(mKNXBlinds.getId());
		this.setKNXControl(mKNXBlinds);
		this.mKNXBlinds = mKNXBlinds;
		this.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.blinds_widge, this, true);
		 
		btnLeft = (Button) v.findViewById(R.id.btnLeft);
		btnRight = (Button) v.findViewById(R.id.btnRight);
		txtBlindsText = (TextView) v.findViewById(R.id.txtBlindsText);
		imgLeft = (ImageView) v.findViewById(R.id.imgLeft);
		imgRight = (ImageView) v.findViewById(R.id.imgRight); 
 
		btnLeft.setOnClickListener(onClickListener);
		btnLeft.setOnLongClickListener(onLongClickListener);
		btnRight.setOnClickListener(onClickListener);
		btnRight.setOnLongClickListener(onLongClickListener);

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MyBlinds, 0, 0); 
		blindsButtonText = a.getString(R.styleable.MyBlinds_blindsButtonText); 
		
		mMinValue = a.getInt(R.styleable.MyBlinds_minValue, 0); 
		mMaxValue = a.getInt(R.styleable.MyBlinds_maxValue, 10); 
		currentValue = a.getInt(R.styleable.MyBlinds_currentValue, 5); 
		
		textColor = a.getColor(R.styleable.MyBlinds_android_textColor, android.R.color.darker_gray); 
		buttonBackground = a.getDrawable(R.styleable.MyBlinds_buttonBackground);
		a.recycle();
		
		if (blindsButtonText != null) {
			txtBlindsText.setText(blindsButtonText);
			txtBlindsText.setTextColor(textColor);
		}  
		if (buttonBackground != null) {
			this.setBackgroundDrawable(buttonBackground);
		}
		
		//初始化值
		initButton(mKNXBlinds);
	} 
	
	
	private void initButton(final KNXBlinds mKNXBlinds) { 
		//uiButton.setId(mKNXControlBase.getId());
		if( mKNXBlinds != null) {
			txtBlindsText.setText(mKNXBlinds.getText());
			if(mKNXBlinds.getHasSingleLabel()) {
				txtBlindsText.setText(mKNXBlinds.getText());
			} else {
				if(!StringUtil.isEmpty(mKNXBlinds.getLeftText())) {
					btnLeft.setText(mKNXBlinds.getLeftText());
				}
				if(!StringUtil.isEmpty(mKNXBlinds.getRightText())) {
					btnRight.setText(mKNXBlinds.getRightText());
				}
			}
			
			//mMinValue = mKNXBlinds.getMinValue();
			//mMaxValue = mKNXBlinds.getMaxValue(); 							 
			String controlSymbol = mKNXBlinds.getControlSymbol(); 
			if(controlSymbol.equalsIgnoreCase("DownUp"))
			{
				 imgLeft.setImageResource(R.drawable.default_blinds_symbol_arrow_down);
				 imgRight.setImageResource(R.drawable.default_blinds_symbol_arrow_up); 
			}
			else if(controlSymbol.equalsIgnoreCase("DardBright"))
			{
				 imgLeft.setImageResource(R.drawable.default_blinds_symbol_light_down);
				 imgRight.setImageResource(R.drawable.default_blinds_symbol_light_up); 
			}
			else if(controlSymbol.equalsIgnoreCase("SubtractAdd"))
			{
				 imgLeft.setImageResource(R.drawable.default_blinds_symbol_sign_down);
				 imgRight.setImageResource(R.drawable.default_blinds_symbol_sign_up);  
			}
			else if(controlSymbol.equalsIgnoreCase("Volume"))
			{
				 imgLeft.setImageResource(R.drawable.default_blinds_symbol_volume_down);
				 imgRight.setImageResource(R.drawable.default_blinds_symbol_volume_up); 
			}
			/*
			switch(controlSymbol) 
			{ 
			   case 0: 
				   imgLeft.setImageResource(R.drawable.default_blinds_symbol_light_down);
				   imgRight.setImageResource(R.drawable.default_blinds_symbol_light_up); 
			       break; 
			   case 1: 
				   imgLeft.setImageResource(R.drawable.default_blinds_symbol_arrow_down);
				   imgRight.setImageResource(R.drawable.default_blinds_symbol_arrow_up); 
			       break; 
			   case 2: 
				   imgLeft.setImageResource(R.drawable.default_blinds_symbol_light_down);
				   imgRight.setImageResource(R.drawable.default_blinds_symbol_light_up); 
			       break;
			   case 3: 
				   imgLeft.setImageResource(R.drawable.default_blinds_symbol_sign_down);
				   imgRight.setImageResource(R.drawable.default_blinds_symbol_sign_up); 
			       break; 
			   case 4: 
				   imgLeft.setImageResource(R.drawable.default_blinds_symbol_volume_down);
				   imgRight.setImageResource(R.drawable.default_blinds_symbol_volume_up); 
				   break; 
			   default: 
				   imgLeft.setImageResource(R.drawable.default_blinds_symbol_down);
				   imgRight.setImageResource(R.drawable.default_blinds_symbol_up); 
			       break; 
			}
			*/
		} else {
			 imgLeft.setImageResource(R.drawable.default_blinds_symbol_down);
			 imgRight.setImageResource(R.drawable.default_blinds_symbol_up); 
		}
		btnLeft.setOnClickListener(onClickListener);
		btnRight.setOnClickListener(onClickListener);
		
	}
	
	 /**
     * Set listener to be notified for scroll state changes.
     *
     * @param onScrollListener The listener.
     */
    public void setOnBlindsListener(OnBlindsListener onBlindsListener) {
        mOnBlindsListener = onBlindsListener;
    }
    
    OnClickListener onClickListener = new OnClickListener() {
    	
        public void onClick(final View v) {
             //if(mOnBlindsListener != null) {
            	
            	 //sendCommandRequest(mKNXBlinds.getWriteAddressIds(), String.valueOf(currentValue), false, new ICallBack() {

					//@Override
					//public void onCallBack() {
						// TODO Auto-generated method stub
						 if(v.getId() == R.id.btnLeft) {
		            		 //if(currentValue > mMinValue) {
		            			 currentValue=1;
		            			 //currentValue = 1;
		            			 //mOnBlindsListener.onLeftClick(currentValue);  
		            		//}
		            	 }
		            	 if(v.getId() == R.id.btnRight) {
		            		 //if(currentValue < mMaxValue) {
		            			 //currentValue++;
		            		 	  currentValue = 0;
		            			 //mOnBlindsListener.onLeftClick(currentValue);   
		            		 //}
		            	 }
		            	 sendCommandRequest(mKNXBlinds.getWriteAddressIds(), String.valueOf(currentValue), false, null);
					//}
            		 
            	 //});
             //}
        }
    };

    OnLongClickListener onLongClickListener = new OnLongClickListener() {
    	
        public boolean onLongClick(View v) {
        	if(mOnBlindsListener != null) {
	           	if(v.getId() == R.id.btnLeft) {
	           		postChangeCurrentByOneFromLongPress(false);
	           	}
	           	if(v.getId() == R.id.btnRight) {
	           		postChangeCurrentByOneFromLongPress(true);
	           	}
            }
            return true;
        }
    };
    
    
    //获取最小值
    public int getMinValue() {
        return mMinValue;
    }

    //设置最小值
    public void setMinValue(int minValue) {
        if (mMinValue == minValue) {
            return;
        }
        if (minValue < 0) {
            throw new IllegalArgumentException("最小值必须大于等于0");
        }
        mMinValue = minValue;
        if (mMinValue > currentValue) {
        	currentValue = mMinValue;
        } 
    }

    //获取最大值
    public int getMaxValue() {
        return mMaxValue;
    }
    //设置最大值
    public void setMaxValue(int maxValue) {
        if (mMaxValue == maxValue) {
            return;
        }
        if (maxValue < 0) {
            throw new IllegalArgumentException("最大值必须大于等于");
        }
        mMaxValue = maxValue;
        if (mMaxValue < currentValue) {
        	currentValue = mMaxValue;
        } 
    }
    
    private ChangeCurrentByLongPressCommand mChangeCurrentByLongPressCommand;
    private static final long DEFAULT_LONG_PRESS_UPDATE_INTERVAL = 300;
    private long mLongPressUpdateInterval = DEFAULT_LONG_PRESS_UPDATE_INTERVAL;
    class ChangeCurrentByLongPressCommand implements Runnable {
    	
        private boolean mIncrement; 
        private void setIncrement(boolean increment) {
            mIncrement = increment;
        }

        public void run() {
            changeCurrentByOne(mIncrement);
            postDelayed(this, mLongPressUpdateInterval);
        }
    }
    private void postChangeCurrentByOneFromLongPress(boolean increment) { 
        removeAllCallbacks();
        if (mChangeCurrentByLongPressCommand == null) {
        	mChangeCurrentByLongPressCommand = new ChangeCurrentByLongPressCommand();
        }
        mChangeCurrentByLongPressCommand.setIncrement(increment);
        post(mChangeCurrentByLongPressCommand);
    }
    
    //从消息队列中移除callback
    private void removeAllCallbacks() {
        if (mChangeCurrentByLongPressCommand != null) {
            removeCallbacks(mChangeCurrentByLongPressCommand);
        }
    } 
    
    private void changeCurrentByOne(boolean increment) { 
        if (increment) {
            changeCurrent(currentValue++);
        } else {
            changeCurrent(currentValue--);
        }
    }
    public void changeCurrent(int value) {
	    if (currentValue == value) {
	        return;
	    }
	    if (value < mMinValue) {
	        value = mMinValue;
	    }
	    if (value > mMaxValue) {
	        value = mMaxValue;
	    }
	    currentValue = value;
    }
     
 
	public interface OnBlindsListener {

		public void onLeftClick(int result);
		public void onRightClick(int result);
		
		public void onLeftLongClick(int result);
		public void onRightLongClick(int result);
	}

}