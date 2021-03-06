package com.baker.flukes.urent;


import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Log;


public class MapsActivity extends SingleFragmentActivity{

    private static final String TAG = "MapsActivity";
    public static final String EXTRA_UNIVERSITY_ID = "com.baker.flukes.urent.university_id";

    protected Fragment createFragment(){
        String universityId = (String) getIntent().getSerializableExtra(EXTRA_UNIVERSITY_ID);
        return MapsFragment.newInstance(universityId);
    }

    public static Intent newIntentForMaps(Context packageContext, String universityId){
        Intent intent = new Intent(packageContext, MapsActivity.class);
        intent.putExtra(EXTRA_UNIVERSITY_ID, universityId);
        return intent;
    }
    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }

}
