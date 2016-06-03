package com.sation.knxcontroller.widget;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup; 
import android.widget.TableRow;

import com.sation.knxcontroller.STKNXControllerConstant;
import com.sation.knxcontroller.control.KNXSlider;
import com.sation.knxcontroller.util.ImageUtil;
import com.sation.knxcontroller.util.NumberFormat;
import com.sation.knxcontroller.widget.seekbar.ORSeekBar;
import com.sation.knxcontroller.R;

public class Slider extends ControlView implements ORSeekBar.OnSeekBarChangeListener {

	public final static long REPEAT_CMD_INTERVAL = 200;

	private static final int SEEK_BAR_PROGRESS_INIT_VALUE = 0;
	private static final int SEEK_BAR_PROGRESS_MAX = 100;

	private static final int SEEK_BAR_MIN_WIDTH = 29;
	private static final int SEEK_BAR_MIN_HEIGHT = 29;

	private static final int DEFAULT_HORIZONTAL_SEEK_BAR_HEIGHT = 26;
	private static final int DEFAULT_VERTICAL_SEEK_BAR_WIDTH = 26;

	private static final int SEEK_BAR_MAX_SIZE = 9;

	private static final int SEEK_BAR_MIN_IMAGE_WIDTH = 20;
	private static final int SEEK_BAR_MIN_IMAGE_HEIGHT = 20;
	private static final int SEEK_BAR_MAX_IMAGE_WIDTH = 20;
	private static final int SEEK_BAR_MAX_IMAGE_HEIGHT = 20;

	private Context context;
	private KNXSlider mKNXSlider;
	private ORSeekBar horizontalSeekBar;
	private int slideToBusinessValue = 0;

	public Slider(Context context) {
		super(context);
	}

	public Slider(Context context, KNXSlider mKNXSlider) {
		super(context);
		this.context= context;
		setKNXControl(mKNXSlider);
		if (mKNXSlider != null) {
			this.mKNXSlider = mKNXSlider;
			initControl(mKNXSlider);
		}
	}

	private void initControl(final KNXSlider mKNXSlider) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ViewGroup seekBarRootView = (ViewGroup) inflater.inflate(R.layout.horizontal_seekbar, (ViewGroup) findViewById(R.id.horizontal_seekbar_root_layout));

		horizontalSeekBar = (ORSeekBar) seekBarRootView .findViewById(R.id.horizontal_seekbar);
		boolean clipImage = false;
		// Set custom track image, include minTrack and maxTrack.
		Drawable maxTrackDrawable = null;
		Drawable minTrackDrawable = null;
		if (mKNXSlider.getRightImage() != null) {
			maxTrackDrawable = ImageUtil.createFromPathQuietly(STKNXControllerConstant.FILE_FOLDER_PATH + mKNXSlider.getRightImage());
		} else {
			maxTrackDrawable =  context.getResources().getDrawable(R.drawable.default_silder_swich_light_down);
		}
		if (mKNXSlider.getLeftImage() != null) {
			minTrackDrawable = ImageUtil.createFromPathQuietly(STKNXControllerConstant.FILE_FOLDER_PATH + mKNXSlider.getLeftImage());
		}else {
			minTrackDrawable =  context.getResources().getDrawable(R.drawable.default_silder_swich_light_up);
		}
		if (maxTrackDrawable != null || minTrackDrawable != null) {
			if (maxTrackDrawable == null) {
				maxTrackDrawable = context.getResources().getDrawable(R.drawable.horizontal_seekbar_background);
			}
			if (minTrackDrawable == null) {
				minTrackDrawable = context.getResources().getDrawable(R.drawable.horizontal_seekbar_progress);
			}
			int maxTrackWidth = maxTrackDrawable.getIntrinsicWidth();
			int maxTrackHeight = maxTrackDrawable.getIntrinsicHeight();
			/*
			if (maxTrackHeight > DEFAULT_HORIZONTAL_SEEK_BAR_HEIGHT && maxTrackWidth > mKNXSlider.getFrameWidth()) {
				BitmapDrawable bd = (BitmapDrawable) maxTrackDrawable;
				bd.setBounds(0, 0, mKNXSlider.getFrameWidth(), DEFAULT_HORIZONTAL_SEEK_BAR_HEIGHT);
				bd.setGravity(Gravity.RIGHT);
				clipImage = true;
			}

			int minTrackWidth = minTrackDrawable.getIntrinsicWidth();
			int minTrackHeight = minTrackDrawable.getIntrinsicHeight();
			if (minTrackHeight > DEFAULT_HORIZONTAL_SEEK_BAR_HEIGHT && minTrackWidth > mKNXSlider.getFrameWidth()) {
				BitmapDrawable bd = (BitmapDrawable) minTrackDrawable;
				bd.setBounds(0, 0, mKNXSlider.getFrameWidth(), DEFAULT_HORIZONTAL_SEEK_BAR_HEIGHT);
				bd.setGravity(Gravity.LEFT);
				clipImage = true;
			}
			*/

			Drawable[] lda = { maxTrackDrawable, new ClipDrawable(minTrackDrawable, Gravity.LEFT, ClipDrawable.HORIZONTAL) };
			LayerDrawable ld = new LayerDrawable(lda);
			ld.setId(0, android.R.id.background);
			ld.setId(1, android.R.id.progress);
			if (ld.getIntrinsicHeight() < DEFAULT_HORIZONTAL_SEEK_BAR_HEIGHT) {
				horizontalSeekBar.setMaxHeight(ld.getIntrinsicHeight());
			}
			horizontalSeekBar.setProgressDrawable(ld);
		}
		if (!clipImage) {
			horizontalSeekBar.setMaxHeight(SEEK_BAR_MAX_SIZE);
		}
		horizontalSeekBar.setMax(SEEK_BAR_PROGRESS_MAX);
		horizontalSeekBar.setProgress(getProgressOfBusinessValue(SEEK_BAR_PROGRESS_INIT_VALUE));

