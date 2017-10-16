package com.bignerdranch.android.criminalintent;

import android.support.v4.app.Fragment;

/**
 * Created by derrek1 on 9/28/17.
 */

public class CrimeListActivity extends SingleFragmentActivity {
    protected Fragment createFragment(){
        return new CrimeListFragment();
    }
}
