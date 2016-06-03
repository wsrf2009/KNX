package com.sation.knxcontroller.widget;

import com.sation.knxcontroller.STKNXControllerConstant;
import com.sation.knxcontroller.control.KNXSliderSwitch;
import com.sation.knxcontroller.models.KNXView.EFlatStyle;
import com.sation.knxcontroller.util.ColorUtils;
import com.sation.knxcontroller.util.ImageUtils;
import com.sation.knxcontroller.util.Log;
import com.sation.knxcontroller.util.MathUtils;
import com.sation.knxcontroller.util.StringUtil;
import com.sation.knxcontroller.widget.ViewSlider.EControlState;

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
import android.support.v4.widget.ViewDragHelper;
import android.view.MotionEvent;
import android.view.View;

public class STKNXSliderSwitch extends STKNXControl {
	private final int PADDING = 5;
    private final int SLIDER_EDGE_WIDTH = 3;
    private final int SLIDER_WIDTH = 40;
	
	private KNXSliderSwitch mKNXSliderSwitch;
	private int leftImgX = 0;
    private int leftImgY = 0;
    private int leftImgRight = 0;
    private int leftImgBottom = 0;
    private int rightImgX = 0;
    private int rightImgY = 0;
    private int rightImgRight = 0;
    private int rightImgBottom = 0;
    private Bitmap imgLeft;
    private Bitmap imgRight;
    
    private ViewDragHelper mViewDragHelper;
    private int sliderPositionX = 100;
//    private int sliderWidth = 40;
    private enum ESliderState {
    	Down,
    	Dragging,
//    	Up,
    	Normal,
    }
    private ESliderState mSliderState;
    private float progress;
    private int[] pos;
    private int curPos;

	public STKNXSliderSwitch(Context context, KNXSliderSwitch knxSliderSwitch) {
		super(context, knxSliderSwitch);

		this.mKNXSliderSwitch = knxSliderSwitch;
//		this.setTag(this.mKNXSliderSwitch.getId());
		this.setId(this.mKNXSliderSwitch.getId());
		this.mSliderState = ESliderState.Normal;
		
		if(!StringUtil.isEmpty(this.mKNXSliderSwitch.getLeftImage())) {
			this.imgLeft = ImageUtils.getDiskBitmap(STKNXControllerConstant.ConfigResImgPath + this.mKNXSliderSwitch.getLeftImage());
		}
			
		if(!StringUtil.isEmpty(mKNXSliderSwitch.getRightImage())) {
			this.imgRight = ImageUtils.getDiskBitmap(STKNXControllerConstant.ConfigResImgPath + this.mKNXSliderSwitch.getRightImage());
		}
		
		if(this.mKNXSliderSwitch.IsRelativeControl) {
			pos = new int[16];
			int range = getThumbScrollRange();
			int interval = range/15;
			for(int i=0; i<16; i++) {
				pos[i] = i*interval;
			}
			pos[15] = range;
			
			this.curPos = 8;
			this.sliderPositionX = this.pos[this.curPos];
		}
		
		this.mViewDragHelper = ViewDragHelper.create(this, 1.0f, new ViewDragHelper.Callback() {
			
			@Override
			public boolean tryCaptureView(View arg0, int arg1) {
//				mSliderState = ESliderState.Dragging;
				return true;
			}
			
			@Override
			public int clampViewPositionHorizontal(View child, int left, int dx) {
				final int leftBound = getPaddingLeft();
                final int rightBound = getWidth() - child.getWidth() - leftBound;

                sliderPositionX = Math.min(Math.max(left, leftBound), rightBound);
                if(mKNXSliderSwitch.IsRelativeControl) {
                	curPos = MathUtils.getTheClosetIndex(sliderPositionX, pos);
                	sliderPositionX = pos[curPos];
                }
                
                return sliderPositionX;
			} 
			
			@Override
			public int clampViewPositionVertical(View child, int top, int dy) {
				return 0;
			}
			
			@Override
			public void onViewCaptured(View capturedChild, int activePointerId) {
//				mSliderState = ESliderState.Down;
				ViewSlider slider = (ViewSlider)capturedChild;
				slider.mEControlState = EControlState.Down;
				slider.invalidate();
			}
			
			@Override
			public void onViewReleased(View releasedChild, float xvel, float yvel) {
//				mSliderState = ESliderState.Normal;
				ViewSlider slider = (ViewSlider)releasedChild;
				slider.mEControlState = EControlState.Up;
				slider.invalidate();
			}
		});

		ViewSlider slider = new ViewSlider(context);
		slider.width = this.SLIDER_WIDTH;
		slider.height = this.mKNXSliderSwitch.Height;
		slider.backColor = Color.parseColor(this.mKNXSliderSwitch.BackgroundColor);
		slider.radius = this.mKNXSliderSwitch.Radius;
		slider.alpha = this.mKNXSliderSwitch.Alpha;
		this.addView(slider);
	}
	
