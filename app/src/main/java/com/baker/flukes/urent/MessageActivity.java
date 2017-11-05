package com.baker.flukes.urent;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

/**
 * Created by rflukes on 11/4/17.
 */

public class MessageActivity extends SingleFragmentActivity{

    private static final String TAG = "MessageActivity";
    public static final String EXTRA_PROPERTY_ADDRESS = "com.baker.flukes.urent.property_address";
    public static final String EXTRA_SENDER_ID = "com.baker.flukes.urent.sender_id";
    public static final String EXTRA_RECIPIENT_ID = "com.baker.flukes.urent.recipient_id";

    public static Intent newIntent(Context packageContext, String address, String senderId, String recipientId){
        Intent intent = new Intent(packageContext, MessageActivity.class);
        intent.putExtra(EXTRA_PROPERTY_ADDRESS, address);
        intent.putExtra(EXTRA_SENDER_ID, senderId);
        intent.putExtra(EXTRA_RECIPIENT_ID, recipientId);
        return intent;
    }

    @Override
    protected Fragment createFragment(){
        String propertyId = (String) getIntent().getSerializableExtra(EXTRA_PROPERTY_ADDRESS);
        String senderId = (String) getIntent().getSerializableExtra(EXTRA_SENDER_ID);
        String recipientId = (String) getIntent().getSerializableExtra(EXTRA_RECIPIENT_ID);
        return MessageFragment.newInstance(propertyId, senderId, recipientId);
    }
}
