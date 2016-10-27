package com.sation.knxcontroller.widget;

import com.sation.knxcontroller.STKNXControllerConstant;
import com.sation.knxcontroller.control.KNXTimerButton;
import com.sation.knxcontroller.models.KNXView.EBool;
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
import android.view.View;
import android.widget.ImageView;

public class STKNXTimerButton extends STKNXControl {
	private final int PADDING = 2;
	
	private KNXTimerButton mKNXTimerButton;
	private enum ControlState {
    	Down,
    	Normal,
    }
    private ControlState mControlState;
    private int imgY = 0;
    private int imgRight = 0;
    private String image;
    private ImageView mImageView;
    private TimerButtonOnClickListener mOnClickListener;

	public STKNXTimerButton(Context context, KNXTimerButton knxTimerButton) {
		super(context, knxTimerButton);

		this.mKNXTimerButton = knxTimerButton;
		this.setId(this.mKNXTimerButton.getId());
		
		this.mControlState = ControlState.Normal;
		
		if(!StringUtil.isEmpty(this.mKNXTimerButton.Icon)) {
			this.image = STKNXControllerConstant.ConfigResImgPath + this.mKNXTimerButton.Icon;
		}
		
		this.mImageView = new ImageView(context);
		this.addView(this.mImageView);
		
		Bitmap bm = ImageUtils.getDiskBitmap(this.image);
		if(null != bm) {
			mImageView.setImageBitmap(bm);
		}
	}
	
	@Override
	public void onDestroy() {
//		this.mKNXTimerButton = null;
		this.mImageView = null;
		this.mOnClickListener = null;
	}
	
	public void setOnClickListener(TimerButtonOnClickListener l) {
		this.mOnClickListener = l;
	}

	@Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		measureChildren(widthMeasureSpec, heightMeasureSpec);

        /**
         * 最后调用父类方法,把View的大小告诉父布局。
         */
        setMeasuredDimension(this.mKNXTimerButton.Width, this.mKNXTimerButton.Height);
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
					int imgHeight = this.mKNXTimerButton.Height - 2 * this.imgY;
					
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
  
    	int backColor = Color.parseColor(this.mKNXTimerButton.BackgroundColor);
    	Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    	paint.setStyle(Paint.Style.FILL_AND_STROKE);	// 充满  
    	paint.setAlpha((int)(this.mKNXTimerButton.Alpha*255));
    	RectF oval3 = new RectF(0, 0, getWidth(), getHeight());// 设置个新的长方形  

    	if(EFlatStyle.Stereo == this.mKNXTimerButton.getFlatStyle()) {	// 画立体感的圆角矩形 
        
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
    		paint.setARGB((int)(this.mKNXTimerButton.Alpha*255), Color.red(backColor), Color.green(backColor), Color.blue(backColor));
    	}
    	canvas.drawRoundRect(oval3, this.mKNXTimerButton.Radius, this.mKNXTimerButton.Radius, paint);//第二个参数是x半径，第三个参数是y半径  
    	
    	paint.reset();

        if(null != this.mKNXTimerButton.getText()) {
        	int x = 0;
        	int y = 0;
        	Rect bound = new Rect();
        	paint.getTextBounds(this.mKNXTimerButton.getText(), 0, this.mKNXTimerButton.getText().length(), bound);
        	if(StringUtil.isEmpty(this.image)) {
        		x = (getWidth() - 2 *x - bound.width())/2;
            	y = (getHeight()  + bound.height())/2;
        	} else {
        		x=(getWidth()- (this.imgRight+this.PADDING)-this.PADDING -bound.width())/2+this.imgRight+this.PADDING;
        		y=(getHeight()  + bound.height())/2;
        	}
        	
        	/* 绘制文本 */
        	paint.reset();
        	paint.setColor(Color.parseColor(this.mKNXTimerButton.FontColor));
        	paint.setTextSize(this.mKNXTimerButton.FontSize);
        	paint.getTextBounds(this.mKNXTimerButton.getText(), 0, this.mKNXTimerButton.getText().length(), bound);
        	canvas.drawText(this.mKNXTimerButton.getText(), x, y, paint);
        }
        
        switch (mControlState) {
    		case Down:
    			paint.reset();
    			paint.setStyle(Paint.Style.FILL);
    			paint.setColor(Color.parseColor("#FF6100"));
    			paint.setAlpha(0x60);
    			canvas.drawRoundRect(oval3, this.mKNXTimerButton.Radius, this.mKNXTimerButton.Radius, paint);	//第二个参数是x半径，第三个参数是y半径  
    			break;
    		case Normal:
    			break;
        }
        
        if(EBool.Yes == this.mKNXTimerButton.getDisplayBorder()) {
    		paint.reset();
    		paint.setStyle(Paint.Style.STROKE);
    		paint.setColor(Color.parseColor(this.mKNXTimerButton.BorderColor));
    		canvas.drawRoundRect(oval3, this.mKNXTimerButton.Radius, this.mKNXTimerButton.Radius, paint);//第二个参数是x半径，第三个参数是y半径  
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
    			this.mControlState = ControlState.Normal;
    			if(null != this.mOnClickListener) {
    				this.mOnClickListener.onClick(this);
    			}
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
    
    public interface TimerButtonOnClickListener {
    	public void onClick(STKNXTimerButton button);
    }
}
