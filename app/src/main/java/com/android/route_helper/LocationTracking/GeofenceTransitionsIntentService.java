package com.android.route_helper.LocationTracking;

import android.app.IntentService;
import android.content.Intent;
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
            for(Geofence gf : geofencingEvent.getTriggeringGeofences()) {
                performGeofenceProcessing(geofenceTransitionCode, gf);
            }
        }
    }


    /*
        Private methods
     */
    private void performGeofenceProcessing(int transitionCode, Geofence gf) {

        Intent nextStepIntent = new Intent("nextStep");
        nextStepIntent.putExtra("requestId", gf.getRequestId());
        sendBroadcast(nextStepIntent);


        switch(transitionCode) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                break;
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                break;
            default:
                break;
        }
    }

}
