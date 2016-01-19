package com.zyyknx.android.widget; 

import com.zyyknx.android.R;
import com.zyyknx.android.control.KNXIconSwitchButton;
import com.zyyknx.android.control.KNXSwitch;
import com.zyyknx.android.util.ImageUtils;
import com.zyyknx.android.util.StringUtil;
import com.zyyknx.android.widget.ControlView.ICallBack;

import android.content.Context; 
import android.content.res.TypedArray;  
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater; 
import android.view.View; 
import android.view.ViewGroup; 
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView; 
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

public class MySwitchButton extends ControlView { 
	
	private KNXSwitch mKNXSwitch; 
	private Button button; 
	private ImageView imageView; 
 
	private String buttonText;  
	private Drawable switchOnImage;
	private Drawable switchOffImage;

	public MySwitchButton(Context context) {
		this(context, null, null);
	} 
	
	public MySwitchButton(Context context, AttributeSet attrs, int defStyle) {
		this(context, attrs, null);
	} 

	public MySwitchButton(Context context, AttributeSet attrs , KNXSwitch mKNXSwitch) {
		super(context, attrs);
	    this.setKNXControl(mKNXSwitch);
		this.mKNXSwitch = mKNXSwitch;
		this.setId(mKNXSwitch.getId());
		this.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.icon_switch_widge, this, true);
		 
		button = (Button) v.findViewById(R.id.button); 
		imageView = (ImageView) v.findViewById(R.id.imageView); 
 
		button.setOnClickListener(onClickListener); 

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MySwitchButton, 0, 0); 
		buttonText = a.getString(R.styleable.MySwitchButton_buttonText); 
		   
		switchOnImage = a.getDrawable(R.styleable.MySwitchButton_switchOnImage);
		switchOffImage = a.getDrawable(R.styleable.MySwitchButton_switchOffImage);
		
		a.recycle();
		
		//初始化值
		initButton(mKNXSwitch);
	} 
	
	
	public void initButton(final KNXSwitch mKNXSwitch) { 
		//uiButton.setId(mKNXControlBase.getId());
		if( mKNXSwitch != null) {
		    imageView.setTag("0");
			button.setText(mKNXSwitch.getText()); 
			
			if(!StringUtil.isEmpty(mKNXSwitch.getOffImage())) {
				FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);  
				//此处相当于布局文件中的Android:layout_gravity属性 
				lp.leftMargin = 15;
				lp.topMargin= 10;
				lp.bottomMargin = 15;
				lp.gravity = Gravity.LEFT; 
				imageView.setLayoutParams(lp);
				//imageView.setScaleType(ScaleType.FIT_CENTER);
				imageView.setImageBitmap(ImageUtils.getDiskBitmap("zyyknxandroid/.nomedia/res/img/" + mKNXSwitch.getOffImage()));
			}
			  
		}
	}
	
	 public void setValue(int controlCurrentValue) { 
		 if(String.valueOf(controlCurrentValue).equals("1")) { 
			imageView.setTag("1");  
     		if(!StringUtil.isEmpty(mKNXSwitch.getOnImage())) {
 				imageView.setImageBitmap(ImageUtils.getDiskBitmap("zyyknxandroid/.nomedia/res/img/" + mKNXSwitch.getOnImage())); 
 			} else {
 				imageView.setImageResource(R.drawable.shixun_silder_swich_light_up);
 			}
     	} else {
     		imageView.setTag("0"); 
     		if(!StringUtil.isEmpty(mKNXSwitch.getOffImage())) {
 				imageView.setImageBitmap(ImageUtils.getDiskBitmap("zyyknxandroid/.nomedia/res/img/" + mKNXSwitch.getOffImage())); 
 			} else {
 				imageView.setImageResource(R.drawable.shixun_silder_swich_light_down);
 			}
     	}
	 }
    
    OnClickListener onClickListener = new OnClickListener() {
    	
        public void onClick(View v) { 
        	
        	if(imageView.getTag().toString().equals("0")) { 
        		imageView.setTag("1");  
        		if(!StringUtil.isEmpty(mKNXSwitch.getOnImage())) {
    				imageView.setImageBitmap(ImageUtils.getDiskBitmap("zyyknxandroid/.nomedia/res/img/" + mKNXSwitch.getOnImage())); 
    			} else {
    				imageView.setImageResource(R.drawable.shixun_silder_swich_light_up);
    			}
        	} else {
        		imageView.setTag("0"); 
        		if(!StringUtil.isEmpty(mKNXSwitch.getOffImage())) {
    				imageView.setImageBitmap(ImageUtils.getDiskBitmap("zyyknxandroid/.nomedia/res/img/" + mKNXSwitch.getOffImage())); 
    			} else {
    				imageView.setImageResource(R.drawable.shixun_silder_swich_light_down);
    			}
        	}
        	sendCommandRequest(mKNXSwitch.getWriteAddressIds(), imageView.getTag().toString(), false, new ICallBack() {
        		@Override
				public void onCallBack() {
        			
        		}
        	}); 
        	
        	
        	/*
        	sendCommandRequest(mKNXSwitch.getWriteAddressIds(), imageView.getTag().toString(), false, new ICallBack() {

				@Override
				public void onCallBack() {
					if(imageView.getTag().toString().equals("0")) { 
		        		imageView.setTag("1");  
		        		if(!StringUtil.isEmpty(mKNXSwitch.getOnImage())) {
		    				imageView.setImageBitmap(ImageUtils.getDiskBitmap("zyyknxandroid/.nomedia/res/img/" + mKNXSwitch.getOnImage())); 
		    			} else {
		    				imageView.setImageResource(R.drawable.shixun_silder_swich_light_up);
		    			}
		        	} else {
		        		imageView.setTag("0"); 
		        		if(!StringUtil.isEmpty(mKNXSwitch.getOffImage())) {
		    				imageView.setImageBitmap(ImageUtils.getDiskBitmap("zyyknxandroid/.nomedia/res/img/" + mKNXSwitch.getOffImage())); 
		    			} else {
		    				imageView.setImageResource(R.drawable.shixun_silder_swich_light_down);
		    			}
		        	}
					return;
				}
        		 
        	 });
        	 */
        }
    };

    /*
    OnLongClickListener onLongClickListener = new OnLongClickListener() {
    	
        public boolean onLongClick(View v) {
            return true;
        }
    };
    
    private ChangeCurrentByLongPressCommand mChangeCurrentByLongPressCommand;
    private static final long DEFAULT_LONG_PRESS_UPDATE_INTERVAL = 300;
    private long mLongPressUpdateInterval = DEFAULT_LONG_PRESS_UPDATE_INTERVAL;
    class ChangeCurrentByLongPressCommand implements Runnable {
    	
        private boolean mIncrement; 
        private void setIncrement(boolean increment) {
            mIncrement = increment;
        }

        public void run() { 
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
    */
}
