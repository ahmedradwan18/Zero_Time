package com.zerotime.zerotime2020.Secretary;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AbsListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zerotime.zerotime2020.Fragments.ProfileFragment;
import com.zerotime.zerotime2020.Moderator.ModeratorHome;
import com.zerotime.zerotime2020.Receiver.MyBroadCast;
import com.zerotime.zerotime2020.No_Internet_Connection;
import com.zerotime.zerotime2020.R;
import com.zerotime.zerotime2020.Secretary.Adapters.FollowingOrderAdapter;
import com.zerotime.zerotime2020.Secretary.Pojos.OrderState;
import com.zerotime.zerotime2020.databinding.SecretaryActivityFollowingTheOrderStateBinding;

import java.util.ArrayList;
import java.util.Objects;

public class FollowingTheOrderState extends AppCompatActivity {
    private SecretaryActivityFollowingTheOrderStateBinding binding;
    private FollowingOrderAdapter adapter;
    private ArrayList<OrderState> ordersList;
    private boolean isScrolling = false;
    private int currentItems, totalItems, scrollOutItems;
    DatabaseReference orderStateRef;
    private static int firstVisibleInListview;
    int currentFirstVisible;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SharedPreferences prefs = getSharedPreferences("UserState", MODE_PRIVATE);
        String userType = prefs.getString("UserType", "");
        if (userType != null) {
            switch (userType) {
                case "Secretary":
                    Intent i = new Intent(FollowingTheOrderState.this, SecretaryHome.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                    break;
                case "Moderator":
                    Intent ii = new Intent(FollowingTheOrderState.this, ModeratorHome.class);
                    startActivity(ii);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                    break;
                case "User":
                    Intent intent = new Intent(FollowingTheOrderState.this, ProfileFragment.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                    break;
            }
        }

    }

    private void layoutAnimation(RecyclerView recyclerView) {
        Context context = recyclerView.getContext();
        LayoutAnimationController animationController =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_slide_right);
        recyclerView.setLayoutAnimation(animationController);
        Objects.requireNonNull(recyclerView.getAdapter()).notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SecretaryActivityFollowingTheOrderStateBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        // Check Internet State
        if (!haveNetworkConnection()) {
            Intent i = new Intent(FollowingTheOrderState.this, No_Internet_Connection.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        }
        checkInternetConnection();
        //-----------------------------------


        Drawable progressDrawable = binding.secretaryOrdersProgress.getIndeterminateDrawable().mutate();
        progressDrawable.setColorFilter(Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
        binding.secretaryOrdersProgress.setProgressDrawable(progressDrawable);
        binding.secretaryOrdersProgress.setVisibility(View.VISIBLE);

        binding.OrderStateRecycler.setLayoutManager(new LinearLayoutManager(this));
        LinearLayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        binding.OrderStateRecycler.setLayoutManager(mLayoutManager);
        firstVisibleInListview = mLayoutManager.findFirstVisibleItemPosition();

        binding.OrderStateRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                currentFirstVisible = mLayoutManager.findFirstVisibleItemPosition();
                currentItems = mLayoutManager.getChildCount();
                totalItems = mLayoutManager.getItemCount();
                scrollOutItems = mLayoutManager.findFirstVisibleItemPosition();
                if (isScrolling && (currentItems + scrollOutItems == totalItems)) {
                    isScrolling = false;

                    if (!(currentFirstVisible > firstVisibleInListview)) {
                        binding.secretaryFollowingOrdersProgress.setVisibility(View.INVISIBLE);
                    } else
                        fetchData();
                    firstVisibleInListview = currentFirstVisible;

                }
            }
        });


        binding.OrderStateRecycler.setItemAnimator(new DefaultItemAnimator());
        ordersList = new ArrayList<>();
        orderStateRef = FirebaseDatabase.getInstance().getReference("PendingOrders");
        orderStateRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    binding.secretaryOrdersProgress.setVisibility(View.GONE);
                    binding.OrderStateRecycler.setVisibility(View.GONE);
                    binding.secretaryFollowingOrdersNoResult.setVisibility(View.VISIBLE);
                }

                if (snapshot.exists()) {
                    if (!snapshot.hasChildren()){
                        binding.secretaryOrdersProgress.setVisibility(View.GONE);
                        binding.OrderStateRecycler.setVisibility(View.GONE);
                        binding.secretaryFollowingOrdersNoResult.setVisibility(View.VISIBLE);
                    }
                    if (snapshot.hasChildren()) {
                        binding.secretaryOrdersProgress.setVisibility(View.GONE);
                        binding.OrderStateRecycler.setVisibility(View.VISIBLE);
                        binding.secretaryFollowingOrdersNoResult.setVisibility(View.GONE);

                        ordersList.clear();

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

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
                        adapter = new FollowingOrderAdapter(ordersList, FollowingTheOrderState.this);
                        binding.OrderStateRecycler.setAdapter(adapter);
                        binding.secretaryFollowingOrdersProgress.setVisibility(View.GONE);

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });



    }


    private void fetchData() {

        binding.secretaryFollowingOrdersProgress.setVisibility(View.VISIBLE);
        new Handler().postDelayed(() -> orderStateRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    binding.secretaryOrdersProgress.setVisibility(View.GONE);
                    binding.OrderStateRecycler.setVisibility(View.GONE);
                    binding.secretaryFollowingOrdersNoResult.setVisibility(View.VISIBLE);
                }
                if (snapshot.exists()) {
                    if (!snapshot.hasChildren()){
                        binding.secretaryOrdersProgress.setVisibility(View.GONE);
                        binding.OrderStateRecycler.setVisibility(View.GONE);
                        binding.secretaryFollowingOrdersNoResult.setVisibility(View.VISIBLE);
                    }
                    if (snapshot.hasChildren()) {
                        binding.secretaryOrdersProgress.setVisibility(View.GONE);
                        binding.OrderStateRecycler.setVisibility(View.VISIBLE);
                        binding.secretaryFollowingOrdersNoResult.setVisibility(View.GONE);

                        ordersList.clear();

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
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
                        binding.secretaryFollowingOrdersProgress.setVisibility(View.INVISIBLE);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        }), 3000);

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