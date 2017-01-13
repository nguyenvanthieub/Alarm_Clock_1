package com.example.framgia.alarmclock.data.model;

import com.example.framgia.alarmclock.data.Constants;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by framgia on 12/07/2016.
 */
public class Alarm extends RealmObject {
    @PrimaryKey
    private int mId;
    private long mTime;
    private boolean mIsEnabled;
    private boolean mIsChecked;

    public Alarm() {
    }

    public int getId() {
        return mId;
    }

    public long getTime() {
        return mTime;
    }

    public boolean isEnabled() {
        return mIsEnabled;
    }

    public String getFormattedTime() {
        return new SimpleDateFormat(Constants.ALARM_TIME_FORMAT).format(new Date(mTime));
    }

    public int getFormattedTimeHours() {
        return Integer.parseInt(new SimpleDateFormat(Constants.ALARM_TIME_FORMAT_HOURS)
                .format(new Date(mTime)));
    }

    public int getFormattedTimeMinute() {
        return Integer.parseInt(new SimpleDateFormat(Constants.ALARM_TIME_FORMAT_MINUTE)
                .format(new Date(mTime)));
    }

    public boolean isChecked() {
        return mIsChecked;
    }

    public void setChecked(boolean checked) {
        mIsChecked = checked;
    }

    public void setEnabled(boolean enabled) {
        mIsEnabled = enabled;
    }

    public void setTime(long time) {
        mTime = time;
    }

    public void setId(int id) {
        mId = id;
    }
}
