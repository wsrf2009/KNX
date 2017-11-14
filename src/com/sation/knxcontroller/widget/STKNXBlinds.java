<<<<<<< HEAD
package com.sation.knxcontroller.widget;

import com.sation.knxcontroller.STKNXControllerApp;
import com.sation.knxcontroller.STKNXControllerConstant;
import com.sation.knxcontroller.control.KNXBlinds;
import com.sation.knxcontroller.models.KNXView.EBool;
import com.sation.knxcontroller.models.KNXView.EFlatStyle;
import com.sation.knxcontroller.util.ColorUtils;
import com.sation.knxcontroller.util.Log;
import com.sation.knxcontroller.util.StringUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

public class STKNXBlinds extends STKNXControl {
	private final String TAG = "STKNXBlinds";

	private static final int PADDING = 2;
	private static final int SUBVIEW_WIDTH = 40;
	private KNXBlinds mKNXBlinds;

	private int txtLeft;
	private int txtTop;
	private int txtWidth;
	private int txtHeight;
	
	public STKNXBlinds(Context context, KNXBlinds knxBlinds) {
		super(context, knxBlinds);

		this.mKNXBlinds = knxBlinds;
		this.setId(this.mKNXBlinds.getId());
		
		STButton vLeft = new STButton(context);
		vLeft.width = Math.max(this.mKNXBlinds.Height-2*STKNXBlinds.PADDING, STKNXBlinds.SUBVIEW_WIDTH);
		vLeft.height = this.mKNXBlinds.Height-2*STKNXBlinds.PADDING;
		vLeft.left = STKNXBlinds.PADDING;
		vLeft.top = STKNXBlinds.PADDING;
		vLeft.backColor = Color.parseColor(this.mKNXBlinds.BackgroundColor);
		vLeft.radius = this.mKNXBlinds.Radius;
		vLeft.alpha = this.mKNXBlinds.Alpha;
		vLeft.text = this.mKNXBlinds.LeftText;
		vLeft.mTextFont = this.mKNXBlinds.LeftTextFont;
		vLeft.clickable = this.mKNXBlinds.getClickable();
		vLeft.setSubViewClickListener(this.leftClicked);
		if(!StringUtil.isNullOrEmpty(this.mKNXBlinds.getLeftImage())) {
			vLeft.setBackgroundImage(STKNXControllerConstant.ConfigResImgPath + this.mKNXBlinds.getLeftImage());
		}
		this.addView(vLeft);
		
		STButton vRight = new STButton(context);
		vRight.width = Math.max(this.mKNXBlinds.Height-2*STKNXBlinds.PADDING, STKNXBlinds.SUBVIEW_WIDTH);
		vRight.height = this.mKNXBlinds.Height-2*STKNXBlinds.PADDING;
		vRight.left = this.mKNXBlinds.Width - STKNXBlinds.PADDING - vRight.width;
		vRight.top = STKNXBlinds.PADDING;
		vRight.backColor = Color.parseColor(this.mKNXBlinds.BackgroundColor);
		vRight.radius = this.mKNXBlinds.Radius;
		vRight.alpha = this.mKNXBlinds.Alpha;
		vRight.text = this.mKNXBlinds.RightText;
		vRight.mTextFont = this.mKNXBlinds.RightTextFont;
		vRight.clickable = this.mKNXBlinds.getClickable();
		vRight.setSubViewClickListener(this.rightClicked);
		if(!StringUtil.isNullOrEmpty(this.mKNXBlinds.getRightImage())) {
			vRight.setBackgroundImage(STKNXControllerConstant.ConfigResImgPath + this.mKNXBlinds.getRightImage());
		}
		this.addView(vRight);
	}

	@Override
	public void onSuspend() {

	}

	@Override
	public void onResume() {

	}

	@Override
	public void onDestroy() {
		super.onDestroy();

//		Log.i(TAG, "");
//		int count = getChildCount();
//		for(int i=0; i<count; i++) {
//			View v = (View)getChildAt(i);
//			if(v instanceof STButton) {
//				STButton stv = (STButton)v;
//				stv.onDestroy();
//				stv = null;
//			}
//		}
	}
	
