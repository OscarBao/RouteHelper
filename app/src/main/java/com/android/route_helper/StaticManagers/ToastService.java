package com.android.route_helper.StaticManagers;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.widget.Toast;


public class ToastService extends IntentService {


    public ToastService() {
        super("ToastService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if(intent == null) return;
        if(intent.getStringExtra("message") != null) {
            Toast.makeText(getApplicationContext(), intent.getStringExtra("message"), Toast.LENGTH_LONG).show();
        }
    }
}
