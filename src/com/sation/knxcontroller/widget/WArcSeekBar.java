package com.sation.knxcontroller.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.sation.knxcontroller.R;
import com.sation.knxcontroller.util.Log;

/**
 * Created by wangchunfeng on 2017/8/29.
 */

public class WArcSeekBar extends View {
    private final String TAG = "WArcSeekBar";

    private Context mContext = null;

    private Drawable mThumbDrawable = null;
    private int mThumbHeight = 0;
    private int mThumbWidth = 0;
    private int[] mThumbNormal = null;
    private int[] mThumbPressed = null;

    private int mStartDegree = 0;
    private int mSweepDegree = 0;
    private Drawable mMinDrawable = null;
    private Drawable mMaxDrawable = null;
    private String mPrefix = null;
    private String mSuffix = null;

    private int mSeekBarMax = 0;
    private Paint mSeekBarBackgroundPaint = null;
    private Paint mSeekbarProgressPaint = null;
    private RectF mArcRectF = null;

    private boolean mIsShowProgressText = false;
    private Paint mProgressTextPaint = null;
    private int mProgressTextSize = 0;

    private int mViewHeight = 0;
    private int mViewWidth = 0;
    private int mSeekBarSize = 0;
    private int mSeekBarRadius = 0;
    private int mSeekBarCenterX = 0;
    private int mSeekBarCenterY = 0;
    private float mThumbLeft = 0;
    private float mThumbTop = 0;

    private float mSeekBarDegree = 0;
    private int mCurrentProgress = 0;
    private float mStartPointX;
    private float mStartPointY;

    private OnSeekBarChangeListener mOnSeekBarChangeListener = null;

    public interface OnSeekBarChangeListener {

        void onProgressChanged(WArcSeekBar seekBar, int progress);

        void onStartTrackingTouch(WArcSeekBar seekBar);

        void onStopTrackingTouch(WArcSeekBar seekBar);
    }

    public WArcSeekBar(Context context) {
        this(context, null);
//        mContext = context;
//        initViewDefault();
//        mArcRectF = new RectF();
    }

