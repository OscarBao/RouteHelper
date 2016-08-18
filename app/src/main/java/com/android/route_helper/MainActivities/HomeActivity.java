package com.android.route_helper.MainActivities;


import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import com.android.route_helper.CheckpointManaging.Checkpoints;
import com.android.route_helper.HTTPRequestors.GoogleDirectionsConnection;
import com.android.route_helper.LocationConstants;
import com.android.route_helper.LocationTracking.GeofencesManager;
import com.android.route_helper.Maps.MapsManager;
import com.android.route_helper.R;
import com.android.route_helper.StaticManagers.ToastHandler;

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
    public void onStart() {
        super.onStart();
        geofencesManager.startLocationTracking();
    }

    private void bootGeofences() {
        for(Location loc : Checkpoints.locationsList()) {
            String name = "";
            try {
                Address address = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1).get(0);
                name = address.getAddressLine(0);
            }
            catch( IOException e) {
                e.printStackTrace();
            }
            Log.i(logTag, name);
        }
        geofencesManager.bootGeofencingWithLocations(Checkpoints.locationsList());
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
            String directionURL = "https://maps.googleapis.com/maps/api/directions/json?";
            String methodOfTransportation = "mode=transit";
            String origin = "origin=" + MapsManager.getLocation(LocationConstants.FLAG_SOURCE).getLatitude() + "," +
                    MapsManager.getLocation(LocationConstants.FLAG_SOURCE).getLongitude();
            String destination = "destination=" + MapsManager.getLocation(LocationConstants.FLAG_DESTINATION).getLatitude() + "," +
                    MapsManager.getLocation(LocationConstants.FLAG_DESTINATION).getLongitude();
            Log.i(logTag, "Here is your source: " + origin);
            Log.i(logTag, "Here is your destination: " + destination);
            if(origin.equals("origin=Default loc") || destination.equals("destination=Default loc")) {
                ToastHandler.displayMessage(getApplicationContext(), "Both origin and destination need a location");
                return;
            }
            String apiKey = "key=AIzaSyAwXZoA-POkRv12Stm4h_kgDdSkM-FKgn8";
            directionURL += origin + "&" + destination + "&" + methodOfTransportation + "&" + apiKey;
            System.out.println(directionURL);
            StartMapDirectionsConnection conn = new StartMapDirectionsConnection(LocationConstants.FLAG_PLANROUTE);
            conn.execute(directionURL);
        }

        private void startRoute() {
            String directionURL = "https://maps.googleapis.com/maps/api/directions/json?";
            String methodOfTransportation = "mode=transit";
            String origin = "origin=" + MapsManager.getLocation(LocationConstants.FLAG_SOURCE).getLatitude() + "," +
                    MapsManager.getLocation(LocationConstants.FLAG_SOURCE).getLongitude();
            String destination = "destination=" + MapsManager.getLocation(LocationConstants.FLAG_DESTINATION).getLatitude() + "," +
                    MapsManager.getLocation(LocationConstants.FLAG_DESTINATION).getLongitude();
            Log.i(logTag, "Here is your source: " + origin);
            Log.i(logTag, "Here is your destination: " + destination);
            if(origin.equals("origin=Default loc") || destination.equals("destination=Default loc")) {
                ToastHandler.displayMessage(getApplicationContext(), "Both origin and destination need a location");
                return;
            }
            String apiKey = "key=AIzaSyAwXZoA-POkRv12Stm4h_kgDdSkM-FKgn8";
            directionURL += origin + "&" + destination + "&" + methodOfTransportation + "&" + apiKey;
            System.out.println(directionURL);
            StartMapDirectionsConnection conn = new StartMapDirectionsConnection();
            conn.execute(directionURL);
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
            super(actionTag);
        }

        public StartMapDirectionsConnection() {
            super();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            bootGeofences();
            MapsManager.loadMap(HomeActivity.this, actionToken);
        }
    }


    protected void updateUI() {
        //Fill in the EditText fields to have friendly location data
        String source = LocationConstants.FLAG_SOURCE;
        String destination = LocationConstants.FLAG_DESTINATION;
        String displayText = "";
        if(MapsManager.hasLocation(source)) {
            try {
                displayText = getAddress(MapsManager.getLocation(source)).getAddressLine(0);
                displayText += ", " + getAddress(MapsManager.getLocation(source)).getAddressLine(1);
                startLocation.setText(displayText);
            } catch (NullPointerException e) {
                startLocation.setText(LocationConstants.DEFAULT_LOCATION_STRING);
            }
        }
        if(MapsManager.hasLocation(destination)) {
            try {
                displayText = getAddress(MapsManager.getLocation(destination)).getAddressLine(0);
                displayText += ", " + getAddress(MapsManager.getLocation(destination)).getAddressLine(1);
                destLocation.setText(displayText);
            } catch (NullPointerException e) {
                destLocation.setText(LocationConstants.DEFAULT_LOCATION_STRING);
            }
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
