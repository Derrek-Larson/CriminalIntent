package com.bignerdranch.android.criminalintent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by derrek1 on 10/17/17.
 */

public class TimePickerFragment extends DialogFragment {
    public static final String EXTRA_TIME = "timeExtraData";
    private static final String ARG_DATE = "date";
    private TimePicker mTimePicker;
    private Calendar mCalendar;
    public static TimePickerFragment newInstance(Date date) {
        TimePickerFragment fragment = new TimePickerFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, date);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_time, null);

        mTimePicker = (TimePicker) v.findViewById(R.id.dialog_time_picker);
        //get current time
        mTimePicker.setIs24HourView(true);

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.date_picker_title)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which){
                        Date date1 = (Date) getArguments().getSerializable(ARG_DATE);
                        mCalendar = Calendar.getInstance();
                        mCalendar.setTime(date1);
                        int year = mCalendar.get(Calendar.YEAR);
                        int month = mCalendar.get(Calendar.MONTH);
                        int day = mCalendar.get(Calendar.DAY_OF_MONTH);
                        int hour = mTimePicker.getCurrentHour();
                        int minute = mTimePicker.getCurrentMinute();
                        Date date = new GregorianCalendar(year,month, day,hour,minute).getTime();
                        sendResult(Activity.RESULT_OK, date);
                    }
                })
                .create();

    }

    private void sendResult(int resultCode, Date date){
        if (getTargetFragment() == null){
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_TIME,date);

        getTargetFragment()
                .onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
