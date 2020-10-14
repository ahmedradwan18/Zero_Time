package com.zerotime.zerotime2020.Moderator;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.zerotime.zerotime2020.Receiver.MyBroadCast;
import com.zerotime.zerotime2020.No_Internet_Connection;
import com.zerotime.zerotime2020.R;
import com.zerotime.zerotime2020.databinding.ModeratorActivityAddClerkBinding;

import java.util.HashMap;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class ModeratorAddClerk extends AppCompatActivity {
    AlphaAnimation inAnimation;
    AlphaAnimation outAnimation;
    String phone;
    boolean temp;
    int tmp;
    private ModeratorActivityAddClerkBinding binding;
    private DatabaseReference clerksRef;
    private HashMap<String, String> clerksMap;
    private String hasVehicle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //view binding
        binding = ModeratorActivityAddClerkBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //animation
        inAnimation = new AlphaAnimation(0f, 2f);
        outAnimation = new AlphaAnimation(2f, 0f);

        // Check Internet State
        if (!haveNetworkConnection()) {
            Intent i = new Intent(ModeratorAddClerk.this, No_Internet_Connection.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        }
        checkInternetConnection();
        //-----------------------------------

        //Firebase Database Reference initialization
        clerksRef = FirebaseDatabase.getInstance().getReference("Clerks");
        clerksMap = new HashMap<>();

        //Add Clerk Button
        binding.ModeratorAddClerkAddBtn.setOnClickListener(view1 -> addClerk());
        tmp = 0;
    }


    public void addClerk() {
        try {
            temp = false;
            //Clerk Name Validation
            if (TextUtils.isEmpty(binding.ModeratorAddClerkNameEdt.getText())) {
                binding.ModeratorAddClerkNameEdt.setError("من فضلك قم بادخال اسم المندوب !");
                binding.ModeratorAddClerkNameEdt.requestFocus();
                return;
            }
            //Primary Phone Validation
            if (TextUtils.isEmpty(binding.ModeratorAddClerkPhone1Edt.getText())) {
                binding.ModeratorAddClerkPhone1Edt.setError("ادخل رقم الهاتف الاول من فضلك !");
                binding.ModeratorAddClerkPhone1Edt.requestFocus();
                return;
            }
            if (Objects.requireNonNull(binding.ModeratorAddClerkPhone1Edt.getText()).length() != 11) {
                binding.ModeratorAddClerkPhone1Edt.setError("رقم الهاتف يجب ان يتكون من 11 رقم فقط !");
                binding.ModeratorAddClerkPhone1Edt.requestFocus();
                return;
            }



            if (!binding.ModeratorAddClerkPhone1Edt.getText().toString().startsWith("01")) {
                binding.ModeratorAddClerkPhone1Edt.setError("رقم الهاتف يجب ان يبدأ بـ 01 !");
                binding.ModeratorAddClerkPhone1Edt.requestFocus();
                return;
            }

            if(binding.ModeratorAddClerkPhone1Edt.getText().toString().startsWith("010") ||
                    binding.ModeratorAddClerkPhone1Edt.getText().toString().startsWith("011")||
                    binding.ModeratorAddClerkPhone1Edt.getText().toString().startsWith("012")||
                    binding.ModeratorAddClerkPhone1Edt.getText().toString().startsWith("015")){

            }
            else {
                binding.ModeratorAddClerkPhone1Edt.setError("رقم الهاتف يجب ان يكون تابع لاحدى شركات المحمول المصرية !");
                binding.ModeratorAddClerkPhone1Edt.requestFocus();
                return;
            }

            //Secondary Phone Validation
            if (TextUtils.isEmpty(binding.ModeratorAddClerkPhone2Edt.getText())) {
                binding.ModeratorAddClerkPhone2Edt.setError("ادخل رقم الهاتف الثانى من فضلك !");
                binding.ModeratorAddClerkPhone2Edt.requestFocus();
                return;
            }

            if (Objects.requireNonNull(binding.ModeratorAddClerkPhone2Edt.getText()).length() != 11) {
                binding.ModeratorAddClerkPhone2Edt.setError("رقم الهاتف يجب ان يتكون من 11 رقم فقط !");
                binding.ModeratorAddClerkPhone2Edt.requestFocus();
                return;
            }

            if (!binding.ModeratorAddClerkPhone2Edt.getText().toString().startsWith("01")) {
                binding.ModeratorAddClerkPhone2Edt.setError("رقم الهاتف يجب ان يبدأ بـ 01 !");
                binding.ModeratorAddClerkPhone2Edt.requestFocus();
                return;
            }


            if(binding.ModeratorAddClerkPhone2Edt.getText().toString().startsWith("010") ||
                    binding.ModeratorAddClerkPhone2Edt.getText().toString().startsWith("011")||
                    binding.ModeratorAddClerkPhone2Edt.getText().toString().startsWith("012")||
                    binding.ModeratorAddClerkPhone2Edt.getText().toString().startsWith("015")){

            }

            else {
                binding.ModeratorAddClerkPhone2Edt.setError("رقم الهاتف يجب ان يكون تابع لاحدى شركات المحمول المصرية !");
                binding.ModeratorAddClerkPhone2Edt.requestFocus();
                return;
            }


            //Primary Phone and Secondary Phone difference Validation
            String primaryPhone = binding.ModeratorAddClerkPhone1Edt.getText().toString();
            String secondaryPhone = binding.ModeratorAddClerkPhone2Edt.getText().toString();
            if (primaryPhone.equals(secondaryPhone)) {
                binding.ModeratorAddClerkPhone2Edt.setError("من فضلك قم باختيار رقمين مختلفين !");
                binding.ModeratorAddClerkPhone2Edt.requestFocus();
                return;
            }

            //Clerk Address Validation
            if (TextUtils.isEmpty(binding.ModeratorAddClerkAddressEdt.getText())) {
                binding.ModeratorAddClerkAddressEdt.setError("ادخل العنوان بالتفصيل من فضلك !");
                binding.ModeratorAddClerkAddressEdt.requestFocus();
                return;
            }

            //Clerk Age Validation
            if (TextUtils.isEmpty(binding.ModeratorAddClerkAgeEdt.getText())) {
                binding.ModeratorAddClerkAgeEdt.setError("من فضلك ادخل عمر المندوب !");
                binding.ModeratorAddClerkAgeEdt.requestFocus();
                return;
            }

            //Clerk Vehicle Validation
            if (!binding.radioHave.isChecked() && !binding.radioDonthave.isChecked()) {
                Toasty.warning(this, "من فضلك اخبرنا إن كنت تمتلك طياره ام لا ", Toasty.LENGTH_SHORT, true).show();
                return;
            } else {
                if (binding.radioHave.isChecked()) hasVehicle = "يمتلك طياره";
                if (binding.radioDonthave.isChecked()) hasVehicle = "لا يمتلك طياره";
            }

            // getting data from user
            String name = Objects.requireNonNull(binding.ModeratorAddClerkNameEdt.getText()).toString();
            String phone1 = Objects.requireNonNull(binding.ModeratorAddClerkPhone1Edt.getText()).toString();
            String phone2 = Objects.requireNonNull(binding.ModeratorAddClerkPhone2Edt.getText()).toString();
            int age = Integer.parseInt(Objects.requireNonNull(binding.ModeratorAddClerkAgeEdt.getText()).toString());
            String address = Objects.requireNonNull(binding.ModeratorAddClerkAddressEdt.getText()).toString();


            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            Query query = reference.child("Clerks").orderByChild("ClerkPhone1").equalTo(phone1);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {

                        binding.ModeratorAddClerkPhone1Edt.setError("عذرا لقد تم التسجيل بهذا الهاتف من قبل..");
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                    } else {

                        binding.addClerkProgressBarHolder.setAnimation(inAnimation);
                        binding.addClerkProgressBarHolder.setVisibility(View.VISIBLE);
                        getWindow().setFlags(
                                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                        // full the map
                        clerksMap.put("ClerkName", name);
                        clerksMap.put("ClerkPhone1", phone1);
                        clerksMap.put("ClerkPhone2", phone2);
                        clerksMap.put("ClerkAge", String.valueOf(age));
                        clerksMap.put("ClerkAddress", address);
                        clerksMap.put("hasVehicle", hasVehicle);


                        //send data to firebase
                        clerksRef.child(phone1)
                                .setValue(clerksMap).addOnCompleteListener(task -> {

                            if (task.isSuccessful()) {

                                //clear progress bar
                                binding.addClerkProgressBarHolder.setAnimation(outAnimation);
                                binding.addClerkProgressBarHolder.setVisibility(View.GONE);
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                Toasty.success(getApplicationContext(),
                                        "تمت الإضافه بنجاح ",
                                        Toasty.LENGTH_SHORT,
                                        true)
                                        .show();
                                clearTools();
                                Intent intent = new Intent(ModeratorAddClerk.this, ModeratorHome.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                finish();

                                //Clear Texts from edit text
                            } else {
                                //clear progress bar
                                binding.addClerkProgressBarHolder.setAnimation(outAnimation);
                                binding.addClerkProgressBarHolder.setVisibility(View.GONE);
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                Toasty.error(getApplicationContext(),
                                        Objects.requireNonNull(Objects.requireNonNull(task.getException()).getMessage()),
                                        Toasty.LENGTH_SHORT,
                                        true)
                                        .show();
                            }
                        });

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        } catch (Exception e) {
            Toasty.error(getApplicationContext(),
                    Objects.requireNonNull(e.getMessage()),
                    Toasty.LENGTH_SHORT,
                    true)
                    .show();
        }


    }

    private void clearTools() {
        Objects.requireNonNull(binding.ModeratorAddClerkNameEdt.getText()).clear();
        Objects.requireNonNull(binding.ModeratorAddClerkPhone1Edt.getText()).clear();
        Objects.requireNonNull(binding.ModeratorAddClerkPhone2Edt.getText()).clear();
        Objects.requireNonNull(binding.ModeratorAddClerkAddressEdt.getText()).clear();
        Objects.requireNonNull(binding.ModeratorAddClerkAgeEdt.getText()).clear();

        if (binding.radioHave.isChecked()) {
            binding.radioHave.setChecked(false);
        }
        if (binding.radioDonthave.isChecked()) {
            binding.radioDonthave.setChecked(false);
        }
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
        Intent i = new Intent(ModeratorAddClerk.this, ModeratorHome.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }

}