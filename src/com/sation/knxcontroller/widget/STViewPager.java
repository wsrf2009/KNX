package com.sation.knxcontroller.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class STViewPager extends ViewPager {
	private boolean mCycleDrag; // 循环滑动页面
	
	@Override
	public int getCurrentItem() {
		if(this.mCycleDrag) {
			return super.getCurrentItem() - 1;
		} else {
			return super.getCurrentItem();
		}
	}

    public STViewPager(Context context) {  
        super(context);
    }  
  
    public STViewPager(Context context, AttributeSet attrs) {  
        super(context, attrs);  
    }  

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
    	try {
    		super.onInterceptTouchEvent(event);
    	} catch (IllegalArgumentException ex) {
    		ex.printStackTrace();
    	}
    	
    	return false;
    }
    
    @SuppressLint("ClickableViewAccessibility")
	@Override
    public boolean onTouchEvent(MotionEvent event) {
		boolean retval = true;
    	try {
			retval = super.onTouchEvent(event);
    	} catch (IllegalArgumentException ex) {
    		ex.printStackTrace();
    	}
    	
    	return retval;
    }
    
    public void setCycleDrag(boolean drag) {
    	this.mCycleDrag = drag;
    }
}
