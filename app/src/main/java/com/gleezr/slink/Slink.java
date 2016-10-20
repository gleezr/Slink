package com.gleezr.slink;

import static android.R.attr.defaultValue;
import static java.lang.System.out;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.content.Context;

import com.facebook.android.crypto.keychain.AndroidConceal;
import com.facebook.android.crypto.keychain.SharedPrefsBackedKeyChain;
import com.facebook.crypto.Crypto;
import com.facebook.crypto.CryptoConfig;
import com.facebook.crypto.Entity;
import com.facebook.crypto.exception.CryptoInitializationException;
import com.facebook.crypto.exception.KeyChainException;
import com.facebook.crypto.keychain.KeyChain;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

public class Slink {

    // Variable Definition
    private String preferenceFile = "slinkPrefs8";
    private String cipherEntity = "Properties";
    private Gson gson;
    private HashMap<String, String> preferences;
    private Crypto crypto;
    private KeyChain keyChain;
    private Context context;

    public Slink(Context context, String uniquePrefFileName) {
        preferences = new HashMap<>();
        gson = new Gson();

        preferenceFile = uniquePrefFileName;
        cipherEntity = cipherEntity + preferenceFile;

        this.context = context;

        // Creates a new Crypto object with default implementations of a key chain
        keyChain = new SharedPrefsBackedKeyChain(this.context, CryptoConfig.KEY_256);
        crypto = AndroidConceal.get().createDefaultCrypto(keyChain);
    }


    /**
     * Saves the boolean to shared preferences.
     * Stores in a temporary structure before saving to memory
     *
     * @param key Key of the obj
     * @param obj The Object to save in the SharedPreferences
     */
    public void setBool(String key, Boolean obj) {
        String jsonObjToSave = gson.toJson(obj);
        preferences.put(key, jsonObjToSave);
    }

    /**
     * Saves the int to shared preferences.
     * Stores in a temporary structure before saving to memory
     *
     * @param key Key of the obj
     * @param obj The Object to save in the SharedPreferences
     */
    public void setint(String key, int obj) {
        String jsonObjToSave = gson.toJson(obj);
        preferences.put(key, jsonObjToSave);
    }

    /**
     * Saves the float to shared preferences.
     * Stores in a temporary structure before saving to memory
     *
     * @param key Key of the obj
     * @param obj The Object to save in the SharedPreferences
     */
    public void setFloat(String key, float obj) {
        String jsonObjToSave = gson.toJson(obj);
        preferences.put(key, jsonObjToSave);
    }

    /**
     * Saves the long to shared preferences.
     * Stores in a temporary structure before saving to memory
     *
     * @param key Key of the obj
     * @param obj The Object to save in the SharedPreferences
     */
    public void setLong(String key, long obj) {
        String jsonObjToSave = gson.toJson(obj);
        preferences.put(key, jsonObjToSave);
    }

    /**
     * Saves the object to shared preferences.
     * Stores in a temporary structure before saving to memory
     *
     * @param key Key of the obj
     * @param obj The Object to save in the SharedPreferences
     */
    public void setObject(String key, Object obj) {
        String jsonObjToSave = gson.toJson(obj);
        preferences.put(key, jsonObjToSave);
    }

    /**
     * Saves the String to shared preferences.
     * Stores in a temporary structure before saving to memory
     *
     * @param key Key of the obj
     * @param obj The Object to save in the SharedPreferences
     */
    public void setString(String key, String obj) {
        String jsonObjToSave = gson.toJson(obj);
        preferences.put(key, jsonObjToSave);
    }

    /**
     * Saves the String to shared preferences.
     * Stores in a temporary structure before saving to memory
     *
     * @param key Key of the obj
     * @param obj The Object to save in the SharedPreferences
     */
    public void setStringSet(String key, Set<String> obj) {
        Type stringSet = new TypeToken<Set<String>>() {
        }.getType();

        String jsonObjToSave = gson.toJson(obj, stringSet);

        preferences.put(key, jsonObjToSave);
    }