	private int getThumbScrollRange() { 
		return this.mKNXSliderSwitch.Width-this.SLIDER_WIDTH;
	} 
	
	public void setProgress(int p) {
		if(!this.mKNXSliderSwitch.IsRelativeControl) { 
			/* 绝对调光 */
			int thumbRange = getThumbScrollRange();
			this.progress = (float)p/255;
			this.sliderPositionX = (int)(this.progress * thumbRange);
		}
		
		requestLayout();
	}
	
	private void onClick() {
		if(this.mKNXSliderSwitch.IsRelativeControl) {
			int val = 0;
			 if (this.curPos < 8) {
				val = this.curPos;
			} else if (this.curPos <= 15) {
				val = 8 +15-this.curPos;
			}
			 sendCommandRequest(this.mKNXSliderSwitch.getWriteAddressIds(), val+"", false, null);
		} else {
			sendCommandRequest(this.mKNXSliderSwitch.getWriteAddressIds(), String.valueOf((int)(this.progress * 255 / 100)), false, null);
		}
	}
	
	@Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return this.mViewDragHelper.shouldInterceptTouchEvent(event);
    }

	@Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		measureChildren(widthMeasureSpec, heightMeasureSpec);

        /* 计算图片的显示位置和大小 */
		this.leftImgX = this.PADDING;
		this.leftImgY = this.SLIDER_EDGE_WIDTH + this.PADDING;
		int height = this.mKNXSliderSwitch.Height - 2 * this.leftImgY;
		int width = height;
        this.leftImgRight = this.leftImgX + width;
        this.leftImgBottom = this.leftImgY + height;
        
        this.rightImgX = this.mKNXSliderSwitch.Width - this.PADDING - width;
        this.rightImgY = this.leftImgY;
        this.rightImgRight = this.rightImgX + width;
        this.rightImgBottom = this.leftImgBottom;
        
        /**
         * 最后调用父类方法,把View的大小告诉父布局。
         */
        setMeasuredDimension(this.mKNXSliderSwitch.Width, this.mKNXSliderSwitch.Height);
    }
	
    @Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int cCount = getChildCount();
		
		if(this.mKNXSliderSwitch.IsRelativeControl) {
			curPos = MathUtils.getTheClosetIndex(sliderPositionX, pos);
			sliderPositionX = pos[curPos];
		}
		
		/*
		 * 遍历所有childView根据其宽和高，以及margin进行布局
		 */
		for (int i = 0; i < cCount; i++) {
			View childView = (View)getChildAt(i);

			int cl = 0, ct = 0, cr = 0, cb = 0;
			cl = this.sliderPositionX;
			ct = 0; 
			cr = cl+this.SLIDER_WIDTH;
			cb = ct+this.getHeight();

			childView.layout(cl, ct, cr, cb);
		}
	}

    @SuppressLint("DrawAllocation")
	@Override
    protected void onDraw(Canvas canvas) {
    	super.onDraw(canvas);

    	int backColor = Color.parseColor(this.mKNXSliderSwitch.BackgroundColor);
    	Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    	paint.setStyle(Paint.Style.FILL_AND_STROKE);	// 充满  
    	paint.setAlpha((int)(this.mKNXSliderSwitch.Alpha*255));
    	
        int x = 0;
        int y = this.SLIDER_EDGE_WIDTH;  // 
        int width = this.getWidth();
        int height = this.getHeight() - 2 * y;
        RectF rect1 = new RectF(x, y, x+width, y+height);
    	if(EFlatStyle.Stereo == this.mKNXSliderSwitch.getFlatStyle()) {	// 画立体感的圆角矩形 
        
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
    		paint.setARGB((int)(this.mKNXSliderSwitch.Alpha*255), Color.red(backColor), Color.green(backColor), Color.blue(backColor));
    	}
    	canvas.drawRoundRect(rect1, this.mKNXSliderSwitch.Radius, this.mKNXSliderSwitch.Radius, paint);//第二个参数是x半径，第三个参数是y半径  
    	
    	/* 左图标 */
        if (null != this.imgLeft) {
            Rect resRect = new Rect(0, 0, this.imgLeft.getWidth(), this.imgRight.getHeight());
            Rect desRect = new Rect(this.leftImgX, this.leftImgY, this.leftImgRight, this.leftImgBottom);
            canvas.drawBitmap(this.imgLeft, resRect, desRect, paint);
        }

        /* 右图标 */
        if (null != this.imgRight) {
            Rect resRect = new Rect(0, 0, this.imgRight.getWidth(), this.imgRight.getHeight());
            Rect desRect = new Rect(this.rightImgX, this.rightImgY, this.rightImgRight, this.rightImgBottom);
            canvas.drawBitmap(this.imgRight, resRect, desRect, paint);
        }
    }
    
    @SuppressLint("ClickableViewAccessibility")
	@Override
    public boolean onTouchEvent(MotionEvent event) {
    	this.mViewDragHelper.processTouchEvent(event);
    	final float x = event.getX();

    	switch (event.getAction()) { 
    		case MotionEvent.ACTION_DOWN: 
    			if(ESliderState.Normal == mSliderState) {
    				this.sliderPositionX = (int)x-this.SLIDER_WIDTH/2;
    				if(this.sliderPositionX < 0) {
    					this.sliderPositionX = 0;
    				} else if (this.sliderPositionX > (this.getWidth() - this.SLIDER_WIDTH)){
    					this.sliderPositionX = this.getWidth() - this.SLIDER_WIDTH;
    				}
    				
    				this.requestLayout();
    			}
    			break; 
    		case MotionEvent.ACTION_UP:
    			int range = getThumbScrollRange();
    			this.progress = 100*this.sliderPositionX/range;
    			Log.e("STKNXSliderSwitch", "sliderPositionX==>"+sliderPositionX+" range==>"+range+" progress==>"+progress);
    			this.onClick();
    			break;
    			
    		case MotionEvent.ACTION_MOVE:
    			break;
    		case MotionEvent.ACTION_CANCEL:
    			break;
    			
    		default:
    			break;
    	}
    	 
    	return true;
    }
}

