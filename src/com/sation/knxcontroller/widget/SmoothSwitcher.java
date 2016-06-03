package com.sation.knxcontroller.widget;

import com.sation.knxcontroller.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.util.AttributeSet; 
import android.view.MotionEvent; 

public class SmoothSwitcher extends ControlView {
	 private static final String TAG = "switcher";
	  private static Integer height = 100;
	  private OnSmoothSwitcherCheckedChangeListener m_listener;
	  
	  private final Paint m_cursor_off_paint, m_cursor_on_paint, m_cursor_drag_paint, m_background_paint, m_text_paint;

	  private final float m_vertical_padding;
	  private final float m_horizontal_padding;
	  
	  private final float m_full_width;
	  private final float m_box_rect_height;
	  private final float m_box_rect_width;
	  private final float m_cursor_rect_width;
	  
	  private final float m_text_width;
	  private final String m_text;
	  
	  private float m_current_relative_position;  // cursor relative position
	  private float m_max_relative_progress;  // max cursor relative position
	  private float m_position;  // cursor absolute position
	  private float m_max_position;  // max cursor absolute position
	  private boolean m_is_dragging;
	  
	  private boolean m_enabled;
	  private SmoothSwitcherCursorStates m_cursor_state;
	  
	  private final float m_text_scale = 30;
	  
	  
	  /* Public API */
	  // --------------------------------------------------------------------------
	  public SmoothSwitcher(Context context, AttributeSet attrs) {
	    super(context, attrs);
	    m_cursor_off_paint = new Paint();
	    m_cursor_off_paint.setColor(getResources().getColor(R.color.switcher_cursor_off));
	    m_cursor_on_paint = new Paint();
	    m_cursor_on_paint.setColor(getResources().getColor(R.color.switcher_cursor_on));
	    m_cursor_drag_paint = new Paint();
	    m_cursor_drag_paint.setColor(getResources().getColor(R.color.switcher_cursor_drag));
	    m_background_paint = new Paint();
	    m_background_paint.setColor(getResources().getColor(R.color.switcher_background));
	    m_text_paint = new Paint();
	    m_text_paint.setColor(Color.WHITE);
	    m_text_paint.setStyle(Style.FILL_AND_STROKE);
	    m_text_paint.setTextSize(m_text_scale);
	    
	    TypedArray attributes_array = context.obtainStyledAttributes(attrs, R.styleable.SmoothSwitcher, 0, 0);
	    m_vertical_padding = attributes_array.getDimension(R.styleable.SmoothSwitcher_vertical_padding, 0.0f);
	    m_horizontal_padding = attributes_array.getDimension(R.styleable.SmoothSwitcher_horizontal_padding, 0.0f);
	    
	    m_box_rect_height = attributes_array.getDimension(R.styleable.SmoothSwitcher_box_height, 15.0f);
	    m_full_width = attributes_array.getDimension(R.styleable.SmoothSwitcher_box_width, 300.0f);
	    float ratio = 0.7f;
	    m_box_rect_width = m_full_width * ratio;
	    m_text_width = m_full_width - m_box_rect_width;
	    
	    m_text = attributes_array.getString(R.styleable.SmoothSwitcher_text);
	    
	    m_max_relative_progress = attributes_array.getDimension(R.styleable.SmoothSwitcher_max, 100.0f) * ratio;
	    m_enabled = attributes_array.getBoolean(R.styleable.SmoothSwitcher_enabled, false);
	    attributes_array.recycle();
	    
	    m_cursor_rect_width = m_box_rect_width * 0.5f;
	    m_max_position = m_horizontal_padding + m_text_width + m_max_relative_progress;
	    m_is_dragging = false;
	    
	    m_current_relative_position = m_enabled ? m_box_rect_width * 0.5f : 0.0f;
	    m_cursor_state = m_enabled ? SmoothSwitcherCursorStates.ENABLED : SmoothSwitcherCursorStates.DISABLED;
	    m_position = m_horizontal_padding + m_text_width + m_current_relative_position;
	  }
	  
	  public boolean isChecked() {
	    return m_enabled;
	  }
	  
	  public void setChecked(boolean checked) {
	    m_enabled = checked;
	    m_current_relative_position = m_enabled ? m_box_rect_width * 0.5f : 0.0f;
	    m_cursor_state = m_enabled ? SmoothSwitcherCursorStates.ENABLED : SmoothSwitcherCursorStates.DISABLED;
	    m_position = m_horizontal_padding + m_text_width + m_current_relative_position;
	    invalidate();
	  }

	  public void setOnSmoothSwitcherCheckedChangeListener(OnSmoothSwitcherCheckedChangeListener listener) {
	    m_listener = listener;
	  }
	  
