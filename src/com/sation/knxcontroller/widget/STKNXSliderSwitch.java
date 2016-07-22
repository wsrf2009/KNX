package com.sation.knxcontroller.widget;

import com.sation.knxcontroller.STKNXControllerConstant;
import com.sation.knxcontroller.control.KNXSliderSwitch;
import com.sation.knxcontroller.models.KNXView.EBool;
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
	private static final int PADDING = 2;
	private static final int SUBVIEW_WIDTH = 40;
    private final int SLIDER_EDGE_WIDTH = 3;
    private final int SLIDER_WIDTH = 50;
	
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
    private enum ESliderState {
    	Down,
    	Dragging,
    	Normal,
    }
    private ESliderState mSliderState;
    private int sliderStartPos;
    private int sliderWidth;
    private float progress;
    private int[] pos;
    private int curPos;

	public STKNXSliderSwitch(Context context, KNXSliderSwitch knxSliderSwitch) {
		super(context, knxSliderSwitch);

		this.mKNXSliderSwitch = knxSliderSwitch;
		this.setId(this.mKNXSliderSwitch.getId());
		this.mSliderState = ESliderState.Normal;

		STButton vLeft = new STButton(context);
		vLeft.width = Math.max(this.mKNXSliderSwitch.Height-2*STKNXSliderSwitch.PADDING, STKNXSliderSwitch.SUBVIEW_WIDTH);
		vLeft.height = this.mKNXSliderSwitch.Height-2*STKNXSliderSwitch.PADDING;
		vLeft.left = STKNXSliderSwitch.PADDING;
		vLeft.top = STKNXSliderSwitch.PADDING;
		vLeft.backColor = Color.parseColor(this.mKNXSliderSwitch.BackgroundColor);
		vLeft.radius = this.mKNXSliderSwitch.Radius;
		vLeft.alpha = this.mKNXSliderSwitch.Alpha;
		vLeft.setSubViewClickListener(this.leftClicked);
		if(!StringUtil.isEmpty(this.mKNXSliderSwitch.getLeftImage())) {
			vLeft.backImage = ImageUtils.getDiskBitmap(STKNXControllerConstant.ConfigResImgPath + this.mKNXSliderSwitch.getLeftImage());
		}
		this.addView(vLeft);
		
		STButton vRight = new STButton(context);
		vRight.width = Math.max(this.mKNXSliderSwitch.Height-2*STKNXSliderSwitch.PADDING, STKNXSliderSwitch.SUBVIEW_WIDTH);
		vRight.height = this.mKNXSliderSwitch.Height-2*STKNXSliderSwitch.PADDING;
		vRight.left = this.mKNXSliderSwitch.Width - STKNXSliderSwitch.PADDING - vRight.width;
		vRight.top = STKNXSliderSwitch.PADDING;
		vRight.backColor = Color.parseColor(this.mKNXSliderSwitch.BackgroundColor);
		vRight.radius = this.mKNXSliderSwitch.Radius;
		vRight.alpha = this.mKNXSliderSwitch.Alpha;
		vRight.setSubViewClickListener(this.rightClicked);
		if(!StringUtil.isEmpty(this.mKNXSliderSwitch.getRightImage())) {
			vRight.backImage = ImageUtils.getDiskBitmap(STKNXControllerConstant.ConfigResImgPath + this.mKNXSliderSwitch.getRightImage());
		}
		this.addView(vRight);
		
		this.sliderStartPos = vLeft.left+vLeft.width+STKNXSliderSwitch.PADDING;
		this.sliderWidth = this.mKNXSliderSwitch.Width-this.sliderStartPos*2;
		
		if(EBool.Yes == this.mKNXSliderSwitch.getIsRelativeControl()) {
			pos = new int[16];
			int range = getThumbScrollRange();
//			int range = this.sliderWidth;
			int interval = range/15;
			for(int i=0; i<16; i++) {
				pos[i] = this.sliderStartPos+ i*interval;
			}
			pos[15] = this.sliderStartPos+range;
			
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

                int range = getThumbScrollRange();
                sliderPositionX = Math.min(Math.max(left, leftBound), sliderStartPos+range);
                sliderPositionX = Math.max(sliderPositionX, sliderStartPos);
                if(EBool.Yes == mKNXSliderSwitch.getIsRelativeControl()) {
                	curPos = MathUtils.getTheClosetIndex(sliderPositionX, pos);
                	sliderPositionX = pos[curPos];
                }
                
//                if(EBool.Yes == mKNXSliderSwitch.getIsRelativeControl()) { 
//
//        		} else {
        			
        			progress = (float)(sliderPositionX-sliderStartPos)/range;
        			if(progress>1){
        				progress = 1;
        			}
//        		}
        				sliderChanged();
        				
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
//		return this.mKNXSliderSwitch.Width-this.SLIDER_WIDTH;
		return this.sliderWidth-this.SLIDER_WIDTH;
	}
	
	private int getSliderPos(float progress){
		int pos = (int)(progress * getThumbScrollRange());
		return this.sliderStartPos+pos;
	}
	
	public void setProgress(int p) {
		if(EBool.Yes == this.mKNXSliderSwitch.getIsRelativeControl()) { 
			/* 相对调光 */
			this.sliderPositionX = this.pos[8];
		} else {
			/* 绝对调光 */
//			int thumbRange = getThumbScrollRange();
			this.progress = (float)p/255;
//			this.sliderPositionX = (int)(this.progress * thumbRange);
			this.sliderPositionX = getSliderPos(this.progress);
		}
		
		requestLayout();
	}
	
	private void sliderChanged() {
		if(EBool.Yes == this.mKNXSliderSwitch.getIsRelativeControl()) {
			int val = 0;
			 if (this.curPos < 8) {
				val = this.curPos;
			} else if (this.curPos <= 15) {
				val = 8 +15-this.curPos;
			}
			
			 sendCommandRequest(this.mKNXSliderSwitch.getWriteAddressIds(), val+"", false, null);
		} else {
			sendCommandRequest(this.mKNXSliderSwitch.getWriteAddressIds(), String.valueOf((int)(this.progress*255/*this.progress * 255 / 100*/)), false, null);
		}
	}
	
	STButton.SubViewClickListener leftClicked = new STButton.SubViewClickListener() {

		@Override
		public void onClick(STButton view) {
			if(EBool.Yes == mKNXSliderSwitch.getIsRelativeControl()) {
				curPos--;
				if(curPos<0){
					curPos = 0;
				} else if(curPos>15){
					curPos = 15;
				}
				sliderPositionX = pos[curPos];
			} else {
				progress -= 0.1f;
				
				if(progress < .0f){
					progress = .0f;
				} else if(progress>1.0f){
					progress = 1.0f;
				}
				
				sliderPositionX = getSliderPos(progress);
			}

			requestLayout();
			
			sliderChanged();
		}
	};
	
	STButton.SubViewClickListener rightClicked = new STButton.SubViewClickListener() {

		@Override
		public void onClick(STButton view) {
			if(EBool.Yes == mKNXSliderSwitch.getIsRelativeControl()) {
				curPos++;
				if(curPos<0){
					curPos = 0;
				} else if(curPos>15){
					curPos = 15;
				}
				sliderPositionX = pos[curPos];
			} else {
				progress += 0.1f;
				
				if(progress < .0f){
					progress = .0f;
				} else if(progress>1.0f){
					progress = 1.0f;
				}
				
				sliderPositionX = getSliderPos(progress);
			}

			requestLayout();
			
			sliderChanged();
		}
	};
	
	@Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return this.mViewDragHelper.shouldInterceptTouchEvent(event);
    }

	@Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		measureChildren(widthMeasureSpec, heightMeasureSpec);

