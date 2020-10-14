package com.zerotime.zerotime2020.Secretary;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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

import com.anstrontechnologies.corehelper.AnstronCoreHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.iceteck.silicompressorr.FileUtils;
import com.iceteck.silicompressorr.SiliCompressor;
import com.zerotime.zerotime2020.Receiver.MyBroadCast;
import com.zerotime.zerotime2020.No_Internet_Connection;
import com.zerotime.zerotime2020.R;
import com.zerotime.zerotime2020.Secretary.Adapters.SecretaryMessageAdapter;
import com.zerotime.zerotime2020.Secretary.Pojos.SecretaryChatPojo;
import com.zerotime.zerotime2020.databinding.SecretaryActivityMessageBinding;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;

public class SecretaryMessage extends AppCompatActivity {
    private static final int GALLERY_PICK = 0;
    SharedPreferences prefs;
    SecretaryMessageAdapter adapter;
    List<SecretaryChatPojo> secretaryChatPojoList;
    DatabaseReference chatRef;
    StorageReference filePath;
    ValueEventListener seenListener;
    String userId, intentFrom;
    Uri imgUri;
    AnstronCoreHelper anstronCoreHelper;
    FirebaseAuth mAuth;
    FirebaseUser user;
    private SecretaryActivityMessageBinding binding;
    SweetAlertDialog uploadDialog, compressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SecretaryActivityMessageBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // Check Internet State
        if (!haveNetworkConnection()) {
            Intent i = new Intent(SecretaryMessage.this, No_Internet_Connection.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        }
        checkInternetConnection();
        //-----------------------------------
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        filePath = FirebaseStorage.getInstance().getReference();

        uploadDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        uploadDialog.setTitleText("جارى رفع الصورة ...");
        uploadDialog.setCancelable(false);
        uploadDialog.setCanceledOnTouchOutside(false);

        compressDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        compressDialog.setTitleText("جارى ضغط الصورة ...");
        compressDialog.setCancelable(false);
        compressDialog.setCanceledOnTouchOutside(false);

        anstronCoreHelper = new AnstronCoreHelper(this);

        binding.nestedScroll.fullScroll(View.FOCUS_DOWN);
        binding.secretaryMessageRecycler.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setStackFromEnd(true);
        binding.secretaryMessageRecycler.setLayoutManager(linearLayoutManager);
        binding.secretaryMessageRecycler.setFocusable(false);
        binding.secretaryMessageRecycler.setNestedScrollingEnabled(false);
        binding.nestedScroll.requestFocus();


        if (getIntent().getStringExtra("UniqueID") != null) {
            if (Objects.requireNonNull(getIntent().getStringExtra("UniqueID")).equals("DisplayChatsAdapter")) {
                intentFrom = "DisplayChatsAdapter";
                userId = getIntent().getStringExtra("UserID");

            }
        }
        prefs = getSharedPreferences("UserState", MODE_PRIVATE);

        if (user == null) {
            signInAnonymously();
        } else {
            binding.secretaryMessageSendBtn.setOnClickListener(view12 -> {
                String msg = Objects.requireNonNull(binding.secretaryMessageWriteMSGEdt.getText()).toString();
                if (!msg.equals("")) {
                    if (intentFrom != null) {
                        sendMessage(userId, msg);
                    }


                } else
                    Toasty.error(SecretaryMessage.this, "لا يمكنك ارسال رسالة فارغة !", Toasty.LENGTH_SHORT, true).show();
                binding.secretaryMessageWriteMSGEdt.setText("");
            });
            binding.secretaryMessageSendImage.setOnClickListener(view1 -> {
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
                /* perform your actions here*/
                binding.secretaryMessageSendBtn.setOnClickListener(view -> {
                    String msg = Objects.requireNonNull(binding.secretaryMessageWriteMSGEdt.getText()).toString();
                    if (!msg.equals("")) {
                        if (intentFrom != null) {
                            sendMessage(userId, msg);
                        }


                    } else
                        Toasty.error(SecretaryMessage.this, "لا يمكنك ارسال رسالة فارغة !", Toasty.LENGTH_SHORT, true).show();
                    binding.secretaryMessageWriteMSGEdt.setText("");
                });
                binding.secretaryMessageSendImage.setOnClickListener(view1 -> {

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
                    SecretaryChatPojo chat = snapshot.getValue(SecretaryChatPojo.class);
                    assert chat != null;
                    if (chat.getReceiver().equals("Zero Time") && chat.getSender().equals(userid)) {
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

    private void sendMessage(String Receiver, String Message) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("Sender", "Zero Time");
        hashMap.put("Receiver", Receiver);
        hashMap.put("Message", Message);
        hashMap.put("isSeen", false);
        hashMap.put("Type", "Text");
        reference.child("Chats").push().setValue(hashMap);

    }

    private void ReadMessages() {
        secretaryChatPojoList = new ArrayList<>();
        chatRef = FirebaseDatabase.getInstance().getReference("Chats");
        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                secretaryChatPojoList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    SecretaryChatPojo mSecretaryChatPojo = dataSnapshot.getValue(SecretaryChatPojo.class);
                    if (mSecretaryChatPojo != null && (mSecretaryChatPojo.getReceiver().equals("Zero Time") && mSecretaryChatPojo.getSender().equals(userId) ||
                            mSecretaryChatPojo.getReceiver().equals(userId) && mSecretaryChatPojo.getSender().equals("Zero Time"))) {
                        secretaryChatPojoList.add(mSecretaryChatPojo);

                    }

                }

                adapter = new SecretaryMessageAdapter(SecretaryMessage.this, secretaryChatPojoList);
                binding.secretaryMessageRecycler.setAdapter(adapter);
                //binding.secretaryMessageRecycler.smoothScrollToPosition(secretaryChatPojoList.size() - 1);

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
    protected void onPause() {
        super.onPause();
        chatRef.removeEventListener(seenListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            if (requestCode == GALLERY_PICK && resultCode == RESULT_OK && data != null) {

                imgUri = data.getData();
                assert imgUri != null;
                try {
                    compressAndUpload();


                    uploadDialog.show();


                    DatabaseReference userMessagePush = FirebaseDatabase.getInstance()
                            .getReference("Messages").child("Zero Time").child(userId).push();
                    final String pushID = userMessagePush.getKey();

                    filePath = FirebaseStorage.getInstance().getReference("Messages")
                            .child("MessageImages").child(pushID + ".jpg");


                    filePath.putFile(imgUri).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseStorage.getInstance().getReference("Messages")
                                    .child("MessageImages").child(pushID + ".jpg")
                                    .getDownloadUrl().addOnSuccessListener(uri -> {

                                String downloadUrl = uri.toString();
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("Sender", "Zero Time");
                                hashMap.put("Receiver", userId);
                                hashMap.put("Message", downloadUrl);
                                hashMap.put("isSeen", false);
                                hashMap.put("Type", "Image");
                                chatRef.push().setValue(hashMap).addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        uploadDialog.cancel();
                                        Toasty.success(getApplicationContext(), "تم رفع الصورة بنجاح", Toasty.LENGTH_SHORT, true).show();
                                        binding.secretaryMessageRecycler.smoothScrollToPosition(secretaryChatPojoList.size() - 1);
                                    } else {
                                        uploadDialog.cancel();
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

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            return false;
        }
        return true;
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
        new AlertDialog.Builder(SecretaryMessage.this)
                .setMessage("يجب ان تعطينا اذن الوصول للصور !")
                .setPositiveButton("سماح", okListener)
                .setNegativeButton("رفض", null)
                .create()
                .show();
    }
    private void compressAndUpload() {
        if (imgUri != null) {

            compressDialog.show();

            File file = new File(SiliCompressor.with(this).
                    compress(FileUtils.getPath(this, imgUri), new File(this.getCacheDir(), "temp")));
            Uri uri = Uri.fromFile(file);
            filePath.child("images/").child(anstronCoreHelper.getFileNameFromUri(uri)).putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        Toasty.success(SecretaryMessage.this, "upload done ..", Toasty.LENGTH_SHORT,true).show();
                    } else {
                        Toasty.error(SecretaryMessage.this, "upload failed..", Toasty.LENGTH_SHORT,true).show();
                    }
                    compressDialog.dismiss();
                    file.delete();

                }
            });

        }

    }
}