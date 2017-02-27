package com.gleezr.slink;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static java.lang.System.out;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class SlinkTests {

    static Context context;
    static SharedPreferences slink;
    static SharedPreferences.Editor editor;
    private static String FILE_NAME = "";

    @BeforeClass
    public static void useAppContext() throws Exception {
        context = InstrumentationRegistry.getTargetContext();

        FILE_NAME = context.getFilesDir() + "Test4";

        slink = SlinkManager.getSlink(context,  FILE_NAME);

        editor = slink.edit();
    }

    @Test
    public void saveIntegerValueUsingCommit() {
        editor.putInt("MyFirstInteger", 1);
        editor.commit();

        assertEquals(1, slink.getInt("MyFirstInteger", 9));
    }

    @Test
    public void saveIntegerValueUsingApply() {
        editor.putInt("MyFirstInteger", 1);
        editor.apply();

        assertEquals(1, slink.getInt("MyFirstInteger", 9));
    }

    @Test
    public void saveFloatValue() {
        editor.putFloat("MyFirstFloat", 2.2f);
        editor.apply();

        assertEquals(2.2f, slink.getFloat("MyFirstFloat", 9.0f), 0f);
    }

    @Test
    public void saveStringValue() {
        editor.putString("MyFirstString", "FirstString");
        editor.apply();

        assertEquals("FirstString", slink.getString("MyFirstString", ""));
    }

    @Test
    public void saveLongValue() {
        editor.putLong("MyFirstLong", 11l);
        editor.apply();

        assertEquals(11l, slink.getLong("MyFirstLong", 80l));
    }

    @Test
    public void saveBooleanValue() {
        editor.putBoolean("MyFirstBool", true);
        editor.apply();

        assertEquals(true, slink.getBoolean("MyFirstBool", false));
    }

    @Test
    public void saveStringSetValue() {
        Set<String> set = new HashSet<>();

        set.add("1");
        set.add("2");

        editor.putStringSet("MyFirstStringSet",  set);
        editor.apply();

        Set<String> srs = slink.getStringSet("MyFirstStringSet", null);

        assertTrue((srs.contains("1") && srs.contains("2")));
    }

    @Test
    public void checkIfClearCleareWithoutCommit() {
        editor.putInt("MyFirstInteger", 1);
        editor.clear();

        assertEquals(1, slink.getInt("MyFirstInteger", 9));
    }

    @Test
    public void checkIfClearClearedBeforeIntegerPut() {
        editor.putInt("MyFirstInteger", 1);
        editor.clear();
        editor.commit();

        assertEquals(1, slink.getInt("MyFirstInteger", 9));
    }

    @Test
    public void checkIfClearCleared() {
        editor.putInt("MyFirstInteger", 1);
        editor.commit();
        editor.clear();
        editor.commit();

        assertTrue(!slink.contains("MyFirstInteger"));
    }

    @Test
    public void checkIfDataIsEncrypted() throws FileNotFoundException {
        editor.clear();

        editor.commit();

        editor.putInt("MyInt", 1);

        File f = new File(FILE_NAME);

        BufferedInputStream is = new BufferedInputStream(new FileInputStream(f), 16 * 1024);

        int read;
        byte[] buffer = new byte[1024];

        // You must read the entire stream to completion.
        // The verification is done at the end of the stream.
        // Thus not reading till the end of the stream will cause
        // a security bug. For safety, you should not
        // use any of the data until it's been fully read or throw
        // away the data if an exception occurs.
        try {
            while ((read = is.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String preferencesString = new String(buffer).trim();

        Type stringStringMap = new TypeToken<HashMap<String, Object>>() {}.getType();

        boolean b = false;

        try {
            Map<String, Object> mMap = new Gson().fromJson(preferencesString, stringStringMap);
        } catch(JsonSyntaxException e) {
           b = true;
        }

        assertEquals(true, b);
    }
}
