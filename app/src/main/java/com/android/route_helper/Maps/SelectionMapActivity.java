package com.android.route_helper.Maps;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import com.android.route_helper.CheckpointManaging.Place;
import com.android.route_helper.HTTPRequestors.GooglePlacesConnection;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Oscar_Local on 8/18/2016.
 */

public class SelectionMapActivity extends MapsActivity {

    private Geocoder geocoder;
    private Place nearestClickedPlace;

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
                getPlaceEstimate(pickedLoc.getLatitude(), pickedLoc.getLongitude());

                return true;
            }
        });
    }


    /*
        INNER METHODS
     */

    private void getPlaceEstimate(double latitude, double longitude) {
        Location currentTarget = new Location(LocationManager.GPS_PROVIDER);
        currentTarget.setLatitude(latitude);
        currentTarget.setLongitude(longitude);
        GetPlaceEstimateConnection gpc = new GetPlaceEstimateConnection();
        gpc.createGooglePlacesURL(currentTarget);
        gpc.execute();
    }

    /*
        INNER CLASSES
     */
    private class GetPlaceEstimateConnection extends GooglePlacesConnection {
        @Override
        protected void onPostExecute(Void aVoid) {
            nearestClickedPlace = getNearestPlace();
            final Location loc = getNearestPlace().getLocation();
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(new LatLng(loc.getLatitude(), loc.getLongitude())).title("wut"));
            AlertDialog.Builder builder = new AlertDialog.Builder(SelectionMapActivity.this);
            builder.setMessage("Save Location?");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    MapsManager.savePlace(getNearestPlace(), hintFlag);
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
        }
    }
}
