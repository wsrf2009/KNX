/**
 * 
 */
package com.zyyknx.android.activity;

import java.util.List;
import java.util.Map;

import com.zyyknx.android.*;
import com.zyyknx.android.adapter.TaskListAdapter;
import com.zyyknx.android.adapter.TimingTaskListAdapter;
import com.zyyknx.android.control.KNXControlBase;
import com.zyyknx.android.control.TimingTaskItem;
import com.zyyknx.android.models.KNXGroupAddress;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;  

/**
 * @author wangchunfeng
 *
 */
public class TimingTaskDialog extends Dialog {
	

	public TimingTaskDialog(Context context) {
		super(context);
	}
	
	public static class Builder {
		private Context mContext;
		private String title;
		private Spinner mSpinner;
//		Map<Integer, KNXControlBase> mapControls;
		private DialogInterface.OnClickListener positiveButtonClickListener;  
        private DialogInterface.OnClickListener negativeButtonClickListener;  
        private CheckBox cbOneOff;
        private CheckBox cbEveryday;
        private CheckBox cbMonday;
        private CheckBox cbTuesday;
        private CheckBox cbWednesday;
        private CheckBox cbThursday;
        private CheckBox cbFriday;
        private CheckBox cbSaturday;
        private CheckBox cbSunday;
        private DatePicker datePicker;
        private TimePicker timePicker;
        private int year = -1;
        private int month = -1;
        private int day = -1;
        private int hour = -1;
        private int minute = -1;
        private CheckBox cbOpen;
        private CheckBox cbClose;
        private KNXGroupAddress address;
        
        public Builder(Context context) {
        	this.mContext = context;
        }
		
//		public Builder(Context context, Map<Integer, KNXControlBase> map) {
//			this.mContext = context;
//			this.mapControls = map;
//		}
  
        /** 
         * Set the Dialog title from resource 
         *  
         * @param title 
         * @return 
         */  
        public Builder setTitle(int title) {  
            this.title = (String) mContext.getText(title);  
            return this;  
        }  
  
        /** 
         * Set the Dialog title from String 
         *  
         * @param title 
         * @return 
         */  
  
        public Builder setTitle(String title) {  
            this.title = title;  
            return this;  
        }
        
        public Builder setDateAndTime(int year, int month, int day, int hour, int minute) {
        	this.year = year;
        	this.month = month;
        	this.day = day;
        	this.hour = hour;
        	this.minute = minute;
        	
        	return this;
        }
        
        public TimingTaskItem getTimingTask() {
        	TimingTaskItem timingTaskItem = new TimingTaskItem(address);
        	timingTaskItem.setYear(datePicker.getYear());
        	timingTaskItem.setMonth(datePicker.getMonth()+1);
        	timingTaskItem.setDay(datePicker.getDayOfMonth());
        	timingTaskItem.setHour(timePicker.getCurrentHour());
        	timingTaskItem.setMinute(timePicker.getCurrentMinute());
        	timingTaskItem.setTaskOpen(cbOpen.isChecked());
        	timingTaskItem.setTaskClose(cbClose.isChecked());
        	timingTaskItem.setOneOff(cbOneOff.isChecked());
        	timingTaskItem.setMonday(cbMonday.isChecked());
        	timingTaskItem.setTuesday(cbTuesday.isChecked());
        	timingTaskItem.setWednesday(cbWednesday.isChecked());
        	timingTaskItem.setThursday(cbThursday.isChecked());
        	timingTaskItem.setFriday(cbFriday.isChecked());
        	timingTaskItem.setSaturday(cbSaturday.isChecked());
        	timingTaskItem.setSunday(cbSunday.isChecked());
        	
        	return timingTaskItem;
        }
  
        public Builder setPositiveButton(DialogInterface.OnClickListener listener) {  
            this.positiveButtonClickListener = listener;  
            return this;  
        }  
  
        public Builder setNegativeButton(DialogInterface.OnClickListener listener) {  
            this.negativeButtonClickListener = listener;  
            return this;  
        } 
        
