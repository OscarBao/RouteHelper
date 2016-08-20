package com.android.route_helper.Maps;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.android.route_helper.LocationConstants;
import com.android.route_helper.R;

import com.android.route_helper.CheckpointManaging.*;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    protected GoogleMap mMap;
    public String hintFlag;
    private LatLng sf = new LatLng(37.7749, -122.4194);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        hintFlag = getIntent().getStringExtra("flag");

    }

    public static Class<? extends MapsActivity> getMapClass(String hintFlag) {
        if(hintFlag.equals(LocationConstants.FLAG_SOURCE) || hintFlag.equals(LocationConstants.FLAG_DESTINATION)) {
            return SelectionMapActivity.class;
        }
        else if(hintFlag.equals(LocationConstants.FLAG_STARTROUTE) ||
                hintFlag.equals(LocationConstants.FLAG_PLANROUTE)) {
            return RouteMapActivity.class;
        }
        else return RouteMapActivity.class;
    }


    private Handler toastHandler = new Handler();
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            toastHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Location not enabled", Toast.LENGTH_SHORT).show();
                }
            });
            // Show rationale and request permission.
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sf, 15));
    }


}
