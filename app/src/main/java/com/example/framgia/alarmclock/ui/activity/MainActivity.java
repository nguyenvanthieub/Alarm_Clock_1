package com.example.framgia.alarmclock.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AnalogClock;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;

import com.example.framgia.alarmclock.R;
import com.example.framgia.alarmclock.data.Constants;
import com.example.framgia.alarmclock.data.controller.AlarmRepository;
import com.example.framgia.alarmclock.data.model.Alarm;
import com.example.framgia.alarmclock.utility.AlarmUtils;
import com.example.framgia.alarmclock.utility.MusicPlayerUtils;
import com.example.framgia.alarmclock.utility.ToastUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final static int DEFAULT_NULL = -1;
    private TextClock mTextClockHour, mTextClockSecond, mTextClockAmPm;
    private AnalogClock mAnalogClockThinLine;
    private TextView mTextViewHideHour, mTextViewHideSecond, mTextViewDay;
    private ImageView mImageViewAlarm, mImageViewStopAlarm;
    private GestureDetector mGestureDetector;
    private float mAlpha;
    private boolean mIscreated, mIsSlideFingers, mIsAlarmFinish;
    private SharedPreferences mSharedPreferences;
    private Typeface mTypeFaceDay, mTypeFaceTime;
    private Vibrator mVibrator;
    private Alarm mAlarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hideStatusBar();
        initViews();
        updateData();
        showAdvanced();
        onSimpleOnGestureListener();
        initAlarmData();
        if (savedInstanceState != null)
            mIsAlarmFinish = savedInstanceState.getBoolean(Constants.IS_ALARM_FINISH, false);
        setupAction();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(Constants.IS_ALARM_FINISH, mIsAlarmFinish);
    }

    private void initAlarmData() {
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        Intent intentAlarmData = getIntent();
        if (intentAlarmData != null) {
            mAlarm = AlarmRepository.getAlarmById(
                    intentAlarmData.getIntExtra(Constants.OBJECT_ID, Constants.DEFAULT_INTENT_VALUE));
        }
    }

    private void updateData() {
        mSharedPreferences = getSharedPreferences(Constants.SHARE_PREFERENCES,
                Context.MODE_PRIVATE);
        mIsSlideFingers = mSharedPreferences.getBoolean(Constants.SLIDE_FINGERS, true);
        mAlpha = Constants.ALPHA_MAX;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mIscreated = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mIscreated) {
            updateData();
            showAdvanced();
            hideStatusBar();
        }
    }

    private void initViews() {
        initTextClocks();
        initImageView();
        initTextView();
        initOnListener();
    }

    private void initTextView() {
        // text day
        mTextViewDay = (TextView) findViewById(R.id.text_day);
        mTextViewDay.setTypeface(mTypeFaceDay);
        // hide text
        mTextViewHideHour = (TextView) findViewById(R.id.text_hide_hour);
        mTextViewHideSecond = (TextView) findViewById(R.id.text_hide_second);
        mTextViewHideHour.setTypeface(mTypeFaceTime);
        mTextViewHideSecond.setTypeface(mTypeFaceTime);
    }

    private void initImageView() {
        // image icon
        mImageViewAlarm = (ImageView) findViewById(R.id.image_alarm);
        mImageViewStopAlarm = (ImageView) findViewById(R.id.image_stop_alarm);
    }

    private void initOnListener() {
        mImageViewAlarm.setOnClickListener(this);
        mImageViewStopAlarm.setOnClickListener(this);
    }

    private void initTextClocks() {
        mAnalogClockThinLine = (AnalogClock) findViewById(R.id.analog_clock_main);
        mTypeFaceTime = Typeface.createFromAsset(getAssets(), Constants.FONT_TIME);
        mTypeFaceDay = Typeface.createFromAsset(getAssets(), Constants.FONT_DAY);
        // text clock
        mTextClockHour = (TextClock) findViewById(R.id.text_clock_hour);
        mTextClockSecond = (TextClock) findViewById(R.id.text_clock_second);
        mTextClockAmPm = (TextClock) findViewById(R.id.text_clock_am_pm);
        mTextClockHour.setTypeface(mTypeFaceTime);
        mTextClockSecond.setTypeface(mTypeFaceTime);
        mTextClockAmPm.setTypeface(mTypeFaceDay);
    }

    private void hideStatusBar() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    private void showAdvanced() {
        showAdvancedTextClock();
        showAdvancedTypeClock();
        showAdvancedTextViewDay();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopAlarm();
    }

    private void stopAlarm() {
        if (mVibrator.hasVibrator()) {
            mVibrator.cancel();
        }
        MusicPlayerUtils.stopMusic();
        mIsAlarmFinish = true;
    }

    private void onSimpleOnGestureListener() {
        GestureDetector.SimpleOnGestureListener simpleOnGestureListener =
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                                           float velocityY) {
                        if (mIsSlideFingers) {
                            float y1 = e1.getY();
                            float y2 = e2.getY();
                            if (y1 > y2) {
                                onFadeInChangeBrightness(Constants.UP);
                            } else if (y1 < y2) {
                                onFadeInChangeBrightness(Constants.DOWN);
                            }
                        }
                        return super.onFling(e1, e2, velocityX, velocityY);
                    }
                };
        mGestureDetector = new GestureDetector(this, simpleOnGestureListener);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_alarm:
                openActivity(ListAlarmsActivity.class);
                break;
            case R.id.image_stop_alarm:
                stopAlarm();
                mImageViewStopAlarm.setVisibility(View.INVISIBLE);
                break;
        }
    }

    private void openActivity(Class myClass) {
        Intent intent = new Intent(this, myClass);
        startActivity(intent);
        stopAlarm();
    }

    private void showAdvancedTextViewDay() {
        mTextViewDay.setText(new SimpleDateFormat(Constants.FORMAT_DAY_SHORT)
                .format(Calendar.getInstance().getTime()).toUpperCase());
        boolean showDay = mSharedPreferences.getBoolean(Constants.SHOW_DAY, true);
        mTextViewDay.setVisibility(showDay ? View.VISIBLE : View.INVISIBLE);
    }

    private void showAdvancedTextClock() {
        boolean use24HourFormat = mSharedPreferences.getBoolean(Constants.USE_24_HOUR_FORMAT, true);
        if (use24HourFormat) {
            mTextClockHour.setFormat12Hour(null);
            mTextClockHour.setFormat24Hour(Constants.FORMAT_TIME_24_HOUR);
        } else {
            mTextClockHour.setFormat12Hour(Constants.FORMAT_TIME_12_HOUR);
            mTextClockHour.setFormat24Hour(null);
        }
        mTextClockAmPm.setFormat12Hour(Constants.FORMAT_AM_PM);
        mTextClockAmPm.setFormat24Hour(null);
        mTextClockSecond.setFormat12Hour(null);
        mTextClockSecond.setFormat24Hour(Constants.FORMAT_TIME_SECOND);
        mTextClockAmPm.setVisibility(use24HourFormat ? View.INVISIBLE : View.VISIBLE);
    }

    private void showAdvancedTypeClock() {
        int typeClock =
                mSharedPreferences.getInt(Constants.TYPE_CLOCKS, Constants.TYPE_CLOCKS_WHITE);
        switch (typeClock) {
            case Constants.TYPE_CLOCKS_WHITE:
                showColorViews(Color.WHITE, R.color.colorWhite);
                showAnalog(false);
                break;
            case Constants.TYPE_CLOCKS_BLUE:
                showColorViews(Color.BLUE, R.color.colorBlue);
                showAnalog(false);
                break;
            case Constants.TYPE_CLOCKS_GREEN:
                showColorViews(Color.GREEN, R.color.colorGreen);
                showAnalog(false);
                break;
            case Constants.TYPE_CLOCKS_RED:
                showColorViews(Color.RED, R.color.colorRed);
                showAnalog(false);
                break;
            case Constants.TYPE_CLOCKS_YELLOW:
                showColorViews(Color.YELLOW, R.color.colorYellow);
                showAnalog(false);
                break;
            case Constants.TYPE_CLOCKS_ANALOG:
                showColorViews(Color.WHITE, R.color.colorWhite);
                showAnalog(true);
                mTextClockAmPm.setVisibility(View.INVISIBLE);
                break;
        }
    }

    private void showAnalog(boolean showAnalog) {
        boolean showSeconds = mSharedPreferences.getBoolean(Constants.SHOW_SECONDS, true);
        mTextClockHour.setVisibility(showAnalog ? View.INVISIBLE : View.VISIBLE);
        mTextClockSecond.setVisibility(showSeconds ? View.VISIBLE : View.INVISIBLE);
        mAnalogClockThinLine.setVisibility(showAnalog ? View.VISIBLE : View.INVISIBLE);
        mTextViewHideHour.setVisibility(showAnalog ? View.INVISIBLE : View.VISIBLE);
        mTextViewHideSecond
                .setVisibility(showSeconds && !showAnalog ? View.VISIBLE : View.INVISIBLE);
    }

    private void showColorViews(int color, int colorHide) {
        // text clock
        mTextClockHour.setTextColor(color);
        mTextClockAmPm.setTextColor(color);
        mTextClockSecond.setTextColor(color);
        // text view
        mTextViewDay.setTextColor(color);
        mTextViewHideHour.setTextColor(ContextCompat.getColor(this, colorHide));
        mTextViewHideSecond.setTextColor(ContextCompat.getColor(this, colorHide));
        // image view
        mImageViewAlarm.setColorFilter(color);
    }

    private void onFadeInChangeBrightness(int type) {
        switch (type) {
            case Constants.DOWN:
                onDownBrightness();
                break;
            case Constants.UP:
                onUpBrightness();
                break;
        }
    }

    private void onDownBrightness() {
        if (mAlpha > Constants.ALPHA_MIN) {
            mAlpha -= Constants.ALPHA_DELTA;
            setAlpha();
        }
    }

    private void setAlpha() {
        // text clock
        mTextClockHour.setAlpha(mAlpha);
        mTextClockAmPm.setAlpha(mAlpha);
        mTextClockSecond.setAlpha(mAlpha);
        // image view
        mImageViewAlarm.setAlpha(mAlpha);
        // text view
        mTextViewDay.setAlpha(mAlpha);
        // analog clock
        mAnalogClockThinLine.setAlpha(mAlpha);
    }

    private void onUpBrightness() {
        if (mAlpha < Constants.ALPHA_MAX) {
            mAlpha += Constants.ALPHA_DELTA;
            setAlpha();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    private void playRingAndVibrate() {
        if (mAlarm.isValid() && mVibrator.hasVibrator()) {
            mVibrator.vibrate(new long[]{Constants.TIME_VIBRATOR, Constants.TIME_VIBRATOR},
                    Constants.VIBRATOR_REPEAT);
        }
        MusicPlayerUtils.playMusic(this);
    }

    public void setupAction() {
        String action = getIntent().getAction();
        if (action == null) return;
        switch (action) {
            case Constants.ACTION_FULLSCREEN_ACTIVITY:
                if (!mIsAlarmFinish) {
                    playRingAndVibrate();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
}
