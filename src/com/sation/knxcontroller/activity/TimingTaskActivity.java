<<<<<<< HEAD
package com.sation.knxcontroller.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import com.sation.knxcontroller.R;
import com.sation.knxcontroller.STKNXControllerApp;
import com.sation.knxcontroller.STKNXControllerConstant;
import com.sation.knxcontroller.adapter.AddressAndActionAdapter;
import com.sation.knxcontroller.adapter.GroupAddressAdapter;
import com.sation.knxcontroller.adapter.TimingTaskListAdapter;
import com.sation.knxcontroller.control.KNXControlBase;
import com.sation.knxcontroller.control.KNXTimerButton;
import com.sation.knxcontroller.control.TimingTaskItem;
import com.sation.knxcontroller.control.TimingTaskItem.KNXGroupAddressAndAction;
import com.sation.knxcontroller.models.KNXGroupAddress;
import com.sation.knxcontroller.models.KNXSelectedAddress;
import com.sation.knxcontroller.util.Log;
import com.sation.knxcontroller.widget.CustomDateAndTimeDialog;
import com.sation.knxcontroller.widget.CustomDateAndTimeDialog.OnDateAndTimeChangedListener;
import com.sation.knxcontroller.widget.CustomDateAndTimeDialog.OnTimeChangedListener;
import com.sation.knxcontroller.widget.CustomTimerPickerDialog;
import com.sation.knxcontroller.widget.CustomTimerPickerDialog.OnTimerChangedListener;
import com.sation.knxcontroller.widget.PromptDialog;
import com.sation.knxcontroller.widget.TimeAndWeekdayPickerDialog;
import com.sation.knxcontroller.widget.TimeAndWeekdayPickerDialog.OnChangedListener;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

public class TimingTaskActivity extends BaseActivity {
	private final static String TAG = "TimingTaskActivity";
	private EditText etTaskName;
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
		fileName = getIntent().getStringExtra(STKNXControllerConstant.CONTROL_ID); // 获取当前定时器的ID
		Map<String, List<TimingTaskItem>> timerTaskMap = STKNXControllerApp.getInstance().getTimerTaskMap(); // 获取当前页面的定时器
		timingTaskList = timerTaskMap.get(fileName); // 根据定时器ID获取定时任务列表
		if(null == timingTaskList) {
			/* 如为新建的定时器 */
			timingTaskList = new ArrayList<TimingTaskItem>();
			timerTaskMap.put(fileName, timingTaskList);
		}
		
		TextView tvTimerName = (TextView)findViewById(R.id.timingTaskLayoutTimerName);
		try {
			int controlId = Integer.parseInt(fileName);
			KNXControlBase control = STKNXControllerApp.getInstance().getCurrentPageKNXControlBaseMap().get(controlId);
			if(control instanceof KNXTimerButton) {
				tvTimerName.setText(control.getTitle());
			} else {
				tvTimerName.setText("");
			}
		} catch (Exception e) {
			Log.e(e.getLocalizedMessage());
			tvTimerName.setText("");
		}

		/* ListView 定时任务列表 */
		ListView lvTimingTaskList = (ListView)findViewById(R.id.timingTaskLayoutTimingTaskList);
        mTimingTaskListAdapter = new TimingTaskListAdapter(this, timingTaskList);
		lvTimingTaskList.setAdapter(mTimingTaskListAdapter);
		lvTimingTaskList.setOnItemClickListener(new TimingTaskListOnItemClickListener());

		/* Button 删除任务按钮 */
		bDeleteTask = (Button)findViewById(R.id.buttonDeleteTask);
		bDeleteTask.setEnabled(false);
		bDeleteTask.setOnClickListener(new DeleteTaskOnClickListener());
		
		/* Button 创建任务按钮 */
		Button bCreateTask = (Button)findViewById(R.id.buttonCreateTask);
		bCreateTask.setOnClickListener(new CreateTaskOnClickListener());
		
		etTaskName = (EditText)findViewById(R.id.timingTaskLayoutTaskItemEditTextName);

		/* 定时任务执行方式 */
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
         
