package com.sation.knxcontroller.util;

import android.content.Context;
import android.os.Handler;
import android.view.ViewGroup;
import android.app.Activity;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class LocationAnimation { 
	
	/**
	 * @param v
	 *            动画运动的图片
	 */
	public static void startAnima(Context mContext, ImageView v,int[] startLocation, int[] endLocation) { 
		//得到根视图，后面把动画层加到根视图
		final ViewGroup root = (ViewGroup)((Activity)mContext). getWindow().getDecorView();

		// 新建一个linearLayout（动画层）
		final LinearLayout ll = new LinearLayout(mContext);
		LinearLayout.LayoutParams params = new android.widget.LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
		// 画动层的背景设置为透明
		ll.setBackgroundResource(android.R.color.transparent);
		ll.setLayoutParams(params);

		LinearLayout.LayoutParams viewParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		viewParams.leftMargin = startLocation[0];
		viewParams.topMargin = startLocation[1];
		v.setLayoutParams(viewParams);
		// 把动画图片加到动画层
		ll.addView(v);

		AnimationSet set1 = new AnimationSet(false);
		// X轴上的动画
		TranslateAnimation aX = new TranslateAnimation(0, endLocation[0] - startLocation[0], 0, 0);
		aX.setFillAfter(true);
		// 线性变化
		aX.setInterpolator(new LinearInterpolator());
		aX.setDuration(800);

		// Y轴上的动画（向下运动部分）
		TranslateAnimation aYdown = new TranslateAnimation(0, 0, 0, endLocation[1] - startLocation[1]);
		aYdown.setFillAfter(true);
		// 加速
		aYdown.setInterpolator(new AccelerateInterpolator());
		aYdown.setStartOffset(200);
		aYdown.setDuration(600);

		// Y轴上的动画（向上运动部分）
		TranslateAnimation aYup = new TranslateAnimation(0, 0, 0, -20);
		aYup.setFillAfter(true);
		aYup.setDuration(200);
		// 减速
		aYup.setInterpolator(new DecelerateInterpolator());

		set1.addAnimation(aX);
		set1.addAnimation(aYdown);
		set1.addAnimation(aYup);
		set1.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				//去除动画层 
				try {
					new Handler().post(new Runnable() {
				        @Override
				        public void run() {
				        	root.removeView(ll);
				        }
				    }); 
				} catch(Exception ex) {
		    		ex.printStackTrace();
		    	} 
			}
		});
		new Handler().post(new Runnable() {
	        @Override
	        public void run() {
	        	//root.removeView(ll);
	        	root.addView(ll);
	        }
	    });  
		v.startAnimation(set1);
	}
}
