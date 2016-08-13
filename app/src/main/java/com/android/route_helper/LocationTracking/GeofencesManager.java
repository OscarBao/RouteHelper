package com.android.route_helper.LocationTracking;


import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.*;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;


/**
 * Created by Oscar_Local on 8/12/2016.
 */

public class GeofencesManager {

    private GoogleApiClient apiClient;
    private Context appContext;
    private ConnectionDealer connectionDealer;
    private ArrayList<Geofence> geofenceList;
    private GeofencingRequest geofencingRequest;
    private String currentRequestId = "";

    public GeofencesManager(Context context) {
        this.appContext = context;
        connectionDealer = new ConnectionDealer();
        apiClient = buildGoogleApiClient(appContext);
    }

    public void bootGeofencingWithLocations(ArrayList<Location> locationsList) {
        bootGeofencingWithGeofences(toGeofenceList(locationsList));
    }

    public void bootGeofencingWithGeofences(ArrayList<Geofence> geofenceList) {
        this.geofenceList = geofenceList;
        apiClient.connect();
    }


    /*
        PRIVATE HELPER METHODS
     */
    private GoogleApiClient buildGoogleApiClient(Context context) {
        return (new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(connectionDealer)
                .addOnConnectionFailedListener(connectionDealer)
                .build());
    }

    private GeofencingRequest getGeofencingRequest() {
        if(geofencingRequest == null) {
            geofencingRequest = new GeofencingRequest.Builder()
                                    .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                                    .addGeofences(geofenceList)
                                    .build();
        }
        return geofencingRequest;
    }

    private PendingIntent getGeofenceTransitionPendingIntent() {
        Intent intent = new Intent(appContext, GeofenceTransitionsIntentService.class);
        return PendingIntent.getService(appContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private ArrayList<Geofence> toGeofenceList(ArrayList<Location> locations) {
        ArrayList<Geofence> geofences = new ArrayList<>();
        for(int i = 0; i < locations.size(); i++) {
            currentRequestId += "c";
            geofences.add(new Geofence.Builder()
                    .setRequestId(currentRequestId)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                            Geofence.GEOFENCE_TRANSITION_EXIT)
                    .setCircularRegion(locations.get(i).getLatitude(), locations.get(i).getLongitude(),
                            LocationConstants.GEOFENCE_RADIUS)
                    .setExpirationDuration(LocationConstants.GEOFENCE_EXPIRATION_DURATION)
                    .setLoiteringDelay(LocationConstants.LOITERING_TIME)
                    .build());
        }
        return geofences;
    }

    private void pushGeofences() {
        if (geofenceList == null || geofenceList.size() < 1) {
            return;
        }
        try {
            LocationServices.GeofencingApi.addGeofences(apiClient, getGeofencingRequest(), getGeofenceTransitionPendingIntent());
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    /*
        INNER CLASS
     */
    private class ConnectionDealer implements ConnectionCallbacks,
            OnConnectionFailedListener {
        @Override
        public void onConnected(Bundle connectionHint) {
            pushGeofences();
        }

        @Override
        public void onConnectionSuspended(int i) {
        }

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
        }
    }
}
