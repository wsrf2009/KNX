package com.sation.knxcontroller.widget;

import com.sation.knxcontroller.STKNXControllerConstant;
import com.sation.knxcontroller.control.KNXSceneButton;
import com.sation.knxcontroller.models.KNXView.EFlatStyle;
import com.sation.knxcontroller.util.ColorUtils;
import com.sation.knxcontroller.util.ImageUtils;
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

public class STKNXSceneButton extends STKNXControl {
	private final int PADDING = 5;
    
	private Bitmap imageOn;
    private Bitmap imageOff;
    
	public KNXSceneButton mKNXSceneButton;
    
    private enum ControlState {
    	Down,
    	Normal,
    }
	private ControlState mControlState;

	private enum SceneState {
    	On,
    	Off,
    }
    private SceneState mSceneState;
    
    private int imgX = 0;
    private int imgY = 0;
    private int imgRight = 0;
    private int imgBottom = 0;

	public STKNXSceneButton(Context context, KNXSceneButton knxSceneButton) {
		super(context, knxSceneButton);
		
		this.mKNXSceneButton = knxSceneButton;
		this.setId(mKNXSceneButton.getId());
		
		this.mControlState = ControlState.Normal;
		this.mSceneState = SceneState.Off;
		
		if(!StringUtil.isEmpty(this.mKNXSceneButton.ImageOn)) {
			this.imageOn = ImageUtils.getDiskBitmap(STKNXControllerConstant.ConfigResImgPath + this.mKNXSceneButton.ImageOn);
		}
			
		if(!StringUtil.isEmpty(this.mKNXSceneButton.ImageOff)) {
			this.imageOff = ImageUtils.getDiskBitmap(STKNXControllerConstant.ConfigResImgPath + this.mKNXSceneButton.ImageOff);
		}
	}
	
	public void setSelected(boolean s) {
		if(s) {
			this.mSceneState = SceneState.On;
		} else {
			this.mSceneState = SceneState.Off;
		}
		
		invalidate();
	}
    
    private void onClick() {
    	if(null == this.mKNXSceneButton) {
    		return;
    	}
    	  
    	if(this.mKNXSceneButton.IsGroup) {
    		STKNXGroupBox mSTKNXGroupBox = (STKNXGroupBox)this.getParent();
    		if((null != mSTKNXGroupBox) && (null != mSTKNXGroupBox.mKNXGroupBox)) {
    			if(mSTKNXGroupBox.mKNXGroupBox.getReadAddressId().isEmpty() ||
    					mSTKNXGroupBox.mKNXGroupBox.getWriteAddressIds().containsKey(
    							mSTKNXGroupBox.mKNXGroupBox.getReadAddressId().keySet().toArray()[0])) {
    				mSTKNXGroupBox.setSelectedValue(this.mKNXSceneButton.DefaultValue);
    			}
    			 
    			sendCommandRequest(mSTKNXGroupBox.mKNXGroupBox.getWriteAddressIds(), this.mKNXSceneButton.DefaultValue+"", false, null);
    		}
    	} else {
    		if(this.mKNXSceneButton.getReadAddressId().isEmpty() ||
    				this.mKNXSceneButton.getWriteAddressIds().containsKey(
    						this.mKNXSceneButton.getReadAddressId().keySet().toArray()[0])) {
    			if(SceneState.On == this.mSceneState) {
    				this.setSelected(false);
    			} else {
    				this.setSelected(true);
    			}
    		}
    		
    		sendCommandRequest(this.mKNXSceneButton.getWriteAddressIds(), "", true, null);
    	}
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    	this.imgX = this.PADDING;
    	this.imgY = this.PADDING;
    	int height = this.mKNXSceneButton.Height - 2 * this.imgY;
    	this.imgRight = this.imgX + height;   // 计算出高度
        this.imgBottom = this.imgY + height;     // 计算出宽度
    	
        /**
         * 最后调用父类方法,把View的大小告诉父布局。
         */
        setMeasuredDimension(this.mKNXSceneButton.Width, this.mKNXSceneButton.Height);
    }

