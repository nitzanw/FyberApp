package com.nitzandev.fyberapp;

import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.test.suitebuilder.annotation.SmallTest;

/**
 * Created by nitzanwerber on 3/12/15.
 */
public class MainActivityUnitTest extends ActivityUnitTestCase<MainActivity> {

    private MainActivity activity;


    public MainActivityUnitTest(Class<MainActivity> activityClass) {
        super(activityClass);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        Intent intent = new Intent(getInstrumentation().getTargetContext(),
                MainActivity.class);
        startActivity(intent, null, null);
        activity = getActivity();
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    // Sanity check for the layout
    @SmallTest
    public void testLayoutExists() {
        // Verifies the button and text field exist
        assertNotNull("the list is not valid",activity.findViewById(R.id.offerList));
        assertNotNull("the empty view is not valid",activity.findViewById(R.id.empty));
    }
}
