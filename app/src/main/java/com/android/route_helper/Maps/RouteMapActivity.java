package com.android.route_helper.Maps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.android.route_helper.CheckpointManaging.Checkpoint;
import com.android.route_helper.CheckpointManaging.Checkpoints;
import com.android.route_helper.LocationConstants;
import com.android.route_helper.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

/**
 * Created by Oscar_Local on 8/18/2016.
 */
public class RouteMapActivity extends MapsActivity{

    private Handler toastHandler;
    private BroadcastReceiver broadcastReceiver;
    private IntentFilter intentFilter;
    private Marker nextPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        toastHandler = new Handler();

        intentFilter = new IntentFilter("nextStep");
        broadcastReceiver = new GeofenceReceiver();
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


    /*
        INHERITED METHODS
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        super.onMapReady(googleMap);

        Checkpoints.reset();
        if(hintFlag.equals(LocationConstants.FLAG_STARTROUTE)) {
            displayCheckpoints("");
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    showInstructions();
                    nextPoint.showInfoWindow();
                }
            });
        }
        else if(hintFlag.equals(LocationConstants.FLAG_PLANROUTE)) {
            displayAllCheckpoints();
        }
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
        }
    }




    /*
        INNER METHODS
     */
    protected String getCheckpointTitle(Checkpoint cp) {
        if(cp.getTypeCode() == 0) {
            return "Follow route by foot";
        }
        else if(cp.getTypeCode() == 1){
            return "Get on transit [" + cp.getTransitInfo() + "]";
        }
        else {
            return cp.getName();
        }
    }

    protected String getGoogleTravelTitle(Checkpoint cp) {
        if(cp.getTypeCode() == 0) {
            return cp.getGoogleTravelInfo();
        }
        if(cp.getTypeCode() == 1) {
            return cp.getGoogleTravelInfo() + " via [" + cp.getTransitInfo() + "]";
        }
        else return cp.getName();
    }

    protected void displayAllCheckpoints() {
        while(!Checkpoints.atEnd()) {
            Checkpoint currCheckpoint = Checkpoints.currentCheckpoint();
            LatLng currLoc = new LatLng(currCheckpoint.getLocation().getLatitude(), currCheckpoint.getLocation().getLongitude());
            mMap.addMarker(new MarkerOptions().position(currLoc).title(getGoogleTravelTitle(currCheckpoint)));
            Checkpoints.moveToNext();
            if(Checkpoints.atEnd()) break;
            Checkpoint nextPoint = Checkpoints.currentCheckpoint();
            LatLng nextLoc = new LatLng(nextPoint.getLocation().getLatitude(), nextPoint.getLocation().getLongitude());
            mMap.addMarker(new MarkerOptions().position(nextLoc).title(getGoogleTravelTitle(nextPoint)));
            mMap.addPolyline(new PolylineOptions().addAll(nextPoint.getPolyline()).width(5).color((nextPoint.getTypeCode() == 1) ? Color.RED : Color.BLUE).geodesic(true));
        }
    }

    protected void displayCheckpoints(String geofenceId) {
        mMap.clear();
        if(!geofenceId.equals(""))
            Checkpoints.pointToGeofence(geofenceId);
        Checkpoint beginCheckpoint = Checkpoints.currentCheckpoint();
        LatLng beginCheckpointLocation = new LatLng(beginCheckpoint.getLocation().getLatitude(), beginCheckpoint.getLocation().getLongitude());
        mMap.addMarker(new MarkerOptions().position(beginCheckpointLocation).title(getGoogleTravelTitle(beginCheckpoint)));
        //System.out.println(c.getLocation().toString());
        Checkpoints.moveToNext();
        if(Checkpoints.atEnd()) {
            endRoute();
        }
        else {
            Checkpoint endCheckpoint = Checkpoints.currentCheckpoint();
            LatLng endCheckpointLocation = new LatLng(endCheckpoint.getLocation().getLatitude(), endCheckpoint.getLocation().getLongitude());
            nextPoint = mMap.addMarker(new MarkerOptions().position(endCheckpointLocation).title(getGoogleTravelTitle(endCheckpoint)));
            nextPoint.showInfoWindow();
            mMap.addPolyline(new PolylineOptions().addAll(endCheckpoint.getPolyline()).width(5)
                    .color((endCheckpoint.getTypeCode() == 1) ? Color.RED : Color.BLUE).geodesic(true));
        }
    }

    protected void endRoute() {
        MapsManager.clearFlags();
        MapsManager.closeMap(this, null);
        toastHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(RouteMapActivity.this, "Destination reached!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showInstructions() {
        toastHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),getCheckpointTitle(Checkpoints.currentCheckpoint()),Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(),Checkpoints.currentCheckpoint().getGoogleTravelInfo(),Toast.LENGTH_LONG).show();
            }
        });
        /*
        toastHandler.post(new Runnable() {
            @Override
            public void run() {
            }
        });
        */
    }
}
