package com.bignerdranch.android.criminalintent.database;

import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by derrek1 on 10/26/17.
 */


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.bignerdranch.android.criminalintent.database.CrimeDbSchema.CrimeTable;

/**
 * Created by derrek1 on 10/26/17.
 */

public class CrimeBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "crimeBase.db";

    public CrimeBaseHelper(Context context){
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

    }
    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("create table " + CrimeTable.NAME+ "("+" _id integer primary key autoincrement, "+ CrimeTable.Cols.UUID+", "+
        CrimeTable.Cols.TITLE + ", "+
        CrimeTable.Cols.DATE+", "+
                CrimeTable.Cols.SOLVED+", "+
        CrimeTable.Cols.POLICE+", "+
        CrimeTable.Cols.SUSPECT+", "+
        CrimeTable.Cols.SUSPECTID+")");
    }
}
