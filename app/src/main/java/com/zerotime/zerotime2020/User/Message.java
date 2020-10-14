package com.zerotime.zerotime2020.User;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.zerotime.zerotime2020.Adapters.MessageAdapter;
import com.zerotime.zerotime2020.Receiver.MyBroadCast;
import com.zerotime.zerotime2020.No_Internet_Connection;
import com.zerotime.zerotime2020.Pojos.ChatPojo;
import com.zerotime.zerotime2020.R;
import com.zerotime.zerotime2020.databinding.UserActivityMessageBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;

public class Message extends AppCompatActivity {
    private static final int GALLERY_PICK = 0;
    SharedPreferences prefs;
    MessageAdapter adapter;
    List<ChatPojo> chatPojos;
    DatabaseReference chatRef;
    ValueEventListener seenListener;
    String userId, intentFrom;
    FirebaseAuth mAuth;
    FirebaseUser user;
    private UserActivityMessageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = UserActivityMessageBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // Check Internet State
        if (!haveNetworkConnection()) {
            Intent i = new Intent(Message.this, No_Internet_Connection.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        }
        checkInternetConnection();
        //-----------------------------------
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();


        binding.messageRecycler.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        binding.messageRecycler.setLayoutManager(linearLayoutManager);

        if (getIntent().getStringExtra("UniqueID") != null) {
            if (Objects.requireNonNull(getIntent().getStringExtra("UniqueID")).equals("ContactFragment")) {
                userId = getIntent().getStringExtra("UserID");
                intentFrom = "ContactFragment";

            }
        }

        prefs = getSharedPreferences("UserState", MODE_PRIVATE);

        if (user == null) {
            signInAnonymously();
        } else {
            binding.messageSendBtn.setOnClickListener(view12 -> {

                String msg = Objects.requireNonNull(binding.messageWriteMSGEdt.getText()).toString();
                if (!msg.equals("")) {
                    if (intentFrom != null) {
                        if (intentFrom.equals("ContactFragment") || intentFrom.equals("Notification")) {
                            sendMessage(userId, msg);

                        }
                    }

                } else{
                    Toasty.error(Message.this, "لا يمكنك ارسال رسالة فارغة !", Toasty.LENGTH_SHORT, true).show();
                }

                binding.messageWriteMSGEdt.setText("");

            });
            binding.messageSendImage.setOnClickListener(view1 -> {
                if (!haveNetworkConnection()) {
                    Toasty.error(this, "انت لست متصلاً", Toasty.LENGTH_SHORT, true).show();
                    return;
                }

                if (checkPermission()) {
                    Intent galleryIntent = new Intent();
                    galleryIntent.setType("image/*");
                    galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);

                } else {
                    requestPermission();
                }

            });
        }

        ReadMessages();

