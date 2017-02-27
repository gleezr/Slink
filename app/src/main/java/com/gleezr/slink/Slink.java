/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gleezr.slink;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;

import com.facebook.android.crypto.keychain.AndroidConceal;
import com.facebook.android.crypto.keychain.SharedPrefsBackedKeyChain;
import com.facebook.crypto.Crypto;
import com.facebook.crypto.CryptoConfig;
import com.facebook.crypto.Entity;
import com.facebook.crypto.exception.CryptoInitializationException;
import com.facebook.crypto.exception.KeyChainException;
import com.facebook.crypto.keychain.KeyChain;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.CountDownLatch;

import static java.lang.System.out;

public final class Slink implements SharedPreferences {
    private static final String TAG = "Slink";
    private static final String ENCODING = "UTF-8";

    // Lock ordering rules:
    // - acquire Slink.this before EditorImpl.this
    // - acquire mWritingToDiskLock before EditorImpl.this

    private final File mFile;
    private final File mBackupFile;

    // Unused - need to implement modes.
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private final int mMode;

    private Map<String, Object> mMap;     // guarded by 'this'
    private int mDiskWritesInFlight = 0;  // guarded by 'this'
    private boolean mLoaded = false;      // guarded by 'this'
    private String cipherEntity = "Properties";
    private Gson gson;
    private Crypto crypto;
    private final Object mWritingToDiskLock = new Object();
    private static final Object mContent = new Object();
    private final WeakHashMap<OnSharedPreferenceChangeListener, Object> mListeners =
            new WeakHashMap<>();

    /**
     * Explicitly create a Slink instance.
     * Unless particularly needed refer to the SlinkManager to get a Slink instance.
     * @param file The SharedPreferences file
     * @param mode Unused
     * @param context The context of the calling activity
     */
    @SuppressWarnings("WeakerAccess")
    public Slink(File file, int mode, Context context) {
        gson = new Gson();

        cipherEntity = cipherEntity + file.getName();

        // Creates a new Crypto value with default implementations of a key chain
        KeyChain keyChain = new SharedPrefsBackedKeyChain(context, CryptoConfig.KEY_256);
        crypto = AndroidConceal.get().createDefaultCrypto(keyChain);
        mFile = file;
        mBackupFile = makeBackupFile(file);
        mMode = mode;
        mLoaded = false;
        mMap = null;
        startLoadFromDisk();
    }

    private void startLoadFromDisk() {
        synchronized (this) {
            mLoaded = false;
        }
        new Thread("SharedPreferencesImpl-load") {
            public void run() {
                loadFromDisk();
            }
        }.start();
    }

