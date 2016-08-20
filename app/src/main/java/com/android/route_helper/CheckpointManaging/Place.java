package com.android.route_helper.CheckpointManaging;

import android.location.Location;

/**
 * Created by Oscar_Local on 8/20/2016.
 */

public class Place {
    Location location;
    String name;

    public Place(Location loc, String name) {
        location = loc;
        this.name = name;
    }

    public Location getLocation() {
        return location;
    }
    public String getName() {
        return name;
    }
}
