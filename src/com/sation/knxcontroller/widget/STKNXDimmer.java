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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.sation.knxcontroller.R;
import com.sation.knxcontroller.STKNXControllerConstant;
import com.sation.knxcontroller.control.KNXDimmer;
import com.sation.knxcontroller.customview.DimmingControl;
import com.sation.knxcontroller.knxdpt.DPT1;
import com.sation.knxcontroller.knxdpt.DPT5;
import com.sation.knxcontroller.knxdpt.KNXDatapointType;
import com.sation.knxcontroller.models.KNXGroupAddress;
import com.sation.knxcontroller.models.KNXSelectedAddress;
import com.sation.knxcontroller.models.KNXView;
import com.sation.knxcontroller.util.ColorUtils;
import com.sation.knxcontroller.util.ImageUtils;
import com.sation.knxcontroller.util.Log;
import com.sation.knxcontroller.util.StringUtil;

import java.lang.ref.WeakReference;
import java.util.Map;

/**
 * Created by wangchunfeng on 2017/8/15.
 */

public class STKNXDimmer extends STKNXControl {
    private static final String TAG = "STKNXDimmer";
    private static final int UPDATE_CONTROL_STATE = 1;
    private static final int UPDATE_DIM_VALUE = 2;

    private KNXDimmer mKNXDimmer;

    private enum ControlState {
        Down,
        Normal,
    }
    private enum DimmerState {
        On,
        Off,
    }

    private ControlState mControlState;
    private DimmerState mDimmerState;
    private String image;
    private ImageView mImageView;
    private Bitmap imageOn;
    private Bitmap imageOff;
    private PopupWindow popupWindow;
    private TextView tvDimmerName;
    private RelativeLayout rlSwitchOp;
    private RelativeLayout rlDimAbsolutely;
    private WArcSeekBar sbDimAbsolutely;
    private RelativeLayout rlDimRelatively;
    private DimmingControl sbDimRelatively;


    private int poD = 0;

