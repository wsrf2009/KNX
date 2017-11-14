package com.sation.knxcontroller.widget;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sation.knxcontroller.R;
import com.sation.knxcontroller.STKNXControllerConstant;
import com.sation.knxcontroller.control.KNXWebCamer;
import com.sation.knxcontroller.models.KNXView;
import com.sation.knxcontroller.third.webcamer.finder.CamerDevice;
import com.sation.knxcontroller.third.webcamer.finder.CamerService;
import com.sation.knxcontroller.third.webcamer.finder.OnCamerDeviceistener;
import com.sation.knxcontroller.util.ColorUtils;
import com.sation.knxcontroller.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;

/*
 * Created by wangchunfeng on 2017/9/27.
 */

public class STKNXWebCamer extends STKNXControl {
    private static final String TAG = "STKNXWebCamer";
    private static final int DISPLAY_BITMAP_ON_SURFACE = 1;
    private static final int ERROR_CONNECT_TO_WEBCAMER = 2;
    private static final int UPDATE_CAMER_LIST = 3;
    private static final int STOP_ANIMATION_REFRESH_CAMER_LIST = 4;
    private static final int CAMER_ITEM_CONNECTION_STATUS_UPDATE = 5;

    private Context mContext;
    private KNXWebCamer mKNXWebCamer;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private STKNXWebCamerHandler mSTKNXWebCamerHandler;
    private View mDetail;
    private ListView mCamers;
    private ImageView mRefreshCamerList;
    private ImageView mDisconnectCamer;
    private ServiceConnection mServiceConnection;
    private CamerService mService;
    private WebCamerAdapter mWebCamerAdapter;
    private CamerDevice mCurCamerDevice;
    private CamerServiceBroadcasrReceiver mCamerServiceBroadcasrReceiver;

    private int detPosDefault;
    private int detPosOnShow;
    private int detHeight;
    private boolean detIsOnShow;
    private boolean runGrabberThread = false;

    public STKNXWebCamer(Context context, KNXWebCamer knxWebCamer) {
        super(context, knxWebCamer);

        this.mContext = context;
        this.mKNXWebCamer = knxWebCamer;
        this.setId(this.mKNXWebCamer.getId());

        this.mSurfaceView = new SurfaceView(context);
        this.mSurfaceHolder = mSurfaceView.getHolder();
        this.addView(this.mSurfaceView);

        this.mSTKNXWebCamerHandler = new STKNXWebCamerHandler(STKNXWebCamer.this);

        this.setOnClickListener(mOnClickListener);
        this.mSurfaceView.setOnClickListener(mOnClickListener);

        this.detPosDefault = this.mKNXWebCamer.Height - 1;
        this.detPosOnShow = this.mKNXWebCamer.Height / 3;
        this.detHeight = this.mKNXWebCamer.Height * 2 / 3;
        this.detIsOnShow = false;
        /* 加入上滑窗 */
        this.mDetail = LayoutInflater.from(context).inflate(R.layout.webcamer_detail, null);
        this.mDetail.setLeft(0);
        this.mDetail.setTop(this.detPosDefault);
        this.mDetail.setRight(this.mKNXWebCamer.Width);
        this.mDetail.setBottom(this.mKNXWebCamer.Height + this.detHeight);
        LayoutParams lp = new LayoutParams(this.mKNXWebCamer.Width, this.mKNXWebCamer.Height * 2 / 3);
        this.mDetail.setLayoutParams(lp);
        this.addView(this.mDetail);

        this.mWebCamerAdapter = new WebCamerAdapter(mContext);

        this.mCamers = (ListView) this.mDetail.findViewById(R.id.webcamer_detail_listview_camers);
        this.mCamers.setOnItemClickListener(mOnItemClickListener);
        this.mCamers.setAdapter(mWebCamerAdapter);

        this.mRefreshCamerList = (ImageView) this.mDetail.findViewById(R.id.webcamer_detail_refresh);
        this.mRefreshCamerList.setOnClickListener(mOnRefreshCamerListClickListener);

        this.mDisconnectCamer = (ImageView) this.mDetail.findViewById(R.id.webcamer_detail_disconnect);
        this.mDisconnectCamer.setOnClickListener(mOnDisconnectCamerClickListener);

        this.mCamerServiceBroadcasrReceiver = new CamerServiceBroadcasrReceiver();

        mServiceConnection = new ServiceConnection() {

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mService = null;
            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                CamerService.CamerBinder cb = (CamerService.CamerBinder) service;
                mService = cb.getService();
            }
        };

