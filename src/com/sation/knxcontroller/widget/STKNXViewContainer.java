package com.sation.knxcontroller.widget;

import com.sation.knxcontroller.STKNXControllerConstant;
import com.sation.knxcontroller.models.KNXContainer;
import com.sation.knxcontroller.util.ImageUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

public class STKNXViewContainer extends STKNXControl {

	public STKNXViewContainer(Context context, KNXContainer container) {
		super(context, container);

	}

	/**
	 * 计算所有ChildView的宽度和高度 然后根据ChildView的计算结果，设置自己的宽和高
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// 计算出所有的childView的宽和高
		measureChildren(widthMeasureSpec, heightMeasureSpec);

		setMeasuredDimension(this.mKNXView.Width, this.mKNXView.Height);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int cCount = getChildCount();
		
		/*
		 * 遍历所有childView根据其宽和高，以及margin进行布局
		 */
		for (int i = 0; i < cCount; i++) {
			STKNXView childView = (STKNXView)getChildAt(i);

			int cl = 0, ct = 0, cr = 0, cb = 0;
			cl = childView.mKNXView.Left;
			ct = childView.mKNXView.Top;
			cr = cl+childView.mKNXView.Width;
			cb = ct+childView.mKNXView.Height;

			childView.layout(cl, ct, cr, cb);
		}
	}
	
	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
    	super.onDraw(canvas);

		try {
			Paint paint = new Paint();
    	/* 画圆角矩形  */
			paint.setStyle(Paint.Style.FILL_AND_STROKE);    // 充满
			paint.setAntiAlias(true);// 设置画笔的锯齿效果
			paint.setColor(Color.parseColor(this.mKNXView.BackgroundColor));
			paint.setAlpha((int) (this.mKNXView.Alpha * 255));
			RectF oval3 = new RectF(0, 0, getWidth(), getHeight());// 设置个新的长方形
			canvas.drawRoundRect(oval3, this.mKNXView.Radius, this.mKNXView.Radius, paint);//第二个参数是x半径，第三个参数是y半径

			if (null != this.mKNXView.getBackgroundImage()) {
				Bitmap backimg = ImageUtils.getDiskBitmap(
						STKNXControllerConstant.ConfigResImgPath + this.mKNXView.getBackgroundImage());
				Rect resRect = new Rect(0, 0, backimg.getWidth(), backimg.getHeight());
				Rect desRect = new Rect(0, 0, this.mKNXView.Width, this.mKNXView.Height);
				canvas.drawBitmap(backimg, resRect, desRect, paint);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

//	@Override
//	public LayoutParams generateLayoutParams(AttributeSet attrs) {
//		return new MarginLayoutParams(getContext(), attrs);
//	}
//
//	@Override
//	protected LayoutParams generateDefaultLayoutParams() {
//		Log.e(TAG, "generateDefaultLayoutParams");
//		return new MarginLayoutParams(LayoutParams.MATCH_PARENT,
//				LayoutParams.MATCH_PARENT);
//	}
//
//	@Override
//	protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
//		Log.e(TAG, "generateLayoutParams p");
//		return new MarginLayoutParams(p);
//	}

}
