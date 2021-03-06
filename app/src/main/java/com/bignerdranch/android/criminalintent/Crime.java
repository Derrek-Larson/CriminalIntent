package com.bignerdranch.android.criminalintent;
import java.util.UUID;
import java.util.Date;
/**
 * Created by derrek1 on 9/28/17.
 */

public class Crime {
    private UUID mId;
    private String mTitle;
    private Date mDate;
    private Boolean mSolved=false;
    private Boolean mRequiresPolice = false;
    private String mTime;
    private int mSuspectId;
    private String mSuspect;
    public int getSuspectId() {
        return mSuspectId;
    }

    public void setSuspectId(int suspectId) {
        mSuspectId = suspectId;
    }

    public String getSuspect() {
        return mSuspect;
    }

    public String getPhotoFilename(){
        return ("IMG_"+getId().toString()+".jpg");
    }

    public void setSuspect(String suspect) {
        mSuspect = suspect;
    }

    public Boolean getMrequiresPolice() {
        return mRequiresPolice;
    }

    public void setMrequiresPolice(Boolean mRequiresPolice) {
        this.mRequiresPolice = mRequiresPolice;
    }


    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public Date getDate() {
        return mDate;
    }

    public Boolean isSolved() {
        return mSolved;
    }

    public Crime() {
        this(UUID.randomUUID());
    }
    public Crime(UUID id){
        mId = id;
        mDate = new Date();
    }
    public void setTitle(String title) {
        mTitle = title;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public void setSolved(Boolean solved) {
        mSolved = solved;
    }

    public void setTime(String time){
        mTime = time;
    }
}
