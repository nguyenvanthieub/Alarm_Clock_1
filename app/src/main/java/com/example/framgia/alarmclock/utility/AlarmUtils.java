package com.example.framgia.alarmclock.utility;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;

import com.example.framgia.alarmclock.data.Constants;
import com.example.framgia.alarmclock.data.controller.AlarmRepository;
import com.example.framgia.alarmclock.data.model.Alarm;
import com.example.framgia.alarmclock.service.SchedulingService;
import com.example.framgia.alarmclock.ui.activity.MainActivity;
import com.example.framgia.alarmclock.ui.alarms.AlarmBootReceiver;

import java.util.Calendar;
import java.util.List;

public class AlarmUtils {
    private static AlarmManager mAlarmManager;
    private static PendingIntent mPendingIntent;

    public static void startAlarm(Context context, int id) {
        Intent startAlarmIntent = new Intent(context, MainActivity.class);
        startAlarmIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startAlarmIntent.setAction(Constants.ACTION_FULLSCREEN_ACTIVITY);
        startAlarmIntent.putExtra(Constants.OBJECT_ID, id);
        context.startActivity(startAlarmIntent);
    }

    public static void setupAlarmBoot(Context context) {
        List<Alarm> alarmList = AlarmRepository.getAllAlarms();
        for (Alarm alarm : alarmList) {
            setAlarm(context, alarm);
        }
    }

    public static void setupAlarm(Context context, Alarm alarm) {
        if (alarm.isEnabled()) {
            setAlarm(context, alarm);
        } else {
            cancelAlarm(context, alarm);
        }
    }

    public static void setAlarm(Context context, Alarm alarm) {
        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, SchedulingService.class);
        intent.putExtra(Constants.OBJECT_ID, alarm.getId());
        mPendingIntent = PendingIntent.getService(context, alarm.getId(), intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        // add time
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, alarm.getFormattedTimeHours());
        calendar.set(Calendar.MINUTE, alarm.getFormattedTimeMinute());
        calendar.set(Calendar.SECOND, Constants.SECONDS_DEFAULT);
        if (calendar.before(Calendar.getInstance()))
            calendar.roll(Calendar.DAY_OF_WEEK, Constants.DEFAULT_UP_DAY);
        // check alarm
        setAlarmByVersionAPI(alarm, calendar);
        enabledAutoBoot(context, PackageManager.COMPONENT_ENABLED_STATE_ENABLED);
    }

    private static void setExactAlarmManager(Calendar calendar) {
        mAlarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                mPendingIntent);
    }

    private static void setAlarmManager(Calendar calendar) {
        mAlarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), mPendingIntent);
    }

    public static void cancelAlarm(Context context, Alarm alarm) {
        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, SchedulingService.class);
        intent.putExtra(Constants.OBJECT_ID, alarm.getId());
        mPendingIntent = PendingIntent.getService(context, alarm.getId(), intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mAlarmManager.cancel(mPendingIntent);
        enabledAutoBoot(context, PackageManager.COMPONENT_ENABLED_STATE_DISABLED);
    }

    private static void enabledAutoBoot(Context context, int enabled) {
        ComponentName componentName = new ComponentName(context, AlarmBootReceiver.class);
        PackageManager packageManager = context.getPackageManager();
        packageManager.setComponentEnabledSetting(componentName,
                enabled, PackageManager.DONT_KILL_APP);
    }

    private static void setAlarmByVersionAPI(Alarm alarm, Calendar calendar) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) setAlarmManager(calendar);
        else setExactAlarmManager(calendar);
    }
}
