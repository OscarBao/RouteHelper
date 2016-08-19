package com.android.route_helper.CheckpointManaging;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.Geofence;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by obao_Lenovo on 7/8/2016.
 */
public class Checkpoints implements Iterable<Checkpoint> {
    /*
        Variables
     */
    static private ArrayList<Checkpoint> checkpointsList;
    static int currentCheckpointIndex;
    /*
        Public methods
     */
    public Checkpoints() {
        checkpointsList = new ArrayList<>();
        currentCheckpointIndex = 0;
    }

    public Iterator<Checkpoint> iterator() {return checkpointsList.iterator();}
    public static void clear() {
        checkpointsList = new ArrayList<>();
        currentCheckpointIndex = 0;}
    public static void add(Checkpoint cp) {checkpointsList.add(cp);}
    public static void remove(Checkpoint cp) {checkpointsList.remove(cp);}
    public static void reset() {currentCheckpointIndex = 0;}
    public static Checkpoint nextCheckpoint() {return checkpointsList.get(currentCheckpointIndex + 1);}
    public static void moveToNext() {currentCheckpointIndex++;}
    public static Checkpoint currentCheckpoint() {return checkpointsList.get(currentCheckpointIndex);}
    public static boolean atEnd() {
        if(currentCheckpointIndex >= checkpointsList.size())
                Log.i("CheckpointsList", "at end of list");
        return currentCheckpointIndex >= checkpointsList.size();
    }

    public static ArrayList<Location> locationsList() {
        ArrayList<Location> spit = new ArrayList<>();
        for(Checkpoint cp : checkpointsList) {
            spit.add(cp.getLocation());
        }
        return spit;
    }

    public static void pointToGeofence(String geofenceRequestId) {
        if(isInteger(geofenceRequestId)) {
            if(Integer.parseInt(geofenceRequestId) < checkpointsList.size()) {
                currentCheckpointIndex = Integer.parseInt(geofenceRequestId);
            }
            else {
                return;
            }
        }
    }

    public static void pointToGeofence(Geofence geofence) {
        String id = geofence.getRequestId();
        if(isInteger(id)) {
            if(Integer.parseInt(id) < checkpointsList.size()) {
                currentCheckpointIndex = Integer.parseInt(id);
            }
            else {
                return;
            }
        }
    }

    /*
        Private methods
     */
    private static boolean isInteger(String input) {
        try {
            Integer.parseInt(input);
            return true;
        }
        catch (NumberFormatException e) {
            return false;
        }
    }
}