    public WArcSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
//        mContext = context;
//        initViewAttrs(attrs);
//        mArcRectF = new RectF();
    }

    public WArcSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        initViewAttrs(attrs);
        mArcRectF = new RectF();
    }

    private void initViewAttrs(AttributeSet attrs) {
        TypedArray localTypedArray = mContext.obtainStyledAttributes(attrs, R.styleable.WArcSeekBar);

        //thumb的属性是使用android:thumb属性进行设置的
        //返回的Drawable为一个StateListDrawable类型，即可以实现选中效果的drawable list
        //mThumbNormal和mThumbPressed则是用于设置不同状态的效果，当点击thumb时设置mThumbPressed，否则设置mThumbNormal
        mThumbDrawable = localTypedArray.getDrawable(R.styleable.WArcSeekBar_android_thumb);
        mThumbWidth = mThumbDrawable.getIntrinsicWidth();
        mThumbHeight = mThumbDrawable.getIntrinsicHeight();

        mThumbNormal = new int[]{-android.R.attr.state_focused, -android.R.attr.state_pressed,
                -android.R.attr.state_selected, -android.R.attr.state_checked};
        mThumbPressed = new int[]{android.R.attr.state_focused, android.R.attr.state_pressed,
                android.R.attr.state_selected, android.R.attr.state_checked};

        mSeekBarDegree = mStartDegree = localTypedArray.getInteger(R.styleable.WArcSeekBar_start_angle, 135);
        mSweepDegree = localTypedArray.getInteger(R.styleable.WArcSeekBar_sweep_angle, 270);
        mSweepDegree = mSweepDegree > 360 ? 360 : mSweepDegree;

        float progressWidth = localTypedArray.getDimension(R.styleable.WArcSeekBar_arc_width, 5);
        int progressBackgroundColor = localTypedArray.getColor(R.styleable.WArcSeekBar_arc_background_color, Color.GRAY);
        int progressFrontColor = localTypedArray.getColor(R.styleable.WArcSeekBar_arc_front_color, Color.BLUE);
        mSeekBarMax = localTypedArray.getInteger(R.styleable.WArcSeekBar_seekbar_max_value, 100);

        mSeekbarProgressPaint = new Paint();
        mSeekBarBackgroundPaint = new Paint();

        mSeekbarProgressPaint.setColor(progressFrontColor);
        mSeekBarBackgroundPaint.setColor(progressBackgroundColor);

        mSeekbarProgressPaint.setAntiAlias(true);
        mSeekBarBackgroundPaint.setAntiAlias(true);

        mSeekbarProgressPaint.setStyle(Paint.Style.STROKE);
        mSeekBarBackgroundPaint.setStyle(Paint.Style.STROKE);

        mSeekbarProgressPaint.setStrokeWidth(progressWidth);
        mSeekBarBackgroundPaint.setStrokeWidth(progressWidth);

        mIsShowProgressText = localTypedArray.getBoolean(R.styleable.WArcSeekBar_show_text, false);
        int progressTextStroke = (int) localTypedArray.getDimension(R.styleable.WArcSeekBar_text_stroke_width, 5);
        int progressTextColor = localTypedArray.getColor(R.styleable.WArcSeekBar_text_color, Color.GREEN);
        mProgressTextSize = (int) localTypedArray.getDimension(R.styleable.WArcSeekBar_text_size, 50);

        mMinDrawable = localTypedArray.getDrawable(R.styleable.WArcSeekBar_min_drawable);
        mMaxDrawable = localTypedArray.getDrawable(R.styleable.WArcSeekBar_max_drawable);
        mPrefix = localTypedArray.getString(R.styleable.WArcSeekBar_text_prefix);
        mSuffix = localTypedArray.getString(R.styleable.WArcSeekBar_text_suffix);

        mProgressTextPaint = new Paint();
        mProgressTextPaint.setColor(progressTextColor);
        mProgressTextPaint.setAntiAlias(true);
        mProgressTextPaint.setStrokeWidth(progressTextStroke);
        mProgressTextPaint.setTextSize(mProgressTextSize);

        localTypedArray.recycle();
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mViewWidth = measureWidth(widthMeasureSpec);
        mViewHeight = measureHeight(heightMeasureSpec);
        setMeasuredDimension(mViewWidth, mViewHeight);

        mViewWidth -= (getPaddingLeft()+getPaddingRight());
        mViewHeight -= (getPaddingTop()+getPaddingBottom());

        mSeekBarSize = mViewWidth > mViewHeight ? mViewHeight : mViewWidth;

        mSeekBarCenterX = getPaddingLeft() + mViewWidth / 2;
        mSeekBarCenterY = getPaddingTop() + mViewHeight / 2;

        mSeekBarRadius = mSeekBarSize / 2 - mThumbWidth / 2;

        int left = mSeekBarCenterX - mSeekBarRadius;
        int right = mSeekBarCenterX + mSeekBarRadius;
        int top = mSeekBarCenterY - mSeekBarRadius;
        int bottom = mSeekBarCenterY + mSeekBarRadius;
        mArcRectF.set(left, top, right, bottom);

        // 起始位置，三点钟方向
        setThumbPosition(Math.toRadians(mSeekBarDegree));
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        canvas.drawArc(this.mArcRectF, mStartDegree, mSweepDegree, false, mSeekBarBackgroundPaint);
        drawMinMaxDrawable(mMinDrawable, mStartDegree-10, canvas);
        drawMinMaxDrawable(mMaxDrawable, mStartDegree+mSweepDegree+10, canvas);
        canvas.drawArc(this.mArcRectF, mStartDegree, (mSeekBarDegree - mStartDegree), false, mSeekbarProgressPaint);
        drawThumbBitmap(canvas);
        drawProgressText(canvas);

        super.onDraw(canvas);
    }

    private void drawMinMaxDrawable(Drawable drawable, int degree, Canvas canvas) {
        if (null != drawable) {
            int drawableWidth = drawable.getIntrinsicWidth();
            int drawableHeight = drawable.getIntrinsicHeight();
            double radians = Math.toRadians(degree);
            double x = mSeekBarCenterX + mSeekBarRadius * Math.cos(radians);
            double y = mSeekBarCenterY + mSeekBarRadius * Math.sin(radians);
            float left = (float) (x - drawableWidth / 2);
            float top = (float) (y - drawableHeight / 2);

            drawable.setBounds((int) left, (int) top,
                    (int) (left + drawableWidth), (int) (top + drawableHeight));
            drawable.draw(canvas);
        }
    }

    private void drawThumbBitmap(Canvas canvas) {
        if (null != mThumbDrawable) {
            mThumbDrawable.setBounds((int) mThumbLeft, (int) mThumbTop,
                    (int) (mThumbLeft + mThumbWidth), (int) (mThumbTop + mThumbHeight));
            mThumbDrawable.draw(canvas);
        }
    }

    private void drawProgressText(Canvas canvas) {
        if (true == mIsShowProgressText) {
            String text = mCurrentProgress + "";
            if (null != mPrefix) {
                text = mPrefix + text;
            }

            if (null != mSuffix) {
                text = text + mSuffix;
            }
            float textWidth = mProgressTextPaint.measureText(text);
            canvas.drawText(text, mSeekBarCenterX - textWidth / 2, mSeekBarCenterY
                    + mProgressTextSize / 2, mProgressTextPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float eventX = event.getX();
        float eventY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartPointX = event.getX();
                mStartPointY = event.getY();
                if (null != mOnSeekBarChangeListener) {
                    mOnSeekBarChangeListener.onStartTrackingTouch(WArcSeekBar.this);
                }
                seekTo(eventX, eventY, false);
                break;

            case MotionEvent.ACTION_MOVE:
                seekTo(eventX, eventY, false);
                mStartPointX = event.getX();
                mStartPointY = event.getY();
                break;

            case MotionEvent.ACTION_UP:
                if (null != mOnSeekBarChangeListener) {
                    mOnSeekBarChangeListener.onStopTrackingTouch(WArcSeekBar.this);
                }
                seekTo(eventX, eventY, true);
                break;
        }
        return true;
    }

    private void seekTo(float eventX, float eventY, boolean isUp) {
        if (true == isPointOnThumb(eventX, eventY) && false == isUp) {
            if (null != mThumbDrawable) {
                mThumbDrawable.setState(mThumbPressed);
            }
            double radian = Math.atan2(eventY - mSeekBarCenterY, eventX - mSeekBarCenterX);
            /*
             * 由于atan2返回的值为[-pi,pi]
             * 因此需要将弧度值转换一下，使得区间为[0,2*pi]
             */
            if (radian < 0) {
                radian = radian + 2 * Math.PI;
            }

            int degree = (int) Math.round(Math.toDegrees(radian));
            int clockWise = (int) ((mStartPointX - mSeekBarCenterX) * (eventY - mSeekBarCenterY) -
                    (mStartPointY - mSeekBarCenterY) * (eventX - mSeekBarCenterX));
            boolean result = isValidRange(degree);
            if (result) {
                mSeekBarDegree = degree;
                mSeekBarDegree = mSeekBarDegree >= mStartDegree ? mSeekBarDegree : mSeekBarDegree + 360;
                mCurrentProgress = (int) (mSeekBarMax * (mSeekBarDegree - mStartDegree) / mSweepDegree);
                setThumbPosition(radian);
            } else if (clockWise > 0) {
                if (0 != mCurrentProgress) {
                    mSeekBarDegree = mStartDegree + mSweepDegree;
                    mCurrentProgress = mSeekBarMax;
                    setThumbPosition(Math.toRadians(mSeekBarDegree));
                }
            } else if (clockWise < 0) {
                if (mSeekBarMax != mCurrentProgress) {
                    mSeekBarDegree = mStartDegree;
                    mCurrentProgress = 0;
                    setThumbPosition(Math.toRadians(mSeekBarDegree));
                }
            }

            if (null != mOnSeekBarChangeListener) {
                mOnSeekBarChangeListener.onProgressChanged(WArcSeekBar.this, mCurrentProgress);
            }
            invalidate();
        } else {
            if (null != mThumbDrawable) {
                mThumbDrawable.setState(mThumbNormal);
            }
            invalidate();
        }
    }

    private boolean isPointOnThumb(float eventX, float eventY) {
        boolean result = false;
        double distance = Math.sqrt(Math.pow(eventX - mSeekBarCenterX, 2)
                + Math.pow(eventY - mSeekBarCenterY, 2));
        if (distance < mSeekBarSize && distance > (mSeekBarSize / 2 - mThumbWidth)) {
            result = true;
        }
        return result;
    }

    private void setThumbPosition(double radian) {

        double x = mSeekBarCenterX + mSeekBarRadius * Math.cos(radian);
        double y = mSeekBarCenterY + mSeekBarRadius * Math.sin(radian);
        mThumbLeft = (float) (x - mThumbWidth / 2);
        mThumbTop = (float) (y - mThumbHeight / 2);
    }

    private boolean isValidRange(int degree) {
        int range1Start = -1;
        int range1End = -1;
        int range2Start = -1;
        int range2End = -1;

        range1Start = mStartDegree;
        range1End = (mStartDegree + mSweepDegree);
        if (range1End >= 360) {
            range2Start = 0;
            range2End = range1End - 360;
            range1End = 360;
        }

        if ((degree >= range1Start) && (degree <= range1End)) {
            return true;
        } else if ((degree >= range2Start) && (degree <= range2End)) {
            return true;
        } else {
            return false;
        }
    }

    private int getSeekBarDegree(int degree) {
        if (degree >= mStartDegree) {
            return degree;
        } else {
            return degree + 360;
        }
    }

    /*
     * 增加set方法，用于在java代码中调用
     */
    public synchronized void setProgress(int progress) {

        if (progress > mSeekBarMax) {
            progress = mSeekBarMax;
        }
        if (progress < 0) {
            progress = 0;
        }
        mCurrentProgress = progress;
        mSeekBarDegree = (progress * mSweepDegree / mSeekBarMax) + mStartDegree;

        setThumbPosition(Math.toRadians(mSeekBarDegree));

        invalidate();
    }

    public synchronized int getProgress() {
        return mCurrentProgress;
    }

    public synchronized void setProgressMax(int max) {

        mSeekBarMax = max;
    }

    public synchronized int getProgressMax() {
        return mSeekBarMax;
    }

    public void setProgressThumb(int thumbId) {
        mThumbDrawable = mContext.getResources().getDrawable(thumbId);
        mThumbWidth = mThumbDrawable.getIntrinsicWidth();
        mThumbHeight = mThumbDrawable.getIntrinsicHeight();
    }

    public void setProgressWidth(int width) {

        mSeekbarProgressPaint.setStrokeWidth(width);
        mSeekBarBackgroundPaint.setStrokeWidth(width);
    }

    public void setProgressBackgroundColor(int color) {
        mSeekBarBackgroundPaint.setColor(color);
    }

    public void setProgressFrontColor(int color) {
        mSeekbarProgressPaint.setColor(color);
    }

    public void setProgressTextColor(int color) {
        mProgressTextPaint.setColor(color);
    }

    public void setProgressTextSize(int size) {

        mProgressTextPaint.setTextSize(size);
    }

    public void setProgressTextStrokeWidth(int width) {

        mProgressTextPaint.setStrokeWidth(width);
    }

    public void setIsShowProgressText(boolean isShow) {
        mIsShowProgressText = isShow;
    }

            /*
     * 增加SeekBar change的监听
     */

    public void setOnSeekBarChangeListener(OnSeekBarChangeListener onSeekBarChangeListener) {
        mOnSeekBarChangeListener = onSeekBarChangeListener;
    }

    private int measureWidth(int measureSpec) {
        int result = 200;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (specMode) {
            case MeasureSpec.UNSPECIFIED:
                result = specSize;
                break;
            case MeasureSpec.AT_MOST:
                result = Math.min(result, specSize);
                break;
            case MeasureSpec.EXACTLY:
                result = specSize;
                break;
        }
        return result;
    }

    private int measureHeight(int measureSpec) {
        int result = 200;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (specMode) {
            case MeasureSpec.UNSPECIFIED:
                result = specSize;
                break;
            case MeasureSpec.AT_MOST:
                result = Math.min(result, specSize);
                break;
            case MeasureSpec.EXACTLY:
                result = specSize;
                break;
        }
        return result;
    }
}