		//horizontalSeekBar.setLayoutParams(new TableRow.LayoutParams(mKNXSlider.getFrameWidth(), SEEK_BAR_MIN_HEIGHT));
		if (mKNXSlider.getSliderImage() != null) {
			Drawable thumbDrawable = ImageUtil.createFromPathQuietly(STKNXControllerConstant.FILE_FOLDER_PATH + mKNXSlider.getSliderImage());
			horizontalSeekBar.setThumb(thumbDrawable);
		}

		/*
		if (mKNXSlider.getMinImage() != null) {
			Drawable minValueDrawable = ImageUtil.createFromPathQuietly(ZyyKNXConstant.FILE_FOLDER_PATH + mKNXSlider.getMinImage());

			ImageView minValueImageView = (ImageView) seekBarRootView.findViewById(R.id.horizontal_seekbar_minvalue_image);
			minValueImageView.setImageDrawable(minValueDrawable);
			minValueImageView.setLayoutParams(new TableRow.LayoutParams(SEEK_BAR_MIN_IMAGE_WIDTH, SEEK_BAR_MIN_IMAGE_HEIGHT));

			horizontalSeekBar.setLayoutParams(new TableRow.LayoutParams(mKNXSlider.getFrameWidth() - SEEK_BAR_MIN_IMAGE_WIDTH, SEEK_BAR_MIN_HEIGHT));
		}

		if (mKNXSlider.getMaxImage() != null) {
			Drawable maxValueDrawable = ImageUtil.createFromPathQuietly(ZyyKNXConstant.FILE_FOLDER_PATH + mKNXSlider.getMaxImage());

			ImageView maxValueImageView = (ImageView) seekBarRootView.findViewById(R.id.horizontal_seekbar_maxvalue_image);
			maxValueImageView.setImageDrawable(maxValueDrawable);
			maxValueImageView.setLayoutParams(new TableRow.LayoutParams(SEEK_BAR_MAX_IMAGE_WIDTH, SEEK_BAR_MAX_IMAGE_HEIGHT));

			if (mKNXSlider.getMinImage() != null) {
				horizontalSeekBar.setLayoutParams(new TableRow.LayoutParams(mKNXSlider.getFrameWidth() - SEEK_BAR_MIN_IMAGE_WIDTH - SEEK_BAR_MAX_IMAGE_WIDTH, SEEK_BAR_MIN_HEIGHT));
			} else {
				horizontalSeekBar.setLayoutParams(new TableRow.LayoutParams(mKNXSlider.getFrameWidth() - SEEK_BAR_MAX_IMAGE_WIDTH, SEEK_BAR_MIN_HEIGHT));
			}
		}
		*/
		
		horizontalSeekBar.setOnSeekBarChangeListener(this);
		this.addView(seekBarRootView);
	}

	private int getProgressOfBusinessValue(int businessValue) {
		if (mKNXSlider.getMaxValue() == 0 || mKNXSlider.getMaxValue() == mKNXSlider.getMinValue()) {
			return 0;
		}
		double progress = SEEK_BAR_PROGRESS_MAX * ((float) (businessValue - mKNXSlider.getMinValue()) / (mKNXSlider.getMaxValue() - mKNXSlider.getMinValue()));
		return (int) NumberFormat.format(0, progress);
	}

	@Override
	public void onProgressChanged(ORSeekBar seekBar, int progress, boolean fromUser) {
		slideToBusinessValue = (int) (((float) progress / SEEK_BAR_PROGRESS_MAX) * (mKNXSlider.getMaxValue() - mKNXSlider.getMinValue()) + mKNXSlider.getMinValue());
	}

	@Override
	public void onStartTrackingTouch(ORSeekBar seekBar) {
		
	}

	@Override
	public void onStopTrackingTouch(ORSeekBar seekBar) {

		//sendCommandRequest(String.valueOf(slideToBusinessValue));
	}

}