        mContext.bindService(new Intent(mContext, CamerService.class),
                mServiceConnection, Service.BIND_AUTO_CREATE);

        /* 注册通知 */
        IntentFilter filter = new IntentFilter();
        filter.addAction(CamerService.CamerListUpdated);
        filter.addAction(CamerService.CamerFound);
        mContext.registerReceiver(mCamerServiceBroadcasrReceiver, filter);
    }

    @Override
    public void onSuspend() {
        super.onSuspend();

        runGrabberThread = false; // 退出IPCamer图像获取线程
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.i(TAG, "");

        mContext.unregisterReceiver(mCamerServiceBroadcasrReceiver);

        runGrabberThread = false;

        this.mContext.unbindService(mServiceConnection);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        /*
         * 最后调用父类方法,把View的大小告诉父布局。
         */
        setMeasuredDimension(this.mKNXWebCamer.Width, this.mKNXWebCamer.Height);
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
            if(view instanceof SurfaceView) {
                cl = this.mKNXWebCamer.getPadding().getLeft();
                ct = this.mKNXWebCamer.getPadding().getTop();
                cr = this.mKNXWebCamer.Width - this.mKNXWebCamer.getPadding().getRight();
                cb = this.mKNXWebCamer.Height - this.mKNXWebCamer.getPadding().getBottom();
            } else if (view instanceof LinearLayout) {
                cl = view.getLeft();
                ct = view.getTop();
                cr = view.getRight();
                cb = view.getBottom();
            }
            view.layout(cl, ct, cr, cb);
        }
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int backColor = Color.parseColor(this.mKNXWebCamer.BackgroundColor);
        Paint paint = new Paint(ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);	// 充满
        paint.setAlpha((int)(this.mKNXWebCamer.Alpha*255));
        RectF oval3 = new RectF(0, 0, getWidth(), getHeight());// 设置个新的长方形

        Drawable back = null;
        if (null != this.mKNXWebCamer.getBackgroundImage()) {
            back = Drawable.createFromPath(
                    STKNXControllerConstant.ConfigResImgPath + this.mKNXWebCamer.getBackgroundImage());
        }

        if (null != back) {
            back.setBounds(0, 0, this.mKNXWebCamer.Width, this.mKNXWebCamer.Height);
            back.draw(canvas);
        } else if(KNXView.EFlatStyle.Stereo == this.mKNXWebCamer.getFlatStyle()) {	// 画立体感的圆角矩形

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
            paint.setARGB((int)(this.mKNXWebCamer.Alpha*255), Color.red(backColor), Color.green(backColor), Color.blue(backColor));
        }
        canvas.drawRoundRect(oval3, this.mKNXWebCamer.Radius, this.mKNXWebCamer.Radius, paint);//第二个参数是x半径，第三个参数是y半径

        paint.reset();

        if(KNXView.EBool.Yes == this.mKNXWebCamer.getDisplayBorder()) {
            paint.reset();
//            paint.setFlags(ANTI_ALIAS_FLAG);
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.parseColor(this.mKNXWebCamer.BorderColor));
            canvas.drawRoundRect(oval3, this.mKNXWebCamer.Radius, this.mKNXWebCamer.Radius, paint);//第二个参数是x半径，第三个参数是y半径
        }
    }

    private static class STKNXWebCamerHandler extends Handler {
        WeakReference<STKNXWebCamer> mSTKNXWebCamer;

        private STKNXWebCamerHandler(STKNXWebCamer webCamer) {
            super(webCamer.getContext().getMainLooper());
            this.mSTKNXWebCamer = new WeakReference<STKNXWebCamer>(webCamer);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            try {
                STKNXWebCamer stknxWebCamer = mSTKNXWebCamer.get();
                Log.i(TAG, "webCamer:"+stknxWebCamer);
                switch (msg.what) {
                    case DISPLAY_BITMAP_ON_SURFACE:
                        break;

                    case ERROR_CONNECT_TO_WEBCAMER:
                        break;

                    case UPDATE_CAMER_LIST:
                        Log.i(TAG, "UPDATE_CAMER_LIST");
                        stknxWebCamer.upgradeCamerList();
                        break;

                    case STOP_ANIMATION_REFRESH_CAMER_LIST:
                        Log.i(TAG, "STOP_ANIMATION_REFRESH_CAMER_LIST" + " " + stknxWebCamer.mRefreshCamerList.getAnimation());
                        stknxWebCamer.mRefreshCamerList.clearAnimation();
                        break;

                    case CAMER_ITEM_CONNECTION_STATUS_UPDATE:
                        break;

                    default:
                        break;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private class CamerItem {
//        ImageView icon;
        TextView title;
        TextView subTitle;
        ProgressBar connecting;
        ImageView connected;
    }

    private class WebCamerAdapter extends BaseAdapter {
        private static final int Disconnect = 1;
        private static final int Connecting = 2;
        private static final int Connected = 3;

        private Context mContext;
        private CamerDevice mCamerDevice;
        private List<CamerDevice> mListCamer;

        private int mState;

        private WebCamerAdapter(Context context) {
            mContext = context;
            mListCamer = new ArrayList<CamerDevice>();
        }

        @Override
        public int getCount() {
            return mListCamer.size();
        }

        @Override
        public Object getItem(int position) {
            return mListCamer.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CamerItem item;

            if (convertView == null) {
                item = new CamerItem();

                convertView = View.inflate(mContext, R.layout.webcamer_item, null);
                item.title = (TextView) convertView.findViewById(R.id.webcamer_item_name);
                item.subTitle = (TextView) convertView.findViewById(R.id.webcamer_item_detail);
                item.connecting = (ProgressBar) convertView.findViewById(R.id.webcamer_item_connecting);
                item.connected = (ImageView) convertView.findViewById(R.id.webcamer_item_connected);

                convertView.setTag(item);
            } else {
                item = (CamerItem) convertView.getTag();
            }

            CamerDevice device = mListCamer.get(position);
            item.title.setText(device.getIpAddress()); // 主标题：IPCamer的IP地址
            item.subTitle.setText(device.serviceURL); // 子标题： IPCmaer的onvif地址

            if (null != this.mCamerDevice) { // 当前已连接设备
                if (this.mCamerDevice.uuid.equals(device.uuid)) {
                    if (Connecting == this.mState) { // 正在连接
                        item.connected.setVisibility(INVISIBLE);
                        item.connecting.setVisibility(VISIBLE);
                    } else if (Connected == this.mState) { // 已连接
                        item.connecting.setVisibility(INVISIBLE);
                        item.connected.setVisibility(VISIBLE);
                    } else { //
                        item.connecting.setVisibility(INVISIBLE);
                        item.connected.setVisibility(INVISIBLE);
                    }
                } else { //
                    item.connecting.setVisibility(INVISIBLE);
                    item.connected.setVisibility(INVISIBLE);
                }
            } else { //
                item.connecting.setVisibility(INVISIBLE);
                item.connected.setVisibility(INVISIBLE);
            }

            return convertView;
        }

        /**
         * 清空Camer列表
         */
        private void clear() {
            mListCamer.clear();
        }

        /**
         *  添加Camer到列表
         * @param device
         */
        private void addCamer(CamerDevice device) {
            mListCamer.add(device);
        }

        /**
         * 设置当前选中的Camer
         * @param device
         */
        private void setmCamerDevice(CamerDevice device) {
            this.mCamerDevice = device;
        }

        public void setState(int state) {
            this.mState = state;
        }
    }

    private class VideoPlayer implements Runnable {
        private CamerDevice mDevice;
//        private AndroidFrameConverter mFrameConvert;

        private VideoPlayer(CamerDevice cd) {
            mDevice = cd;
//            mFrameConvert = new AndroidFrameConverter();
        }

        @Override
        public void run() {
            runGrabberThread = true;

            mSTKNXWebCamerHandler.post(new Runnable() {
                @Override
                public void run() {
                    mSurfaceView.setKeepScreenOn(true); // 设置背光常亮
                }
            });

            while (runGrabberThread) {
                try {
                    Bitmap bitmap = mDevice.grabImage(); // 获取图像
                    if (null != bitmap) {
                        Canvas canvas = mSurfaceHolder.lockCanvas();
                        if (null != canvas) {
                            canvas.drawBitmap(bitmap,
                                    new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()),
                                    new Rect(0, 0, mSurfaceView.getWidth(), mSurfaceView.getHeight()),
                                    null);
                            mSurfaceHolder.unlockCanvasAndPost(canvas);
                        }

                        if (!bitmap.isRecycled()) {
                            bitmap.recycle();
                        }

                    } else {
                        runGrabberThread = false;
                        Log.w(TAG, "Bitmap is null!!!");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();

                    runGrabberThread = false;

                    Log.w(TAG, "Thowing an exception!!!");
                }
            }

            Log.i(TAG, "Exit Loop!");

            mDevice.IPCamRelease();

            mSTKNXWebCamerHandler.post(new Runnable() {
                @Override
                public void run() {
                    mSurfaceView.setKeepScreenOn(false); // 关闭背光常亮
                }
            });

            try {
                if (null != mSurfaceHolder) {
                    Canvas canvas = mSurfaceHolder.lockCanvas(null);
                    if (null != canvas) {
                        canvas.drawColor(Color.BLACK); // 清楚画面，全黑
                        mSurfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }

                System.gc();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private class CamerServiceBroadcasrReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(CamerService.CamerListUpdated)) { // IPC列表已发生变化
                if (detIsOnShow) {
                    Message msgStr = mSTKNXWebCamerHandler.obtainMessage(
                            UPDATE_CAMER_LIST, STKNXWebCamer.this);
                    mSTKNXWebCamerHandler.sendMessage(msgStr);
                }
            } else if (intent.getAction().equals(CamerService.CamerFound)) { // 发现新的IPC
                if (detIsOnShow) {
                    Message msgStr = mSTKNXWebCamerHandler.obtainMessage(
                            STOP_ANIMATION_REFRESH_CAMER_LIST, STKNXWebCamer.this);
                    mSTKNXWebCamerHandler.sendMessage(msgStr);
                }
            }
        }
    }

    OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                toggleCamerDetail(); // 上滑、隐藏IPCamer列表
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    };

    OnClickListener mOnRefreshCamerListClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                RotateAnimation anim = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF,
                        0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                anim.setDuration(1000);
                anim.setRepeatCount(Animation.INFINITE);
                anim.setRepeatMode(Animation.RESTART);
                mRefreshCamerList.startAnimation(anim); // 开启 按钮 旋转动画

                mService.sendBroadcast(); // 发起IPCamer搜寻
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    };

    OnClickListener mOnDisconnectCamerClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                disconnectCamerDevice(); // 断开设备连接
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    };

    AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            try {
                runGrabberThread = false; // 退出先前的Camer采集线程

                mCurCamerDevice = mWebCamerAdapter.mListCamer.get(position);
                mCurCamerDevice.setSecurity("", ""); // 设置登录密码

                mWebCamerAdapter.setmCamerDevice(mCurCamerDevice); // 设置当前选中设备
                mWebCamerAdapter.setState(WebCamerAdapter.Connecting); // 设置状态
                mWebCamerAdapter.notifyDataSetChanged();

                mCurCamerDevice.setOnCamerDeviceistener(new OnCamerDeviceistener() {

                    @Override
                    public void onSoapDone(CamerDevice device, boolean success) {
                        Log.i(TAG, "success:" + success);
                        if (success) { // 连接成功
                            try {
                                mService.getDb().addCamer(device);

                                new Thread(new VideoPlayer(device)).start();

                                /* 更新列表状态 */
                                mWebCamerAdapter.setState(WebCamerAdapter.Connected);
                                Message msg = new Message();
                                msg.what = UPDATE_CAMER_LIST;
                                msg.obj = STKNXWebCamer.this;
                                mSTKNXWebCamerHandler.sendMessage(msg);

                                mSTKNXWebCamerHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        toggleCamerDetail();
                                    }
                                }, 1000); // 延迟一秒收回Camer列表
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        } else { // 连接失败
                            Message msgStr = mSTKNXWebCamerHandler.obtainMessage(ERROR_CONNECT_TO_WEBCAMER, STKNXWebCamer.this);
                            mSTKNXWebCamerHandler.sendMessage(msgStr);
                        }
                    }

                    @Override
                    public void onDisconnected(CamerDevice device) {
                        runGrabberThread = false;
                    }
                });
                mCurCamerDevice.IPCamInit(); // 连接IPCamer
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    };

    private void toggleCamerDetail() {
        Log.i(TAG, "detIsOnShow:"+detIsOnShow);
        if (detIsOnShow) { // 上滑窗已经显示
            TranslateAnimation animMove = new TranslateAnimation(0, 0, 0, detHeight);
            animMove.setDuration(500);
            animMove.setInterpolator(new AccelerateDecelerateInterpolator());
            animMove.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mDetail.clearAnimation();   //防止跳动
                    mDetail.setTop(detPosDefault);
                    detIsOnShow = false;
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            mDetail.startAnimation(animMove); // 启动下滑动画
        } else { // 上滑窗已隐藏
//            upgradeCamerList();

            TranslateAnimation animMove = new TranslateAnimation(0, 0, 0, -detHeight);
            animMove.setDuration(500);
            animMove.setInterpolator(new AccelerateDecelerateInterpolator());
//            Log.i(TAG, "aniMove:" + animMove + " mDetail:" + mDetail + " detHeight:"+detHeight);
            animMove.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    Log.i(TAG, "");
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    Log.i(TAG, "");
                    mDetail.clearAnimation();   //防止跳动
                    mDetail.setTop(detPosOnShow);
                    detIsOnShow = true;

                    upgradeCamerList(); // 更新IPC列表

                    mRefreshCamerList.clearAnimation();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    Log.i(TAG, "");
                }
            });

            mDetail.startAnimation(animMove); // 启动上滑动画
        }
    }

    private void disconnectCamerDevice() {
        if (null != mCurCamerDevice) {
            runGrabberThread = false; // 退出流媒体采集线程

            /* 更新Cmaer列表 */
            mWebCamerAdapter.setState(WebCamerAdapter.Disconnect);
            Message msg = new Message();
            msg.what = UPDATE_CAMER_LIST;
            msg.obj = STKNXWebCamer.this;
            mSTKNXWebCamerHandler.sendMessage(msg);
        }
    }

    /**
     * 从Finder获取最新的Camer列表
     */
    private void upgradeCamerList() {
        mWebCamerAdapter.clear();
        List<CamerDevice> list = mService.getFinder().getCamerList();
        for (CamerDevice device : list) {
            mWebCamerAdapter.addCamer(device); // 更新到Camer Adapter
        }
        mWebCamerAdapter.notifyDataSetChanged();
    }
}
