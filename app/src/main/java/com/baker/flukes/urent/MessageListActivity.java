package com.baker.flukes.urent;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Log;

/**
 * Created by rflukes on 11/5/17.
 */

public class MessageListActivity extends SingleFragmentActivity{
    private static final String TAG = "MessageListActivity";
    public static final String EXTRA_USER_ID = "com.baker.flukes.urent.user_id";

    public static Intent newIntentForUser(Context packageContext, String userId){
        Intent intent = new Intent(packageContext, MessageListActivity.class);
        intent.putExtra(EXTRA_USER_ID, userId);
        return intent;
    }


    @Override
    protected Fragment createFragment(){
        String userId = (String) getIntent().getSerializableExtra(EXTRA_USER_ID);
        return MessageListFragment.newInstance(userId);
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
