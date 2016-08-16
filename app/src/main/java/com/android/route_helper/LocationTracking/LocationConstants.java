package com.android.route_helper.LocationTracking;

/**
 * Created by Oscar_Local on 7/2/2016.
 */
public interface LocationConstants {
    int GEOFENCE_RADIUS = 40;
    int GEOFENCE_EXPIRATION_IN_HOURS = 10;
    int GEOFENCE_EXPIRATION_DURATION = 1000*3600*GEOFENCE_EXPIRATION_IN_HOURS;
    int LOITERING_TIME = 500;


    double DEFAULT_LOCATION_LATITUDE = 37.7104d;
    double DEFAULT_LOCATION_LONGITUDE = -122.404d;

    String DEFAULT_LOCATION_STRING = "";

    String FLAG_DESTINATION = "destinationFlag";
    String FLAG_SOURCE = "sourceFlag";
}
