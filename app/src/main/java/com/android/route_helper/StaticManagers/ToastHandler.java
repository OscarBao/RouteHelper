package com.android.route_helper.StaticManagers;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by obao_Lenovo on 7/12/2016.
 */
public class ToastHandler {
    public static Context context;

    public ToastHandler(Context context) {
        this.context = context;
    }

    public static void displayMessage(String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}
