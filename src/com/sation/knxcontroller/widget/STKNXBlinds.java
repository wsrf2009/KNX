package com.sation.knxcontroller.widget;

import com.sation.knxcontroller.STKNXControllerConstant;
import com.sation.knxcontroller.control.KNXBlinds;
import com.sation.knxcontroller.models.KNXView.EBool;
import com.sation.knxcontroller.models.KNXView.EFlatStyle;
import com.sation.knxcontroller.util.ColorUtils;
import com.sation.knxcontroller.util.StringUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.view.View;

public class STKNXBlinds extends STKNXControl {
	private static final int PADDING = 2;
	private static final int SUBVIEW_WIDTH = 40;
	private KNXBlinds mKNXBlinds;
	
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
		vLeft.fontSize = this.mKNXBlinds.LeftTextFontSize;
		vLeft.fontColor = Color.parseColor(this.mKNXBlinds.LeftTextFontColor);
		vLeft.setSubViewClickListener(this.leftClicked);
		if(!StringUtil.isEmpty(this.mKNXBlinds.LeftImage)) {
			vLeft.setBackgroundImage(STKNXControllerConstant.ConfigResImgPath + this.mKNXBlinds.LeftImage);
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
		vRight.fontSize = this.mKNXBlinds.RightTextFontSize;
		vRight.fontColor = Color.parseColor(this.mKNXBlinds.RightTextFontColor);
		vRight.setSubViewClickListener(this.rightClicked);
		if(!StringUtil.isEmpty(this.mKNXBlinds.RightImage)) {
			vRight.setBackgroundImage(STKNXControllerConstant.ConfigResImgPath + this.mKNXBlinds.RightImage);
		}
		this.addView(vRight);
	}

	@Override
	public void onDestroy() {
//		this.mKNXBlinds = null;
		
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
    	
    	if(EBool.Yes == this.mKNXBlinds.getDisplayBorder()) {
    		paint.reset();
    		paint.setStyle(Paint.Style.STROKE);
    		paint.setColor(Color.parseColor(this.mKNXBlinds.BorderColor));
    		canvas.drawRoundRect(rect1, this.mKNXBlinds.Radius, this.mKNXBlinds.Radius, paint);//第二个参数是x半径，第三个参数是y半径  
    	}
    }
}