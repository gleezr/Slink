//package com.gleezr.slink;
//
//import static android.R.attr.defaultValue;
//import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
//import static java.lang.System.out;
//
//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;
//
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.preference.Preference;
//
//import com.facebook.android.crypto.keychain.AndroidConceal;
//import com.facebook.android.crypto.keychain.SharedPrefsBackedKeyChain;
//import com.facebook.crypto.Crypto;
//import com.facebook.crypto.CryptoConfig;
//import com.facebook.crypto.Entity;
//import com.facebook.crypto.exception.CryptoInitializationException;
//import com.facebook.crypto.exception.KeyChainException;
//import com.facebook.crypto.keychain.KeyChain;
//
//import java.io.BufferedInputStream;
//import java.io.BufferedOutputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.lang.reflect.Type;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//import java.util.WeakHashMap;
//import java.util.concurrent.CountDownLatch;
//import java.util.prefs.Preferences;
//
//public abstract class Slink implements SharedPreferences {
//
////    // Variable Definition
////    private String preferenceFile = "slinkPrefs8";
////    private String cipherEntity = "Properties";
////    private Gson gson;
////    private Map<String, Object> preferences;
////    private Crypto crypto;
////    private KeyChain keyChain;
////    private Context context;
////    private static final Object mContent = new Object();
////    private final WeakHashMap<OnSharedPreferenceChangeListener, Object> mListeners =
////            new WeakHashMap<OnSharedPreferenceChangeListener, Object>();
////
////    public Slink(Context context, String uniquePrefFileName) {
////
////    }
////
////
////    /**
////     * Get a boolean value.
////     *
////     * @param key      Preference key
////     * @param defValue Default value to return in case of not value found
////     * @return Returns the Value attached to the key
////     */
////    @Override
////    public boolean getBoolean(String key, boolean defValue) {
////        synchronized (this) {
////            Boolean b = (Boolean) preferences.get(key);
////
////            return ((b != null) ? b : defValue);
////        }
////    }
////
////    /**
////     * Get a float value from preferences.
////     *
////     * @param key      Preference key.
////     * @param defValue Default value to return in case of not value found.
////     * @return Returns the value attached to the key.
////     */
////    @Override
////    public float getFloat(String key, float defValue) {
////        synchronized (this) {
////            Float f = (Float) preferences.get(key);
////            return ((f != null) ? f : defaultValue);
////        }
////    }
////
////    /**
////     * Get a integer value from preferences.
////     *
////     * @param key          Preference key.
////     * @param defaultValue Default value to return in case of not value found.
////     * @return Returns the value attached to the key.
////     */
////    public int getInt(String key, int defaultValue) {
////        synchronized (this) {
////            Integer i = (Integer) preferences.get(key);
////            return ((i != null) ? i : defaultValue);
////        }
////    }
////
////    /**
////     * Get a String value from preferences.
////     *
////     * @param key          Preference key.
////     * @param defaultValue Default value to return in case of not value found.
////     * @return Returns the value attached to the key.
////     */
////    public String getString(String key, String defaultValue) {
////        synchronized (this) {
////            String s = (String) preferences.get(key);
////            return ((s != null) ? s : defaultValue);
////        }
////    }
////
////    /**
////     * Get all String set values from preferences.
////     *
////     * @param key          Preference key.
////     * @param defaultValue Default value to return in case of not value found.
////     * @return Returns the value attached to the key.
////     */
////    public Set<String> getStringSet(String key, Set<String> defaultValue) {
////        synchronized (this) {
////            Set<String> s = (Set<String>) preferences.get(key);
////            return ((s != null) ? s : defaultValue);
////        }
////    }
////
////    /**
////     * Get all preferences values
////     *
////     * @return Returns a map containing all the preferences
////     */
////    @Override
////    public Map<String, ?> getAll() {
////        return preferences;
////    }
////
////    /**
////     * Get a Long value from preferences.
////     *
////     * @param key          Preference key.
////     * @param defaultValue Default value to return in case of not value found.
////     * @return Returns the value attached to the key.
////     */
////    @Override
////    public long getLong(String key, long defaultValue) {
////        synchronized (this) {
////            Long l = (Long) preferences.get(key);
////            return ((l != null) ? l : defaultValue);
////        }
////    }
////
////    @Override
////    public boolean contains(String key) {
////        return (preferences.containsKey(key));
////    }
////
////    @Override
////    public Editor edit() {
////        return null;
////    }
////
////    @Override
////    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
////        synchronized (this) {
////            mListeners.put(listener, mContent);
////        }
////    }
////
////    @Override
////    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
////        synchronized (this) {
////            mListeners.remove(listener);
////        }
////    }
////
////    public final class SlinkEditor implements Editor {
////
////        private Map<String, Object> modifiedPreferences;
////        private boolean Clear = false;
////        private boolean ChangesMade = false;
////
////        /**
////         * Saves the boolean to shared preferences.
////         * Stores in a temporary structure before saving to memory
////         *
////         * @param key   Key of the value
////         * @param value The Object to save in the SharedPreferences
////         */
////        @Override
////        public Editor putBoolean(String key, boolean value) {
////            synchronized (this) {
////                modifiedPreferences.put(key, value);
////                return (this);
////            }
////        }
////
////
////        /**
////         * Saves the int to shared preferences.
////         * Stores in a temporary structure before saving to memory
////         *
////         * @param key   Key of the value
////         * @param value The Object to save in the SharedPreferences
////         */
////        @Override
////        public Editor putInt(String key, int value) {
////            synchronized (this) {
////                modifiedPreferences.put(key, value);
////                return (this);
////            }
////        }
////
////        /**
////         * Saves the float to shared preferences.
////         * Stores in a temporary structure before saving to memory
////         *
////         * @param key   Key of the value
////         * @param value The Object to save in the SharedPreferences
////         */
////        @Override
////        public Editor putFloat(String key, float value) {
////            synchronized (this) {
////                modifiedPreferences.put(key, value);
////                return (this);
////            }
////        }
////
////        /**
////         * Saves the long to shared preferences.
////         * Stores in a temporary structure before saving to memory
////         *
////         * @param key   Key of the value
////         * @param value The Object to save in the SharedPreferences
////         */
////        @Override
////        public Editor putLong(String key, long value) {
////            synchronized (this) {
////                modifiedPreferences.put(key, value);
////                return (this);
////            }
////        }
////
////        /**
////         * Saves the String to shared preferences.
////         * Stores in a temporary structure before saving to memory
////         *
////         * @param key   Key of the value
////         * @param value The Object to save in the SharedPreferences
////         */
////        @Override
////        public Editor putString(String key, String value) {
////            synchronized (this) {
////                modifiedPreferences.put(key, value);
////                return (this);
////            }
////        }
////
////        /**
////         * Saves the String to shared preferences.
////         * Stores in a temporary structure before saving to memory
////         *
////         * @param key   Key of the value
////         * @param value The Object to save in the SharedPreferences
////         */
////        @Override
////        public Editor putStringSet(String key, Set<String> value) {
////            synchronized (this) {
////                Type stringSet = new TypeToken<Set<String>>() {
////                }.getType();
////
////
////                modifiedPreferences.put(key, stringSet);
////
////                return (this);
////            }
////        }
////
////        @Override
////        public Editor remove(String key) {
////            synchronized (this) {
////                modifiedPreferences.put(key, this);
////
////                return this;
////            }
////        }
////
////        @Override
////        public Editor clear() {
////            synchronized (this) {
////                Clear = true;
////                return this;
////            }
////        }
////
////        @Override
////        public boolean commit() {
////
////            CommitToMemory();
////
////            saveSharedPropertiesToMem();
////
////
////            return false;
////        }
////
////        private void CommitToMemory()
////        {
////            MemoryCommitResult mcr = new MemoryCommitResult();
////            synchronized (Slink.this) {
////                // We optimistically don't make a deep copy until
////                // a memory commit comes in when we're already
////                // writing to disk.
////                if (mDiskWritesInFlight > 0) {
////                    // We can't modify our mMap as a currently
////                    // in-flight write owns it.  Clone it before
////                    // modifying it.
////                    // noinspection unchecked
////                    mMap = new HashMap<String, Object>(mMap);
////                }
////                mcr.mapToWriteToDisk = mMap;
////                mDiskWritesInFlight++;
////
////                boolean hasListeners = mListeners.size() > 0;
////                if (hasListeners) {
////                    mcr.keysModified = new ArrayList<String>();
////                    mcr.listeners =
////                            new HashSet<OnSharedPreferenceChangeListener>(mListeners.keySet());
////                }
////
////                synchronized (this) {
////                    if (mClear) {
////                        if (!mMap.isEmpty()) {
////                            mcr.changesMade = true;
////                            mMap.clear();
////                        }
////                        mClear = false;
////                    }
////
////                    for (Map.Entry<String, Object> e : mModified.entrySet()) {
////                        String k = e.getKey();
////                        Object v = e.getValue();
////                        // "this" is the magic value for a removal mutation. In addition,
////                        // setting a value to "null" for a given key is specified to be
////                        // equivalent to calling remove on that key.
////                        if (v == this || v == null) {
////                            if (!mMap.containsKey(k)) {
////                                continue;
////                            }
////                            mMap.remove(k);
////                        } else {
////                            if (mMap.containsKey(k)) {
////                                Object existingValue = mMap.get(k);
////                                if (existingValue != null && existingValue.equals(v)) {
////                                    continue;
////                                }
////                            }
////                            mMap.put(k, v);
////                        }
////
////                        mcr.changesMade = true;
////                        if (hasListeners) {
////                            mcr.keysModified.add(k);
////                        }
////                    }
////
////                    mModified.clear();
////                }
////            }
////            return mcr;
////        }
////
////        @Override
////        public void apply()
////        {
////            synchronized (Slink.this)
////            {
////
////            }
////
////            synchronized (this)
////            {
////                saveSharedPropertiesToMem();
////            }
////
////        }
////
////        /**
////         * Saves the valueect to Share preferences.
////         */
////        public void saveSharedPropertiesToMem() {
////            File file = new File(context.getFilesDir(), preferenceFile);
////
////            Gson gson = new Gson();
////            Type stringStringMap = new TypeToken<HashMap<String, String>>() {
////            }.getType();
////            String map = gson.toJson(preferences, stringStringMap);
////
////            // Check for whether the crypto functionality is available
////            // This might fail if Android does not load libaries correctly.
////            if (!crypto.isAvailable()) {
////                return;
////            }
////
////            OutputStream fileStream = null;
////            try {
////                fileStream = new BufferedOutputStream(
////                        new FileOutputStream(file));
////            } catch (FileNotFoundException e) {
////                e.printStackTrace();
////            }
////
////            // Creates an output stream which encrypts the data as
////            // it is written to it and writes it out to the file.
////            OutputStream outputStream = null;
////
////            try {
////                outputStream = crypto.getCipherOutputStream(
////                        fileStream,
////                        Entity.create(cipherEntity));
////            } catch (IOException e) {
////                e.printStackTrace();
////            } catch (CryptoInitializationException e) {
////                e.printStackTrace();
////            } catch (KeyChainException e) {
////                e.printStackTrace();
////            }
//////
//////        byte[] b = new byte[1024 - map.getBytes().length];
//////        byte[] c = new byte[1024];
//////        System.arraycopy(map.getBytes(), 0, c, 0, map.getBytes().length);
//////        System.arraycopy(b, 0, c, map.getBytes().length, b.length);
////
////            try {
////                // Write plaintext to it.
////                outputStream.write(map.getBytes());
////                outputStream.close();
////            } catch (IOException e) {
////                e.printStackTrace();
////            }
////        }
////    }
////
////
////    /**
////     * Get the shared preferences file from memory.
////     */
////    public void getSharedPreferencesFromMem() {
////        File file = new File(context.getFilesDir(), preferenceFile);
////        BufferedInputStream bf = null;
////
////        if (!file.exists()) {
////            try {
////                file.createNewFile();
////            } catch (IOException e) {
////                e.printStackTrace();
////            }
////        }
////
////        if (file.length() == 0) {
////            return;
////        }
////
////        FileInputStream fileStream = null;
////
////        try {
////
////            // Get the file to which ciphertext has been written.
////            fileStream = new FileInputStream(file);
////        } catch (FileNotFoundException e) {
////            // Not Empty
////        }
////
////        if (!crypto.isAvailable()) {
////            return;
////        }
////
////        // Creates an input stream which decrypts the data as
////        // it is read from it.
////        InputStream inputStream = null;
////        try {
////            Entity e = Entity.create(cipherEntity);
////            inputStream = crypto.getCipherInputStream(fileStream, e);
////            bf = new BufferedInputStream(inputStream);
////        } catch (IOException e) {
////            e.printStackTrace();
////        } catch (CryptoInitializationException e) {
////            e.printStackTrace();
////        } catch (KeyChainException e) {
////            e.printStackTrace();
////        }
////
////        // Read into a byte array.
////        int read;
////        byte[] buffer = new byte[1024];
////
////        // You must read the entire stream to completion.
////        // The verification is done at the end of the stream.
////        // Thus not reading till the end of the stream will cause
////        // a security bug. For safety, you should not
////        // use any of the data until it's been fully read or throw
////        // away the data if an exception occurs.
////        try {
////            while ((read = bf.read(buffer)) != -1) {
////                out.write(buffer, 0, read);
////            }
////            inputStream.close();
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
////
////        String preferencesString = new String(buffer).trim();
////
////        Type stringStringMap = new TypeToken<HashMap<String, String>>() {
////        }.getType();
////
////        preferences = gson.fromJson(preferencesString, stringStringMap);
////
//////        preferences = (HashMap<String, String>) m;
////    }
////
////    // Return value from EditorImpl#commitToMemory()
////    private static class MemoryCommitResult {
////        public boolean changesMade;  // any keys different?
////        public List<String> keysModified;  // may be null
////        public Set<OnSharedPreferenceChangeListener> listeners;  // may be null
////        public Map<?, ?> mapToWriteToDisk;
////        public final CountDownLatch writtenToDiskLatch = new CountDownLatch(1);
////        public volatile boolean writeToDiskResult = false;
////
////        public void setDiskWriteResult(boolean result) {
////            writeToDiskResult = result;
////            writtenToDiskLatch.countDown();
////        }
////    }
////
//}
