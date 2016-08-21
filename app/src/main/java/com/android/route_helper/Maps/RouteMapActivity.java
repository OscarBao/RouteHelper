package com.android.route_helper.Maps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;

import com.android.route_helper.CheckpointManaging.Checkpoint;
import com.android.route_helper.CheckpointManaging.Checkpoints;
import com.android.route_helper.LocationConstants;
import com.android.route_helper.StaticManagers.*;
import com.android.route_helper.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

/**
 * Created by Oscar_Local on 8/18/2016.
 */
public class RouteMapActivity extends MapsActivity{

    private BroadcastReceiver broadcastReceiver;
    private IntentFilter intentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        intentFilter = new IntentFilter("nextStep");
        broadcastReceiver = new GeofenceReceiver();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    /*
        INHERITED METHODS
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        super.onMapReady(googleMap);
        Checkpoints.reset();
        while(!Checkpoints.atEnd()) {
            Log.i("RouteMapActivity", Checkpoints.currentCheckpoint().getLocation().toString());
            Checkpoints.moveToNext();
        }
        Checkpoints.reset();
        if(hintFlag.equals(LocationConstants.FLAG_STARTROUTE)) {
            displayCheckpoints("");
        }
        else if(hintFlag.equals(LocationConstants.FLAG_PLANROUTE)) {
            displayAllCheckpoints();
        }
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                ToastHandler.
            }
        });
    }

    /*
        INNER CLASS
     */
    protected class GeofenceReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle b = intent.getExtras();
            String requestId = b.getString("requestId");
            displayCheckpoints(requestId);
            Log.i("GeofenceReceiver", "User has entered checkpoint location");
        }
    }




    /*
        INNER METHODS
     */
    protected void displayAllCheckpoints() {
        while(!Checkpoints.atEnd()) {
            Checkpoint currCheckpoint = Checkpoints.currentCheckpoint();
            LatLng currLoc = new LatLng(currCheckpoint.getLocation().getLatitude(), currCheckpoint.getLocation().getLongitude());
            mMap.addMarker(new MarkerOptions().position(currLoc).title("leg"));
            Checkpoints.moveToNext();


            if(Checkpoints.atEnd()) break;
            Checkpoint nextPoint = Checkpoints.currentCheckpoint();
            LatLng nextLoc = new LatLng(nextPoint.getLocation().getLatitude(), nextPoint.getLocation().getLongitude());
            mMap.addMarker(new MarkerOptions().position(nextLoc).title("leg"));
            mMap.addPolyline(new PolylineOptions().addAll(nextPoint.getPolyline()).width(5).color((nextPoint.getTypeCode() == 1) ? Color.RED : Color.BLUE).geodesic(true));
        }
    }

    protected void displayCheckpoints(String geofenceId) {
        //TODO: uncomment when no longer testing
        //mMap.clear();
        if(!geofenceId.equals(""))
            Checkpoints.pointToGeofence(geofenceId);
        Checkpoint beginCheckpoint = Checkpoints.currentCheckpoint();
        LatLng beginCheckpointLocation = new LatLng(beginCheckpoint.getLocation().getLatitude(), beginCheckpoint.getLocation().getLongitude());
        System.out.println(beginCheckpoint.toString());
        mMap.addMarker(new MarkerOptions().position(beginCheckpointLocation).title("Starting point" ));
        //System.out.println(c.getLocation().toString());
        Checkpoints.moveToNext();
        if(Checkpoints.atEnd())
            endRoute();
        else {
            Checkpoint endCheckpoint = Checkpoints.currentCheckpoint();
            LatLng endCheckpointLocation = new LatLng(endCheckpoint.getLocation().getLatitude(), endCheckpoint.getLocation().getLongitude());
            mMap.addMarker(new MarkerOptions().position(endCheckpointLocation).title("Ending point"));
            mMap.addPolyline(new PolylineOptions().addAll(endCheckpoint.getPolyline()).width(5)
                    .color((endCheckpoint.getTypeCode() == 1) ? Color.RED : Color.BLUE).geodesic(true));
        }
    }

    protected void endRoute() {
        //TODO
        MapsManager.closeMap(this, null);
        ToastHandler.displayMessage(this, "Finished route");
    }
}