        /* Button 添加移除组地址按钮 */
        bAddOrRemoveGroupAddress = (Button)findViewById(R.id.buttonAddOrRemoveGroupAddress);
        bAddOrRemoveGroupAddress.setOnClickListener(new AddOrRemoveGroupAddressClickListener());
        
        /* Button 保存任务按钮 */
        bSaveModify = (Button)findViewById(R.id.buttonSaveTask);
        bSaveModify.setOnClickListener(new SaveModifyClickListener());
        
        disableTimingTaskEditor();
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
		mRefreshTimingTaskListReceiver = new RefreshTimingTaskListReceiver();
        IntentFilter filter = new IntentFilter(STKNXControllerConstant.BROADCAST_REFRESH_TIMING_TASK_LIST);
        registerReceiver(mRefreshTimingTaskListReceiver, filter);  
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
	public void onPause() {
		super.onPause();

		STKNXControllerApp.getInstance().saveTimerTask();
	}
	
	@Override
	public void onStop() {
		super.onStop();
		
		unregisterReceiver(mRefreshTimingTaskListReceiver);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		System.gc();
	}

	@Override
	public void finish() {
		super.finish();

		overridePendingTransition(R.anim.scale_from_center_600, R.anim.scale_to_center_600);
	}

	/**
	 *  刷新定时任务执行方式的界面 
	 */
  	private void refreshThisUI(TimingTaskItem item) {
  		etTaskName.setText(item.getName());
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
	
	@SuppressLint("DefaultLocale")
	private String getMonthlyContent(TimingTaskItem item) {
		return String.format("%s: %02d%s  %02d%s%02d%s", getResources().getString(R.string.monthly),
				item.getDay(), getResources().getString(R.string.day),
				item.getHour(), getResources().getString(R.string.hour),
				item.getMinute(), getResources().getString(R.string.minute));
	}
	
	@SuppressLint("DefaultLocale")
	private String getWeeklyContent(TimingTaskItem item) {
		String weekly = String.format("%s: %s", getResources().getString(R.string.weekly), item.getWeekly(this)) ;
		String time = String.format("%02d%s%02d%s", item.getHour(), getResources().getString(R.string.hour),
				item.getMinute(), getResources().getString(R.string.minute));
		return weekly + " " + time;
	}
	
	@SuppressLint("DefaultLocale")
	private String getEverydayContent(TimingTaskItem item) {
		return String.format("%s: %02d%s%02d%s", getResources().getString(R.string.everyday),
				item.getHour(), getResources().getString(R.string.hour),
				item.getMinute(), getResources().getString(R.string.minute));
	}
	
	@SuppressLint("DefaultLocale")
	private String getCycleContent(TimingTaskItem item) {
		return String.format("%s: %d%s%d%s%d%s", getResources().getString(R.string.cycle_interval),
				item.getIntervalHour(), getResources().getString(R.string.unit_hour), 
				item.getIntervalMinute(), getResources().getString(R.string.unit_minute), 
				item.getIntervalSecond(), getResources().getString(R.string.second));
	}
	
	@SuppressLint("DefaultLocale")
	private String getOneOffContent(TimingTaskItem item) {
		return String.format("%04d%s%02d%s%02d%s  %02d%s%02d%s", item.getYear(), getResources().getString(R.string.year),
				item.getMonth(), getResources().getString(R.string.month), 
				item.getDay(), getResources().getString(R.string.day),
				item.getHour(), getResources().getString(R.string.hour),
				item.getMinute(), getResources().getString(R.string.minute));
	}
	
	private void enableTimingTaskEditor() {
		etTaskName.setEnabled(true);
		rbMonthly.setEnabled(true);
		rbWeekly.setEnabled(true);
		rbEveryday.setEnabled(true);
		rbCycle.setEnabled(true);
		rbOneOff.setEnabled(true);
		tvExecuteWayDetail.setVisibility(View.VISIBLE);
		tvExecuteWayDetail.setEnabled(true);
		lvGroupAddressList.setVisibility(View.VISIBLE);
	    lvGroupAddressList.setEnabled(true);
	    bAddOrRemoveGroupAddress.setEnabled(true);
	    bSaveModify.setEnabled(true);
	}
	
	private void disableTimingTaskEditor() {
		etTaskName.setText("");
		etTaskName.setEnabled(false);
		rbMonthly.setChecked(false);
		rbMonthly.setEnabled(false);
		rbWeekly.setChecked(false);
		rbWeekly.setEnabled(false);
		rbEveryday.setChecked(false);
		rbEveryday.setEnabled(false);
		rbCycle.setChecked(false);
		rbCycle.setEnabled(false);
		rbOneOff.setChecked(false);
		rbOneOff.setEnabled(false);
		tvExecuteWayDetail.setVisibility(View.INVISIBLE);
		tvExecuteWayDetail.setEnabled(false);
		lvGroupAddressList.setVisibility(View.INVISIBLE);
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
						
						STKNXControllerApp.getInstance().saveTimerTask();
						
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
			popGroupAddressSelectWindow(TimingTaskActivity.this, getResources().getString(R.string.add_address_to_task));

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
			dialogMonthly.setIcon(R.mipmap.launcher);
			dialogMonthly.setViewStyle(PromptDialog.VIEW_STYLE_NORMAL);
			dialogMonthly.setButton1(R.string.confirm,  new PromptDialog.OnClickListener() {
					
				@Override
				public void onClick(Dialog dialog, int which) {
					currentTask.setMonthly(true);
					currentTask.setWeekly(false);
					currentTask.setEveryday(false);
					currentTask.setLoop(false);
					currentTask.setOneOff(false);

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
			dialogWeekly.setIcon(R.mipmap.launcher);
			dialogWeekly.setViewStyle(PromptDialog.VIEW_STYLE_NORMAL);
			dialogWeekly.setButton1(R.string.confirm,  new PromptDialog.OnClickListener() {
							
				@Override
				public void onClick(Dialog dialog, int which) {
					currentTask.setMonthly(false);
					currentTask.setWeekly(true);
					currentTask.setEveryday(false);
					currentTask.setLoop(false);
					currentTask.setOneOff(false);
						
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
			dialogEveryday.setIcon(R.mipmap.launcher);
			dialogEveryday.setViewStyle(PromptDialog.VIEW_STYLE_NORMAL);
			dialogEveryday.setButton1(R.string.confirm,  new PromptDialog.OnClickListener() {
							
				@Override
				public void onClick(Dialog dialog, int which) {
					currentTask.setMonthly(false);
					currentTask.setWeekly(false);
					currentTask.setEveryday(true);
					currentTask.setLoop(false);
					currentTask.setOneOff(false);
						
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
			dialogTime.setIcon(R.mipmap.launcher);
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
			dialogOneOff.setIcon(R.mipmap.launcher);
			dialogOneOff.setViewStyle(PromptDialog.VIEW_STYLE_NORMAL);
			dialogOneOff.setButton1(R.string.confirm,  new PromptDialog.OnClickListener() {
							
				@Override
				public void onClick(Dialog dialog, int which) {
					
					currentTask.setMonthly(false);
					currentTask.setWeekly(false);
					currentTask.setEveryday(false);
					currentTask.setLoop(false);
					currentTask.setOneOff(true);
						
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
	 *  添加组地址按钮点击事件
	 * */
	private class AddOrRemoveGroupAddressClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {

			popGroupAddressSelectWindow(v.getContext(),
					v.getContext().getResources().getString(R.string.add_or_remove_group_address));
		}
	}
	
	/**
	 *  保存定时任务按钮点击事件 
	 */
	private class SaveModifyClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			
			currentTask.setName(etTaskName.getText().toString());
			
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

			currentTask.setAddressList(mAddressAndActionAdapter.getAddressAndActionList());

			mTimingTaskListAdapter.addTimingTask(currentTask);

			mTimingTaskListAdapter.setSelectedPosition(-1);
			mTimingTaskListAdapter.notifyDataSetChanged();
			
			disableTimingTaskEditor();

			bDeleteTask.setEnabled(false);
			
			STKNXControllerApp.getInstance().saveTimerTask();
		}
		
	}
	
	/**
	 *  弹出组地址选择框
	 */
	private void popGroupAddressSelectWindow(Context context, String title) {
		
		/* 获取当前定时器的地址列表 */
		List<KNXGroupAddress> groupList = new ArrayList<KNXGroupAddress>();

		Map<String, Integer> groupAddressIndexMap = STKNXControllerApp.getInstance().getGroupAddressIndexMap();
		Map<Integer, KNXGroupAddress> groupAddressMap = STKNXControllerApp.getInstance().getGroupAddressMap();
		Map<String, KNXGroupAddress> addrMap = STKNXControllerApp.getInstance().getGroupAddressIdMap();
		
		Map<Integer, KNXControlBase> controlBaseMap = STKNXControllerApp.getInstance().getCurrentPageKNXControlBaseMap();
		KNXControlBase control = controlBaseMap.get(Integer.parseInt(fileName));
		if(null != control) {
			Map<String, KNXSelectedAddress> writeAddressMap = control.getWriteAddressIds();
			Map<String, KNXSelectedAddress> readAddressMap = control.getReadAddressId();
			if (null != writeAddressMap) {
				for (Map.Entry<String, KNXSelectedAddress> entry : writeAddressMap.entrySet()) {
					String id = entry.getKey();
					Integer index = groupAddressIndexMap.get(id);
					KNXGroupAddress address = groupAddressMap.get(index);
					groupList.add(address);
				}
			}
			if (null != readAddressMap) {
				for (Map.Entry<String, KNXSelectedAddress> entry : readAddressMap.entrySet()) {
					String id = entry.getKey();
					Integer index = groupAddressIndexMap.get(id);
					KNXGroupAddress address = groupAddressMap.get(index);

					/* 去掉与写地址表中重复的地址 */
					boolean exist = false;
					for (KNXGroupAddress addr: groupList) {
						if(addr.getStringKnxAddress().equals(address.getStringKnxAddress())) {
							exist = true;
							break;
						}
					}
					if(!exist) {
						groupList.add(address);
					}
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
			for (KNXGroupAddressAndAction addressAndAction : mAddressAndActionAdapter.getAddressAndActionList()) {
//				selectedList.add(addressAndAction.getAddress());
				selectedList.add(addrMap.get(addressAndAction.getAddressId()));
			}
        
        	/* 给ListView准备内容 */
			final GroupAddressAdapter mGroupAddressAdapter = new GroupAddressAdapter(context, groupList, selectedList);
			lvGroupAddressList.setAdapter(mGroupAddressAdapter);

			new PromptDialog.Builder(context)
					.setTitle(title)
					.setIcon(R.mipmap.launcher)
					.setViewStyle(PromptDialog.VIEW_STYLE_NORMAL)
					.setView(lvGroupAddressList)
					.setButton1(R.string.confirm, new PromptDialog.OnClickListener() {

						@Override
						public void onClick(Dialog dialog, int which) {
							dialog.dismiss();
						
							/* 替换原来的地址列表 */
//							mAddressAndActionAdapter.clearAddressAndActionList();
							List<KNXGroupAddress> selectedList = mGroupAddressAdapter.getSelectedAddress();
							List<KNXGroupAddressAndAction> list = new ArrayList<KNXGroupAddressAndAction>();
							for (KNXGroupAddress address : selectedList) {
								boolean isContains = false;
								for (KNXGroupAddressAndAction AAA : mAddressAndActionAdapter.getAddressAndActionList()) {
									if(AAA.getAddressId().equals(address.getId())) {
										list.add(AAA);
										isContains = true;
										break;
									}
								}

								if (!isContains) {
									list.add(new KNXGroupAddressAndAction(address));
								}
//								mAddressAndActionAdapter.addAddressAndAction(new KNXGroupAddressAndAction(address));
							}

							mAddressAndActionAdapter.setAddressAndActionList(list);
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
	}

	/**
	 *  定时任务列表刷新事件接收器
	 */
	private class RefreshTimingTaskListReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			if(intent.getAction().equals(STKNXControllerConstant.BROADCAST_REFRESH_TIMING_TASK_LIST)/* &&
					SystemUtil.isForeground(TimingTaskActivity.this, "com.sation.knxcontroller.activity.TimingTaskActivity")*/) {
				if(null != mTimingTaskListAdapter) {
					try {
//						Log.i(TAG, "1");
//						STKNXControllerApp.getInstance().saveTimerTask();
						mTimingTaskListAdapter.notifyDataSetChanged();
//						Log.i(TAG, "2");
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		}
		
	}
}
=======
package com.sation.knxcontroller.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import com.sation.knxcontroller.R;
import com.sation.knxcontroller.STKNXControllerApp;
import com.sation.knxcontroller.STKNXControllerConstant;
import com.sation.knxcontroller.adapter.AddressAndActionAdapter;
import com.sation.knxcontroller.adapter.GroupAddressAdapter;
import com.sation.knxcontroller.adapter.TimingTaskListAdapter;
import com.sation.knxcontroller.control.KNXControlBase;
import com.sation.knxcontroller.control.KNXTimerButton;
import com.sation.knxcontroller.control.TimingTaskItem;
import com.sation.knxcontroller.control.TimingTaskItem.KNXGroupAddressAndAction;
import com.sation.knxcontroller.models.KNXGroupAddress;
import com.sation.knxcontroller.models.KNXSelectedAddress;
import com.sation.knxcontroller.util.Log;
import com.sation.knxcontroller.widget.CustomDateAndTimeDialog;
import com.sation.knxcontroller.widget.CustomDateAndTimeDialog.OnDateAndTimeChangedListener;
import com.sation.knxcontroller.widget.CustomDateAndTimeDialog.OnTimeChangedListener;
import com.sation.knxcontroller.widget.CustomTimerPickerDialog;
import com.sation.knxcontroller.widget.CustomTimerPickerDialog.OnTimerChangedListener;
import com.sation.knxcontroller.widget.PromptDialog;
import com.sation.knxcontroller.widget.TimeAndWeekdayPickerDialog;
import com.sation.knxcontroller.widget.TimeAndWeekdayPickerDialog.OnChangedListener;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

public class TimingTaskActivity extends BaseActivity {
	private final static String TAG = "TimingTaskActivity";
	private EditText etTaskName;
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
		fileName = getIntent().getStringExtra(STKNXControllerConstant.CONTROL_ID); // 获取当前定时器的ID
		Map<String, List<TimingTaskItem>> timerTaskMap = STKNXControllerApp.getInstance().getTimerTaskMap(); // 获取当前页面的定时器
		timingTaskList = timerTaskMap.get(fileName); // 根据定时器ID获取定时任务列表
		if(null == timingTaskList) {
			/* 如为新建的定时器 */
			timingTaskList = new ArrayList<TimingTaskItem>();
			timerTaskMap.put(fileName, timingTaskList);
		}
		
		TextView tvTimerName = (TextView)findViewById(R.id.timingTaskLayoutTimerName);
		try {
			int controlId = Integer.parseInt(fileName);
			KNXControlBase control = STKNXControllerApp.getInstance().getCurrentPageKNXControlBaseMap().get(controlId);
			if(control instanceof KNXTimerButton) {
				tvTimerName.setText(control.getTitle());
			} else {
				tvTimerName.setText("");
			}
		} catch (Exception e) {
			Log.e(e.getLocalizedMessage());
			tvTimerName.setText("");
		}

		/* ListView 定时任务列表 */
		ListView lvTimingTaskList = (ListView)findViewById(R.id.timingTaskLayoutTimingTaskList);
        mTimingTaskListAdapter = new TimingTaskListAdapter(this, timingTaskList);
		lvTimingTaskList.setAdapter(mTimingTaskListAdapter);
		lvTimingTaskList.setOnItemClickListener(new TimingTaskListOnItemClickListener());

		/* Button 删除任务按钮 */
		bDeleteTask = (Button)findViewById(R.id.buttonDeleteTask);
		bDeleteTask.setEnabled(false);
		bDeleteTask.setOnClickListener(new DeleteTaskOnClickListener());
		
		/* Button 创建任务按钮 */
		Button bCreateTask = (Button)findViewById(R.id.buttonCreateTask);
		bCreateTask.setOnClickListener(new CreateTaskOnClickListener());
		
		etTaskName = (EditText)findViewById(R.id.timingTaskLayoutTaskItemEditTextName);

		/* 定时任务执行方式 */
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
         
        /* Button 添加移除组地址按钮 */
        bAddOrRemoveGroupAddress = (Button)findViewById(R.id.buttonAddOrRemoveGroupAddress);
        bAddOrRemoveGroupAddress.setOnClickListener(new AddOrRemoveGroupAddressClickListener());
        
        /* Button 保存任务按钮 */
        bSaveModify = (Button)findViewById(R.id.buttonSaveTask);
        bSaveModify.setOnClickListener(new SaveModifyClickListener());
        
        disableTimingTaskEditor();
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
		mRefreshTimingTaskListReceiver = new RefreshTimingTaskListReceiver();
        IntentFilter filter = new IntentFilter(STKNXControllerConstant.BROADCAST_REFRESH_TIMING_TASK_LIST);
        registerReceiver(mRefreshTimingTaskListReceiver, filter);  
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
	public void onPause() {
		super.onPause();

		STKNXControllerApp.getInstance().saveTimerTask();
	}
	
	@Override
	public void onStop() {
		super.onStop();
		
		unregisterReceiver(mRefreshTimingTaskListReceiver);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		System.gc();
	}

	@Override
	public void finish() {
		super.finish();

		overridePendingTransition(R.anim.scale_from_center_600, R.anim.scale_to_center_600);
	}

	/**
	 *  刷新定时任务执行方式的界面 
	 */
  	private void refreshThisUI(TimingTaskItem item) {
  		etTaskName.setText(item.getName());
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
	
	@SuppressLint("DefaultLocale")
	private String getMonthlyContent(TimingTaskItem item) {
		return String.format("%s: %02d%s  %02d%s%02d%s", getResources().getString(R.string.monthly),
				item.getDay(), getResources().getString(R.string.day),
				item.getHour(), getResources().getString(R.string.hour),
				item.getMinute(), getResources().getString(R.string.minute));
	}
	
	@SuppressLint("DefaultLocale")
	private String getWeeklyContent(TimingTaskItem item) {
		String weekly = String.format("%s: %s", getResources().getString(R.string.weekly), item.getWeekly(this)) ;
		String time = String.format("%02d%s%02d%s", item.getHour(), getResources().getString(R.string.hour),
				item.getMinute(), getResources().getString(R.string.minute));
		return weekly + " " + time;
	}
	
	@SuppressLint("DefaultLocale")
	private String getEverydayContent(TimingTaskItem item) {
		return String.format("%s: %02d%s%02d%s", getResources().getString(R.string.everyday),
				item.getHour(), getResources().getString(R.string.hour),
				item.getMinute(), getResources().getString(R.string.minute));
	}
	
	@SuppressLint("DefaultLocale")
	private String getCycleContent(TimingTaskItem item) {
		return String.format("%s: %d%s%d%s%d%s", getResources().getString(R.string.cycle_interval),
				item.getIntervalHour(), getResources().getString(R.string.unit_hour), 
				item.getIntervalMinute(), getResources().getString(R.string.unit_minute), 
				item.getIntervalSecond(), getResources().getString(R.string.second));
	}
	
	@SuppressLint("DefaultLocale")
	private String getOneOffContent(TimingTaskItem item) {
		return String.format("%04d%s%02d%s%02d%s  %02d%s%02d%s", item.getYear(), getResources().getString(R.string.year),
				item.getMonth(), getResources().getString(R.string.month), 
				item.getDay(), getResources().getString(R.string.day),
				item.getHour(), getResources().getString(R.string.hour),
				item.getMinute(), getResources().getString(R.string.minute));
	}
	
	private void enableTimingTaskEditor() {
		etTaskName.setEnabled(true);
		rbMonthly.setEnabled(true);
		rbWeekly.setEnabled(true);
		rbEveryday.setEnabled(true);
		rbCycle.setEnabled(true);
		rbOneOff.setEnabled(true);
		tvExecuteWayDetail.setVisibility(View.VISIBLE);
		tvExecuteWayDetail.setEnabled(true);
		lvGroupAddressList.setVisibility(View.VISIBLE);
	    lvGroupAddressList.setEnabled(true);
	    bAddOrRemoveGroupAddress.setEnabled(true);
	    bSaveModify.setEnabled(true);
	}
	
	private void disableTimingTaskEditor() {
		etTaskName.setText("");
		etTaskName.setEnabled(false);
		rbMonthly.setChecked(false);
		rbMonthly.setEnabled(false);
		rbWeekly.setChecked(false);
		rbWeekly.setEnabled(false);
		rbEveryday.setChecked(false);
		rbEveryday.setEnabled(false);
		rbCycle.setChecked(false);
		rbCycle.setEnabled(false);
		rbOneOff.setChecked(false);
		rbOneOff.setEnabled(false);
		tvExecuteWayDetail.setVisibility(View.INVISIBLE);
		tvExecuteWayDetail.setEnabled(false);
		lvGroupAddressList.setVisibility(View.INVISIBLE);
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
						
						STKNXControllerApp.getInstance().saveTimerTask();
						
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
			popGroupAddressSelectWindow(TimingTaskActivity.this, getResources().getString(R.string.add_address_to_task));

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
			dialogMonthly.setIcon(R.mipmap.launcher);
			dialogMonthly.setViewStyle(PromptDialog.VIEW_STYLE_NORMAL);
			dialogMonthly.setButton1(R.string.confirm,  new PromptDialog.OnClickListener() {
					
				@Override
				public void onClick(Dialog dialog, int which) {
					currentTask.setMonthly(true);
					currentTask.setWeekly(false);
					currentTask.setEveryday(false);
					currentTask.setLoop(false);
					currentTask.setOneOff(false);

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
			dialogWeekly.setIcon(R.mipmap.launcher);
			dialogWeekly.setViewStyle(PromptDialog.VIEW_STYLE_NORMAL);
			dialogWeekly.setButton1(R.string.confirm,  new PromptDialog.OnClickListener() {
							
				@Override
				public void onClick(Dialog dialog, int which) {
					currentTask.setMonthly(false);
					currentTask.setWeekly(true);
					currentTask.setEveryday(false);
					currentTask.setLoop(false);
					currentTask.setOneOff(false);
						
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
			dialogEveryday.setIcon(R.mipmap.launcher);
			dialogEveryday.setViewStyle(PromptDialog.VIEW_STYLE_NORMAL);
			dialogEveryday.setButton1(R.string.confirm,  new PromptDialog.OnClickListener() {
							
				@Override
				public void onClick(Dialog dialog, int which) {
					currentTask.setMonthly(false);
					currentTask.setWeekly(false);
					currentTask.setEveryday(true);
					currentTask.setLoop(false);
					currentTask.setOneOff(false);
						
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
			dialogTime.setIcon(R.mipmap.launcher);
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
			dialogOneOff.setIcon(R.mipmap.launcher);
			dialogOneOff.setViewStyle(PromptDialog.VIEW_STYLE_NORMAL);
			dialogOneOff.setButton1(R.string.confirm,  new PromptDialog.OnClickListener() {
							
				@Override
				public void onClick(Dialog dialog, int which) {
					
					currentTask.setMonthly(false);
					currentTask.setWeekly(false);
					currentTask.setEveryday(false);
					currentTask.setLoop(false);
					currentTask.setOneOff(true);
						
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
	 *  添加组地址按钮点击事件
	 * */
	private class AddOrRemoveGroupAddressClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {

			popGroupAddressSelectWindow(v.getContext(),
					v.getContext().getResources().getString(R.string.add_or_remove_group_address));
		}
	}
	
	/**
	 *  保存定时任务按钮点击事件 
	 */
	private class SaveModifyClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			
			currentTask.setName(etTaskName.getText().toString());
			
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

			currentTask.setAddressList(mAddressAndActionAdapter.getAddressAndActionList());

			mTimingTaskListAdapter.addTimingTask(currentTask);

			mTimingTaskListAdapter.setSelectedPosition(-1);
			mTimingTaskListAdapter.notifyDataSetChanged();
			
			disableTimingTaskEditor();

			bDeleteTask.setEnabled(false);
			
			STKNXControllerApp.getInstance().saveTimerTask();
		}
		
	}
	
	/**
	 *  弹出组地址选择框
	 */
	private void popGroupAddressSelectWindow(Context context, String title) {
		
		/* 获取当前定时器的地址列表 */
		List<KNXGroupAddress> groupList = new ArrayList<KNXGroupAddress>();

		Map<String, Integer> groupAddressIndexMap = STKNXControllerApp.getInstance().getGroupAddressIndexMap();
		Map<Integer, KNXGroupAddress> groupAddressMap = STKNXControllerApp.getInstance().getGroupAddressMap();
		Map<String, KNXGroupAddress> addrMap = STKNXControllerApp.getInstance().getGroupAddressIdMap();
		
		Map<Integer, KNXControlBase> controlBaseMap = STKNXControllerApp.getInstance().getCurrentPageKNXControlBaseMap();
		KNXControlBase control = controlBaseMap.get(Integer.parseInt(fileName));
		if(null != control) {
			Map<String, KNXSelectedAddress> writeAddressMap = control.getWriteAddressIds();
			Map<String, KNXSelectedAddress> readAddressMap = control.getReadAddressId();
			if (null != writeAddressMap) {
				for (Map.Entry<String, KNXSelectedAddress> entry : writeAddressMap.entrySet()) {
					String id = entry.getKey();
					Integer index = groupAddressIndexMap.get(id);
					KNXGroupAddress address = groupAddressMap.get(index);
					groupList.add(address);
				}
			}
			if (null != readAddressMap) {
				for (Map.Entry<String, KNXSelectedAddress> entry : readAddressMap.entrySet()) {
					String id = entry.getKey();
					Integer index = groupAddressIndexMap.get(id);
					KNXGroupAddress address = groupAddressMap.get(index);

					/* 去掉与写地址表中重复的地址 */
					boolean exist = false;
					for (KNXGroupAddress addr: groupList) {
						if(addr.getStringKnxAddress().equals(address.getStringKnxAddress())) {
							exist = true;
							break;
						}
					}
					if(!exist) {
						groupList.add(address);
					}
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
			for (KNXGroupAddressAndAction addressAndAction : mAddressAndActionAdapter.getAddressAndActionList()) {
//				selectedList.add(addressAndAction.getAddress());
				selectedList.add(addrMap.get(addressAndAction.getAddressId()));
			}
        
        	/* 给ListView准备内容 */
			final GroupAddressAdapter mGroupAddressAdapter = new GroupAddressAdapter(context, groupList, selectedList);
			lvGroupAddressList.setAdapter(mGroupAddressAdapter);

			new PromptDialog.Builder(context)
					.setTitle(title)
					.setIcon(R.mipmap.launcher)
					.setViewStyle(PromptDialog.VIEW_STYLE_NORMAL)
					.setView(lvGroupAddressList)
					.setButton1(R.string.confirm, new PromptDialog.OnClickListener() {

						@Override
						public void onClick(Dialog dialog, int which) {
							dialog.dismiss();
						
							/* 替换原来的地址列表 */
//							mAddressAndActionAdapter.clearAddressAndActionList();
							List<KNXGroupAddress> selectedList = mGroupAddressAdapter.getSelectedAddress();
							List<KNXGroupAddressAndAction> list = new ArrayList<KNXGroupAddressAndAction>();
							for (KNXGroupAddress address : selectedList) {
								boolean isContains = false;
								for (KNXGroupAddressAndAction AAA : mAddressAndActionAdapter.getAddressAndActionList()) {
									if(AAA.getAddressId().equals(address.getId())) {
										list.add(AAA);
										isContains = true;
										break;
									}
								}

								if (!isContains) {
									list.add(new KNXGroupAddressAndAction(address));
								}
//								mAddressAndActionAdapter.addAddressAndAction(new KNXGroupAddressAndAction(address));
							}

							mAddressAndActionAdapter.setAddressAndActionList(list);
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
	}

	/**
	 *  定时任务列表刷新事件接收器
	 */
	private class RefreshTimingTaskListReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			if(intent.getAction().equals(STKNXControllerConstant.BROADCAST_REFRESH_TIMING_TASK_LIST)/* &&
					SystemUtil.isForeground(TimingTaskActivity.this, "com.sation.knxcontroller.activity.TimingTaskActivity")*/) {
				if(null != mTimingTaskListAdapter) {
					try {
//						Log.i(TAG, "1");
//						STKNXControllerApp.getInstance().saveTimerTask();
						mTimingTaskListAdapter.notifyDataSetChanged();
//						Log.i(TAG, "2");
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		}
		
	}
}
>>>>>>> SationCentralControl(10inch)
