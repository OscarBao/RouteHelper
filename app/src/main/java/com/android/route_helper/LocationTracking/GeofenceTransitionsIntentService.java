package com.android.route_helper.LocationTracking;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * helper methods.
 */
public class GeofenceTransitionsIntentService extends IntentService {

    String logTag = "GeofenceService";

    public GeofenceTransitionsIntentService() {super("GeofenceTransitionsIntentService");}

    @Override
    public void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if(geofencingEvent.hasError()) {
            Log.e(logTag, GeofenceStatusCodes.getStatusCodeString(geofencingEvent.getErrorCode()));
            return;
        }
        int geofenceTransitionCode = geofencingEvent.getGeofenceTransition();

        if(geofenceTransitionCode == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransitionCode == Geofence.GEOFENCE_TRANSITION_EXIT) {
            performGeofenceProcessing(geofenceTransitionCode);
        }
    }


    /*
        Private methods
     */
    private void performGeofenceProcessing(int transitionCode) {
        switch(transitionCode) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                //TODO add tasks to perform on enter
                break;
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                //TODO add tasks to perform on exit
                break;
            default:
                break;
        }
    }

}
