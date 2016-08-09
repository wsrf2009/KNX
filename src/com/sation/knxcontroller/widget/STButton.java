package com.sation.knxcontroller.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

public class STButton extends View {

	private final int PADDING = 5;
	
	public int left;
	public int top;
	public int width;
	public int height;
	public int backColor;
	public int radius;
	public double alpha;
	public Bitmap backImage;
	public String text;
	public int fontSize;
	public int fontColor;
	private SubViewClickListener mSubViewClickListener;
	
	
	public enum EControlState {
		Down,
		Up,
	}
	public EControlState mEControlState;
	
	private int backImgLeft = 0;
	private int backImgTop = 0;
	private int backImgRight = 0;
	private int backImgBottom = 0;

	protected STButton(Context context) {
		super(context);
		
		this.mEControlState = EControlState.Up;
	}
	
	public void setSubViewClickListener(SubViewClickListener l) {
		this.mSubViewClickListener = l;
	}
	
	@Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		this.backImgLeft = this.PADDING;
		this.backImgTop = this.PADDING;
		int width = this.width - this.PADDING - this.PADDING;
		int height = this.height - this.PADDING - this.PADDING;
		if(width > height) {
			this.backImgRight = this.backImgLeft + height;
			this.backImgBottom = this.backImgTop + height;
		} else {
			this.backImgRight = this.backImgLeft + width;
			this.backImgBottom = this.backImgTop + width;
		}
		
        /**
         * 最后调用父类方法,把View的大小告诉父布局。
         */
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

	@SuppressLint({ "DrawAllocation", "ClickableViewAccessibility" })
	@Override
    protected void onDraw(Canvas canvas) {
    	super.onDraw(canvas);

    	Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    	paint.setStyle(Paint.Style.FILL_AND_STROKE);	// 充满  
//    	paint.setAlpha((int)(this.alpha*255));
    	RectF oval3 = new RectF(0, 0, this.getWidth(), this.getHeight());

        /* 绘制背景图片 */
        if(null != this.backImage) {
        	Rect resRect = new Rect(0, 0, this.backImage.getWidth(), this.backImage.getHeight());
        	Rect desRect = new Rect(this.backImgLeft, this.backImgTop, this.backImgRight, this.backImgBottom);
        	canvas.drawBitmap(this.backImage, resRect, desRect, paint);
        }
        
        if(null != this.text) {
        	int x = 0;
        	int y = 0;
        	Rect bound = new Rect();
        	paint.getTextBounds(this.text, 0, this.text.length(), bound);
        	x = (getWidth() - 2 *x - bound.width())/2;
            y = (getHeight()  + bound.height())/2;
        	
        	/* 绘制文本 */
        	paint.reset();
        	paint.setColor(this.fontColor);
        	paint.setTextSize(this.fontSize);
        	canvas.drawText(this.text, x, y, paint);
        }
    	
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
	
    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	switch (event.getAction()) { 
    		case MotionEvent.ACTION_DOWN:
    			this.mEControlState = EControlState.Down;
    			this.invalidate();
    			break; 
    		case MotionEvent.ACTION_UP:
    			this.mEControlState = EControlState.Up;
    			if(null != this.mSubViewClickListener) {
    				this.mSubViewClickListener.onClick(this);
    			}
    			this.invalidate();
    			break;
    		case MotionEvent.ACTION_MOVE:
    			break;
    		case MotionEvent.ACTION_CANCEL:
    			this.mEControlState = EControlState.Up;
    			this.invalidate();
    			break;
    			
    		default:
    			break;
    	}
    	
    	return true;
    }
    
    public interface SubViewClickListener {
    	public void onClick(STButton view);
    }

}
