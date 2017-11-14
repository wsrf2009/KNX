package com.sation.knxcontroller.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.sation.knxcontroller.STKNXControllerApp;
import com.sation.knxcontroller.STKNXControllerConstant;
import com.sation.knxcontroller.control.KNXDigitalAdjustment;
import com.sation.knxcontroller.control.KNXSwitch;
import com.sation.knxcontroller.knxdpt.DPT1;
import com.sation.knxcontroller.knxdpt.KNXDatapointType;
import com.sation.knxcontroller.models.KNXGroupAddress;
import com.sation.knxcontroller.models.KNXSelectedAddress;
import com.sation.knxcontroller.models.KNXView.EBool;
import com.sation.knxcontroller.models.KNXView.EFlatStyle;
import com.sation.knxcontroller.util.ColorUtils;
import com.sation.knxcontroller.util.ImageUtils;
import com.sation.knxcontroller.util.Log;
import com.sation.knxcontroller.util.MapUtils;
import com.sation.knxcontroller.util.StringUtil;

import java.lang.ref.WeakReference;

public class STKNXSwitch extends STKNXControl {
	private final static String TAG = "STKNXSwitch";
	private final int PADDING = 2;

    private ImageView mImageView;
    private KNXSwitch mKNXSwitch;
    
    private enum ControlState {
    	Down,
    	Normal,
    }
    private ControlState mControlState;
    private int imgY = 0;
    private int imgRight = 0;
    private enum SwitchState {
    	On,
    	Off,
    }
    private SwitchState mSwitchState;
	private Bitmap imageOn;
	private Bitmap imageOff;

	public STKNXSwitch(Context context, KNXSwitch knxswitch) {
		super(context, knxswitch);
		
		this.mKNXSwitch = knxswitch;
		this.setId(mKNXSwitch.getId());

		this.mControlState = ControlState.Normal;
		this.mSwitchState = SwitchState.Off;

		this.mImageView = new ImageView(context);
		this.addView(this.mImageView);

		new Thread(new Runnable() {
			@Override
			public void run() {
				if(!StringUtil.isNullOrEmpty(mKNXSwitch.getImageOn())) {
					imageOn = ImageUtils.getDiskBitmap(STKNXControllerConstant.ConfigResImgPath +
									mKNXSwitch.getImageOn(),
							getImageViewLength(), getImageViewLength());
				}

				if(!StringUtil.isNullOrEmpty(mKNXSwitch.getImageOff())) {
					imageOff = ImageUtils.getDiskBitmap(STKNXControllerConstant.ConfigResImgPath +
									mKNXSwitch.getImageOff(),
							getImageViewLength(), getImageViewLength());
				}

				updateControlState();
			}
		}).start();
	}

	@Override
	public void onSuspend() {

	}

