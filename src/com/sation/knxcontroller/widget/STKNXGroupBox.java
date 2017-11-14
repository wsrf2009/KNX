package com.sation.knxcontroller.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.view.View;

import com.sation.knxcontroller.control.KNXGroupBox;
import com.sation.knxcontroller.control.KNXSceneButton;
import com.sation.knxcontroller.knxdpt.KNXDatapointType;
import com.sation.knxcontroller.models.KNXGroupAddress;
import com.sation.knxcontroller.models.KNXSelectedAddress;
import com.sation.knxcontroller.models.KNXView.EBool;
import com.sation.knxcontroller.models.KNXView.EFlatStyle;
import com.sation.knxcontroller.util.ColorUtils;
import com.sation.knxcontroller.util.Log;
import com.sation.knxcontroller.util.MapUtils;

public class STKNXGroupBox extends STKNXViewContainer {
	private final String TAG = "STKNXGroupBox";
	public KNXGroupBox mKNXGroupBox;

	public STKNXGroupBox(Context context, KNXGroupBox groupBox) {
		super(context, groupBox);

		this.mKNXGroupBox = groupBox; 
		this.setId(this.mKNXGroupBox.getId());
	}

	@Override
	public void onResume() { // 控件恢复
		int count = getChildCount();
		for(int i=0; i<count; i++) {
			View v = getChildAt(i);
			if(v instanceof STKNXView) {
				STKNXView sv = (STKNXView)v;
				sv.onResume();
			}
		}
	}

	@Override
	public void onSuspend() { // 控件挂起
		int count = getChildCount();
		for(int i=0; i<count; i++) {
			View v = getChildAt(i);
			if(v instanceof STKNXView) {
				STKNXView sv = (STKNXView)v;
				sv.onSuspend();
			}
		}
	}
	
	@Override
	public void onDestroy() { // 控件销毁
		super.onDestroy();

		Log.i(TAG, "");
		int count = getChildCount();
		for(int i=0; i<count; i++) {
			View v = getChildAt(i);
			if(v instanceof STKNXView) {
				STKNXView sv = (STKNXView)v;
				sv.onDestroy();
			}
		}
	}

	public void setSelectedValue(byte[] array) { // 组框与场景按钮工作于组模式时
		KNXGroupAddress address = this.mKNXGroupBox.getReadAddress();
		if (null != address) { // 组框的组地址有效？
			int value = KNXDatapointType.bytes2int(array, address.getType()); // 解析状态值
			for(int i=0; i<this.getChildCount(); i++) {
				View v = this.getChildAt(i);
				if(v instanceof STKNXSceneButton) { // 子控件，场景按钮？
					STKNXSceneButton mSTKNXSceneButton = (STKNXSceneButton)v;
					KNXSceneButton mKNXSceneButton = mSTKNXSceneButton.mKNXSceneButton;
					if(EBool.Yes == mKNXSceneButton.getIsGroup()) { // 场景按钮属于组？
						if(value == mKNXSceneButton.DefaultValue) {
							mSTKNXSceneButton.setSelected(true); // 选中？
						} else {
							mSTKNXSceneButton.setSelected(false);
						}
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

	@Override
	public void copyStatusAndRequest() { // 请求对象最新状态
		super.copyStatusAndRequest();

		byte[] bytes = getControlStatus(this.mKNXGroupBox.getReadAddressId(), true);
		if (null != bytes) {
			this.setSelectedValue(bytes);
		}
	}

	@Override
	public void statusUpdate(int asp, KNXGroupAddress address) { // 拷贝对象状态
		super.statusUpdate(asp, address);

		KNXSelectedAddress readAddr = MapUtils.getFirstOrNull(this.mKNXGroupBox.getReadAddressId());
		if (null != readAddr) {
			if (address.getId().equals(readAddr.getId())) {
				byte[] bytes = copyObjectStatus(asp);
				this.setSelectedValue(bytes);
			}
		}
	}
}
