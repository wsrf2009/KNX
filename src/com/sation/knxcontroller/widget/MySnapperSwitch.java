package com.sation.knxcontroller.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout.LayoutParams;

import com.sation.knxcontroller.STKNXControllerConstant;
import com.sation.knxcontroller.control.ControlSymbol;
import com.sation.knxcontroller.control.KNXSnapperSwitch;
import com.sation.knxcontroller.R;

public class MySnapperSwitch extends ControlView {
	
	private Context context;
	private KNXSnapperSwitch mKNXSnapperSwitch;
	
	private static final int TOUCH_MODE_IDLE = 0;
	private static final int TOUCH_MODE_DOWN = 1;
	private static final int TOUCH_MODE_DRAGGING = 2;
	private static final String TAG = "SliderSwitch";

	//定义字体类型.
	private static final int SANS = 1;
	private static final int SERIF = 2;
	private static final int MONOSPACE = 3;

	private static final int VERTICAL = 0;
	private static final int HORIZONTAL = 1;

	private int mOrientation = HORIZONTAL;
	private OnChangeAttemptListener mOnChangeAttemptListener; 
	 
	private Drawable mThumbDrawable; 
	private int mThumbTextPadding; 
	private int mSwitchMinWidth;
	private int mSwitchMinHeight;
	private int mSwitchPadding; 
	private CharSequence mSliderSwitchButtonText; 
	private Drawable mDrawableLeft;
	private Drawable mDrawableRight;
	private boolean fixed = false;
	private boolean clickDisabled = false;  

	private int mTouchMode;
	private int mTouchSlop;
	private float mTouchX;
	private float mTouchY;
	private VelocityTracker mVelocityTracker = VelocityTracker.obtain();
	private int mMinFlingVelocity;

	private float mThumbPosition = 0;
	//控件的宽度
	private int mSliderSwitchWidth;
	//控件的高度
	private int mSliderSwitchHeight;
	//滑动条的宽度
	private int mThumbWidth;
	//滑动条的高度
	private int mThumbHeight;

	private int mSwitchLeft;
	private int mSwitchTop;
	private int mSwitchRight;
	private int mSwitchBottom;
    //文字画布
	private TextPaint mTextPaint;
	private ColorStateList mTextColors; 
	private Layout mButtonTextLayout;

	private Paint xferPaint;   
	private final Rect mThPad = new Rect(); 
	
	private int MinValue;
	private int MaxValue;
	
	private OnSliderChangeListener mOnSliderChangeListener;
	
	
	public MySnapperSwitch(Context context) {
		super(context);
	}
	
	public MySnapperSwitch(Context context, AttributeSet attrs) {
		this(context, attrs, R.attr.sliderSwitchStyleAttr);
	}

	public MySnapperSwitch(Context context, KNXSnapperSwitch mKNXSnapperSwitch) {
		this(context, null, R.attr.styleMySilder);
		this.context= context;
		setKNXControl(mKNXSnapperSwitch);
		if (mKNXSnapperSwitch != null) {
			this.mKNXSnapperSwitch = mKNXSnapperSwitch;
			initControl(mKNXSnapperSwitch);
		}
	}
	
