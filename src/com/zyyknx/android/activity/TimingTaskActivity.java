package com.zyyknx.android.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import com.zyyknx.android.R;
import com.zyyknx.android.ZyyKNXApp;
import com.zyyknx.android.ZyyKNXConstant;
import com.zyyknx.android.adapter.AddressAndActionAdapter;
import com.zyyknx.android.adapter.GroupAddressAdapter;
import com.zyyknx.android.adapter.TimingTaskListAdapter;
import com.zyyknx.android.control.KNXControlBase;
import com.zyyknx.android.control.KNXTimerButton;
import com.zyyknx.android.control.TimingTaskItem;
import com.zyyknx.android.control.TimingTaskItem.KNXGroupAddressAndAction;
import com.zyyknx.android.models.KNXGroupAddress;
import com.zyyknx.android.models.KNXGroupAddressAction;
import com.zyyknx.android.models.KNXSelectedAddress;
import com.zyyknx.android.util.Log;
import com.zyyknx.android.widget.CustomDateAndTimeDialog;
import com.zyyknx.android.widget.CustomDateAndTimeDialog.OnDateAndTimeChangedListener;
import com.zyyknx.android.widget.CustomDateAndTimeDialog.OnTimeChangedListener;
import com.zyyknx.android.widget.CustomTimerPickerDialog;
import com.zyyknx.android.widget.CustomTimerPickerDialog.OnTimerChangedListener;
import com.zyyknx.android.widget.PromptDialog;
import com.zyyknx.android.widget.TimeAndWeekdayPickerDialog;
import com.zyyknx.android.widget.TimeAndWeekdayPickerDialog.OnChangedListener;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class TimingTaskActivity extends BaseActivity {
	private ListView lvTimingTaskList;
	private RadioButton rbMonthly;
	private RadioButton rbWeekly;
	private RadioButton rbEveryday;
	private RadioButton rbCycle;
	private RadioButton rbOneOff;
	private TextView tvExecuteWayDetail;
	private TimingTaskItem oldTask = null;
    private TimingTaskItem currentTask;
    private ListView lvGroupAddressList;
    private Button bDeleteTask;
    private Button bCreateTask;
    private Button bAddOrRemoveGroupAddress;
    private TimingTaskListAdapter mTimingTaskListAdapter = null;
    private Button bSaveModify;
    private List<TimingTaskItem> timingTaskList;
    private String fileName;
    private AddressAndActionAdapter mAddressAndActionAdapter;
    private RefreshTimingTaskListReceiver mRefreshTimingTaskListReceiver;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.timing_task_layout);

		/* 获取当前定时器的定时任务列表 */
		fileName = getIntent().getStringExtra(ZyyKNXConstant.CONTROL_ID); // 获取当前定时器的ID
		Map<String, List<TimingTaskItem>> timerTaskMap = ZyyKNXApp.getInstance().getTimerTaskMap(); // 获取当前页面的定时器
		timingTaskList = timerTaskMap.get(fileName); // 根据定时器ID获取定时任务列表
		if(null == timingTaskList) {
			/* 如为新建的定时器 */
			timingTaskList = new ArrayList<TimingTaskItem>();
			timerTaskMap.put(fileName, timingTaskList);
		}
		
		Log.i(ZyyKNXConstant.DEBUG, "  isMainThread:"+(Thread.currentThread() == Looper.getMainLooper().getThread()));
		
		TextView tvTimerName = (TextView)findViewById(R.id.timingTaskLayoutTimerName);
		try {
			int controlId = Integer.parseInt(fileName);
			KNXControlBase control = ZyyKNXApp.getInstance().getCurrentPageKNXControlBaseMap().get(controlId);
			if(control instanceof KNXTimerButton) {
				tvTimerName.setText(control.getText());
			} else {
				tvTimerName.setText("");
			}
		} catch (Exception e) {
			Log.e(e.getLocalizedMessage());
			tvTimerName.setText("");
		}

		/* ListView 定时任务列表 */
		lvTimingTaskList = (ListView)findViewById(R.id.timingTaskLayoutTimingTaskList);
        mTimingTaskListAdapter = new TimingTaskListAdapter(this, timingTaskList);
		lvTimingTaskList.setAdapter(mTimingTaskListAdapter);
		lvTimingTaskList.setOnItemClickListener(new TimingTaskListOnItemClickListener());

		/* Button 删除任务按钮 */
		bDeleteTask = (Button)findViewById(R.id.buttonDeleteTask);
		bDeleteTask.setEnabled(false);
		bDeleteTask.setOnClickListener(new DeleteTaskOnClickListener());
		
		/* Button 创建任务按钮 */
		bCreateTask = (Button)findViewById(R.id.buttonCreateTask);
		bCreateTask.setOnClickListener(new CreateTaskOnClickListener());

		/* UITableView 定时任务执行方式 */
		rbMonthly = (RadioButton)findViewById(R.id.timingTaskLayoutExecuteWayMonthly);
		rbMonthly.setOnClickListener(new ExecuteWayMonthlyOnClickListener());
		rbWeekly = (RadioButton)findViewById(R.id.timingTaskLayoutExecuteWayWeekly);
		rbWeekly.setOnClickListener(new ExecuteWayWeeklyOnClickListener());
		rbEveryday = (RadioButton)findViewById(R.id.timingTaskLayoutExecuteWayEveryday);
		rbEveryday.setOnClickListener(new ExecuteWayEverydayOnClickListener());
		rbCycle = (RadioButton)findViewById(R.id.timingTaskLayoutExecuteWayCycle);
		rbCycle.setOnClickListener(new ExecuteWayCycleOnClickListener());
		rbOneOff = (RadioButton)findViewById(R.id.timingTaskLayoutExecuteWayOneOff);
		rbOneOff.setOnClickListener(new ExecuteWayOneOffOnClickListener());
		tvExecuteWayDetail = (TextView)findViewById(R.id.timingTaskLayoutExecuteWayDetail);

		/* ListView 组地址列表 */
        lvGroupAddressList = (ListView)findViewById(R.id.timingTaskLayoutGroupAddressList);
        mAddressAndActionAdapter = new AddressAndActionAdapter(TimingTaskActivity.this);
    	lvGroupAddressList.setAdapter(mAddressAndActionAdapter);
        lvGroupAddressList.setOnItemClickListener(new GroupAddressListOnItemClickListener());
        
        /* Button 添加移除组地址按钮 */
        bAddOrRemoveGroupAddress = (Button)findViewById(R.id.buttonAddOrRemoveGroupAddress);
        bAddOrRemoveGroupAddress.setOnClickListener(new AddOrRemoveGroupAddressClickListener());
        
        /* Button 保存任务按钮 */
        bSaveModify = (Button)findViewById(R.id.buttonSaveTask);
        bSaveModify.setOnClickListener(new SaveModifyClickListener());
        
        disableTimingTaskEditor();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		mRefreshTimingTaskListReceiver = new RefreshTimingTaskListReceiver();
        IntentFilter filter = new IntentFilter(ZyyKNXConstant.BROADCAST_REFRESH_TIMING_TASK_LIST);
        registerReceiver(mRefreshTimingTaskListReceiver, filter);  
	}
	
	@Override
	public void onPause() {
		super.onPause();
		
		Log.i(ZyyKNXConstant.ACTIVITY_JUMP, "...");
		
		ZyyKNXApp.getInstance().saveTimerTask();
		
		unregisterReceiver(mRefreshTimingTaskListReceiver);
	}

	/**
	 *  刷新定时任务执行方式的界面 
	 */
  	private void refreshThisUI(TimingTaskItem item) {
  		refreshExecuteWay(item);
  		refreshExecuteWayDetail(item);

    	mAddressAndActionAdapter.setAddressAndActionList(item.getAddressList());
    	mAddressAndActionAdapter.notifyDataSetChanged(); // 刷新任务的组地址和行为列表
	}
  	
  	private void refreshExecuteWay(TimingTaskItem item) {
  		rbMonthly.setChecked(item.isMonthlySelected());
		rbWeekly.setChecked(item.isWeeklySelected());
		rbEveryday.setChecked(item.isEverydaySelected());
		rbCycle.setChecked(item.isLoopSelected());
		rbOneOff.setChecked(item.isOneOffSelected());
  	}
  	
  	private void refreshExecuteWayDetail(TimingTaskItem item) {
  		if(item.isMonthlySelected()) {
  			String text = getMonthlyContent(item);
			tvExecuteWayDetail.setText(text);
		} else if (item.isWeeklySelected()) {
			String text = getWeeklyContent(item);
			tvExecuteWayDetail.setText(text);
		} else if (item.isEverydaySelected()) {
			String text = getEverydayContent(item);
			tvExecuteWayDetail.setText(text);
		} else if (item.isLoopSelected()) {
			String text = getCycleContent(item);
			tvExecuteWayDetail.setText(text);
		} else if (item.isOneOffSelected()) {
			String text = getOneOffContent(item);
			tvExecuteWayDetail.setText(text);
		}
  	}
	
	private String getMonthlyContent(TimingTaskItem item) {
		String text = String.format("%s: %02d%s  %02d%s%02d%s", getResources().getString(R.string.monthly), 
				item.getDay(), getResources().getString(R.string.day),
				item.getHour(), getResources().getString(R.string.hour),
				item.getMinute(), getResources().getString(R.string.minute));
		return text;
	}
	
	private String getWeeklyContent(TimingTaskItem item) {
		String weekly = String.format("%s: %s", getResources().getString(R.string.weekly), item.getWeekly(this)) ;
		String time = String.format("%02d%s%02d%s", item.getHour(), getResources().getString(R.string.hour),
				item.getMinute(), getResources().getString(R.string.minute));
		return weekly + " " + time;
	}
	
	private String getEverydayContent(TimingTaskItem item) {
		String text = String.format("%s: %02d%s%02d%s", getResources().getString(R.string.everyday),
				item.getHour(), getResources().getString(R.string.hour),
				item.getMinute(), getResources().getString(R.string.minute));
		return text;
	}
	
	private String getCycleContent(TimingTaskItem item) {
		String text = String.format("%s: %d%s%d%s%d%s", getResources().getString(R.string.cycle_interval), 
				item.getIntervalHour(), getResources().getString(R.string.unit_hour), 
				item.getIntervalMinute(), getResources().getString(R.string.unit_minute), 
				item.getIntervalSecond(), getResources().getString(R.string.second));
		return text;
	}
	
	private String getOneOffContent(TimingTaskItem item) {
		String text = String.format("%04d%s%02d%s%02d%s  %02d%s%02d%s", item.getYear(), getResources().getString(R.string.year), 
				item.getMonth(), getResources().getString(R.string.month), 
				item.getDay(), getResources().getString(R.string.day),
				item.getHour(), getResources().getString(R.string.hour),
				item.getMinute(), getResources().getString(R.string.minute));
		return text;
	}
	
	private void enableTimingTaskEditor() {
		rbMonthly.setEnabled(true);
		rbWeekly.setEnabled(true);
		rbEveryday.setEnabled(true);
		rbCycle.setEnabled(true);
		rbOneOff.setEnabled(true);
		tvExecuteWayDetail.setEnabled(true);
	    lvGroupAddressList.setEnabled(true);
	    bAddOrRemoveGroupAddress.setEnabled(true);
	    bSaveModify.setEnabled(true);
	}
	
	private void disableTimingTaskEditor() {
		rbMonthly.setEnabled(false);
		rbWeekly.setEnabled(false);
		rbEveryday.setEnabled(false);
		rbCycle.setEnabled(false);
		rbOneOff.setEnabled(false);
		tvExecuteWayDetail.setEnabled(false);
	    lvGroupAddressList.setEnabled(false);
	    bAddOrRemoveGroupAddress.setEnabled(false);
	    bSaveModify.setEnabled(false);
	}

	/**
	 *  定时任务列表选项点击事件：编辑定时任务
	 */
	private class TimingTaskListOnItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			oldTask = (TimingTaskItem)parent.getAdapter().getItem(position);
			currentTask = oldTask.clone();

			refreshThisUI(currentTask);
			
			enableTimingTaskEditor();

			bDeleteTask.setEnabled(true);

			mTimingTaskListAdapter.setSelectedPosition(position);
			mTimingTaskListAdapter.notifyDataSetInvalidated(); 
		}
		
	}
	
	/**
	 *  删除定时任务按钮点击事件
	 */
	private class DeleteTaskOnClickListener implements OnClickListener {
		
		@Override
		public void onClick(View v) {
			if((null != oldTask) && (timingTaskList.contains(oldTask))) {
				new AlertDialog.Builder(TimingTaskActivity.this) 
				.setTitle(R.string.prompt)
				.setMessage(R.string.are_you_sure_to_delete_the_task)
				.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {

						timingTaskList.remove(oldTask);
						mTimingTaskListAdapter.setSelectedPosition(-1);
						mTimingTaskListAdapter.notifyDataSetInvalidated(); 
						
						ZyyKNXApp.getInstance().saveTimerTask();
						
						bDeleteTask.setEnabled(false);
						disableTimingTaskEditor();

						oldTask= null;
					}
					
				})
				.setNegativeButton(R.string.cancel, null)
				.show();
				
			}
		}
	}
	
	/**
	 *  创建定时任务按钮点击事件
	 */
	private class CreateTaskOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			oldTask = null;
			currentTask = new TimingTaskItem();
			refreshThisUI(currentTask);
			popGroupAddressSelectWindow(TimingTaskActivity.this, getResources().getString(R.string.add_address_to_task), true);

			enableTimingTaskEditor();
			
			bDeleteTask.setEnabled(false);
		}
		
	}

	/**
	 * 任务的执行方式点击事件：每月
	 * */
	private class ExecuteWayMonthlyOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			rbWeekly.setChecked(false);
			rbEveryday.setChecked(false);
			rbCycle.setChecked(false);
			rbOneOff.setChecked(false);
			
			final CustomDateAndTimeDialog dialogMonthly = new CustomDateAndTimeDialog(TimingTaskActivity.this);
			dialogMonthly.setYearGone();
			dialogMonthly.setMonthGone();
			dialogMonthly.setDate(currentTask.getYear(), currentTask.getMonth()-1, currentTask.getDay());
			dialogMonthly.setTime(true, currentTask.getHour(), currentTask.getMinute());
			dialogMonthly.setOnDateAndTimeChangedListener(new OnDateAndTimeChangedListener() {

				@Override
				public void onDateAndTimeChanged(CustomDateAndTimeDialog dialog, int year, int monthOfYear,
						int dayOfMonth, int hourOfDay, int minute) {
					currentTask.setDay(dayOfMonth);
					currentTask.setHour(hourOfDay);
					currentTask.setMinute(minute);
					
					final String text = getMonthlyContent(currentTask);

					dialog.setTitle(text);
				}
				
			});
			dialogMonthly.setTitle(getMonthlyContent(currentTask));
			dialogMonthly.setIcon(R.drawable.launcher);
			dialogMonthly.setViewStyle(PromptDialog.VIEW_STYLE_NORMAL);
			dialogMonthly.setButton1(R.string.confirm,  new PromptDialog.OnClickListener() {
					
				@Override
				public void onClick(Dialog dialog, int which) {
					currentTask.setMonthly(true);
					currentTask.setWeekly(false);
					currentTask.setEveryday(false);
					currentTask.setLoop(false);
					currentTask.setOneOff(false);

//					currentTask.setDay(dialogMonthly.getDay());
//					currentTask.setHour(dialogMonthly.getHour());
//					currentTask.setMinute(dialogMonthly.getMinute());
						
					refreshExecuteWayDetail(currentTask);
					
					dialog.dismiss();
				}
			});
			dialogMonthly.setButton2(R.string.cancel, new PromptDialog.OnClickListener() {
							
				@Override
				public void onClick(Dialog dialog, int which) {
					refreshExecuteWay(currentTask);
					dialog.dismiss();
				}
			})
			.show();
		}
		
	}
	
	/**
	 * 任务的执行方式点击事件：每周
	 * */
	private class ExecuteWayWeeklyOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			rbMonthly.setChecked(false);
			rbEveryday.setChecked(false);
			rbCycle.setChecked(false);
			rbOneOff.setChecked(false);
			
			final TimeAndWeekdayPickerDialog dialogWeekly = new TimeAndWeekdayPickerDialog(TimingTaskActivity.this);
			dialogWeekly.setTime(true, currentTask.getHour(), currentTask.getMinute());
			dialogWeekly.setMondayChecked(currentTask.isMondaySelected());
			dialogWeekly.setTuesdayChecked(currentTask.isTuesdaySelected());
			dialogWeekly.setWednesdayChecked(currentTask.isWednesdaySelected());
			dialogWeekly.setThursdayChecked(currentTask.isThursdaySelected());
			dialogWeekly.setFridayChecked(currentTask.isFridaySelected());
			dialogWeekly.setSaturdayChecked(currentTask.isSaturdaySelected());
			dialogWeekly.setSundayChecked(currentTask.isSundaySelected());
			dialogWeekly.setOnChangedListener(new OnChangedListener() {

				@Override
				public void onChangedListener(TimeAndWeekdayPickerDialog dialog, int hourOfDay, int minute, boolean isMondaySelected,
						boolean isTuesdaySelected, boolean isWednesdaySelected, boolean isThursdaySelected,
						boolean isFridaySelected, boolean isSaturdaySelected, boolean isSundaySelected) {
					currentTask.setHour(hourOfDay);
					currentTask.setMinute(minute);
					currentTask.setMonday(isMondaySelected);
					currentTask.setTuesday(isTuesdaySelected);
					currentTask.setWednesday(isWednesdaySelected);
					currentTask.setThursday(isThursdaySelected);
					currentTask.setFriday(isFridaySelected);
					currentTask.setSaturday(isSaturdaySelected);
					currentTask.setSunday(isSundaySelected);
					
					final String text = getWeeklyContent(currentTask);

					dialog.setTitle(text);
				}
				
			});
			dialogWeekly.setTitle(getWeeklyContent(currentTask));
			dialogWeekly.setIcon(R.drawable.launcher);
			dialogWeekly.setViewStyle(PromptDialog.VIEW_STYLE_NORMAL);
			dialogWeekly.setButton1(R.string.confirm,  new PromptDialog.OnClickListener() {
							
				@Override
				public void onClick(Dialog dialog, int which) {
					currentTask.setMonthly(false);
					currentTask.setWeekly(true);
					currentTask.setEveryday(false);
					currentTask.setLoop(false);
					currentTask.setOneOff(false);
						
					currentTask.setHour(dialogWeekly.getHour());
					currentTask.setMinute(dialogWeekly.getMinute());
					currentTask.setMonday(dialogWeekly.getMondayChecked());
					currentTask.setTuesday(dialogWeekly.getTuesdayChecked());
					currentTask.setWednesday(dialogWeekly.getWednesdayChecked());
					currentTask.setThursday(dialogWeekly.getThursdayChecked());
					currentTask.setFriday(dialogWeekly.getFridayChecked());
					currentTask.setSaturday(dialogWeekly.getSaturdayChecked());
					currentTask.setSunday(dialogWeekly.getSundayChecked());
						
					refreshExecuteWayDetail(currentTask);
					
					dialog.dismiss();
				}
			});
			dialogWeekly.setButton2(R.string.cancel, new PromptDialog.OnClickListener() {
							
				@Override
				public void onClick(Dialog dialog, int which) {
					refreshExecuteWay(currentTask);
					dialog.dismiss();
				}
			})
			.show();
		}
		
	}
	
	/**
	 * 任务的执行方式点击事件：每天 
	 **/
	private class ExecuteWayEverydayOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			rbMonthly.setChecked(false);
			rbWeekly.setChecked(false);
			rbCycle.setChecked(false);
			rbOneOff.setChecked(false);
			
			final CustomDateAndTimeDialog dialogEveryday = new CustomDateAndTimeDialog(TimingTaskActivity.this);
			dialogEveryday.setDatePickerGone();
			dialogEveryday.setTime(true, currentTask.getHour(), currentTask.getMinute());
			dialogEveryday.setOnTimeChangedListener(new OnTimeChangedListener() {

				@Override
				public void onTimeChanged(CustomDateAndTimeDialog dialog, int hourOfDay, int minute) {
					currentTask.setHour(hourOfDay);
					currentTask.setMinute(minute);
					
					final String text = getEverydayContent(currentTask);

					dialog.setTitle(text);
				}
				
			});
			dialogEveryday.setTitle(getEverydayContent(currentTask));
			dialogEveryday.setIcon(R.drawable.launcher);
			dialogEveryday.setViewStyle(PromptDialog.VIEW_STYLE_NORMAL);
			dialogEveryday.setButton1(R.string.confirm,  new PromptDialog.OnClickListener() {
							
				@Override
				public void onClick(Dialog dialog, int which) {
					currentTask.setMonthly(false);
					currentTask.setWeekly(false);
					currentTask.setEveryday(true);
					currentTask.setLoop(false);
					currentTask.setOneOff(false);
						
//					currentTask.setHour(dialogEveryday.getHour());
//					currentTask.setMinute(dialogEveryday.getMinute());
						
					refreshExecuteWayDetail(currentTask);
					
					dialog.dismiss(); 
				}
			});
			dialogEveryday.setButton2(R.string.cancel, new PromptDialog.OnClickListener() {
							
				@Override
				public void onClick(Dialog dialog, int which) {
					refreshExecuteWay(currentTask);
						dialog.dismiss();
				}
			})
			.show();
		}
		
	}
	
	/**
	 * 任务的执行方式点击事件：循环
	 **/
	private class ExecuteWayCycleOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			rbMonthly.setChecked(false);
			rbWeekly.setChecked(false);
			rbEveryday.setChecked(false);
			rbOneOff.setChecked(false);
			
			final CustomTimerPickerDialog dialogTime = new CustomTimerPickerDialog(TimingTaskActivity.this);
			dialogTime.setHour(currentTask.getIntervalHour());
			dialogTime.setMinute(currentTask.getIntervalMinute());
			dialogTime.setSecond(currentTask.getIntervalSecond());
			dialogTime.setOnTimerChangedListener(new OnTimerChangedListener() {

				@Override
				public void onTimerChangedListener(CustomTimerPickerDialog dialog, int hour, int minute, int second) {
					currentTask.setIntervalHour(hour);
					currentTask.setIntervalMinute(minute);
					currentTask.setIntervalSecond(second);
					
					final String text = getCycleContent(currentTask);

					dialog.setTitle(text);
				}
				
			});
			dialogTime.setTitle(getCycleContent(currentTask));
			dialogTime.setIcon(R.drawable.launcher);
			dialogTime.setViewStyle(PromptDialog.VIEW_STYLE_NORMAL);
			dialogTime.setButton1(R.string.confirm,  new PromptDialog.OnClickListener() {
							
				@Override
				public void onClick(Dialog dialog, int which) {

					currentTask.setMonthly(false);
					currentTask.setWeekly(false);
					currentTask.setEveryday(false);
					currentTask.setLoop(true);
					currentTask.setOneOff(false);
						
					Calendar cal = Calendar.getInstance();
						
					currentTask.setHour(cal.get(Calendar.HOUR_OF_DAY));
					currentTask.setMinute(cal.get(Calendar.MINUTE));
					currentTask.setSecond(cal.get(Calendar.SECOND));
						
//					currentTask.setIntervalHour(dialogTime.getHour());
//					currentTask.setIntervalMinute(dialogTime.getMinute());
//					currentTask.setIntervalSecond(dialogTime.getSecond());
					
					refreshExecuteWayDetail(currentTask);
					
					dialog.dismiss(); 
				}
			});
			dialogTime.setButton2(R.string.cancel, new PromptDialog.OnClickListener() {
							
				@Override
				public void onClick(Dialog dialog, int which) {
					refreshExecuteWay(currentTask);
					dialog.dismiss();
				}
			})
			.show();
		}
		
	}
	
	/**
	 * 任务的执行方式点击事件：一次性
	 * */
	private class ExecuteWayOneOffOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			rbMonthly.setChecked(false);
			rbWeekly.setChecked(false);
			rbEveryday.setChecked(false);
			rbCycle.setChecked(false);
			
			final CustomDateAndTimeDialog dialogOneOff = new CustomDateAndTimeDialog(TimingTaskActivity.this);
			dialogOneOff.setDate(currentTask.getYear(), currentTask.getMonth()-1, currentTask.getDay());
			dialogOneOff.setTime(true, currentTask.getHour(), currentTask.getMinute());
			dialogOneOff.setOnDateAndTimeChangedListener(new OnDateAndTimeChangedListener() {

				@Override
				public void onDateAndTimeChanged(CustomDateAndTimeDialog dialog, int year, int monthOfYear,
						int dayOfMonth, int hourOfDay, int minute) {
					currentTask.setYear(year);
					currentTask.setMonth(monthOfYear+1);
					currentTask.setDay(dayOfMonth);
					currentTask.setHour(hourOfDay);
					currentTask.setMinute(minute);
					
					final String text = getOneOffContent(currentTask);

					dialog.setTitle(text);

				}
				
			});
			dialogOneOff.setTitle(getOneOffContent(currentTask));
			dialogOneOff.setIcon(R.drawable.launcher);
			dialogOneOff.setViewStyle(PromptDialog.VIEW_STYLE_NORMAL);
			dialogOneOff.setButton1(R.string.confirm,  new PromptDialog.OnClickListener() {
							
				@Override
				public void onClick(Dialog dialog, int which) {
					
					currentTask.setMonthly(false);
					currentTask.setWeekly(false);
					currentTask.setEveryday(false);
					currentTask.setLoop(false);
					currentTask.setOneOff(true);
						
//					currentTask.setYear(dialogOneOff.getYear());
//					currentTask.setMonth(dialogOneOff.getMonth()+1);
//					currentTask.setDay(dialogOneOff.getDay());
//					currentTask.setHour(dialogOneOff.getHour());
//					currentTask.setMinute(dialogOneOff.getMinute());
						
					refreshExecuteWayDetail(currentTask);
					
					dialog.dismiss(); 
				}
			});
			dialogOneOff.setButton2(R.string.cancel, new PromptDialog.OnClickListener() {
							
				@Override
				public void onClick(Dialog dialog, int which) {
					refreshExecuteWay(currentTask);
					dialog.dismiss();
				}
			})
			.show();
		}
		
	}

	/**
	 *  组地址列表点击事件：编辑组地址的执行行为 
	 */
 	private class GroupAddressListOnItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			final KNXGroupAddressAndAction addressAndAction = (KNXGroupAddressAndAction)arg0.getAdapter().getItem(arg2);
			LayoutInflater mLayoutInflater = LayoutInflater.from(arg0.getContext());
			View view = mLayoutInflater.inflate(R.layout.datapoint_action_layout, null);
			final RadioButton rbGetStatus = (RadioButton)view.findViewById(R.id.datapointActionLayoutRadioButtonGetStatus);
			final RadioButton rbDefaultAction = (RadioButton)view.findViewById(R.id.radioButtonDefaultAction);
			final Spinner actionSpinner = (Spinner)view.findViewById(R.id.spinnerActions);
			final RadioButton rbCustomAction = (RadioButton)view.findViewById(R.id.radioButtonCustomAction);
			final Button bHelper = (Button)view.findViewById(R.id.buttonDatapointActionLayoutHelper);
			final TextView tvActionName = (TextView)view.findViewById(R.id.textViewActionItemActionName);
			final EditText etActionName = (EditText)view.findViewById(R.id.editTextActionName);
			etActionName.clearFocus();
			final TextView tvActionParam = (TextView)view.findViewById(R.id.textViewActionParameter);
			final EditText etActionValue = (EditText)view.findViewById(R.id.editTextActionValue);
			etActionValue.clearFocus();
			final TextView tvHint = (TextView)view.findViewById(R.id.textViewDatapointActionLayoutHint);
			rbGetStatus.requestFocus();

			ArrayAdapter<String> actionSpinnerAdapter = new ArrayAdapter<String>(arg0.getContext(), android.R.layout.simple_spinner_item);
			actionSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			KNXGroupAddressAction currentAction = addressAndAction.getAction();
			String actionName = null;
			if(null != currentAction) {
				actionName = currentAction.getName();
			}
			List<KNXGroupAddressAction> defaultActions = addressAndAction.getAddress().getAddressAction();
			int selectedIndex = -1;
			boolean defaultActionsIsValid = true;
			if(null != defaultActions) { // 该地址有可供选择的默认行为列表
				for(int i=0; i<defaultActions.size(); i++) {
					String name = defaultActions.get(i).getName();
					actionSpinnerAdapter.add(name);
					if(null != actionName) {
						if(actionName.equals(name)) { // 该地址设置的行为为默认行为，记录下索引号
							selectedIndex = i;
						}
					}
				}
				
				actionSpinner.setAdapter(actionSpinnerAdapter);
			} else {
				defaultActionsIsValid = false;
			}
			
			if(addressAndAction.getIsRead()) { // 读取状态
				rbGetStatus.setChecked(true);
				
				rbDefaultAction.setChecked(false);
				actionSpinner.setEnabled(false);
				
				rbCustomAction.setChecked(false);
				tvActionName.setEnabled(false);
				etActionName.setEnabled(false);
				tvActionParam.setEnabled(false);
				etActionValue.setEnabled(false);
			} else if(0 <= selectedIndex) { // 已经设置了行为，且为默认行为
				rbGetStatus.setChecked(false);
				
				/* 默认行为可用，且选中 */
				rbDefaultAction.setChecked(true);
				rbDefaultAction.setEnabled(true);
				actionSpinner.setEnabled(true);
				actionSpinner.setSelection(selectedIndex, true); // 在Spinner中选中该地址的行为
				
				/* 自定义行为可用 ，但不选中*/
				rbCustomAction.setChecked(false);
				rbCustomAction.setEnabled(true);
				tvActionName.setEnabled(false);
				etActionName.setEnabled(false);
				etActionName.clearFocus();
				tvActionParam.setEnabled(false);
				etActionValue.setEnabled(false);
				etActionValue.clearFocus();
			} else { // 
				
				rbGetStatus.setChecked(false);

				rbDefaultAction.setChecked(false);
				actionSpinner.setEnabled(false);
				if(defaultActionsIsValid) { // 有默认的行为可供选择
					rbDefaultAction.setEnabled(true); // 默认行为可用
				} else {
					rbDefaultAction.setEnabled(false); // 默认行为不可用
				}
				
				if(null != currentAction) {
					rbCustomAction.setChecked(true);
					
					etActionName.setText(actionName);
					etActionValue.setText(String.valueOf(addressAndAction.getAction().getValue()));
				} else {
					rbCustomAction.setChecked(false);
				}
				
				/* 自定义行为可用，且选中 */
				rbCustomAction.setEnabled(true);
				tvActionName.setEnabled(true);
				etActionName.setEnabled(true);
				tvActionParam.setEnabled(true);
				etActionValue.setEnabled(true);
			}
			
