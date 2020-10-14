package com.zerotime.zerotime2020;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.zerotime.zerotime2020.User.SplashScreen;
import com.zerotime.zerotime2020.databinding.ActivityNoInternetConnectionBinding;

import es.dmoral.toasty.Toasty;

public class No_Internet_Connection extends AppCompatActivity {

    private static final int NO_CONNECTION_TYPE = -1;

    private static int sLastType = NO_CONNECTION_TYPE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityNoInternetConnectionBinding binding = ActivityNoInternetConnectionBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        binding.noConnectionRetryBtn.setOnClickListener(view1 -> {
            ConnectivityManager connectivityManager = (ConnectivityManager)
                    getSystemService(Context.CONNECTIVITY_SERVICE);

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
                    Toasty.success(getApplicationContext(), "اونلاين مجدداً", Toasty.LENGTH_SHORT, true).show();
                    Intent splash_intent = new Intent(No_Internet_Connection.this, SplashScreen.class);
                    splash_intent.putExtra("UniqueID", "NoInternetConnection");
                    splash_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP );
                    startActivity(splash_intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();

                } else {
                    // TODO Disconnected. Do your stuff!
                    Toasty.error(getApplicationContext(), "لست متصلاً حتى الان !", Toasty.LENGTH_SHORT, true).show();

                }

                sLastType = currentType;
            } else
                Toasty.error(getApplicationContext(), "لست متصلاً حتى الان !", Toasty.LENGTH_SHORT, true).show();

        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        int pid = android.os.Process.myPid();
        android.os.Process.killProcess(pid);


    }

}