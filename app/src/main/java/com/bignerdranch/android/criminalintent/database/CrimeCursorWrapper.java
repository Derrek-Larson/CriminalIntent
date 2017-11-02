package com.bignerdranch.android.criminalintent.database;

import android.database.Cursor;
import android.database.CursorWrapper;
import com.bignerdranch.android.criminalintent.database.CrimeDbSchema.CrimeTable;
import com.bignerdranch.android.criminalintent.Crime;

import java.util.Date;
import java.util.UUID;

/**
 * Created by derrek1 on 10/26/17.
 */

public class CrimeCursorWrapper extends CursorWrapper {
    public CrimeCursorWrapper(Cursor cursor){
        super(cursor);
    }
    public Crime getCrime(){
        String uuidString = getString(getColumnIndex(CrimeTable.Cols.UUID));
        String title = getString(getColumnIndex(CrimeTable.Cols.TITLE));
        long date = getLong(getColumnIndex(CrimeTable.Cols.DATE));
        int isSolved = getInt(getColumnIndex(CrimeTable.Cols.SOLVED));
        int requiresPolice = getInt(getColumnIndex(CrimeTable.Cols.POLICE));
        String suspect = getString(getColumnIndex(CrimeTable.Cols.SUSPECT));
        int suspectId = getInt(getColumnIndex(CrimeTable.Cols.SUSPECTID));
        //String suspect = getString(getColumnIndex(CrimeTable.Cols.SUSPECT));
        //add suspect id as well String suspectId = getString(getColumnIndex(CrimeTable.Cols.SUSPECTID));
        Crime crime=  new Crime(UUID.fromString(uuidString));
        crime.setTitle(title);
        crime.setDate(new Date(date));
        crime.setSolved(isSolved !=0);
        crime.setMrequiresPolice(requiresPolice!=0);
        crime.setSuspect(suspect);
        crime.setSuspectId(suspectId);
        return crime;
    }

}
