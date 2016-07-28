package com.sation.knxcontroller.widget;


import com.sation.knxcontroller.STKNXControllerConstant;
import com.sation.knxcontroller.control.KNXSwitch;
import com.sation.knxcontroller.models.KNXView.EBool;
import com.sation.knxcontroller.models.KNXView.EFlatStyle;
import com.sation.knxcontroller.util.ColorUtils;
import com.sation.knxcontroller.util.ImageUtils;
import com.sation.knxcontroller.util.Log;
import com.sation.knxcontroller.util.StringUtil;

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
import android.view.MotionEvent;


public class STKNXSwitch extends STKNXControl {
	private final int PADDING = 2;

    private Bitmap imageOn;
    private Bitmap imageOff;
    
    private KNXSwitch mKNXSwitch;
    
    private enum ControlState {
    	Down,
    	Normal,
    }
    private ControlState mControlState;
    private int imgX = 0;
    private int imgY = 0;
    private int imgRight = 0;
    private int imgBottom = 0;
    private enum SwitchState {
    	On,
    	Off,
    }
    private SwitchState mSwitchState;

	public STKNXSwitch(Context context, KNXSwitch knxswitch) {
		super(context, knxswitch);
		
		this.mKNXSwitch = knxswitch;
		this.setId(mKNXSwitch.getId());

		this.mControlState = ControlState.Normal;
		this.mSwitchState = SwitchState.Off;

		if(!StringUtil.isEmpty(this.mKNXSwitch.ImageOn)) {
			this.imageOn = ImageUtils.getDiskBitmap(STKNXControllerConstant.ConfigResImgPath + this.mKNXSwitch.ImageOn);
		}
			
		if(!StringUtil.isEmpty(mKNXSwitch.ImageOff)) {
			this.imageOff = ImageUtils.getDiskBitmap(STKNXControllerConstant.ConfigResImgPath + this.mKNXSwitch.ImageOff);
		}
	} 
	
	 public void setValue(int controlCurrentValue) { 
		 if(0 == controlCurrentValue) {
			 this.mSwitchState = SwitchState.Off;
			 if(null != this.imageOff) {
				 invalidate();
			 }
		 } else {
			 this.mSwitchState = SwitchState.On;
			 if(null != this.imageOn) {
				 invalidate();
			 }
		 }
	 }
    
    private void onClick() {
    	if(null == this.mKNXSwitch) {
    		return;
    	}
    	
    	int val;
    	if(SwitchState.Off == this.mSwitchState) {
    		if(this.mKNXSwitch.getReadAddressId().isEmpty() ||
    				this.mKNXSwitch.getWriteAddressIds().containsKey(
    						this.mKNXSwitch.getReadAddressId().keySet().toArray()[0])) {
    			if (null != this.imageOn) {
   				 	invalidate();
    			}
    			
    			this.mSwitchState = SwitchState.On;
    		}
    		
    		val = 1;
    	} else {
    		if (this.mKNXSwitch.getReadAddressId().isEmpty() ||
    				this.mKNXSwitch.getWriteAddressIds().containsKey(
    						this.mKNXSwitch.getReadAddressId().keySet().toArray()[0])) {
    			if(null != this.imageOff) {
    				invalidate();
    			}
    			
    			this.mSwitchState = SwitchState.Off;
    		}
    		
    		val = 0;
    	}
    	
    	sendCommandRequest(this.mKNXSwitch.getWriteAddressIds(), val+"", false, null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    	
        /* 计算图片的显示位置和大小 */
    	this.imgX = this.PADDING;
    	this.imgY = this.PADDING;
    	int height = this.mKNXSwitch.Height - 2 * this.imgY;
    	this.imgRight = this.imgX + height;   // 计算出高度
        this.imgBottom = this.imgY + height;     // 计算出宽度
        
        /**
         * 最后调用父类方法,把View的大小告诉父布局。
         */
        setMeasuredDimension(this.mKNXSwitch.Width, this.mKNXSwitch.Height);
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

        /* 绘制图片 */
        Bitmap image = null;
        if(SwitchState.On == this.mSwitchState) {
        	image = this.imageOn;
        } else if(SwitchState.Off == this.mSwitchState) {
       		image = this.imageOff;
       	}
        if(null != image) {
        	paint.reset();
        	Rect resRect = new Rect(0, 0, image.getWidth(), image.getHeight());
        	Rect desRect = new Rect(this.imgX, this.imgY, this.imgRight, this.imgBottom);
        	canvas.drawBitmap(image, resRect, desRect, paint);
        }

        if(null != this.mKNXSwitch.getText()) {
        	int x = 0;
        	int y = 0;
        	Rect bound = new Rect();
        	paint.getTextBounds(this.mKNXSwitch.getText(), 0, this.mKNXSwitch.getText().length(), bound);
        	if(null != image) {
        		x= (getWidth()- (this.imgRight+this.PADDING)-this.PADDING -bound.width())/2+this.imgRight+this.PADDING;
        		y=(getHeight()  + bound.height())/2;
        	} else {
        		x = getWidth()/2;
            	y = (getHeight()  + bound.height())/2;
        	} 

        	/* 绘制文本 */
        	paint.reset();
//        	paint.setTextAlign(Paint.Align.CENTER);
        	paint.setColor(Color.parseColor(this.mKNXSwitch.FontColor));
        	paint.setTextSize(this.mKNXSwitch.FontSize);
        	canvas.drawText(this.mKNXSwitch.getText(), x, y, paint);
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
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	switch (event.getAction()) { 
    		case MotionEvent.ACTION_DOWN: 
    			this.mControlState = ControlState.Down;
    			invalidate();
    			break; 
    		case MotionEvent.ACTION_UP: 
    			onClick();
    			this.mControlState = ControlState.Normal;
    			invalidate();
    			break;
    		case MotionEvent.ACTION_CANCEL:
    			this.mControlState = ControlState.Normal;
    			invalidate();
    			break;
    			
    		default:
    			break;
    	}

    	return true;
    }
}




	