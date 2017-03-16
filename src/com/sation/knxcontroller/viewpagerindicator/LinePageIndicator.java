/*
 * Copyright (C) 2012 Jake Wharton
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sation.knxcontroller.viewpagerindicator;



import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewConfigurationCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

/**
 * Draws a line for each page. The current page line is colored differently than
 * the unselected page lines.
 */
public class LinePageIndicator extends View implements PageIndicator {
	private static final int INVALID_POINTER = -1;
	// ============================================
	// ===================Config===================
	// ============================================
	// 线条粗细,单位dp
	private static final int STROKE_WIDTH = 2;
	// 线条长度,单位dp
	private static final int LINE_WIDTH = 12;
	// 线条间隔,单位dp
	private static final int GAP_WIDTH = 4;
	// 选中的时候线条颜色
	private static final int SELECTED_COLOR = Color.parseColor("#FF33B5E5");
	// 没有选中的时候线条颜色
	private static final int UN_SELECTED_COLOR = Color.parseColor("#FFBBBBBB");
	// ============================================
	// =================Config End=================
	// ============================================

	// 没有选中的时候的画笔
	private final Paint mPaintUnselected = new Paint(Paint.ANTI_ALIAS_FLAG);
	// 选中的时候的画笔
	private final Paint mPaintSelected = new Paint(Paint.ANTI_ALIAS_FLAG);
	private ViewPager mViewPager;
	private ViewPager.OnPageChangeListener mListener;
	// 当前页面
	private int mCurrentPage;
	// 是否居中
	private boolean mCentered = true;
	// 线条长度,单位px
	private float mLineWidth;
	// 线条之间的间隔,单位px
	private float mGapWidth;

	private int mTouchSlop;
	private float mLastMotionX = -1;
	private int mActivePointerId = INVALID_POINTER;
	private boolean mIsDragging;
	private boolean mCycleDrag;
	private int mPageCount;

	public LinePageIndicator(Context context) {
		this(context, null);
	}

	/**
	 * dp转px
	 * 
	 * @param res
	 *            android.content.res.Resources
	 * @param dp
	 * @return
	 */
	public static int dpToPx(Resources res, int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, res.getDisplayMetrics());
	}

	public LinePageIndicator(Context context, AttributeSet attrs) {
		super(context, attrs);
		if (isInEditMode())
			return;

		final Resources res = getResources();
		mLineWidth = dpToPx(res, LINE_WIDTH);
		mGapWidth = dpToPx(res, GAP_WIDTH);
		setStrokeWidth(dpToPx(res, STROKE_WIDTH));
		mPaintUnselected.setColor(UN_SELECTED_COLOR);
		mPaintSelected.setColor(SELECTED_COLOR);

		final ViewConfiguration configuration = ViewConfiguration.get(context);
		mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration);
	}

	public void setCentered(boolean centered) {
		mCentered = centered;
		invalidate();
	}

	public boolean isCentered() {
		return mCentered;
	}

	public void setUnselectedColor(int unselectedColor) {
		mPaintUnselected.setColor(unselectedColor);
		invalidate();
	}

	public int getUnselectedColor() {
		return mPaintUnselected.getColor();
	}

	public void setSelectedColor(int selectedColor) {
		mPaintSelected.setColor(selectedColor);
		invalidate();
	}

	public int getSelectedColor() {
		return mPaintSelected.getColor();
	}

	public void setLineWidth(float lineWidth) {
		mLineWidth = lineWidth;
		invalidate();
	}

	public float getLineWidth() {
		return mLineWidth;
	}

	public void setStrokeWidth(float lineHeight) {
		mPaintSelected.setStrokeWidth(lineHeight);
		mPaintUnselected.setStrokeWidth(lineHeight);
		invalidate();
	}

	public float getStrokeWidth() {
		return mPaintSelected.getStrokeWidth();
	}

	public void setGapWidth(float gapWidth) {
		mGapWidth = gapWidth;
		invalidate();
	}

	public float getGapWidth() {
		return mGapWidth;
	}
	
	public void setCycleDrag(boolean cycle) {
		this.mCycleDrag = cycle;
	}

	public void setPageCount(int count) {
		this.mPageCount = count;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (mViewPager == null) {
			return;
		}

		int count = this.mPageCount;
		if (count <= 1) {
			return;
		}

		if (mCurrentPage >= count) {
			setCurrentItem(count - 1);
			return;
		}

		final float lineWidthAndGap = mLineWidth + mGapWidth;
		final float indicatorWidth = (count * lineWidthAndGap) - mGapWidth;
		final float paddingTop = getPaddingTop();
		final float paddingLeft = getPaddingLeft();
		final float paddingRight = getPaddingRight();

		float verticalOffset = paddingTop + ((getHeight() - paddingTop - getPaddingBottom()) / 2.0f);
		float horizontalOffset = paddingLeft;
		if (mCentered) {
			horizontalOffset += ((getWidth() - paddingLeft - paddingRight) / 2.0f) - (indicatorWidth / 2.0f);
		}

		// Draw stroked circles
		for (int i = 0; i < count; i++) {
			float dx1 = horizontalOffset + (i * lineWidthAndGap);
			float dx2 = dx1 + mLineWidth;
			canvas.drawLine(dx1, verticalOffset, dx2, verticalOffset, (i == mCurrentPage) ? mPaintSelected : mPaintUnselected);
		}
	}

