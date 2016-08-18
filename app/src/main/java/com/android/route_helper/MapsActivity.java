package com.android.route_helper;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import com.google.maps.android.PolyUtil;

import com.android.route_helper.CheckpointManaging.*;

import com.android.route_helper.LocationTracking.LocationConstants;
import com.android.route_helper.StaticManagers.ToastHandler;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private Geocoder geocoder;
    private GoogleMap mMap;
    private String locationFlag;
    private LatLng sf = new LatLng(37.7749, -122.4194);
    private BroadcastReceiver broadcastReceiver;
    private IntentFilter intentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        geocoder = new Geocoder(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationFlag = getIntent().getStringExtra("flag");
    }

    @Override
    protected void onStart() {
        super.onStart();

        intentFilter = new IntentFilter("nextStep");
        broadcastReceiver = new GeofenceReceiver();
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            ToastHandler.displayMessage(this, "Sum ting wong");
            // Show rationale and request permission.
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sf, 15));
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(latLng).title("wut"));
            }
        });


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if(locationFlag.equals("startRoute")) {
                    marker.showInfoWindow();
                }
                else {
                    Location pickedLoc = new Location(LocationManager.GPS_PROVIDER);
                    pickedLoc.setLongitude(marker.getPosition().longitude);
                    pickedLoc.setLatitude(marker.getPosition().latitude);
                    final Location loc = getPlaceEstimate(pickedLoc.getLatitude(), pickedLoc.getLongitude());
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(new LatLng(loc.getLatitude(), loc.getLongitude())).title("wut"));
                    AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                    builder.setMessage("Save Location?");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MapsManager.saveLocation(loc, locationFlag);
                            MapsManager.closeMap(MapsActivity.this, null);
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }
                return true;
            }
        });
        Checkpoints.reset();
        if(locationFlag.equals("startRoute")) {
                displayCheckpoints("");
        }
        else if(locationFlag.equals("planRoute")) {
            mMap.setContentDescription("Route planning in progress");
            displayAllCheckpoints();
        }


        // Add a marker in Sydney and move the camera
        /*
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        */
    }

    private class GeofenceReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle b = intent.getExtras();
            String requestId = b.getString("requestId");
            displayCheckpoints(requestId);
            Log.i("GeofenceReceiver", "Received an event");
        }
    }

    private void displayAllCheckpoints() {
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

    private void displayCheckpoints(String geofenceId) {
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
            ToastHandler.displayMessage(this, "At end of list");
        else {
            Checkpoint endCheckpoint = Checkpoints.currentCheckpoint();
            LatLng endCheckpointLocation = new LatLng(endCheckpoint.getLocation().getLatitude(), endCheckpoint.getLocation().getLongitude());
            mMap.addMarker(new MarkerOptions().position(endCheckpointLocation).title("Ending point"));
            mMap.addPolyline(new PolylineOptions().addAll(endCheckpoint.getPolyline()).width(5)
                    .color((endCheckpoint.getTypeCode() == 1) ? Color.RED : Color.BLUE).geodesic(true));
        }
    }

    private Location getPlaceEstimate(double latitude, double longitude) {
        //TODO check if proper location received
        try {
            Address closestAddress = geocoder.getFromLocation(latitude, longitude, 1).get(0);
            Location closestLoc = new Location(LocationManager.GPS_PROVIDER);
            closestLoc.setLatitude(closestAddress.getLatitude());
            closestLoc.setLongitude(closestAddress.getLongitude());
            return closestLoc;
        }
        catch (IOException e) {
            e.printStackTrace();
            Location defaultLoc = new Location(LocationManager.GPS_PROVIDER);
            defaultLoc.setLatitude(latitude);
            defaultLoc.setLongitude(longitude);
            return defaultLoc;
        }
    }

}
