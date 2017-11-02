package com.bignerdranch.android.criminalintent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.bignerdranch.android.criminalintent.database.CrimeBaseHelper;
import com.bignerdranch.android.criminalintent.database.CrimeCursorWrapper;
import com.bignerdranch.android.criminalintent.database.CrimeDbSchema;
import com.bignerdranch.android.criminalintent.database.CrimeDbSchema.CrimeTable;

import java.io.File;
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
    //private Map<UUID, Crime> mCrimes;
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
       // mCrimes = new LinkedHashMap<>();

    }

    public void addCrime(Crime c){
        //mCrimes.put(c.getId(),c);
        ContentValues values = getContentValues(c);
        mDatabase.insert(CrimeDbSchema.CrimeTable.NAME, null, values);
    }
    public void deleteCrime(Crime crime){
       // mCrimes.remove(c);
        mDatabase.delete(
                 CrimeDbSchema.CrimeTable.NAME,
                CrimeDbSchema.CrimeTable.Cols.UUID + "=?",
                new String[] {crime.getId().toString()}
        )       ;

    }

    public List<Crime> getCrimes(){
        List<Crime> crimes = new ArrayList<>();
        CrimeCursorWrapper cursor = queryCrimes(null,null);
        try{
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                crimes.add(cursor.getCrime());
                cursor.moveToNext();}
            }finally{
                cursor.close();
        }
        return crimes;
    }
    public File getPhotoFile(Crime crime){
        File filesDir = mContext.getFilesDir();
        return new File(filesDir, crime.getPhotoFilename());

    }
     //arraylist for all other purposes

    public Crime getCrime(UUID id){
        //return mCrimes.get(id); //instant retrieval
         CrimeCursorWrapper cursor = queryCrimes(
                 CrimeTable.Cols.UUID+" = ?",
                 new String[]{ id.toString()}
         );
        try{
            if(cursor.getCount() == 0){
                return null;
            } cursor.moveToFirst();
            return cursor.getCrime();
        }    finally{
            cursor.close();
        }
    }
    public void updateCrime(Crime crime){
        String uuidString = crime.getId().toString();
        ContentValues values = getContentValues(crime);

        mDatabase.update(CrimeTable.NAME, values,
                CrimeTable.Cols.UUID + " =?",
                new String[]{uuidString});
    }

    private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs){     // page 282 for ref on how to retrieve data from cursor
        Cursor cursor = mDatabase.query(
        CrimeTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null

        );
        return new CrimeCursorWrapper(cursor);
    }

    private static ContentValues getContentValues(Crime crime){
        ContentValues values = new ContentValues();
        values.put(CrimeTable.Cols.UUID, crime.getId().toString() );
        values.put(CrimeTable.Cols.TITLE, crime.getTitle() );
        values.put(CrimeTable.Cols.DATE, crime.getDate().getTime() );
        values.put(CrimeTable.Cols.SOLVED, crime.isSolved() ? 1:0);
        values.put(CrimeTable.Cols.POLICE, crime.getMrequiresPolice() ? 1:0 );
        values.put(CrimeTable.Cols.SUSPECT, crime.getSuspect());
        values.put(CrimeTable.Cols.SUSPECTID, crime.getSuspectId())          ;
       // values.put(CrimeTabe.Cols.SUSPECT, crime.getSuspect() ); // add suspect and susId var to crime
        return values;
    }
}

