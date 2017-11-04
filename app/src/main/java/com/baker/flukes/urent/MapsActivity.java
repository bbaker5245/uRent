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
        return MapsFragment.newInstance(universityId);}

    public static Intent newIntentForMaps(Context packageContext, String universityId){
        Intent intent = new Intent(packageContext, MapsActivity.class);
        intent.putExtra(EXTRA_UNIVERSITY_ID, universityId);
        return intent;
    }

}
