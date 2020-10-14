package com.zerotime.zerotime2020.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zerotime.zerotime2020.No_Internet_Connection;
import com.zerotime.zerotime2020.R;
import com.zerotime.zerotime2020.Receiver.MyBroadCast;
import com.zerotime.zerotime2020.databinding.UserFragmentDisplayOffersBinding;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class DisplayOffersFragment extends Fragment {
    View view;
    Context context;
    private UserFragmentDisplayOffersBinding binding;
    private DatabaseReference offersRef;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = UserFragmentDisplayOffersBinding.inflate(getLayoutInflater());
        view = binding.getRoot();
        context = container.getContext();


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

        offersRef = FirebaseDatabase.getInstance().getReference("Offers");


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //gettingOffers();
        startThread();

    }

    public void startThread() {
        new Thread(this::gettingOffers).start();
    }

    private void gettingOffers() {
        binding.displayOffersFragmentProgress.setVisibility(View.VISIBLE);
        offersRef.child("Offers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    binding.displayOffersFragmentProgress.setVisibility(View.GONE);
                    binding.scrollLayout.setVisibility(View.GONE);
                    binding.offersNoResult.setVisibility(View.VISIBLE);
                }
                if (dataSnapshot.exists()) {
                    if (!dataSnapshot.hasChildren()) {
                        binding.displayOffersFragmentProgress.setVisibility(View.GONE);
                        binding.scrollLayout.setVisibility(View.GONE);
                        binding.offersNoResult.setVisibility(View.VISIBLE);
                    }


                    if (dataSnapshot.hasChildren()) {
                        binding.offersNoResult.setVisibility(View.GONE);
                        binding.displayOffersFragmentProgress.setVisibility(View.GONE);
                        binding.scrollLayout.setVisibility(View.VISIBLE);

                        String firstOffer = dataSnapshot.child("FirstOffer").getValue(String.class);
                        String secondOffer = dataSnapshot.child("SecondOffer").getValue(String.class);
                        String thirdOffer = dataSnapshot.child("ThirdOffer").getValue(String.class);
                        String fourthOffer = dataSnapshot.child("FourthOffer").getValue(String.class);
                        String fifthOffer = dataSnapshot.child("FifthOffer").getValue(String.class);
                        String sixthOffer = dataSnapshot.child("SixthOffer").getValue(String.class);

                        if (Objects.requireNonNull(firstOffer).equals("لا يوجد")) {
                            binding.displayOffersFragmentFirstCard.setVisibility(View.GONE);
                        } else {
                            binding.displayOffersFragmentFirstOfferTextView.setText(firstOffer);

                        }
                        if (Objects.requireNonNull(secondOffer).equals("لا يوجد")) {
                            binding.displayOffersFragmentSecondCard.setVisibility(View.GONE);
                        } else {
                            binding.displayOffersFragmentSecondOfferTextView.setText(secondOffer);

                        }
                        if (Objects.requireNonNull(thirdOffer).equals("لا يوجد")) {
                            binding.displayOffersFragmentThirdCard.setVisibility(View.GONE);
                        } else {
                            binding.displayOffersFragmentThirdOfferTextView.setText(thirdOffer);

                        }
                        if (Objects.requireNonNull(fourthOffer).equals("لا يوجد")) {
                            binding.displayOffersFragmentFourthCard.setVisibility(View.GONE);
                        } else {
                            binding.displayOffersFragmentFourthOfferTextView.setText(fourthOffer);

                        }
                        if (Objects.requireNonNull(fifthOffer).equals("لا يوجد")) {
                            binding.displayOffersFragmentFifthCard.setVisibility(View.GONE);
                        } else {
                            binding.displayOffersFragmentFifthOfferTextView.setText(fifthOffer);

                        }
                        if (Objects.requireNonNull(sixthOffer).equals("لا يوجد")) {
                            binding.displayOffersFragmentSixthCard.setVisibility(View.GONE);
                        } else {
                            binding.displayOffersFragmentSixthOfferTextView.setText(sixthOffer);

                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
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
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener((v, keyCode, event) -> {

            if (keyCode == KeyEvent.KEYCODE_BACK) {

                assert getFragmentManager() != null;
                getFragmentManager().popBackStackImmediate();

                return true;
            }

            return false;
        });
    }
}