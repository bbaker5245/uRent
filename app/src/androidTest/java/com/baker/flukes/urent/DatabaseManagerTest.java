package com.baker.flukes.urent;

import junit.framework.Assert;

import org.junit.Test;

import static android.support.test.InstrumentationRegistry.getContext;

/**
 * Created by Brian on 11/19/2017.
 */

public class DatabaseManagerTest {
    @Test
    public void testCloseTogetherTrue() {
        Property p = new Property();
        p.setAddress("50 E 18th Ave, Columbus, OH");
        University u = new University();
        u.setName("The Ohio State University");
        Assert.assertTrue(DatabaseManager.getInstance(getContext()).closeTogether(p, u));
    }

    @Test
    public void testCloseTogetherFalse() {
        Property p = new Property();
        p.setAddress("50 E 18th Ave, Columbus, OH");
        University u = new University();
        u.setName("University of Michigan");
        Assert.assertFalse(DatabaseManager.getInstance(getContext()).closeTogether(p, u));
    }
}
