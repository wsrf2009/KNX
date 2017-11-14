package com.sation.knxcontroller.widget;

import com.sation.knxcontroller.STKNXControllerApp;
import com.sation.knxcontroller.control.KNXValueDisplay;
import com.sation.knxcontroller.knxdpt.DPT14;
import com.sation.knxcontroller.knxdpt.DPT7;
import com.sation.knxcontroller.knxdpt.DPT9;
import com.sation.knxcontroller.knxdpt.KNXDatapointType;
import com.sation.knxcontroller.models.KNXGroupAddress;
import com.sation.knxcontroller.models.KNXSelectedAddress;
import com.sation.knxcontroller.models.KNXView.EBool;
import com.sation.knxcontroller.models.KNXView.EFlatStyle;
import com.sation.knxcontroller.util.ColorUtils;
import com.sation.knxcontroller.util.Log;
import com.sation.knxcontroller.util.MapUtils;
import com.sation.knxcontroller.util.MathUtils;
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

import java.lang.ref.WeakReference;

public class STKNXValueDisplay extends STKNXControl {
	private static final String TAG = "STKNXValueDisplay";
	private final int PADDING = 2;

	private KNXValueDisplay mKNXValueDisplay;
	private String valueString = "---";

	public STKNXValueDisplay(Context context, KNXValueDisplay knxValueDisplay) {
		super(context, knxValueDisplay);

		this.mKNXValueDisplay = knxValueDisplay;
		this.setId(mKNXValueDisplay.getId());
	}

	@Override
	public void onSuspend() {

	}

	@Override
	public void onResume() {
		copyStatusAndRequest();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	private static class STKNXValueDisplayHandler extends Handler {
		WeakReference<STKNXValueDisplay> mValueDisplay;

		private STKNXValueDisplayHandler(STKNXValueDisplay vd) {
			super(vd.getContext().getMainLooper());

			mValueDisplay = new WeakReference<STKNXValueDisplay>(vd);
		}

		@Override
		public void handleMessage(Message msg) {
			setControlImage(mValueDisplay.get());
		}
	}

	private static void setControlImage(STKNXValueDisplay mValueDisplay) {
		mValueDisplay.invalidate();
	}

	private void updateControlState() {
		STKNXValueDisplayHandler mHandler = new STKNXValueDisplayHandler(STKNXValueDisplay.this);
		mHandler.sendEmptyMessage(0);
	}
	
	public void setValue(byte[] array) {
		KNXGroupAddress address = this.mKNXValueDisplay.getReadAddress();
		if (null != address) {
			double value;

			/* 根据组地址的DPT解析数据 */
			if (address.getKnxMainNumber().equals(KNXDatapointType.DPT_7)) {
				value = DPT7.bytes2int(array);
			} else if (address.getKnxMainNumber().equals(KNXDatapointType.DPT_9)) {
				value = DPT9.bytes2float(array);
			} else if (address.getKnxMainNumber().equals(KNXDatapointType.DPT_14)) {
				value = DPT14.bytes2float(array);
			} else {
				value = KNXDatapointType.bytes2int(array, address.getType());
			}

            if(mKNXValueDisplay.getDecimalDigitInt() <= 0) {
                this.valueString = (int)value +"";
            } else {
                this.valueString = MathUtils.Rounding(value, (this.mKNXValueDisplay.getDecimalDigitInt())) + "";
            }
            this.valueString += " " + this.mKNXValueDisplay.getUnit();

			updateControlState();
		}
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

    	if(null != this.valueString) {
			Paint textPaint = this.mKNXValueDisplay.ValueFont.getTextPaint();
			int baseY = (int) ((oval3.height() / 2) - ((textPaint.descent() + textPaint.ascent()) / 2));
			canvas.drawText(this.valueString, oval3.width()/2, baseY, textPaint);
    	}
    	
    	if(EBool.Yes == this.mKNXValueDisplay.getDisplayBorder()) {
    		paint.reset();
    		paint.setStyle(Paint.Style.STROKE);
    		paint.setColor(Color.parseColor(this.mKNXValueDisplay.BorderColor));
    		canvas.drawRoundRect(oval3, this.mKNXValueDisplay.Radius, this.mKNXValueDisplay.Radius, paint);//第二个参数是x半径，第三个参数是y半径  
    	}
	}

	@Override
	public void copyStatusAndRequest() {
		super.copyStatusAndRequest();

		byte[] bytes = getControlStatus(this.mKNXValueDisplay.getReadAddressId(), true);
		if (null != bytes) {
			this.setValue(bytes);
		}
	}

	@Override
	public void statusUpdate(int asp, KNXGroupAddress address) {
		super.statusUpdate(asp, address);

		KNXSelectedAddress readAddr = MapUtils.getFirstOrNull(this.mKNXValueDisplay.getReadAddressId());
		if  (null != readAddr) {
			if (address.getId().equals(readAddr.getId())) {
				byte[] bytes = copyObjectStatus(asp);
				this.setValue(bytes);
			}
		}
	}
}
