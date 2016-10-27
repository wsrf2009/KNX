package com.sation.knxcontroller.widget;

import com.sation.knxcontroller.control.KNXGroupBox;
import com.sation.knxcontroller.control.KNXSceneButton;
import com.sation.knxcontroller.models.KNXView.EBool;
import com.sation.knxcontroller.models.KNXView.EFlatStyle;
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

public class STKNXGroupBox extends STKNXViewContainer {
	public KNXGroupBox mKNXGroupBox;

	public STKNXGroupBox(Context context, KNXGroupBox groupBox) {
		super(context, groupBox);

		this.mKNXGroupBox = groupBox; 
		this.setId(this.mKNXGroupBox.getId());
	}
	
	@Override
	public void onDestroy() {
//		this.mKNXGroupBox = null;
		
		int count = getChildCount();
		for(int i=0; i<count; i++) {
			View v = (View)getChildAt(i);
			if(v instanceof STKNXView) {
				STKNXView sv = (STKNXView)v;
				sv.onDestroy();
				sv = null;
			}
		}
	}

	public void setSelectedValue(int value) {
		for(int i=0; i<this.getChildCount(); i++) {
			View v = this.getChildAt(i);
			if(v instanceof STKNXSceneButton) {
				STKNXSceneButton mSTKNXSceneButton = (STKNXSceneButton)v;
				KNXSceneButton mKNXSceneButton = mSTKNXSceneButton.mKNXSceneButton;
				if(EBool.Yes == mKNXSceneButton.getIsGroup()) {
					if(value == mKNXSceneButton.DefaultValue) {
						mSTKNXSceneButton.setSelected(true);
					} else {
						mSTKNXSceneButton.setSelected(false);
					}
				}
			}
		}
	}
	
    @SuppressLint("DrawAllocation")
	protected void onDraw(Canvas canvas) {
    	super.onDraw(canvas);

    	int backColor = Color.parseColor(this.mKNXGroupBox.BackgroundColor);
    	Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    	paint.setStyle(Paint.Style.FILL_AND_STROKE);	// 充满  
    	paint.setAlpha((int)(this.mKNXGroupBox.Alpha*255));
    	
        int x = 0;
        int y = 0;  // 
        int width = this.getWidth();
        int height = this.getHeight() - 2 * y;
        RectF rect1 = new RectF(x, y, x+width, y+height);
        if(EFlatStyle.Stereo == this.mKNXGroupBox.getFlatStyle()) {	// 画立体感的圆角矩形 
            
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
    		paint.setARGB((int)(this.mKNXGroupBox.Alpha*255), Color.red(backColor), Color.green(backColor), Color.blue(backColor));
    	}
    	canvas.drawRoundRect(rect1, this.mKNXGroupBox.Radius, this.mKNXGroupBox.Radius, paint);//第二个参数是x半径，第三个参数是y半径  
        
        if(EBool.Yes == this.mKNXGroupBox.getDisplayBorder()) {
    		paint.reset();
    		paint.setStyle(Paint.Style.STROKE);
    		paint.setColor(Color.parseColor(this.mKNXGroupBox.BorderColor));
    		canvas.drawRoundRect(rect1, this.mKNXGroupBox.Radius, this.mKNXGroupBox.Radius, paint);//第二个参数是x半径，第三个参数是y半径  
    	}
    }
}
