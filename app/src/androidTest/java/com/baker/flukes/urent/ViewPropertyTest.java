package com.baker.flukes.urent;

import android.test.ActivityInstrumentationTestCase2;
import junit.framework.Assert;
import org.junit.Test;

public class ViewPropertyTest extends ActivityInstrumentationTestCase2<ViewPropertyActivity> {
    private ViewPropertyActivity viewPropertyActivity;

    public ViewPropertyTest() {
        super(ViewPropertyActivity.class);
    }

    protected void setUp() throws Exception {
        super.setUp();
        viewPropertyActivity = getActivity();
    }

    @Test
    public void testPreconditions() {
        Assert.assertNotNull(viewPropertyActivity);
    }

    protected void tearDown() throws Exception {
        viewPropertyActivity.finish();
    }
}