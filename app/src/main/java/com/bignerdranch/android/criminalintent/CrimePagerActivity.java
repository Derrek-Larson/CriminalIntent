package com.bignerdranch.android.criminalintent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.List;
import java.util.UUID;

/**
 * Created by derrek1 on 10/11/17.
 */

public class CrimePagerActivity extends AppCompatActivity{

    private static final String EXTRA_CRIME_ID =
            "com.bignerdranch.android.criminalintent.crime_id";
    private static final String POSITION_EXTRA=
            "com.bignerdranch.android.criminalintent.position";

    private ViewPager mViewPager;
    private List<Crime> mCrimes;

    private int position;

    public static Intent newIntent(Context packageContext, UUID crimeID, int position){
        Intent intent = new Intent(packageContext, CrimePagerActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeID);
        intent.putExtra(POSITION_EXTRA, position);
        return intent;
    }

    @Override
    protected void onCreate (Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_pager);

        UUID crimeId = (UUID) getIntent()
                .getSerializableExtra(EXTRA_CRIME_ID);

        position = getIntent().getIntExtra(POSITION_EXTRA, -1);

        mViewPager = (ViewPager) findViewById(R.id.crime_view_pager);

        mCrimes = CrimeLab.get(this).getCrimes();
        Log.d("CrimePager", "crimes: " + mCrimes.size());
        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                Log.d("position", "" + position);
                Log.d("current page", "" + mViewPager.getCurrentItem());
                Crime crime = mCrimes.get(position);
                return CrimeFragment.newInstance(crime.getId());
            }

            @Override
            public int getCount() {
                return mCrimes.size();
            }
        });
        mViewPager.setCurrentItem(position);

    }
    public void returnResult(){ // fix this once extra for position is passed
        Intent intent = new Intent(); //blank intent
        intent.putExtra(POSITION_EXTRA, position); //put info in intent
        setResult(Activity.RESULT_OK, intent); // pass intent w info with the result to crimelistfragment
    }
   @Override
    public void onBackPressed() {
        returnResult();
        super.onBackPressed();
    }
}