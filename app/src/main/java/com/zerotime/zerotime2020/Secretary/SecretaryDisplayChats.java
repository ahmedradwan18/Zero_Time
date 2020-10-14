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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zerotime.zerotime2020.Receiver.MyBroadCast;
import com.zerotime.zerotime2020.No_Internet_Connection;
import com.zerotime.zerotime2020.R;
import com.zerotime.zerotime2020.Secretary.Adapters.DisplayChatsAdapter;
import com.zerotime.zerotime2020.Secretary.Pojos.ChatList;
import com.zerotime.zerotime2020.Secretary.Pojos.Users;
import com.zerotime.zerotime2020.databinding.SecretaryActivityDisplayChatsBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SecretaryDisplayChats extends AppCompatActivity {
    String userToken;
    Random random;
    private SecretaryActivityDisplayChatsBinding binding;
    private DisplayChatsAdapter userAdapter;
    private List<Users> mUsers;
    private List<ChatList> userList;
    private DatabaseReference chatListRef1, chatListRef2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SecretaryActivityDisplayChatsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // Check Internet State
        if (!haveNetworkConnection()) {
            Intent i = new Intent(SecretaryDisplayChats.this, No_Internet_Connection.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        }
        checkInternetConnection();
        //-----------------------------------
        binding.secretaryDisplayChatsRecycler.setHasFixedSize(true);
        binding.secretaryDisplayChatsRecycler.setLayoutManager(new LinearLayoutManager(this));

        random = new Random();

        binding.secretaryChatsProgress.setVisibility(View.VISIBLE);

        userList = new ArrayList<>();

        chatListRef1 = FirebaseDatabase.getInstance().getReference("ChatList").child("Zero Time");
        chatListRef2 = FirebaseDatabase.getInstance().getReference("ChatList");

        chatListRef1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    String userPrimaryPhone = ds.child("Receiver_ID").getValue(String.class);
                    ChatList chatList = new ChatList();
                    chatList.setUserPrimaryPhone(userPrimaryPhone);
                    userList.add(chatList);

                }

                chatList();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
       
    }

    private void chatList() {
        mUsers = new ArrayList<>();
        chatListRef1 = FirebaseDatabase.getInstance().getReference("Users");
        chatListRef1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    binding.secretaryChatsProgress.setVisibility(View.GONE);
                    binding.secretaryDisplayChatsRecycler.setVisibility(View.GONE);
                    binding.secretaryChatsNoResult.setVisibility(View.VISIBLE);
                }
                if (snapshot.exists()) {
                    if (!snapshot.hasChildren()) {

                        binding.secretaryChatsProgress.setVisibility(View.GONE);
                        binding.secretaryDisplayChatsRecycler.setVisibility(View.GONE);
                        binding.secretaryChatsNoResult.setVisibility(View.VISIBLE);

                    } else {
                        binding.secretaryChatsProgress.setVisibility(View.GONE);
                        binding.secretaryDisplayChatsRecycler.setVisibility(View.VISIBLE);
                        binding.secretaryChatsNoResult.setVisibility(View.GONE);

                        mUsers.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            String userPrimaryPhone = ds.child("UserPrimaryPhone").getValue(String.class);
                            String userName = ds.child("UserName").getValue(String.class);
                            int rand_int = random.nextInt(10);

                            Users users = new Users();
                            users.setUserPrimaryPhone(userPrimaryPhone);
                            users.setUserName(userName);
                            users.setRandom(rand_int);

                            for (ChatList chatList : userList) {
                                //Toast.makeText(context,users.getUser_ID() + "\n" + chatList.getUser_ID(),Toast.LENGTH_SHORT).show();

                                if (users.getUserPrimaryPhone().equals(chatList.getUserPrimaryPhone())) {
                                    mUsers.add(users);
                                }
                            }
                        }
                        userAdapter = new DisplayChatsAdapter(SecretaryDisplayChats.this, mUsers);
                        binding.secretaryDisplayChatsRecycler.setAdapter(userAdapter);
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.secretaryDisplayChatsRecycler.setHasFixedSize(true);
        binding.secretaryDisplayChatsRecycler.setLayoutManager(new LinearLayoutManager(this));
        //--------------------------------
        userList = new ArrayList<>();

        chatListRef1 = FirebaseDatabase.getInstance().getReference("ChatList").child("Zero Time");
        chatListRef1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String userPrimaryPhone = ds.child("Receiver_ID").getValue(String.class);
                    ChatList chatList = new ChatList();
                    chatList.setUserPrimaryPhone(userPrimaryPhone);
                    userList.add(chatList);

                }

                chatList();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(SecretaryDisplayChats.this, SecretaryHome.class);
        startActivity(i);
        finish();
    }
}