    public STKNXDimmer(Context context, KNXDimmer knxDimmer) {
            super(context, knxDimmer);

        this.mKNXDimmer = knxDimmer;
        this.setId(this.mKNXDimmer.getId());

        this.mControlState = ControlState.Normal;
        this.mDimmerState = DimmerState.Off;

        this.mImageView = new ImageView(context);
        this.addView(this.mImageView);

        this.image = STKNXControllerConstant.ConfigResImgPath + this.mKNXDimmer.getSymbol();
        Bitmap bm = ImageUtils.getDiskBitmap(this.image);
        if (null != bm) {
            mImageView.setImageBitmap(bm);
        }

        DimmerDetail();

        final Animation zoomIn = AnimationUtils.loadAnimation(context, R.anim.scale_zoom_in_100);
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (KNXView.EBool.Yes == mKNXDimmer.getClickable()) { // 控件是否可点击
                    STKNXDimmer.this.startAnimation(zoomIn);
                    zoomIn.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            try {
                                if (!showDimmerDetail()) {
                                    shutterClickEvent();
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
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
                if(!StringUtil.isNullOrEmpty(mKNXDimmer.getImageOn())) {
                    imageOn = ImageUtils.getDiskBitmap(STKNXControllerConstant.ConfigResImgPath +
                            mKNXDimmer.getImageOn());
                }

                if(!StringUtil.isNullOrEmpty(mKNXDimmer.getImageOff())) {
                    imageOff = ImageUtils.getDiskBitmap(STKNXControllerConstant.ConfigResImgPath +
                            mKNXDimmer.getImageOff());
                }

                updateDimmerState();
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

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        /**
         * 最后调用父类方法,把View的大小告诉父布局。
         */
        setMeasuredDimension(this.mKNXDimmer.Width, this.mKNXDimmer.Height);
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
                cl = this.mKNXDimmer.getPadding().getLeft();
                ct = this.mKNXDimmer.getPadding().getTop();
                cr = this.mKNXDimmer.Width - this.mKNXDimmer.getPadding().getRight();
                cb = this.mKNXDimmer.Height - this.mKNXDimmer.getPadding().getBottom();
            }
            view.layout(cl, ct, cr, cb);
        }
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int backColor = Color.parseColor(this.mKNXDimmer.BackgroundColor);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);	// 充满
        paint.setAlpha((int)(this.mKNXDimmer.Alpha*255));
        RectF oval3 = new RectF(0, 0, getWidth(), getHeight());// 设置个新的长方形

        if(KNXView.EFlatStyle.Stereo == this.mKNXDimmer.getFlatStyle()) {	// 画立体感的圆角矩形

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
            paint.setARGB((int)(this.mKNXDimmer.Alpha*255), Color.red(backColor), Color.green(backColor), Color.blue(backColor));
        }
        canvas.drawRoundRect(oval3, this.mKNXDimmer.Radius, this.mKNXDimmer.Radius, paint);//第二个参数是x半径，第三个参数是y半径

        paint.reset();

        switch (mControlState) {
            case Down:
                paint.reset();
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(Color.parseColor("#FF6100"));
                paint.setAlpha(0x60);
                canvas.drawRoundRect(oval3, this.mKNXDimmer.Radius, this.mKNXDimmer.Radius, paint);	//第二个参数是x半径，第三个参数是y半径
                break;
            case Normal:
                break;
        }

        if(KNXView.EBool.Yes == this.mKNXDimmer.getDisplayBorder()) {
            paint.reset();
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.parseColor(this.mKNXDimmer.BorderColor));
            canvas.drawRoundRect(oval3, this.mKNXDimmer.Radius, this.mKNXDimmer.Radius, paint);//第二个参数是x半径，第三个参数是y半径
        }
    }

    @Override
    public void copyStatusAndRequest() {
        super.copyStatusAndRequest();

        if (this.mKNXDimmer.getStateOnOff().getEnable()) { // 开关状态反馈已开启？
            Map<String, KNXSelectedAddress> map =
                    this.mKNXDimmer.getStateOnOff().getSelectedAddress();
            byte[] bytes = getControlStatus(map, true);
            if (null != bytes) {
                this.parseStateOnOff(bytes);
            }
        }

        if(this.mKNXDimmer.getStateDimValue().getEnable()) {
            Map<String, KNXSelectedAddress> map =
                    this.mKNXDimmer.getStateDimValue().getSelectedAddress();
            byte[] bytes = getControlStatus(map, true);
            if (null != bytes) {
                this.parseStateDimValue(bytes);
            }
        }
    }

    @Override
    public void statusUpdate(int asp, KNXGroupAddress address) {
        super.statusUpdate(asp, address);

        if (this.mKNXDimmer.getStateOnOff().getEnable()) { // 开关状态反馈功能开启？
            KNXGroupAddress addr = this.mKNXDimmer.getStateOnOff().getGroupAddress();
            if ((null != addr) && (addr.getId().equals(address.getId()))) {
                byte[] bytes = copyObjectStatus(asp);

                this.parseStateOnOff(bytes);
            }
        }

        if (this.mKNXDimmer.getStateDimValue().getEnable()) {
            KNXGroupAddress addr = this.mKNXDimmer.getStateDimValue().getGroupAddress();
            if ((null != addr) && (addr.getId().equals(address.getId()))) {
                byte[] bytes = copyObjectStatus(asp);

                this.parseStateDimValue(bytes);
            }
        }
    }

    /**
     * 窗帘控制器点击事件
     */
    private void shutterClickEvent() {
        int val;
        if(DimmerState.Off == mDimmerState) {
            mDimmerState = DimmerState.On;
            val = 1;
        } else {
            mDimmerState = DimmerState.Off;
            val = 0;
        }

        setShutterImage(STKNXDimmer.this);
        sendCommandRequest(this.mKNXDimmer.getSwitch().getSelectedAddress(), val+"", false, null);
    }

    public void parseStateOnOff(byte[] array) { // 解析开关状态数据
        KNXGroupAddress address = this.mKNXDimmer.getStateOnOff().getGroupAddress();
        if (null != address) {
            int value;
            if (address.getKnxMainNumber().equals(KNXDatapointType.DPT_1)) {
                value = DPT1.byteArray2int(array);
            } else {
                value = KNXDatapointType.bytes2int(array, address.getType());
            }

            if (0 == value) {
                mDimmerState = DimmerState.Off;
            } else {
                mDimmerState = DimmerState.On;
            }

            updateDimmerState();
        }
    }

    public void parseStateDimValue(byte[] array) { // 解析调光值数据
        KNXGroupAddress address =
                this.mKNXDimmer.getStateDimValue().getGroupAddress();
        if (null != address) {
            int value = 0;
            if (address.getKnxMainNumber().equals(KNXDatapointType.DPT_5)) {
                if (address.getKnxSubNumber().equals(KNXDatapointType.DPST_1)) {
                    value = DPT5.byte2Scaling(array);
                } else if (address.getKnxSubNumber().equals(KNXDatapointType.DPST_3)) {
                    value = DPT5.byte2Angle(array);
                } else if (address.getKnxSubNumber().equals(KNXDatapointType.DPST_4)) {
                    value = DPT5.byte2percentu8((array));
                } else if (address.getKnxSubNumber().equals(KNXDatapointType.DPST_5)) {
                    value = DPT5.byte2DecimalFactor(array);
                } else if (address.getKnxSubNumber().equals(KNXDatapointType.DPST_10)) {
                    value = DPT5.byte2Value1Ucount(array);
                } else {
                    value = KNXDatapointType.bytes2int(array, address.getType());
                }
            }

            this.poD = value;
            updateDimmerValue();
        }
    }

    private void updateDimmerState() {
        STKNXDimmerHandler mHandler = new STKNXDimmerHandler(STKNXDimmer.this);
        mHandler.sendEmptyMessage(UPDATE_CONTROL_STATE);
    }

    private void updateDimmerValue() {
        STKNXDimmerHandler mHandler = new STKNXDimmerHandler(STKNXDimmer.this);
        mHandler.sendEmptyMessage(UPDATE_DIM_VALUE);
    }

    private static class STKNXDimmerHandler extends Handler {
        WeakReference<STKNXDimmer> mDimmer;

        private STKNXDimmerHandler(STKNXDimmer dimmer) {
            super(dimmer.getContext().getMainLooper());

            mDimmer = new WeakReference<STKNXDimmer>(dimmer);
        }

        @Override
        public void handleMessage(Message msg) {
            try {
                switch (msg.what) {
                    case UPDATE_CONTROL_STATE:
                        setShutterImage(mDimmer.get());
                        break;
                    case UPDATE_DIM_VALUE:
                        setPositionOfShutter(mDimmer.get());
                        break;

                    default:
                        break;
                }

            } catch (Exception ex) {
                Log.e(TAG, ex.getLocalizedMessage());
            }
        }
    }

    private static void setShutterImage(STKNXDimmer mDimmer) {
        if(null != mDimmer.mImageView) {
            if (DimmerState.On == mDimmer.mDimmerState) {
                if (null != mDimmer.imageOn) {
                    mDimmer.mImageView.setImageBitmap(mDimmer.imageOn);
                }
            } else if (DimmerState.Off == mDimmer.mDimmerState) {
                if (null != mDimmer.imageOff) {
                    mDimmer.mImageView.setImageBitmap(mDimmer.imageOff);
                }
            }
        }
    }

    private static void setPositionOfShutter(STKNXDimmer mDimmer) {
        if (mDimmer.popupWindow.isShowing()) {
            mDimmer.sbDimAbsolutely.setProgress(mDimmer.poD);
        }
    }

    private void DimmerDetail() {
        final View view = LayoutInflater.from(getContext()).inflate(R.layout.stknx_dimmer_detail, null);
        RelativeLayout outside = (RelativeLayout) view.findViewById(R.id.stknx_dimmer_detail_relativelayout);
        RelativeLayout shutterDetail = (RelativeLayout) view.findViewById(R.id.dimmer_detail_relativelayout);
        this.tvDimmerName = (TextView) view.findViewById(R.id.dimmer_detail_textview_title);
        this.rlSwitchOp = (RelativeLayout) view.findViewById(R.id.dimmer_detail_relativelayout_switch_op);
        final ImageButton on = (ImageButton) view.findViewById(R.id.dimmer_detail_imagebutton_on);
        final ImageButton off = (ImageButton) view.findViewById(R.id.dimmer_detail_imagebutton_off);
        this.rlDimAbsolutely = (RelativeLayout) view.findViewById(R.id.dimmer_detail_relativelayout_dim_absolutely);
        this.sbDimAbsolutely = (WArcSeekBar) view.findViewById(R.id.dimmer_detail_WArc_seekbar_dim_absolutely);
        this.rlDimRelatively = (RelativeLayout) view.findViewById(R.id.dimmer_detail_relativelayout_dim_relatively);
        this.sbDimRelatively = (DimmingControl) view.findViewById(R.id.dimmer_detail_dimmingcontrol_dim_relatively);

        this.popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.popupWindow.setAnimationStyle(R.style.PopupWindowAnimation_ShutterDetail);
        this.popupWindow.update();

        final Animation zoomOut = AnimationUtils.loadAnimation(getContext(), R.anim.scale_zoom_out_100);

        on.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) { // 开灯按钮
                try {
                    on.startAnimation(zoomOut);
                    sendCommandRequest(mKNXDimmer.getSwitch().getSelectedAddress(), "1", false, null);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        off.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) { // 关灯按钮
                try {
                    off.startAnimation(zoomOut);
                    sendCommandRequest(mKNXDimmer.getSwitch().getSelectedAddress(), "0", false, null);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        sbDimAbsolutely.setOnSeekBarChangeListener(new WArcSeekBar.OnSeekBarChangeListener() { // 调节灯光亮度 - 绝对
            @Override
            public void onProgressChanged(WArcSeekBar seekBar, int progress) {

            }

            @Override
            public void onStartTrackingTouch(WArcSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(WArcSeekBar seekBar) {
                try {
                    if (mKNXDimmer.getDimAbsolutely().getEnable()) {
                        sendCommandRequest(
                                mKNXDimmer.getDimAbsolutely().getSelectedAddress(),
                                String.valueOf((int) (seekBar.getProgress() * 255 / 100)), false, null);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        sbDimRelatively.setOnSeekBarChangeListener(new DimmingControl.OnSeekBarChangeListener() { // 调节灯光亮度 - 相对
            @Override
            public void onProgressChanged(DimmingControl seekBar, int index) {

            }

            @Override
            public void onStartTrackingTouch(DimmingControl seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DimmingControl seekBar) {
                try {
                    if (mKNXDimmer.getDimRelatively().getEnable()) {
                        sendCommandRequest(
                                mKNXDimmer.getDimRelatively().getSelectedAddress(),
                                String.valueOf(seekBar.getDimmingValue()), false, null);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        outside.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) { // 其他区域点击，隐藏弹窗
                if(popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
            }
        });
    }

    private boolean showDimmerDetail() { // 显示弹窗？
        boolean show = false;
        this.tvDimmerName.setText(this.mKNXDimmer.getTitle());

        if (this.mKNXDimmer.getSwitch().getEnable()) { // 灯开关功能？

        } else {
            this.rlSwitchOp.setVisibility(View.GONE);
        }

        if(this.mKNXDimmer.getDimAbsolutely().getEnable() ||
                this.mKNXDimmer.getStateDimValue().getEnable()) { // 绝对调光？
            show = true;
            this.sbDimAbsolutely.setProgress(this.poD);
        } else {
            this.rlDimAbsolutely.setVisibility(View.GONE);
        }

        if (this.mKNXDimmer.getDimRelatively().getEnable()) { // 相对调光？
            show = true;
        } else {
            this.rlDimRelatively.setVisibility(View.GONE);
        }

        if (show) {
            this.popupWindow.showAtLocation(this, Gravity.CENTER, 0, 0);
        }

        return show;
    }
}
