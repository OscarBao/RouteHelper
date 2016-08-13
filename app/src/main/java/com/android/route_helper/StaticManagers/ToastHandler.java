package com.android.route_helper.StaticManagers;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by obao_Lenovo on 7/12/2016.
 */
public class ToastHandler {

    public ToastHandler() {
    }

    public static void displayMessage(Context context, String message) {
        Intent toToastServiceIntent = new Intent(context, ToastService.class);
        toToastServiceIntent.putExtra("message", message);
        context.startService(toToastServiceIntent);
    }
}
