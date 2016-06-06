package com.sation.knxcontroller.widget;

import com.sation.knxcontroller.control.KNXValueDisplay;
import com.sation.knxcontroller.models.KNXView.EFlatStyle;
import com.sation.knxcontroller.util.ColorUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;

public class STKNXValueDisplay extends STKNXControl {
	private final int PADDING = 5;
	
	private KNXValueDisplay mKNXValueDisplay;
	private String valueString = "---";

	public STKNXValueDisplay(Context context, KNXValueDisplay knxValueDisplay) {
		super(context, knxValueDisplay);

		this.mKNXValueDisplay = knxValueDisplay;
		this.setId(this.mKNXValueDisplay.getId());
	}
	
	public void setValue(int value) {
		this.valueString = value+" "+this.mKNXValueDisplay.getUnit();
	
		this.invalidate();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(this.mKNXValueDisplay.Width, this.mKNXValueDisplay.Height);
	}
	
	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		int backColor = Color.parseColor(this.mKNXValueDisplay.BackgroundColor);
    	Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    	paint.setStyle(Paint.Style.FILL_AND_STROKE);	// 充满  
    	paint.setAlpha((int)(this.mKNXValueDisplay.Alpha*255));
    	RectF oval3 = new RectF(0, 0, getWidth(), getHeight());// 设置个新的长方形 ;
    	if(EFlatStyle.Stereo == this.mKNXValueDisplay.getFlatStyle()) {	// 画立体感的圆角矩形 
        
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
    		paint.setARGB((int)(this.mKNXValueDisplay.Alpha*255), Color.red(backColor), Color.green(backColor), Color.blue(backColor));
    	}
    	canvas.drawRoundRect(oval3, this.mKNXValueDisplay.Radius, this.mKNXValueDisplay.Radius, paint);//第二个参数是x半径，第三个参数是y半径  
    	
    	int x = this.PADDING;
    	int y = this.PADDING;
    	int fontColor = Color.parseColor(this.mKNXValueDisplay.FontColor);
    	paint.reset();
    	paint.setColor(fontColor);
    	paint.setTextSize(this.mKNXValueDisplay.FontSize);
    	
//    	if(null != this.mKNXValueDisplay.getText()) {
//        	/* 绘制文本 */
//        	Rect bound = new Rect();
//        	paint.getTextBounds(this.mKNXValueDisplay.getText(), 0, this.mKNXValueDisplay.getText().length(), bound);
//        	canvas.drawText(this.mKNXValueDisplay.getText(), x, getHeight() / 2 + bound.height() / 2, paint);
//        }
    	
    	if(null != this.valueString) { 
//    		valueString += " " +/* this.mKNXValueDisplay.getUnitDescription()*/this.mKNXValueDisplay.Unit;
    		
    		Rect bound = new Rect();
        	paint.getTextBounds(this.valueString, 0, this.valueString.length(), bound);
//        	x = getWidth() - this.PADDING - bound.width();
        	x = (getWidth() - 2 *x - bound.width())/2;
        	y = (getHeight()  + bound.height())/2;
        	canvas.drawText(this.valueString, x, y, paint);
    	}
	}
}