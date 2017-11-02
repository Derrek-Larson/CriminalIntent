package com.bignerdranch.android.criminalintent;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by derrek1 on 9/28/17. resume at listing 8.24
 */

public class CrimeListFragment extends Fragment {

    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";

    private static final String POSITION_ARRAY_EXTRA = "com.bignerdranch.android.criminalintent.position_array";

    private static final String POSITION_EXTRA =
            "com.bignerdranch.android.criminalintent.crime_position";

    private static final String TAG = "CrimeListFragment";

private RecyclerView mCrimeRecyclerView;
private CrimeAdapter mAdapter;
private Button mEmptyViewAddButton;
    private View mEmptyView;
private boolean mSubtitleVisible;
    private static final int REQUEST_CRIME = 1;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);
        mCrimeRecyclerView= (RecyclerView) view
                .findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        if (savedInstanceState!=null){
            mSubtitleVisible=savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }
        mEmptyViewAddButton = (Button) view.findViewById(R.id.empty_view_add_button);
        mEmptyViewAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addACrime();
            }
        });
        setEmptyView(mCrimeRecyclerView);
        updateUI();
        return view;
    }

    @Override public void onResume(){
        super.onResume();
        updateUI();
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible);
    }

    private void addACrime() {
        Crime crime = new Crime();
        CrimeLab.get(getActivity()).addCrime(crime);
        Intent intent = CrimePagerActivity.newIntent(getActivity(), crime.getId(),mAdapter.getItemCount() );
        startActivity(intent);
    }
    private void updateEmptyView() {
        if (mEmptyView != null && mCrimeRecyclerView.getAdapter() != null) {
            boolean showEmptyView = mCrimeRecyclerView.getAdapter().getItemCount() == 0;
            mEmptyView.setVisibility(showEmptyView ? View.VISIBLE : View.GONE);
            mCrimeRecyclerView.setVisibility(showEmptyView ? View.GONE : View.VISIBLE);
        }
    }
    public void setEmptyView(View emptyView) {
        mEmptyView = emptyView;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);
        MenuItem subtitleItem = menu.findItem(R.id.show_subtitle);
        if(mSubtitleVisible){
            subtitleItem.setTitle(R.string.hide_subtitle);
        }
        else{
            subtitleItem.setTitle(R.string.show_subtitle);
        }

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.new_crime:
                addACrime(); // recently added
                return true;
            case R.id.show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void updateSubtitle(){
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        int crimeCount = crimeLab.getCrimes().size();
        String subtitle = getString(R.string.subtitle_format, crimeCount);

        if (!mSubtitleVisible){
            subtitle = null;
        }

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }
    private void updateUI(){
        Log.d(TAG, "updateUI called");
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();
        if (mAdapter == null){
            mAdapter = new CrimeAdapter(crimes);
            mCrimeRecyclerView.setAdapter(mAdapter);
        }
        else{
           mAdapter.setCrimes(crimes);
            mAdapter.notifyDataSetChanged();
            mAdapter = new CrimeAdapter(crimes);
            mCrimeRecyclerView.swapAdapter(mAdapter, false);

        }
        updateSubtitle();
        updateEmptyView();
    }
    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener { //essential component of a recycler, holds and specifies a view

        private TextView mTitleTextView;
        private TextView mDateTextView;
        private Crime mCrime;
        private ImageView mSolvedImageView;
        private TextView mPoliceTextView;
        private int position;

        public CrimeHolder(LayoutInflater inflater, ViewGroup parent){
            super(inflater.inflate(R.layout.list_item_crime, parent, false));
            itemView.setOnClickListener(this);
            mTitleTextView = (TextView) itemView.findViewById(R.id.crime_title);
            mDateTextView = (TextView) itemView.findViewById(R.id.crime_date);
            mPoliceTextView = (TextView) itemView.findViewById(R.id.requires_police);
            mSolvedImageView = (ImageView) itemView.findViewById(R.id.crime_solved);


        }
        @Override
        public void onClick(View view){

            Intent intent = CrimePagerActivity.newIntent(getActivity(), mCrime.getId(), position);
            startActivityForResult(intent, REQUEST_CRIME);
        }

       //chapter 9 challenge below
        public void bind(Crime crime, int position){
            mCrime = crime;
            DateFormat dateMaker = new DateFormat();
            CharSequence shortScopeDateCharS;
            Date shortRangeDate = mCrime.getDate();
            shortScopeDateCharS = dateMaker.format("EEEE MMMM dd, yyyy", shortRangeDate);
            mTitleTextView.setText(mCrime.getTitle());
            mDateTextView.setText(shortScopeDateCharS);
            //mDateTextView.setText(mCrime.getDate().toString());
            mSolvedImageView.setVisibility(crime.isSolved() ? View.VISIBLE :View.GONE);
            mPoliceTextView.setVisibility(crime.getMrequiresPolice() ? View.VISIBLE : View.GONE);
            this.position = position;

            //}

        }
    } // DELETING THIS MADE IT WORK HAHAHHAHAHAHAHAHHAHA
//    @Override public void onActivityResult(int requestCode, int resultCode, Intent data){ //happens when result is retrieved HAHA  DELETING THIS MAKES IT WORK SOMEHOW
//    if (requestCode == REQUEST_CRIME){
//        if(data.getIntArrayExtra(POSITION_ARRAY_EXTRA)!=null){
//        int[] positionArray;
//        positionArray = data.getIntArrayExtra(POSITION_ARRAY_EXTRA); //position comes back from the data passed by result of crimefragment
//       //array confirmed to hold new value
//        Log.d(TAG, "positionArray length "+positionArray.length);
//        mAdapter.notifyItemRangeChanged(positionArray[0], positionArray.length);
//        updateUI();}
//        else{mAdapter.notifyDataSetChanged();}
//    }
   // }
    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder>{
        private List<Crime> mCrimes;

        @Override
        public int getItemViewType(int position) {
            Crime cCrime = mCrimes.get(position);
            if(cCrime.getMrequiresPolice()){ //method implemented, now add logic to use list_item_crime_requires_police
                return 1;
            }
            else return 0;
        }

        public CrimeAdapter(List<Crime> crimes){
            mCrimes=crimes;
        }
        public void setCrimes(List<Crime> crimes){
            mCrimes=crimes;
        }
    @Override
        public CrimeHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        return new CrimeHolder(layoutInflater, parent);

    }
    @Override
        public void onBindViewHolder(CrimeHolder holder, int position){
        Crime crime = mCrimes.get(position);
        holder.bind(crime, position);
    }
    @Override
        public int getItemCount(){
        return mCrimes.size();
    }
    }


}
