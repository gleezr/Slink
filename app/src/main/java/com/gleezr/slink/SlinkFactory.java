package com.gleezr.slink;

import android.content.Context;

import java.util.HashMap;

public class SlinkFactory {

    private static HashMap<String, Slink> Slinks = new HashMap<>();

    /**
     * Create a new instance of Slink.
     * In case you want only one instance you can use Slink Ctor Directly.
     *
     * @param context The Context of the activity to create Slink from.
     * @param id      The id of the Slink. Unique identifier.
     * @return Returns a new or formerly created slink instance paired with the given id.
     */
    public static Slink slinkFactory(Context context, String id) {
        if (Slinks.get(id) == null) {
            Slink newSlinkTemp = new Slink(context, id);
            Slinks.put(id, newSlinkTemp);
        }

        return (Slinks.get(id));
    }
}
