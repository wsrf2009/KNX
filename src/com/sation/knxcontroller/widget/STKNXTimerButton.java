package com.sation.knxcontroller.widget;

import com.sation.knxcontroller.R;
import com.sation.knxcontroller.STKNXControllerApp;
import com.sation.knxcontroller.STKNXControllerConstant;
import com.sation.knxcontroller.activity.RoomDetailsActivity;
import com.sation.knxcontroller.activity.TimingTaskActivity;
import com.sation.knxcontroller.control.KNXTimerButton;
import com.sation.knxcontroller.models.KNXView.EBool;
import com.sation.knxcontroller.models.KNXView.EFlatStyle;
import com.sation.knxcontroller.util.ColorUtils;
import com.sation.knxcontroller.util.ImageUtils;
import com.sation.knxcontroller.util.Log;
import com.sation.knxcontroller.util.StringUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class STKNXTimerButton extends STKNXControl {
	private final String TAG = "STKNXTimerButton";
	private final int PADDING = 2;
	
	private KNXTimerButton mKNXTimerButton;
	private enum ControlState {
    	Down,
    	Normal,
    }
    private ControlState mControlState;
    private int imgY = 0;
    private int imgRight = 0;
    private String image;
    private ImageView mImageView;
	private SharedPreferences settings;
	private Context mContext;

	public STKNXTimerButton(Context context, KNXTimerButton knxTimerButton) {
		super(context, knxTimerButton);

		this.mContext = context;
		this.settings = context.getSharedPreferences(
				STKNXControllerConstant.SETTING_FILE, android.content.Context.MODE_PRIVATE);

		this.mKNXTimerButton = knxTimerButton;
		this.setId(this.mKNXTimerButton.getId());

		this.mControlState = ControlState.Normal;

		this.mImageView = new ImageView(context);
		this.addView(this.mImageView);

		this.image = STKNXControllerConstant.ConfigResImgPath + this.mKNXTimerButton.getSymbol();

		Bitmap bm = ImageUtils.getDiskBitmap(this.image);
		if (null != bm) {
			mImageView.setImageBitmap(bm);
		}
	}

	@Override
	public void onSuspend() {

	}

	@Override
	public void onResume() {

	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		measureChildren(widthMeasureSpec, heightMeasureSpec);

        /**
         * 最后调用父类方法,把View的大小告诉父布局。
         */
        setMeasuredDimension(this.mKNXTimerButton.Width, this.mKNXTimerButton.Height);
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
			 if(view instanceof ImageView) {
				 int imgHeight = this.mKNXTimerButton.Height - 2 * this.imgY;

				 cl = this.PADDING;
				 ct = this.PADDING;
				 cr = this.PADDING+imgHeight;
				 cb = this.PADDING+imgHeight;

//				 cl = this.mKNXTimerButton.getPadding().getLeft();
//				 ct = this.mKNXTimerButton.getPadding().getTop();
//				 cr = this.mKNXTimerButton.Width - this.mKNXTimerButton.getPadding().getRight();
//				 cb = this.mKNXTimerButton.Height - this.mKNXTimerButton.getPadding().getBottom();
				}
				view.layout(cl, ct, cr, cb);
			}
		}

    @SuppressLint("DrawAllocation")
	@Override
    protected void onDraw(Canvas canvas) {
    	super.onDraw(canvas);
  
    	int backColor = Color.parseColor(this.mKNXTimerButton.BackgroundColor);
    	Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    	paint.setStyle(Paint.Style.FILL_AND_STROKE);	// 充满  
    	paint.setAlpha((int)(this.mKNXTimerButton.Alpha*255));
    	RectF oval3 = new RectF(0, 0, getWidth(), getHeight());// 设置个新的长方形  

    	if(EFlatStyle.Stereo == this.mKNXTimerButton.getFlatStyle()) {	// 画立体感的圆角矩形 
        
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
    		paint.setARGB((int)(this.mKNXTimerButton.Alpha*255), Color.red(backColor), Color.green(backColor), Color.blue(backColor));
    	}
    	canvas.drawRoundRect(oval3, this.mKNXTimerButton.Radius, this.mKNXTimerButton.Radius, paint);//第二个参数是x半径，第三个参数是y半径  
    	
    	paint.reset();

        if(null != this.mKNXTimerButton.getTitle()) {
			Paint textPaint = this.mKNXTimerButton.TitleFont.getTextPaint();
			float x = 0;
			if (StringUtil.isNullOrEmpty(this.image) && StringUtil.isNullOrEmpty(this.image)) {
				x = oval3.width() / 2;
			} else {
				x = this.imgRight + this.PADDING + (oval3.width() - (this.imgRight + this.PADDING) )/2;
			}
			int baseY = (int) ((oval3.height() / 2) - ((textPaint.descent() + textPaint.ascent()) / 2));
			canvas.drawText(this.mKNXTimerButton.getTitle(), oval3.width()/2, baseY, textPaint);
        }
        
        switch (mControlState) {
    		case Down:
    			paint.reset();
    			paint.setStyle(Paint.Style.FILL);
    			paint.setColor(Color.parseColor("#FF6100"));
    			paint.setAlpha(0x60);
    			canvas.drawRoundRect(oval3, this.mKNXTimerButton.Radius, this.mKNXTimerButton.Radius, paint);	//第二个参数是x半径，第三个参数是y半径  
    			break;
    		case Normal:
    			break;
        }
        
        if(EBool.Yes == this.mKNXTimerButton.getDisplayBorder()) {
    		paint.reset();
			paint.setAntiAlias(true);
    		paint.setStyle(Paint.Style.STROKE);
    		paint.setColor(Color.parseColor(this.mKNXTimerButton.BorderColor));
    		canvas.drawRoundRect(oval3, this.mKNXTimerButton.Radius, this.mKNXTimerButton.Radius, paint);//第二个参数是x半径，第三个参数是y半径  
    	}
    }
    
    @SuppressLint("ClickableViewAccessibility")
	@Override
    public boolean onTouchEvent(MotionEvent event) {
    	switch (event.getAction()) { 
    		case MotionEvent.ACTION_DOWN: 
    			this.mControlState = ControlState.Down;
    			invalidate();
    			if(null != this.mImageView) {
    				this.mImageView.setAlpha(0.6f);
    			}
    			break; 
    		case MotionEvent.ACTION_UP: 
    			this.mControlState = ControlState.Normal;
				onClick();
    			invalidate();
    			if(null != this.mImageView) {
    				this.mImageView.setAlpha(1.0f);
    			}
    			break;
    		case MotionEvent.ACTION_CANCEL:
    			this.mControlState = ControlState.Normal;
    			invalidate();
    			if(null != this.mImageView) {
    				this.mImageView.setAlpha(1.0f);
    			}
    			break;
    			
    		default:
    			break;
    	}

    	return true;
    }

	private void onClick() {
		String id = String.valueOf(this.getId());
		if (STKNXControllerApp.getInstance().getRememberLastInterface()) { // 是否要记住最后界面
			STKNXControllerApp.getInstance().setLastTimerId(id);
			SharedPreferences.Editor editor = settings.edit();
			editor.putString(STKNXControllerConstant.LAST_TIMER_ID, id);
			editor.apply(); // 将当前Timer的ID保存起来

		}

		jumpToTimingTaskActivity(id); // 跳转到定时器界面
	}

	private void jumpToTimingTaskActivity(String id) {
		Intent intent = new Intent(this.mContext, TimingTaskActivity.class);
		intent.putExtra(STKNXControllerConstant.CONTROL_ID, id); // 将定时器ID作为参数传递
        Bundle b = new Bundle();
        b.putSerializable(STKNXControllerConstant.KNXTIMEROBJECT, mKNXTimerButton);
        intent.putExtras(b);
		this.mContext.startActivity(intent);

		((RoomDetailsActivity)this.mContext).overridePendingTransition(R.anim.scale_from_center_600,
				R.anim.scale_to_center_600);
	}
}
