package com.gleezr.slink;

import android.content.Context;

import java.io.File;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;

public class SlinkManager {

    private static final String DEFAULT_SLINK_NAME = "Default";
    private static HashMap<String, Slink> Slinks = new HashMap<>();

    /**
     * Create a new instance of Slink.
     * In case you want only one instance you can use Slink Ctor Directly.
     *
     * @param context The Context of the activity to create Slink from.
     * @param name The ID of the Slink. Unique identifier.
     * @return {@link Slink} New or formerly created Slink instance paired with the given ID.
     */
    @SuppressWarnings("WeakerAccess")
    public static Slink getSlink(Context context, String name) {
        if (Slinks.get(name) == null) {
            File file = new File(context.getFilesDir(), name);
            Slink newSlinkTemp = new Slink(file, MODE_PRIVATE, context);
            Slinks.put(name, newSlinkTemp);
        }

        return (Slinks.get(name));
    }

    /**
     * Get the default Slink object
     * @param context The context of the calling activity
     * @return The default Slink object
     */
    @SuppressWarnings("WeakerAccess")
    public static Slink getDefaultSlink(Context context) {
        if (Slinks.get(DEFAULT_SLINK_NAME) == null) {

            File file = new File(context.getFilesDir(), DEFAULT_SLINK_NAME);
            //noinspection ResultOfMethodCallIgnored
            file.setReadable(true);
            //noinspection ResultOfMethodCallIgnored
            file.setWritable(true);
            Slink newSlinkTemp = new Slink(file, MODE_PRIVATE, context);
            Slinks.put(DEFAULT_SLINK_NAME, newSlinkTemp);
        }

        return (Slinks.get(DEFAULT_SLINK_NAME));
    }
}
