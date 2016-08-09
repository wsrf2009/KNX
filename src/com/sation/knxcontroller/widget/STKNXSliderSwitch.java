package com.sation.knxcontroller.widget;

import com.sation.knxcontroller.STKNXControllerConstant;
import com.sation.knxcontroller.control.KNXSliderSwitch;
import com.sation.knxcontroller.models.KNXView.EBool;
import com.sation.knxcontroller.models.KNXView.EFlatStyle;
import com.sation.knxcontroller.util.ColorUtils;
import com.sation.knxcontroller.util.ImageUtils;
import com.sation.knxcontroller.util.MathUtils;
import com.sation.knxcontroller.util.StringUtil;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;

import android.support.v4.view.MotionEventCompat;
import android.support.v4.widget.ViewDragHelper;
import android.view.MotionEvent;
import android.view.View;


public class STKNXSliderSwitch extends STKNXControl {
	private static final int PADDING = 2;
	private static final int SUBVIEW_WIDTH = 40;
    private final int SLIDER_EDGE_WIDTH = 3;
    private final int SLIDER_WIDTH = 40;
	
    private KNXSliderSwitch mKNXSliderSwitch;
    private STSlider slider;
    private ViewDragHelper mViewDragHelper;
    private int sliderPositionX;
    private enum ESliderState {
    	Down,
    	Dragging,
    	Normal,
    }
    private ESliderState mSliderState;
    private int sliderStartPos;
    private int sliderWidth;
    private float progress;
    private int[] pos = new int[16];;
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
		vLeft.backColor = Color.parseColor(this.mKNXSliderSwitch.BackgroundColor);
		vLeft.radius = this.mKNXSliderSwitch.Radius;
		vLeft.alpha = this.mKNXSliderSwitch.Alpha;
		vRight.setSubViewClickListener(this.rightClicked);
		if(!StringUtil.isEmpty(this.mKNXSliderSwitch.getRightImage())) {
			vRight.backImage = ImageUtils.getDiskBitmap(STKNXControllerConstant.ConfigResImgPath + this.mKNXSliderSwitch.getRightImage());
		}
		this.addView(vRight);
		
		this.sliderStartPos = vLeft.left+vLeft.width+STKNXSliderSwitch.PADDING;
		this.sliderWidth = this.mKNXSliderSwitch.Width-this.sliderStartPos*2;
		this.sliderPositionX = this.sliderStartPos;
		
		if(EBool.Yes == this.mKNXSliderSwitch.getIsRelativeControl()) {
			int range = getThumbScrollRange();
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
				return arg0 instanceof STSlider;
			}
			
