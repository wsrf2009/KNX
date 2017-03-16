package com.sation.knxcontroller.widget;

import com.sation.knxcontroller.STKNXControllerConstant;
import com.sation.knxcontroller.control.KNXSceneButton;
import com.sation.knxcontroller.models.KNXView.EBool;
import com.sation.knxcontroller.models.KNXView.EFlatStyle;
import com.sation.knxcontroller.util.ColorUtils;
import com.sation.knxcontroller.util.ImageUtils;
import com.sation.knxcontroller.util.Log;
import com.sation.knxcontroller.util.StringUtil;
import com.sation.knxcontroller.util.uikit.UIKit;

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
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;

public class STKNXSceneButton extends STKNXControl {
	private static final String TAG = "STKNXSceneButton";
	private final int PADDING = 2;

	private String imageOn;
	private String imageOff;
	private ImageView mImageView;
    
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
    
    private int imgY = 0;
    private int imgRight = 0;
//    private Handler mHandler;

	public STKNXSceneButton(Context context, KNXSceneButton knxSceneButton) {
		super(context, knxSceneButton);
		
		this.mKNXSceneButton = knxSceneButton;
		this.setId(mKNXSceneButton.getId());
		
		this.mControlState = ControlState.Normal;
		this.mSceneState = SceneState.Off;
		
		if(!StringUtil.isEmpty(this.mKNXSceneButton.getImageOn())) {
			this.imageOn = STKNXControllerConstant.ConfigResImgPath + this.mKNXSceneButton.getImageOn();
		}
			
		if(!StringUtil.isEmpty(this.mKNXSceneButton.getImageOff())) {
			this.imageOff = STKNXControllerConstant.ConfigResImgPath + this.mKNXSceneButton.getImageOff();
		}
		
		this.mImageView = new ImageView(context);
		this.addView(this.mImageView);

		
		
//		this.mHandler = new Handler() {
//			@Override
//			public void handleMessage(Message msg) {
//				super.handleMessage(msg);
//				
//				if(1 == msg.what) {
////					setStateSelected();
//					Bitmap bm = (Bitmap) msg.obj;
//                	if((null != bm) && (null != mImageView)) {
//                		mImageView.setImageBitmap(bm);
//                	}
//				}
////				else if(2 == msg.what) {
////					setStateNotSelected();
////				}
//			}
//		};
		
		this.selecte(false);
	}
	
	@Override
	public void onDestroy() {
		this.imageOn = null;
		this.imageOff = null;
		this.mImageView = null;
//		this.mKNXSceneButton = null;
	}
	
	private void setStateSelected() {
		Bitmap bm = ImageUtils.getDiskBitmap(imageOn);
		if((null != bm) && (null != mImageView)) {
			mImageView.setImageBitmap(bm);
//			Message msg = new Message();
//			msg.what = 1;
//			msg.obj = bm;
//			this.mHandler.sendMessage(msg);
		}
	}
	
	private void setStateNotSelected() {
		Bitmap bm = ImageUtils.getDiskBitmap(imageOff);
		if((null != bm) && (null != mImageView)) {
			mImageView.setImageBitmap(bm);
//			Message msg = new Message();
//			msg.what = 1;
//			msg.obj = bm;
//			this.mHandler.sendMessage(msg);
		}
	}
	
	private void selecte(boolean select) {
		if(select) {
			setStateSelected();
		} else {
			setStateNotSelected();
		}
	}
	
	public void setSelected(final boolean s) {
//		Message msg = new Message();
//		if(s) {
//			msg.what = 1;
//		} else {
//			msg.what = 2;
//		}
//		this.mHandler.sendMessage(msg);
		
		selecte(s);

//		invalidate();
	}
    
