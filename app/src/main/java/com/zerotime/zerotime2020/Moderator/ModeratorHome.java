package com.zerotime.zerotime2020.Moderator;

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
import com.zerotime.zerotime2020.Secretary.FollowingTheOrderState;
import com.zerotime.zerotime2020.User.Login;
import com.zerotime.zerotime2020.databinding.ModeratorActivityHomeBinding;

public class ModeratorHome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //binding view
        ModeratorActivityHomeBinding binding = ModeratorActivityHomeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // Check Internet State
        if (!haveNetworkConnection()) {
            Intent i = new Intent(ModeratorHome.this, No_Internet_Connection.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        }
        checkInternetConnection();
        //-----------------------------------

        //New Orders Button  ->  go to view pending Orders
        binding.ModeratorHomeOrdersBtn.setOnClickListener(view1 -> {
            Intent i = new Intent(ModeratorHome.this, FollowingTheOrderState.class);
            i.putExtra("from", "M");
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        });

        //Add Clerk Button  ->  to add a new Clerk
        binding.ModeratorHomeAddClerkBtn.setOnClickListener(view1 -> {
            Intent i = new Intent(ModeratorHome.this, ModeratorAddClerk.class);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        });

        //View Clerks Button  ->  to view all Clerks in the Company
        binding.ModeratorHomeViewClerksBtn.setOnClickListener(view12 -> {
            Intent i = new Intent(ModeratorHome.this, ModeratorViewClerks.class);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        });
        //Add Offer Button  ->  to add a new Monthly Offer
        binding.ModeratorHomeAddOffersBtn.setOnClickListener(view12 -> {
            Intent i = new Intent(ModeratorHome.this, ModeratorAddOffer.class);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        });

        // Complaints Button  -> to view users Complaints
        binding.ModeratorHomeComplaintsBtn.setOnClickListener(view12 -> {
            Intent i = new Intent(ModeratorHome.this, ModeratorComplaints.class);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        });

        // number of order Button  ->  to view the order's number of each user
        binding.ModeratorHomeNumberOFOrdersBtn.setOnClickListener(view12 -> {
            Intent i = new Intent(ModeratorHome.this, ModeratorNumberOfOrders.class);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    /*    ModeratorAddClerk moderatorAddClerk=new ModeratorAddClerk();
        ModeratorAddOffer moderatorAddOffer=new ModeratorAddOffer();
        ModeratorClerksHistory moderatorClerksHistory=new ModeratorClerksHistory();
        ModeratorComplaints moderatorComplaints=new ModeratorComplaints();
        ModeratorNumberOfOrders moderatorNumberOfOrders=new ModeratorNumberOfOrders();
        ModeratorViewClerks moderatorViewClerks=new ModeratorViewClerks();*/


        /*ModeratorAddClerk moderatorAddClerk = new ModeratorAddClerk();
        ModeratorAddOffer moderatorAddOffer = new ModeratorAddOffer();
        ModeratorComplaints moderatorComplaints = new ModeratorComplaints();
        ModeratorNumberOfOrders moderatorNumberOfOrders = new ModeratorNumberOfOrders();
        ModeratorViewClerks moderatorViewClerks = new ModeratorViewClerks();
        FollowingTheOrderState followingTheOrderState = new FollowingTheOrderState();*/

        //go back to login activity
        Intent i = new Intent(ModeratorHome.this, Login.class);
        startActivity(i);
     /*   moderatorAddClerk.finish();
        moderatorAddOffer.finish();
        moderatorComplaints.finish();
        moderatorViewClerks.finish();
        moderatorClerksHistory.finish();
        moderatorNumberOfOrders.finish();*/
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        /*moderatorAddClerk.finish();
        moderatorAddOffer.finish();
        moderatorComplaints.finish();
        moderatorNumberOfOrders.finish();
        moderatorViewClerks.finish();
        followingTheOrderState.finish();*/
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
}