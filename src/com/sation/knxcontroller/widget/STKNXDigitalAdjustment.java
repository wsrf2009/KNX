package com.sation.knxcontroller.widget;

import com.sation.knxcontroller.STKNXControllerConstant;
import com.sation.knxcontroller.control.KNXDigitalAdjustment;
import com.sation.knxcontroller.models.KNXView.EBool;
import com.sation.knxcontroller.models.KNXView.EFlatStyle;
import com.sation.knxcontroller.util.ColorUtils;
import com.sation.knxcontroller.util.Log;
import com.sation.knxcontroller.util.StringUtil;
import com.sation.knxcontroller.util.uikit.UIKit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Handler;
import android.os.Message;
import android.view.View;

public class STKNXDigitalAdjustment extends STKNXControl {
	private static final int PADDING = 2;
	private static final int SUBVIEW_WIDTH = 40;
	private KNXDigitalAdjustment mKNXDigitalAdjustment;
    private int currentValue = 20;
//    private Handler mHandler;

	public STKNXDigitalAdjustment(Context context, KNXDigitalAdjustment knxDigitalAdjustment) {
		super(context, knxDigitalAdjustment);
		
		this.mKNXDigitalAdjustment = knxDigitalAdjustment;
		this.setId(mKNXDigitalAdjustment.getId());
		
		STButton vLeft = new STButton(context);
		vLeft.width = Math.max(this.mKNXDigitalAdjustment.Height-2*STKNXDigitalAdjustment.PADDING, STKNXDigitalAdjustment.SUBVIEW_WIDTH);
		vLeft.height = this.mKNXDigitalAdjustment.Height-2*STKNXDigitalAdjustment.PADDING;
		vLeft.left = STKNXDigitalAdjustment.PADDING;
		vLeft.top = STKNXDigitalAdjustment.PADDING;
		vLeft.backColor = Color.parseColor(this.mKNXDigitalAdjustment.BackgroundColor);
		vLeft.radius = this.mKNXDigitalAdjustment.Radius;
		vLeft.alpha = this.mKNXDigitalAdjustment.Alpha;
		vLeft.setSubViewClickListener(this.leftClicked);
		if(!StringUtil.isEmpty(this.mKNXDigitalAdjustment.LeftImage)) {
			vLeft.setBackgroundImage(STKNXControllerConstant.ConfigResImgPath + this.mKNXDigitalAdjustment.LeftImage);
		}
		this.addView(vLeft);
		
		STButton vRight = new STButton(context);
		vRight.width = Math.max(this.mKNXDigitalAdjustment.Height-2*STKNXDigitalAdjustment.PADDING, STKNXDigitalAdjustment.SUBVIEW_WIDTH);
		vRight.height = this.mKNXDigitalAdjustment.Height-2*STKNXDigitalAdjustment.PADDING;
		vRight.left = this.mKNXDigitalAdjustment.Width - STKNXDigitalAdjustment.PADDING - vRight.width;
		vRight.top = STKNXDigitalAdjustment.PADDING;
		vRight.backColor = Color.parseColor(this.mKNXDigitalAdjustment.BackgroundColor);
		vRight.radius = this.mKNXDigitalAdjustment.Radius;
		vRight.alpha = this.mKNXDigitalAdjustment.Alpha;
		vRight.setSubViewClickListener(this.rightClicked);
		if(!StringUtil.isEmpty(this.mKNXDigitalAdjustment.LeftImage)) {
			vRight.setBackgroundImage(STKNXControllerConstant.ConfigResImgPath + this.mKNXDigitalAdjustment.RightImage);
		}
		this.addView(vRight);
		
//		this.mHandler = new Handler() {
//			@Override
//			public void handleMessage(Message msg) {
//				super.handleMessage(msg);
//				
//				if(1 == msg.what) {
//					invalidate();
//				}
//			}
//		};
	}
	
	@Override
	public void onDestroy() {
//		this.mKNXDigitalAdjustment = null;
		
		int count = getChildCount();
		for(int i=0; i<count; i++) {
			View v = (View)getChildAt(i);
			if(v instanceof STButton) {
				STButton stv = (STButton)v;
				stv.onDestroy();
				stv = null;
			}
		}
	}
	
