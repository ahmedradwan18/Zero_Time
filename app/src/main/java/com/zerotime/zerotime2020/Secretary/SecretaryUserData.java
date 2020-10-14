package com.zerotime.zerotime2020.Secretary;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zerotime.zerotime2020.Receiver.MyBroadCast;
import com.zerotime.zerotime2020.No_Internet_Connection;
import com.zerotime.zerotime2020.R;
import com.zerotime.zerotime2020.databinding.SecretaryActivityUserDataBinding;


public class SecretaryUserData extends AppCompatActivity {
    private SecretaryActivityUserDataBinding binding;
    String phone;
    private DatabaseReference UserRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SecretaryActivityUserDataBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // Check Internet State
        if (!haveNetworkConnection()) {
            Intent i = new Intent(SecretaryUserData.this, No_Internet_Connection.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        }
        checkInternetConnection();
        //-----------------------------------

        Intent intent = getIntent();

        phone = intent.getStringExtra("UserPhone");

        UserRef = FirebaseDatabase.getInstance().getReference("Users");

        UserRef.child(phone).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                binding.secretaryUserDataProgress.setVisibility(View.GONE);
                binding.secretaryUserDataConstraint.setVisibility(View.VISIBLE);
                String userAddress = dataSnapshot.child("UserAddress").getValue(String.class);
                String userPrimaryPhone = dataSnapshot.child("UserPrimaryPhone").getValue(String.class);
                String userSecondaryPhone = dataSnapshot.child("UserSecondaryPhone").getValue(String.class);
                String userName = dataSnapshot.child("UserName").getValue(String.class);

                binding.UserName.setText(userName);
                binding.UserAddress.setText(userAddress);
                binding.UserPhone1.setText(userPrimaryPhone);
                binding.UserPhone2.setText(userSecondaryPhone);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
    private void checkInternetConnection(){
        MyBroadCast broadCast=new MyBroadCast();
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(broadCast,intentFilter);

    }
}