package com.android.route_helper;

import com.android.route_helper.LocationTracking.LocationTracker;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Toast;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.android.route_helper.CheckpointManaging.Checkpoints;
import com.android.route_helper.LocationTracking.LocationTracker;

public class LaunchActivity extends AppCompatActivity {

    public static final int REQUEST_LOCATION_CODE = 120;
    Class homeActivity = HomeActivity.class;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        /*
        Perform loading activities
         */
        LocationTracker locationTracker = new LocationTracker(this);
        MapsManager mapsManager = new MapsManager();
        Checkpoints checkpoints = new Checkpoints();


        /*
        DialogInterface.OnClickListener yesNoListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == DialogInterface.BUTTON_POSITIVE) {
                    ActivityCompat.requestPermissions(LaunchActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);

                }
                else {
                    //exit app
                    LaunchActivity.this.finish();
                }
            }
        };

        AlertDialog.Builder locationAlertBuilder = new AlertDialog.Builder(this);
        locationAlertBuilder.setMessage("Please enable location services").setPositiveButton("Ok", yesNoListener).setNegativeButton("Uhh I don't think so", yesNoListener).show();
        */

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if(requestCode == REQUEST_LOCATION_CODE) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) { //if permission granted
                Intent switchToHome = new Intent(this, homeActivity);
                startActivity(switchToHome);
            }
            else {
                finish();
            }
        }
    }
}
