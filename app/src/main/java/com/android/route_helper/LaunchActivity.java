package com.android.route_helper;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.android.route_helper.LocationTracking.LocationTracker;

public class LaunchActivity extends AppCompatActivity {

    Class homeActivity = HomeActivity.class;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        /*
        Perform loading activities
         */
        LocationTracker locationTracker = new LocationTracker(this);

        Intent toHomeActivity = new Intent(this, homeActivity);
        startActivity(toHomeActivity);
    }
}
