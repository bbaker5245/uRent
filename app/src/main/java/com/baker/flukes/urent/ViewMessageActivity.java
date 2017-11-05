package com.baker.flukes.urent;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

/**
 * Created by rflukes on 11/5/17.
 */

public class ViewMessageActivity extends SingleFragmentActivity{
    private static final String TAG = "ViewMessageActivity";
    public static final String EXTRA_MESSAGE_ID = "com.baker.flukes.urent.message_id";

    public static Intent newIntent(Context packageContext, String messageId){
        Intent intent = new Intent(packageContext, ViewMessageActivity.class);
        intent.putExtra(EXTRA_MESSAGE_ID, messageId);
        return intent;
    }

    @Override
    protected Fragment createFragment(){
        String messageId = (String) getIntent().getSerializableExtra(EXTRA_MESSAGE_ID);
        return ViewMessageFragment.newInstance(messageId);
    }
}
