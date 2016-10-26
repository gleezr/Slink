package com.gleezr.slink;


import android.content.Context;
import android.content.SharedPreferences;
//import android.support.test.InstrumentationRegistry;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;


/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    static Context cntxt;
    static SharedPreferences slnk;
    static SharedPreferences.Editor edt;

    @BeforeClass
    public static void useAppContext() throws Exception {
        cntxt = InstrumentationRegistry.getTargetContext();

        slnk = new Slink(new File(cntxt.getFilesDir() + "Test4"),cntxt.MODE_PRIVATE, cntxt);

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

    @Test
    public void saveFloatValue()
    {
        edt.putFloat("MyFirstFloat", 2.2f);

        edt.apply();

        assertEquals(2.2f, slnk.getFloat("MyFirstFloat", 9.0f), 0f);
    }

    @Test
    public void saveStringValue()
    {
        edt.putString("MyFirstString", "FirstString");

        edt.apply();

        assertEquals("FirstString", slnk.getString("MyFirstString", ""));
    }

    @Test
    public void saveLongValue()
    {
        edt.putLong("MyFirstLong", 11l);

        edt.apply();

        assertEquals(11l, slnk.getLong("MyFirstLong", 80l));
    }

    @Test
    public void saveBooleanValue()
    {
        edt.putBoolean("MyFirstBool", true);

        edt.apply();

        assertEquals(true, slnk.getBoolean("MyFirstBool", false));
    }

    @Test
    public void saveStringSetValue()
    {
        Set<String> set = new HashSet<String>();

        set.add("1");
        set.add("2");

        edt.putStringSet("MyFirstStringSet",  set);

        edt.apply();

        Set<String> srs = slnk.getStringSet("MyFirstStringSet", null);

        assertTrue((srs.contains("1") && srs.contains("2")));
    }

    @Test
    public void CheckIfClearCleareWithoutCommit()
    {
        edt.putInt("MyFirstInteger", 1);

        edt.clear();

        assertEquals(1,slnk.getInt("MyFirstInteger", 9));
    }

    @Test
    public void CheckIfClearClearedBeforeIntegerPut()
    {
        edt.putInt("MyFirstInteger", 1);

        edt.clear();

        edt.commit();

        assertEquals(1,slnk.getInt("MyFirstInteger", 9));
    }

    @Test
    public void CheckIfClearCleared()
    {
        edt.putInt("MyFirstInteger", 1);

        edt.commit();

        edt.clear();

        edt.commit();

        assertTrue(!slnk.contains("MyFirstInteger"));
    }
}
