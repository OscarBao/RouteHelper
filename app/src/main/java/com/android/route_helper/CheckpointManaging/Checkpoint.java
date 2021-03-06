package com.android.route_helper.CheckpointManaging;

import android.location.Geocoder;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by obao_Lenovo on 7/8/2016.
 */
public class Checkpoint {
    int typeCode; //typeCode for the checkpoint, 0 for walk, 1 for bus
    String name;
    Location location;
    List<LatLng> polyline;
    String transitInfo;
    String googleTravelInfo;

    public Checkpoint() {
        this(null, "Default Checkpoint", 0, null, "");
    }

    public Checkpoint(Location location, String name, int typeCode, List<LatLng> polyline, String googleTravelInfo) {
        this.location = location;
        this.name = name;
        this.typeCode = typeCode;
        this.polyline = polyline;
        this.googleTravelInfo = googleTravelInfo;
    }

    /*
        Accessors and mutators
     */
    public Location getLocation() {return this.location;}
    public String getName() {return this.name;}
    public int getTypeCode() {return this.typeCode;}
    public List<LatLng> getPolyline() {return this.polyline;}
    public String getTransitInfo() {return this.transitInfo;}
    public String getGoogleTravelInfo() {return this.googleTravelInfo;}

    public void setLocation(Location location) {this.location = location;}
    public void setName(String name) {this.name = name;}
    public void setTypeCode(int typeCode) {this.typeCode = typeCode;}
    public void setPolyline(List<LatLng> polyline) {this.polyline = polyline;}
    public void setTransitInfo(String transitInfo) {this.transitInfo = transitInfo;}
}
