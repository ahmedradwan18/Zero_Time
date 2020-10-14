package com.zerotime.zerotime2020.Moderator;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zerotime.zerotime2020.Moderator.Adapters.NumberOrdersAdapter;
import com.zerotime.zerotime2020.Moderator.Pojos.OrdersNumber;
import com.zerotime.zerotime2020.Receiver.MyBroadCast;
import com.zerotime.zerotime2020.No_Internet_Connection;
import com.zerotime.zerotime2020.R;
import com.zerotime.zerotime2020.databinding.ModeratorActivityNumberOfOrdersBinding;

import java.util.ArrayList;
import java.util.Objects;

public class ModeratorNumberOfOrders extends AppCompatActivity {
    DatabaseReference reference;
    SharedPreferences preferences;
    ArrayList<OrdersNumber> ordersNumbers;
    NumberOrdersAdapter adapter;


    private ModeratorActivityNumberOfOrdersBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ModeratorActivityNumberOfOrdersBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // Check Internet State
        if (!haveNetworkConnection()) {
            Intent i = new Intent(ModeratorNumberOfOrders.this, No_Internet_Connection.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        }
        checkInternetConnection();
        //-----------------------------------

        binding.recyclerOrdersNumber.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        binding.recyclerOrdersNumber.setLayoutManager(linearLayoutManager);
        binding.recyclerOrdersNumber.setItemAnimator(new DefaultItemAnimator());

        binding.numberOfOrdersFragmentProgress.setVisibility(View.VISIBLE);

        preferences = getSharedPreferences("UserState", MODE_PRIVATE);
        ordersNumbers = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("OrdersCount");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    binding.numberOfOrdersFragmentProgress.setVisibility(View.GONE);
                    binding.recyclerOrdersNumber.setVisibility(View.GONE);
                    binding.moderatorNumberOfOrdersNoResult.setVisibility(View.VISIBLE);
                }
                if (snapshot.exists()) {
                    if (!snapshot.hasChildren()){
                        binding.numberOfOrdersFragmentProgress.setVisibility(View.GONE);
                        binding.recyclerOrdersNumber.setVisibility(View.GONE);
                        binding.moderatorNumberOfOrdersNoResult.setVisibility(View.VISIBLE);
                    }
                    if (snapshot.hasChildren()) {
                        binding.numberOfOrdersFragmentProgress.setVisibility(View.GONE);
                        binding.recyclerOrdersNumber.setVisibility(View.VISIBLE);
                        binding.moderatorNumberOfOrdersNoResult.setVisibility(View.GONE);

                        ordersNumbers.clear();
                        for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {

                            String userName = dataSnapshot1.child("UserName").getValue(String.class);
                            long ordersCount = Objects.requireNonNull(dataSnapshot1.child("OrdersCount").getValue(Long.class));

                            OrdersNumber ordersNumber = new OrdersNumber();
                            ordersNumber.setName(userName);
                            ordersNumber.setOrdersNumber(ordersCount);
                            ordersNumber.setPhone(dataSnapshot1.getKey());

                            ordersNumbers.add(ordersNumber);


                        }
                    }
                    adapter = new NumberOrdersAdapter(ordersNumbers, ModeratorNumberOfOrders.this);
                    binding.recyclerOrdersNumber.setAdapter(adapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
    private void layoutAnimation(RecyclerView recyclerView) {

        Context context = recyclerView.getContext();
        LayoutAnimationController animationController =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_slide_right);
        recyclerView.setLayoutAnimation(animationController);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();


    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i=new Intent(ModeratorNumberOfOrders.this,ModeratorHome.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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