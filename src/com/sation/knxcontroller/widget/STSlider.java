package com.sation.knxcontroller.widget;

import com.sation.knxcontroller.models.KNXView;
import com.sation.knxcontroller.util.ColorUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.view.View;

public class STSlider extends View {
	public int backColor;
	public int radius;
	public double alpha;
	public KNXView.EOrientation orientation;

	public enum EControlState {
		Down,
		Up,
	}
	public EControlState mEControlState;

	protected STSlider(Context context) {
		super(context);

		this.mEControlState = EControlState.Up;
	}
	
	public void onDestroy() {
		
	}

	@Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        /**
         * 最后调用父类方法,把View的大小告诉父布局。
         */
        setMeasuredDimension(/*this.width, this.height*/this.getWidth(), this.getHeight());
    }

	@SuppressLint("DrawAllocation")
	@Override
    protected void onDraw(Canvas canvas) {
    	super.onDraw(canvas);

    	Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    	paint.setStyle(Paint.Style.FILL_AND_STROKE);	// 充满
    	RectF oval3 = new RectF(0, 0, this.getWidth(), this.getHeight());

    	/* 绘制滑块 */
        int sliderColor = ColorUtils.changeBrightnessOfColor(this.backColor, 70);
        int[] sliderColors = new int[3];
        sliderColors[0] = ColorUtils.changeBrightnessOfColor(sliderColor, 100);
        sliderColors[1] = sliderColor;
        sliderColors[2] = ColorUtils.changeBrightnessOfColor(sliderColor, -30);

        float sliderPositions[] = new float[3];
		Shader sliderShader;

		if(KNXView.EOrientation.Horizontal == this.orientation) {
			sliderPositions[0] = .0f;
			sliderPositions[1] = .3f;
			sliderPositions[2] = 1.0f;

			sliderShader = new LinearGradient(0, 0, 0, getHeight(),
					sliderColors, sliderPositions, Shader.TileMode.CLAMP); // 设置渐变色 这个正方形的颜色是改变的 , 一个材质,打造出一个线性梯度沿著一条线。
		} else {
			sliderPositions[0] = .0f;
			sliderPositions[1] = .7f;
			sliderPositions[2] = 1.0f;
			sliderShader = new  LinearGradient(0, 0, getWidth(), 0,
					sliderColors, sliderPositions, Shader.TileMode.CLAMP);
		}
    	paint.setShader(sliderShader);
    	canvas.drawRoundRect(oval3, this.radius, this.radius, paint);

    	switch (this.mEControlState) {
			case Down:
				paint.reset();
				paint.setStyle(Paint.Style.FILL);
				paint.setColor(Color.parseColor("#FF6100"));
				paint.setAlpha(0x60);
				canvas.drawRoundRect(oval3, this.radius, this.radius, paint);	//第二个参数是x半径，第三个参数是y半径
				break;
			default:
				break;
    	}
	}


}
