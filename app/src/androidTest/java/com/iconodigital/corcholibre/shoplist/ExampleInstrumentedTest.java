package com.iconodigital.corcholibre.shoplist;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * Instrumentation test_act, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test_act.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.com.iconodigital.com.com.iconodigital.com.iconodigital.com.iconodigital.com.iconodigital.com.iconodigital.corcholibre.com.com.iconodigital.com.iconodigital.com.iconodigital.com.iconodigital.com.iconodigital.corcholibre", appContext.getPackageName());
    }
}
