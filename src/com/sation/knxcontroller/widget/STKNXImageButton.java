package com.sation.knxcontroller.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.sation.knxcontroller.R;
import com.sation.knxcontroller.STKNXControllerApp;
import com.sation.knxcontroller.STKNXControllerConstant;
import com.sation.knxcontroller.control.KNXImageButton;
import com.sation.knxcontroller.knxdpt.DPT1;
import com.sation.knxcontroller.knxdpt.KNXDatapointType;
import com.sation.knxcontroller.models.KNXGroupAddress;
import com.sation.knxcontroller.models.KNXSelectedAddress;
import com.sation.knxcontroller.models.KNXView;
import com.sation.knxcontroller.util.ColorUtils;
import com.sation.knxcontroller.util.ImageUtils;
import com.sation.knxcontroller.util.Log;
import com.sation.knxcontroller.util.MapUtils;
import com.sation.knxcontroller.util.StringUtil;

import java.lang.ref.WeakReference;

/**
 * Created by wangchunfeng on 2017/5/19.
 */

public class STKNXImageButton extends STKNXControl {
    private final static String TAG = "STKNXControl";

    private enum ControlState {
        Down,
        Normal,
    }
    private enum SwitchState {
        On,
        Off,
    }

    private ControlState mControlState;

    private KNXImageButton mKNXImageButton;
    private ImageView mImageView;
    private SwitchState mSwitchState;
    private Bitmap imageOn;
    private Bitmap imageOff;

