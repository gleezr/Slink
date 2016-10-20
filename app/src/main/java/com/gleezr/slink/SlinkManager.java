//package com.gleezr.slink;
//
//import android.content.Context;
//
//import java.util.HashMap;
//
//public class SlinkManager {
//
//    final static String DEFAULT_SLINK_NAME = "Default";
//    private static HashMap<String, Slink> Slinks = new HashMap<>();
//
//    /**
//     * Create a new instance of Slink.
//     * In case you want only one instance you can use Slink Ctor Directly.
//     *
//     * @param context The Context of the activity to create Slink from.
//     * @param name      The id of the Slink. Unique identifier.
//     * @return Returns a new or formerly created slink instance paired with the given id.
//     */
//    public static Slink getSlink(Context context, String name) {
//        if (Slinks.get(name) == null) {
//            Slink newSlinkTemp = new Slink(context, name);
//            Slinks.put(name, newSlinkTemp);
//        }
//
//        return (Slinks.get(name));
//    }
//
//    public static Slink getDefaultSlink(Context context) {
//        if (Slinks.get(DEFAULT_SLINK_NAME) == null) {
//            Slink newSlinkTemp = new Slink(context, DEFAULT_SLINK_NAME);
//            Slinks.put(DEFAULT_SLINK_NAME, newSlinkTemp);
//        }
//
//        return (Slinks.get(DEFAULT_SLINK_NAME));
//    }
//}
