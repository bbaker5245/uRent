package com.baker.flukes.urent;

import android.support.v4.app.Fragment;

/**
 * Created by Brian on 10/4/2017.
 */

public class PropertyListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new PropertyListFragment();
    }
}