        public TimingTaskDialog create() {  
            LayoutInflater inflater = (LayoutInflater) mContext  
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
            // instantiate the dialog with the custom Theme  
            final TimingTaskDialog dialog = new TimingTaskDialog(mContext);  
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            final View customView = inflater.inflate(R.layout.timing_task_dialog, null);  
            dialog.addContentView(customView, new LayoutParams(  
                    LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
            
            ListView lvTimingTaskList = (ListView)customView.findViewById(R.id.listViewTimingTaskList);
            List<TimingTaskItem> timingTaskList = ZyyKNXApp.getInstance().getTimingTaskList();
            TimingTaskListAdapter mTimingTaskListAdapter = new TimingTaskListAdapter(mContext, timingTaskList);
			lvTimingTaskList.setAdapter(mTimingTaskListAdapter);
            
			Map<Integer, KNXGroupAddress> groupAddressMap = ZyyKNXApp.getInstance().getGroupAddressMap();
            mSpinner = (Spinner)customView.findViewById(R.id.spinner);
            final TaskListAdapter mAdapter = new TaskListAdapter(mContext, groupAddressMap); 
            mSpinner.setAdapter(mAdapter);
            mSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					Log.i("SliderSwitch", this.getClass() + ":" + "arg0 = " + arg0 + ", arg1 = " + arg1 + ", arg2 = " + arg2 + ", arg3 = " + arg3);
					Log.i("SliderSwitch", this.getClass() + "create():selectedItem" + mAdapter.getItem(arg2));
					
					address = (KNXGroupAddress)mAdapter.getItem(arg2);
					
//					cbOpen = (CheckBox)customView.findViewById(R.id.checkBoxOpen);
//					cbOpen.setOnCheckedChangeListener(cbOpenCheckedChangeListener);
//					
//					cbClose = (CheckBox)customView.findViewById(R.id.checkBoxClose);
//					cbClose.setOnCheckedChangeListener(cbCloseCheckedChangeListener);
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub
					
				}
            });
            
            TextView textviewTitle = (TextView)customView.findViewById(R.id.textviewDialogTitile);
            textviewTitle.setText(title);
            
            datePicker = (DatePicker)customView.findViewById(R.id.datePicker);
            if ((year>0) && (month>0) && (day>0)) { // 输入的日期有效则更新日期
            	datePicker.updateDate(year, month, day);
            }
            
            timePicker = (TimePicker)customView.findViewById(R.id.timePicker);
            timePicker.setIs24HourView(true);
            if ((hour>0) && (minute>0)) { // 输入的时间有效则更新时间
            	timePicker.setCurrentHour(hour);
            	timePicker.setCurrentMinute(minute);
            }
         
            cbOneOff = (CheckBox)customView.findViewById(R.id.checkBoxOneTime);
            cbOneOff.setOnCheckedChangeListener(cbOneTimeCheckedChangeListener);
            
            cbEveryday = (CheckBox)customView.findViewById(R.id.checkBoxEveryday);
            
            
            cbMonday = (CheckBox)customView.findViewById(R.id.checkBoxMonday);
            cbMonday.setOnCheckedChangeListener(cbMondayCheckedChangeListener);
            
            cbTuesday = (CheckBox)customView.findViewById(R.id.checkBoxTuseday);
            cbTuesday.setOnCheckedChangeListener(cbTuesdayCheckedChangeListener);
            
            cbWednesday = (CheckBox)customView.findViewById(R.id.checkBoxWensday);
            cbWednesday.setOnCheckedChangeListener(cbWensdayCheckedChangeListener);
            
            cbThursday = (CheckBox)customView.findViewById(R.id.checkBoxThursday);
            cbThursday.setOnCheckedChangeListener(cbTursdayCheckedChangeListener);
            
            cbFriday = (CheckBox)customView.findViewById(R.id.checkBoxFriday);
            cbFriday.setOnCheckedChangeListener(cbFridayCheckedChangeListener);
            
