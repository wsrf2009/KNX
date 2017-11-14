package com.sation.knxcontroller.widget;

import com.sation.knxcontroller.STKNXControllerApp;
import com.sation.knxcontroller.STKNXControllerConstant;
import com.sation.knxcontroller.control.KNXSliderSwitch;
import com.sation.knxcontroller.knxdpt.DPT5;
import com.sation.knxcontroller.knxdpt.KNXDatapointType;
import com.sation.knxcontroller.models.KNXGroupAddress;
import com.sation.knxcontroller.models.KNXSelectedAddress;
import com.sation.knxcontroller.models.KNXView;
import com.sation.knxcontroller.models.KNXView.EBool;
import com.sation.knxcontroller.models.KNXView.EFlatStyle;
import com.sation.knxcontroller.util.ColorUtils;
import com.sation.knxcontroller.util.Log;
import com.sation.knxcontroller.util.MapUtils;
import com.sation.knxcontroller.util.MathUtils;
import com.sation.knxcontroller.util.StringUtil;
import com.sation.knxcontroller.util.uikit.UIKit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.widget.ViewDragHelper;
import android.view.MotionEvent;
import android.view.View;

import java.lang.ref.WeakReference;


public class STKNXSliderSwitch extends STKNXControl {
	private static final String TAG = "STKNXSliderSwitch";
	private static final int PADDING = 2;
	private static final int SUBVIEW_WIDTH = 40;
    private final int SLIDER_EDGE_WIDTH = 3;
	
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

		/* 第一个按钮 左边/下边的 */
		STButton vLeft = new STButton(context);
		if(KNXView.EOrientation.Horizontal == this.mKNXSliderSwitch.getOrientation()) {
			vLeft.left = STKNXSliderSwitch.PADDING;
			vLeft.top = STKNXSliderSwitch.PADDING;
			vLeft.height = this.mKNXSliderSwitch.Height - 2 * vLeft.top;
			vLeft.width = this.mKNXSliderSwitch.Height - 2 * vLeft.top;
		} else {
			vLeft.left = STKNXSliderSwitch.PADDING;
			vLeft.width = this.mKNXSliderSwitch.Width - 2 * vLeft.left;
			vLeft.height = this.mKNXSliderSwitch.Width - 2 * vLeft.left;
			vLeft.top = this.mKNXSliderSwitch.Height - STKNXSliderSwitch.PADDING - vLeft.height;
		}
		vLeft.backColor = Color.parseColor(this.mKNXSliderSwitch.BackgroundColor);
		vLeft.radius = this.mKNXSliderSwitch.Radius;
		vLeft.alpha = this.mKNXSliderSwitch.Alpha;
		vLeft.clickable = this.mKNXSliderSwitch.getClickable();
		vLeft.setSubViewClickListener(this.leftClicked);
		if(!StringUtil.isNullOrEmpty(this.mKNXSliderSwitch.getLeftImage())) {
			vLeft.setBackgroundImage(STKNXControllerConstant.ConfigResImgPath + this.mKNXSliderSwitch.getLeftImage());
		}
		this.addView(vLeft);

		/* 第二个按钮 右边/上边 */
		STButton vRight = new STButton(context);
		if(KNXView.EOrientation.Horizontal == this.mKNXSliderSwitch.getOrientation()) {
			vRight.top = STKNXSliderSwitch.PADDING;
			vRight.height = this.mKNXSliderSwitch.Height - 2 * vRight.top;
			vRight.width = this.mKNXSliderSwitch.Height - 2 * vRight.top;
			vRight.left = this.mKNXSliderSwitch.Width - STKNXSliderSwitch.PADDING - vRight.width;
		} else {
			vRight.left = STKNXSliderSwitch.PADDING;
			vRight.top = STKNXSliderSwitch.PADDING;
			vRight.width = this.mKNXSliderSwitch.Width - 2 * vRight.left;
			vRight.height = this.mKNXSliderSwitch.Width - 2 * vRight.left;
		}
		vLeft.backColor = Color.parseColor(this.mKNXSliderSwitch.BackgroundColor);
		vLeft.radius = this.mKNXSliderSwitch.Radius;
		vLeft.alpha = this.mKNXSliderSwitch.Alpha;
		vRight.clickable = this.mKNXSliderSwitch.getClickable();
		vRight.setSubViewClickListener(this.rightClicked);
		if(!StringUtil.isNullOrEmpty(this.mKNXSliderSwitch.getRightImage())) {
			vRight.setBackgroundImage(STKNXControllerConstant.ConfigResImgPath + this.mKNXSliderSwitch.getRightImage());
		}
		this.addView(vRight);

