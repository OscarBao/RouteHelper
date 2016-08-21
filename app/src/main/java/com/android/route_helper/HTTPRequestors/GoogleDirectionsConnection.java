package com.android.route_helper.HTTPRequestors;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.util.Log;

import com.android.route_helper.CheckpointManaging.Checkpoint;
import com.android.route_helper.CheckpointManaging.Checkpoints;
import com.android.route_helper.LocationConstants;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Oscar_Local on 8/18/2016.
 */
public class GoogleDirectionsConnection extends AsyncTask<String,Void,Void> {
    private static String logTag = "GoogleDirectionsConn";
    public String actionToken = "";
    private String googleDirectionsURL = "";
    private Geocoder geocoder;
    private Context appContext;

    public GoogleDirectionsConnection(Context context) {
        actionToken = LocationConstants.FLAG_STARTROUTE;
        appContext = context;
        geocoder = new Geocoder(appContext, Locale.US);
    }

    public GoogleDirectionsConnection(Context context, String actionToken) {
        this.actionToken = actionToken;
        appContext = context;
        geocoder = new Geocoder(appContext, Locale.US);
    }

    public void createGoogleDirectionsURL(Location origin, Location destination) {
        String originString = "origin=" + origin.getLatitude() + "," + origin.getLongitude();
        String destinationString = "destination=" + destination.getLatitude() + "," + destination.getLongitude();
        googleDirectionsURL = LocationConstants.GOOGLE_DIRECTIONS_BASE_URL +
                                originString + "&" +
                                destinationString + "&" +
                                LocationConstants.GOOGLE_DIRECTIONS_TRANSIT_MODE + "&" +
                                LocationConstants.GOOGLE_DIRECTIONS_API_KEY;
        Log.i(logTag, googleDirectionsURL);
    }


    @Override
    protected Void doInBackground(String... params) {
        if(googleDirectionsURL == null || googleDirectionsURL.equals("")) {
            return null;
        }
        try {
            Checkpoints.clear();
            URL url = new URL(googleDirectionsURL);
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
                if(curr.getJSONObject("distance").getInt("value") > 45)
                    createCheckpoint(curr, i, false);
                //usually for walking
//                    if(curr.has("steps")) {
//                        List<LatLng> walkingPolylines = PolyUtil.decode(curr.getJSONObject("polyline").getString("points"));
//                        for(int j = 0; j < curr.getJSONArray("steps").length(); j++) {
//                            JSONObject currWalkingStep = curr.getJSONArray("steps").getJSONObject(j);
//                            if(currWalkingStep.getJSONObject("distance").getInt("value") > 45) {
//                                if(j == (curr.getJSONArray("steps").length() - 1))
//                                    createCheckpoint(currWalkingStep, j, false, walkingPolylines);
//                            }
//                            else
//                                Log.i("HomeActivity", "Skipped a checkpoint");
//
//                        }
//                    }
//                    else {

                //}
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
        if(googleDirectionsURL == null || googleDirectionsURL.equals("")) {
            return;
        }
    }

    private void createCheckpoint(JSONObject jsonObject, int index, boolean isStartLocation) throws JSONException{
        String lat = jsonObject.getJSONObject((isStartLocation) ? "start_location": "end_location").getString("lat");
        String lng = jsonObject.getJSONObject((isStartLocation) ? "start_location": "end_location").getString("lng");
        List<LatLng> encodedPolylines = PolyUtil.decode(jsonObject.getJSONObject("polyline").getString("points"));
        int typeCode = (jsonObject.getString("travel_mode").equals("TRANSIT")) ? 1:0;
        if(isStartLocation) typeCode = -1;
        Location l = new Location(LocationManager.GPS_PROVIDER);
        l.setLatitude(Double.parseDouble(lat));
        l.setLongitude(Double.parseDouble(lng));
        String address = getAddress(l).getAddressLine(0);
        Checkpoint c = new Checkpoint(l,address,typeCode,encodedPolylines);
        Checkpoints.add(c);
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


