package com.bignerdranch.android.criminalintent;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.bignerdranch.android.criminalintent.database.CrimeBaseHelper;

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
    private Map<UUID, Crime> mCrimes;
    private Context mContext;
    private SQLiteDatabase mDatabase;
    //uuid key, crime objects
    public static CrimeLab get(Context context){
        if (sCrimeLab==null){
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    private CrimeLab(Context context){
        mContext = context.getApplicationContext();
        mDatabase = new CrimeBaseHelper(mContext)
                .getWritableDatabase();
        mCrimes = new LinkedHashMap<>();

    }

    public void addCrime(Crime c){
        mCrimes.put(c.getId(),c);
    }
    public void deleteCrime(UUID c){mCrimes.remove(c);}

    public List<Crime> getCrimes(){
        return new ArrayList<>(mCrimes.values());
    } //arraylist for all other purposes

    public Crime getCrime(UUID id){
        return mCrimes.get(id); //instant retrieval
    }
}

