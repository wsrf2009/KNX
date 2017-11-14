package com.sation.knxcontroller.services;

import android.app.Service;
import android.content.Intent;
import android.content.pm.FeatureInfo;
import android.os.IBinder;

import com.sation.knxcontroller.STKNXControllerApp;
import com.sation.knxcontroller.STKNXControllerConstant;
import com.sation.knxcontroller.control.TimingTaskItem;
import com.sation.knxcontroller.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class TimingService extends Service {
    final private String TAG = "TimingService";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int retval = super.onStartCommand(intent, flags, startId);
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH)+1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY); /* 24小时制的时间 */
        int minute = c.get(Calendar.MINUTE);
        int second = c.get(Calendar.SECOND);

        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        boolean refreshUI = false;

        Map<String, List<TimingTaskItem>> timerTaskMap = STKNXControllerApp.getInstance().getTimerTaskMap();
//        Log.i(TAG, "timerTaskMap:" + timerTaskMap);
        if(null != timerTaskMap) {
            for (Map.Entry<String, List<TimingTaskItem>> taskMap : timerTaskMap.entrySet()) { // 枚举定时任务按钮所对应的定时任务列表
                List<TimingTaskItem> delList = new ArrayList<TimingTaskItem>();
                List<TimingTaskItem> taskList = taskMap.getValue();
                for (TimingTaskItem item : taskList) { // 枚举定时任务列表下的每个定时任务
                    if (item.getIsPaused()) {
                        continue;
                    }

                    boolean execute = false;
                    boolean delete = false;

                    if (item.isOneOffSelected()) { // 若是一次性任务
                        if ((year == item.getYear()) && (month == item.getMonth()) && (day == item.getDay()) &&
                                (hour == item.getHour()) && (minute == item.getMinute()) && (second == item.getSecond())) { // 当前时间与任务所指时间一致
                            execute = true; // 执行任务
                            delete = true; // 从定时任务列表移除该任务
                        }
                    } else if (item.isEverydaySelected()) { // 若是每天都要执行的任务
                        if ((hour == item.getHour()) && (minute == item.getMinute()) && (second == item.getSecond())) {
                            execute = true; // 执行任务
                        }
                    } else if (item.isWeeklySelected()) { // 若是周期性的
                        if ((hour == item.getHour()) && (minute == item.getMinute()) && (second == item.getSecond())) {
                            if (1 == dayOfWeek) { // 周日
                                if (item.isSundaySelected()) {
                                    execute = true; // 执行任务
                                }
                            } else if (2 == dayOfWeek) { // 周一
                                if (item.isMondaySelected()) {
                                    execute = true; // 执行任务
                                }
                            } else if (3 == dayOfWeek) { // 周二
                                if (item.isTuesdaySelected()) {
                                    execute = true; // 执行任务
                                }
                            } else if (4 == dayOfWeek) { // 周三
                                if (item.isWednesdaySelected()) {
                                    execute = true; // 执行任务
                                }
                            } else if (5 == dayOfWeek) { // 周四
                                if (item.isThursdaySelected()) {
                                    execute = true; // 执行任务
                                }
                            } else if (6 == dayOfWeek) { // 周五
                                if (item.isFridaySelected()) {
                                    execute = true; // 执行任务
                                }
                            } else if (7 == dayOfWeek) { // 周六
                                if (item.isSaturdaySelected()) {
                                    execute = true; // 执行任务
                                }
                            }
                        }
                    } else if (item.isMonthlySelected()) { // 每月
                        if ((day == item.getDay()) && (hour == item.getHour()) && (minute == item.getMinute()) && (second == item.getSecond())) {
                            execute = true; // 执行任务
                        }
                    } else if (item.isLoopSelected()) { // 循环
						/* 计数器减一秒 */
                        if (item.decCounterSecond > 0) {
                            item.decCounterSecond--;
                        } else {
                            if (item.decCounterMinute > 0) {
                                item.decCounterSecond = 59;
                                item.decCounterMinute--;
                            } else {
                                if (item.decCounterHour > 0) {
                                    item.decCounterSecond = 59;
                                    item.decCounterMinute = 59;
                                    item.decCounterHour--;
                                }
                            }
                        }

						/* 计数器减完，则执行任务，并开始下一轮计数 */
                        if ((0 >= item.decCounterHour) && (0 >= item.decCounterMinute) && (0 >= item.decCounterSecond)) {
                            item.decCounterSecond = item.getIntervalSecond();
                            item.decCounterMinute = item.getIntervalMinute();
                            item.decCounterHour = item.getIntervalHour();

                            execute = true; // 执行任务
                        }

                        int newHour = hour + item.decCounterHour;
                        int newMinute = minute + item.decCounterMinute;
                        int newSecond = second + item.decCounterSecond;
                        if (newSecond >= 60) {
                            newSecond -= 60;
                            newMinute += 1;
                        }
                        if (newMinute >= 60) {
                            newMinute -= 60;
                            newHour += 1;
                        }
                        if (newHour != item.getHour()) {
                            item.setHour(newHour);
                            item.setMinute(newMinute);
                            item.setSecond(newSecond);
                            refreshUI = true;
                        } else if (newMinute != item.getMinute()) {
                            item.setMinute(newMinute);
                            item.setSecond(newSecond);
                            refreshUI = true;
                        } else if (newSecond != item.getSecond()) {
                            item.setSecond(newSecond);
                            refreshUI = true;
                        }
                    }

                    if (execute) {
                        new Thread(item).start();
                    }

                    if (delete) {
                        delList.add(item);
                    }
                }

                if (delList.size() > 0) {
                    taskList.removeAll(delList);
                    refreshUI = true;
                }
            }
        }
        if (refreshUI) {
            Intent mIntent = new Intent(STKNXControllerConstant.BROADCAST_REFRESH_TIMING_TASK_LIST);
            sendBroadcast(mIntent);
        }

        return  retval;
    }
}
