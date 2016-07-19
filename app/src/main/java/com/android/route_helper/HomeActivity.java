package com.android.route_helper;

import android.app.PendingIntent;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.android.route_helper.LocationTracking.GeofenceTransitionsIntentService;
import com.android.route_helper.LocationTracking.LocationConstants;
import com.android.route_helper.LocationTracking.LocationTracker;

public class HomeActivity extends AppCompatActivity {

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
        LocationTracker.registerAddedGeofencesToIntent(
                PendingIntent.getService(this, 0, geofenceTriggersIntent, PendingIntent.FLAG_UPDATE_CURRENT));
    }

    @Override
    public void onStart() {
        super.onStart();
        //start tracker
        LocationTracker.startTracker();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocationTracker.endTracker();
    }
}