	STButton.SubViewClickListener leftClicked = new STButton.SubViewClickListener() {

		@Override
		public void onClick(STButton view) {
			try {
				int val = currentValue;
				val--;

				if(mKNXDigitalAdjustment.getReadAddressId().isEmpty()) {
					setValue(val);
				}
 
				sendCommandRequest(mKNXDigitalAdjustment.getWriteAddressIds(), val+"", false, null);
			} catch (Exception e) {
				Log.e("STKNXDigitalAdjustment", e.getLocalizedMessage());
			}
		}
	};

	STButton.SubViewClickListener rightClicked = new STButton.SubViewClickListener() {

		@Override
		public void onClick(STButton view) {
			try {
				int val = currentValue;
				val++;
				
				if(mKNXDigitalAdjustment.getReadAddressId().isEmpty()) {
					setValue(val);
				}
     
				sendCommandRequest(mKNXDigitalAdjustment.getWriteAddressIds(), val+"", false, null);

			} catch (Exception e) {
				Log.e("STKNXDigitalAdjustment", e.getLocalizedMessage());
			}
		}
	};
	
	public void setValue(int val) {
		this.currentValue = val;
		
//		Message message = new Message();
//        message.what = 1;
//        this.mHandler.sendMessage(message);
        
//		UIKit.runOnMainThreadAsync(new Runnable() {
//
//			@Override
//			public void run() {
				invalidate();
//			}
//		});
	}

	@Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		measureChildren(widthMeasureSpec, heightMeasureSpec);

        /**
         * 最后调用父类方法,把View的大小告诉父布局。
         */
        setMeasuredDimension(this.mKNXDigitalAdjustment.Width, this.mKNXDigitalAdjustment.Height);
    }
	
    @Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int cCount = getChildCount();

		/*
		 * 遍历所有childView根据其宽和高，以及margin进行布局
		 */
		for (int i = 0; i < cCount; i++) {
			STButton childView = (STButton)getChildAt(i);

			int cl = 0, ct = 0, cr = 0, cb = 0;
			cl = childView.left;
			ct = childView.top;
			cr = childView.left+childView.width;
			cb = childView.top+childView.height;

			childView.layout(cl, ct, cr, cb);
		}
	}

    @SuppressLint("DrawAllocation")
	@Override
    protected void onDraw(Canvas canvas) {
    	super.onDraw(canvas);

    	int backColor = Color.parseColor(this.mKNXDigitalAdjustment.BackgroundColor);
    	Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    	paint.setStyle(Paint.Style.FILL_AND_STROKE);	// 充满  
    	paint.setAlpha((int)(this.mKNXDigitalAdjustment.Alpha*255));
    	
        int x = 0;
        int y = 0;  // 
        int width = this.getWidth();
        int height = this.getHeight() - 2 * y;
        RectF rect1 = new RectF(x, y, x+width, y+height);
    	if(EFlatStyle.Stereo == this.mKNXDigitalAdjustment.getFlatStyle()) {	// 画立体感的圆角矩形 
        
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
    		paint.setARGB((int)(this.mKNXDigitalAdjustment.Alpha*255), Color.red(backColor), Color.green(backColor), Color.blue(backColor));
    	}
    	canvas.drawRoundRect(rect1, this.mKNXDigitalAdjustment.Radius, this.mKNXDigitalAdjustment.Radius, paint);//第二个参数是x半径，第三个参数是y半径  

    	String str = this.currentValue+" " + this.mKNXDigitalAdjustment.getUnit();
		str = str.trim();

        /* 绘制文本 */
        paint.reset();
        paint.setColor(Color.parseColor(this.mKNXDigitalAdjustment.FontColor));
        paint.setTextSize(this.mKNXDigitalAdjustment.FontSize);
        Rect bound = new Rect();
        paint.getTextBounds(str, 0, str.length(), bound);
        canvas.drawText(str, getWidth() / 2 - bound.width() / 2, getHeight() / 2 + bound.height() / 2, paint);
        
        if(EBool.Yes == this.mKNXDigitalAdjustment.getDisplayBorder()) {
    		paint.reset();
    		paint.setStyle(Paint.Style.STROKE);
    		paint.setColor(Color.parseColor(this.mKNXDigitalAdjustment.BorderColor));
    		canvas.drawRoundRect(rect1, this.mKNXDigitalAdjustment.Radius, this.mKNXDigitalAdjustment.Radius, paint);//第二个参数是x半径，第三个参数是y半径  
    	}
    }
}

