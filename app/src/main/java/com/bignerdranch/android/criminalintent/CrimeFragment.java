package com.bignerdranch.android.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Button;
import android.text.format.DateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import static android.content.ContentValues.TAG;

/**
 * Created by derrek1 on 9/28/17. Resume page 146 listing 7.14
 */

public class CrimeFragment extends Fragment {
    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 7;
    private static final String DIALOG_TIME = "DialogTime";
    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;
    private Button mTimeButton;
    private CheckBox mRequiresPoliceCBox;
    Menu mMenu;
    MenuInflater mMenuInflater;


    public static CrimeFragment newInstance(UUID crimeID){
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeID);
        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
        setHasOptionsMenu(true);

    }
    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putSerializable(ARG_CRIME_ID, mCrime.getId());

        super.onSaveInstanceState(outState);
    }

    private void deleteCrime(){
        CrimeLab.get(getActivity()).deleteCrime(mCrime.getId());
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_crime_menu:
                deleteCrime();
                Log.d("CrimeFragment", "ActivityDeleted");
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater ) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime, menu);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);
        mTitleField = (EditText) v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int count, int after) {
                mCrime.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }


        });
        mDateButton = (Button) v.findViewById(R.id.crime_date);
        updateDate(mCrime.getDate());
        mDateButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });

        mTimeButton = (Button) v.findViewById(R.id.crime_time);
        updateTime(mCrime.getDate());
        mTimeButton.setOnClickListener(new View.OnClickListener(){
           @Override
            public void onClick(View v){
               FragmentManager manager = getFragmentManager();
               TimePickerFragment dialog = TimePickerFragment.newInstance(mCrime.getDate());
               dialog.setTargetFragment(CrimeFragment.this, REQUEST_TIME);
               dialog.show(manager, DIALOG_TIME);
           }
        });


        mSolvedCheckBox = (CheckBox)v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
            }
        });
        mRequiresPoliceCBox = (CheckBox)v.findViewById(R.id.requires_police);
        mRequiresPoliceCBox.setChecked(mCrime.getMrequiresPolice());
        mRequiresPoliceCBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                mCrime.setMrequiresPolice(isChecked);
            }
        });
        return v;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode != Activity.RESULT_OK){
            return;
        }
        if (requestCode == REQUEST_DATE){
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            updateDate(mCrime.getDate());
            updateTime(mCrime.getDate());
        }
        if(requestCode == REQUEST_TIME){
            Date date = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            mCrime.setDate(date);
            updateDate(mCrime.getDate());
            updateTime(mCrime.getDate());
        }
    }

    private void updateDate(Date shortRangeDate) { //formats and updates date button text
        DateFormat dateMaker = new DateFormat();
        CharSequence dateText = dateMaker.format("EEEE, MM/dd/yy", shortRangeDate);
        mDateButton.setText(dateText);
    }
    private void updateTime(Date shortRangeDate){ // formats and updates time button text
        DateFormat timeMaker = new DateFormat();
        CharSequence timeText = timeMaker.format("k:mm", shortRangeDate);
        mTimeButton.setText(timeText);
    }


}
