package com.android.route_helper.Abstract;

import android.support.v7.app.AppCompatActivity;

/**
 * Created by Oscar_Local on 8/16/2016.
 */

public abstract class RefreshActivity extends AppCompatActivity {

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    protected abstract void updateUI();
}
