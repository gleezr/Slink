package com.gleezr.slink;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    static Context cntxt;
    static  SharedPreferences slnk;
    static SharedPreferences.Editor edt;

    @BeforeClass
    public static void useAppContext() throws Exception {
        cntxt = InstrumentationRegistry.getTargetContext();

        slnk = new Slink2(new File(cntxt.getFilesDir() + "Test1"),cntxt.MODE_PRIVATE, cntxt);

        edt = slnk.edit();
    }

    @Test
    public void saveIntegerValueUsingCommit()
    {
        edt.putInt("MyFirstInteger", 1);

        edt.commit();

        assertEquals(1,slnk.getInt("MyFirstInteger", 9));
    }

    @Test
    public void saveIntegerValueUsingApply()
    {
        edt.putInt("MyFirstInteger", 1);

        edt.apply();

        assertEquals(1,slnk.getInt("MyFirstInteger", 9));
    }
}
