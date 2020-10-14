package com.zerotime.zerotime2020.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.zerotime.zerotime2020.Adapters.HistoryAdapter;
import com.zerotime.zerotime2020.No_Internet_Connection;
import com.zerotime.zerotime2020.Pojos.HistoryPojo;
import com.zerotime.zerotime2020.R;
import com.zerotime.zerotime2020.Receiver.MyBroadCast;
import com.zerotime.zerotime2020.databinding.UserFragmentHistoryBinding;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class HistoryFragment extends Fragment {

    DatabaseReference reference;
    SharedPreferences preferences;
    ArrayList<HistoryPojo> historyPojos;
    HistoryAdapter adapter;
    Context context;
    View view;
    private UserFragmentHistoryBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = UserFragmentHistoryBinding.inflate(getLayoutInflater());
        view = binding.getRoot();
        context = container.getContext();


        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Check Internet State
        if (!haveNetworkConnection()) {
            Intent i = new Intent(context, No_Internet_Connection.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            ((Activity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            ((Activity) context).finish();
        }
        checkInternetConnection();
        //-----------------------------------


    }

    @Override
    public void onStart() {
        super.onStart();
        binding.historyRecycler.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        binding.historyRecycler.setLayoutManager(linearLayoutManager);

        binding.myHistoryProgress.setVisibility(View.VISIBLE);

        preferences = context.getSharedPreferences("UserState", MODE_PRIVATE);
        historyPojos = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("DeliveredOrders");
        Query query = reference.orderByChild("UserPrimaryPhone").equalTo(preferences.getString("isLogged", "null"));
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    binding.historyRecycler.setVisibility(View.GONE);
                    binding.myHistoryProgress.setVisibility(View.GONE);
                    binding.myHistoryNoResult.setVisibility(View.VISIBLE);
                }
                if (snapshot.exists()) {
                    if (!snapshot.hasChildren()) {

                        binding.historyRecycler.setVisibility(View.GONE);
                        binding.myHistoryProgress.setVisibility(View.GONE);
                        binding.myHistoryNoResult.setVisibility(View.VISIBLE);

                    } else {
                        binding.historyRecycler.setVisibility(View.GONE);
                        binding.myHistoryProgress.setVisibility(View.VISIBLE);
                        binding.myHistoryNoResult.setVisibility(View.GONE);

                        historyPojos.clear();

                        for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {

                            binding.myHistoryProgress.setVisibility(View.GONE);
                            binding.historyRecycler.setVisibility(View.VISIBLE);

                            String description = dataSnapshot1.child("OrderDescription").getValue(String.class);
                            String date = dataSnapshot1.child("OrderDate").getValue(String.class);
                            String price = dataSnapshot1.child("OrderPrice").getValue(String.class);
                            String size = dataSnapshot1.child("OrderSize").getValue(String.class);
                            String Raddress = dataSnapshot1.child("ReceiverName").getValue(String.class);
                            String Rname = dataSnapshot1.child("ReceiverAddress").getValue(String.class);
                            String Rphone1 = dataSnapshot1.child("ReceiverPrimaryPhone").getValue(String.class);
                            String Rphone2 = dataSnapshot1.child("ReceiverSecondaryPhone").getValue(String.class);

                            HistoryPojo historyPojo = new HistoryPojo();
                            historyPojo.setDescription(description);
                            historyPojo.setDate(date);
                            historyPojo.setSize(size);
                            historyPojo.setPrice(price);
                            historyPojo.setRaddress(Raddress);
                            historyPojo.setRname(Rname);
                            historyPojo.setRphone1(Rphone1);
                            historyPojo.setRphone2(Rphone2);

                            historyPojos.add(historyPojo);


                        }

                    }
                    adapter = new HistoryAdapter(historyPojos, context);
                    binding.historyRecycler.setAdapter(adapter);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @Override
    public void onResume() {
        super.onResume();

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener((v, keyCode, event) -> {

            if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                assert getFragmentManager() != null;
                getFragmentManager().popBackStackImmediate();
                return true;
            }

            return false;
        });

    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
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
        context.registerReceiver(broadCast, intentFilter);

    }
}