    private void onClick() {
    	if(null == this.mKNXSceneButton) {
    		return;
    	}
    	  
    	if(EBool.Yes == this.mKNXSceneButton.getIsGroup()) {
    		if(this.getParent() instanceof STKNXGroupBox) {
    			STKNXGroupBox mSTKNXGroupBox = (STKNXGroupBox)this.getParent();
    			if((null != mSTKNXGroupBox) && (null != mSTKNXGroupBox.mKNXGroupBox)) {
    				if(mSTKNXGroupBox.mKNXGroupBox.getReadAddressId().isEmpty() ||
    						mSTKNXGroupBox.mKNXGroupBox.getWriteAddressIds().containsKey(
    							mSTKNXGroupBox.mKNXGroupBox.getReadAddressId().keySet().toArray()[0])) {
    					mSTKNXGroupBox.setSelectedValue(this.mKNXSceneButton.DefaultValue);
    				}

    				sendCommandRequest(mSTKNXGroupBox.mKNXGroupBox.getWriteAddressIds(), this.mKNXSceneButton.DefaultValue+"", false, null);
    			}
    		}
    	} else {
    		if(this.mKNXSceneButton.getReadAddressId().isEmpty() ||
    				this.mKNXSceneButton.getWriteAddressIds().containsKey(
    						this.mKNXSceneButton.getReadAddressId().keySet().toArray()[0])) {
    			if(SceneState.On == this.mSceneState) {
    				this.selecte(false);
    			} else {
    				this.selecte(true);
    			}
    		}
    		
    		sendCommandRequest(this.mKNXSceneButton.getWriteAddressIds(), "", true, null);
    	}
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    	measureChildren(widthMeasureSpec, heightMeasureSpec);
    	
        /**
         * 最后调用父类方法,把View的大小告诉父布局。
         */
        setMeasuredDimension(this.mKNXSceneButton.Width, this.mKNXSceneButton.Height);
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
				int imgHeight = this.mKNXSceneButton.Height - 2 * this.imgY;
				
				cl = this.PADDING;
				ct = this.PADDING;
				cr = this.PADDING+imgHeight;
				cb = this.PADDING+imgHeight;
			}
			view.layout(cl, ct, cr, cb);
		}
	}

    @SuppressLint("DrawAllocation")
	@Override
    protected void onDraw(Canvas canvas) {
    	super.onDraw(canvas);
  
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

        if(null != this.mKNXSceneButton.getText()) {
        	int x = 0;
        	int y = 0;
        	Rect bound = new Rect();
        	paint.getTextBounds(this.mKNXSceneButton.getText(), 0, this.mKNXSceneButton.getText().length(), bound);
        	if(StringUtil.isEmpty(this.imageOn) && StringUtil.isEmpty(this.imageOff)) {
        		x = (getWidth() - 2 *x - bound.width())/2;
            	y = (getHeight()  + bound.height())/2;
        	} else {
        		x=(getWidth()- (this.imgRight+this.PADDING)-this.PADDING -bound.width())/2+this.imgRight+this.PADDING;
        		y=(getHeight()  + bound.height())/2;
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
        		canvas.drawRoundRect(oval3, this.mKNXSceneButton.Radius, this.mKNXSceneButton.Radius, paint);	//第二个参数是x半径，第三个参数是y半径  
        		break;
        	case Normal:
        		break;
        }
        
        if(EBool.Yes == this.mKNXSceneButton.getDisplayBorder()) {
    		paint.reset();
    		paint.setStyle(Paint.Style.STROKE);
    		paint.setColor(Color.parseColor(this.mKNXSceneButton.BorderColor));
    		canvas.drawRoundRect(oval3, this.mKNXSceneButton.Radius, this.mKNXSceneButton.Radius, paint);//第二个参数是x半径，第三个参数是y半径  
    	}
    }
      
    @SuppressLint("ClickableViewAccessibility")
	@Override
    public boolean onTouchEvent(MotionEvent event) {
    	switch (event.getAction()) { 
    		case MotionEvent.ACTION_DOWN: 
    			this.mControlState = ControlState.Down;
    			invalidate();
    			if(null != this.mImageView) {
    				this.mImageView.setAlpha(0.6f);
    			}
    			break; 
    		case MotionEvent.ACTION_UP: 
    			onClick();
    			this.mControlState = ControlState.Normal;
    			invalidate();
    			if(null != this.mImageView) {
    				this.mImageView.setAlpha(1.0f);
    			}
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
