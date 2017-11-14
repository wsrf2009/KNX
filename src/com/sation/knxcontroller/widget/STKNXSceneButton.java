package com.sation.knxcontroller.widget;

import com.sation.knxcontroller.STKNXControllerConstant;
import com.sation.knxcontroller.control.KNXSceneButton;
import com.sation.knxcontroller.models.KNXView.EBool;
import com.sation.knxcontroller.models.KNXView.EFlatStyle;
import com.sation.knxcontroller.util.ColorUtils;
import com.sation.knxcontroller.util.ImageUtils;
import com.sation.knxcontroller.util.Log;
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
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;

import java.lang.ref.WeakReference;

public class STKNXSceneButton extends STKNXControl {
	private static final String TAG = "STKNXSceneButton";
	private final int PADDING = 2;

	private ImageView mImageView;
	private Bitmap imageOn;
	private Bitmap imageOff;

	public KNXSceneButton mKNXSceneButton;

	private enum ControlState {
		Down,
		Normal,
	}

	private ControlState mControlState;

	private enum SceneState {
		On,
		Off,
	}

	private SceneState mSceneState;

	private int imgY = 0;

	public STKNXSceneButton(Context context, KNXSceneButton knxSceneButton) {
		super(context, knxSceneButton);

		this.mKNXSceneButton = knxSceneButton;
		this.setId(mKNXSceneButton.getId());

		this.mControlState = ControlState.Normal;
		this.mSceneState = SceneState.Off;

		this.mImageView = new ImageView(context);
		this.addView(this.mImageView);

		new Thread(new Runnable() {
			@Override
			public void run() {
				if(!StringUtil.isNullOrEmpty(mKNXSceneButton.getImageOn())) {
					imageOn = ImageUtils.getDiskBitmap(STKNXControllerConstant.ConfigResImgPath +
									mKNXSceneButton.getImageOn(),
							getImageViewLength(), getImageViewLength());
				}

				if(!StringUtil.isNullOrEmpty(mKNXSceneButton.getImageOff())) {
					imageOff = ImageUtils.getDiskBitmap(STKNXControllerConstant.ConfigResImgPath +
									mKNXSceneButton.getImageOff(),
							getImageViewLength(), getImageViewLength());
				}

				updateControlState();
			}
		}).start();
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
	}

	private static class STKNXSceneButtonHandler extends Handler {
		WeakReference<STKNXSceneButton> mSceneButton;

		private STKNXSceneButtonHandler(STKNXSceneButton s) {
			super(s.getContext().getMainLooper());

			mSceneButton = new WeakReference<STKNXSceneButton>(s);
		}

		@Override
		public void handleMessage(Message msg) {
			setControlImage(mSceneButton.get());
		}
	}

	private static void setControlImage(STKNXSceneButton mSceneButton) {
		if(null != mSceneButton.mImageView) {
			if (SceneState.On == mSceneButton.mSceneState) {
				if (null != mSceneButton.imageOn) {
					mSceneButton.mImageView.setImageBitmap(mSceneButton.imageOn);
				}
			} else if (SceneState.Off == mSceneButton.mSceneState) {
				if (null != mSceneButton.imageOff) {
					mSceneButton.mImageView.setImageBitmap(mSceneButton.imageOff);
				}
			}
		}

		mSceneButton.invalidate();
	}

	private void updateControlState() {
		STKNXSceneButtonHandler mHandler = new STKNXSceneButtonHandler(STKNXSceneButton.this);
		mHandler.sendEmptyMessage(0);
	}

	public void setSelected(final boolean s) {
		if (s) {
			this.mSceneState = SceneState.On;
		} else {

			this.mSceneState = SceneState.Off;
		}
		updateControlState();
	}

	private int getImageViewLength(){
		int w = this.mKNXSceneButton.Width - 2 * this.imgY;
		int h = this.mKNXSceneButton.Height - 2 * this.imgY;
		return w > h ? h:w;
	}