	public MySnapperSwitch(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.setBackgroundResource(R.drawable.button_background);
		
		mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
		Resources res = getResources();
		mTextPaint.density = res.getDisplayMetrics().density;
		mTextPaint.setShadowLayer(0.5f, 1.0f, 1.0f, Color.BLACK);
		mTextPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 20,  context.getResources().getDisplayMetrics())); 

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MySnapperSwitch, defStyle, 0);
 
		mOrientation = a.getInteger(R.styleable.MySliderSwitch_direct, HORIZONTAL); //目前只支持水平
		mThumbDrawable = a.getDrawable(R.styleable.MySliderSwitch_thumb);  //滑动条Drawable
		if(mThumbDrawable == null) {
			//mThumbDrawable = context.getResources().getDrawable(R.drawable.btn_toggle_bg);
			mThumbDrawable = context.getResources().getDrawable(R.drawable.slider_thum_bg); 
		}
		mSliderSwitchButtonText = a.getText(R.styleable.MySnapperSwitch_sliderSwitchButtonText);  //SliderSwitch名称
		if(mSliderSwitchButtonText == null) {
			mSliderSwitchButtonText = "测试";
		}
		mDrawableLeft = a.getDrawable(R.styleable.MySnapperSwitch_drawableLeft);  //SliderSwitch左边背景
		if(mDrawableLeft == null) {
			mDrawableLeft = context.getResources().getDrawable(R.drawable.shixun_silder_swich_light_down);
		}
		mDrawableRight = a.getDrawable(R.styleable.MySliderSwitch_drawableRight); //SliderSwitch右边背景
		if(mDrawableRight == null) {
			mDrawableRight = context.getResources().getDrawable(R.drawable.shixun_silder_swich_light_down);
		}
		mThumbTextPadding = a.getDimensionPixelSize(R.styleable.MySliderSwitch_thumbTextPadding, 0);
		 
		mSwitchMinWidth = a.getDimensionPixelSize(R.styleable.MySliderSwitch_switchMinWidth, 0);
		mSwitchMinHeight = a.getDimensionPixelSize(R.styleable.MySliderSwitch_switchMinHeight, 0);
		mSwitchPadding = a.getDimensionPixelSize(R.styleable.MySliderSwitch_switchPadding, 0);
 
		mThumbDrawable.getPadding(mThPad);
		Log.d(TAG, "mThPad=" + mThPad); 
	  
		xferPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		xferPaint.setColor(Color.TRANSPARENT);
		xferPaint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));

		int appearance = a.getResourceId(R.styleable.MySliderSwitch_switchTextAppearanceAttrib, 0);
		if (appearance != 0) {
			setSwitchTextAppearance(context, appearance);
		}
		a.recycle(); 
		
		ViewConfiguration config = ViewConfiguration.get(context);
		mTouchSlop = config.getScaledTouchSlop();  //用法 是一个距离，表示滑动的时候，手的移动要大于这个距离才开始移动控件。 
		mMinFlingVelocity = config.getScaledMinimumFlingVelocity();
		// Log.d(TAG, "mMinFlingVelocity="+mMinFlingVelocity); 
		// Refresh display with current params
		refreshDrawableState(); 
		this.setClickable(true);  
	}

	private void initControl(final KNXSnapperSwitch mKNXSnapperSwitch) {
		if(mKNXSnapperSwitch != null) {
			this.setId(mKNXSnapperSwitch.getId());
			setSliderSwitchButtonText(mKNXSnapperSwitch.getText());
			
			MinValue = mKNXSnapperSwitch.getMinValue();
			MaxValue = mKNXSnapperSwitch.getMaxValue();
			
			setProgress(100);
			//增加控件
			//addView(this);
			//设置控件大小
			setControlDefaultSize(this); 
			
			invalidate();
		}
	}
	
	public void setSwitchTextAppearance(Context context, int resid) {
		TypedArray appearance = context.obtainStyledAttributes(resid, R.styleable.sliderSwitchTextAppearanceAttrib);

		ColorStateList colors;
		int ts;

		colors = appearance.getColorStateList(R.styleable.sliderSwitchTextAppearanceAttrib_textColor);
		if (colors != null) {
			mTextColors = colors;
		} 

		ts = appearance.getDimensionPixelSize(R.styleable.sliderSwitchTextAppearanceAttrib_textSize, 0);
		if (ts != 0) {
			if (ts != mTextPaint.getTextSize()) {
				mTextPaint.setTextSize(ts);
				requestLayout();
			}
		}

		int typefaceIndex, styleIndex; 
		typefaceIndex = appearance.getInt(R.styleable.sliderSwitchTextAppearanceAttrib_typeface, -1);
		styleIndex = appearance.getInt(R.styleable.sliderSwitchTextAppearanceAttrib_textStyle, -1);

		setSwitchTypefaceByIndex(typefaceIndex, styleIndex); 
		appearance.recycle();
	}

	private void setSwitchTypefaceByIndex(int typefaceIndex, int styleIndex) {
		Typeface tf = null;
		switch (typefaceIndex) {
		case SANS:
			tf = Typeface.SANS_SERIF;
			break;

		case SERIF:
			tf = Typeface.SERIF;
			break;

		case MONOSPACE:
			tf = Typeface.MONOSPACE;
			break;
		}

		setSwitchTypeface(tf, styleIndex);
	}

	/**
	 * Sets the typeface and style in which the text should be displayed on the
	 * switch, and turns on the fake bold and italic bits in the Paint if the
	 * Typeface that you provided does not have all the bits in the style that
	 * you specified.
	 */
	public void setSwitchTypeface(Typeface tf, int style) {
		if (style > 0) {
			if (tf == null) {
				tf = Typeface.defaultFromStyle(style);
			} else {
				tf = Typeface.create(tf, style);
			}

			setSwitchTypeface(tf);
			// now compute what (if any) algorithmic styling is needed
			int typefaceStyle = tf != null ? tf.getStyle() : 0;
			int need = style & ~typefaceStyle;
			mTextPaint.setFakeBoldText((need & Typeface.BOLD) != 0);
			mTextPaint.setTextSkewX((need & Typeface.ITALIC) != 0 ? -0.25f : 0);
		} else {
			mTextPaint.setFakeBoldText(false);
			mTextPaint.setTextSkewX(0);
			setSwitchTypeface(tf);
		}
	}

	/**
	 * Sets the typeface in which the text should be displayed on the switch.
	 * Note that not all Typeface families actually have bold and italic
	 * variants, so you may need to use
	 * {@link #setSwitchTypeface(Typeface, int)} to get the appearance that you
	 * actually want.
	 * 
	 * @attr ref android.R.styleable#TextView_typeface
	 * @attr ref android.R.styleable#TextView_textStyle
	 */
	public void setSwitchTypeface(Typeface tf) {
		if (mTextPaint.getTypeface() != tf) {
			mTextPaint.setTypeface(tf);

			requestLayout();
			invalidate();
		}
	}
	 
	public CharSequence getSliderSwitchButtonText() {
		return mSliderSwitchButtonText;
	} 
	public void setSliderSwitchButtonText(CharSequence sliderSwitchButtonText) {
		mSliderSwitchButtonText = sliderSwitchButtonText;
		this.mButtonTextLayout = null;
		requestLayout();
	} 

	/**
	 * Interface definition for a callback to be invoked when the switch is in a
	 * fixed state and there was an attempt to change its state either via a
	 * click or drag
	 */
	public static interface OnChangeAttemptListener {
		/**
		 * Called when an attempt was made to change the checked state of the
		 * switch while the switch was in a fixed state.
		 * 
		 * @param isChecked
		 *            The current state of switch.
		 */
		void onChangeAttempted(boolean isChecked);
	}

	/**
	 * Register a callback to be invoked when there is an attempt to change the
	 * state of the switch when its in fixated
	 * 
	 * @param listener
	 *            the callback to call on checked state change
	 */
	public void setOnChangeAttemptListener(OnChangeAttemptListener listener) {
		mOnChangeAttemptListener = listener;
	} 

	private Layout makeLayout(CharSequence text) {
		return new StaticLayout(text, mTextPaint, (int) Math.ceil(Layout.getDesiredWidth(text, mTextPaint)), Layout.Alignment.ALIGN_NORMAL, 1.f, 0, true);
	}
 
 
	@Override
	public boolean onTouchEvent(MotionEvent ev) { 
		mVelocityTracker.addMovement(ev);
		// Log.d(TAG, "onTouchEvent(ev="+ev.toString()+")");
		// Log.d(TAG, "mTouchMode="+mTouchMode);
		final int action = ev.getActionMasked();
		switch (action) {
		case MotionEvent.ACTION_DOWN: {
			final float x = ev.getX();
			final float y = ev.getY();
			//if (isEnabled() && hitThumb(x, y)) {
			if (isEnabled()) {  
				mTouchMode = TOUCH_MODE_DOWN;
				mTouchX = x;
				mTouchY = y;
			}
			break;
		}

		case MotionEvent.ACTION_MOVE: {
			switch (mTouchMode) {
			case TOUCH_MODE_IDLE:
				// Didn't target the thumb, treat normally.
				break;

			case TOUCH_MODE_DOWN: {
				final float x = ev.getX();
				final float y = ev.getY();
				if (Math.abs(x - mTouchX) > mTouchSlop / 2 || Math.abs(y - mTouchY) > mTouchSlop / 2) {
					mTouchMode = TOUCH_MODE_DRAGGING;
					if (getParent() != null) {
						getParent().requestDisallowInterceptTouchEvent(true);
					}
					mTouchX = x;
					mTouchY = y;
					return true;
				}
				break;
			}

			case TOUCH_MODE_DRAGGING: {
				final float x = ev.getX();
				final float dx = x - mTouchX;
				final float y = ev.getY();
				final float dy = y - mTouchY;
				if (mOrientation == HORIZONTAL) {
					float newPos = Math.max(0, Math.min(mThumbPosition + dx, getThumbScrollRange()));
					if (newPos != mThumbPosition) {
						mThumbPosition = newPos;
						mTouchX = x;
						invalidate();
						
						if (mOnSliderChangeListener != null) {  
							mOnSliderChangeListener.onProgressChanged(this, (int) (mThumbPosition), true);
						}
						//发送数据
						sendCommandRequest(mKNXSnapperSwitch.getWriteAddressIds(), String.valueOf((int)(mThumbPosition)), false, null); 
					}
					return true;
				} 
			}
			}
			break;
		}

		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL: {
			if (mTouchMode == TOUCH_MODE_DRAGGING) {
				 
				return true;
			}
			mTouchMode = TOUCH_MODE_IDLE;
			mVelocityTracker.clear(); 
			break;
		}
		}

		Log.d(TAG, "mThumbPosition=" + mThumbPosition);
		boolean flag = super.onTouchEvent(ev);
		// Log.d(TAG, "super.onTouchEvent(ev) returned="+flag);
		return flag;
	}  

	 
	public void disableClick() {
		clickDisabled = true;
	}

	public void enableClick() {
		clickDisabled = false;
	}   

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		final int heightMode = MeasureSpec.getMode(heightMeasureSpec);

		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
 
		if (mButtonTextLayout == null) {
			mButtonTextLayout = makeLayout(mSliderSwitchButtonText);
		}
		//计算滑竿的大小
		final int textWidth = mButtonTextLayout.getWidth();
		final int textHeight = mButtonTextLayout.getHeight(); 
		mThumbWidth = mThumbDrawable.getIntrinsicWidth(); 
		mThumbHeight = mThumbDrawable.getIntrinsicHeight();
		 
		Log.d(TAG, "mThumbWidth=" + mThumbWidth);
		Log.d(TAG, "mThumbHeight=" + mThumbHeight);
		
		//计算控件的宽度
		int switchWidth;
		if (mOrientation == HORIZONTAL) {  //水平
			switchWidth = Math.max(mSwitchMinWidth, textWidth * 2 + mThumbTextPadding * 2); 
		} else {
			switchWidth = Math.max(textWidth + mThumbTextPadding * 2 + mThPad.left + mThPad.right, mThumbWidth);
		}
		switchWidth = Math.max(mSwitchMinWidth, switchWidth);
 
		int switchHeight = Math.max(mSwitchMinHeight, textHeight); 
		switchHeight = Math.max(switchHeight, mThumbHeight);

		if (mOrientation == VERTICAL) {
			switchHeight = mButtonTextLayout.getHeight() + mThPad.top + mThPad.bottom; 
		}

		switch (widthMode) {
		case MeasureSpec.AT_MOST:
			widthSize = Math.min(widthSize, switchWidth);
			break;

		case MeasureSpec.UNSPECIFIED:
			widthSize = switchWidth;
			break;

		case MeasureSpec.EXACTLY:
			// Just use what we were given
			widthSize = MeasureSpec.getSize(widthMeasureSpec);
			break;
		}

		switch (heightMode) {
		case MeasureSpec.AT_MOST:
			heightSize = Math.min(heightSize, switchHeight);
			break;

		case MeasureSpec.UNSPECIFIED:
			heightSize = switchHeight;
			break;

		case MeasureSpec.EXACTLY:
			// Just use what we were given
			heightSize = MeasureSpec.getSize(heightMeasureSpec);
			break;
		}

		//mSliderSwitchWidth = switchWidth + getPaddingLeft() + getPaddingRight();
		//mSliderSwitchHeight = switchHeight  + getPaddingTop() + getPaddingBottom();
		mSliderSwitchWidth = switchWidth;
		mSliderSwitchHeight = switchHeight;
 
		if (getLayoutParams().width == LayoutParams.MATCH_PARENT  || getLayoutParams().width == LayoutParams.FILL_PARENT) {
			mSliderSwitchWidth = MeasureSpec.getSize(widthMeasureSpec);
		}

		Log.d(TAG, "onMeasure():mSwitchWidth=" + mSliderSwitchWidth + " mSwitchHeight=" + mSliderSwitchHeight);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		final int measuredHeight = getMeasuredHeight();
		final int measuredWidth = getMeasuredWidth();
		if (measuredHeight < switchHeight) {
			setMeasuredDimension(getMeasuredWidth(), switchHeight | MeasureSpec.EXACTLY);
		}
		if (measuredWidth < switchWidth) {
			setMeasuredDimension(switchWidth | MeasureSpec.EXACTLY, getMeasuredHeight());
		} 
		
		mSliderSwitchWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, STKNXControllerConstant.CONTROL_DEFAULT_WIDTH, getResources().getDisplayMetrics()); 
		
		mSliderSwitchWidth = widthSize;
		mSliderSwitchHeight = heightSize;
		setMeasuredDimension(mSliderSwitchWidth, mSliderSwitchHeight);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		Log.d(TAG, "onLayout()-left=" + left + ",top=" + top + ",right=" + right + ",bottom=" + bottom);
		super.onLayout(changed, left, top, right, bottom);

		int switchTop = 0;
		int switchBottom = 0;
		switch (Gravity.VERTICAL_GRAVITY_MASK) {
		default:
		case Gravity.TOP:
			switchTop = getPaddingTop();
			switchBottom = switchTop + mSliderSwitchHeight;
			break;

		case Gravity.CENTER_VERTICAL:
			switchTop = (getPaddingTop() + getHeight() - getPaddingBottom()) / 2 - mSliderSwitchHeight / 2;
			switchBottom = switchTop + mSliderSwitchHeight;
			break;

		case Gravity.BOTTOM:
			switchBottom = getHeight() - getPaddingBottom();
			switchTop = switchBottom - mSliderSwitchHeight;
			break;
		}
		//mSwitchTop = mSwitchHeight - mSwitchBottom;
		mSwitchTop = 0;
		mSwitchBottom = mSliderSwitchHeight; 
		mSwitchLeft = mSliderSwitchWidth - mSwitchRight; 
		mSwitchRight = mSliderSwitchWidth; 

		Log.d(TAG, "getWidth()=" + getWidth() + " getHeight()=" + getHeight());
		Log.d(TAG, "getPaddingLeft()=" + getPaddingLeft() + " getPaddingRight()=" + getPaddingRight());
		Log.d(TAG, "getPaddingTop()=" + getPaddingTop() + " getPaddingBottom()=" + getPaddingBottom());

		Log.d(TAG, "mSwitchWidth=" + mSliderSwitchWidth + " mSwitchHeight=" + mSliderSwitchHeight);
		Log.d(TAG, "mSwitchLeft=" + mSwitchLeft + " mSwitchRight=" + mSwitchRight);
		Log.d(TAG, "mSwitchTop=" + mSwitchTop + " mSwitchBottom=" + mSwitchBottom); 
	 
		
		// now that the layout is known, prepare the drawables 
		if (mDrawableLeft != null) {
			mDrawableLeft.setBounds(0, 0, mDrawableLeft.getIntrinsicWidth(), mDrawableLeft.getIntrinsicHeight());
		}
		if (mDrawableRight != null) {
			mDrawableRight.setBounds(0, 0, mDrawableRight.getIntrinsicWidth(), mDrawableRight.getIntrinsicHeight()); 
		} 
	}

	private int mProgress;
	public synchronized int getProgress() {
        return mProgress;
    } 
 
	 public synchronized void setProgress(int progress) {
        setProgress(progress, false); 
    	mThumbPosition = progress; 
		invalidate();
    }
     
    synchronized void setProgress(int progress, boolean fromUser) { 
        if (progress < 0) {
            progress = 0;
        } 
        if (progress != mProgress) {
            mProgress = progress; 
        }
    }
	
	
	// Draw the switch
	@Override
	protected void onDraw(Canvas canvas) { 
		// super.onDraw(canvas); 
		int thumbRange = getThumbScrollRange();

		int thumbPos = (int) (mThumbPosition); 
		
		mProgress = (int)(mThumbPosition * 100 / thumbRange);
		Log.d(TAG, "当前进度条的值为： "+mProgress);
		
		if(mKNXSnapperSwitch.getSliderSymbol() == ControlSymbol.DardBright.getControlSymbol()) {
			if(mProgress > 0) {
				mDrawableLeft = context.getResources().getDrawable(R.drawable.shixun_silder_swich_light_up);
				mDrawableRight = context.getResources().getDrawable(R.drawable.shixun_silder_swich_light_up);
			} else {
				mDrawableLeft = context.getResources().getDrawable(R.drawable.shixun_silder_swich_light_down);
				mDrawableRight = context.getResources().getDrawable(R.drawable.shixun_silder_swich_light_down);
			}
		} else if(mKNXSnapperSwitch.getSliderSymbol() ==  ControlSymbol.DownUp.getControlSymbol()) {
			
			mDrawableLeft = context.getResources().getDrawable(R.drawable.shixun_blinds_symbol_sign_down); 
			mDrawableRight = context.getResources().getDrawable(R.drawable.shixun_blinds_symbol_sign_up);
		} 
		
		// now that the layout is known, prepare the drawables 
		if (mDrawableLeft != null) {
			mDrawableLeft.setBounds(0, 0, mDrawableLeft.getIntrinsicWidth(), mDrawableLeft.getIntrinsicHeight());
		}
		if (mDrawableRight != null) {
			mDrawableRight.setBounds(0, 0, mDrawableRight.getIntrinsicWidth(), mDrawableRight.getIntrinsicHeight()); 
		} 
		 
		mTextPaint.drawableState = getDrawableState();
		// Log.d(TAG, "switchInnerLeft="+switchInnerLeft+" switchInnerRight="+switchInnerRight);
		// Log.d(TAG, "switchInnerTop="+switchInnerTop+" switchInnerBottom="+switchInnerBottom);
		// Log.d(TAG, "thumbRange="+thumbRange+" thumbPos="+thumbPos); 
		if (mOrientation == HORIZONTAL) { // 水平  
			int thumbBoundL = mSwitchLeft + thumbPos;
			int thumbBoundR = mSwitchLeft + thumbPos + mThumbWidth;
 
			// 绘制滑动按钮 
			mThumbDrawable.setBounds(thumbBoundL, mSwitchTop, thumbBoundR, mSwitchBottom);
			mThumbDrawable.setAlpha(255);
			mThumbDrawable.draw(canvas);
			
			//设置文字颜色
			if (mTextColors != null) {
				mTextPaint.setColor(mTextColors.getColorForState(getDrawableState(), mTextColors.getDefaultColor()));
			}   
			
			// 绘制滑动条左右的背景图
			if (true) {
				//画左背景图
				canvas.save();
				canvas.translate(mSwitchLeft + getPaddingLeft(), (mSliderSwitchHeight - mDrawableLeft.getIntrinsicHeight()) / 2);  
				if (mDrawableLeft != null){
					mDrawableLeft.draw(canvas);
				}
				canvas.restore();  
				//画右背景图
				canvas.save();
				canvas.translate(mSwitchRight - mDrawableRight.getIntrinsicWidth() - getPaddingRight(), (mSliderSwitchHeight - mDrawableRight.getIntrinsicHeight()) / 2); 
				if (mDrawableRight != null) {
					mDrawableRight.draw(canvas);
				}
				canvas.restore();
			} else {
				//画左背景图
				canvas.save();
				canvas.translate(mSwitchLeft  + getPaddingLeft(), (mSliderSwitchHeight - mDrawableLeft.getIntrinsicHeight()) / 2);  
				if (mDrawableLeft != null) {
					mDrawableLeft.draw(canvas);
				}
				canvas.restore(); 
				//画右背景图
				canvas.save();
				canvas.translate(mSwitchRight - mDrawableRight.getIntrinsicWidth() - getPaddingRight(), (mSliderSwitchHeight - mDrawableRight.getIntrinsicHeight()) / 2 );  
				if (mDrawableRight != null) {
					mDrawableRight.draw(canvas);
				}
				canvas.restore();
			} 
			//绘制背景上的文字
			canvas.save();
			canvas.translate((mSliderSwitchWidth - mButtonTextLayout.getWidth())  / 2 , (mSliderSwitchHeight - mButtonTextLayout.getHeight()) / 2);
			mButtonTextLayout.draw(canvas);
			canvas.restore();
			 
		}
	}
 

	private int getThumbScrollRange() { 
		int range = 0; 
		if (mOrientation == HORIZONTAL) {
			range = mSliderSwitchWidth - mThumbWidth;
		}
		// Log.d(TAG,"getThumbScrollRange() = "+ range);
		return range;
	} 
	
	@Override
	protected void drawableStateChanged() {
		super.drawableStateChanged(); 
		int[] myDrawableState = getDrawableState();

		// Set the state of the Drawable
		// Drawable may be null when checked state is set from XML, from super
		// constructor
		if (mThumbDrawable != null) {
			mThumbDrawable.setState(myDrawableState); 
		}
		invalidate();
	}

	@Override
	protected boolean verifyDrawable(Drawable who) {
		return super.verifyDrawable(who) || who == mThumbDrawable;
	}
	
	public void setOnSliderChangeListener(OnSliderChangeListener l) {
		mOnSliderChangeListener = l;
	}

	public interface OnSliderChangeListener { 
		public abstract void onProgressChanged(MySnapperSwitch slider, int progress, boolean fromUser);

	}
}