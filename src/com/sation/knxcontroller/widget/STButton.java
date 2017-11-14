package com.sation.knxcontroller.widget;

import com.sation.knxcontroller.models.KNXFont;
import com.sation.knxcontroller.models.KNXView;
import com.sation.knxcontroller.util.ImageUtils;
import com.sation.knxcontroller.util.StringUtil;
import com.sation.knxcontroller.util.uikit.UIKit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class STButton extends ViewGroup {
	private final int PADDING = 0;
	
	public int left;
	public int top;
	public int width;
	public int height;
	public int backColor;
	public int radius;
	public double alpha;
	private ImageView mImageView;
	public String text;
//	public int fontSize;
//	public int fontColor;
	public KNXFont mTextFont;
	public KNXView.EBool clickable;
//	private Bitmap backgroundImage;
	private SubViewClickListener mSubViewClickListener;
	
	public enum EControlState {
		Down,
		Up,
	}
	public EControlState mEControlState;

	protected STButton(Context context) {
		super(context);

		setWillNotDraw(false);
		
		this.mEControlState = EControlState.Up;
		this.mImageView = new ImageView(context);
		this.addView(this.mImageView);
	}
	
	public void onDestroy() {
		this.mImageView = null;
		this.text = null;
		mTextFont = null;
		this.mSubViewClickListener = null;
	}
	
	public void setSubViewClickListener(SubViewClickListener l) {
		this.mSubViewClickListener = l;
	}
	
	public void setBackgroundImage(final String imagePath) {
		if (!StringUtil.isNullOrEmpty(imagePath)) {
			Bitmap bm = ImageUtils.getDiskBitmap(imagePath);
			if ((null != bm) && (null != mImageView)) {
//				mImageView.setImageBitmap(bm);
				this.setBackground(new BitmapDrawable(bm));
			}
		}
	}
	
	@Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		measureChildren(widthMeasureSpec, heightMeasureSpec);

        /**
         * 最后调用父类方法,把View的大小告诉父布局。
         */
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
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
				cl = this.PADDING;
				ct = this.PADDING;
				
				int width = this.width - this.PADDING - this.PADDING;
				int height = this.height - this.PADDING - this.PADDING;
				if(width > height) {
					cr = cl + height;
					cb = ct + height;
				} else {
					cr = cl + width;
					cb = ct + width;
				}
			}
			view.layout(cl, ct, cr, cb);
		}
	}

	@SuppressLint({ "DrawAllocation", "ClickableViewAccessibility" })
	@Override
    protected void onDraw(Canvas canvas) {
    	super.onDraw(canvas);

    	Paint paint = new Paint();
    	paint.setStyle(Paint.Style.FILL_AND_STROKE);	// 充满  
    	RectF oval3 = new RectF(0, 0, this.getWidth(), this.getHeight());
		paint.setARGB((int)(this.alpha*0), Color.red(this.backColor), Color.green(this.backColor), Color.blue(this.backColor));
		canvas.drawRoundRect(oval3, this.radius, this.radius, paint); //第二个参数是x半径，第三个参数是y半径

        if(null != this.text) {
			Paint textPaint = this.mTextFont.getTextPaint();
			int baseY = (int) ((oval3.height() / 2) - ((textPaint.descent() + textPaint.ascent()) / 2));
			canvas.drawText(this.text, oval3.width()/2, baseY, textPaint);
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
	
    @SuppressLint("ClickableViewAccessibility")
	@Override
    public boolean onTouchEvent(MotionEvent event) {
		if(KNXView.EBool.Yes != this.clickable) {
			return true;
		}

    	switch (event.getAction()) {
    		case MotionEvent.ACTION_DOWN:
    			this.mEControlState = EControlState.Down;
    			invalidate();
    			if(null != this.mImageView) {
    				this.mImageView.setAlpha(0.6f);
    			}
    			break; 
    		case MotionEvent.ACTION_UP:
    			this.mEControlState = EControlState.Up;
    			if(null != this.mSubViewClickListener) {
    				this.mSubViewClickListener.onClick(this);
    			}
    			invalidate();
    			if(null != this.mImageView) {
    				this.mImageView.setAlpha(1.0f);
    			}
    			break;
    		case MotionEvent.ACTION_MOVE:
    			break;
    		case MotionEvent.ACTION_CANCEL:
    			this.mEControlState = EControlState.Up;
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
    
    public interface SubViewClickListener {
    	public void onClick(STButton view);
    }
}
