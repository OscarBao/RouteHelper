package com.android.route_helper;


import android.app.PendingIntent;
import android.content.Intent;
import android.location.Location;
import android.app.Activity;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.android.route_helper.LocationTracking.GeofenceTransitionsIntentService;
import com.android.route_helper.LocationTracking.LocationConstants;
import com.android.route_helper.LocationTracking.LocationTracker;

public class HomeActivity extends AppCompatActivity {

    String logTag = "HomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //TODO add geofencing pendingintent and inter-activity geofence management

        Location defaultLocation = new Location("");
        defaultLocation.setLatitude(LocationConstants.DEFAULT_LOCATION_LATITUDE);
        defaultLocation.setLongitude(LocationConstants.DEFAULT_LOCATION_LONGITUDE);
        LocationTracker.addLocation(defaultLocation);


        //Creating PendingIntent to register geofences into
        Intent geofenceTriggersIntent = new Intent(this, GeofenceTransitionsIntentService.class);


        //When geofences need to be added, place below code into process
        if(LocationTracker.isConnected()) {
            Log.i(logTag, "Location Tracker connected -- adding geofences to intent");
            LocationTracker.registerAddedGeofencesToIntent(
                    PendingIntent.getService(this, 0, geofenceTriggersIntent, PendingIntent.FLAG_UPDATE_CURRENT));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        Intent geofenceTriggersIntent = new Intent(this, GeofenceTransitionsIntentService.class);
        if(LocationTracker.isConnected()) {
            Log.i(logTag, "Location Tracker connected -- adding geofences to intent");
            LocationTracker.registerAddedGeofencesToIntent(
                    PendingIntent.getService(this, 0, geofenceTriggersIntent, PendingIntent.FLAG_UPDATE_CURRENT));
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocationTracker.endTracker();
    }

    public void startRoute(View v) {
        MapsManager.loadMap(this, "Map");
    }



}