//	public boolean onTouchEvent(android.view.MotionEvent ev) {
//		try {
//			if (super.onTouchEvent(ev)) {
//				return true;
//			}
//			if ((mViewPager == null) || (mViewPager.getAdapter().getCount() == 0)) {
//				return false;
//			}
//
//			final int action = ev.getAction() & MotionEventCompat.ACTION_MASK;
//			switch (action) {
//			case MotionEvent.ACTION_DOWN:
//				mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
//				mLastMotionX = ev.getX();
//				break;
//
//			case MotionEvent.ACTION_MOVE: {
//				final int activePointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
//				final float x = MotionEventCompat.getX(ev, activePointerIndex);
//				final float deltaX = x - mLastMotionX;
//
//				if (!mIsDragging) {
//					if (Math.abs(deltaX) > mTouchSlop) {
//						mIsDragging = true;
//					}
//				}
//
//				if (mIsDragging) {
//					mLastMotionX = x;
//					if(null != this.mViewPager) {
//						if ((null != this.mViewPager) || mViewPager.beginFakeDrag()) {
//							mViewPager.fakeDragBy(deltaX);
//						}
//					}
//				}
//
//				break;
//			}
//
//			case MotionEvent.ACTION_CANCEL:
//			case MotionEvent.ACTION_UP:
//				if ((!mIsDragging) && (null != this.mViewPager)) {
//					final int count = mViewPager.getAdapter().getCount();
//					final int width = getWidth();
//					final float halfWidth = width / 2f;
//					final float sixthWidth = width / 6f;
//
//					if ((mCurrentPage > 0) && (ev.getX() < halfWidth - sixthWidth)) {
//						if (action != MotionEvent.ACTION_CANCEL) {
//							mViewPager.setCurrentItem(mCurrentPage - 1);
//						}
//						return true;
//					} else if ((mCurrentPage < count - 1) && (ev.getX() > halfWidth + sixthWidth)) {
//						if (action != MotionEvent.ACTION_CANCEL) {
//							mViewPager.setCurrentItem(mCurrentPage + 1);
//						}
//						return true;
//					}
//				}
//
//				mIsDragging = false;
//				mActivePointerId = INVALID_POINTER;
//				if ((null != this.mViewPager) && (mViewPager.isFakeDragging())) {
//					mViewPager.endFakeDrag();
//				}
//				break;
//
//			case MotionEventCompat.ACTION_POINTER_DOWN: {
//				final int index = MotionEventCompat.getActionIndex(ev);
//				mLastMotionX = MotionEventCompat.getX(ev, index);
//				mActivePointerId = MotionEventCompat.getPointerId(ev, index);
//				break;
//			}
//
//			case MotionEventCompat.ACTION_POINTER_UP:
//				final int pointerIndex = MotionEventCompat.getActionIndex(ev);
//				final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
//				if (pointerId == mActivePointerId) {
//					final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
//					mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
//				}
//				mLastMotionX = MotionEventCompat.getX(ev, MotionEventCompat.findPointerIndex(ev, mActivePointerId));
//				break;
//			}
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
//		
//		return true;
//	}

	@Override
	public void setViewPager(ViewPager viewPager) {
		if (mViewPager == viewPager) {
			return;
		}
		if (mViewPager != null) {
			// Clear us from the old pager.
			mViewPager.setOnPageChangeListener(null);
		}
		if (viewPager.getAdapter() == null) {
			throw new IllegalStateException("ViewPager does not have adapter instance.");
		}
		mViewPager = viewPager;
		mViewPager.addOnPageChangeListener(this);
		mCurrentPage = mViewPager.getCurrentItem();
		invalidate();
	}

	@Override
	public void setViewPager(ViewPager view, int initialPosition) {
		setViewPager(view);
		setCurrentItem(initialPosition);
	}

	@Override
	public void setCurrentItem(int item) {
		if (null == this.mViewPager) {
			throw new IllegalStateException("ViewPager has not been bound.");
		}
		if(this.mCycleDrag) {
			this.mViewPager.setCurrentItem(item+1);
		} else {
			this.mViewPager.setCurrentItem(item);
		}
		this.mCurrentPage = item;
		invalidate();
	}

	@Override
	public void notifyDataSetChanged() {
		invalidate();
	}

	@Override
	public void onPageScrollStateChanged(int state) {
		if (mListener != null) {
			mListener.onPageScrollStateChanged(state);
		}
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		if (mListener != null) {
			mListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
		}
	}

	@Override
	public void onPageSelected(int position) {
		if(this.mCycleDrag) {
			int pageCount = this.mPageCount;
			if (0 == position) {
				mCurrentPage = pageCount - 1;
			} else if ((pageCount + 1) == position) {
				mCurrentPage = 0;
			} else {
				mCurrentPage = position -1;
			}
		} else {
			mCurrentPage = position;
		}
		invalidate();

		if (mListener != null) {
			mListener.onPageSelected(position);
		}
	}

	@Override
	public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
		mListener = listener;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
	}

	/**
	 * Determines the width of this view
	 * 
	 * @param measureSpec
	 *            A measureSpec packed into an int
	 * @return The width of the view, honoring constraints from measureSpec
	 */
	private int measureWidth(int measureSpec) {
		float result;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		if ((specMode == MeasureSpec.EXACTLY) || (mViewPager == null)) {
			// We were told how big to be
			result = specSize;
		} else {
			// Calculate the width according the views count
			final int count = mViewPager.getAdapter().getCount();
			result = getPaddingLeft() + getPaddingRight() + (count * mLineWidth) + ((count - 1) * mGapWidth);
			// Respect AT_MOST value if that was what is called for by
			// measureSpec
			if (specMode == MeasureSpec.AT_MOST) {
				result = Math.min(result, specSize);
			}
		}
		return (int) Math.ceil(result);
	}

	/**
	 * Determines the height of this view
	 * 
	 * @param measureSpec
	 *            A measureSpec packed into an int
	 * @return The height of the view, honoring constraints from measureSpec
	 */
	private int measureHeight(int measureSpec) {
		float result;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		if (specMode == MeasureSpec.EXACTLY) {
			// We were told how big to be
			result = specSize;
		} else {
			// Measure the height
			result = mPaintSelected.getStrokeWidth() + getPaddingTop() + getPaddingBottom();
			// Respect AT_MOST value if that was what is called for by
			// measureSpec
			if (specMode == MeasureSpec.AT_MOST) {
				result = Math.min(result, specSize);
			}
		}
		return (int) Math.ceil(result);
	}

	@Override
	public void onRestoreInstanceState(Parcelable state) {
		SavedState savedState = (SavedState) state;
		super.onRestoreInstanceState(savedState.getSuperState());
		mCurrentPage = savedState.currentPage;
		requestLayout();
	}

	@Override
	public Parcelable onSaveInstanceState() {
		Parcelable superState = super.onSaveInstanceState();
		SavedState savedState = new SavedState(superState);
		savedState.currentPage = mCurrentPage;
		return savedState;
	}

	static class SavedState extends BaseSavedState {
		int currentPage;

		public SavedState(Parcelable superState) {
			super(superState);
		}

		private SavedState(Parcel in) {
			super(in);
			currentPage = in.readInt();
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeInt(currentPage);
		}

		public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
			@Override
			public SavedState createFromParcel(Parcel in) {
				return new SavedState(in);
			}

			@Override
			public SavedState[] newArray(int size) {
				return new SavedState[size];
			}
		};
	}
}
