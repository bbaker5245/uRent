package com.baker.flukes.urent;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ToggleButton;

import com.google.android.gms.maps.SupportMapFragment;

/**
 * Created by rflukes on 10/27/17.
 */

public class AddPropertyActivity extends SingleFragmentActivity {

    private static final String TAG = "AddPropertyActivity";

    protected Fragment createFragment(){ return new AddPropertyFragment(); }
}