//        /* 计算图片的显示位置和大小 */
//		this.leftImgX = this.PADDING;
//		this.leftImgY = this.SLIDER_EDGE_WIDTH + this.PADDING;
//		int height = this.mKNXSliderSwitch.Height - 2 * this.leftImgY;
//		int width = height;
//        this.leftImgRight = this.leftImgX + width;
//        this.leftImgBottom = this.leftImgY + height;
//        
//        this.rightImgX = this.mKNXSliderSwitch.Width - this.PADDING - width;
//        this.rightImgY = this.leftImgY;
//        this.rightImgRight = this.rightImgX + width;
//        this.rightImgBottom = this.leftImgBottom;
        
        /**
         * 最后调用父类方法,把View的大小告诉父布局。
         */
        setMeasuredDimension(this.mKNXSliderSwitch.Width, this.mKNXSliderSwitch.Height);
    }
	
    @Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int cCount = getChildCount();
		
		if(EBool.Yes == this.mKNXSliderSwitch.getIsRelativeControl()) {
			curPos = MathUtils.getTheClosetIndex(sliderPositionX, pos);
			sliderPositionX = pos[curPos];
		}
		
		/*
		 * 遍历所有childView根据其宽和高，以及margin进行布局
		 */
		for (int i = 0; i < cCount; i++) {
			View view = (View)getChildAt(i);

			int cl = 0, ct = 0, cr = 0, cb = 0;
			if(view instanceof ViewSlider) {
				cl = this.sliderPositionX;
				ct = 0; 
				cr = cl+this.SLIDER_WIDTH;
				cb = ct+this.getHeight();
			} else if(view instanceof STButton){
				STButton button = (STButton)view;
				cl = button.left;
				ct = button.top;
				cr = button.left+button.width;
				cb = button.top+button.height;
			}

			view.layout(cl, ct, cr, cb);
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

//    	paint.reset();
//    	/* 左图标 */
//        if (null != this.imgLeft) {
//            Rect resRect = new Rect(0, 0, this.imgLeft.getWidth(), this.imgRight.getHeight());
//            Rect desRect = new Rect(this.leftImgX, this.leftImgY, this.leftImgRight, this.leftImgBottom);
//            canvas.drawBitmap(this.imgLeft, resRect, desRect, paint);
//        }
//
//        /* 右图标 */
//        if (null != this.imgRight) {
//            Rect resRect = new Rect(0, 0, this.imgRight.getWidth(), this.imgRight.getHeight());
//            Rect desRect = new Rect(this.rightImgX, this.rightImgY, this.rightImgRight, this.rightImgBottom);
//            canvas.drawBitmap(this.imgRight, resRect, desRect, paint);
//        }
    }
    
    @SuppressLint("ClickableViewAccessibility")
	@Override
    public boolean onTouchEvent(MotionEvent event) {
    	this.mViewDragHelper.processTouchEvent(event);
    	final float x = event.getX();

    	switch (event.getAction()) { 
    		case MotionEvent.ACTION_DOWN: 
    			if(ESliderState.Normal == mSliderState) {
//    				this.sliderPositionX = (int)x-this.SLIDER_WIDTH/2;
//    				if(this.sliderPositionX < 0) {
//    					this.sliderPositionX = 0;
//    				} else if (this.sliderPositionX > (this.getWidth() - this.SLIDER_WIDTH)){
//    					this.sliderPositionX = this.getWidth() - this.SLIDER_WIDTH;
//    				}
//    				if((x>this.sliderStartPos) && (x<(this.sliderStartPos+this.sliderWidth))){
//    					this.sliderPositionX = (int)((x-this.SLIDER_WIDTH)/2);
//    					
//    				}
//    				
//    				this.requestLayout();
    			}
    			break; 
    		case MotionEvent.ACTION_UP:
    			int range = getThumbScrollRange();
//    			this.progress = 100*this.sliderPositionX/range;
    			this.progress = (this.sliderPositionX-this.sliderStartPos)/range;
    			Log.e("STKNXSliderSwitch", "sliderPositionX==>"+sliderPositionX+" range==>"+range+" progress==>"+progress);
    			this.sliderChanged();
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
				canvas.drawRoundRect(oval3, this.radius, this.radius, paint);	//第二个参数是x半径，第三个参数是y半径  
				break;
			case Up:
				break;
    	}
	}
}