	@Override
	public void onResume() {
		copyStatusAndRequest();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	private static class STKNXSwitchHandler extends Handler {
		WeakReference<STKNXSwitch> mSwitch;

		private STKNXSwitchHandler(STKNXSwitch s) {
			super(s.getContext().getMainLooper());

			mSwitch = new WeakReference<STKNXSwitch>(s);
		}

		@Override
		public void handleMessage(Message msg) {
			setControlImage(mSwitch.get());
		}
	}
	
	private static void setControlImage(STKNXSwitch mSwitch) {
		if(null != mSwitch.mImageView) {
			if (SwitchState.On == mSwitch.mSwitchState) {
				if (null != mSwitch.imageOn) {
					mSwitch.mImageView.setImageBitmap(mSwitch.imageOn);
				}
			} else if (SwitchState.Off == mSwitch.mSwitchState) {
				if (null != mSwitch.imageOff) {
					mSwitch.mImageView.setImageBitmap(mSwitch.imageOff);
				}
			}
		}
	}

	private void updateControlState() {
		STKNXSwitchHandler mHandler = new STKNXSwitchHandler(STKNXSwitch.this);
		mHandler.sendEmptyMessage(0);
	}
	
	 public void setValue(byte[] array) {
		 KNXGroupAddress address = this.mKNXSwitch.getReadAddress();
		 if (null != address) {
			 int value;

			 /* 根据组地址DPT解析数据 */
			 if (address.getKnxMainNumber().equals(KNXDatapointType.DPT_1)) {
				 value = DPT1.byteArray2int(array);
			 } else {
				 value = KNXDatapointType.bytes2int(array, address.getType());
			 }

			 if (0 == value) {
				 this.mSwitchState = SwitchState.Off;
			 } else {
				 this.mSwitchState = SwitchState.On;
			 }

			 updateControlState();
		 }
	 }

	 private int getImageViewLength(){
		 int w = this.mKNXSwitch.Width - 2 * this.imgY;
		 int h = this.mKNXSwitch.Height - 2 * this.imgY;
		 return w > h ? h:w;
	 }
    
    private void onClick() {
    	if(null == this.mKNXSwitch) {
    		return;
    	}
    	
    	int val;
    	if(SwitchState.Off == this.mSwitchState) {
			this.mSwitchState = SwitchState.On;
    		val = 1;
    	} else {
			this.mSwitchState = SwitchState.Off;
    		val = 0;
    	}

		setControlImage(STKNXSwitch.this);
    	sendCommandRequest(this.mKNXSwitch.getWriteAddressIds(), val+"", false, null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    	measureChildren(widthMeasureSpec, heightMeasureSpec);
        
        /**
         * 最后调用父类方法,把View的大小告诉父布局。
         */
        setMeasuredDimension(this.mKNXSwitch.Width, this.mKNXSwitch.Height);
    } 
    
    @Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int childViewCount = getChildCount();

		/*
		 * 遍历所有childView根据其宽和高，以及margin进行布局
		 */
		for (int i = 0; i < childViewCount; i++) {
			int cl = 0, ct = 0, cr = 0, cb = 0;
			
			View view = getChildAt(i);
			if(view instanceof ImageView) {
				int imgHeight = getImageViewLength();

				cl = this.mKNXSwitch.getPadding().getLeft();
				ct = this.mKNXSwitch.getPadding().getTop();
				cr = this.mKNXSwitch.Width - this.mKNXSwitch.getPadding().getRight();
				cb = this.mKNXSwitch.Height - this.mKNXSwitch.getPadding().getBottom();
			}
			view.layout(cl, ct, cr, cb);
		}
	}

    @SuppressLint("DrawAllocation")
	@Override
    protected void onDraw(Canvas canvas) {
    	super.onDraw(canvas);
  
    	Paint paint = new Paint();
    	paint.setStyle(Paint.Style.FILL_AND_STROKE);	// 充满  
    	paint.setAlpha((int)(this.mKNXSwitch.Alpha*255));
    	RectF oval3 = new RectF(0, 0, getWidth(), getHeight());// 设置个新的长方形  
    	
    	int backColor = Color.parseColor(this.mKNXSwitch.BackgroundColor);
    	if((SwitchState.On == this.mSwitchState) && (null != this.mKNXSwitch.ColorOn)) {
    		backColor = Color.parseColor(this.mKNXSwitch.ColorOn);
        } else if((SwitchState.Off == this.mSwitchState) && (null != this.mKNXSwitch.ColorOff)) {
        	backColor = Color.parseColor(this.mKNXSwitch.ColorOff);
       	}

    	if(EFlatStyle.Stereo == this.mKNXSwitch.getFlatStyle()) {	// 画立体感的圆角矩形 
        
    		/* 渐变色，颜色数组 */
    		int colors[] = new int[3];
    		colors[0] = ColorUtils.changeBrightnessOfColor(backColor, 100);
    		colors[1] = backColor;
    		colors[2] = ColorUtils.changeBrightnessOfColor(backColor, -50);
    		
    		/* 各颜色所在的位置 */
    		float positions[] = new float[3];
    		positions[0] = .0f;
    		positions[1] = .3f;
    		positions[2] = 1.0f;
    		
    		Shader mShader = new LinearGradient(0, 0, 0, getHeight(), 
    			colors, positions, Shader.TileMode.CLAMP); // 设置渐变色 这个正方形的颜色是改变的 , 一个材质,打造出一个线性梯度沿著一条线。  
    		paint.setShader(mShader);  
    	} else {	// 画扁平风格的圆角矩形
    		paint.setARGB((int)(this.mKNXSwitch.Alpha*255), Color.red(backColor), Color.green(backColor), Color.blue(backColor));
    	}
    	canvas.drawRoundRect(oval3, this.mKNXSwitch.Radius, this.mKNXSwitch.Radius, paint);//第二个参数是x半径，第三个参数是y半径  

        if(null != this.mKNXSwitch.getTitle()) {
			Paint textPaint = this.mKNXSwitch.TitleFont.getTextPaint();
			float x = 0;
//			if (StringUtil.isNullOrEmpty(this.imageOn) && StringUtil.isNullOrEmpty(this.imageOff)) {
			if((null == this.imageOn) && (null == this.imageOff)) {
				x = oval3.width() / 2;
			} else {
				x = this.imgRight + this.PADDING + (oval3.width() - (this.imgRight + this.PADDING) )/2;
			}
			int baseY = (int) ((oval3.height() / 2) - ((textPaint.descent() + textPaint.ascent()) / 2));
			canvas.drawText(this.mKNXSwitch.getTitle(), oval3.width()/2, baseY, textPaint);
        }
        
        switch (mControlState) {
        	case Down:
        		paint.reset();
        		paint.setStyle(Paint.Style.FILL);
        		paint.setColor(Color.parseColor("#FF6100"));
        		paint.setAlpha(0x60);
        		canvas.drawRoundRect(oval3, this.mKNXSwitch.Radius, this.mKNXSwitch.Radius, paint);	//第二个参数是x半径，第三个参数是y半径  
        		break;
        	case Normal:
        		break;
        }
        
        if(EBool.Yes == this.mKNXSwitch.getDisplayBorder()) {
    		paint.reset();
    		paint.setStyle(Paint.Style.STROKE);
    		paint.setColor(Color.parseColor(this.mKNXSwitch.BorderColor));
    		canvas.drawRoundRect(oval3, this.mKNXSwitch.Radius, this.mKNXSwitch.Radius, paint);//第二个参数是x半径，第三个参数是y半径  
    	}
    }
    
    @SuppressLint("ClickableViewAccessibility")
	@Override
    public boolean onTouchEvent(MotionEvent event) {
		if(EBool.Yes != this.mKNXSwitch.getClickable()) {
			return true;
		}

    	switch (event.getAction()) { 
    		case MotionEvent.ACTION_DOWN:
				onClick();
    			this.mControlState = ControlState.Down;
    			invalidate();
    			if(null != this.mImageView) {
					this.mImageView.setAlpha(0.6f);
				}
    			break; 
    		case MotionEvent.ACTION_UP:
    			this.mControlState = ControlState.Normal;
    			invalidate();
    			if(null != this.mImageView) {
    				this.mImageView.setAlpha(1.0f);
    			}
    			break;
    		case MotionEvent.ACTION_CANCEL:
    			this.mControlState = ControlState.Normal;
    			invalidate();
    			if(null != this.mImageView) {
    				this.mImageView.setAlpha(1.0f);
    			}
    			break;
    			
    		default:
    			break;
    	}

    	return true;
    }

    @Override
	public void copyStatusAndRequest() {
		super.copyStatusAndRequest();

		byte[] bytes = getControlStatus(this.mKNXSwitch.getReadAddressId(), true);
		if (null != bytes) {
			setValue(bytes);
		}
	}

	@Override
	public void statusUpdate(int asp, KNXGroupAddress address) {
		super.statusUpdate(asp, address);

		KNXSelectedAddress readAddr = MapUtils.getFirstOrNull(this.mKNXSwitch.getReadAddressId());
		if (null != readAddr) {
			if (address.getId().equals(readAddr.getId())) {
				byte[] bytes = copyObjectStatus(asp);
				this.setValue(bytes);
			}
		}
	}
}




	