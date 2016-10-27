package com.sation.knxcontroller.widget;

import com.sation.knxcontroller.control.KNXLabel;
import com.sation.knxcontroller.models.KNXView.EBool;
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

public class STKNXLabel extends STKNXControl {
	private KNXLabel mKNXLabel;
	
	public STKNXLabel(Context context, KNXLabel label) {
		super(context, label);
		
		this.mKNXLabel = label;
		this.setId(this.mKNXLabel.getId());
	}
	
	@Override
	public void onDestroy() {
//		this.mKNXLabel = null;
	}
	
	@Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        /**
         * 最后调用父类方法,把View的大小告诉父布局。
         */
        setMeasuredDimension(this.mKNXLabel.Width, this.mKNXLabel.Height);
    }

    @SuppressLint("DrawAllocation")
	@Override
    protected void onDraw(Canvas canvas) {
    	super.onDraw(canvas);

    	int backColor = Color.parseColor(this.mKNXLabel.BackgroundColor);
    	Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    	paint.setStyle(Paint.Style.FILL_AND_STROKE);	// 充满  
    	paint.setAlpha((int)(this.mKNXLabel.Alpha*255));
    	RectF oval3 = new RectF(0, 0, getWidth(), getHeight());// 设置个新的长方形  

    	if(EFlatStyle.Stereo == this.mKNXLabel.getFlatStyle()) {	// 画立体感的圆角矩形 

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
    		paint.setARGB((int)(this.mKNXLabel.Alpha*255), Color.red(backColor), Color.green(backColor), Color.blue(backColor));
    	}
    	canvas.drawRoundRect(oval3, this.mKNXLabel.Radius, this.mKNXLabel.Radius, paint);//第二个参数是x半径，第三个参数是y半径  

        if(null != this.mKNXLabel.getText()) {
        	/* 绘制文本 */
        	paint.reset();
        	paint.setColor(Color.parseColor(this.mKNXLabel.FontColor));
        	paint.setTextSize(this.mKNXLabel.FontSize);
        	Rect bound = new Rect();
        	paint.getTextBounds(this.mKNXLabel.getText(), 0, this.mKNXLabel.getText().length(), bound);
        	canvas.drawText(this.mKNXLabel.getText(), getWidth() / 2 - bound.width() / 2, getHeight() / 2 + bound.height() / 2, paint);
        }
        
        if(EBool.Yes == this.mKNXLabel.getDisplayBorder()) {
    		paint.reset();
    		paint.setStyle(Paint.Style.STROKE);
    		paint.setColor(Color.parseColor(this.mKNXLabel.BorderColor));
    		canvas.drawRoundRect(oval3, this.mKNXLabel.Radius, this.mKNXLabel.Radius, paint);//第二个参数是x半径，第三个参数是y半径  
    	}
    }

}
