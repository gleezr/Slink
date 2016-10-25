package com.gleezr.slink;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.File;
import java.util.HashMap;

public class SlinkManager {

    static final String DEFAULT_SLINK_NAME = "Default";
    private static HashMap<String, Slink> Slinks = new HashMap<>();

    /**
     * Create a new instance of Slink.
     * In case you want only one instance you can use Slink Ctor Directly.
     *
     * @param context The Context of the activity to create Slink from.
     * @param name    The id of the Slink. Unique identifier.
     * @return Returns a new or formerly created slink instance paired with the given id.
     */
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
    public static Slink getDefaultSlink(Context context) {
        if (Slinks.get(DEFAULT_SLINK_NAME) == null) {

            File file = new File(context.getFilesDir(), DEFAULT_SLINK_NAME);

            Slink newSlinkTemp = new Slink(file, MODE_PRIVATE, context);
            Slinks.put(DEFAULT_SLINK_NAME, newSlinkTemp);
        }

        return (Slinks.get(DEFAULT_SLINK_NAME));
    }
}
