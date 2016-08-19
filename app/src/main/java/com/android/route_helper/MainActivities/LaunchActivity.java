package com.android.route_helper.MainActivities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.android.route_helper.CheckpointManaging.Checkpoints;
import com.android.route_helper.Maps.MapsManager;
import com.android.route_helper.R;
import com.android.route_helper.StaticManagers.ToastHandler;

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
        ToastHandler toastHandler = new ToastHandler();
        MapsManager mapsManager = new MapsManager();
        Checkpoints checkpoints = new Checkpoints();



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
