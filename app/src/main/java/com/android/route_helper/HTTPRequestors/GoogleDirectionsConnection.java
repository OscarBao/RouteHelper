package com.android.route_helper.HTTPRequestors;

import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;

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
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Oscar_Local on 8/18/2016.
 */
public class GoogleDirectionsConnection extends AsyncTask<String,Void,Void> {
    public String actionToken = "";

    public GoogleDirectionsConnection() {
        actionToken = LocationConstants.FLAG_STARTROUTE;
    }

    public GoogleDirectionsConnection(String actionToken) {
        this.actionToken = actionToken;
    }

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
    }

    private void createCheckpoint(JSONObject jsonObject, int index, boolean isStartLocation) throws JSONException{
        String lat = jsonObject.getJSONObject((isStartLocation) ? "start_location": "end_location").getString("lat");
        String lng = jsonObject.getJSONObject((isStartLocation) ? "start_location": "end_location").getString("lng");
        String instructions = jsonObject.getString("html_instructions");
        List<LatLng> encodedPolylines = PolyUtil.decode(jsonObject.getJSONObject("polyline").getString("points"));
        int typeCode = (jsonObject.getString("travel_mode").equals("TRANSIT")) ? 1:0;
        String address = "Checkpoint " + index;
        Location l = new Location(LocationManager.GPS_PROVIDER);
        l.setLatitude(Double.parseDouble(lat));
        l.setLongitude(Double.parseDouble(lng));
        Checkpoint c = new Checkpoint(l,address,typeCode,encodedPolylines,instructions);
        Checkpoints.add(c);
    }
}


