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
import android.support.v4.util.ArraySet;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import android.widget.Button;
import android.view.View;

/**
 * Created by derrek1 on 10/11/17.
 */

public class CrimePagerActivity extends AppCompatActivity{

    private static final String TAG = "CrimePagerActivity";

    private static final String POSITION_ARRAY_EXTRA = "com.bignerdranch.android.criminalintent.position_array";

    private static final String EXTRA_CRIME_ID =
            "com.bignerdranch.android.criminalintent.crime_id";
    private static final String POSITION_EXTRA=
            "com.bignerdranch.android.criminalintent.position";

    private ViewPager mViewPager;
    private List<Crime> mCrimes;
    private Set<Integer> visitedPages = new ArraySet<>();
    private int position;
    private Button mFirstButton;
    private Button mLastButton;

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
                visitedPages.add(position);
                Log.d(TAG, "Integer added to set: " + position);
                return CrimeFragment.newInstance(crime.getId());
            }

            @Override
            public int getCount() {
                return mCrimes.size();
            }
        });
        mViewPager.setCurrentItem(position);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                if (mViewPager.getCurrentItem() == 0) {
                    mFirstButton.setVisibility(View.INVISIBLE);
                } else {
                    mFirstButton.setVisibility(View.VISIBLE);
                }

                if (mViewPager.getCurrentItem() == mViewPager.getAdapter().getCount() - 1) {
                    mLastButton.setVisibility(View.INVISIBLE);
                } else {
                    mLastButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mFirstButton = (Button) findViewById(R.id.first_crime);
        mFirstButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(0);
            }
        });

        mLastButton = (Button) findViewById(R.id.last_crime);
        mLastButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(mViewPager.getAdapter().getCount() - 1);
            }
        });



    }
    public void returnResult(){ // fix this once extra for position is passed
        Intent intent = new Intent(); //blank intent
        int positionArray[];
         positionArray = toInt(visitedPages);
        for (int i=0; i<positionArray.length; i++){
            Log.d(TAG, "value in array at "+i+" = "+positionArray[i]);
        }
        intent.putExtra(POSITION_ARRAY_EXTRA, positionArray); //put info in intent
        setResult(Activity.RESULT_OK, intent); // pass intent w info with the result to crimelistfragment
    }
   @Override
    public void onBackPressed() {
        returnResult();
        super.onBackPressed();
    }
    public int[] toInt(Set<Integer> set) { //working i guess
        Integer[] b = set.toArray(new Integer[set.size()]);
      //  Log.d(TAG, "value of first b array spot is: " + b[0]);
        int a[] = new int[b.length];
        for(int i = 0; i<b.length;i++){
            a[i] = b[i];
          //  Log.d(TAG, "added value to i=" +a[i]);
        }
        return a;
    }


}
