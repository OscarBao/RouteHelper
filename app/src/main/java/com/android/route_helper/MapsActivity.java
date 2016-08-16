package com.android.route_helper;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.android.route_helper.CheckpointManaging.*;

import com.android.route_helper.StaticManagers.ToastHandler;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String locationFlag;
    private LatLng sf = new LatLng(37.7749, -122.4194);
    private BroadcastReceiver broadcastReceiver;
    private IntentFilter intentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
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
                final Location loc = new Location(LocationManager.GPS_PROVIDER);
                loc.setLongitude(marker.getPosition().longitude);
                loc.setLatitude(marker.getPosition().latitude);
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
                return true;
                /*
                showToast("Saving this marker to source");
                Location loc = new Location(LocationManager.GPS_PROVIDER);
                loc.setLongitude(marker.getPosition().longitude);
                loc.setLongitude(marker.getPosition().latitude);
                MapsManager.saveLocation(loc, true);
                System.out.println(MapsManager.getLocation(true).toString());
                return true;
                */
            }
        });
//        if(locationFlag.equals("startRoute")) {
//            displayCheckpoints();
//        }


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


    private void displayCheckpoints(String geofenceId) {
        Checkpoints.pointToGeofence(geofenceId);
        Checkpoint beginCheckpoint = Checkpoints.currentCheckpoint();
        LatLng beginCheckpointLocation = new LatLng(beginCheckpoint.getLocation().getLatitude(), beginCheckpoint.getLocation().getLongitude());
        System.out.println(beginCheckpoint);
        mMap.addMarker(new MarkerOptions().position(beginCheckpointLocation).title("Checkpoint something" ));
        //System.out.println(c.getLocation().toString());
        Checkpoints.moveToNext();
        if(Checkpoints.atEnd())
            ToastHandler.displayMessage(this, "At end of list");
        else {
            Checkpoint endCheckpoint = Checkpoints.currentCheckpoint();
            LatLng endCheckpointLocation = new LatLng(endCheckpoint.getLocation().getLatitude(), endCheckpoint.getLocation().getLongitude());
            mMap.addMarker(new MarkerOptions().position(endCheckpointLocation).title("Checkpoint something else"));
            mMap.addPolyline(new PolylineOptions().add(beginCheckpointLocation).add(endCheckpointLocation).width(5)
                    .color((beginCheckpoint.getTypeCode() == 1) ? Color.RED : Color.BLUE));
        }
    }

    private void toast(String message) {
        ToastHandler.displayMessage(this, message);
    }

}