        seenMessage(userId);

    }

    private void signInAnonymously() {
        try {
            mAuth.signInAnonymously().addOnSuccessListener(this, authResult -> {

                binding.messageSendBtn.setOnClickListener(view -> {

                    String msg = Objects.requireNonNull(binding.messageWriteMSGEdt.getText()).toString();
                    if (!msg.equals("")) {
                        if (intentFrom != null) {
                            sendMessage(userId, msg);

                        }

                    } else
                        Toasty.error(Message.this, "لا يمكنك ارسال رسالة فارغة !", Toasty.LENGTH_SHORT, true).show();
                    binding.messageWriteMSGEdt.setText("");

                });
                binding.messageSendImage.setOnClickListener(view1 -> {
                    if (!haveNetworkConnection()) {
                        Toasty.error(this, "انت لست متصلاً", Toasty.LENGTH_SHORT, true).show();
                        return;
                    }

                    if (checkPermission()) {
                        Intent galleryIntent = new Intent();
                        galleryIntent.setType("image/*");
                        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);

                    } else {
                        requestPermission();
                    }

                });
            });

        } catch (Exception e) {
            Toasty.info(this, "Catch sign anonymously\n" + e.getMessage(), Toasty.LENGTH_LONG,true).show();
        }
    }

    private void seenMessage(final String userid) {
        chatRef = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ChatPojo chat = snapshot.getValue(ChatPojo.class);
                    assert chat != null;
                    if (chat.getReceiver().equals(userid) && chat.getSender().equals("Zero Time")) {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isSeen", true);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage(String Sender, String Message) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("Sender", Sender);
        hashMap.put("Receiver", "Zero Time");
        hashMap.put("Message", Message);
        hashMap.put("isSeen", false);
        hashMap.put("Type", "Text");
        reference.child("Chats").push().setValue(hashMap);

    }

    private void ReadMessages() {
        chatPojos = new ArrayList<>();
        chatRef = FirebaseDatabase.getInstance().getReference("Chats");
        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatPojos.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ChatPojo mChatPojo = dataSnapshot.getValue(ChatPojo.class);
                    if (mChatPojo != null && (mChatPojo.getReceiver().equals("Zero Time") && mChatPojo.getSender().equals(userId) ||
                            mChatPojo.getReceiver().equals(userId) && mChatPojo.getSender().equals("Zero Time"))) {
                        chatPojos.add(mChatPojo);
                    }
                    adapter = new MessageAdapter(Message.this, chatPojos);
                    binding.messageRecycler.setAdapter(adapter);
                    //binding.messageRecycler.smoothScrollToPosition(chatPojos.size() - 1);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(Objects.requireNonNull(userId))
                .child("Zero Time");

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    chatRef.child("Receiver_ID").setValue("Zero Time");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final DatabaseReference chatRefReceiver = FirebaseDatabase.getInstance().getReference("ChatList")
                .child("Zero Time")
                .child(userId);
        chatRefReceiver.child("Receiver_ID").setValue(userId);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            if (requestCode == GALLERY_PICK && resultCode == RESULT_OK && data != null) {

                Uri imageUri = data.getData();
                try {
                    SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
                    pDialog.setTitleText("جارى رفع الصورة ...");

                    pDialog.setCancelable(false);
                    pDialog.setCanceledOnTouchOutside(false);
                    pDialog.show();

                    DatabaseReference userMessagePush = FirebaseDatabase.getInstance()
                            .getReference("Messages").child(userId).child("Zero Time").push();
                    final String pushID = userMessagePush.getKey();

                    StorageReference filePath = FirebaseStorage.getInstance().getReference("Messages")
                            .child("MessageImages").child(pushID + ".jpg");

                    assert imageUri != null;

                    filePath.putFile(imageUri).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseStorage.getInstance().getReference("Messages")
                                    .child("MessageImages").child(pushID + ".jpg")
                                    .getDownloadUrl().addOnSuccessListener(uri -> {

                                String downloadUrl = uri.toString();
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("Sender", userId);
                                hashMap.put("Receiver", "Zero Time");
                                hashMap.put("Message", downloadUrl);
                                hashMap.put("isSeen", false);
                                hashMap.put("Type", "Image");
                                chatRef.push().setValue(hashMap).addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        pDialog.cancel();
                                        Toasty.success(getApplicationContext(), "تم رفع الصورة بنجاح", Toasty.LENGTH_SHORT, true).show();
                                        binding.messageRecycler.smoothScrollToPosition(chatPojos.size() - 1);
                                    } else {
                                        pDialog.cancel();
                                        Toasty.error(getApplicationContext(), "لقد حدث خطأ ما", Toasty.LENGTH_SHORT, true).show();
                                    }
                                });

                            });
                        }
                    });
                } catch (Exception e) {
                    Toasty.info(this, "first on activity result catch\n" + e.getMessage(), Toasty.LENGTH_LONG,true).show();
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        chatRef.removeEventListener(seenListener);
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

    private boolean checkPermission() {
        // Permission is not granted
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                200);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 200) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);

            } else {
                Toasty.info(getApplicationContext(), "Permission Denied", Toasty.LENGTH_SHORT,true).show();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {
                        showMessageOKCancel(
                                (dialog, which) -> requestPermission());
                    }
                }
            }
        }
    }

    private void showMessageOKCancel(DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(Message.this)
                .setMessage("يجب ان تعطينا اذن الوصول للصور !")
                .setPositiveButton("سماح", okListener)
                .setNegativeButton("رفض", null)
                .create()
                .show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Message.this, Home.class);
        intent.putExtra("UniqueID","Message");
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }
}