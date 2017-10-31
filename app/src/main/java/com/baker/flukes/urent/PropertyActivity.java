package com.baker.flukes.urent;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

/**
 * Created by rflukes on 10/27/17.
 */

public class PropertyActivity extends SingleFragmentActivity {

    private static final String TAG = "PropertyActivity";
    public static final String EXTRA_PROPERTY_ID = "com.baker.flukes.urent.property_id";

    public static Intent newIntent(Context packageContext, String propertyId){
        Intent intent = new Intent(packageContext, PropertyActivity.class);
        intent.putExtra(EXTRA_PROPERTY_ID, propertyId);
        return intent;
    }

    @Override
    protected Fragment createFragment(){
        String propertyId = (String) getIntent().getSerializableExtra(EXTRA_PROPERTY_ID);
        return PropertyFragment.newInstance(propertyId);
    }
}
