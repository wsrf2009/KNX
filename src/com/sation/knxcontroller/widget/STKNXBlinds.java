package com.sation.knxcontroller.widget;

import com.sation.knxcontroller.STKNXControllerConstant;
import com.sation.knxcontroller.control.KNXBlinds;
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

public class STKNXBlinds extends STKNXControl {
	private static final int PADDING = 0;
	private static final int SUBVIEW_WIDTH = 40;
	
	private KNXBlinds mKNXBlinds;
	
	public STKNXBlinds(Context context, KNXBlinds knxBlinds) {
		super(context, knxBlinds);

		this.mKNXBlinds = knxBlinds;
		this.setId(this.mKNXBlinds.getId());
		
		SubView vLeft = new SubView(context);
		vLeft.width = Math.max(this.mKNXBlinds.Height, STKNXBlinds.SUBVIEW_WIDTH);
		vLeft.height = this.mKNXBlinds.Height;
		vLeft.left = STKNXBlinds.PADDING;
		vLeft.top = STKNXBlinds.PADDING;
		vLeft.backColor = Color.parseColor(this.mKNXBlinds.BackgroundColor);
		vLeft.radius = this.mKNXBlinds.Radius;
		vLeft.alpha = this.mKNXBlinds.Alpha;
		vLeft.text = this.mKNXBlinds.LeftText;
		vLeft.fontSize = this.mKNXBlinds.LeftTextFontSize;
		vLeft.fontColor = Color.parseColor(this.mKNXBlinds.LeftTextFontColor);
		vLeft.setSubViewClickListener(this.leftClicked);
		if(!StringUtil.isEmpty(this.mKNXBlinds.LeftImage)) {
			vLeft.backImage = ImageUtils.getDiskBitmap(STKNXControllerConstant.ConfigResImgPath + this.mKNXBlinds.LeftImage);
		}
		this.addView(vLeft);
		
		SubView vRight = new SubView(context);
		vRight.width = Math.max(this.mKNXBlinds.Height, STKNXBlinds.SUBVIEW_WIDTH);
		vRight.height = this.mKNXBlinds.Height;
		vRight.left = this.mKNXBlinds.Width - STKNXBlinds.PADDING - vRight.width;
		vRight.top = STKNXBlinds.PADDING;
		vRight.backColor = Color.parseColor(this.mKNXBlinds.BackgroundColor);
		vRight.radius = this.mKNXBlinds.Radius;
		vRight.alpha = this.mKNXBlinds.Alpha;
		vRight.text = this.mKNXBlinds.RightText;
		vRight.fontSize = this.mKNXBlinds.RightTextFontSize;
		vRight.fontColor = Color.parseColor(this.mKNXBlinds.RightTextFontColor);
		vRight.setSubViewClickListener(this.rightClicked);
		if(!StringUtil.isEmpty(this.mKNXBlinds.LeftImage)) {
			vRight.backImage = ImageUtils.getDiskBitmap(STKNXControllerConstant.ConfigResImgPath + this.mKNXBlinds.RightImage);
		}
		this.addView(vRight);
	}
	
	SubView.SubViewClickListener leftClicked = new SubView.SubViewClickListener() {

		@Override
		public void onClick(SubView view) {
			sendCommandRequest(mKNXBlinds.getWriteAddressIds(), "0", false, null);
		}
	};
	
	SubView.SubViewClickListener rightClicked = new SubView.SubViewClickListener() {

		@Override
		public void onClick(SubView view) {
			sendCommandRequest(mKNXBlinds.getWriteAddressIds(), "1", false, null);
		}
	};
	
	@Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		measureChildren(widthMeasureSpec, heightMeasureSpec);

        /**
         * 最后调用父类方法,把View的大小告诉父布局。
         */
        setMeasuredDimension(this.mKNXBlinds.Width, this.mKNXBlinds.Height);
    }
	
    @Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int cCount = getChildCount();

		/*
		 * 遍历所有childView根据其宽和高，以及margin进行布局
		 */
		for (int i = 0; i < cCount; i++) {
			SubView childView = (SubView)getChildAt(i);

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

    	int backColor = Color.parseColor(this.mKNXBlinds.BackgroundColor);
    	Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    	paint.setStyle(Paint.Style.FILL_AND_STROKE);	// 充满  
    	paint.setAlpha((int)(this.mKNXBlinds.Alpha*255));
    	
        int x = 0;
        int y = 0;  // 
        int width = this.getWidth();
        int height = this.getHeight() - 2 * y;
        RectF rect1 = new RectF(x, y, x+width, y+height);
    	if(EFlatStyle.Stereo == this.mKNXBlinds.getFlatStyle()) {	// 画立体感的圆角矩形 
        
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
    		paint.setARGB((int)(this.mKNXBlinds.Alpha*255), Color.red(backColor), Color.green(backColor), Color.blue(backColor));
    	}
    	canvas.drawRoundRect(rect1, this.mKNXBlinds.Radius, this.mKNXBlinds.Radius, paint);//第二个参数是x半径，第三个参数是y半径  

    	if(null != this.mKNXBlinds.getText()) {
        	/* 绘制文本 */
        	Rect bound = new Rect();
        	paint.getTextBounds(this.mKNXBlinds.getText(), 0, this.mKNXBlinds.getText().length(), bound);
        	x = getWidth()/2;
            y = (getHeight()  + bound.height())/2;
            
            paint.reset();
        	paint.setTextSize(this.mKNXBlinds.FontSize);
            paint.setColor(Color.parseColor(this.mKNXBlinds.FontColor));
            paint.setTextAlign(Paint.Align.CENTER);
        	canvas.drawText(this.mKNXBlinds.getText(), x, y, paint);
    	}
    }
}

class SubView extends View {
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
	
	
	private enum EControlState {
		Down,
		Up,
	}
	private EControlState mEControlState;
	
	private int backImgLeft = 0;
	private int backImgTop = 0;
	private int backImgRight = 0;
	private int backImgBottom = 0;

	protected SubView(Context context) {
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
    	paint.setAlpha((int)(this.alpha*255));
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
        	
//        	Log.e("SubView", "x:"+x+" y:"+y+" height:"+getHeight() +" bHeight:"+bound.height()+" fontSize:"+this.fontSize);
        }
    	
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
    	public void onClick(SubView view);
    }
}