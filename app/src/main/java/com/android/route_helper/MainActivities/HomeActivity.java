package com.android.route_helper.MainActivities;


import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.android.route_helper.CheckpointManaging.Checkpoint;
import com.android.route_helper.CheckpointManaging.Checkpoints;
import com.android.route_helper.HTTPRequestors.GoogleDirectionsConnection;
import com.android.route_helper.HTTPRequestors.GooglePlacesConnection;
import com.android.route_helper.LocationConstants;
import com.android.route_helper.LocationTracking.GeofencesManager;
import com.android.route_helper.Maps.MapsManager;
import com.android.route_helper.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;

public class HomeActivity extends RefreshActivity {


    String logTag = "HomeActivity";

    private EditText startLocation;
    private EditText destLocation;
    private Button startRouteButton;
    private Button planRouteButton;

    Geocoder geocoder;

    ArrayList<Location> defaultLocationsList;

    GeofencesManager geofencesManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        geocoder = new Geocoder(this);
        geofencesManager = new GeofencesManager(this);

        //Set up the interactive edittext fields
        EditTextListener editTextListener = new EditTextListener();
        startLocation = (EditText) findViewById(R.id.activity_home_edittext_source);
        destLocation = (EditText) findViewById(R.id.activity_home_edittext_destination);
        startRouteButton = (Button) findViewById(R.id.activity_home_button_startroute);
        planRouteButton = (Button) findViewById(R.id.activity_home_button_planroute);

        startLocation.setOnFocusChangeListener(editTextListener);
        startLocation.setOnClickListener(editTextListener);
        destLocation.setOnFocusChangeListener(editTextListener);
        destLocation.setOnClickListener(editTextListener);
        startRouteButton.setOnClickListener(new RouteButtonListener());
        planRouteButton.setOnClickListener(new RouteButtonListener());



    }

    @Override
    public void onResume() {
        super.onResume();
        geofencesManager.shutdownGeofences();
        geofencesManager.endLocationTracking();
        Checkpoints.clear();
    }

    @Override
    public void onStart() {
        super.onStart();
        geofencesManager.startLocationTracking();
    }



    /*
        PRIVATE INNER CLASSES
     */
    private class RouteButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.activity_home_button_startroute) {
                startRoute();
            }
            else if(v.getId() == R.id.activity_home_button_planroute) {
                planRoute();
            }
        }

        private void planRoute() {
            Location origin = MapsManager.getPlace(LocationConstants.FLAG_SOURCE).getLocation();
            Location destination = MapsManager.getPlace(LocationConstants.FLAG_DESTINATION).getLocation();
            StartMapDirectionsConnection conn = new StartMapDirectionsConnection(LocationConstants.FLAG_PLANROUTE);
            conn.createGoogleDirectionsURL(origin, destination);
            conn.execute();
        }

        private void startRoute() {
            Location origin = MapsManager.getPlace(LocationConstants.FLAG_SOURCE).getLocation();
            Location destination = MapsManager.getPlace(LocationConstants.FLAG_DESTINATION).getLocation();
            StartMapDirectionsConnection conn = new StartMapDirectionsConnection(LocationConstants.FLAG_STARTROUTE);
            conn.createGoogleDirectionsURL(origin, destination);
            conn.execute();
        }
    }

    private class EditTextListener implements View.OnFocusChangeListener, View.OnClickListener {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if(v.getId() == R.id.activity_home_edittext_destination) {
                if(hasFocus) loadMap(LocationConstants.FLAG_DESTINATION);
            } else if (v.getId() == R.id.activity_home_edittext_source) {
                if(hasFocus) loadMap(LocationConstants.FLAG_SOURCE);
            }
        }

        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.activity_home_edittext_destination:
                    loadMap(LocationConstants.FLAG_DESTINATION);
                    break;
                case R.id.activity_home_edittext_source:
                    loadMap(LocationConstants.FLAG_SOURCE);
                    break;
            }
        }


        private void loadMap(String tag) {
            MapsManager.loadMap(HomeActivity.this, tag);
        }
    }


    /*
        PRIVATE METHODS
     */
    private class StartMapDirectionsConnection extends GoogleDirectionsConnection {    // private void
        public StartMapDirectionsConnection(String actionTag) {
            super(getApplicationContext(), actionTag);
        }

        public StartMapDirectionsConnection() {
            super(getApplicationContext());
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            bootGeofences();
            MapsManager.loadMap(HomeActivity.this, actionToken);
        }
    }




    /*
        PRIVATE METHODS
     */

    private void bootGeofences() {
        geofencesManager.bootGeofencingWithLocations(Checkpoints.locationsList());
    }

    protected void updateUI() {
        //Fill in the EditText fields to have friendly location data
        String source = LocationConstants.FLAG_SOURCE;
        String destination = LocationConstants.FLAG_DESTINATION;
        String displayText = "";
        if(MapsManager.hasPlace(source)) {
            try {
                displayText = MapsManager.getPlace(source).getName();
                displayText += ", " + getAddress(MapsManager.getPlace(source).getLocation()).getAddressLine(1);
                startLocation.setText(displayText);
            } catch (NullPointerException e) {
                startLocation.setText(LocationConstants.DEFAULT_LOCATION_STRING);
            }
        }
        else {
            startLocation.setText(LocationConstants.DEFAULT_LOCATION_STRING);
        }
        if(MapsManager.hasPlace(destination)) {
            try {
                displayText = MapsManager.getPlace(destination).getName();
                displayText += ", " + getAddress(MapsManager.getPlace(destination).getLocation()).getAddressLine(1);
                destLocation.setText(displayText);
            } catch (NullPointerException e) {
                destLocation.setText(LocationConstants.DEFAULT_LOCATION_STRING);
            }
        }
        else {
            destLocation.setText(LocationConstants.DEFAULT_LOCATION_STRING);
        }

    }

    private Address getAddress(Location loc) {
        try {
            return geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1).get(0);
        }
        catch (IOException e) {
            e.printStackTrace();
            return new Address(Locale.US);
        }
    }
}