		if(KNXView.EOrientation.Horizontal == this.mKNXSliderSwitch.getOrientation()) { // 控件为水平放置？
			this.sliderStartPos = vLeft.left + vLeft.width + STKNXSliderSwitch.PADDING;
			this.sliderWidth = this.mKNXSliderSwitch.Width - (vLeft.left + vLeft.width) - (vRight.width + STKNXSliderSwitch.PADDING) - STKNXSliderSwitch.PADDING * 2;
		} else {
			this.sliderStartPos = vLeft.top -  STKNXSliderSwitch.PADDING  - this.mKNXSliderSwitch.getSliderWidth();
			this.sliderWidth = this.mKNXSliderSwitch.Height - (vRight.top + vRight.height + STKNXSliderSwitch.PADDING) - (this.mKNXSliderSwitch.Height - this.sliderStartPos);
		}
		this.sliderPositionX = this.sliderStartPos;
		
		if(EBool.Yes == this.mKNXSliderSwitch.getIsRelativeControl()) { // 相对调光？
			int range = getThumbScrollRange();
			int interval = range/15;
			if(KNXView.EOrientation.Horizontal == this.mKNXSliderSwitch.getOrientation()) {
				for (int i = 0; i < 16; i++) {
					pos[i] = this.sliderStartPos + i * interval;
				}
				pos[15] = this.sliderStartPos + range;
			} else {
				for (int i = 0; i < 16; i++) {
					pos[i] = this.sliderStartPos - i * interval;
				}
				pos[15] = this.sliderStartPos - range;
			}
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
	        }

	        @Override
	        public void onEdgeTouched(int edgeFlags, int pointerId) {
	            super.onEdgeTouched(edgeFlags, pointerId);
	        }

	        @Override
	        public void onEdgeDragStarted(int edgeFlags, int pointerId) {
	        	if(null != mViewDragHelper) {
	        		mViewDragHelper.captureChildView(slider, pointerId);
	        	}
	        }
			
			@Override
			public int clampViewPositionHorizontal(View child, int left, int dx) {
				mSliderState = ESliderState.Dragging;
				if (KNXView.EOrientation.Horizontal == mKNXSliderSwitch.getOrientation()) {
					return sliderPositionX = getSliderPosX(left);
				} else {
					return super.clampViewPositionHorizontal(child, left, dx);
				}
			} 
			
			@Override
			public int clampViewPositionVertical(View child, int top, int dy) {
				mSliderState = ESliderState.Dragging;
				if (KNXView.EOrientation.Horizontal == mKNXSliderSwitch.getOrientation()) {
					return super.clampViewPositionVertical(child, top, dy);
				} else {
					return sliderPositionX = getSliderPosX(top);
				}
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
				if(KNXView.EOrientation.Horizontal == mKNXSliderSwitch.getOrientation()) {
					return getThumbScrollRange();
				} else {
					return 0;
				}
			}
			
