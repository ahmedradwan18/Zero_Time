package com.zerotime.zerotime2020.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.zerotime.zerotime2020.No_Internet_Connection;


public class MyBroadCast extends BroadcastReceiver {
    private static final int NO_CONNECTION_TYPE = -1;
    private static int sLastType = NO_CONNECTION_TYPE;

    @Override
    public void onReceive(Context context, Intent intent) {

        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        final int currentType = activeNetworkInfo != null
                ? activeNetworkInfo.getType() : NO_CONNECTION_TYPE;

        // Avoid handling multiple broadcasts for the same connection type
        if (sLastType != currentType) {
            if (activeNetworkInfo != null) {
                boolean isConnectedOrConnecting = activeNetworkInfo.isConnectedOrConnecting();
                boolean isWiFi = ConnectivityManager.TYPE_WIFI == currentType;
                boolean isMobile = ConnectivityManager.TYPE_MOBILE == currentType;

                // TODO Connected. Do your stuff!
            } else {
                // TODO Disconnected. Do your stuff!
                Intent i = new Intent(context, No_Internet_Connection.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);

            }

            sLastType = currentType;
        }


    }
}