	STButton.SubViewClickListener leftClicked = new STButton.SubViewClickListener() {

		@Override
		public void onClick(STButton view) {
			sendCommandRequest(mKNXBlinds.getWriteAddressIds(), "0", false, null);
		}
	};
	
	STButton.SubViewClickListener rightClicked = new STButton.SubViewClickListener() {

		@Override
		public void onClick(STButton view) {
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
		int cl=0, ct=0, cr=0, cb=0;

		/*
		 * 遍历所有childView根据其宽和高，以及margin进行布局
		 */
		for (int i = 0; i < cCount; i++) {
			View v = getChildAt(i);
			if(v instanceof STButton) {
				STButton childView = (STButton) getChildAt(i);


				cl = childView.left;
				ct = childView.top;
				cr = childView.left + childView.width;
				cb = childView.top + childView.height;
			} else if(v instanceof TextView) {
				TextView txt = (TextView)v;

				cl = this.txtLeft; // txt.getLeft(); // this.txtLeft;
				ct = this.txtTop; // txt.getTop(); // this.txtTop;
				cr = cl + this.txtWidth; // txt.getRight(); // cl + this.txtWidth;
				cb = ct + this.txtHeight; //txt.getBottom(); // ct + this.txtHeight;
			}

			v.layout(cl, ct, cr, cb);
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

    	if(null != this.mKNXBlinds.getTitle()) {
        	/* 绘制文本 */
			Paint textPaint = this.mKNXBlinds.TitleFont.getTextPaint();
			int baseY = (int) ((rect1.height() / 2) - ((textPaint.descent() + textPaint.ascent()) / 2));
			canvas.drawText(this.mKNXBlinds.getTitle(), rect1.width()/2, baseY, textPaint);
    	}
    	
    	if(EBool.Yes == this.mKNXBlinds.getDisplayBorder()) {
    		paint.reset();
    		paint.setStyle(Paint.Style.STROKE);
    		paint.setColor(Color.parseColor(this.mKNXBlinds.BorderColor));
    		canvas.drawRoundRect(rect1, this.mKNXBlinds.Radius, this.mKNXBlinds.Radius, paint);//第二个参数是x半径，第三个参数是y半径  
    	}
    }
=======
package com.sation.knxcontroller.widget;

import com.sation.knxcontroller.STKNXControllerApp;
import com.sation.knxcontroller.STKNXControllerConstant;
import com.sation.knxcontroller.control.KNXBlinds;
import com.sation.knxcontroller.models.KNXView.EBool;
import com.sation.knxcontroller.models.KNXView.EFlatStyle;
import com.sation.knxcontroller.util.ColorUtils;
import com.sation.knxcontroller.util.Log;
import com.sation.knxcontroller.util.StringUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

public class STKNXBlinds extends STKNXControl {
	private final String TAG = "STKNXBlinds";

	private static final int PADDING = 2;
	private static final int SUBVIEW_WIDTH = 40;
	private KNXBlinds mKNXBlinds;

	private int txtLeft;
	private int txtTop;
	private int txtWidth;
	private int txtHeight;
	
	public STKNXBlinds(Context context, KNXBlinds knxBlinds) {
		super(context, knxBlinds);

		this.mKNXBlinds = knxBlinds;
		this.setId(this.mKNXBlinds.getId());
		
		STButton vLeft = new STButton(context);
		vLeft.width = Math.max(this.mKNXBlinds.Height-2*STKNXBlinds.PADDING, STKNXBlinds.SUBVIEW_WIDTH);
		vLeft.height = this.mKNXBlinds.Height-2*STKNXBlinds.PADDING;
		vLeft.left = STKNXBlinds.PADDING;
		vLeft.top = STKNXBlinds.PADDING;
		vLeft.backColor = Color.parseColor(this.mKNXBlinds.BackgroundColor);
		vLeft.radius = this.mKNXBlinds.Radius;
		vLeft.alpha = this.mKNXBlinds.Alpha;
		vLeft.text = this.mKNXBlinds.LeftText;
		vLeft.mTextFont = this.mKNXBlinds.LeftTextFont;
		vLeft.clickable = this.mKNXBlinds.getClickable();
		vLeft.setSubViewClickListener(this.leftClicked);
		if(!StringUtil.isNullOrEmpty(this.mKNXBlinds.getLeftImage())) {
			vLeft.setBackgroundImage(STKNXControllerConstant.ConfigResImgPath + this.mKNXBlinds.getLeftImage());
		}
		this.addView(vLeft);
		
		STButton vRight = new STButton(context);
		vRight.width = Math.max(this.mKNXBlinds.Height-2*STKNXBlinds.PADDING, STKNXBlinds.SUBVIEW_WIDTH);
		vRight.height = this.mKNXBlinds.Height-2*STKNXBlinds.PADDING;
		vRight.left = this.mKNXBlinds.Width - STKNXBlinds.PADDING - vRight.width;
		vRight.top = STKNXBlinds.PADDING;
		vRight.backColor = Color.parseColor(this.mKNXBlinds.BackgroundColor);
		vRight.radius = this.mKNXBlinds.Radius;
		vRight.alpha = this.mKNXBlinds.Alpha;
		vRight.text = this.mKNXBlinds.RightText;
		vRight.mTextFont = this.mKNXBlinds.RightTextFont;
		vRight.clickable = this.mKNXBlinds.getClickable();
		vRight.setSubViewClickListener(this.rightClicked);
		if(!StringUtil.isNullOrEmpty(this.mKNXBlinds.getRightImage())) {
			vRight.setBackgroundImage(STKNXControllerConstant.ConfigResImgPath + this.mKNXBlinds.getRightImage());
		}
		this.addView(vRight);
	}

	@Override
	public void onSuspend() {

	}

	@Override
	public void onResume() {

	}

	@Override
	public void onDestroy() {
		super.onDestroy();

//		Log.i(TAG, "");
//		int count = getChildCount();
//		for(int i=0; i<count; i++) {
//			View v = (View)getChildAt(i);
//			if(v instanceof STButton) {
//				STButton stv = (STButton)v;
//				stv.onDestroy();
//				stv = null;
//			}
//		}
	}
	
	STButton.SubViewClickListener leftClicked = new STButton.SubViewClickListener() {

		@Override
		public void onClick(STButton view) {
			sendCommandRequest(mKNXBlinds.getWriteAddressIds(), "0", false, null);
		}
	};
	
	STButton.SubViewClickListener rightClicked = new STButton.SubViewClickListener() {

		@Override
		public void onClick(STButton view) {
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
		int cl=0, ct=0, cr=0, cb=0;

		/*
		 * 遍历所有childView根据其宽和高，以及margin进行布局
		 */
		for (int i = 0; i < cCount; i++) {
			View v = getChildAt(i);
			if(v instanceof STButton) {
				STButton childView = (STButton) getChildAt(i);


				cl = childView.left;
				ct = childView.top;
				cr = childView.left + childView.width;
				cb = childView.top + childView.height;
			} else if(v instanceof TextView) {
				TextView txt = (TextView)v;

				cl = this.txtLeft; // txt.getLeft(); // this.txtLeft;
				ct = this.txtTop; // txt.getTop(); // this.txtTop;
				cr = cl + this.txtWidth; // txt.getRight(); // cl + this.txtWidth;
				cb = ct + this.txtHeight; //txt.getBottom(); // ct + this.txtHeight;
			}

			v.layout(cl, ct, cr, cb);
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

    	if(null != this.mKNXBlinds.getTitle()) {
        	/* 绘制文本 */
			Paint textPaint = this.mKNXBlinds.TitleFont.getTextPaint();
			int baseY = (int) ((rect1.height() / 2) - ((textPaint.descent() + textPaint.ascent()) / 2));
			canvas.drawText(this.mKNXBlinds.getTitle(), rect1.width()/2, baseY, textPaint);
    	}
    	
    	if(EBool.Yes == this.mKNXBlinds.getDisplayBorder()) {
    		paint.reset();
    		paint.setStyle(Paint.Style.STROKE);
    		paint.setColor(Color.parseColor(this.mKNXBlinds.BorderColor));
    		canvas.drawRoundRect(rect1, this.mKNXBlinds.Radius, this.mKNXBlinds.Radius, paint);//第二个参数是x半径，第三个参数是y半径  
    	}
    }
>>>>>>> SationCentralControl(10inch)
}