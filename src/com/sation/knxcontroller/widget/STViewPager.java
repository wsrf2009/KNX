package com.sation.knxcontroller.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class STViewPager extends ViewPager {
	private boolean isCanScroll = true;  
	  
    public STViewPager(Context context) {  
        super(context);  
    }  
  
    public STViewPager(Context context, AttributeSet attrs) {  
        super(context, attrs);  
    }  
  
    public void setScanScroll(boolean isCanScroll){  
        this.isCanScroll = isCanScroll;  
    }  

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
    	super.onInterceptTouchEvent(event);
    	
		return false;
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	super.onTouchEvent(event);
    	
    	return true;
    }
    
    @Override  
    public void scrollTo(int x, int y){  
        if (isCanScroll){  
            super.scrollTo(x, y);  
        }  
    }  
}
