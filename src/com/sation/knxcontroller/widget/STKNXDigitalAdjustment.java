<<<<<<< HEAD
package com.sation.knxcontroller.widget;

import com.sation.knxcontroller.STKNXControllerApp;
import com.sation.knxcontroller.STKNXControllerConstant;
import com.sation.knxcontroller.activity.RoomDetailsActivity;
import com.sation.knxcontroller.control.KNXDigitalAdjustment;
import com.sation.knxcontroller.knxdpt.DPT14;
import com.sation.knxcontroller.knxdpt.DPT7;
import com.sation.knxcontroller.knxdpt.DPT9;
import com.sation.knxcontroller.knxdpt.KNXDatapointType;
import com.sation.knxcontroller.models.KNXGroupAddress;
import com.sation.knxcontroller.models.KNXSelectedAddress;
import com.sation.knxcontroller.models.KNXView.EBool;
import com.sation.knxcontroller.models.KNXView.EFlatStyle;
import com.sation.knxcontroller.util.ColorUtils;
import com.sation.knxcontroller.util.ImageUtils;
import com.sation.knxcontroller.util.Log;
import com.sation.knxcontroller.util.MapUtils;
import com.sation.knxcontroller.util.MathUtils;
import com.sation.knxcontroller.util.StringUtil;
import com.sation.knxcontroller.util.uikit.UIKit;

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
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.lang.ref.WeakReference;

public class STKNXDigitalAdjustment extends STKNXControl {
	private static final String TAG = "STKNXDigitalAdjustment";
	final private static String DATATYPE_DOUBLE = "double";

	private static final int PADDING = 2;
	private static final int SUBVIEW_WIDTH = 40;
	private KNXDigitalAdjustment mKNXDigitalAdjustment;
    private double currentValue = 20;

