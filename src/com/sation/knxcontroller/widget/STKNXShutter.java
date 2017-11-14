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
import com.sation.knxcontroller.STKNXControllerApp;
import com.sation.knxcontroller.STKNXControllerConstant;
import com.sation.knxcontroller.control.KNXShutter;
import com.sation.knxcontroller.customview.SectionalSeekBar;
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

import org.w3c.dom.Text;

import java.lang.ref.WeakReference;
import java.util.Map;

import static com.sation.knxcontroller.util.KNX0X01Lib.UGetAndClearRequestState;
import static com.sation.knxcontroller.util.KNX0X01Lib.USetAndRequestObject;
import static com.sation.knxcontroller.util.KNX0X01Lib.USetAndTransmitObject;

/**
 * Created by wangchunfeng on 2017/8/15.
 */

public class STKNXShutter extends STKNXControl {
    private static final String TAG = "STKNXShutter";
    private static final int UPDATE_CONTROL_STATE = 1;
    private static final int UPDATE_POSITION_OF_SHUTTER = 2;
    private static final int UPDATE_POSITION_OF_BLINDS = 3;

    private KNXShutter mKNXShutter;

    private enum ControlState {
        Down,
        Normal,
    }
    private enum ShutterState {
        On,
        Off,
    }

    private ControlState mControlState;
    private ShutterState mShutterState;
    private String image;
    private ImageView mImageView;
    private Bitmap imageOn;
    private Bitmap imageOff;
    private PopupWindow popupWindow;
    private TextView tvShutterName;
    private ImageButton ibShutterStop;
    private RelativeLayout rlPositionOfShutter;
    private SeekBar sbAbsolutePositionOfShutter;
    private TextView tvAbsolutePositionOfShutter;
    private RelativeLayout rlPositionOfBlinds;
    private SectionalSeekBar sbAbsolutePositionOfBlinds;
    private TextView tvAbsolutePositionOfBlinds;

    private int poS = 0;
    private int poB = 0;