    public STKNXImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public STKNXImageButton(final Context context, KNXImageButton knxImageButton) {
        super(context, knxImageButton);

        this.mKNXImageButton = knxImageButton;
        this.setId(this.mKNXImageButton.getId());

        this.mControlState = ControlState.Normal;
        mSwitchState = SwitchState.Off;

        mImageView = new ImageView(context);
        this.addView(mImageView);

        final Animation zoomIn = AnimationUtils.loadAnimation(context, R.anim.scale_zoom_in_100);
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (KNXView.EBool.Yes == mKNXImageButton.getClickable()) { // 是否可点击
                }
            }
        });

        this.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                if(!StringUtil.isNullOrEmpty(mKNXImageButton.getImageOn())) {
                    imageOn = ImageUtils.getDiskBitmap(STKNXControllerConstant.ConfigResImgPath +
                                    mKNXImageButton.getImageOn());
                }

                if(!StringUtil.isNullOrEmpty(mKNXImageButton.getImageOff())) {
                    imageOff = ImageUtils.getDiskBitmap(STKNXControllerConstant.ConfigResImgPath +
                                    mKNXImageButton.getImageOff());
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
        copyStatusAndRequest();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private static class STKNXImageButtonHandler extends Handler {
        WeakReference<STKNXImageButton> mImageButton;

        private STKNXImageButtonHandler(STKNXImageButton ib) {
            super(ib.getContext().getMainLooper());

            mImageButton = new WeakReference<STKNXImageButton>(ib);
        }

        @Override
        public void handleMessage(Message msg) {
            try {
                setControlImage(mImageButton.get()); // 更改控件图片
            } catch (Exception ex) {
                Log.e(TAG, ex.getLocalizedMessage());
            }
        }
    }

    private static void setControlImage(STKNXImageButton mImageButton) {
        if(null != mImageButton.mImageView) {
            if (SwitchState.On == mImageButton.mSwitchState) {
                if (null != mImageButton.imageOn) {
                    mImageButton.mImageView.setImageBitmap(mImageButton.imageOn);
                }
            } else if (SwitchState.Off == mImageButton.mSwitchState) {
                if (null != mImageButton.imageOff) {
                    mImageButton.mImageView.setImageBitmap(mImageButton.imageOff);
                }
            }
        }
    }

    private void updateControlState() {
        STKNXImageButtonHandler mHandler = new STKNXImageButtonHandler(STKNXImageButton.this);
        mHandler.sendEmptyMessage(0);
    }

    public void setValue(byte[] array) {
        KNXGroupAddress address = this.mKNXImageButton.getReadAddress();
        if (null != address) {
            int value;
            if (address.getKnxMainNumber().equals(KNXDatapointType.DPT_1)) {
                value = DPT1.byteArray2int(array); // 解析数值
            } else {
                value = KNXDatapointType.bytes2int(array, address.getType());
            }

            if (0 == value) { // 根据值更新控件状态
                mSwitchState = SwitchState.Off;
            } else {
                mSwitchState = SwitchState.On;
            }

            updateControlState();
        }
    }

    private void clickEvent() {
        if(null == this.mKNXImageButton) {
            return;
        }

        int val;
        if(SwitchState.Off == mSwitchState) {
            mSwitchState = SwitchState.On;
            val = 1;
        } else {
            mSwitchState = SwitchState.Off;
            val = 0;
        }

        setControlImage(STKNXImageButton.this);
        sendCommandRequest(this.mKNXImageButton.getWriteAddressIds(), val+"", false, null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        /**
         * 最后调用父类方法,把View的大小告诉父布局。
         */
        setMeasuredDimension(this.mKNXImageButton.Width, this.mKNXImageButton.Height);
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
            if(view instanceof ImageView) {
                cl = this.mKNXImageButton.getPadding().getLeft();
                ct = this.mKNXImageButton.getPadding().getTop();
                cr = this.mKNXImageButton.Width - this.mKNXImageButton.getPadding().getRight();
                cb = this.mKNXImageButton.Height - this.mKNXImageButton.getPadding().getBottom();
            }
            view.layout(cl, ct, cr, cb);
        }
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int backColor = Color.parseColor(this.mKNXImageButton.BackgroundColor);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);	// 充满
        paint.setAlpha((int)(this.mKNXImageButton.Alpha*255));
        RectF oval3 = new RectF(0, 0, getWidth(), getHeight());// 设置个新的长方形

        if(KNXView.EFlatStyle.Stereo == this.mKNXImageButton.getFlatStyle()) {	// 画立体感的圆角矩形

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
            paint.setARGB((int)(this.mKNXImageButton.Alpha*255), Color.red(backColor), Color.green(backColor), Color.blue(backColor));
        }
        canvas.drawRoundRect(oval3, this.mKNXImageButton.Radius, this.mKNXImageButton.Radius, paint);//第二个参数是x半径，第三个参数是y半径

        paint.reset();

        switch (mControlState) {
            case Down:
                paint.reset();
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(Color.parseColor("#FF6100"));
                paint.setAlpha(0x60);
                canvas.drawRoundRect(oval3, this.mKNXImageButton.Radius, this.mKNXImageButton.Radius, paint);	//第二个参数是x半径，第三个参数是y半径
                break;
            case Normal:
                break;
        }

        if(KNXView.EBool.Yes == this.mKNXImageButton.getDisplayBorder()) {
            paint.reset();
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.parseColor(this.mKNXImageButton.BorderColor));
            canvas.drawRoundRect(oval3, this.mKNXImageButton.Radius, this.mKNXImageButton.Radius, paint);//第二个参数是x半径，第三个参数是y半径
        }
    }

    @Override
    public void copyStatusAndRequest() {
        super.copyStatusAndRequest();

        byte[] bytes = getControlStatus(this.mKNXImageButton.getReadAddressId(), true);
        if (null != bytes) {
            this.setValue(bytes);
        }
    }

    @Override
    public void statusUpdate(int asp, KNXGroupAddress address) {
        super.statusUpdate(asp, address);

        KNXSelectedAddress readAddr = MapUtils.getFirstOrNull(this.mKNXImageButton.getReadAddressId());
        if (null != readAddr) {
            if (address.getId().equals(readAddr.getId())) {
                byte[] bytes = copyObjectStatus(asp);
                this.setValue(bytes);
            }
        }
    }
}