			@Override
			public int getViewVerticalDragRange(View child){
				if(KNXView.EOrientation.Horizontal == mKNXSliderSwitch.getOrientation()) {
					return 0;
				} else {
					return getThumbScrollRange();
				}
			}
		});

		slider = new STSlider(context);
		slider.backColor = Color.parseColor(this.mKNXSliderSwitch.BackgroundColor);
		slider.radius = this.mKNXSliderSwitch.Radius;
		slider.alpha = this.mKNXSliderSwitch.Alpha;
		slider.orientation = this.mKNXSliderSwitch.getOrientation();
		LayoutParams pageLayoutParams = new LayoutParams(this.mKNXSliderSwitch.getSliderWidth(), this.mKNXSliderSwitch.Height);
		slider.setLayoutParams(pageLayoutParams);
		this.addView(slider);
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

	private static class STKNXSliderSwitchHandler extends Handler {
		WeakReference<STKNXSliderSwitch> mSliderSwitch;

		private STKNXSliderSwitchHandler(STKNXSliderSwitch ss) {
			super(ss.getContext().getMainLooper());

			mSliderSwitch = new WeakReference<STKNXSliderSwitch>(ss);
		}

		@Override
		public void handleMessage(Message msg) {
			mSliderSwitch.get().requestLayout();
		}
	}

	private void updateControlState() {
		STKNXSliderSwitchHandler mHandler = new STKNXSliderSwitchHandler(STKNXSliderSwitch.this);
		mHandler.sendEmptyMessage(0);
	}
	
	private int getThumbScrollRange() {
		if (KNXView.EOrientation.Horizontal == this.mKNXSliderSwitch.getOrientation()) {
			return this.sliderWidth - this.mKNXSliderSwitch.getSliderWidth();
		} else {
			return this.sliderWidth;
		}
	}
	
	private int getSliderStartPos() {
		return this.sliderStartPos;
	}
	
	private int getSliderEndPos(){
		if (KNXView.EOrientation.Horizontal == this.mKNXSliderSwitch.getOrientation()) {
			return this.getSliderStartPos() + this.getThumbScrollRange();
		} else {
			return this.getSliderStartPos() - this.getThumbScrollRange();
		}
	}
	
	private int getSliderPos(float progress){
		int pos = (int)(progress * getThumbScrollRange());
		if (KNXView.EOrientation.Horizontal == this.mKNXSliderSwitch.getOrientation()) {
			return this.sliderStartPos + pos;
		} else {
			return this.sliderStartPos - pos;
		}
	}
	
	private int getSliderPosX(int position) {
		if (KNXView.EOrientation.Horizontal == this.mKNXSliderSwitch.getOrientation()) {
			this.sliderPositionX = Math.max(getSliderStartPos(), position); // 上边界
			this.sliderPositionX = Math.min(this.sliderPositionX, getSliderEndPos()); // 下边界
			if (EBool.Yes == this.mKNXSliderSwitch.getIsRelativeControl()) {
				this.curPos = MathUtils.getTheClosetIndex(this.sliderPositionX, pos);
				this.sliderPositionX = this.pos[curPos];
			}
		} else { // 垂直布局
			this.sliderPositionX = Math.min(getSliderStartPos(), position);
			this.sliderPositionX = Math.max(this.sliderPositionX, getSliderEndPos());
			if(EBool.Yes == this.mKNXSliderSwitch.getIsRelativeControl()) {
				this.curPos = MathUtils.getTheClosetIndex(this.sliderPositionX, pos); //
				this.sliderPositionX = this.pos[curPos];
			}
		}

        return this.sliderPositionX;
	}

	public void setProgress(byte[] array) {
        KNXGroupAddress address = this.mKNXSliderSwitch.getReadAddress();
        if (null != address) {
			int value;
            if (address.getKnxMainNumber().equals(KNXDatapointType.DPT_5)
                    && address.getKnxSubNumber().equals(KNXDatapointType.DPST_1)) {
                value = DPT5.byte2Scaling(array);
			} else {
				value = KNXDatapointType.bytes2int(array, address.getType());
			}

			if (EBool.Yes == this.mKNXSliderSwitch.getIsRelativeControl()) {
			        /* 相对调光 */

			} else {
			        /* 绝对调光 */
				this.progress = (float) value / 100;
				this.sliderPositionX = getSliderPos(this.progress);
				updateControlState();
			}
        }
    }
	
	private void sliderChanged() { // 滑块位置改变
		if(EBool.Yes == this.mKNXSliderSwitch.getIsRelativeControl()) { // 相对调光？
			int val = 0;
			 if (this.curPos < 8) {
				val = this.curPos;
			} else if (this.curPos <= 15) {
				val = 8 +15-this.curPos;
			}
			
			 sendCommandRequest(this.mKNXSliderSwitch.getWriteAddressIds(), val+"", false, null); // 发送命令
		} else {
			if (KNXView.EOrientation.Horizontal == this.mKNXSliderSwitch.getOrientation()) {
				progress = (float) (sliderPositionX - this.getSliderStartPos()) / this.getThumbScrollRange();
			} else {
				progress = (float) (this.getSliderStartPos() - sliderPositionX) / this.getThumbScrollRange();
			}
    		if(progress>1){
    			progress = 1;
    		}
    		
			sendCommandRequest(this.mKNXSliderSwitch.getWriteAddressIds(), String.valueOf((int)(this.progress*255)), false, null);
		}
	}
	
	STButton.SubViewClickListener leftClicked = new STButton.SubViewClickListener() {

		@Override
		public void onClick(STButton view) { // －键点击
			if(EBool.Yes == mKNXSliderSwitch.getIsRelativeControl()) { // 相对调光？
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
		public void onClick(STButton view) { // ＋键点击
			if(EBool.Yes == mKNXSliderSwitch.getIsRelativeControl()) { // 相对控制
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
		if(null == this.mViewDragHelper) { 
			return true;
		}
		
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
				if(KNXView.EOrientation.Horizontal == this.mKNXSliderSwitch.getOrientation()) {
					cl = this.sliderPositionX;
					ct = 0;
					cr = cl + this.mKNXSliderSwitch.getSliderWidth();
					cb = ct + this.getHeight();
				} else {
					cl = 0;
					ct = this.sliderPositionX;
					cr = cl+this.getWidth();
					cb = ct+this.mKNXSliderSwitch.getSliderWidth();
				}
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
		if(KNXView.EOrientation.Horizontal == this.mKNXSliderSwitch.getOrientation()) {
			x = 0;
			y = this.SLIDER_EDGE_WIDTH;
			width = this.getWidth();
			height = this.getHeight() - 2 * y;
		} else {
			x = this.SLIDER_EDGE_WIDTH;
			y = 0;
			width = this.getWidth() - 2 * x;
			height = this.getHeight();
		}
        RectF rect1 = new RectF(x, y, x+width, y+height);
    	if(EFlatStyle.Stereo == this.mKNXSliderSwitch.getFlatStyle()) {	// 画立体感的圆角矩形 
        
    		/* 渐变色，颜色数组 */
    		int colors[] = new int[3];
    		colors[0] = ColorUtils.changeBrightnessOfColor(backColor, 100);
    		colors[1] = backColor;
    		colors[2] = ColorUtils.changeBrightnessOfColor(backColor, -50);
    		
    		/* 各颜色所在的位置 */
    		float positions[] = new float[3];
			Shader mShader;

			if(KNXView.EOrientation.Horizontal == this.mKNXSliderSwitch.getOrientation()) {
				positions[0] = .0f;
				positions[1] = .3f;
				positions[2] = 1.0f;

				mShader = new LinearGradient(0, 0, 0, getHeight(),
						colors, positions, Shader.TileMode.CLAMP); // 设置渐变色 这个正方形的颜色是改变的 , 一个材质,打造出一个线性梯度沿著一条线。
			} else {
				positions[0] = .0f;
				positions[1] = .7f;
				positions[2] = 1.0f;
				mShader = new  LinearGradient(0, 0, getWidth(), 0,
						colors, positions, Shader.TileMode.CLAMP);
			}
    		paint.setShader(mShader);  
    	} else {	// 画扁平风格的圆角矩形
    		paint.setARGB((int)(this.mKNXSliderSwitch.Alpha*255), Color.red(backColor), Color.green(backColor), Color.blue(backColor));
    	}
    	canvas.drawRoundRect(rect1, this.mKNXSliderSwitch.Radius, this.mKNXSliderSwitch.Radius, paint);//第二个参数是x半径，第三个参数是y半径  
    }
    
    @SuppressLint("ClickableViewAccessibility")
	@Override
    public boolean onTouchEvent(MotionEvent event) {
		if(EBool.Yes != this.mKNXSliderSwitch.getClickable()) {
			return true;
		}

    	if(null != this.mViewDragHelper) {
    		this.mViewDragHelper.processTouchEvent(event);
    	}
    	
    	final float x;
		if(KNXView.EOrientation.Horizontal == this.mKNXSliderSwitch.getOrientation()) { // 控件为水平放置？
			x = event.getX();

			if((x>=this.sliderStartPos) && (x<=(this.getSliderEndPos()+this.mKNXSliderSwitch.getSliderWidth())) &&
					(ESliderState.Normal == this.mSliderState)) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						this.sliderPositionX = getSliderPosX((int)x-this.mKNXSliderSwitch.getSliderWidth()/2);
						this.slider.requestLayout();
						break;
					case MotionEvent.ACTION_UP:
						this.sliderChanged();
						break;

					case MotionEvent.ACTION_MOVE:
						this.sliderPositionX = getSliderPosX((int)x-this.mKNXSliderSwitch.getSliderWidth()/2);
						this.slider.requestLayout();
						break;

					default:
						break;
				}
			}
		} else { // 垂直放置
			x = event.getY();

			if((x <= this.sliderStartPos) && (x >= (this.getSliderEndPos())) &&
					(ESliderState.Normal == this.mSliderState)) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						this.sliderPositionX = getSliderPosX( (int)x + this.mKNXSliderSwitch.getSliderWidth()/2 );
						this.slider.requestLayout();
						break;
					case MotionEvent.ACTION_UP:
						this.sliderChanged();
						break;

					case MotionEvent.ACTION_MOVE:
						this.sliderPositionX = getSliderPosX((int)x+this.mKNXSliderSwitch.getSliderWidth()/2);
						this.slider.requestLayout();
						break;

					default:
						break;
				}
			}
		}

    	return true;
    }

    @Override
	public void copyStatusAndRequest() { // 复制、请求对象状态
		super.copyStatusAndRequest();

		byte[] bytes = getControlStatus(this.mKNXSliderSwitch.getReadAddressId(), true);
		if (null != bytes) {
			this.setProgress(bytes);
		}
	}

	@Override
	public void statusUpdate(int asp, KNXGroupAddress address) { // 复制对象状态
		super.statusUpdate(asp, address);

		KNXSelectedAddress readAddr = MapUtils.getFirstOrNull(this.mKNXSliderSwitch.getReadAddressId());
		if (null != readAddr) {
			if (address.getId().equals(readAddr.getId())) {
				byte[] bytes = copyObjectStatus(asp);
				this.setProgress(bytes);
			}
		}
	}
}