    public STKNXShutter(Context context, KNXShutter knxShutter) {
            super(context, knxShutter);

        this.mKNXShutter = knxShutter;
        this.setId(this.mKNXShutter.getId());

        this.mControlState = ControlState.Normal;
        this.mShutterState = ShutterState.Off;

        this.mImageView = new ImageView(context);
        this.addView(this.mImageView);

        this.image = STKNXControllerConstant.ConfigResImgPath + this.mKNXShutter.getSymbol();

        Bitmap bm = ImageUtils.getDiskBitmap(this.image);
        if (null != bm) {
            mImageView.setImageBitmap(bm);
        }

        ShutterDetail();

        final Animation zoomIn = AnimationUtils.loadAnimation(context, R.anim.scale_zoom_in_100);
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (KNXView.EBool.Yes == mKNXShutter.getClickable()) { // 控件是否可点击
                    STKNXShutter.this.startAnimation(zoomIn); // 启动缩放动画
                    zoomIn.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            try {
                                if (!showShutterDetail()) {
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
                if(!StringUtil.isNullOrEmpty(mKNXShutter.getImageOn())) {
                    imageOn = ImageUtils.getDiskBitmap(STKNXControllerConstant.ConfigResImgPath +
                            mKNXShutter.getImageOn());
                }

                if(!StringUtil.isNullOrEmpty(mKNXShutter.getImageOff())) {
                    imageOff = ImageUtils.getDiskBitmap(STKNXControllerConstant.ConfigResImgPath +
                            mKNXShutter.getImageOff());
                }

                updateShutterState();
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
        setMeasuredDimension(this.mKNXShutter.Width, this.mKNXShutter.Height);
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
                cl = this.mKNXShutter.getPadding().getLeft();
                ct = this.mKNXShutter.getPadding().getTop();
                cr = this.mKNXShutter.Width - this.mKNXShutter.getPadding().getRight();
                cb = this.mKNXShutter.Height - this.mKNXShutter.getPadding().getBottom();
            }
            view.layout(cl, ct, cr, cb);
        }
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int backColor = Color.parseColor(this.mKNXShutter.BackgroundColor);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);	// 充满
        paint.setAlpha((int)(this.mKNXShutter.Alpha*255));
        RectF oval3 = new RectF(0, 0, getWidth(), getHeight());// 设置个新的长方形

        if(KNXView.EFlatStyle.Stereo == this.mKNXShutter.getFlatStyle()) {	// 画立体感的圆角矩形

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
            paint.setARGB((int)(this.mKNXShutter.Alpha*255), Color.red(backColor), Color.green(backColor), Color.blue(backColor));
        }
        canvas.drawRoundRect(oval3, this.mKNXShutter.Radius, this.mKNXShutter.Radius, paint);//第二个参数是x半径，第三个参数是y半径

        paint.reset();

        switch (mControlState) {
            case Down:
                paint.reset();
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(Color.parseColor("#FF6100"));
                paint.setAlpha(0x60);
                canvas.drawRoundRect(oval3, this.mKNXShutter.Radius, this.mKNXShutter.Radius, paint);	//第二个参数是x半径，第三个参数是y半径
                break;
            case Normal:
                break;
        }

        if(KNXView.EBool.Yes == this.mKNXShutter.getDisplayBorder()) {
            paint.reset();
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.parseColor(this.mKNXShutter.BorderColor));
            canvas.drawRoundRect(oval3, this.mKNXShutter.Radius, this.mKNXShutter.Radius, paint);//第二个参数是x半径，第三个参数是y半径
        }
    }

    @Override
    public void copyStatusAndRequest() {
        super.copyStatusAndRequest();

        if(this.mKNXShutter.getStateLowerPosition().getEnable()) { // 窗帘到达底部 状态 功能 开启？
            Map<String, KNXSelectedAddress> map =
                    this.mKNXShutter.getStateLowerPosition().getSelectedAddress();
            byte[] bytes = getControlStatus(map, true);
            if (null != bytes) {
                this.parseStateLowerPosition(bytes);

                resetControlStatus(map, 0);
            }
        }

        if(this.mKNXShutter.getStateUpperPosition().getEnable()) { // 窗帘到达顶部 状态 功能 开启？
            Map<String, KNXSelectedAddress> map =
                    this.mKNXShutter.getStateUpperPosition().getSelectedAddress();
            byte[] bytes = getControlStatus(map, true);
            if (null != bytes) {
                this.parseStateUpperPosition(bytes);

                resetControlStatus(map, 0);
            }
        }

        if(this.mKNXShutter.getStatusActualPositionOfShutter().getEnable()) { // 窗帘绝对位置 状态 功能 开启？
            byte[] bytes = getControlStatus(
                    this.mKNXShutter.getStatusActualPositionOfShutter().getSelectedAddress(), true);
            if (null != bytes) {
                this.parseAbsolutePositionOfShutter(bytes);
            }
        }

        if (this.mKNXShutter.getStatusActualPositionOfBlinds().getEnable()) { // 板条旋转角度 状态 功能 开启？
            byte[] bytes = getControlStatus(
                    this.mKNXShutter.getStatusActualPositionOfBlinds().getSelectedAddress(), true);
            if (null != bytes) {
                this.parseAbsolutePositionOfBlinds(bytes);
            }
        }
    }

    @Override
    public void statusUpdate(int asp, KNXGroupAddress address) {
        super.statusUpdate(asp, address);

        if (this.mKNXShutter.getStateLowerPosition().getEnable()) { // 窗帘到达底部 状态 功能 开启？
            KNXGroupAddress addr = this.mKNXShutter.getStateLowerPosition().getGroupAddress();
            if ((null != addr) && (addr.getId().equals(address.getId()))) {
                byte[] bytes = copyObjectStatus(asp);

                this.parseStateLowerPosition(bytes);

                resetObjectStatus(asp, 0);
            }
        }

        if (this.mKNXShutter.getStateUpperPosition().getEnable()) { // 窗帘到达顶部 状态 功能 开启？
            KNXGroupAddress addr = this.mKNXShutter.getStateUpperPosition().getGroupAddress();
            if ((null != addr) && (addr.getId().equals(address.getId()))) {
                byte[] bytes = copyObjectStatus(asp);

                this.parseStateUpperPosition(bytes);

                resetObjectStatus(asp, 0);
            }
        }

        if (this.mKNXShutter.getStatusActualPositionOfShutter().getEnable()) { // 窗帘绝对位置 状态 功能 开启？
            KNXGroupAddress addr = this.mKNXShutter.getStatusActualPositionOfShutter().getGroupAddress();
            if ((null != addr) && (addr.getId().equals(address.getId()))) {
                byte[] bytes = copyObjectStatus(asp);

                this.parseAbsolutePositionOfShutter(bytes);
            }
        }

        if (this.mKNXShutter.getStatusActualPositionOfBlinds().getEnable()) { // 板条旋转角度 状态 功能 开启？
            KNXGroupAddress addr = this.mKNXShutter.getStatusActualPositionOfBlinds().getGroupAddress();
            if ((null != addr) && (addr.getId().equals(address.getId()))) {
                byte[] bytes = copyObjectStatus(asp);

                this.parseAbsolutePositionOfBlinds(bytes);
            }
        }
    }

    private void shutterClickEvent() {
        int val;
        if(ShutterState.Off == mShutterState) {
            mShutterState = ShutterState.On;
            val = 0; // 开启
        } else {
            mShutterState = ShutterState.Off;
            val = 1; // 关闭
        }

        setShutterImage(STKNXShutter.this); // 更新状态
        sendCommandRequest(this.mKNXShutter.getShutterUpDown().getSelectedAddress(), val+"", false, null);
    }

    /**
     * 解析窗帘到达底部状态
     * @param array
     */
    public void parseStateLowerPosition(byte[] array) {
        KNXGroupAddress address = this.mKNXShutter.getStateLowerPosition().getGroupAddress();
        if (null != address) {
            int value;
            if (address.getKnxMainNumber().equals(KNXDatapointType.DPT_1)) {
                value = DPT1.byteArray2int(array);
            } else {
                value = KNXDatapointType.bytes2int(array, address.getType());
            }

            if (1 == value) {
                mShutterState = ShutterState.Off;
            }

            updateShutterState();
        }
    }

    /**
     * 解析窗帘到达顶部的状态
     * @param array
     */
    public void parseStateUpperPosition(byte[] array) {
        KNXGroupAddress address = this.mKNXShutter.getStateUpperPosition().getGroupAddress();
        if (null != address) {
            int value;
            if (address.getKnxMainNumber().equals(KNXDatapointType.DPT_1)) {
                value = DPT1.byteArray2int(array);
            } else {
                value = KNXDatapointType.bytes2int(array, address.getType());
            }

            if (1 == value) {
                mShutterState = ShutterState.On;
            }

            updateShutterState();
        }
    }

    /**
     * 解析窗帘绝对位置
     * @param array
     */
    public void parseAbsolutePositionOfShutter(byte[] array) {
        KNXGroupAddress address =
                this.mKNXShutter.getStatusActualPositionOfShutter().getGroupAddress();
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

            this.poS = value;
            updatePositionOfShutter();
        }
    }

    /**
     * 解析窗帘板条角度
     * @param array
     */
    public void  parseAbsolutePositionOfBlinds(byte[] array) {
        KNXGroupAddress address = this.mKNXShutter.getStatusActualPositionOfBlinds().getGroupAddress();
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

            this.poB = value;
            updatePositionOfBlinds();
        }
    }

    private void updateShutterState() {
        STKNXShutterHandler mHandler = new STKNXShutterHandler(STKNXShutter.this);
        mHandler.sendEmptyMessage(UPDATE_CONTROL_STATE);
    }

    private void updatePositionOfShutter() {
        STKNXShutterHandler mHandler = new STKNXShutterHandler(STKNXShutter.this);
        mHandler.sendEmptyMessage(UPDATE_POSITION_OF_SHUTTER);
    }

    private void updatePositionOfBlinds() {
        STKNXShutterHandler mHandler = new STKNXShutterHandler(STKNXShutter.this);
        mHandler.sendEmptyMessage(UPDATE_POSITION_OF_BLINDS);
    }

    private static class STKNXShutterHandler extends Handler {
        WeakReference<STKNXShutter> mShutter;

        private STKNXShutterHandler(STKNXShutter shutter) {
            super(shutter.getContext().getMainLooper());

            mShutter = new WeakReference<STKNXShutter>(shutter);
        }

        @Override
        public void handleMessage(Message msg) {
            try {
                switch (msg.what) {
                    case UPDATE_CONTROL_STATE:
                        setShutterImage(mShutter.get());
                        break;
                    case UPDATE_POSITION_OF_SHUTTER:
                        setPositionOfShutter(mShutter.get());
                        break;
                    case UPDATE_POSITION_OF_BLINDS:
                        setPositionOfBlinds(mShutter.get());

                    default:
                        break;
                }

            } catch (Exception ex) {
                Log.e(TAG, ex.getLocalizedMessage());
            }
        }
    }

    private static void setShutterImage(STKNXShutter mShutter) {
        if(null != mShutter.mImageView) {
            if (ShutterState.On == mShutter.mShutterState) { // 窗帘已开启
                if (null != mShutter.imageOn) {
                    mShutter.mImageView.setImageBitmap(mShutter.imageOn);
                }
            } else if (ShutterState.Off == mShutter.mShutterState) { // 窗帘已关闭
                if (null != mShutter.imageOff) {
                    mShutter.mImageView.setImageBitmap(mShutter.imageOff);
                }
            }
        }
    }

    /**
     * 更新窗帘位置
     * @param mShutter
     */
    private static void setPositionOfShutter(STKNXShutter mShutter) {
        if (mShutter.popupWindow.isShowing()) {
            mShutter.sbAbsolutePositionOfShutter.setProgress(mShutter.poS);
            mShutter.tvAbsolutePositionOfShutter.setText(mShutter.poS + "%");
        }
    }

    /**
     * 更新板条位置
     * @param mShutter
     */
    private static void setPositionOfBlinds(STKNXShutter mShutter) {
        if (mShutter.popupWindow.isShowing()) {
            mShutter.sbAbsolutePositionOfBlinds.setProgress(mShutter.poB);
            mShutter.tvAbsolutePositionOfBlinds.setText(mShutter.poB + "%");
        }
    }

    private void ShutterDetail() {
        final View view = LayoutInflater.from(getContext()).inflate(R.layout.stknx_shutter_detail, null);
        RelativeLayout outside = (RelativeLayout) view.findViewById(R.id.stknx_shutter_detail_relativelayout);
        RelativeLayout shutterDetail = (RelativeLayout) view.findViewById(R.id.shutter_detail_relativelayout);
        this.tvShutterName = (TextView) view.findViewById(R.id.shutter_detail_textview_title);
        final ImageButton down = (ImageButton) view.findViewById(R.id.shutter_detail_imagebutton_down);
        this.ibShutterStop = (ImageButton) view.findViewById(R.id.shutter_detail_imagebutton_stop);
        final ImageButton up = (ImageButton) view.findViewById(R.id.shutter_detail_imagebutton_up);
        this.rlPositionOfShutter = (RelativeLayout) view.findViewById(R.id.shutter_detail_relativelayout_position_of_shutter);
        this.sbAbsolutePositionOfShutter = (SeekBar) view.findViewById(R.id.shutter_detail_seekbar_position_of_shutter);
        this.tvAbsolutePositionOfShutter = (TextView) view.findViewById(R.id.shutter_detail_textview_postition_of_shutter);
        this.rlPositionOfBlinds = (RelativeLayout) view.findViewById(R.id.shutter_detail_relativelayout_position_of_blinds);
        this.sbAbsolutePositionOfBlinds = (SectionalSeekBar) view.findViewById(R.id.shutter_detail_sectionalseekbar_position_of_slat);
        this.tvAbsolutePositionOfBlinds = (TextView) view.findViewById(R.id.shutter_detail_textview_postition_of_blinds);

        this.popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.popupWindow.setAnimationStyle(R.style.PopupWindowAnimation_ShutterDetail);
        this.popupWindow.update();

        final Animation zoomOut = AnimationUtils.loadAnimation(getContext(), R.anim.scale_zoom_out_100);

        down.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) { // 关闭窗帘
                try {
                    down.startAnimation(zoomOut);
                    sendCommandRequest(mKNXShutter.getShutterUpDown().getSelectedAddress(), "1", false, null);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        up.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) { // 打开窗帘
                try {
                    up.startAnimation(zoomOut);
                    sendCommandRequest(mKNXShutter.getShutterUpDown().getSelectedAddress(), "0", false, null);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        this.ibShutterStop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) { // 窗帘停止
                try {
                    ibShutterStop.startAnimation(zoomOut);
                    sendCommandRequest(mKNXShutter.getShutterStop().getSelectedAddress(), "1", false, null);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        sbAbsolutePositionOfShutter.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvAbsolutePositionOfShutter.setText(seekBar.getProgress() + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { // 调节窗帘位置
                try {
                    if (mKNXShutter.getAbsolutePositionOfShutter().getEnable()) {
                        sendCommandRequest(
                                mKNXShutter.getAbsolutePositionOfShutter().getSelectedAddress(),
                                String.valueOf((int) (seekBar.getProgress() * 255 / 100)), false, null);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        sbAbsolutePositionOfBlinds.setOnSeekBarChangeListener(new SectionalSeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SectionalSeekBar seekBar, int progress) {
                tvAbsolutePositionOfBlinds.setText(seekBar.getProgress() + "%");
            }

            @Override
            public void onStartTrackingTouch(SectionalSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SectionalSeekBar seekBar) { // 调节板条位置
                try {
                    if (mKNXShutter.getAbsolutePositionOfBlinds().getEnable()) {
                        sendCommandRequest(
                                mKNXShutter.getAbsolutePositionOfBlinds().getSelectedAddress(),
                                String.valueOf((int) (seekBar.getProgress() * 255 / 100)), false, null);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        outside.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(popupWindow.isShowing()) {
                    popupWindow.dismiss(); // 隐藏弹窗
                }
            }
        });
    }

    /**
     * 显示弹窗
     * @return
     */
    private boolean showShutterDetail() {
        boolean show = false;
        this.tvShutterName.setText(this.mKNXShutter.getTitle());
        if(this.mKNXShutter.getShutterStop().getEnable()) { // 停止功能是否开启？
            show = true;
        } else {
            this.ibShutterStop.setVisibility(View.GONE);
        }

        if(this.mKNXShutter.getAbsolutePositionOfShutter().getEnable() ||
                this.mKNXShutter.getStatusActualPositionOfShutter().getEnable()) { // 窗帘位置？
            show = true;
            this.sbAbsolutePositionOfShutter.setProgress(this.poS);
            this.tvAbsolutePositionOfShutter.setText(this.poS + "%");
        } else {
            this.rlPositionOfShutter.setVisibility(View.GONE);
        }

        if (this.mKNXShutter.getAbsolutePositionOfBlinds().getEnable() ||
                this.mKNXShutter.getStatusActualPositionOfBlinds().getEnable()) { // 板条位置？
            show = true;
            this.sbAbsolutePositionOfBlinds.setProgress(this.poB);
            this.tvAbsolutePositionOfBlinds.setText(this.poB + "%");
        } else {
            this.rlPositionOfBlinds.setVisibility(View.GONE);
        }

        if (show) {
            this.popupWindow.showAtLocation(this, Gravity.CENTER, 0, 0);
        }

        return show;
    }
}