	private void onClick() {
		if (null == this.mKNXSceneButton) {
			return;
		}

		if (EBool.Yes == this.mKNXSceneButton.getIsGroup()) { // 若属于组？有互斥效果
			if (this.getParent() instanceof STKNXGroupBox) { // 父视图必须为组框
				STKNXGroupBox mSTKNXGroupBox = (STKNXGroupBox) this.getParent();
				if ((null != mSTKNXGroupBox) && (null != mSTKNXGroupBox.mKNXGroupBox)) {
					if (mSTKNXGroupBox.mKNXGroupBox.getReadAddressId().isEmpty() ||
							mSTKNXGroupBox.mKNXGroupBox.getWriteAddressIds().containsKey( // 组框的读写地址才有效
									mSTKNXGroupBox.mKNXGroupBox.getReadAddressId().keySet().toArray()[0])) {
						byte[] b = new byte[1];
						b[0] = (byte)(this.mKNXSceneButton.DefaultValue & 0xFF);
						mSTKNXGroupBox.setSelectedValue(b);
					}

					sendCommandRequest(mSTKNXGroupBox.mKNXGroupBox.getWriteAddressIds(), this.mKNXSceneButton.DefaultValue + "", false, null);
				}
			}
		} else {
			if (this.mKNXSceneButton.getReadAddressId().isEmpty() ||
					this.mKNXSceneButton.getWriteAddressIds().containsKey(// 非组的工作模式下，场景按钮的读写地址才有效
							this.mKNXSceneButton.getReadAddressId().keySet().toArray()[0])) {
				if (SceneState.On == this.mSceneState) {
					this.mSceneState = SceneState.Off;
				} else {
					this.mSceneState = SceneState.On;
				}
			}

			sendCommandRequest(this.mKNXSceneButton.getWriteAddressIds(), "", true, null);
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		measureChildren(widthMeasureSpec, heightMeasureSpec);

		/**
		 * 最后调用父类方法,把View的大小告诉父布局。
		 */
		setMeasuredDimension(this.mKNXSceneButton.Width, this.mKNXSceneButton.Height);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int childViewCount = getChildCount();

		/*
		 * 遍历所有childView根据其宽和高，以及margin进行布局
		 */
		for (int i = 0; i < childViewCount; i++) {
			int cl = 0, ct = 0, cr = 0, cb = 0;

			View view = getChildAt(i);
			if (view instanceof ImageView) {
				int imgHeight = getImageViewLength();

				cl = this.mKNXSceneButton.getPadding().getLeft();
				ct = this.mKNXSceneButton.getPadding().getTop();
				cr = this.mKNXSceneButton.Width - this.mKNXSceneButton.getPadding().getRight();
				cb = this.mKNXSceneButton.Height - this.mKNXSceneButton.getPadding().getBottom();
			}
			view.layout(cl, ct, cr, cb);
		}
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setStyle(Paint.Style.FILL_AND_STROKE);    // 充满
		paint.setAlpha((int) (this.mKNXSceneButton.Alpha * 255));
		RectF oval3 = new RectF(0, 0, getWidth(), getHeight());// 设置个新的长方形

		int backColor = Color.parseColor(this.mKNXSceneButton.BackgroundColor);
		if ((SceneState.On == this.mSceneState) && (null != this.mKNXSceneButton.ColorOn)) {
			backColor = Color.parseColor(this.mKNXSceneButton.ColorOn);
		} else if ((SceneState.Off == this.mSceneState) && (null != this.mKNXSceneButton.ColorOff)) {
			backColor = Color.parseColor(this.mKNXSceneButton.ColorOff);
		}

		if (EFlatStyle.Stereo == this.mKNXSceneButton.getFlatStyle()) {    // 画立体感的圆角矩形
        
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
		} else {    // 画扁平风格的圆角矩形
			paint.setARGB((int) (this.mKNXSceneButton.Alpha * 255), Color.red(backColor), Color.green(backColor), Color.blue(backColor));
		}
		canvas.drawRoundRect(oval3, this.mKNXSceneButton.Radius, this.mKNXSceneButton.Radius, paint);//第二个参数是x半径，第三个参数是y半径

		if (null != this.mKNXSceneButton.getTitle()) {
			Paint textPaint = this.mKNXSceneButton.TitleFont.getTextPaint();

			int baseY = (int) ((oval3.height() / 2) - ((textPaint.descent() + textPaint.ascent()) / 2));
			canvas.drawText(this.mKNXSceneButton.getTitle(), oval3.width()/2, baseY, textPaint);
		}

		switch (mControlState) {
			case Down:
				paint.reset();
				paint.setStyle(Paint.Style.FILL);
				paint.setColor(Color.parseColor("#FF6100"));
				paint.setAlpha(0x60);
				canvas.drawRoundRect(oval3, this.mKNXSceneButton.Radius, this.mKNXSceneButton.Radius, paint);    //第二个参数是x半径，第三个参数是y半径
				break;
			case Normal:
				break;
		}

		if (EBool.Yes == this.mKNXSceneButton.getDisplayBorder()) {
			paint.reset();
			paint.setStyle(Paint.Style.STROKE);
			paint.setColor(Color.parseColor(this.mKNXSceneButton.BorderColor));
			canvas.drawRoundRect(oval3, this.mKNXSceneButton.Radius, this.mKNXSceneButton.Radius, paint);//第二个参数是x半径，第三个参数是y半径
		}
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(EBool.Yes != this.mKNXSceneButton.getClickable()) { // 是否可点击？
			return true;
		}

		try {
			switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					this.mControlState = ControlState.Down;
					invalidate(); // 重绘控件
					if (null != this.mImageView) {
						this.mImageView.setAlpha(0.6f);
					}
					break;
				case MotionEvent.ACTION_UP:
					onClick(); // 点击事件
					this.mControlState = ControlState.Normal;
					invalidate();
					if (null != this.mImageView) {
						this.mImageView.setAlpha(1.0f);
					}
					break;
				case MotionEvent.ACTION_CANCEL:
					this.mControlState = ControlState.Normal;
					invalidate();
					break;

				default:
					break;
			}

			return true;

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return true;
	}
}
