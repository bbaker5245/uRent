package com.baker.flukes.urent;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

/**
 * Created by rflukes on 11/3/17.
 */

public class ViewPropertyActivity extends SingleFragmentActivity {

    private static final String TAG = "PropertyActivity";
    public static final String EXTRA_PROPERTY_ID = "com.baker.flukes.urent.property_id";

    public static Intent newIntent(Context packageContext, String propertyId){
        Intent intent = new Intent(packageContext, ViewPropertyActivity.class);
        intent.putExtra(EXTRA_PROPERTY_ID, propertyId);
        return intent;
    }

    @Override
    protected Fragment createFragment(){
        String propertyId = (String) getIntent().getSerializableExtra(EXTRA_PROPERTY_ID);
        return ViewPropertyFragment.newInstance(propertyId);
    }
}
