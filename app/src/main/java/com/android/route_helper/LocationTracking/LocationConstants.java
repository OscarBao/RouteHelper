package com.android.route_helper.LocationTracking;

/**
 * Created by Oscar_Local on 7/2/2016.
 */
public interface LocationConstants {
    int GEOFENCE_RADIUS = 100;
    int GEOFENCE_EXPIRATION_DURATION = 1000;

    double DEFAULT_LOCATION_LATITUDE = 37.723245d;
    double DEFAULT_LOCATION_LONGITUDE = -122.435618d;

    String DEFAULT_LOCATION_STRING = "";

    String FLAG_DESTINATION = "destinationFlag";
    String FLAG_SOURCE = "sourceFlag";
}
