package com.baker.flukes.urent;


import android.support.v4.app.Fragment;





public class MapsActivity extends SingleFragmentActivity{

    private static final String TAG = "MapsActivity";

    protected Fragment createFragment(){ return MapsFragment.newInstance();}

}
