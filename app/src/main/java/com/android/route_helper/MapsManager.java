package com.android.route_helper;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.widget.BaseAdapter;

import junit.framework.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Erik Mei on 6/30/2016.
 */
public class MapsManager{
    private static SharedPreferences sp;
    private static Location sourceLoc;
    private static Location destLoc;
    private static HashMap<String, Location> flagMap;
    private static int REQUEST_GET_MAP_LOC = 0;

    public MapsManager() {
        flagMap = new HashMap<>();
    };

    public static void loadMap(Context currContext, String flag) {

        //ideally, targetClass is the Map Activity
        Intent switchToMap = new Intent(currContext, MapsActivity.class);
        switchToMap.putExtra("flag", flag);
        ((Activity)currContext).startActivityForResult(switchToMap, REQUEST_GET_MAP_LOC);

    }

    public static void closeMap(Context currContext, Class<?> targetClass) {
        if(targetClass == null) {
            //Go to previous screen if none has been specified
            //((Activity)currContext).setResult(Activity.RESULT_OK, new Intent().putExtra("isSource", isSource));
            ((Activity)currContext).finish();
            return;
        }
        Intent switchToMap = new Intent(/*Maps Activity*/currContext, targetClass);
        currContext.startActivity(switchToMap);
    }

    /*
    public static void saveLocation(Location loc, boolean isSourceLoc) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString((isSourceLoc) ? "sourceLocLat" : "destLocLat", Double.toString(loc.getLatitude()));
        editor.putString((isSourceLoc) ? "sourceLocLng" : "destLocLng", Double.toString(loc.getLongitude()));
        editor.putString((isSourceLoc) ? "sourceLocProvider" : "destLocProvider", loc.getProvider());
        editor.commit();
    }*/

    public static void saveLocation(Location loc, String flag) {
        flagMap.put(flag, loc);
    }

    public static Location getLocation(String flag) {
        return flagMap.get(flag);
    }

    public static boolean hasLocation(String flag) {
        return flagMap.containsKey(flag);
    }

    /*
    public static Location getLocation(boolean isSource) {
        String provider;
        Double lat, lng;
        lat = (isSource) ? Double.parseDouble(sp.getString("sourceLocLat", "99999")) : Double.parseDouble(sp.getString("destLocLat", "99999"));
        lng = (isSource) ? Double.parseDouble(sp.getString("sourceLocLng", "99999")) : Double.parseDouble(sp.getString("destLocLng", "99999"));
        provider = (isSource) ? sp.getString("sourceLocProvider", "No provider found ") : sp.getString("destLocProvider", "No provider found");
        Location loc = new Location(provider);
        loc.setLatitude(lat);
        loc.setLongitude(lng);
        return loc;
    }*/

}
