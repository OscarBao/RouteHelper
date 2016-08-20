package com.android.route_helper.Maps;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;

import com.android.route_helper.CheckpointManaging.Checkpoints;
import com.android.route_helper.CheckpointManaging.Place;

import java.util.HashMap;

/**
 * Created by Erik Mei on 6/30/2016.
 */
public class MapsManager{
    private static SharedPreferences sp;
    private static Location sourceLoc;
    private static Location destLoc;
    private static HashMap<String, Place> flagMap;
    private static int REQUEST_GET_MAP_LOC = 0;

    public MapsManager() {
        flagMap = new HashMap<>();
    };

    public static void loadMap(Context currContext, String flag) {

        //ideally, targetClass is the Map Activity
        Class<? extends MapsActivity> map = MapsActivity.getMapClass(flag);
        Intent switchToMap = new Intent(currContext, map);
        switchToMap.putExtra("flag", flag);
        currContext.startActivity(switchToMap);
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


    public static void savePlace(Place loc, String flag) {
        flagMap.put(flag, loc);
    }
    public static Place getPlace(String flag) {
        return flagMap.get(flag);
    }
    public static boolean hasPlace(String flag) {
        return flagMap.containsKey(flag);
    }


}
