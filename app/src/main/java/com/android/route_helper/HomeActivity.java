package com.android.route_helper;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLPeerUnverifiedException;

public class HomeActivity extends AppCompatActivity {

    private EditText startLocation;
    private EditText destLocation;
    private static final String destinationTag = "destinationFlag";
    private static final String sourceTag = "sourceFlag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //TODO add geofencing pendingintent and inter-activity geofence management
        //you are blind bitch
        startLocation = (EditText) findViewById(R.id.activity_home_edittext_source);
        destLocation = (EditText) findViewById(R.id.activity_home_edittext_destination);
        startLocation.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    MapsManager.loadMap(HomeActivity.this, sourceTag);
                }
            }
        });
        destLocation.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    MapsManager.loadMap(HomeActivity.this, destinationTag);

                }
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        String defaultLocString = "Default loc";
        if(MapsManager.hasLocation(sourceTag)) {
            startLocation.setText(MapsManager.getLocation(sourceTag).getLatitude() + "," + MapsManager.getLocation(sourceTag).getLongitude());
        } else startLocation.setText(defaultLocString);

        if (MapsManager.hasLocation(destinationTag)) {
            destLocation.setText(MapsManager.getLocation(destinationTag).getLatitude() + "," + MapsManager.getLocation(destinationTag).getLongitude());
        } else destLocation.setText(defaultLocString);
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
                System.out.println(jsonObject.getJSONArray("geocoded_waypoints").getJSONObject(0));
            }

        /*
        try {
            URL url = new URL("http://www.android.com/");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            readStream(in);
        }*/
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

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
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
        conn.execute(directionURL);

        //System.out.println("Connected");

        //MapsManager.loadMap(this, "Map", false);
    }



}