			@Override
	        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
//	            invalidate();
	        }

	        @Override
	        public void onEdgeTouched(int edgeFlags, int pointerId) {
	            super.onEdgeTouched(edgeFlags, pointerId);
	        }

	        @Override
	        public void onEdgeDragStarted(int edgeFlags, int pointerId) {
	            mViewDragHelper.captureChildView(slider, pointerId);
	        }
			
			@Override
			public int clampViewPositionHorizontal(View child, int left, int dx) {
				mSliderState = ESliderState.Dragging;
                return sliderPositionX = getSliderPosX(left);
			} 
			
			@Override
			public int clampViewPositionVertical(View child, int top, int dy) {
				mSliderState = ESliderState.Dragging;
				return super.clampViewPositionVertical(child, top, dy);
			}
			
			@Override
			public void onViewCaptured(View capturedChild, int activePointerId) {
				super.onViewCaptured(capturedChild, activePointerId);
				STSlider slider = (STSlider)capturedChild;
				slider.mEControlState = STSlider.EControlState.Down;
				mSliderState = ESliderState.Down;
			}
			
			@Override
			public void onViewReleased(View releasedChild, float xvel, float yvel) {
				super.onViewReleased(releasedChild, xvel, yvel);
				STSlider slider = (STSlider)releasedChild;
				slider.mEControlState = STSlider.EControlState.Up;
				sliderChanged();
				mSliderState = ESliderState.Normal;
			}
			
			@Override
			public int getViewHorizontalDragRange(View child){
				return getThumbScrollRange();
			}
			
			@Override
			public int getViewVerticalDragRange(View child){
				return 0;
			}
		});

		slider = new STSlider(context);
		slider.backColor = Color.parseColor(this.mKNXSliderSwitch.BackgroundColor);
		slider.radius = this.mKNXSliderSwitch.Radius;
		slider.alpha = this.mKNXSliderSwitch.Alpha;
		
		LayoutParams pageLayoutParams = new LayoutParams(this.SLIDER_WIDTH, this.mKNXSliderSwitch.Height); 
		slider.setLayoutParams(pageLayoutParams);
		this.addView(slider);
	}
	
	private int getThumbScrollRange() { 
		return this.sliderWidth-this.SLIDER_WIDTH;
	}
	
	private int getSliderStartPos() {
		return this.sliderStartPos;
	}
	
	private int getSliderEndPos(){
		return this.getSliderStartPos()+this.getThumbScrollRange();
	}
	
	private int getSliderPos(float progress){
		int pos = (int)(progress * getThumbScrollRange());
		return this.sliderStartPos+pos;
	}
	
	private int getSliderPosX(int position) {
		this.sliderPositionX = Math.max(getSliderStartPos(), position);
        sliderPositionX = Math.min(sliderPositionX, getSliderEndPos());
        if(EBool.Yes == mKNXSliderSwitch.getIsRelativeControl()) {
        	curPos = MathUtils.getTheClosetIndex(sliderPositionX, pos);
        	sliderPositionX = pos[curPos];
        }
        
        return sliderPositionX;
	}
	
	public void setProgress(int p) {
		if(EBool.Yes == this.mKNXSliderSwitch.getIsRelativeControl()) { 
			/* 相对调光 */
			this.sliderPositionX = this.pos[8];
		} else {
			/* 绝对调光 */
			this.progress = (float)p/255;
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
			
			progress = (float)(sliderPositionX-this.getSliderStartPos())/this.getThumbScrollRange();
    		if(progress>1){
    			progress = 1;
    		}
    		
			sendCommandRequest(this.mKNXSliderSwitch.getWriteAddressIds(), String.valueOf((int)(this.progress*255)), false, null);
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
		final int action = MotionEventCompat.getActionMasked(event);
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            this.mViewDragHelper.cancel();
            return false;
        }
        return this.mViewDragHelper.shouldInterceptTouchEvent(event);
    }

    @Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int cCount = getChildCount();

		/*
		 * 遍历所有childView根据其宽和高，以及margin进行布局
		 */
		for (int i = 0; i < cCount; i++) {
			View view = (View)getChildAt(i);

			int cl = 0, ct = 0, cr = 0, cb = 0;
			if(view instanceof STSlider) {
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
    }
    
    @SuppressLint("ClickableViewAccessibility")
	@Override
    public boolean onTouchEvent(MotionEvent event) {
    	this.mViewDragHelper.processTouchEvent(event);
    	final float x = event.getX();

    	if((x>=this.sliderStartPos) && (x<=(this.getSliderEndPos()+this.SLIDER_WIDTH)) &&
    			(ESliderState.Normal == this.mSliderState)) {
    		switch (event.getAction()) { 
    			case MotionEvent.ACTION_DOWN: 
    				this.sliderPositionX = getSliderPosX((int)x-this.SLIDER_WIDTH/2);
    				this.slider.requestLayout();
    				break;
    			case MotionEvent.ACTION_UP:
    				this.sliderChanged();
    				break;
    			
    			case MotionEvent.ACTION_MOVE:
    				this.sliderPositionX = getSliderPosX((int)x-this.SLIDER_WIDTH/2);
    				this.slider.requestLayout();
    				break;
    			
    			default:
    				break;
    		}
    	}
    	 
    	return true;
    }
}

