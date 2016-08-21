package com.android.route_helper;

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

    String FLAG_DESTINATION = "selectDestinationFlag";
    String FLAG_SOURCE = "selectSourceFlag";
    String FLAG_STARTROUTE = "startRouteFlag";
    String FLAG_PLANROUTE = "planRouteFlag";


    //Google Directions Info
    String GOOGLE_DIRECTIONS_BASE_URL = "https://maps.googleapis.com/maps/api/directions/json?";
    String GOOGLE_DIRECTIONS_TRANSIT_MODE = "mode=transit";
    String GOOGLE_DIRECTIONS_API_KEY = "key=AIzaSyAwXZoA-POkRv12Stm4h_kgDdSkM-FKgn8";

    String GOOGLE_PLACES_BASE_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
    int GOOGLE_PLACES_DEFAULT_RADIUS = 100;
    String GOOGLE_PLACES_RADIUS_STRING = "radius=" + GOOGLE_PLACES_DEFAULT_RADIUS;
    String GOOGLE_PLACES_API_KEY = "key=AIzaSyDoj7qerU5m56m9NU42R1r7DZ96jgGR4gc";
}
