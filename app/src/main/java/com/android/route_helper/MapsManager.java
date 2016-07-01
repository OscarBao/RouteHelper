package com.android.route_helper;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;

/**
 * Created by Erik Mei on 6/30/2016.
 */
public class MapsManager {
    private static SharedPreferences sp;
    private Context context;

    public MapsManager(Context currContext) {
        sp = context.getSharedPreferences("MyPrefs", 0);
    }

    public static void loadMap(Context c) {
        Intent switchToMap = new Intent(c, /*Maps Activivty */);
        startActivity(switchToMap);
    }

    public static void closeMap(Context c) {

    }

    public static void saveSourceLocation(Location loc) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("sourceLocLat", Double.toString(loc.getLatitude()));
        editor.putString("sourceLocLong", Double.toString(loc.getLatitude()));
        editor.commit();
    }

    public static void saveDestinationLocation(Location loc) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("destLocLat", Double.toString(loc.getLatitude()));
        editor.putString("destLocLong", Double.toString(loc.getLatitude()));
        editor.commit();
    }

    public static Location getSourceLocation() {return null;}

    public static Location getDestinationLocation() {return null;}

}
