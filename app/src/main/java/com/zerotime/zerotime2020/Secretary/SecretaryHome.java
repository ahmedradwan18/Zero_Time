package com.zerotime.zerotime2020.Secretary;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.zerotime.zerotime2020.Receiver.MyBroadCast;
import com.zerotime.zerotime2020.No_Internet_Connection;
import com.zerotime.zerotime2020.R;
import com.zerotime.zerotime2020.User.Login;
import com.zerotime.zerotime2020.databinding.SecretaryActivityHomeBinding;

public class SecretaryHome extends AppCompatActivity {
    private SecretaryActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SecretaryActivityHomeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // Check Internet State
        if (!haveNetworkConnection()) {
            Intent i = new Intent(SecretaryHome.this, No_Internet_Connection.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        }
        checkInternetConnection();
        //-----------------------------------

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SecretaryMessage secretaryMessage = new SecretaryMessage();
        SecretaryDisplayChats secretaryDisplayChats = new SecretaryDisplayChats();
        FollowingTheOrderState followingTheOrderState = new FollowingTheOrderState();
        Intent i = new Intent(SecretaryHome.this, Login.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        secretaryMessage.finish();
        secretaryDisplayChats.finish();
        followingTheOrderState.finish();
        finish();
    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    private void checkInternetConnection() {
        MyBroadCast broadCast = new MyBroadCast();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(broadCast, intentFilter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.secretaryHomeOrdersBtn.setOnClickListener(view1 -> {
            Intent intent = new Intent(SecretaryHome.this, FollowingTheOrderState.class);
            intent.putExtra("from", "S");
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();

        });
        binding.secretaryHomeChatsBtn.setOnClickListener(view1 -> {
            Intent intent = new Intent(SecretaryHome.this, SecretaryDisplayChats.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();

        });
    }
}