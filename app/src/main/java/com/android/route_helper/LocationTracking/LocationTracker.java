package com.android.route_helper.LocationTracking;

import android.app.PendingIntent;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

/**
 * Created by Oscar_Local on 7/2/2016.
 */
public class LocationTracker {
    Context context;

    static GoogleApiClient thisApiClient;
    static ConnectionDealer connectionDealer;
    static int currentId;
    static ArrayList<Geofence> geofencesList;

    public LocationTracker(Context context) {
        this.context = context;
        connectionDealer = new ConnectionDealer();

        thisApiClient = buildGoogleApiClient(context);
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

    public synchronized static void addGeofencesToActivity(PendingIntent generatedIntent) {
        try {
            LocationServices.GeofencingApi.addGeofences(thisApiClient, getGeofencingRequest(), generatedIntent);
        }
        catch (SecurityException e) {
            e.printStackTrace();
        }
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

    private static GoogleApiClient buildGoogleApiClient(Context context) {
        return (new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(connectionDealer)
                .addOnConnectionFailedListener(connectionDealer)
                .addApi(LocationServices.API)
                .build()
        );
    }

    private static String nextRequestId() {
        return (currentId < 1000) ? Integer.toString(currentId++) : Integer.toString(currentId = 0);
    }

    /*
        PRIVATE INNER CLASSES
     */
    private class ConnectionDealer implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status> {

        String logTag = "ConnectionDealer";

        @Override
        public void onConnected(Bundle connectionHint) {
            Log.i(logTag, "Connection success");
        }
        @Override
        public void onConnectionFailed(ConnectionResult result) {
            Log.i(logTag, "Connection failed");
        }
        @Override
        public void onConnectionSuspended(int cause) {
            Log.i(logTag, "Connection suspended");
        }

        @Override
        public void onResult(Status status) {
            if(status.isSuccess()) {
                Log.i(logTag, status.getStatusMessage());
            }
            else {
                Log.e(logTag, status.getStatusMessage());
            }
        }

    }
}