class ViewSlider extends View {
	public int width;
	public int height;
	public int backColor;
	public int radius;
	public double alpha;
	
	public enum EControlState {
		Down,
		Up,
	}
	public EControlState mEControlState;

	protected ViewSlider(Context context) {
		super(context);
		
		this.mEControlState = EControlState.Up;
	}
	
	@Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		
        /**
         * 最后调用父类方法,把View的大小告诉父布局。
         */
        setMeasuredDimension(this.width, this.height);
    }

	@SuppressLint({ "DrawAllocation", "ClickableViewAccessibility" })
	@Override
    protected void onDraw(Canvas canvas) {
    	super.onDraw(canvas);

    	Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    	paint.setStyle(Paint.Style.FILL_AND_STROKE);	// 充满  
    	paint.setAlpha((int)(this.alpha*255));
    	RectF oval3 = new RectF(0, 0, this.getWidth(), this.getHeight());
    	
    	/* 绘制滑块 */
        int sliderColor = ColorUtils.changeBrightnessOfColor(this.backColor, 70);
        int[] sliderColors = new int[3];
        sliderColors[0] = ColorUtils.changeBrightnessOfColor(sliderColor, 100);
        sliderColors[1] = sliderColor;
        sliderColors[2] = ColorUtils.changeBrightnessOfColor(sliderColor, -30);
        
        float sliderPositions[] = new float[3];
        sliderPositions[0] = .0f;
        sliderPositions[1] = .3f;
        sliderPositions[2] = 1.0f;
        
        Shader sliderShader = new LinearGradient(0, 0, 0, getHeight(), 
        		sliderColors, sliderPositions, Shader.TileMode.CLAMP); // 设置渐变色 这个正方形的颜色是改变的 , 一个材质,打造出一个线性梯度沿著一条线。  
    	paint.setShader(sliderShader);  
    	canvas.drawRoundRect(oval3, this.radius, this.radius, paint);
    	
    	switch (this.mEControlState) {
			case Down:
				paint.reset();
				paint.setStyle(Paint.Style.FILL);
				paint.setColor(Color.parseColor("#FF6100"));
				paint.setAlpha(0x60);
				canvas.drawRoundRect(oval3, 5, 5, paint);	//第二个参数是x半径，第三个参数是y半径  
				break;
			case Up:
				break;
    	}
	}
	
//	@Override
//    public boolean onTouchEvent(MotionEvent event) {
//    	switch (event.getAction()) { 
//    		case MotionEvent.ACTION_DOWN:
//    			this.mEControlState = EControlState.Down;
//    			this.invalidate();
//    			break; 
//    		case MotionEvent.ACTION_UP:
//    			this.mEControlState = EControlState.Up;
////    			if(null != this.mViewControlClickListener) {
////    				this.mViewControlClickListener.onClick(this);
////    			}
//    			this.invalidate();
//    			break;
//    		case MotionEvent.ACTION_MOVE:
//    			break;
//    		case MotionEvent.ACTION_CANCEL:
//    			this.mEControlState = EControlState.Up;
//    			this.invalidate();
//    			break;
//    			
//    		default:
//    			break;
//    	}
//    	
//    	return true;
//    }
}
