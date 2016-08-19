package com.android.route_helper.Maps;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import com.android.route_helper.LocationConstants;
import com.android.route_helper.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;

/**
 * Created by Oscar_Local on 8/18/2016.
 */

public class SelectionMapActivity extends MapsActivity {

    private Geocoder geocoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        geocoder = new Geocoder(this);
    }

    /*
        INHERITED METHODS
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        super.onMapReady(googleMap);
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
                Location pickedLoc = new Location(LocationManager.GPS_PROVIDER);
                pickedLoc.setLongitude(marker.getPosition().longitude);
                pickedLoc.setLatitude(marker.getPosition().latitude);
                final Location loc = getPlaceEstimate(pickedLoc.getLatitude(), pickedLoc.getLongitude());
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(new LatLng(loc.getLatitude(), loc.getLongitude())).title("wut"));
                AlertDialog.Builder builder = new AlertDialog.Builder(SelectionMapActivity.this);
                builder.setMessage("Save Location?");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MapsManager.saveLocation(loc, hintFlag);
                        MapsManager.closeMap(SelectionMapActivity.this, null);
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
            }
        });
    }


    /*
        INNER METHODS
     */


    private Location getPlaceEstimate(double latitude, double longitude) {
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