	public STKNXDigitalAdjustment(Context context, KNXDigitalAdjustment knxDigitalAdjustment) {
		super(context, knxDigitalAdjustment);
		
		this.mKNXDigitalAdjustment = knxDigitalAdjustment;
		this.setId(mKNXDigitalAdjustment.getId());

		/* 左侧按键 */
		STButton vLeft = new STButton(context);
		vLeft.width = Math.max(this.mKNXDigitalAdjustment.Height-2*STKNXDigitalAdjustment.PADDING, STKNXDigitalAdjustment.SUBVIEW_WIDTH);
		vLeft.height = this.mKNXDigitalAdjustment.Height-2*STKNXDigitalAdjustment.PADDING;
		vLeft.left = STKNXDigitalAdjustment.PADDING;
		vLeft.top = STKNXDigitalAdjustment.PADDING;
		vLeft.backColor = Color.parseColor(this.mKNXDigitalAdjustment.BackgroundColor);
		vLeft.radius = this.mKNXDigitalAdjustment.Radius;
		vLeft.alpha = this.mKNXDigitalAdjustment.Alpha;
		vLeft.clickable = this.mKNXDigitalAdjustment.getClickable();
		vLeft.setSubViewClickListener(this.leftClicked);
		if(!StringUtil.isNullOrEmpty(this.mKNXDigitalAdjustment.getLeftImage())) {
			vLeft.setBackgroundImage(STKNXControllerConstant.ConfigResImgPath + this.mKNXDigitalAdjustment.getLeftImage());
		}
		this.addView(vLeft);

		/* 右侧按键 */
		STButton vRight = new STButton(context);
		vRight.width = Math.max(this.mKNXDigitalAdjustment.Height-2*STKNXDigitalAdjustment.PADDING, STKNXDigitalAdjustment.SUBVIEW_WIDTH);
		vRight.height = this.mKNXDigitalAdjustment.Height-2*STKNXDigitalAdjustment.PADDING;
		vRight.left = this.mKNXDigitalAdjustment.Width - STKNXDigitalAdjustment.PADDING - vRight.width;
		vRight.top = STKNXDigitalAdjustment.PADDING;
		vRight.backColor = Color.parseColor(this.mKNXDigitalAdjustment.BackgroundColor);
		vRight.radius = this.mKNXDigitalAdjustment.Radius;
		vRight.alpha = this.mKNXDigitalAdjustment.Alpha;
		vRight.clickable = this.mKNXDigitalAdjustment.getClickable();
		vRight.setSubViewClickListener(this.rightClicked);
		if(!StringUtil.isNullOrEmpty(this.mKNXDigitalAdjustment.getLeftImage())) {
			vRight.setBackgroundImage(STKNXControllerConstant.ConfigResImgPath + this.mKNXDigitalAdjustment.getRightImage());
		}
		this.addView(vRight);
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

	private static class STKNXDigitalAdjustmentHandler extends Handler {
		WeakReference<STKNXDigitalAdjustment> mDigitalAdjustment;

		private STKNXDigitalAdjustmentHandler(STKNXDigitalAdjustment da) {
			super(da.getContext().getMainLooper());

			mDigitalAdjustment = new WeakReference<STKNXDigitalAdjustment>(da);
		}

		public void handleMessage(Message msg) {
			double val = msg.getData().getDouble(DATATYPE_DOUBLE);
			updateValue(mDigitalAdjustment.get(), val); // 更新数值
		}
	}

	private void updateControlState(double val) {
		STKNXDigitalAdjustmentHandler mHandler = new STKNXDigitalAdjustmentHandler(STKNXDigitalAdjustment.this);
		Bundle bundle = new Bundle();
		bundle.putDouble(DATATYPE_DOUBLE, val);
		Message msg = new Message();
		msg.setData(bundle);
		mHandler.sendMessage(msg);
	}

	private double getDigitalValue(double val) {
		if(val < mKNXDigitalAdjustment.getMinValue()) {
			val = mKNXDigitalAdjustment.getMinValue();
		} else if(val > mKNXDigitalAdjustment.getMaxValue()) {
			val = mKNXDigitalAdjustment.getMaxValue();
		}

		return val;
	}
	
	STButton.SubViewClickListener leftClicked = new STButton.SubViewClickListener() {

		@Override
		public void onClick(STButton view) { // 左侧按钮点击 －
			try {
				double val = currentValue;
				double step = Double.parseDouble(mKNXDigitalAdjustment.getRegulationStep());
				val = getDigitalValue(val-step);

				if(mKNXDigitalAdjustment.getDecimalDigitInt() <= 0) {
					int ival = (int)val;
					sendCommandRequest(mKNXDigitalAdjustment.getWriteAddressIds(), ival+"", false, null);
				} else {
					val = MathUtils.Rounding(val, (mKNXDigitalAdjustment.getDecimalDigitInt()));
					sendCommandRequest(mKNXDigitalAdjustment.getWriteAddressIds(), val+"", false, null);
				}

				updateValue(STKNXDigitalAdjustment.this, val);
			} catch (Exception e) {
				Log.e("STKNXDigitalAdjustment", e.getLocalizedMessage());
			}
		}
	};

	STButton.SubViewClickListener rightClicked = new STButton.SubViewClickListener() {

		@Override
		public void onClick(STButton view) { // 右侧按钮点击 +
			try {
				double val = currentValue;
				double step = Double.parseDouble(mKNXDigitalAdjustment.getRegulationStep());
				val = getDigitalValue(val+step);

				if(mKNXDigitalAdjustment.getDecimalDigitInt() <= 0) {
					int ival = (int)val;
					sendCommandRequest(mKNXDigitalAdjustment.getWriteAddressIds(), ival+"", false, null);
				} else {
					val = MathUtils.Rounding(val, (mKNXDigitalAdjustment.getDecimalDigitInt()));
					sendCommandRequest(mKNXDigitalAdjustment.getWriteAddressIds(), val+"", false, null);
				}

				updateValue(STKNXDigitalAdjustment.this, val);
			} catch (Exception e) {
				Log.e("STKNXDigitalAdjustment", e.getLocalizedMessage());
			}
		}
	};

	public void setValue(byte[] array) { // 解析数据
		KNXGroupAddress address = this.mKNXDigitalAdjustment.getReadAddress();
		if(null != address) {
			double value;
			/* 按照数据类型解析 */
			if (address.getKnxMainNumber().equals(KNXDatapointType.DPT_7)) {
				value = DPT7.bytes2int(array);
			} else if (address.getKnxMainNumber().equals(KNXDatapointType.DPT_9)) {
				value = DPT9.bytes2float(array);
			} else if(address.getKnxMainNumber().equals(KNXDatapointType.DPT_14)) {
				value = DPT14.bytes2float(array);
			} else {
				value = KNXDatapointType.bytes2int(array, address.getType());
			}

			value = MathUtils.Rounding(value, (mKNXDigitalAdjustment.getDecimalDigitInt()));

			updateControlState(value);
		}
	}

	private static void updateValue(STKNXDigitalAdjustment mDigitalAdjustment, double val) {
		mDigitalAdjustment.currentValue = val;
		mDigitalAdjustment.invalidate();
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
			int cl = 0, ct = 0, cr = 0, cb = 0;
			View v = getChildAt(i);
			if(v instanceof STButton) {
				STButton childView = (STButton) getChildAt(i);

				cl = childView.left;
				ct = childView.top;
				cr = childView.left + childView.width;
				cb = childView.top + childView.height;

				childView.layout(cl, ct, cr, cb);
			} else if (v instanceof ImageButton) {
				cl = v.getLeft();
				ct = v.getTop();
				cr = v.getLeft() + v.getWidth();
				cb = v.getTop() + v.getHeight();

				v.layout(cl, ct, cr, cb);
			}
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

		String str="";
		if(mKNXDigitalAdjustment.getDecimalDigitInt() <= 0) {
			str = (int)this.currentValue +"";
		} else {
			str = this.currentValue + "";
		}
    	str += " " + this.mKNXDigitalAdjustment.getUnit();

        /* 绘制文本 */
		Paint textPaint = this.mKNXDigitalAdjustment.ValueFont.getTextPaint();
		int baseY = (int) ((rect1.height() / 2) - ((textPaint.descent() + textPaint.ascent()) / 2));
		canvas.drawText(str, rect1.width()/2, baseY, textPaint);
        
        if(EBool.Yes == this.mKNXDigitalAdjustment.getDisplayBorder()) {
    		paint.reset();
    		paint.setStyle(Paint.Style.STROKE);
    		paint.setColor(Color.parseColor(this.mKNXDigitalAdjustment.BorderColor));
    		canvas.drawRoundRect(rect1, this.mKNXDigitalAdjustment.Radius, this.mKNXDigitalAdjustment.Radius, paint);//第二个参数是x半径，第三个参数是y半径  
    	}
    }

	@Override
	public void copyStatusAndRequest() {
		super.copyStatusAndRequest();

		byte[] bytes = getControlStatus(this.mKNXDigitalAdjustment.getReadAddressId(), true);
		if (null != bytes) {
			this.setValue(bytes);
		}
	}

	@Override
	public void statusUpdate(int asp, KNXGroupAddress address) {
		super.statusUpdate(asp, address);

		KNXSelectedAddress readAddr = MapUtils.getFirstOrNull(this.mKNXDigitalAdjustment.getReadAddressId());
		if (null != readAddr) {
			if (address.getId().equals(readAddr.getId())) {
				byte[] bytes = copyObjectStatus(asp);
				this.setValue(bytes);
			}
		}
	}
=======
package com.sation.knxcontroller.widget;

import com.sation.knxcontroller.STKNXControllerApp;
import com.sation.knxcontroller.STKNXControllerConstant;
import com.sation.knxcontroller.activity.RoomDetailsActivity;
import com.sation.knxcontroller.control.KNXDigitalAdjustment;
import com.sation.knxcontroller.knxdpt.DPT14;
import com.sation.knxcontroller.knxdpt.DPT7;
import com.sation.knxcontroller.knxdpt.DPT9;
import com.sation.knxcontroller.knxdpt.KNXDatapointType;
import com.sation.knxcontroller.models.KNXGroupAddress;
import com.sation.knxcontroller.models.KNXSelectedAddress;
import com.sation.knxcontroller.models.KNXView.EBool;
import com.sation.knxcontroller.models.KNXView.EFlatStyle;
import com.sation.knxcontroller.util.ColorUtils;
import com.sation.knxcontroller.util.ImageUtils;
import com.sation.knxcontroller.util.Log;
import com.sation.knxcontroller.util.MapUtils;
import com.sation.knxcontroller.util.MathUtils;
import com.sation.knxcontroller.util.StringUtil;
import com.sation.knxcontroller.util.uikit.UIKit;

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
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.lang.ref.WeakReference;

public class STKNXDigitalAdjustment extends STKNXControl {
	private static final String TAG = "STKNXDigitalAdjustment";
	final private static String DATATYPE_DOUBLE = "double";

	private static final int PADDING = 2;
	private static final int SUBVIEW_WIDTH = 40;
	private KNXDigitalAdjustment mKNXDigitalAdjustment;
    private double currentValue = 20;

	public STKNXDigitalAdjustment(Context context, KNXDigitalAdjustment knxDigitalAdjustment) {
		super(context, knxDigitalAdjustment);
		
		this.mKNXDigitalAdjustment = knxDigitalAdjustment;
		this.setId(mKNXDigitalAdjustment.getId());

		/* 左侧按键 */
		STButton vLeft = new STButton(context);
		vLeft.width = Math.max(this.mKNXDigitalAdjustment.Height-2*STKNXDigitalAdjustment.PADDING, STKNXDigitalAdjustment.SUBVIEW_WIDTH);
		vLeft.height = this.mKNXDigitalAdjustment.Height-2*STKNXDigitalAdjustment.PADDING;
		vLeft.left = STKNXDigitalAdjustment.PADDING;
		vLeft.top = STKNXDigitalAdjustment.PADDING;
		vLeft.backColor = Color.parseColor(this.mKNXDigitalAdjustment.BackgroundColor);
		vLeft.radius = this.mKNXDigitalAdjustment.Radius;
		vLeft.alpha = this.mKNXDigitalAdjustment.Alpha;
		vLeft.clickable = this.mKNXDigitalAdjustment.getClickable();
		vLeft.setSubViewClickListener(this.leftClicked);
		if(!StringUtil.isNullOrEmpty(this.mKNXDigitalAdjustment.getLeftImage())) {
			vLeft.setBackgroundImage(STKNXControllerConstant.ConfigResImgPath + this.mKNXDigitalAdjustment.getLeftImage());
		}
		this.addView(vLeft);

		/* 右侧按键 */
		STButton vRight = new STButton(context);
		vRight.width = Math.max(this.mKNXDigitalAdjustment.Height-2*STKNXDigitalAdjustment.PADDING, STKNXDigitalAdjustment.SUBVIEW_WIDTH);
		vRight.height = this.mKNXDigitalAdjustment.Height-2*STKNXDigitalAdjustment.PADDING;
		vRight.left = this.mKNXDigitalAdjustment.Width - STKNXDigitalAdjustment.PADDING - vRight.width;
		vRight.top = STKNXDigitalAdjustment.PADDING;
		vRight.backColor = Color.parseColor(this.mKNXDigitalAdjustment.BackgroundColor);
		vRight.radius = this.mKNXDigitalAdjustment.Radius;
		vRight.alpha = this.mKNXDigitalAdjustment.Alpha;
		vRight.clickable = this.mKNXDigitalAdjustment.getClickable();
		vRight.setSubViewClickListener(this.rightClicked);
		if(!StringUtil.isNullOrEmpty(this.mKNXDigitalAdjustment.getLeftImage())) {
			vRight.setBackgroundImage(STKNXControllerConstant.ConfigResImgPath + this.mKNXDigitalAdjustment.getRightImage());
		}
		this.addView(vRight);
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

	private static class STKNXDigitalAdjustmentHandler extends Handler {
		WeakReference<STKNXDigitalAdjustment> mDigitalAdjustment;

		private STKNXDigitalAdjustmentHandler(STKNXDigitalAdjustment da) {
			super(da.getContext().getMainLooper());

			mDigitalAdjustment = new WeakReference<STKNXDigitalAdjustment>(da);
		}

		public void handleMessage(Message msg) {
			double val = msg.getData().getDouble(DATATYPE_DOUBLE);
			updateValue(mDigitalAdjustment.get(), val); // 更新数值
		}
	}

	private void updateControlState(double val) {
		STKNXDigitalAdjustmentHandler mHandler = new STKNXDigitalAdjustmentHandler(STKNXDigitalAdjustment.this);
		Bundle bundle = new Bundle();
		bundle.putDouble(DATATYPE_DOUBLE, val);
		Message msg = new Message();
		msg.setData(bundle);
		mHandler.sendMessage(msg);
	}

	private double getDigitalValue(double val) {
		if(val < mKNXDigitalAdjustment.getMinValue()) {
			val = mKNXDigitalAdjustment.getMinValue();
		} else if(val > mKNXDigitalAdjustment.getMaxValue()) {
			val = mKNXDigitalAdjustment.getMaxValue();
		}

		return val;
	}
	
	STButton.SubViewClickListener leftClicked = new STButton.SubViewClickListener() {

		@Override
		public void onClick(STButton view) { // 左侧按钮点击 －
			try {
				double val = currentValue;
				double step = Double.parseDouble(mKNXDigitalAdjustment.getRegulationStep());
				val = getDigitalValue(val-step);

				if(mKNXDigitalAdjustment.getDecimalDigitInt() <= 0) {
					int ival = (int)val;
					sendCommandRequest(mKNXDigitalAdjustment.getWriteAddressIds(), ival+"", false, null);
				} else {
					val = MathUtils.Rounding(val, (mKNXDigitalAdjustment.getDecimalDigitInt()));
					sendCommandRequest(mKNXDigitalAdjustment.getWriteAddressIds(), val+"", false, null);
				}

				updateValue(STKNXDigitalAdjustment.this, val);
			} catch (Exception e) {
				Log.e("STKNXDigitalAdjustment", e.getLocalizedMessage());
			}
		}
	};

	STButton.SubViewClickListener rightClicked = new STButton.SubViewClickListener() {

		@Override
		public void onClick(STButton view) { // 右侧按钮点击 +
			try {
				double val = currentValue;
				double step = Double.parseDouble(mKNXDigitalAdjustment.getRegulationStep());
				val = getDigitalValue(val+step);

				if(mKNXDigitalAdjustment.getDecimalDigitInt() <= 0) {
					int ival = (int)val;
					sendCommandRequest(mKNXDigitalAdjustment.getWriteAddressIds(), ival+"", false, null);
				} else {
					val = MathUtils.Rounding(val, (mKNXDigitalAdjustment.getDecimalDigitInt()));
					sendCommandRequest(mKNXDigitalAdjustment.getWriteAddressIds(), val+"", false, null);
				}

				updateValue(STKNXDigitalAdjustment.this, val);
			} catch (Exception e) {
				Log.e("STKNXDigitalAdjustment", e.getLocalizedMessage());
			}
		}
	};

	public void setValue(byte[] array) { // 解析数据
		KNXGroupAddress address = this.mKNXDigitalAdjustment.getReadAddress();
		if(null != address) {
			double value;
			/* 按照数据类型解析 */
			if (address.getKnxMainNumber().equals(KNXDatapointType.DPT_7)) {
				value = DPT7.bytes2int(array);
			} else if (address.getKnxMainNumber().equals(KNXDatapointType.DPT_9)) {
				value = DPT9.bytes2float(array);
			} else if(address.getKnxMainNumber().equals(KNXDatapointType.DPT_14)) {
				value = DPT14.bytes2float(array);
			} else {
				value = KNXDatapointType.bytes2int(array, address.getType());
			}

			value = MathUtils.Rounding(value, (mKNXDigitalAdjustment.getDecimalDigitInt()));

			updateControlState(value);
		}
	}

	private static void updateValue(STKNXDigitalAdjustment mDigitalAdjustment, double val) {
		mDigitalAdjustment.currentValue = val;
		mDigitalAdjustment.invalidate();
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
			int cl = 0, ct = 0, cr = 0, cb = 0;
			View v = getChildAt(i);
			if(v instanceof STButton) {
				STButton childView = (STButton) getChildAt(i);

				cl = childView.left;
				ct = childView.top;
				cr = childView.left + childView.width;
				cb = childView.top + childView.height;

				childView.layout(cl, ct, cr, cb);
			} else if (v instanceof ImageButton) {
				cl = v.getLeft();
				ct = v.getTop();
				cr = v.getLeft() + v.getWidth();
				cb = v.getTop() + v.getHeight();

				v.layout(cl, ct, cr, cb);
			}
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

		String str="";
		if(mKNXDigitalAdjustment.getDecimalDigitInt() <= 0) {
			str = (int)this.currentValue +"";
		} else {
			str = this.currentValue + "";
		}
    	str += " " + this.mKNXDigitalAdjustment.getUnit();

        /* 绘制文本 */
		Paint textPaint = this.mKNXDigitalAdjustment.ValueFont.getTextPaint();
		int baseY = (int) ((rect1.height() / 2) - ((textPaint.descent() + textPaint.ascent()) / 2));
		canvas.drawText(str, rect1.width()/2, baseY, textPaint);
        
        if(EBool.Yes == this.mKNXDigitalAdjustment.getDisplayBorder()) {
    		paint.reset();
    		paint.setStyle(Paint.Style.STROKE);
    		paint.setColor(Color.parseColor(this.mKNXDigitalAdjustment.BorderColor));
    		canvas.drawRoundRect(rect1, this.mKNXDigitalAdjustment.Radius, this.mKNXDigitalAdjustment.Radius, paint);//第二个参数是x半径，第三个参数是y半径  
    	}
    }

	@Override
	public void copyStatusAndRequest() {
		super.copyStatusAndRequest();

		byte[] bytes = getControlStatus(this.mKNXDigitalAdjustment.getReadAddressId(), true);
		if (null != bytes) {
			this.setValue(bytes);
		}
	}

	@Override
	public void statusUpdate(int asp, KNXGroupAddress address) {
		super.statusUpdate(asp, address);

		KNXSelectedAddress readAddr = MapUtils.getFirstOrNull(this.mKNXDigitalAdjustment.getReadAddressId());
		if (null != readAddr) {
			if (address.getId().equals(readAddr.getId())) {
				byte[] bytes = copyObjectStatus(asp);
				this.setValue(bytes);
			}
		}
	}
>>>>>>> SationCentralControl(10inch)
}