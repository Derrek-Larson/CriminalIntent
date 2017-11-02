package com.bignerdranch.android.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.DialogFragment;

/**
 * Created by derrek1 on 10/17/17.
 */


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by derrek1 on 10/16/17.
 */

public class DatePickerFragment extends DialogFragment {

    private static final String ARG_DATE = "date";
    public static final String EXTRA_DATE = "com.bignerdranch.android.criminalintent.date";
    private static final String TAG = "temptag";
    private Calendar mCalendar = Calendar.getInstance();
    DatePicker mDatePicker;
    private Button mPositiveButton;
    private Button mNegativeButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (ScreenTypeHelper.getInstance(getActivity()).isPhone()) {
            View v = inflater.inflate(R.layout.date_picker, container, false);

            Date date = (Date) getArguments().getSerializable(ARG_DATE);

            if (date == null) {
                date = new Date();
            }
            Log.d(TAG, "we got that date here set as uhhhh "+ date.toString());
            mCalendar.setTime(date);
            int year = mCalendar.get(Calendar.YEAR);
            int month = mCalendar.get(Calendar.MONTH);
            int day = mCalendar.get(Calendar.DAY_OF_MONTH);

            mDatePicker = (DatePicker) v.findViewById(R.id.date_picker);
            mDatePicker.init(year, month, day, null);

            mPositiveButton = (Button) v.findViewById(R.id.dialog_date_ok_button);
            mPositiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int year = mDatePicker.getYear();
                    int month = mDatePicker.getMonth();
                    int day = mDatePicker.getDayOfMonth();
                    mCalendar.set(year, month, day);
                    Date date = mCalendar.getTime();
                    Log.d(TAG, "but that date is now uhhhhh "+ date.toString());
                    sendResult(Activity.RESULT_OK, date);
                    getActivity().finish();
                }
            });
            mNegativeButton = (Button) v.findViewById(R.id.dialog_date_cancel_button);
            mNegativeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().finish();
                }
            });

            return v;
        }
        return null;
    }

    public static DatePickerFragment newInstance(Date date){
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, date);
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void sendResult(int resultCode, Date date) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATE, date);

        if (getTargetFragment() == null) {
            getActivity().setResult(resultCode, intent);
        } else {
            getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
        }
    }
}