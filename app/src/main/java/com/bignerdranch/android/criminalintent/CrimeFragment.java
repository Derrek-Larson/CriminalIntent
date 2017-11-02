package com.bignerdranch.android.criminalintent;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.widget.CompoundButton;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Button;
import android.text.format.DateFormat;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static android.content.ContentValues.TAG;
import static com.bignerdranch.android.criminalintent.DatePickerFragment.EXTRA_DATE;

/**
 * Created by derrek1 on 9/28/17. Resume page 146 listing 7.14
 */

public class CrimeFragment extends Fragment {
    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 7;
    private static final String DIALOG_TIME = "DialogTime";
    private static final int REQUEST_CONTACT = 1;
    private static final int REQUEST_PHOTO=2;
    public static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 6;
    private static final String CRIME_FRAGMENT = "CrimeFragment";
    private static final String DIALOG_IMAGE = "DialogImage";
    private static final int ACTIVITY_REQUEST_DATE = 9;
    private Crime mCrime;
    private File mPhotoFile;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;
    private Button mTimeButton;
    private Button mReportButton;
    private CheckBox mRequiresPoliceCBox;
    private Button mSuspectButton;
    Menu mMenu;
    MenuInflater mMenuInflater;
    private Button mCallSuspectButton;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private ViewTreeObserver mPhotoTreeObserver;
    private Point mPhotoViewSize;