//class ViewControl extends View {
//	private final int PADDING_LEFT = 5;
//	private final int PADDING_TOP = 5;
//	private final int PADDING_RIGHT = 5;
//	private final int PADDING_BOTTOM = 5;
//
//	public int left;
//	public int top;
//	public int width;
//	public int height;
//	public int backColor;
//	public int radius;
//	public double alpha;
//	public Bitmap backImage;
//	private ViewControlClickListener mViewControlClickListener;
//	private String text = "";
//
//	private enum EControlState {
//		Down,
//		Up,
//	}
//	private EControlState mEControlState;
//
//	private int backImgLeft = 0;
//	private int backImgTop = 0;
//	private int backImgRight = 0;
//	private int backImgBottom = 0;
//
//	protected ViewControl(Context context) {
//		super(context);
//
//		this.mEControlState = EControlState.Up;
//	}
//
//	public void setViewControlClickListener(ViewControlClickListener l) {
//		this.mViewControlClickListener = l;
//	}
//
//	@Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//		this.backImgLeft = this.PADDING_LEFT;
//		this.backImgTop = this.PADDING_TOP;
//		int width = this.width - this.PADDING_LEFT - this.PADDING_RIGHT;
//		int height = this.height - this.PADDING_TOP - this.PADDING_BOTTOM;
//		if(width > height) {
//			this.backImgRight = this.backImgLeft + height;
//			this.backImgBottom = this.backImgTop + height;
//		} else {
//			this.backImgRight = this.backImgLeft + width;
//			this.backImgBottom = this.backImgTop + width;
//		}
//
//        /**
//         * 最后调用父类方法,把View的大小告诉父布局。
//         */
//        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
//    }
//
//	@SuppressLint({ "DrawAllocation", "ClickableViewAccessibility" })
//	@Override
//    protected void onDraw(Canvas canvas) {
//    	super.onDraw(canvas);
//
//    	Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
//    	paint.setStyle(Paint.Style.FILL_AND_STROKE);	// 充满
////    	paint.setAlpha((int)(this.alpha*255));
//    	RectF oval3 = new RectF(0, 0, this.getWidth(), this.getHeight());
//
//        /* 绘制背景图片 */
//        if(null != this.backImage) {
//        	Rect resRect = new Rect(0, 0, this.backImage.getWidth(), this.backImage.getHeight());
//        	Rect desRect = new Rect(this.backImgLeft, this.backImgTop, this.backImgRight, this.backImgBottom);
//        	canvas.drawBitmap(this.backImage, resRect, desRect, paint);
//        }
//
//    	switch (this.mEControlState) {
//    		case Down:
//    			paint.reset();
//    			paint.setStyle(Paint.Style.FILL);
//    			paint.setColor(Color.parseColor("#FF6100"));
//    			paint.setAlpha(0x60);
//    			canvas.drawRoundRect(oval3, this.radius, this.radius, paint);	//第二个参数是x半径，第三个参数是y半径
//    		break;
//    		case Up:
//    			break;
//    	}
//	}
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//    	switch (event.getAction()) {
//    		case MotionEvent.ACTION_DOWN:
//    			this.mEControlState = EControlState.Down;
//    			this.invalidate();
//    			break;
//    		case MotionEvent.ACTION_UP:
//    			this.mEControlState = EControlState.Up;
//    			if(null != this.mViewControlClickListener) {
//    				this.mViewControlClickListener.onClick(this);
//    			}
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
//
//    public interface ViewControlClickListener {
//    	public void onClick(ViewControl view);
//    }
//}
