package com.sation.knxcontroller.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.sation.knxcontroller.R;
import com.sation.knxcontroller.util.Log;

import java.util.ArrayList;

/**
 * Created by wangchunfeng on 2017/9/1.
 */

public class SectionalSeekBar extends View {
    private final String TAG = "SectionalSeekBar";
    private int width;
    private int height;
    private int downX = 0;
    private int downY = 0;
    private int upX = 0;
    private int upY = 0;
    private int moveX = 0;
    private int moveY = 0;
    private int perWidth = 0;
    private Paint mPaint;
    private Paint mTextPaint;
    private Paint buttonPaint;
    private Drawable thumb;
    private Drawable mUpMin;
    private Drawable mUpMinOn;
    private Drawable mUpMiddle;
    private Drawable mUpMiddleOn;
    private Drawable mMax;
    private Drawable mMaxOn;
    private Drawable mDownMiddle;
    private Drawable mDownMiddleOn;
    private Drawable mDownMin;
    private Drawable mDownMinOn;
    private String mTxtUpMin;
    private String mTxtUpMiddle;
    private String mTxtMax;
    private String mTxtDownMiddle;
    private String mTxtDownMin;
    private int mEnableTextColor;
    private int mDisableTextColor;
    private Drawable mSeekBarEnabled;
    private Drawable mSeekBarDisabled;
    private int mSeekBarWidth;
    private int mMaxValue;
    private int mValue;
    private int mSpacing;

    private int bitMapHeight = 38;//第一个点的起始位置起始，图片的长宽是76，所以取一半的距离
    private int textMove = 60;//字与下方点的距离，因为字体字体是40px，再加上10的间隔
    private float textSize;
    private int circleRadius;

    private OnSeekBarChangeListener mOnSeekBarChangeListener = null;

    Rect rectBmp1;
    Rect rectLine1;
    Rect rectBmp2;
    Rect rectLine2;
    Rect rectBmp3;
    Rect rectLine3;
    Rect rectBmp4;
    Rect rectLine4;
    Rect rectBmp5;

    int bmp1CenterX;
    int bmp1CenterY;
    int bmp5CenterX;
    int bmp5CenterY;
    int curPosX;
    int curPosY;

    Rect rectThumb;
    Rect rectEnabled;

    float textY;
    float text1X;
    float text2X;
    float text3X;
    float text4X;
    float text5X;

    public interface OnSeekBarChangeListener {

        void onProgressChanged(SectionalSeekBar seekBar, int progress);

        void onStartTrackingTouch(SectionalSeekBar seekBar);

        void onStopTrackingTouch(SectionalSeekBar seekBar);
    }

    public SectionalSeekBar(Context context) {
        this(context, null);
    }

    public SectionalSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SectionalSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        rectBmp1 = new Rect();
        rectBmp2 = new Rect();
        rectBmp3 = new Rect();
        rectBmp4 = new Rect();
        rectBmp5 = new Rect();
        rectLine1 = new Rect();
        rectLine2 = new Rect();
        rectLine3 = new Rect();
        rectLine4 = new Rect();
        rectThumb = new Rect();
        rectEnabled = new Rect();

        TypedArray localTypedArray = context.obtainStyledAttributes(attrs, R.styleable.SectionalSeekBar);

