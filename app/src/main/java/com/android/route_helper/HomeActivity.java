package com.android.route_helper;


import android.app.PendingIntent;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import com.android.route_helper.CheckpointManaging.Checkpoint;
import com.android.route_helper.CheckpointManaging.Checkpoints;
import com.android.route_helper.LocationTracking.GeofenceTransitionsIntentService;
import com.android.route_helper.LocationTracking.LocationConstants;
import com.android.route_helper.LocationTracking.LocationTracker;

public class HomeActivity extends AppCompatActivity {


    String logTag = "HomeActivity";

    private EditText startLocation;
    private EditText destLocation;
    String defaultLocString = "Default loc";
    private static final String destinationTag = "destinationFlag";
    private static final String sourceTag = "sourceFlag";

    private EditTextListener editTextListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("at on create");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        editTextListener = new EditTextListener();
        startLocation = (EditText) findViewById(R.id.activity_home_edittext_source);
        destLocation = (EditText) findViewById(R.id.activity_home_edittext_destination);
        startLocation.setOnFocusChangeListener(editTextListener);
        startLocation.setOnClickListener(editTextListener);
        destLocation.setOnFocusChangeListener(editTextListener);
        destLocation.setOnClickListener(editTextListener);

        //TODO add geofencing pendingintent and inter-activity geofence management

        Location defaultLocation = new Location("");
        defaultLocation.setLatitude(LocationConstants.DEFAULT_LOCATION_LATITUDE);
        defaultLocation.setLongitude(LocationConstants.DEFAULT_LOCATION_LONGITUDE);
        LocationTracker.addLocation(defaultLocation);


        //Creating PendingIntent to register geofences into
        Intent geofenceTriggersIntent = new Intent(this, GeofenceTransitionsIntentService.class);


        //When geofences need to be added, place below code into process
        if(LocationTracker.isConnected()) {
            Log.i(logTag, "Location Tracker connected -- adding geofences to intent");
            LocationTracker.registerAddedGeofencesToIntent(
                    PendingIntent.getService(this, 0, geofenceTriggersIntent, PendingIntent.FLAG_UPDATE_CURRENT));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("at on resume");
        if(MapsManager.hasLocation(sourceTag)) {
            startLocation.setText(MapsManager.getLocation(sourceTag).getLatitude() + "," + MapsManager.getLocation(sourceTag).getLongitude());
        } else startLocation.setText(defaultLocString);

        if (MapsManager.hasLocation(destinationTag)) {
            destLocation.setText(MapsManager.getLocation(destinationTag).getLatitude() + "," + MapsManager.getLocation(destinationTag).getLongitude());
        } else destLocation.setText(defaultLocString);

        Intent geofenceTriggersIntent = new Intent(this, GeofenceTransitionsIntentService.class);
        if(LocationTracker.isConnected()) {
            Log.i(logTag, "Location Tracker connected -- adding geofences to intent");
            LocationTracker.registerAddedGeofencesToIntent(
                    PendingIntent.getService(this, 0, geofenceTriggersIntent, PendingIntent.FLAG_UPDATE_CURRENT));
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocationTracker.endTracker();

    }

    public void startRoute(View v) {
        String directionURL = "https://maps.googleapis.com/maps/api/directions/json?";
        String methodOfTransportation = "mode=transit";
        String origin = "origin=" + startLocation.getText().toString();
        String destination = "destination=" + destLocation.getText().toString();
        if(origin.equals("origin=Default loc") || destination.equals("destination=Default loc")) {
            Toast.makeText(this, "Both origin and destination need a location", Toast.LENGTH_SHORT).show();
            return;
        }
        String apiKey = "key=AIzaSyAwXZoA-POkRv12Stm4h_kgDdSkM-FKgn8";
        directionURL += origin + "&" + destination + "&" + methodOfTransportation + "&" + apiKey;
        Connection conn = new Connection();
        conn.execute("https://maps.googleapis.com/maps/api/directions/json?origin=place_id:ChIJY-C3tIh-j4AR0BLzcg4YzlQ&destination=place_id:Ei4yLTk4IFRydW1idWxsIFN0LCBTYW4gRnJhbmNpc2NvLCBDQSA5NDExMiwgVVNB&mode=transit&key=AIzaSyAwXZoA-POkRv12Stm4h_kgDdSkM-FKgn8");

        //System.out.println("Connected");

        //MapsManager.loadMap(this, "Map", false);
    }


    /*
        PRIVATE INNER CLASSES
     */

    private class EditTextListener implements View.OnFocusChangeListener, View.OnClickListener {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if(v.getId() == R.id.activity_home_edittext_destination) {
                if(hasFocus) loadMap(destinationTag);
            } else if (v.getId() == R.id.activity_home_edittext_source) {
                if(hasFocus) loadMap(sourceTag);
            }
        }

        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.activity_home_edittext_destination:
                    loadMap(destinationTag);
                    break;
                case R.id.activity_home_edittext_source:
                    loadMap(sourceTag);
                    break;
            }
        }


        private void loadMap(String tag) {
            MapsManager.loadMap(HomeActivity.this, tag);
        }
    }

    private class Connection extends AsyncTask<String,Void,Void> {
        @Override
        protected Void doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        conn.getInputStream()));
                String inputLine;
                String jsonObj = "";
                while ((inputLine = in.readLine()) != null)
                    jsonObj += inputLine;
                in.close();
                JSONObject jsonObject = new JSONObject(jsonObj);
                //System.out.println(jsonObject);
                JSONArray stepsOfRoute = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONArray("steps");
                for(int i = 0; i < stepsOfRoute.length(); i++) {
                    JSONObject curr = stepsOfRoute.getJSONObject(i);

                    //usually for walkling
                    if(curr.has("steps")) {
                        for(int j = 0; j < curr.getJSONArray("steps").length(); j++) {
                            JSONObject currWalkingStep = curr.getJSONArray("steps").getJSONObject(j);
                            String lat = currWalkingStep.getJSONObject("end_location").getString("lat");
                            String lng = currWalkingStep.getJSONObject("end_location").getString("lng");
                            int typeCode = (currWalkingStep.getString("travel_mode").equals("TRANSIT")) ? 1:0;
                            String address = "Checkpoint " + j;
                            Location l = new Location(LocationManager.GPS_PROVIDER);
                            l.setLatitude(Double.parseDouble(lat));
                            l.setLongitude(Double.parseDouble(lng));
                            Checkpoint c = new Checkpoint(l,address,typeCode);
                            Checkpoints.add(c);
                        }
                    }
                    else {
                        String lat = curr.getJSONObject("end_location").getString("lat");
                        String lng = curr.getJSONObject("end_location").getString("lng");
                        int typeCode = (curr.getString("travel_mode").equals("TRANSIT")) ? 1:0;
                        String address = "Checkpoint " + i;
                        Location l = new Location(LocationManager.GPS_PROVIDER);
                        l.setLatitude(Double.parseDouble(lat));
                        l.setLongitude(Double.parseDouble(lng));
                        Checkpoint c = new Checkpoint(l,address,typeCode);
                        Checkpoints.add(c);
                    }
                }
                while(!Checkpoints.atEnd()) {
                    Checkpoint c = Checkpoints.currentCheckpoint();
                    System.out.println(c.getLocation().toString());
                    System.out.println(c.getTypeCode());
                    Checkpoints.moveToNext();
                }
            }


            catch(MalformedURLException e) {
                e.printStackTrace();
            }
            catch(IOException e) {
                e.printStackTrace();
            }
            catch(JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

       // private void
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }



    /*
        PRIVATE METHODS
     */




}
