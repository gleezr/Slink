package com.gleezr.slink;

import android.content.Context;
//import android.content.SharedPreferences;
import android.content.SharedPreferences;
import android.support.test.runner.AndroidJUnit4;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

//import static com.gleezr.slink.ExampleInstrumentedTest.cntxt;
//import static com.gleezr.slink.ExampleInstrumentedTest.edt;
//import static com.gleezr.slink.ExampleInstrumentedTest.slnk;
//import static com.gleezr.slink.ExampleInstrumentedTest.edt;
//import static com.gleezr.slink.ExampleInstrumentedTest.slnk;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import android.support.test.InstrumentationRegistry;

import java.io.File;

/**
 * Created by erez on 26/10/2016.
 */

@RunWith(AndroidJUnit4.class)
public class Test2 {

    private static Context cntxt;
    private static SharedPreferences slnk;
    private static SharedPreferences.Editor edt;

    @BeforeClass
    public static void useAppContext() throws Exception {
        cntxt = InstrumentationRegistry.getTargetContext();

        slnk = new Slink(new File(cntxt.getFilesDir() + "Test1"),cntxt.MODE_PRIVATE, cntxt);
//
        edt = slnk.edit();
    }

    @Test
    public void saveIntegerValueUsingCommit()
    {


        assertEquals(1,1);
    }
    @Test
    public void saveIntegerValueUsingCommit1()
    {


        assertTrue(1 == 1);
    }
}