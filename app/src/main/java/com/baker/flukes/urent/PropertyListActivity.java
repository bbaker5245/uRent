package com.baker.flukes.urent;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Log;

/**
 * Created by Brian on 10/4/2017.
 */

public class PropertyListActivity extends SingleFragmentActivity {

    private static final String TAG = "PropertyListActivity";
    public static final String EXTRA_UNIVERSITY_ID = "com.baker.flukes.urent.university_id";

    public static Intent newIntent(Context packageContext, String universityId){
        Intent intent = new Intent(packageContext, PropertyListActivity.class);
        intent.putExtra(EXTRA_UNIVERSITY_ID, universityId);
        return intent;
    }

    @Override
    protected Fragment createFragment(){
        String propertyId = (String) getIntent().getSerializableExtra(EXTRA_UNIVERSITY_ID);
        return PropertyListFragment.newInstance(propertyId);
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
