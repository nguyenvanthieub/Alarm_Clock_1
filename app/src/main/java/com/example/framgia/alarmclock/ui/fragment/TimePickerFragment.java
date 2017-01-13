package com.example.framgia.alarmclock.ui.fragment;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.widget.TimePicker;

import com.example.framgia.alarmclock.R;
import com.example.framgia.alarmclock.data.Constants;
import com.example.framgia.alarmclock.data.listener.OnTimeSetPickerListener;

/**
 * Created by framgia on 18/07/2016.
 */
public class TimePickerFragment extends DialogFragment implements
        TimePickerDialog.OnTimeSetListener {
    private final static int HOUR_OF_DAY = 0;
    private final static int MINUTE = 1;
    private static OnTimeSetPickerListener mOnTimeSetPickerListener;
    private String mTimePicker;

    public static TimePickerFragment newInstance(OnTimeSetPickerListener onTimeSetPickerListener,
                                                 String timePicker) {
        mOnTimeSetPickerListener = onTimeSetPickerListener;
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TIME_SLEEP_ON_ROTATE_CHANGE, timePicker);
        TimePickerFragment timePickerFragment = new TimePickerFragment();
        timePickerFragment.setArguments(bundle);
        return timePickerFragment;
    }

    public static String getFormatTime(Context context, int hourOfDay, int minute) {
        return String.format(context.getResources().getString(R.string.time_picker_default),
                hourOfDay, minute);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTimePicker = getArguments().getString(Constants.TIME_SLEEP_ON_ROTATE_CHANGE,
                getString(R.string.timer_sleep_default));
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String[] arr = mTimePicker.split(":");
        int hourOfDay = Integer.parseInt(arr[HOUR_OF_DAY]);
        int minute = Integer.parseInt(arr[MINUTE]);
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(Constants
                .SHARE_PREFERENCES, Context.MODE_PRIVATE);
        int typePicker = sharedPreferences.getInt(Constants.TYPE_PICKER, Constants.TYPE_ANALOG);
        if (typePicker == Constants.TYPE_DIGITAL) {
            return new TimePickerDialog(getContext(),
                    R.style.myTimePickerStyle, this, hourOfDay, minute, true);
        }
        return new TimePickerDialog(getContext(), this, hourOfDay, minute, true);
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
        if (mOnTimeSetPickerListener != null)
            mOnTimeSetPickerListener.onTimeSetPicker(hourOfDay, minute);
    }
}
