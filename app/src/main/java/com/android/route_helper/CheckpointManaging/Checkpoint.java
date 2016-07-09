package com.android.route_helper.CheckpointManaging;

import android.location.Location;

/**
 * Created by obao_Lenovo on 7/8/2016.
 */
public class Checkpoint {
    int typeCode; //typeCode for the checkpoint, 0 for walk, 1 for bus
    String name;
    Location location;

    public Checkpoint() {
        this(null, "Default Checkpoint", 0);
    }

    public Checkpoint(Location location, String name, int typeCode) {
        this.location = location;
        this.name = name;
        this.typeCode = typeCode;
    }

    /*
        Accessors and mutators
     */
    public Location getLocation() {return this.location;}
    public String getName() {return this.name;}
    public int getTypeCode() {return this.typeCode;}

    public void setLocation(Location location) {this.location = location;}
    public void setName(String name) {this.name = name;}
    public void setTypeCode(int typeCode) {this.typeCode = typeCode;}
}
