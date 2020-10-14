package com.zerotime.zerotime2020.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.zerotime.zerotime2020.Receiver.MyBroadCast;
import com.zerotime.zerotime2020.No_Internet_Connection;
import com.zerotime.zerotime2020.R;
import com.zerotime.zerotime2020.databinding.UserFragmentHomeBinding;

import java.util.Objects;

import es.dmoral.toasty.Toasty;


public class HomeFragment extends Fragment {
    UserFragmentHomeBinding binding;
    Context context;
    View view;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = UserFragmentHomeBinding.inflate(inflater, container, false);
        view = binding.getRoot();
        context = container.getContext();

        // Check Internet State
        if (!haveNetworkConnection()) {
            Intent i = new Intent(context, No_Internet_Connection.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
            ((Activity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            ((Activity) context).finish();
        }
        checkInternetConnection();
        //-----------------------------------

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Our Products Button
        binding.homeFragmentOurProductsCard.setOnClickListener(view1 -> Toasty.warning(context,
                "قريباً",
                Toasty.LENGTH_SHORT,
                true).show());

        //Month Offers Button
        binding.homeFragmentMonthOffersCard.setOnClickListener(view1 -> {
            FragmentManager fragmentManager = getFragmentManager();
            DisplayOffersFragment fragment = new DisplayOffersFragment();
            assert fragmentManager != null;
            fragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.enter_right_to_left, R.anim.exit_right_to_left,
                            R.anim.enter_left_to_right, R.anim.exit_left_to_right)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .replace(R.id.Frame_Content, fragment)
                    .addToBackStack("HomeFragment")
                    .commit();

        });
        //Add Order Button
        binding.homeFragmentAddOrderCard.setOnClickListener(view1 -> {
            // Create new fragment and transaction
            FragmentManager fragmentManager = getFragmentManager();
            AddOrderFragment fragment = new AddOrderFragment();
            assert fragmentManager != null;
            fragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.enter_right_to_left, R.anim.exit_right_to_left,
                            R.anim.enter_left_to_right, R.anim.exit_left_to_right)
                    .replace(R.id.Frame_Content, fragment)
                    .addToBackStack("HomeFragment")
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();
        });


        //About Us Button
        binding.homeFragmentAboutUsCard.setOnClickListener(view1 -> {

            // Create new fragment and transaction
            FragmentManager fragmentManager = getFragmentManager();
            AboutUsFragment fragment = new AboutUsFragment();
            assert fragmentManager != null;
            fragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.enter_right_to_left, R.anim.exit_right_to_left,
                            R.anim.enter_left_to_right, R.anim.exit_left_to_right)
                    .replace(R.id.Frame_Content, fragment)
                    .addToBackStack("HomeFragment")
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();
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

    @Override
    public void onResume() {

        super.onResume();

        try {
            new Handler().postDelayed(() -> {
                view.setFocusableInTouchMode(true);
                view.requestFocus();
                view.setOnKeyListener((v, keyCode, event) -> {

                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        Objects.requireNonNull(getActivity()).onBackPressed();
                        Toast.makeText(context.getApplicationContext(), "Home Fragment Back", Toast.LENGTH_SHORT).show();
                        return true;

                    }

                    return false;
                });
            }, 50000);


        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

}