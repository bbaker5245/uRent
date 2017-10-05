package com.baker.flukes.urent;

import android.support.v4.app.Fragment;
import android.util.Log;

/**
 * Created by Brian on 10/4/2017.
 */

public class PropertyListActivity extends SingleFragmentActivity {

    private static final String TAG = "PropertyListActivity";

    @Override
    protected Fragment createFragment() {
        return new PropertyListFragment();
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