	  @Override 
	  public boolean onTouchEvent(MotionEvent event) {
	    switch (event.getAction()) {
	      case MotionEvent.ACTION_DOWN:
	        if (event.getX() >= m_position &&
	            event.getX() <= m_position + m_cursor_rect_width &&
	            event.getY() >= m_vertical_padding &&
	            event.getY() <= m_vertical_padding + m_box_rect_height) {
	          // user touch draggable box - start dragging
	          getParent().requestDisallowInterceptTouchEvent(true);
	          m_is_dragging = true;
	        }
	        break;
	      case MotionEvent.ACTION_MOVE:
	        handleMove(event);
	        break;
	      case MotionEvent.ACTION_UP:
	        if (m_is_dragging) {
	          m_is_dragging = false;
	          flip(relativePositionToState());
	        }
	        break;
	    }
	    return true;
	  }
	  
	  @SuppressLint("DrawAllocation")
	  @Override
	  public void onDraw(Canvas canvas) {
	    if (m_is_dragging) {
	      m_position = m_horizontal_padding + m_text_width + m_current_relative_position;
	    } else {
	    }
	    
	    RectF background_left_box = new RectF(
	      m_horizontal_padding + m_text_width,
	      m_vertical_padding,
	      m_horizontal_padding + m_text_width + m_current_relative_position,
	      m_vertical_padding + m_box_rect_height);
	    canvas.drawRect(background_left_box, m_background_paint);
	    
	    RectF cursor_box = new RectF(
	      m_horizontal_padding + m_text_width + m_current_relative_position,
	      m_vertical_padding,
	      m_horizontal_padding + m_text_width + m_current_relative_position + m_cursor_rect_width,
	      m_vertical_padding + m_box_rect_height);
	    
	    String text_label = "";
	    switch (m_cursor_state) {
	      case DISABLED:
	        canvas.drawRect(cursor_box, m_cursor_off_paint);
	        text_label = getResources().getString(R.string.switcher_cursor_off);
	        break;
	      case DRAGGING:
	        canvas.drawRect(cursor_box, m_cursor_drag_paint);
	        text_label = getResources().getString(R.string.switcher_cursor_drag);
	        break;
	      case ENABLED:
	        canvas.drawRect(cursor_box, m_cursor_on_paint);
	        text_label = getResources().getString(R.string.switcher_cursor_on);
	        break;
	      default:
	        break;
	    }
	    
	    RectF background_right_box = new RectF(
	      m_horizontal_padding + m_text_width + m_current_relative_position + m_cursor_rect_width,
	      m_vertical_padding,
	      m_horizontal_padding + m_text_width + m_box_rect_width,
	      m_vertical_padding + m_box_rect_height);
	    canvas.drawRect(background_right_box, m_background_paint);
	    
	    float text_x = m_horizontal_padding + m_text_width + m_current_relative_position + (m_cursor_rect_width - m_text_scale) * 0.5f;
	    float text_y = m_vertical_padding + (m_box_rect_height - m_text_scale) * 0.5f;
	    canvas.drawText(m_text, m_horizontal_padding + 2.0f, text_y, m_text_paint);
	    canvas.drawText(text_label, text_x, text_y, m_text_paint);
	  }
	  
	  
	  /* Private methods */
	  // --------------------------------------------------------------------------
	  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	    if (getLayoutParams().height == LayoutParams.WRAP_CONTENT) {
	      height = 100;
	    } else if (getLayoutParams().height == LayoutParams.MATCH_PARENT ||
	               getLayoutParams().height == LayoutParams.FILL_PARENT) {
	      height = MeasureSpec.getSize(heightMeasureSpec);
	    } else {
	      height = getLayoutParams().height;
	    }
	    setMeasuredDimension(widthMeasureSpec | MeasureSpec.EXACTLY, height | MeasureSpec.EXACTLY);
	  }
	  
	  private void handleMove(MotionEvent event) {
	    if (!m_is_dragging ||
	        event.getX() > m_horizontal_padding + m_text_width + m_box_rect_width ||
	        event.getX() < m_horizontal_padding + m_text_width ||
	        event.getX() > m_max_position) {
	      return;
	    }
	    float actual_position = event.getX() - m_horizontal_padding - m_text_width;
	    if (m_enabled) {
	      if (actual_position >= 0.0f && actual_position <= m_box_rect_width * 0.5) {
	        m_current_relative_position = actual_position;
	      } else {
	        return;
	      }
	    } else {
	      if (actual_position >= m_box_rect_width * 0.5 && actual_position <= m_box_rect_width) {
	        m_current_relative_position = actual_position - m_cursor_rect_width;
	      } else {
	        return;
	      }
	    }
	    m_cursor_state = SmoothSwitcherCursorStates.DRAGGING;
	    invalidate();
	  }
	  
	  private void flip(boolean state) {
	    m_enabled = state;
	    if (state) {
	      m_current_relative_position = m_box_rect_width * 0.5f;
	    } else {
	      m_current_relative_position = 0.0f;
	    }
	    m_position = m_horizontal_padding + m_text_width + m_current_relative_position;
	    invalidate();
	    m_cursor_state = m_enabled ? SmoothSwitcherCursorStates.ENABLED : SmoothSwitcherCursorStates.DISABLED;
	    m_listener.onCheckedChanged(this, m_enabled);
	  }
	  
	  private boolean relativePositionToState() {
	    return (m_current_relative_position / m_box_rect_width) > 0.25f;
	  }
}
