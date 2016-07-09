package com.android.route_helper;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //TODO add geofencing pendingintent and inter-activity geofence management
    }

    public void startRoute(View v) {
        MapsManager.loadMap(this, "Map");
    }



}