    public static CrimeFragment newInstance(UUID crimeID){
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeID);
        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
        mPhotoFile = CrimeLab.get(getActivity()).getPhotoFile(mCrime);
        setHasOptionsMenu(true);

    }
    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putSerializable(ARG_CRIME_ID, mCrime.getId());

        super.onSaveInstanceState(outState);
    }

    private void deleteCrime(){
        CrimeLab.get(getActivity()).deleteCrime(mCrime);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_crime_menu:
                deleteCrime();
                Log.d("CrimeFragment", "ActivityDeleted");
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater ) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime, menu);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);
        mTitleField = (EditText) v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int count, int after) {
                mCrime.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }


        });
        final Intent pickContact = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
        mSuspectButton = (Button) v.findViewById(R.id.crime_suspect);
        mSuspectButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }

        });
        if (mCrime.getSuspect() != null){
            mSuspectButton.setText(mCrime.getSuspect());
        }
        mCallSuspectButton = (Button) v.findViewById(R.id.crime_call_suspect);
        mCallSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkReadContactsPermission();
            }
        });

        final PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickContact,PackageManager.MATCH_DEFAULT_ONLY)==null){
            mSuspectButton.setEnabled(false);
        }
        mDateButton = (Button) v.findViewById(R.id.crime_date);
        updateDate(mCrime.getDate());
        mDateButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                if (ScreenTypeHelper.getInstance(getActivity()).isPhone()) { // Using a phone
                    Intent intent = new Intent(getContext(), DatePickerActivity.class);
                    intent.putExtra(EXTRA_DATE, mCrime.getDate());
                    startActivityForResult(intent, ACTIVITY_REQUEST_DATE);
                }
            }
        });

        mTimeButton = (Button) v.findViewById(R.id.crime_time);
        updateTime(mCrime.getDate());
        mTimeButton.setOnClickListener(new View.OnClickListener(){
           @Override
            public void onClick(View v){
               FragmentManager manager = getFragmentManager();
               TimePickerFragment dialog = TimePickerFragment.newInstance(mCrime.getDate());
               dialog.setTargetFragment(CrimeFragment.this, REQUEST_TIME);
               dialog.show(manager, DIALOG_TIME);
           }
        });


        mSolvedCheckBox = (CheckBox)v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
            }
        });
        mRequiresPoliceCBox = (CheckBox)v.findViewById(R.id.requires_police);
        mRequiresPoliceCBox.setChecked(mCrime.getMrequiresPolice());
        mRequiresPoliceCBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                mCrime.setMrequiresPolice(isChecked);
            }
        });
        mReportButton = (Button)v.findViewById(R.id.crime_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
                i = Intent.createChooser(i, getString(R.string.send_report));
                startActivity(i);
            }
        });
        mPhotoButton = (ImageButton) v.findViewById(R.id.crime_camera);
        mPhotoView = (ImageView) v.findViewById(R.id.crime_photo);
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getFragmentManager();
                ImageDisplayFragment fragment = ImageDisplayFragment.newInstance(mPhotoFile);
                fragment.show(fragmentManager, DIALOG_IMAGE);
            }
        });

        mPhotoTreeObserver = mPhotoView.getViewTreeObserver();
        mPhotoTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mPhotoView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                mPhotoViewSize = new Point();
                mPhotoViewSize.set(mPhotoView.getWidth(), mPhotoView.getHeight());

                updatePhotoView();
            }
        });


        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean canTakePhoto = mPhotoFile!=null &&
                captureImage.resolveActivity(packageManager)!=null;
        mPhotoButton.setEnabled(canTakePhoto);
        mPhotoButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Uri uri = FileProvider.getUriForFile(getActivity(),
                        "com.bignerdranch.android.criminalintent.fileprovider",mPhotoFile);
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                List<ResolveInfo> cameraActivities = getActivity()
                    .getPackageManager().queryIntentActivities(captureImage, PackageManager.MATCH_DEFAULT_ONLY);
                for(ResolveInfo activity : cameraActivities){
                    getActivity().grantUriPermission(activity.activityInfo.packageName,
                            uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
                startActivityForResult(captureImage, REQUEST_PHOTO);

            }
        });
        updatePhotoView();
        return v;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode != Activity.RESULT_OK){
            return;
        }
        if (requestCode == ACTIVITY_REQUEST_DATE){
            Log.d(TAG, "if ACTIVITY_REQUEST_DATE returned true");
            Date date = (Date) data.getSerializableExtra(EXTRA_DATE);
            mCrime.setDate(date);
            updateDate(mCrime.getDate());
            updateTime(mCrime.getDate());
        }
        if (requestCode == REQUEST_CONTACT && data != null){
            Uri contactUri =  data.getData();
            String[] queryFields = new String[]{
                    ContactsContract.Contacts.DISPLAY_NAME
            };
            Cursor c = getActivity().getContentResolver().query(contactUri, queryFields, null, null, null);
            try {
                if (c.getCount() == 0){
                    return;
                }
                c.moveToFirst();
                String suspect = c.getString(0);
                mCrime.setSuspect(suspect);
                mSuspectButton.setText(suspect);
            }finally{
                c.close();
            }
        }
        if(requestCode == REQUEST_PHOTO){
            Uri uri = FileProvider.getUriForFile(getActivity(), "com.bignerdranch.android.criminalintent.fileprovider", mPhotoFile);
            getActivity().revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            updatePhotoView();
        }
        if(requestCode == REQUEST_TIME){
            Log.d(TAG, "if REQUEST_TIME returned true");
            Date date = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            mCrime.setDate(date);
            updateDate(mCrime.getDate());
            updateTime(mCrime.getDate());
        }
    }

    private void updateDate(Date shortRangeDate) { //formats and updates date button text
        DateFormat dateMaker = new DateFormat();
        CharSequence dateText = dateMaker.format("EEEE, MM/dd/yy", shortRangeDate);
        mDateButton.setText(dateText);
    }
    private void updateTime(Date shortRangeDate){ // formats and updates time button text
        DateFormat timeMaker = new DateFormat();
        CharSequence timeText = timeMaker.format("k:mm", shortRangeDate);
        mTimeButton.setText(timeText);
    }
    public void checkReadContactsPermission() {// Here, thisActivity is the current activity
        Log.i(CRIME_FRAGMENT, "checkReadContactsPermission: start");
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.READ_CONTACTS)) {

            } else {

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            }
        } else {
            Log.i(CRIME_FRAGMENT, "checkReadContactsPermission: called");
            callSuspect();
        }

    }

    // Get Permissions

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                Toast.makeText(getActivity(), "Dial with phone successful", Toast.LENGTH_SHORT).show();
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    callSuspect();

                } else {
                    // No Permission
                }
                break;
            }

        }
    }

    private void callSuspect() {
        Cursor c = getActivity().getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?",
                new String[]{String.valueOf(mCrime.getSuspectId())},
                null
        );
        // Dial suspect if one exists

        try {
            if (c == null || c.getCount() == 0) {
                return;
            }
            c.moveToFirst();

            String phoneNumber = c.getString(0);

            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
            startActivity(intent);

        } finally {
            c.close();
        }
    }

    @Override
    public void onPause(){
        super.onPause();

        CrimeLab.get(getActivity())
                .updateCrime(mCrime);
    }
    private String getCrimeReport(){
        String solvedString = null;
        if (mCrime.isSolved()){
            solvedString = getString(R.string.crime_report_solved);
        }
        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat, mCrime.getDate()).toString();
        String suspect = mCrime.getSuspect();
        if(suspect==null){
            suspect=getString(R.string.crime_report_no_suspect);
        }
        else{
            suspect=getString(R.string.crime_report_suspect, suspect);
        }
        String report = getString(R.string.crime_report , mCrime.getTitle(),dateString,solvedString,suspect);
        return report;
    }

    private void updatePhotoView(){
        if (mPhotoFile == null || !mPhotoFile.exists()){
            mPhotoView.setImageDrawable(null);
            mPhotoView.setClickable(false);
        }
        else{
            Bitmap bitmap = PictureUtils.getScaledBitmap(
                    mPhotoFile.getPath(), getActivity()
            );
            mPhotoView.setImageBitmap(bitmap);
        }
    }


}
