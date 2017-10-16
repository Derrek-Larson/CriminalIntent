package com.bignerdranch.android.criminalintent;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static android.content.ContentValues.TAG;

/**
 * Created by derrek1 on 9/28/17.
 */

public class CrimeLab {
    private static CrimeLab sCrimeLab;
    private Map<UUID, Crime> mCrimes; //uuid key, crime objects
    public static CrimeLab get(Context context){
        if (sCrimeLab==null){
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }
    private CrimeLab(Context context){
        mCrimes = new LinkedHashMap<>();
        for (int i=0; i<100; i++){
            Crime crime = new Crime();
            crime.setTitle("Crime #"+i);
            crime.setSolved(i%2==0);
            crime.setMrequiresPolice(i%2==0); // only needed for challenge of chapter 8
            mCrimes.put(crime.getId(),crime);
            Log.d(TAG, "crime created");

        }
    }
    public List<Crime> getCrimes(){
        return new ArrayList<>(mCrimes.values());
    } //arraylist for all other purposes
    public Crime getCrime(UUID id){
        return mCrimes.get(id); //instant retrieval
    }
}
