package com.android.route_helper.LocationTracking;

import android.content.Context;
import android.location.Location;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;

import java.util.ArrayList;

/**
 * Created by Oscar_Local on 7/2/2016.
 */
public class LocationTracker {
    Context context;

    static int currentId;
    static ArrayList<Geofence> geofencesList;

    public LocationTracker(Context context) {
        this.context = context;
        currentId = 0;
        geofencesList = new ArrayList<>();
    }

    public static void addLocation(Location loc) {
        addGeofence(loc);
    }

    public static GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofencesList);
        return builder.build();
    }



    /*
        PRIVATE HELPER METHODS
     */
    private static void addGeofence(Location newEntry) {
        geofencesList.add(new Geofence.Builder()
            .setRequestId(nextRequestId())
            .setCircularRegion(newEntry.getLatitude(), newEntry.getLongitude(), LocationConstants.GEOFENCE_RADIUS)
            .setExpirationDuration(LocationConstants.GEOFENCE_EXPIRATION_DURATION)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
        .build());
    }



    private static String nextRequestId() {
        return (currentId < 1000) ? Integer.toString(currentId++) : Integer.toString(currentId = 0);
    }
}