    private void loadFromDisk() {
        synchronized (Slink.this) {
            if (mLoaded) {
                return;
            }
            if (mBackupFile.exists()) {
                //noinspection ResultOfMethodCallIgnored
                mFile.delete();
                //noinspection ResultOfMethodCallIgnored
                mBackupFile.renameTo(mFile);
            }
        }

        // Debugging
        if (mFile.exists() && !mFile.canRead()) {
            Log.w(TAG, "Attempt to read preferences file " + mFile + " without permission");
        }

        if (mFile.canRead()) {
            BufferedInputStream str = null;

            // Creates an input stream which decrypts the data as
            // it is read from it.
            InputStream inputStream = null;

            try {
                str = new BufferedInputStream(new FileInputStream(mFile), 1024);

                Gson gson = new Gson();

                try {
                    Entity e = Entity.create(cipherEntity);
                    inputStream = crypto.getCipherInputStream(str, e);
                    str = new BufferedInputStream(inputStream);
                } catch (IOException | CryptoInitializationException | KeyChainException e) {
                    Log.e(TAG, "loadFromDisk could not open inputStream to file");
                    str.close();
                    return;
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
                while ((read = str.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }

                String preferencesString = new String(buffer, ENCODING).trim();

                Type stringStringMap = new TypeToken<HashMap<String, Object>>() { }.getType();

                mMap = gson.fromJson(preferencesString, stringStringMap);
            } catch (IOException e) {
                Log.w(TAG, "getSharedPreferences", e);
            } finally {
                try {
                    if (str != null) {
                        str.close();
                    }
                    if (inputStream != null) {
                        inputStream.close();
                    }
                } catch (IOException e) {
                    Log.e(TAG, "loadFromDisk Couldn't close file");
                }
            }
        }

        synchronized (Slink.this) {
            mLoaded = true;
//            if (map != null) {
//                mMap = map;
//            } else {
//                mMap = new HashMap<>();
//            }
            mMap = new HashMap<>();
            notifyAll();
        }
    }

    private static File makeBackupFile(File prefsFile) {
        return new File(prefsFile.getPath() + ".bak");
    }

    /**
     * Registers a callback to be invoked when a change happens to a preference.
     * @param listener The callback that will run.
     */
    public void registerOnSharedPreferenceChangeListener(
            OnSharedPreferenceChangeListener listener) {
        synchronized (this) {
            mListeners.put(listener, mContent);
        }
    }

    /**
     * Unregisters a previous callback.
     * @param listener The callback that should be unregistered.
     */
    public void unregisterOnSharedPreferenceChangeListener(
            OnSharedPreferenceChangeListener listener) {
        synchronized (this) {
            mListeners.remove(listener);
        }
    }

    private void awaitLoadedLocked() {
//        if (!mLoaded) {
            // Raise an explicit StrictMode onReadFromDisk for this
            // thread, since the real read will be in a different
            // thread and otherwise ignored by StrictMode.
//        }
        while (!mLoaded) {
            try {
                wait();
            } catch (InterruptedException unused) {
                //Empty
            }
        }
    }

    /**
     * Retrieve all values from the preferences.
     * Note that you must not modify the collection returned by this method, or alter any of
     * its contents. The consistency of your stored data is not guaranteed if you do.
     * @return Returns a map containing a list of pairs key/value representing the preferences.
     */
    public Map<String, ?> getAll() {
        synchronized (this) {
            awaitLoadedLocked();
            return new HashMap<>(mMap);
        }
    }

    /**
     * Retrieve a String value from the preferences.
     * @param key The name of the preference to retrieve
     * @param defValue Value to return if this preference does not exist.
     * @return Returns the preference value if it exists, or defValue. Throws ClassCastException.
     *         If there is a preference with this name that is not a String.
     */
    @Nullable
    public String getString(String key, @Nullable String defValue) {
        synchronized (this) {
            awaitLoadedLocked();
            String v = (String) mMap.get(key);
            return v != null ? v : defValue;
        }
    }

    /**
     * Retrieve a set of String values from the preferences.
     * Note that you must not modify the set instance returned by this call. The consistency of
     * the stored data is not guaranteed if you do, nor is your ability to modify the instance
     * at all.
     * @param key The name of the preference to retrieve.
     * @param defValues Values to return if this preference does not exist.
     * @return Returns the preference values if they exist, or defValues. Throws ClassCastException
     *         if there is a preference with this name that is not a Set.
     */
    @Nullable
    public Set<String> getStringSet(String key, @Nullable Set<String> defValues) {
        synchronized (this) {
            awaitLoadedLocked();
            //noinspection unchecked
            Set<String> v = (Set<String>) mMap.get(key);
            return v != null ? v : defValues;
        }
    }

    /**
     * Retrieve an int value from the preferences.
     * @param key The name of the preference to retrieve.
     * @param defValue Value to return if this preference does not exist.
     * @return Returns the preference value if it exists, or defValue. Throws
     *         ClassCastException if there is a preference with this name that is not an int.
     */
    public int getInt(String key, int defValue) {
        synchronized (this) {
            awaitLoadedLocked();
            Integer v = (Integer) mMap.get(key);
            return v != null ? v : defValue;
        }
    }

    /**
     * Retrieve a long value from the preferences.
     * @param key The name of the preference to retrieve.
     * @param defValue Value to return if this preference does not exist.
     * @return Returns the preference value if it exists, or defValue. Throws
     *         ClassCastException if there is a preference with this name that is not a long.
     */
    public long getLong(String key, long defValue) {
        synchronized (this) {
            awaitLoadedLocked();
            Long v = (Long) mMap.get(key);
            return v != null ? v : defValue;
        }
    }

    /**
     * Retrieve a float value from the preferences.
     * @param key The name of the preference to retrieve.
     * @param defValue Value to return if this preference does not exist.
     * @return Returns the preference value if it exists, or defValue. Throws
     *         ClassCastException if there is a preference with this name that is not a float.
     */
    public float getFloat(String key, float defValue) {
        synchronized (this) {
            awaitLoadedLocked();
            Float v = (Float) mMap.get(key);
            return v != null ? v : defValue;
        }
    }

    /**
     * Retrieve a boolean value from the preferences.
     * @param key The name of the preference to retrieve.
     * @param defValue Value to return if this preference does not exist.
     * @return Returns the preference value if it exists, or defValue. Throws ClassCastException
     *         if there is a preference with this name that is not a boolean.
     */
    public boolean getBoolean(String key, boolean defValue) {
        synchronized (this) {
            awaitLoadedLocked();
            Boolean v = (Boolean) mMap.get(key);
            return v != null ? v : defValue;
        }
    }

    /**
     * Checks whether the preferences contains a preference.
     * @param key The name of the preference to check.
     * @return Returns true if the preference exists in the preferences, otherwise false.
     */
    public boolean contains(String key) {
        synchronized (this) {
            awaitLoadedLocked();
            return mMap.containsKey(key);
        }
    }

    /**
     * Create a new Editor for these preferences, through which you can make modifications to
     * the data in the preferences and atomically commit those changes back to the
     * SharedPreferences object. Note that you must call commit() to have any changes you perform
     * in the Editor actually show up in the SharedPreferences.
     * @return Returns a new instance of the SharedPreferences.Editor interface, allowing
     *         you to modify the values in this SharedPreferences object.
     */
    public Editor edit() {
        // TODO: remove the need to call awaitLoadedLocked() when
        // requesting an editor.  will require some work on the
        // Editor, but then we should be able to do:
        //
        //      context.getSharedPreferences(..).edit().putString(..).apply()
        //
        // ... all without blocking.
        synchronized (this) {
            awaitLoadedLocked();
        }

        return new EditorImpl();
    }

    // Return value from EditorImpl#commitToMemory()
    private static class MemoryCommitResult {
        boolean changesMade;  // any keys different?
        List<String> keysModified;  // may be null
        Set<OnSharedPreferenceChangeListener> listeners;  // may be null
        Map<?, ?> mapToWriteToDisk;
        final CountDownLatch writtenToDiskLatch = new CountDownLatch(1);
        volatile boolean writeToDiskResult = false;

        void setDiskWriteResult(boolean result) {
            writeToDiskResult = result;
            writtenToDiskLatch.countDown();
        }
    }

    @SuppressWarnings("WeakerAccess")
    public final class EditorImpl implements Editor {
        private final Map<String, Object> mModified = new HashMap<>();
        private boolean mClear = false;

        /**
         * Set a String value in the preferences editor, to be written back once commit()
         * or apply() are called.
         * @param key The name of the preference to modify.
         * @param value The new value for the preference. Passing null for this argument is
         *              equivalent to calling remove(String) with this key.
         * @return The Editor object for chaining purposes
         */
        public Editor putString(String key, @Nullable String value) {
            synchronized (this) {
                mModified.put(key, value);
                return this;
            }
        }

        /**
         * Set a set of String values in the preferences editor, to be written back once commit()
         * or apply() is called.
         * @param key The name of the preference to modify.
         * @param values The set of new values for the preference. Passing null for this argument
         *               is equivalent to calling remove(String) with this key.
         * @return The Editor object for chaining purposes
         */
        public Editor putStringSet(String key, @Nullable Set<String> values) {
            synchronized (this) {
                mModified.put(key, (values == null) ? null : new HashSet<>(values));
                return this;
            }
        }

        /**
         * Set an int value in the preferences editor, to be written back once commit()
         * or apply() are called.
         * @param key The name of the preference to modify.
         * @param value The new value for the preference.
         * @return Returns a reference to the same Editor object, so you can chain put
         *         calls together.
         */
        public Editor putInt(String key, int value) {
            synchronized (this) {
                mModified.put(key, value);
                return this;
            }
        }

        /**
         * Set a long value in the preferences editor, to be written back once commit()
         * or apply() are called.
         * @param key The name of the preference to modify.
         * @param value The new value for the preference.
         * @return Returns a reference to the same Editor object, so you can
         *         chain put calls together.
         */
        public Editor putLong(String key, long value) {
            synchronized (this) {
                mModified.put(key, value);
                return this;
            }
        }

        /**
         * Returns a reference to the same Editor object, so you can chain put calls together.
         * @param key The name of the preference to modify.
         * @param value The new value for the preference.
         * @return Returns a reference to the same Editor object, so you can chain
         *         put calls together.
         */
        public Editor putFloat(String key, float value) {
            synchronized (this) {
                mModified.put(key, value);
                return this;
            }
        }

        /**
         * Set a boolean value in the preferences editor, to be written back once commit()
         * or apply() are called.
         * @param key The name of the preference to modify.
         * @param value The new value for the preference.
         * @return Returns a reference to the same Editor object,
         *         so you can chain put calls together.
         */
        public Editor putBoolean(String key, boolean value) {
            synchronized (this) {
                mModified.put(key, value);
                return this;
            }
        }

        /**
         * Mark in the editor that a preference value should be removed, which will be done
         * in the actual preferences once commit() is called.
         * Note that when committing back to the preferences, all removals are done first,
         * regardless of whether you called remove before or after put methods on this editor.
         * @param key The name of the preference to remove.
         * @return Returns a reference to the same Editor object, so you can
         *         chain put calls together.
         */
        public Editor remove(String key) {
            synchronized (this) {
                mModified.put(key, this);
                return this;
            }
        }

        /**
         * Mark in the editor to remove all values from the preferences.
         * Once commit is called, the only remaining preferences will be any that you have
         * defined in this editor. Note that when committing back to the preferences, the clear
         * is done first, regardless of whether you called clear before or after put methods
         * on this editor.
         * @return Returns a reference to the same Editor object, so you can chain
         *         put calls together.
         */
        public Editor clear() {
            synchronized (this) {
                mClear = true;
                return this;
            }
        }

        /**
         * Commit your preferences changes back from this Editor to the SharedPreferences object
         * it is editing. This atomically performs the requested modifications, replacing whatever
         * is currently in the SharedPreferences. Note that when two editors are modifying
         * preferences at the same time, the last one to call apply wins. Unlike commit(), which
         * writes its preferences out to persistent storage synchronously, apply() commits its
         * changes to the in-memory SharedPreferences immediately but starts an asynchronous
         * commit to disk and you won't be notified of any failures. If another editor on this
         * SharedPreferences does a regular commit() while a apply() is still outstanding, the
         * commit() will block until all async commits are completed as well as the commit itself.
         * As SharedPreferences instances are singletons within a process, it's safe to replace any
         * instance of commit() with apply() if you were already ignoring the return value. You
         * don't need to worry about Android component lifecycles and their interaction with
         * apply() writing to disk. The framework makes sure in-flight disk writes from apply()
         * complete before switching states.
         */
        public void apply() {
            final MemoryCommitResult mcr = commitToMemory();
            final Runnable awaitCommit = new Runnable() {
                public void run() {
                    try {
                        mcr.writtenToDiskLatch.await();
                    } catch (InterruptedException ignored) {
                        //Empty
                    }
                }
            };

            QueuedWork.add(awaitCommit);

            Runnable postWriteRunnable = new Runnable() {
                public void run() {
                    awaitCommit.run();
                    QueuedWork.remove(awaitCommit);
                }
            };

            Slink.this.enqueueDiskWrite(mcr, postWriteRunnable);

            // Okay to notify the listeners before it's hit disk
            // because the listeners should always get the same
            // SharedPreferences instance back, which has the
            // changes reflected in memory.
            notifyListeners(mcr);
        }

        // Returns true if any changes were made
        private MemoryCommitResult commitToMemory() {
            MemoryCommitResult mcr = new MemoryCommitResult();
            synchronized (Slink.this) {
                // We optimistically don't make a deep copy until
                // a memory commit comes in when we're already
                // writing to disk.
                if (mDiskWritesInFlight > 0) {
                    // We can't modify our mMap as a currently
                    // in-flight write owns it.  Clone it before
                    // modifying it.
                    // noinspection unchecked
                    mMap = new HashMap<>(mMap);
                }
                mcr.mapToWriteToDisk = mMap;
                mDiskWritesInFlight++;

                boolean hasListeners = mListeners.size() > 0;
                if (hasListeners) {
                    mcr.keysModified = new ArrayList<>();
                    mcr.listeners = new HashSet<>(mListeners.keySet());
                }

                synchronized (this) {
                    if (mClear) {
                        if (!mMap.isEmpty()) {
                            mcr.changesMade = true;
                            mMap.clear();
                        }
                        mClear = false;
                    }

                    for (Map.Entry<String, Object> e : mModified.entrySet()) {
                        String k = e.getKey();
                        Object v = e.getValue();
                        // "this" is the magic value for a removal mutation. In addition,
                        // setting a value to "null" for a given key is specified to be
                        // equivalent to calling remove on that key.
                        if (v == this || v == null) {
                            if (!mMap.containsKey(k)) {
                                continue;
                            }
                            mMap.remove(k);
                        } else {
                            if (mMap.containsKey(k)) {
                                Object existingValue = mMap.get(k);
                                if (existingValue != null && existingValue.equals(v)) {
                                    continue;
                                }
                            }
                            mMap.put(k, v);
                        }

                        mcr.changesMade = true;
                        if (hasListeners) {
                            mcr.keysModified.add(k);
                        }
                    }

                    mModified.clear();
                }
            }
            return mcr;
        }

        /**
         * Commit your preferences changes back from this Editor to the SharedPreferences
         * object it is editing. This atomically performs the requested modifications, replacing
         * whatever is currently in the SharedPreferences.Note that when two editors are modifying
         * preferences at the same time, the last one to call commit wins.
         * If you don't care about the return value and you're using this from your application's
         * main thread, consider using apply() instead.
         * @return Returns true if the new values were successfully written to persistent storage.
         */
        public boolean commit() {
            MemoryCommitResult mcr = commitToMemory();
            Slink.this.enqueueDiskWrite(
                    mcr, null /* sync write on this thread okay */);
            try {
                mcr.writtenToDiskLatch.await();
            } catch (InterruptedException e) {
                return false;
            }
            notifyListeners(mcr);
            return mcr.writeToDiskResult;
        }

        private void notifyListeners(final MemoryCommitResult mcr) {
            if (mcr.listeners == null || mcr.keysModified == null
                    || mcr.keysModified.size() == 0) {
                return;
            }
            if (Looper.myLooper() == Looper.getMainLooper()) {
                for (int i = mcr.keysModified.size() - 1; i >= 0; i--) {
                    final String key = mcr.keysModified.get(i);
                    for (OnSharedPreferenceChangeListener listener : mcr.listeners) {
                        if (listener != null) {
                            listener.onSharedPreferenceChanged(Slink.this, key);
                        }
                    }
                }
            } else {
                // Run this function on the main thread.
                new Handler().post(new Runnable() {
                    public void run() {
                        notifyListeners(mcr);
                    }
                });
            }
        }
    }

    /**
     * Enqueue an already-committed-to-memory result to be written
     * to disk.
     * They will be written to disk one-at-a-time in the order
     * that they're enqueued.
     *
     * @param postWriteRunnable if non-null, we're being called
     *                          from apply() and this is the runnable to run after
     *                          the write proceeds.  if null (from a regular commit()),
     *                          then we're allowed to do this disk write on the main
     *                          thread (which in addition to reducing allocations and
     *                          creating a background thread, this has the advantage that
     *                          we catch them in userdebug StrictMode reports to convert
     *                          them where possible to apply() ...)
     */
    private void enqueueDiskWrite(final MemoryCommitResult mcr, final Runnable postWriteRunnable) {
        final Runnable writeToDiskRunnable = new Runnable() {
            public void run() {
                synchronized (mWritingToDiskLock) {
                    writeToFile(mcr);
                }
                synchronized (Slink.this) {
                    mDiskWritesInFlight--;
                }
                if (postWriteRunnable != null) {
                    postWriteRunnable.run();
                }
            }
        };

        final boolean isFromSyncCommit = (postWriteRunnable == null);

        // Typical #commit() path with fewer allocations, doing a write on
        // the current thread.
        if (isFromSyncCommit) {
            boolean wasEmpty;
            synchronized (Slink.this) {
                wasEmpty = mDiskWritesInFlight == 1;
            }
            if (wasEmpty) {
                writeToDiskRunnable.run();
                return;
            }
        }

        QueuedWork.singleThreadExecutor().execute(writeToDiskRunnable);
    }

    private static FileOutputStream createFileOutputStream(File file) {
        FileOutputStream str = null;
        try {
            str = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            File parent = file.getParentFile();
            if (!parent.mkdir()) {
                Log.e(TAG, "Couldn't create directory for SharedPreferences file " + file);
                return null;
            }

            //noinspection ResultOfMethodCallIgnored
            parent.setWritable(true);
            //noinspection ResultOfMethodCallIgnored
            parent.setReadable(true);

//            FileUtils.setPermissions(
//                    parent.getPath(),
//                    FileUtils.S_IRWXU | FileUtils.S_IRWXG | FileUtils.S_IXOTH,
//                    -1, -1);
            try {
                str = new FileOutputStream(file);
            } catch (FileNotFoundException e2) {
                Log.e(TAG, "Couldn't create SharedPreferences file " + file, e2);
            }
        }
        return str;
    }

    // Note: must hold mWritingToDiskLock
    private void writeToFile(MemoryCommitResult mcr) {
        // Rename the current file so it may be used as a backup during the next read
        if (mFile.exists()) {
            if (!mcr.changesMade) {
                // If the file already exists, but no changes were
                // made to the underlying map, it's wasteful to
                // re-write the file.  Return as if we wrote it
                // out.
                mcr.setDiskWriteResult(true);
                return;
            }
            if (!mBackupFile.exists()) {
                if (!mFile.renameTo(mBackupFile)) {
                    Log.e(TAG, "Couldn't rename file " + mFile
                            + " to backup file " + mBackupFile);
                    mcr.setDiskWriteResult(false);
                    return;
                }
            } else {
                //noinspection ResultOfMethodCallIgnored
                mFile.delete();
            }
        }

        // Attempt to write the file, delete the backup and return true as atomically as
        // possible.  If any exception occurs, delete the new file; next time we will restore
        // from the backup.
        try {
            FileOutputStream str = createFileOutputStream(mFile);

            if (str == null) {
                mcr.setDiskWriteResult(false);
                return;
            }

            Type stringStringMap = new TypeToken<HashMap<String, Object>>() { }.getType();
            String map = gson.toJson(mcr.mapToWriteToDisk, stringStringMap);

            // Check for whether the crypto functionality is available
            // This might fail if Android does not load libraries correctly.
            if (!crypto.isAvailable()) {
                return;
            }

            OutputStream fileStream;
            fileStream = new BufferedOutputStream(str);

            // Creates an output stream which encrypts the data as
            // it is written to it and writes it out to the file.
            OutputStream outputStream;
            try {
                outputStream = crypto.getCipherOutputStream(fileStream,
                        Entity.create(cipherEntity));
            } catch (IOException | CryptoInitializationException | KeyChainException e) {
                Log.e(TAG, "writeToFile Couldn't get CypherOutputStream");
                return;
            }
//
//        byte[] b = new byte[1024 - map.getBytes().length];
//        byte[] c = new byte[1024];
//        System.arraycopy(map.getBytes(), 0, c, 0, map.getBytes().length);
//        System.arraycopy(b, 0, c, map.getBytes().length, b.length);

            try {
                // Write plaintext to it.
                byte[] bytes = map.getBytes(ENCODING);
                outputStream.write(bytes);
                outputStream.close();
            } catch (IOException e) {
                Log.e(TAG, "writeToFile Couldn't write to file");
                return;
            }

            str.close();
            // Writing was successful, delete the backup file if there is one.
            //noinspection ResultOfMethodCallIgnored
            mBackupFile.delete();
            mcr.setDiskWriteResult(true);
            return;
        } catch (IOException e) {
            Log.w(TAG, "writeToFile: Got exception:", e);
        }
        // Clean up an unsuccessfully written file
        if (mFile.exists()) {
            if (!mFile.delete()) {
                Log.e(TAG, "Couldn't clean up partially-written file " + mFile);
            }
        }
        mcr.setDiskWriteResult(false);
    }
}