    @SuppressLint("DrawAllocation")
	@Override
    protected void onDraw(Canvas canvas) {
    	super.onDraw(canvas);
  
//    	int backColor = Color.parseColor(this.mKNXSceneButton.BackgroundColor);
    	Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    	paint.setStyle(Paint.Style.FILL_AND_STROKE);	// 充满  
    	paint.setAlpha((int)(this.mKNXSceneButton.Alpha*255));
    	RectF oval3 = new RectF(0, 0, getWidth(), getHeight());// 设置个新的长方形  
    	
    	int backColor = Color.parseColor(this.mKNXSceneButton.BackgroundColor);
    	if((SceneState.On == this.mSceneState) && (null != this.mKNXSceneButton.ColorOn)) {
    		backColor = Color.parseColor(this.mKNXSceneButton.ColorOn);
        } else if((SceneState.Off == this.mSceneState) && (null != this.mKNXSceneButton.ColorOff)) {
        	backColor = Color.parseColor(this.mKNXSceneButton.ColorOff);
       	}

    	if(EFlatStyle.Stereo == this.mKNXSceneButton.getFlatStyle()) {	// 画立体感的圆角矩形 
        
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
    		paint.setARGB((int)(this.mKNXSceneButton.Alpha*255), Color.red(backColor), Color.green(backColor), Color.blue(backColor));
    	}
    	canvas.drawRoundRect(oval3, this.mKNXSceneButton.Radius, this.mKNXSceneButton.Radius, paint);//第二个参数是x半径，第三个参数是y半径  
    	
    	if(this.mKNXSceneButton.DisplayBorder && (this.mKNXSceneButton.Radius ==0)) {
    		paint.reset();
    		paint.setStyle(Paint.Style.STROKE);
    		paint.setColor(Color.parseColor(this.mKNXSceneButton.BorderColor));
    		canvas.drawRoundRect(oval3, 0, 0, paint);//第二个参数是x半径，第三个参数是y半径  
    	}
    
        /* 绘制图片 */
        Bitmap image = null;
        if(SceneState.On == this.mSceneState) {
        	image = this.imageOn;
        } else if(SceneState.Off == this.mSceneState) {
       		image = this.imageOff;
       	}
        if(null != image) {
        	Rect resRect = new Rect(0, 0, image.getWidth(), image.getHeight());
        	Rect desRect = new Rect(this.imgX, this.imgY, this.imgRight, this.imgBottom);
        	canvas.drawBitmap(image, resRect, desRect, paint);
        }

        if(null != this.mKNXSceneButton.getText()) {
        	int x = 0;
        	int y = 0;
        	Rect bound = new Rect();
        	paint.getTextBounds(this.mKNXSceneButton.getText(), 0, this.mKNXSceneButton.getText().length(), bound);
        	if(null != image) {
        		x=(getWidth()-this.imgRight+this.PADDING)/2-(bound.right-bound.left)/2+this.imgRight+this.PADDING;
        		y=(getHeight()  + bound.height())/2;
        	} else {
        		x = (getWidth() - 2 *x - bound.width())/2;
            	y = (getHeight()  + bound.height())/2;
        	}
        	
        	/* 绘制文本 */
        	paint.reset();
        	paint.setColor(Color.parseColor(this.mKNXSceneButton.FontColor));
        	paint.setTextSize(this.mKNXSceneButton.FontSize);
        	canvas.drawText(this.mKNXSceneButton.getText(), x, y, paint);
        }

        switch (mControlState) {
        	case Down:
        		paint.reset();
        		paint.setStyle(Paint.Style.FILL);
        		paint.setColor(Color.parseColor("#FF6100"));
        		paint.setAlpha(0x60);
        		canvas.drawRoundRect(oval3, 5, 5, paint);	//第二个参数是x半径，第三个参数是y半径  
        		break;
        	case Normal:
        		break;
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