            cbSaturday = (CheckBox)customView.findViewById(R.id.checkBoxSaturday);
            cbSaturday.setOnCheckedChangeListener(cbSaturdayCheckedChangeListener);
            
            cbSunday = (CheckBox)customView.findViewById(R.id.checkBoxSunday);
            cbSunday.setOnCheckedChangeListener(cbFridayCheckedChangeListener);
            
            if (positiveButtonClickListener != null) {  
                ((Button)customView.findViewById(R.id.buttonSave))  
                        .setOnClickListener(new View.OnClickListener() {  
                            public void onClick(View v) {  
                                positiveButtonClickListener.onClick(dialog,  
                                        DialogInterface.BUTTON_POSITIVE);  
                            }  
                        });  
            }
            
            if (negativeButtonClickListener != null) {  
                ((Button)customView.findViewById(R.id.buttonCancel))  
                        .setOnClickListener(new View.OnClickListener() {  
                            public void onClick(View v) {  
                                negativeButtonClickListener.onClick(dialog,  
                                        DialogInterface.BUTTON_NEGATIVE);  
                            }  
                        });  
            } 
 
            dialog.setContentView(customView);  
            return dialog;  
        }  
    	
    	/** 一次性 被点击 */
    	OnCheckedChangeListener cbOneTimeCheckedChangeListener = new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) { //选中一次性
					cbMonday.setChecked(false);
					cbTuesday.setChecked(false);
					cbWednesday.setChecked(false);
					cbThursday.setChecked(false);
					cbFriday.setChecked(false);
					cbSaturday.setChecked(false);
					cbSunday.setChecked(false);
				} else { // 一次性未选中
					
				}
			}
    		
    	};
    	
    	OnCheckedChangeListener cbEverydayCheckedChangeListener = new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked) { // 选中每天
					cbOneOff.setChecked(false);
					cbMonday.setChecked(true);
					cbTuesday.setChecked(true);
					cbWednesday.setChecked(true);
					cbThursday.setChecked(true);
					cbFriday.setChecked(true);
					cbSaturday.setChecked(true);
					cbSunday.setChecked(true);
				}
				
			}
    		
    	};
    	
    	OnCheckedChangeListener cbMondayCheckedChangeListener = new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) { // 选中周一
					cbOneOff.setChecked(false);
				} else { // 周一未选中
				
				}
			}
    		
    	};
    	
    	OnCheckedChangeListener cbTuesdayCheckedChangeListener = new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) { // 选中周二
					cbOneOff.setChecked(false);
				} else { // 周二未选中
				
				}
			}
    		
    	};
    	
    	OnCheckedChangeListener cbWensdayCheckedChangeListener = new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) { // 选中周三
					cbOneOff.setChecked(false);
				} else { // 周三未选中
				
				}
			}
    		
    	};
    	
    	OnCheckedChangeListener cbTursdayCheckedChangeListener = new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) { // 选中周四
					cbOneOff.setChecked(false);
				} else { // 周四未选中
				
				}
			}
    		
    	};
    	
    	OnCheckedChangeListener cbFridayCheckedChangeListener = new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) { // 选中周五
					cbOneOff.setChecked(false);
				} else { // 周五未选中
				
				}
			}
    		
    	};
    	
    	OnCheckedChangeListener cbSaturdayCheckedChangeListener = new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) { // 选中周六
					cbOneOff.setChecked(false);
				} else { // 周六未选中
				
				}
			}
    		
    	};
    	
    	OnCheckedChangeListener cbSundayCheckedChangeListener = new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) { // 选中周日
					cbOneOff.setChecked(false);
				} else { // 周日未选中
				
				}
			}
    		
    	};
    	
    	OnCheckedChangeListener cbOpenCheckedChangeListener = new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked) {
					cbClose.setChecked(false);
				}
			}
    		
    	};
    	
    	OnCheckedChangeListener cbCloseCheckedChangeListener = new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked) {
					cbOpen.setChecked(false);
				}
			}
    		
    	};
    	
	}
	
}