        thumb = localTypedArray.getDrawable(R.styleable.SectionalSeekBar_android_thumb);
        mUpMin = localTypedArray.getDrawable(R.styleable.SectionalSeekBar_up_min_disabled);
        mUpMinOn = localTypedArray.getDrawable(R.styleable.SectionalSeekBar_up_min_enabled);
        mUpMiddle = localTypedArray.getDrawable(R.styleable.SectionalSeekBar_up_middle_disabled);
        mUpMiddleOn = localTypedArray.getDrawable(R.styleable.SectionalSeekBar_up_middle_enabled);
        mMax = localTypedArray.getDrawable(R.styleable.SectionalSeekBar_max_disabled);
        mMaxOn = localTypedArray.getDrawable(R.styleable.SectionalSeekBar_max_enabled);
        mDownMiddle = localTypedArray.getDrawable(R.styleable.SectionalSeekBar_down_middle_disabled);
        mDownMiddleOn = localTypedArray.getDrawable(R.styleable.SectionalSeekBar_down_middle_enabled);
        mDownMin = localTypedArray.getDrawable(R.styleable.SectionalSeekBar_down_min_disabled);
        mDownMinOn = localTypedArray.getDrawable(R.styleable.SectionalSeekBar_down_min_enabled);
        mTxtUpMin = localTypedArray.getString(R.styleable.SectionalSeekBar_text_up_min);
        mTxtUpMiddle = localTypedArray.getString(R.styleable.SectionalSeekBar_text_up_middle);
        mTxtMax = localTypedArray.getString(R.styleable.SectionalSeekBar_text_max);
        mTxtDownMiddle = localTypedArray.getString(R.styleable.SectionalSeekBar_text_down_middle);
        mTxtDownMin = localTypedArray.getString(R.styleable.SectionalSeekBar_text_down_min);
        mEnableTextColor = localTypedArray.getColor(R.styleable.SectionalSeekBar_enabled_text_color, Color.WHITE);
        mDisableTextColor = localTypedArray.getColor(R.styleable.SectionalSeekBar_disabled_text_color, Color.DKGRAY);
        mSeekBarEnabled = localTypedArray.getDrawable(R.styleable.SectionalSeekBar_seekbar_enabled);
        mSeekBarDisabled = localTypedArray.getDrawable(R.styleable.SectionalSeekBar_seekbar_disabled);
        mSeekBarWidth = (int)localTypedArray.getDimension(R.styleable.SectionalSeekBar_seekbar_width, 4);
        mMaxValue = localTypedArray.getInt(R.styleable.SectionalSeekBar_max_value, 100);
        mValue = localTypedArray.getInt(R.styleable.SectionalSeekBar_seekbar_value, 50);
        mSpacing = (int)localTypedArray.getDimension(R.styleable.SectionalSeekBar_spacing, 4);
        bitMapHeight = thumb.getIntrinsicWidth();
        textMove = bitMapHeight+22;
        textSize = localTypedArray.getDimension(R.styleable.SectionalSeekBar_android_textSize, 20);
        circleRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, getResources().getDisplayMetrics());
        mPaint = new Paint(Paint.DITHER_FLAG);
        mPaint.setAntiAlias(true);//锯齿不显示
        mPaint.setStrokeWidth(3);
        mTextPaint = new Paint(Paint.DITHER_FLAG);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(textSize);
        mTextPaint.setColor(0xffb5b5b4);
        buttonPaint = new Paint(Paint.DITHER_FLAG);
        buttonPaint.setAntiAlias(true);

        localTypedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        width = measureWidth(widthMeasureSpec);
        height = measureHeight(heightMeasureSpec);
        setMeasuredDimension(width, height);

        width -= (getPaddingLeft()+getPaddingRight());
        height -= (getPaddingTop()+getPaddingBottom());

        int w1 = (null == mUpMin ? 0 :mUpMin.getIntrinsicWidth()) +
                (null == mUpMiddle ? 0:mUpMiddle.getIntrinsicWidth()) +
                (null == mMax ? 0:mMax.getIntrinsicWidth()) +
                (null == mDownMiddle ? 0:mDownMiddle.getIntrinsicWidth()) +
                (null == mDownMin ? 0:mDownMin.getIntrinsicWidth());
        int w2 = ((null == mUpMinOn ? 0 :mUpMinOn.getIntrinsicWidth()) +
                (null == mUpMiddleOn ? 0:mUpMiddleOn.getIntrinsicWidth()) +
                (null == mMaxOn ? 0:mMaxOn.getIntrinsicWidth()) +
                (null == mDownMiddleOn ? 0:mDownMiddleOn.getIntrinsicWidth()) +
                (null == mDownMinOn ? 0:mDownMinOn.getIntrinsicWidth()));

        perWidth = (width - (w1 > w2 ? w1:w2) - (8*mSpacing)) / 4;

        textY = getPaddingTop() + height / 3;

        int bmp1X = getPaddingLeft();
        int bmp1Y = getPaddingTop() + height /2;
        int bmp1Right = bmp1X+mUpMin.getIntrinsicWidth();
        int bmp1Bottom = bmp1Y+mUpMin.getIntrinsicHeight();
        rectBmp1.set(bmp1X, bmp1Y, bmp1Right, bmp1Bottom);

        bmp1CenterX = rectBmp1.centerX();
        bmp1CenterY  = rectBmp1.centerY();

        int line1X = bmp1Right + mSpacing;
        int line1Y = bmp1CenterY - mSeekBarWidth/2;
        int line1Right = line1X + perWidth;
        int line1Bottom = bmp1CenterY + mSeekBarWidth/2;
        rectLine1.set(line1X, line1Y, line1Right, line1Bottom);

        int bmp2X = line1Right + mSpacing;
        int bmp2Y = bmp1CenterY - mUpMiddle.getIntrinsicHeight() /2;
        int bmp2Right = bmp2X+mUpMiddle.getIntrinsicWidth();
        int bmp2Bottom = bmp2Y+mUpMiddle.getIntrinsicHeight();
        rectBmp2.set(bmp2X, bmp2Y, bmp2Right, bmp2Bottom);

        int line2X = bmp2Right + mSpacing;
        int line2Y = line1Y;
        int line2Right = line2X + perWidth;
        int line2Bottom = line1Bottom;
        rectLine2.set(line2X, line2Y, line2Right, line2Bottom);

        int bmp3X = line2Right + mSpacing;
        int bmp3Y = bmp1CenterY - mMax.getIntrinsicHeight() /2;
        int bmp3Right = bmp3X+mMax.getIntrinsicWidth();
        int bmp3Bottom = bmp3Y+mMax.getIntrinsicHeight();
        rectBmp3.set(bmp3X, bmp3Y, bmp3Right, bmp3Bottom);

        int line3X = bmp3Right + mSpacing;
        int line3Y = line2Y;
        int line3Right = line3X + perWidth;
        int line3Bottom = line2Bottom;
        rectLine3.set(line3X, line3Y, line3Right, line3Bottom);

        int bmp4X = line3Right + mSpacing;
        int bmp4Y = bmp1CenterY - mDownMiddle.getIntrinsicHeight() /2;
        int bmp4Right = bmp4X +mDownMiddle.getIntrinsicWidth();
        int bmp4Bottom = bmp4Y+mDownMiddle.getIntrinsicHeight();
        rectBmp4.set(bmp4X, bmp4Y, bmp4Right, bmp4Bottom);

        int line4X = bmp4Right + mSpacing;
        int line4Y = line3Y;
        int line4Right = line4X + perWidth;
        int line4Bottom = line3Bottom;
        rectLine4.set(line4X, line4Y, line4Right, line4Bottom);

        int bmp5X = line4Right + mSpacing;
        int bmp5Y = bmp1CenterY - mDownMin.getIntrinsicHeight() /2;
        int bmp5Right = bmp5X+mDownMin.getIntrinsicWidth();
        int bmp5Bottom = bmp5Y+mDownMin.getIntrinsicHeight();
        rectBmp5.set(bmp5X, bmp5Y, bmp5Right, bmp5Bottom);

        bmp5CenterX = rectBmp5.centerX();
        bmp5CenterY  = rectBmp5.centerY();

        mPaint.setTextSize(textSize);
        text1X = rectBmp1.centerX() - mPaint.measureText(mTxtUpMin)/2;
        text2X = rectBmp2.centerX() - mPaint.measureText(mTxtUpMiddle)/2;
        text3X = rectBmp3.centerX() - mPaint.measureText(mTxtMax)/2;
        text4X = rectBmp4.centerX() - mPaint.measureText(mTxtDownMiddle)/2;
        text5X = rectBmp5.centerX() - mPaint.measureText(mTxtDownMin)/2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(mSeekBarWidth);

        int validWidth = bmp5CenterX - bmp1CenterX;
        float curValue = (float)mValue / mMaxValue;
        curPosX = bmp1CenterX + (int)(validWidth * curValue);
        curPosY = bmp5CenterY;

        rectThumb.set(curPosX-thumb.getIntrinsicWidth()/2,
                curPosY - thumb.getIntrinsicHeight() /2,
                curPosX+thumb.getIntrinsicWidth()/2,
                curPosY+thumb.getIntrinsicHeight()/2);

        if (null != mUpMin) {
            mUpMin.setBounds(rectBmp1);
            mUpMin.draw(canvas);
        }
        mSeekBarDisabled.setBounds(rectLine1);
        mSeekBarDisabled.draw(canvas);

        if(null != mUpMiddle) {
            mUpMiddle.setBounds(rectBmp2);
            mUpMiddle.draw(canvas);
        }
        mSeekBarDisabled.setBounds(rectLine2);
        mSeekBarDisabled.draw(canvas);

        if(null != mMax) {
            mMax.setBounds(rectBmp3);
            mMax.draw(canvas);
        }
        mSeekBarDisabled.setBounds(rectLine3);
        mSeekBarDisabled.draw(canvas);

        if (null != mDownMiddle) {
            mDownMiddle.setBounds(rectBmp4);
            mDownMiddle.draw(canvas);
        }
        mSeekBarDisabled.setBounds(rectLine4);
        mSeekBarDisabled.draw(canvas);

        if(null != mDownMin) {
            mDownMin.setBounds(rectBmp5);
            mDownMin.draw(canvas);
        }

        if (curPosX > rectLine1.left) {
            if (null != mUpMinOn) {
                mUpMinOn.setBounds(rectBmp1);
                mUpMinOn.draw(canvas);
            }
            if (curPosX < rectLine1.right) {
                rectEnabled.set(rectLine1.left, rectLine1.top, curPosX, rectLine1.bottom);
                mSeekBarEnabled.setBounds(rectEnabled);
                mSeekBarEnabled.draw(canvas);
            } else {
                mSeekBarEnabled.setBounds(rectLine1);
                mSeekBarEnabled.draw(canvas);
                if(curPosX > rectLine2.left) {
                    if(null != mUpMiddleOn) {
                        mUpMiddleOn.setBounds(rectBmp2);
                        mUpMiddleOn.draw(canvas);
                    }
                    if (curPosX < rectLine2.right) {
                        rectEnabled.set(rectLine2.left, rectLine2.top, curPosX, rectLine2.bottom);
                        mSeekBarEnabled.setBounds(rectEnabled);
                        mSeekBarEnabled.draw(canvas);
                    } else {
                        mSeekBarEnabled.setBounds(rectLine2);
                        mSeekBarEnabled.draw(canvas);
                        if (curPosX > rectLine3.left) {
                            if(null != mMaxOn) {
                                mMaxOn.setBounds(rectBmp3);
                                mMaxOn.draw(canvas);
                            }
                            if (curPosX < rectLine3.right) {
                                rectEnabled.set(rectLine3.left, rectLine3.top, curPosX, rectLine3.bottom);
                                mSeekBarEnabled.setBounds(rectEnabled);
                                mSeekBarEnabled.draw(canvas);
                            } else {
                                mSeekBarEnabled.setBounds(rectLine3);
                                mSeekBarEnabled.draw(canvas);
                                if (curPosX > rectLine4.left) {
                                    if (null != mDownMiddleOn) {
                                        mDownMiddleOn.setBounds(rectBmp4);
                                        mDownMiddleOn.draw(canvas);
                                    }
                                    if (curPosX < rectLine4.right) {
                                        rectEnabled.set(rectLine4.left, rectLine4.top, curPosX, rectLine4.bottom);
                                        mSeekBarEnabled.setBounds(rectEnabled);
                                        mSeekBarEnabled.draw(canvas);
                                    } else {
                                        mSeekBarEnabled.setBounds(rectLine4);
                                        mSeekBarEnabled.draw(canvas);
                                        if(curPosX >= bmp5CenterX) {
                                            if(null != mDownMinOn) {
                                                mDownMinOn.setBounds(rectBmp5);
                                                mDownMinOn.draw(canvas);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        mPaint.setTextSize(textSize);
        mPaint.setColor(mDisableTextColor);
        canvas.drawText(mTxtUpMin, text1X, textY, mPaint);
        canvas.drawText(mTxtUpMiddle, text2X, textY, mPaint);
        canvas.drawText(mTxtMax, text3X, textY, mPaint);
        canvas.drawText(mTxtDownMiddle, text4X, textY, mPaint);
        canvas.drawText(mTxtDownMin, text5X, textY, mPaint);
        mPaint.setColor(mEnableTextColor);
        if (curPosX >= rectBmp1.centerX()) {
            canvas.drawText(mTxtUpMin, text1X, textY, mPaint);
        }
        if (curPosX >= rectBmp2.centerX()) {
            canvas.drawText(mTxtUpMiddle, text2X, textY, mPaint);
        }
        if (curPosX >= rectBmp3.centerX()) {
            canvas.drawText(mTxtMax, text3X, textY, mPaint);
        }
        if (curPosX >= rectBmp4.centerX()) {
            canvas.drawText(mTxtDownMiddle, text4X, textY, mPaint);
        }
        if (curPosX >= rectBmp5.centerX()) {
            canvas.drawText(mTxtDownMin, text5X, textY, mPaint);
        }

        thumb.setBounds(rectThumb);
        thumb.draw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = (int) event.getX();
                downY = (int) event.getY();
                if (null != mOnSeekBarChangeListener) {
                    mOnSeekBarChangeListener.onStartTrackingTouch(SectionalSeekBar.this);
                }
                responseTouch(downX, downY);
                break;
            case MotionEvent.ACTION_MOVE:
                moveX = (int) event.getX();
                moveY = (int) event.getY();
                responseTouch(moveX, moveY);
                break;
            case MotionEvent.ACTION_UP:
                upX = (int) event.getX();
                upY = (int) event.getY();
                if (null != mOnSeekBarChangeListener) {
                    mOnSeekBarChangeListener.onStopTrackingTouch(SectionalSeekBar.this);
                }
                break;
        }
        return true;
    }
    private void responseTouch(int x, int y){
        if (x < bmp1CenterX) {
            x = bmp1CenterX;
        } else if (x > bmp5CenterX) {
            x = bmp5CenterX;
        }
        int validWidth = bmp5CenterX - bmp1CenterX;
        mValue = 100 * (x - bmp1CenterX) / validWidth;

//        if (isPointOnThumb(moveX, moveY)) {
            if (null != mOnSeekBarChangeListener) {
                mOnSeekBarChangeListener.onProgressChanged(SectionalSeekBar.this, mValue);
            }
//        }

        invalidate();
    }

    //设置监听
    public void setOnSeekBarChangeListener(OnSeekBarChangeListener onSeekBarChangeListener) {
        mOnSeekBarChangeListener = onSeekBarChangeListener;
    }

    //设置进度
    public void setProgress(int progress){
        if (progress > mMaxValue) {
            mValue = mMaxValue;
        } else if (progress < 0) {
            mValue = 0;
        } else {
            mValue = progress;
        }

        invalidate();
    }

    public int getProgress() {
        return mValue;
    }

    private boolean isPointOnThumb(int x, int y) {
        return rectThumb.contains(x, y);
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