//			if (null != addressAndAction.getAction()) { // 已对该地址设置了行为
//				String actionName = addressAndAction.getAction().getName();
//				List<KNXGroupAddressAction> defaultActions = addressAndAction.getAddress().getAddressAction();
//				if(null != defaultActions) { // 该地址有可供选择的默认行为列表
//					int selectedIndex = -1;
//					for(int i=0; i<defaultActions.size(); i++) {
//						String name = defaultActions.get(i).getName();
//						actionSpinnerAdapter.add(name);
//						if(actionName.equals(name)) { // 该地址设置的行为为默认行为，记录下索引号
//							selectedIndex = i;
//						}
//					}
//					
//					actionSpinner.setAdapter(actionSpinnerAdapter);
//					
//					Log.i(ZyyKNXConstant.DEBUG, "selectedIndex:"+selectedIndex);
//					
//					if (selectedIndex >= 0) { // 该地址的行为为默认行为列表中的选项
//						rbGetStatus.setChecked(false);
//						
//						/* 默认行为可用，且选中 */
//						rbDefaultAction.setChecked(true);
//						rbDefaultAction.setEnabled(true);
//						actionSpinner.setEnabled(true);
//						
//						/* 自定义行为可用 ，但不选中*/
//						rbCustomAction.setChecked(false);
//						rbCustomAction.setEnabled(true);
//						tvActionName.setEnabled(false);
//						etActionName.setEnabled(false);
//						etActionName.clearFocus();
//						tvActionParam.setEnabled(false);
//						etActionValue.setEnabled(false);
//						etActionValue.clearFocus();
//						
//						actionSpinner.setSelection(selectedIndex, true); // 在Spinner中选中该地址的行为
//					} else { // 该地址的行为为自定义行为
//						rbGetStatus.setChecked(false);
//						
//						/* 默认行为可用，但不选中 */
//						rbDefaultAction.setChecked(false);
//						rbDefaultAction.setEnabled(true);
//						actionSpinner.setEnabled(false);
//						
//						/* 自定义行为可用，且选中 */
//						rbCustomAction.setChecked(true);
//						rbCustomAction.setEnabled(true);
//						tvActionName.setEnabled(true);
//						etActionName.setEnabled(true);
//						etActionName.clearFocus();;
//						tvActionParam.setEnabled(true);
//						etActionValue.setEnabled(true);
//						etActionValue.clearFocus();
//						
//						etActionName.setText(actionName);
//						etActionValue.setText(String.valueOf(addressAndAction.getAction().getValue()));
//					}
//				} else { // 该地址无可供选择的默认行为列表
//					rbGetStatus.setChecked(false);
//					
//					/* 默认行为不可用，且不选中 */
//					rbDefaultAction.setChecked(false);
//					rbDefaultAction.setEnabled(false);
//					actionSpinner.setEnabled(false);
//					
//					/* 自定义行为可用，且选中 */
//					rbCustomAction.setChecked(true);
//					rbCustomAction.setEnabled(true);
//					tvActionName.setEnabled(true);
//					etActionName.setEnabled(true);
//					etActionName.clearFocus();
//					tvActionParam.setEnabled(true);
//					etActionValue.setEnabled(true);
//					etActionValue.clearFocus();
//					
//					etActionName.setText(actionName);
//					etActionValue.setText(String.valueOf(addressAndAction.getAction().getValue()));
//				}
//			} else { // 该地址尚未设置行为
//				List<KNXGroupAddressAction> defaultActions = addressAndAction.getAddress().getAddressAction();
//				if(null != defaultActions) { // 该地址有可供选择的默认行为列表
//					
//					for(KNXGroupAddressAction action:defaultActions) {
//						actionSpinnerAdapter.add(action.getName());
//					}
//					
//					actionSpinner.setAdapter(actionSpinnerAdapter);
//
//					rbGetStatus.setChecked(false);
//					
//					/* 默认行为可用，但不选中 */
//					rbDefaultAction.setChecked(false);
//					rbDefaultAction.setEnabled(true);
//					actionSpinner.setEnabled(false);
//
//					/* 自定义行为可用，但不选中 */
//					rbCustomAction.setChecked(false);
//					rbCustomAction.setEnabled(true);
//					tvActionName.setEnabled(false);
//					etActionName.setEnabled(false);
//					etActionName.clearFocus();
//					tvActionParam.setEnabled(false);
//					etActionValue.setEnabled(false);
//					etActionValue.clearFocus();
//				} else { // 该地址无可供选择的默认行为列表
//					rbGetStatus.setChecked(false);
//					
//					/* 默认行为不可用，且不选中 */
//					rbDefaultAction.setChecked(false);
//					rbDefaultAction.setEnabled(false);
//					actionSpinner.setEnabled(false);
//					
//					/* 自定义行为可用，但不选中 */
//					rbCustomAction.setChecked(false);
//					rbCustomAction.setEnabled(true);
//					tvActionName.setEnabled(false);
//					etActionName.setEnabled(false);
//					etActionName.clearFocus();
//					tvActionParam.setEnabled(false);
//					etActionValue.setEnabled(false);
//					etActionValue.clearFocus();
//				}
//			}

			/* 读取对象的状态 */
			rbGetStatus.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if (isChecked) {
						rbDefaultAction.setChecked(false);
						actionSpinner.setEnabled(false);
						rbCustomAction.setChecked(false);
						tvActionName.setEnabled(false);
						etActionName.setEnabled(false);
						tvActionParam.setEnabled(false);
						etActionValue.setEnabled(false);
						
						/* 收起虚拟键盘 */
						InputMethodManager imm =  (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE); 
						if(imm != null) { 
							imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0); 
						}
					}
				}
				
			});
			
			/* RadioButton 默认选项 */
			rbDefaultAction.setOnCheckedChangeListener(new OnCheckedChangeListener(){

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if (isChecked) {
						rbGetStatus.setChecked(false);
						actionSpinner.setEnabled(true);
						rbCustomAction.setChecked(false);
						tvActionName.setEnabled(false);
						etActionName.setEnabled(false);
						tvActionParam.setEnabled(false);
						etActionValue.setEnabled(false);
						
						/* 收起虚拟键盘 */
						InputMethodManager imm =  (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE); 
						if(imm != null) { 
							imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0); 
						}
					} else {
						actionSpinner.setEnabled(false);
					}
				}
			});
			
			/* RadioButton 自定义行为 */
			rbCustomAction.setOnCheckedChangeListener(new OnCheckedChangeListener(){

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					etActionName.clearFocus();
					etActionValue.clearFocus();
					if (isChecked) {
						rbGetStatus.setChecked(false);
						rbDefaultAction.setChecked(false);
						actionSpinner.setEnabled(false);
						tvActionName.setEnabled(true);
						etActionName.setEnabled(true);
						tvActionParam.setEnabled(true);
						etActionValue.setEnabled(true);
					} else {
						tvActionName.setEnabled(false);
						etActionName.setEnabled(false);
						tvActionParam.setEnabled(false);
						etActionValue.setEnabled(false);
					}
				}
				
			});
			
			bHelper.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					tvHint.setText(addressAndAction.getAddress().getTip());
				}
				
			});

			new PromptDialog.Builder(arg0.getContext())
			.setTitle(addressAndAction.getAddress().getName())
			.setIcon(R.drawable.launcher)
			.setViewStyle(PromptDialog.VIEW_STYLE_NORMAL)
			.setView(view)
			.setButton1(R.string.confirm,  new PromptDialog.OnClickListener() {
						
				@Override
				public void onClick(Dialog dialog, int which) {
					KNXGroupAddressAction selectedAction = null;
					boolean actionIsValid = true;
					
					if(rbGetStatus.isChecked()) {
						addressAndAction.setIsRead(true);
					} else {
						addressAndAction.setIsRead(false);
						
						if (rbDefaultAction.isChecked()) { // 设置的行为为默认行为
							if (null != addressAndAction.getAddress().getAddressAction()) { // 有默认行为列表
								String selectedActionName = (String)actionSpinner.getSelectedItem();
								for(KNXGroupAddressAction action : addressAndAction.getAddress().getAddressAction()) {
									if (action.getName().equals(selectedActionName)) {
										selectedAction = action;

										break;
									}
								}
							} else { // 无默认行为列表
								Toast.makeText(TimingTaskActivity.this, R.string.you_are_not_set_action_yet, 2).show();
								tvHint.setText(R.string.you_are_not_set_action_yet);
								actionIsValid = false;
							}
						} else if (rbCustomAction.isChecked()) { // 设置的行为为自定义行为
							if(etActionName.getText().toString().trim().isEmpty()) { // 行为名称为空
								actionIsValid = false; 
								etActionName.requestFocus(); // 行为名称框获取焦点
							}
							
							if (actionIsValid) {
								if(etActionValue.getText().toString().trim().isEmpty()) {
									actionIsValid = false;
									etActionValue.requestFocus(); // 行为参数框获取焦点
								}
							}
							
							String actionName = null;
							if (actionIsValid) {
								actionName = etActionName.getText().toString();
								List<KNXGroupAddressAction> defaultActions = addressAndAction.getAddress().getAddressAction();
									
								if (null != defaultActions) { // 有默认行为列表
									for(KNXGroupAddressAction action : defaultActions) {
										if (action.getName().equals(actionName)) { // 自定义的行为与默认的行为列表中的行为重名
											tvHint.setText(R.string.action_name_already_exits);
											actionIsValid = false; // action name 无效。重名
											etActionName.requestFocus(); // 行为名称框获取焦点
												
											break;
										}
									}
								}
							}
									
							if(actionIsValid) { // action name 有效

								int value = 0;
								try {
									value = Integer.parseInt(etActionValue.getText().toString());
								} catch(Exception e) {
									tvHint.setText(R.string.input_value_is_invalid);
									actionIsValid = false;
									etActionValue.requestFocus(); // 行为参数框获取焦点
								}
								
								if(actionIsValid) {
									if (1 == addressAndAction.getAddress().getType()) { // 1 Bit
						                if ((0 > value) || (1 < value)) {
						                	actionIsValid = false;
						                }
						            } else if (4 == addressAndAction.getAddress().getType()) { // 4 Bit
						                if ((0 > value) || (15 < value)) {
						                	actionIsValid = false;
						                }
						            } else if (8 == addressAndAction.getAddress().getType()) { // 1 Byte
						                if ((-127 > value) || (255 < value)) {
						                	actionIsValid = false;
						                }

						                if ((5 == addressAndAction.getAddress().getKnxMainNumber()) && 
						                		(1 == addressAndAction.getAddress().getKnxSubNumber())) { // 5.001
						                    if ((0 > value) || (255 < value)) {
						                    	actionIsValid = false;
						                    }
						                } else if ((18 == addressAndAction.getAddress().getKnxMainNumber()) &&
						                		(1 == addressAndAction.getAddress().getKnxSubNumber())) { // 18.001
						                    if ((0 > value) || (255 < value)) {
						                    	actionIsValid = false;
						                    }
						                }
						            } 
									
									if(actionIsValid) {
										selectedAction = new KNXGroupAddressAction(actionName, value);
										actionIsValid = true;
									} else {
										tvHint.setText(R.string.input_value_is_invalid);
										actionIsValid = false;
										etActionValue.requestFocus(); // 行为参数框获取焦点
									}
								}
							}
						}
					}
							
					if(actionIsValid) {
						addressAndAction.setAction(selectedAction);
						dialog.dismiss(); 
						mAddressAndActionAdapter.notifyDataSetChanged();
								
						Log.i(ZyyKNXConstant.DEBUG, "\nselectedAction:"+selectedAction);
					}
				}
			})
			.setButton2(R.string.cancel, new PromptDialog.OnClickListener() {
						
				@Override
				public void onClick(Dialog dialog, int which) {
					dialog.dismiss();
				}
			})
			.show();  
		}
		
	};
	
	/**
	 *  添加组地址按钮点击事件
	 * */
	private class AddOrRemoveGroupAddressClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
 
			popGroupAddressSelectWindow(v.getContext(), 
					v.getContext().getResources().getString(R.string.add_or_remove_group_address), false);
		}
		
	};
	
	/**
	 *  保存定时任务按钮点击事件 
	 */
	private class SaveModifyClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			
			if(currentTask.isOneOffSelected()) {
				Calendar cal = Calendar.getInstance();
				int sysYear = cal.get(Calendar.YEAR);
				int sysMonth = cal.get(Calendar.MONTH)+1;
				int sysDay = cal.get(Calendar.DAY_OF_MONTH);
				int sysHour = cal.get(Calendar.HOUR_OF_DAY);
				int sysMinute = cal.get(Calendar.MINUTE);
				int sysSecond = cal.get(Calendar.SECOND);
				boolean timeIsInvalid = false;
				
				if (sysYear > currentTask.getYear()) {
					timeIsInvalid = true;
				} else if (sysYear == currentTask.getYear()) {
					if (sysMonth > currentTask.getMonth()) {
						timeIsInvalid = true;
					} else if (sysMonth == currentTask.getMonth()) {
						if (sysDay > currentTask.getDay()) {
							timeIsInvalid = true;
						} else if (sysDay == currentTask.getDay()) {
							if (sysHour > currentTask.getHour()) {
								timeIsInvalid = true;
							} else if (sysHour == currentTask.getHour()) {
								if (sysMinute > currentTask.getMinute()) {
									timeIsInvalid = true;
								} else if (sysMinute == currentTask.getMinute()) {
									if (sysSecond > currentTask.getSecond()) {
										timeIsInvalid = true;
									}
								}
							}
						}
					}
				}
				
				Log.i(ZyyKNXConstant.DEBUG, "timeIsInvalid:"+timeIsInvalid);
				
				if (timeIsInvalid) {
					new AlertDialog.Builder(TimingTaskActivity.this) 
					.setTitle(R.string.error)
					.setMessage(R.string.the_current_task_time_has_expired)
					.setPositiveButton(R.string.confirm, null)
					.show();
					
					return;
				}
			}
			
			if (null != oldTask) {
				mTimingTaskListAdapter.removeTimingTask(oldTask);
				oldTask = null;
			}

			mTimingTaskListAdapter.addTimingTask(currentTask);

			mTimingTaskListAdapter.setSelectedPosition(-1);
			mTimingTaskListAdapter.notifyDataSetChanged();
			
			disableTimingTaskEditor();

			bDeleteTask.setEnabled(false);
			
			ZyyKNXApp.getInstance().saveTimerTask();
		}
		
	};
	
	/**
	 *  弹出组地址选择框
	 */
	private void popGroupAddressSelectWindow(Context context, String title, final boolean isAddTask) {
		
		/* 获取当前定时器的地址列表 */
		List<KNXGroupAddress> groupList = new ArrayList<KNXGroupAddress>();

		Map<String, Integer> groupAddressIndexMap = ZyyKNXApp.getInstance().getGroupAddressIndexMap();
		Map<Integer, KNXGroupAddress> groupAddressMap = ZyyKNXApp.getInstance().getGroupAddressMap();
		
		Map<Integer, KNXControlBase> controlBaseMap = ZyyKNXApp.getInstance().getCurrentPageKNXControlBaseMap();
		KNXControlBase control = controlBaseMap.get(Integer.parseInt(fileName));
		
		Map<String ,KNXSelectedAddress> writeAddressMap = control.getWriteAddressIds();
		if(null != writeAddressMap) {
			for(Map.Entry<String, KNXSelectedAddress> entry:writeAddressMap.entrySet()) {
				String id = entry.getKey();
				Integer index = groupAddressIndexMap.get(id);
				KNXGroupAddress address = groupAddressMap.get(index);
				groupList.add(address);
			}
		}

		/* 显示组地址的ListView */
		ListView lvGroupAddressList = new ListView(context);
		LayoutParams mLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT); 
        mLayoutParams.height = 300;
        mLayoutParams.width = LayoutParams.WRAP_CONTENT;
        lvGroupAddressList.setLayoutParams(mLayoutParams);
        
        /* 获得mAddressAndActionAdapter中已选择的组地址 */
        List<KNXGroupAddress> selectedList = new ArrayList<KNXGroupAddress>();
        for(KNXGroupAddressAndAction addressAndAction : mAddressAndActionAdapter.getAddressAndActionList()) {
        	selectedList.add(addressAndAction.getAddress());
        }
        
        /* 给ListView准备内容 */
        final GroupAddressAdapter mGroupAddressAdapter = new GroupAddressAdapter(context, groupList, selectedList);
        lvGroupAddressList.setAdapter(mGroupAddressAdapter);

		new PromptDialog.Builder(context)
		.setTitle(title) 
		.setIcon(R.drawable.launcher)
		.setViewStyle(PromptDialog.VIEW_STYLE_NORMAL)
		.setView(lvGroupAddressList)
		.setButton1(R.string.confirm,  new PromptDialog.OnClickListener() {
					
			@Override
			public void onClick(Dialog dialog, int which) {
				dialog.dismiss(); 
						
				/* 替换原来的地址列表 */
				mAddressAndActionAdapter.clearAddressAndActionList();
				List<KNXGroupAddress> selectedList = mGroupAddressAdapter.getSelectedAddress();
				for(KNXGroupAddress address : selectedList) {
					mAddressAndActionAdapter.addAddressAndAction(new KNXGroupAddressAndAction(address));
				}

				mAddressAndActionAdapter.notifyDataSetChanged();
			}
		})
		.setButton2(R.string.cancel, new PromptDialog.OnClickListener() {
					
			@Override
			public void onClick(Dialog dialog, int which) {
				dialog.dismiss();
			}
		})
		.show(); 
	}

	
	/**
	 *  定时任务列表刷新事件接收器
	 */
	private class RefreshTimingTaskListReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			if(intent.getAction().equals(ZyyKNXConstant.BROADCAST_REFRESH_TIMING_TASK_LIST)) {
				if(null != mTimingTaskListAdapter) {
					ZyyKNXApp.getInstance().saveTimerTask();
					mTimingTaskListAdapter.notifyDataSetChanged();
				}
			}
		}
		
	}
}