    /**
     * Get a boolean value.
     *
     * @param key          Preference key
     * @param defaultValue Default value to return in case of not value found
     * @return Returns the Value attached to the key
     */
    public Boolean getBoolean(String key, Boolean defaultValue) {

        try {
            if (preferences.containsKey(key)) {
                String tempBool = preferences.get(key);

                if (tempBool.contains("false") || tempBool.contains("true")) {
                    boolean b = gson.fromJson(tempBool, Boolean.TYPE);

                    return (b);
                }
            }
            return defaultValue;

        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Get a float value from preferences.
     *
     * @param key          Preference key.
     * @param defaultValue Default value to return in case of not value found.
     * @return Returns the value attached to the key.
     */
    public Float getFloat(String key, Float defaultValue) {
        Float f;

        try {
            f = gson.fromJson(preferences.get(key), Float.TYPE);
        } catch (Exception e) {
            return defaultValue;
        }

        return f;
    }

    /**
     * Get a integer value from preferences.
     *
     * @param key          Preference key.
     * @param defaultValue Default value to return in case of not value found.
     * @return Returns the value attached to the key.
     */
    public int getInt(String key, int defaultValue) {
        int i;

        try {
            i = gson.fromJson(preferences.get(key), Integer.TYPE);
        } catch (Exception e) {
            return (defaultValue);
        }

        return (i);
    }

    /**
     * Get a Long value from preferences.
     *
     * @param key          Preference key.
     * @param defaultValue Default value to return in case of not value found.
     * @return Returns the value attached to the key.
     */
    public long getLong(String key, long defaultValue) {
        long l;

        try {
            l = gson.fromJson(preferences.get(key), Long.TYPE);
        } catch (Exception e) {
            return (defaultValue);
        }

        return (l);
    }

    /**
     * Get a String value from preferences.
     *
     * @param key          Preference key.
     * @param defaultValue Default value to return in case of not value found.
     * @return Returns the value attached to the key.
     */
    public String getString(String key, String defaultValue) {
        try {
            if (preferences.containsKey(key)) {
                return (preferences.get(key));
            }
        } catch (Exception e) {
            return (defaultValue);
        }

        return (defaultValue);
    }

    /**
     * Get all String set values from preferences.
     *
     * @param key          Preference key.
     * @param defaultValue Default value to return in case of not value found.
     * @return Returns the value attached to the key.
     */
    public Set<String> getStringSet(String key, Set<String> defaultValue) {
        Object set;

        try {
            if (preferences.containsKey(key)) {
                Type stringSet = new TypeToken<Set<String>>() {
                }.getType();

                set = gson.fromJson(preferences.get(key), stringSet);

                if (set instanceof Collection<?>) {
                    return ((Set<String>) set);
                }
            }
        } catch (Exception e) {
            return (defaultValue);
        }

        return (defaultValue);
    }

    /**
     * Get the shared preferences file from memory.
     */
    public void getSharedPreferencesFromMem() {
        File file = new File(context.getFilesDir(), preferenceFile);
        BufferedInputStream bf = null;

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (file.length() == 0) {
            return;
        }

        FileInputStream fileStream = null;

        try {

            // Get the file to which ciphertext has been written.
            fileStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            // Not Empty
        }

        if (!crypto.isAvailable()) {
            return;
        }

        // Creates an input stream which decrypts the data as
        // it is read from it.
        InputStream inputStream = null;
        try {
            Entity e = Entity.create(cipherEntity);
            inputStream = crypto.getCipherInputStream(fileStream, e);
            bf = new BufferedInputStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CryptoInitializationException e) {
            e.printStackTrace();
        } catch (KeyChainException e) {
            e.printStackTrace();
        }

        // Read into a byte array.
        int read;
        byte[] buffer = new byte[1024];

        // You must read the entire stream to completion.
        // The verification is done at the end of the stream.
        // Thus not reading till the end of the stream will cause
        // a security bug. For safety, you should not
        // use any of the data until it's been fully read or throw
        // away the data if an exception occurs.
        try {
            while ((read = bf.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String preferencesString = new String(buffer).trim();

        Type stringStringMap = new TypeToken<HashMap<String, String>>() {
        }.getType();

        preferences = gson.fromJson(preferencesString, stringStringMap);

//        preferences = (HashMap<String, String>) m;
    }


    /**
     * Saves the object to Share preferences.
     */
    public void saveSharedPropertiesToMem() {
        File file = new File(context.getFilesDir(), preferenceFile);

        Gson gson = new Gson();
        Type stringStringMap = new TypeToken<HashMap<String, String>>() {
        }.getType();
        String map = gson.toJson(preferences, stringStringMap);

        // Check for whether the crypto functionality is available
        // This might fail if Android does not load libaries correctly.
        if (!crypto.isAvailable()) {
            return;
        }

        OutputStream fileStream = null;
        try {
            fileStream = new BufferedOutputStream(
                    new FileOutputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Creates an output stream which encrypts the data as
        // it is written to it and writes it out to the file.
        OutputStream outputStream = null;

        try {
            outputStream = crypto.getCipherOutputStream(
                    fileStream,
                    Entity.create(cipherEntity));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CryptoInitializationException e) {
            e.printStackTrace();
        } catch (KeyChainException e) {
            e.printStackTrace();
        }
//
//        byte[] b = new byte[1024 - map.getBytes().length];
//        byte[] c = new byte[1024];
//        System.arraycopy(map.getBytes(), 0, c, 0, map.getBytes().length);
//        System.arraycopy(b, 0, c, map.getBytes().length, b.length);

        try {
            // Write plaintext to it.
            outputStream.write(map.getBytes());
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
