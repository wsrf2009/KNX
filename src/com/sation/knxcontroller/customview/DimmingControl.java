package com.sation.knxcontroller.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.sation.knxcontroller.R;
import com.sation.knxcontroller.util.MathUtils;

import java.text.AttributedCharacterIterator;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangchunfeng on 2017/9/6.
 */

public class DimmingControl extends View {
    private final String TAG = "DimmingControl";

    private int mWidth;
    private int mHeight;
    private Drawable mBar;
    private float mBarHeight;
    private Drawable mTick;
    private float mTickWidth;
    private float mTickHeight;
    private Drawable mBarCentral;
    private float mBarCentralWidth;
    private float mBarCentrolHeight;
    private Drawable mThumb;
    private float mThumbWidth;
    private float mThumbHeight;
    private Drawable mDecrease;
    private float mDecreaseWidth;
    private float mDecreaseHeight;
    private Drawable mIncrease;
    private float mIncreaseWidth;
    private float mIncreaseHeight;
    private float mTextSize;
    private int mTextColor;
    private String mDecPrefix;
    private String mDecSuffix;
    private String mIncPrefix;
    private String mIncSuffix;
    private String mBreak;

    Paint paint = new Paint();
    Rect rectBar = new Rect();
    Rect rectBarCentral = new Rect();
    Rect rectTick0 = new Rect();
    Rect rectTick1 = new Rect();
    Rect rectTick2 = new Rect();
    Rect rectTick3 = new Rect();
    Rect rectTick4 = new Rect();
    Rect rectTick5 = new Rect();
    Rect rectTick6 = new Rect();
    Rect rectTick7 = new Rect();
    Rect rectTick8 = new Rect();
    Rect rectTick9 = new Rect();
    Rect rectTick10 = new Rect();
    Rect rectTick11 = new Rect();
    Rect rectTick12 = new Rect();
    Rect rectTick13 = new Rect();
    Rect rectTick14 = new Rect();
    Rect rectTick15 = new Rect();
    List<Rect> listRectTick = new ArrayList<Rect>();
    Rect rectThumb = new Rect();
    int[] ticks = new int[16];
    Rect rectDec = new Rect();
    Rect rectInc = new Rect();
    String text;
    float textX;
    float textY;
    private int index = 8;
    private OnSeekBarChangeListener mOnSeekBarChangeListener = null;

    public interface OnSeekBarChangeListener {

        void onProgressChanged(DimmingControl seekBar, int index);

        void onStartTrackingTouch(DimmingControl seekBar);

        void onStopTrackingTouch(DimmingControl seekBar);
    }

    public DimmingControl(Context context) {
        this(context, null);
    }

