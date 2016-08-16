package com.android.route_helper;


import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
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
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

import com.android.route_helper.CheckpointManaging.Checkpoint;
import com.android.route_helper.CheckpointManaging.Checkpoints;
import com.android.route_helper.LocationTracking.GeofencesManager;
import com.android.route_helper.LocationTracking.LocationConstants;
import com.android.route_helper.StaticManagers.ToastHandler;

public class HomeActivity extends AppCompatActivity {


    String logTag = "HomeActivity";

    private EditText startLocation;
    private EditText destLocation;

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
        startLocation.setOnFocusChangeListener(editTextListener);
        startLocation.setOnClickListener(editTextListener);
        destLocation.setOnFocusChangeListener(editTextListener);
        destLocation.setOnClickListener(editTextListener);

        defaultLocationsList = new ArrayList<>();
        Location firstDefaultLoc = new Location("");
        firstDefaultLoc.setLatitude(LocationConstants.DEFAULT_LOCATION_LATITUDE);
        firstDefaultLoc.setLongitude(LocationConstants.DEFAULT_LOCATION_LONGITUDE);
        Location secondDefaultLoc = new Location("");
        secondDefaultLoc.setLatitude(9.046496d);
        secondDefaultLoc.setLongitude(21.467285);
        defaultLocationsList.add(firstDefaultLoc);
        defaultLocationsList.add(secondDefaultLoc);


    }

    @Override
    public void onStart() {
        super.onStart();
        //geofencesManager.bootGeofencingWithLocations(defaultLocationsList);
        geofencesManager.startLocationTracking();
    }


    @Override
    public void onResume() {
        super.onResume();
        updateUI();
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

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void startRoute(View v) {
        String directionURL = "https://maps.googleapis.com/maps/api/directions/json?";
        String methodOfTransportation = "mode=transit";
        String origin = "origin=" + MapsManager.getLocation(LocationConstants.FLAG_SOURCE).getLatitude() + "," +
                MapsManager.getLocation(LocationConstants.FLAG_SOURCE).getLongitude();
        String destination = "destination=" + MapsManager.getLocation(LocationConstants.FLAG_DESTINATION).getLatitude() + "," +
                                            MapsManager.getLocation(LocationConstants.FLAG_DESTINATION).getLongitude();
        Log.i(logTag, "Here is your source: " + origin);
        Log.i(logTag, "Here is your destination: " + destination);
        if(origin.equals("origin=Default loc") || destination.equals("destination=Default loc")) {
            ToastHandler.displayMessage(this, "Both origin and destination need a location");
            return;
        }
        String apiKey = "key=AIzaSyAwXZoA-POkRv12Stm4h_kgDdSkM-FKgn8";
        directionURL += origin + "&" + destination + "&" + methodOfTransportation + "&" + apiKey;
        System.out.println(directionURL);
        Connection conn = new Connection();
        conn.execute(directionURL);
        //conn.execute("https://maps.googleapis.com/maps/api/directions/json?origin=place_id:ChIJY-C3tIh-j4AR0BLzcg4YzlQ&destination=place_id:Ei4yLTk4IFRydW1idWxsIFN0LCBTYW4gRnJhbmNpc2NvLCBDQSA5NDExMiwgVVNB&mode=transit&key=AIzaSyAwXZoA-POkRv12Stm4h_kgDdSkM-FKgn8");

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

    private class Connection extends AsyncTask<String,Void,Void> {
        @Override
        protected Void doInBackground(String... params) {
            try {
                Checkpoints.clear();
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
                    //checkpoint for home
                    if(i == 0) {
                        createCheckpoint(curr, i, true);
                    }
                    //usually for walkling
                    if(curr.has("steps")) {
                        for(int j = 0; j < curr.getJSONArray("steps").length(); j++) {
                            JSONObject currWalkingStep = curr.getJSONArray("steps").getJSONObject(j);
                            if(currWalkingStep.getJSONObject("distance").getInt("value") > 45)
                                createCheckpoint(currWalkingStep, j, false);

                        }
                    }
                    else {
                        if(curr.getJSONObject("distance").getInt("value") > 45)
                            createCheckpoint(curr, i, false);

                    }
                }
                /*
                while(!Checkpoints.atEnd()) {
                    Checkpoint c = Checkpoints.currentCheckpoint();
                    System.out.println(c.getLocation().toString());
                    System.out.println(c.getTypeCode());
                    Checkpoints.moveToNext();
                }*/
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
            bootGeofences();
            MapsManager.loadMap(HomeActivity.this, "startRoute");
        }
    }




    /*
        PRIVATE METHODS
     */

    private void createCheckpoint(JSONObject jsonObject, int index, boolean isStartLocation) throws JSONException{
        String lat = jsonObject.getJSONObject((isStartLocation) ? "start_location" : "end_location").getString("lat");
        String lng = jsonObject.getJSONObject((isStartLocation) ? "start_location" : "end_location").getString("lng");
        int typeCode = (jsonObject.getString("travel_mode").equals("TRANSIT")) ? 1:0;
        String address = "Checkpoint " + index;
        Location l = new Location(LocationManager.GPS_PROVIDER);
        l.setLatitude(Double.parseDouble(lat));
        l.setLongitude(Double.parseDouble(lng));
        Checkpoint c = new Checkpoint(l,address,typeCode);
        Checkpoints.add(c);
    }


    private void updateUI() {
        //Fill in the EditText fields to have friendly location data
        String source = LocationConstants.FLAG_SOURCE;
        String destination = LocationConstants.FLAG_DESTINATION;
        String displayText = "";
        if(MapsManager.hasLocation(source)) {
            try {
                displayText = getAddress(MapsManager.getLocation(source)).getAddressLine(0);
                startLocation.setText(displayText);
            } catch (NullPointerException e) {
                startLocation.setText(LocationConstants.DEFAULT_LOCATION_STRING);
            }
        }
        if(MapsManager.hasLocation(destination)) {
            try {
                displayText = getAddress(MapsManager.getLocation(destination)).getAddressLine(0);
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
        }
        return null;
    }
}
