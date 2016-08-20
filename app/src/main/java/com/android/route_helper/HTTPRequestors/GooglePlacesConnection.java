package com.android.route_helper.HTTPRequestors;

import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.util.Log;

import com.android.route_helper.CheckpointManaging.Place;
import com.android.route_helper.LocationConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Oscar_Local on 8/20/2016.
 */

public class GooglePlacesConnection extends AsyncTask<String, Void, Void> {
    private static String logTag = "GooglePlacesConn";
    private String googlePlacesURL = "";
    protected static Location nearestLocation;
    protected static String nearestLocationName;
    protected static Place nearestPlace;

    public GooglePlacesConnection() {}

    public void createGooglePlacesURL(Location targetLoc) {
        String locationString = "location=" + targetLoc.getLatitude() + "," + targetLoc.getLongitude();
        googlePlacesURL = LocationConstants.GOOGLE_PLACES_BASE_URL +
                            locationString + "&" +
                            LocationConstants.GOOGLE_PLACES_RADIUS_STRING + "&" +
                            LocationConstants.GOOGLE_PLACES_API_KEY;
        Log.i(logTag, googlePlacesURL);

    }

    public String getNearestPlaceName () {
        if(nearestLocationName == null || nearestLocationName.equals("")) {
            return "Default Location Name";
        }
        return nearestLocationName;
    }

    public Place getNearestPlace() {
        if(nearestPlace == null) {
            return null;
        }
        return nearestPlace;
    }

    public Location getNearestPlaceLocation() {
        if(nearestLocation == null) {
            return new Location("");
        }
        return nearestLocation;
    }

    @Override
    protected Void doInBackground(String... params) {
        try {
            URL url = new URL(googlePlacesURL);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String inputLine;
            String jsonObj = "";

            while((inputLine = in.readLine()) != null) {
                jsonObj += inputLine;
            }
            in.close();

            JSONObject jsonObject = new JSONObject(jsonObj);

            JSONObject closestLocationJSON = jsonObject.getJSONArray("results").getJSONObject(1);
            Location closestLoc = new Location(LocationManager.GPS_PROVIDER);
            closestLoc.setLatitude(Double.parseDouble(closestLocationJSON
                                        .getJSONObject("geometry")
                                            .getJSONObject("location")
                                                .getString("lat")));
            closestLoc.setLongitude(Double.parseDouble(closestLocationJSON
                                        .getJSONObject("geometry")
                                            .getJSONObject("location")
                                                .getString("lng")));
            nearestLocation = closestLoc;
            nearestLocationName = closestLocationJSON
                                    .getString("name");
            nearestPlace = new Place(nearestLocation, nearestLocationName);
            Log.i(logTag, "Done");
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

    @Override
    protected void onPostExecute(Void aVoid) {

    }
}