    public DimmingControl(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public DimmingControl(Context context, AttributeSet attributeSet, int defStyleAttr) {
        super(context, attributeSet, defStyleAttr);

        listRectTick.add(rectTick0);
        listRectTick.add(rectTick1);
        listRectTick.add(rectTick2);
        listRectTick.add(rectTick3);
        listRectTick.add(rectTick4);
        listRectTick.add(rectTick5);
        listRectTick.add(rectTick6);
        listRectTick.add(rectTick7);
        listRectTick.add(rectTick8);
        listRectTick.add(rectTick9);
        listRectTick.add(rectTick10);
        listRectTick.add(rectTick11);
        listRectTick.add(rectTick12);
        listRectTick.add(rectTick13);
        listRectTick.add(rectTick14);
        listRectTick.add(rectTick15);

        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.DimmingControl);
        mBar = typedArray.getDrawable(R.styleable.DimmingControl_bar);
        mBar = (null != mBar) ? mBar : context.getResources().getDrawable(R.drawable.listview_item_horizontal_divider);
        mBarHeight = typedArray.getDimension(R.styleable.DimmingControl_barHeight, 4);
        mTick = typedArray.getDrawable(R.styleable.DimmingControl_tick);
        mTick = (null != mTick) ? mTick : context.getResources().getDrawable(R.drawable.dimmingcontrol_tick);
        mTickWidth = typedArray.getDimension(R.styleable.DimmingControl_tickWidth, 1);
        mTickHeight = typedArray.getDimension(R.styleable.DimmingControl_tickHeight, 30);
        mBarCentral = typedArray.getDrawable(R.styleable.DimmingControl_barCentral);
        mBarCentral = (null != mBarCentral) ? mBarCentral : context.getResources().getDrawable(R.drawable.dimmingcontrol_central);
        mBarCentralWidth = typedArray.getDimension(R.styleable.DimmingControl_barCentralWidth, 1);
        mBarCentrolHeight = typedArray.getDimension(R.styleable.DimmingControl_barCentralHeight, 16);
        mThumb = typedArray.getDrawable(R.styleable.DimmingControl_android_thumb);
        mThumb = (null != mThumb) ? mThumb : context.getResources().getDrawable(R.drawable.dimmingcontrol_thumb);
        mThumbWidth = typedArray.getDimension(R.styleable.DimmingControl_thumbWidth, 12);
        mThumbHeight = typedArray.getDimension(R.styleable.DimmingControl_thumbHeight, 32);
        mDecrease = typedArray.getDrawable(R.styleable.DimmingControl_decrease);
        mDecrease = (null != mDecrease) ? mDecrease : context.getResources().getDrawable(R.drawable.dimmer_min);
        mDecreaseWidth = typedArray.getDimension(R.styleable.DimmingControl_decreaseWidth, 16);
        mDecreaseHeight = typedArray.getDimension(R.styleable.DimmingControl_decreaseHeight, 16);
        mIncrease = typedArray.getDrawable(R.styleable.DimmingControl_increase);
        mIncrease = (null != mIncrease) ? mIncrease : context.getResources().getDrawable(R.drawable.dimmer_max);
        mIncreaseWidth = typedArray.getDimension(R.styleable.DimmingControl_increaseWidth, 16);
        mIncreaseHeight = typedArray.getDimension(R.styleable.DimmingControl_increaseHeight, 16);
        mTextSize = typedArray.getDimension(R.styleable.DimmingControl_android_textSize, 20);
        mTextColor = typedArray.getColor(R.styleable.DimmingControl_android_textColor, Color.WHITE);
        mDecPrefix = typedArray.getString(R.styleable.DimmingControl_decreasePrefix);
        mDecPrefix = (null != mDecPrefix) ? mDecPrefix : "Dec";
        mDecSuffix = typedArray.getString(R.styleable.DimmingControl_decreaseSuffix);
        mDecSuffix = (null != mDecSuffix) ? mDecSuffix : "";
        mIncPrefix = typedArray.getString(R.styleable.DimmingControl_increasePrefix);
        mIncPrefix = (null != mIncPrefix) ? mIncPrefix : "Inc";
        mIncSuffix = typedArray.getString(R.styleable.DimmingControl_increaseSuffix);
        mIncSuffix = (null != mIncSuffix) ? mIncSuffix : "";
        mBreak = typedArray.getString(R.styleable.DimmingControl_dimmingBreak);
        mBreak = (null != mBreak) ? mBreak : "Break";
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mWidth = measureWidth(widthMeasureSpec);
        mHeight = measureHeight(heightMeasureSpec);
        setMeasuredDimension(mWidth, mHeight);

        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        mWidth -= (paddingLeft + paddingRight);
        mHeight -= (paddingTop + paddingBottom);

        float barLeft = paddingLeft;
        float barTop = paddingTop + mHeight * 3 / 4;
        rectBar.set((int)barLeft, (int)barTop, (int)(barLeft+mWidth), (int)(barTop + mBarHeight));

        float barCentralLeft = rectBar.centerX()-mBarCentralWidth/2;
        float barCentralTop = rectBar.centerY()-mBarCentrolHeight/2;
        rectBarCentral.set((int)barCentralLeft, (int)barCentralTop,
                (int)(barCentralLeft+mBarCentralWidth), (int)(barCentralTop+mBarCentrolHeight));

        float perWidth = (mWidth-mTickWidth) / 15 ;

        float tick0Left = barLeft;
        float tick0Top = rectBar.centerY() - mTickHeight / 2;
        rectTick0.set((int)tick0Left, (int)tick0Top,
                (int)(tick0Left+mTickWidth), (int)(tick0Top + mTickHeight));
        float tick0Center = tick0Left + mTickWidth / 2;
        ticks[0] = rectTick0.centerX();


        float tick1Center = tick0Center+perWidth;
        float tick1Left = tick1Center - mTickWidth / 2;
        float tick1Top = tick0Top;
        rectTick1.set((int)tick1Left, (int)tick1Top,
                (int)(tick1Left+mTickWidth), (int)(tick1Top+mTickHeight));
        ticks[1] = rectTick1.centerX();

        float tick2Center = tick1Center+perWidth;
        float tick2Left = tick2Center - mTickWidth / 2;
        float tick2Top = tick1Top;
        rectTick2.set((int)tick2Left, (int)tick2Top,
                (int)(tick2Left+mTickWidth), (int)(tick2Top+mTickHeight));
        ticks[2] = rectTick2.centerX();

        float tick3Center = tick2Center+perWidth;
        float tick3Left = tick3Center - mTickWidth / 2;
        float tick3Top = tick2Top;
        rectTick3.set((int)tick3Left, (int)tick3Top,
                (int)(tick3Left+mTickWidth), (int)(tick3Top+mTickHeight));
        ticks[3] = rectTick3.centerX();

        float tick4Center = tick3Center+perWidth;
        float tick4Left = tick4Center - mTickWidth / 2;
        float tick4Top = tick3Top;
        rectTick4.set((int)tick4Left, (int)tick4Top,
                (int)(tick4Left+mTickWidth), (int)(tick4Top+mTickHeight));
        ticks[4] = rectTick4.centerX();

        float tick5Center = tick4Center+perWidth;
        float tick5Left = tick5Center - mTickWidth / 2;
        float tick5Top = tick4Top;
        rectTick5.set((int)tick5Left, (int)tick5Top,
                (int)(tick5Left+mTickWidth), (int)(tick5Top+mTickHeight));
        ticks[5] = rectTick5.centerX();

        float tick6Center = tick5Center+perWidth;
        float tick6Left = tick6Center - mTickWidth / 2;
        float tick6Top = tick5Top;
        rectTick6.set((int)tick6Left, (int)tick6Top,
                (int)(tick6Left+mTickWidth), (int)(tick6Top+mTickHeight));
        ticks[6] = rectTick6.centerX();

        float tick7Center = tick6Center+perWidth;
        float tick7Left = tick7Center - mTickWidth / 2;
        float tick7Top = tick6Top;
        rectTick7.set((int)tick7Left, (int)tick7Top,
                (int)(tick7Left+mTickWidth), (int)(tick7Top+mTickHeight));
        ticks[7] = rectTick7.centerX();

        float tick8Center = tick7Center+perWidth;
        float tick8Left = tick8Center - mTickWidth / 2;
        float tick8Top = tick7Top;
        rectTick8.set((int)tick8Left, (int)tick8Top,
                (int)(tick8Left+mTickWidth), (int)(tick8Top+mTickHeight));
        ticks[8] = rectTick8.centerX();

        float tick9Center = tick8Center+perWidth;
        float tick9Left = tick9Center - mTickWidth / 2;
        float tick9Top = tick8Top;
        rectTick9.set((int)tick9Left, (int)tick9Top,
                (int)(tick9Left+mTickWidth), (int)(tick9Top+mTickHeight));
        ticks[9] = rectTick9.centerX();

        float tick10Center = tick9Center+perWidth;
        float tick10Left = tick10Center - mTickWidth / 2;
        float tick10Top = tick9Top;
        rectTick10.set((int)tick10Left, (int)tick10Top,
                (int)(tick10Left+mTickWidth), (int)(tick10Top+mTickHeight));
        ticks[10] = rectTick10.centerX();

        float tick11Center = tick10Center+perWidth;
        float tick11Left = tick11Center - mTickWidth / 2;
        float tick11Top = tick10Top;
        rectTick11.set((int)tick11Left, (int)tick11Top,
                (int)(tick11Left+mTickWidth), (int)(tick11Top+mTickHeight));
        ticks[11] = rectTick11.centerX();

        float tick12Center = tick11Center+perWidth;
        float tick12Left = tick12Center - mTickWidth / 2;
        float tick12Top = tick11Top;
        rectTick12.set((int)tick12Left, (int)tick12Top,
                (int)(tick12Left+mTickWidth), (int)(tick12Top+mTickHeight));
        ticks[12] = rectTick12.centerX();

        float tick13Center = tick12Center+perWidth;
        float tick13Left = tick13Center - mTickWidth / 2;
        float tick13Top = tick12Top;
        rectTick13.set((int)tick13Left, (int)tick13Top,
                (int)(tick13Left+mTickWidth), (int)(tick13Top+mTickHeight));
        ticks[13] = rectTick13.centerX();

        float tick14Center = tick13Center+perWidth;
        float tick14Left = tick14Center - mTickWidth / 2;
        float tick14Top = tick13Top;
        rectTick14.set((int)tick14Left, (int)tick14Top,
                (int)(tick14Left+mTickWidth), (int)(tick14Top+mTickHeight));
        ticks[14] = rectTick14.centerX();

        float tick15Center = tick14Center+perWidth;
        float tick15Left = tick15Center - mTickWidth / 2;
        float tick15Top = tick14Top;
        rectTick15.set((int)tick15Left, (int)tick15Top,
                (int)(tick15Left+mTickWidth), (int)(tick15Top+mTickHeight));
        ticks[15] = rectTick15.centerX();

        textY = (float)mHeight / 4;

        float decLeft = barLeft;
        float decTop =  paddingTop + textY - mDecreaseHeight/2;
        rectDec.set((int)decLeft, (int)decTop,
                (int)(decLeft+mDecreaseWidth), (int)(decTop+mDecreaseHeight));

        float incLeft = rectBar.right - mIncreaseWidth;
        float incTop = decTop;
        rectInc.set((int)incLeft, (int)incTop,
                (int)(incLeft + mIncreaseWidth), (int)(incTop+mIncreaseHeight));

        paint.setTextSize(mTextSize);
        paint.setColor(mTextColor);
        setTextPosition(index);

        setThumbPosition((int)tick8Center, rectBar.centerY());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (null != mBar) {
            mBar.setBounds(rectBar);
            mBar.draw(canvas);
        }

        if (null != mBarCentral) {
            mBarCentral.setBounds(rectBarCentral);
            mBarCentral.draw(canvas);
        }

        if (null != mDecrease) {
            mDecrease.setBounds(rectDec);
            mDecrease.draw(canvas);
        }

        if (null != mIncrease) {
            mIncrease.setBounds(rectInc);
            mIncrease.draw(canvas);
        }

        if (null != mTick) {
            for (Rect rect:listRectTick) {
                mTick.setBounds(rect);
                mTick.draw(canvas);
            }
        }

        canvas.drawText(text, textX, textY, paint);

        if (null != mThumb) {
            mThumb.setBounds(rectThumb);
            mThumb.draw(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        int x = (int)event.getX();
        int y = (int)event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (null != mOnSeekBarChangeListener) {
                    mOnSeekBarChangeListener.onStartTrackingTouch(DimmingControl.this);
                }
                responseTouch(x, y);
                break;

            case MotionEvent.ACTION_MOVE:
                responseTouch(x, y);
                break;

            case MotionEvent.ACTION_UP:
                if (null != mOnSeekBarChangeListener) {
                    mOnSeekBarChangeListener.onStopTrackingTouch(DimmingControl.this);
                }
                break;
        }

        return true;
    }

    public void setOnSeekBarChangeListener(OnSeekBarChangeListener onSeekBarChangeListener) {
        mOnSeekBarChangeListener = onSeekBarChangeListener;
    }

    public int getDimmingValue() {
        int val = 0;
        if (index < 8) {
            val = index;
        } else if (index <= 15) {
            val = 8 +15-index;
        }

        return val;
    }

    public int getDimmingIndex() {
        return index;
    }

    public String getDimmingText() {
        return text;
    }

    private void setThumbPosition(int centerX, int centerY) {
        float thumbLeft = centerX - mThumbWidth / 2;
        float thumbTop = centerY - mThumbHeight / 2;
        rectThumb.set((int)thumbLeft, (int)thumbTop,
                (int)(thumbLeft+mThumbWidth), (int)(thumbTop+mThumbHeight));
    }

    private void responseTouch(int x, int y) {
        index = MathUtils.getTheClosetIndex(x, ticks);
        setThumbPosition(ticks[index], rectBar.centerY());
        setTextPosition(index);

        if (null != mOnSeekBarChangeListener) {
            mOnSeekBarChangeListener.onProgressChanged(DimmingControl.this, index);
        }

        invalidate();
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

    private void setTextPosition(int index) {
        text = getTickText(index);
        float width = paint.measureText(text);
        textX = (float)rectBar.centerX() - width / 2;
    }

    private String getTickText(int index) {
        String text = "";
        String decPrefix = (null != mDecPrefix) ? mDecPrefix : "";
        String decSuffix = (null != mDecSuffix) ? mDecSuffix : "";
        String incPrefix = (null != mIncPrefix) ? mIncPrefix : "";
        String incSuffix = (null != mIncSuffix) ? mIncSuffix : "";
        String prefix;
        String suffix;

        if (index < 8) {
            prefix = decPrefix;
            suffix = decSuffix;
        } else {
            prefix = incPrefix;
            suffix = incSuffix;
        }

        switch (index) {
            case 0:
            case 15:
                text = (null != mBreak) ? mBreak : "break";
                break;

            case 1:
            case 14:
                text = "100%";
                break;

            case 2:
            case 13:
                text = "50%";
                break;

            case 3:
            case 12:
                text = "25%";
                break;

            case 4:
            case 11:
                text = "12%";
                break;

            case 5:
            case 10:
                text = "6%";
                break;

            case 6:
            case 9:
                text = "3%";
                break;

            case 7:
            case 8:
                text = "1%";
                break;
        }

        return prefix + " " + text + " " + suffix;
    }
}
