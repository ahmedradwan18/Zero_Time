package com.zerotime.zerotime2020.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.zerotime.zerotime2020.Receiver.MyBroadCast;
import com.zerotime.zerotime2020.No_Internet_Connection;
import com.zerotime.zerotime2020.R;
import com.zerotime.zerotime2020.Secretary.Adapters.FollowingOrderAdapter;
import com.zerotime.zerotime2020.Secretary.Pojos.OrderState;
import com.zerotime.zerotime2020.databinding.UserFragmentFollowMyOrdersBinding;

import java.util.ArrayList;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class FollowMyOrdersFragment extends Fragment {

    private UserFragmentFollowMyOrdersBinding binding;
    private FollowingOrderAdapter adapter;
    private boolean isScrolling = false;
    private int currentItems, totalItems, scrollOutItems;
    private DatabaseReference ordersRef;
    private ArrayList<OrderState> ordersList;
    String userPhone;
    Context context;
    View view;
    Query query;
    private static int firstVisibleInListview;
    int currentFirstVisible;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = UserFragmentFollowMyOrdersBinding.inflate(getLayoutInflater());
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

        ordersRef = FirebaseDatabase.getInstance().getReference("PendingOrders");
        ordersList = new ArrayList<>();

        SharedPreferences prefs = Objects.requireNonNull(getContext()).getSharedPreferences("UserState", MODE_PRIVATE);
        userPhone = prefs.getString("isLogged", "");

        binding.followingMyOrdersFragmentRecycler.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new GridLayoutManager(context, 1);

        binding.followingMyOrdersFragmentRecycler.setLayoutManager(mLayoutManager);
        binding.followingMyOrdersFragmentRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true;
                }

            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentItems = mLayoutManager.getChildCount();
                totalItems = mLayoutManager.getItemCount();
                scrollOutItems = mLayoutManager.findFirstVisibleItemPosition();

                if (isScrolling && (currentItems + scrollOutItems == totalItems)) {
                    isScrolling = false;
                    //fetchData();
                    if (!(currentFirstVisible > firstVisibleInListview)) {
                        binding.myOrdersFragmentProgress.setVisibility(View.INVISIBLE);
                    } else
                        fetchData();
                    firstVisibleInListview = currentFirstVisible;

                }

            }
        });


        binding.myOrdersFragmentProgress.setVisibility(View.VISIBLE);

            query = ordersRef.orderByChild("UserPrimaryPhone").equalTo(userPhone);
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.exists()){
                        binding.followingMyOrdersFragmentRecycler.setVisibility(View.GONE);
                        binding.myOrdersFragmentProgress.setVisibility(View.GONE);
                        binding.myOrdersNoResult.setVisibility(View.VISIBLE);
                    }

                    if (snapshot.exists()) {
                        if (!snapshot.hasChildren()){
                            binding.followingMyOrdersFragmentRecycler.setVisibility(View.GONE);
                            binding.myOrdersFragmentProgress.setVisibility(View.GONE);
                            binding.myOrdersNoResult.setVisibility(View.VISIBLE);
                        }
                        if (snapshot.hasChildren()) {
                            binding.followingMyOrdersFragmentRecycler.setVisibility(View.GONE);
                            binding.myOrdersFragmentProgress.setVisibility(View.VISIBLE);
                            binding.myOrdersNoResult.setVisibility(View.GONE);

                            ordersList.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                                binding.myOrdersFragmentProgress.setVisibility(View.GONE);
                                binding.followingMyOrdersFragmentRecycler.setVisibility(View.VISIBLE);

                                String orderDescription = dataSnapshot.child("OrderDescription").getValue(String.class);
                                String orderDate = dataSnapshot.child("OrderDate").getValue(String.class);
                                String orderPrice = dataSnapshot.child("OrderPrice").getValue(String.class);
                                String receiverName = dataSnapshot.child("ReceiverName").getValue(String.class);
                                String OrderState = dataSnapshot.child("OrderState").getValue(String.class);
                                String receiverAddress = dataSnapshot.child("ReceiverAddress").getValue(String.class);
                                String receiverPrimaryPhone = dataSnapshot.child("ReceiverPrimaryPhone").getValue(String.class);
                                String userPrimaryPhone = dataSnapshot.child("UserPrimaryPhone").getValue(String.class);


                                OrderState orderState = new OrderState();

                                orderState.setDescription(orderDescription);
                                orderState.setDate(orderDate);
                                orderState.setPrice(orderPrice);
                                orderState.setName(receiverName);
                                orderState.setAddress(receiverAddress);
                                orderState.setPhone(receiverPrimaryPhone);
                                orderState.setUser_Phone(userPrimaryPhone);

                                assert OrderState != null;
                                if (OrderState.equals("لم يتم الاستلام"))
                                    orderState.setCurrentState(0);
                                if (OrderState.equals("تم الاستلام"))
                                    orderState.setCurrentState(1);
                                if (OrderState.equals("جارى التوصيل"))
                                    orderState.setCurrentState(2);
                                if (OrderState.equals("تم التوصيل"))
                                    orderState.setCurrentState(3);

                                ordersList.add(orderState);

                            }
                            adapter = new FollowingOrderAdapter(ordersList, context);
                            binding.followingMyOrdersFragmentRecycler.setAdapter(adapter);
                            binding.myOrdersFragmentProgress.setVisibility(View.GONE);;

                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        return view;
    }

    private void fetchData() {
        binding.myOrdersFragmentProgress.setVisibility(View.VISIBLE);
        new Handler().postDelayed(() -> {
            query = ordersRef.orderByChild("UserPrimaryPhone").equalTo(userPhone);
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        if (snapshot.hasChildren()) {
                            ordersList.clear();

                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                                binding.myOrdersFragmentProgress.setVisibility(View.GONE);
                                binding.followingMyOrdersFragmentRecycler.setVisibility(View.VISIBLE);

                                String orderDescription = dataSnapshot.child("OrderDescription").getValue(String.class);
                                String orderDate = dataSnapshot.child("OrderDate").getValue(String.class);
                                String orderPrice = dataSnapshot.child("OrderPrice").getValue(String.class);
                                String receiverName = dataSnapshot.child("ReceiverName").getValue(String.class);
                                String OrderState = dataSnapshot.child("OrderState").getValue(String.class);
                                String receiverAddress = dataSnapshot.child("ReceiverAddress").getValue(String.class);
                                String receiverPrimaryPhone = dataSnapshot.child("ReceiverPrimaryPhone").getValue(String.class);
                                String userPrimaryPhone = dataSnapshot.child("UserPrimaryPhone").getValue(String.class);


                                OrderState orderState = new OrderState();

                                orderState.setDescription(orderDescription);
                                orderState.setDate(orderDate);
                                orderState.setPrice(orderPrice);
                                orderState.setName(receiverName);
                                orderState.setAddress(receiverAddress);
                                orderState.setPhone(receiverPrimaryPhone);
                                orderState.setUser_Phone(userPrimaryPhone);

                                assert OrderState != null;
                                if (OrderState.equals("لم يتم الاستلام"))
                                    orderState.setCurrentState(0);
                                if (OrderState.equals("تم الاستلام"))
                                    orderState.setCurrentState(1);
                                if (OrderState.equals("جارى التوصيل"))
                                    orderState.setCurrentState(2);
                                if (OrderState.equals("تم التوصيل"))
                                    orderState.setCurrentState(3);


                                ordersList.add(orderState);


                            }
                            adapter.notifyDataSetChanged();
                            binding.myOrdersFragmentProgress.setVisibility(View.GONE);

                        }
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }, 4000);


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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