package com.zerotime.zerotime2020.User;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
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
import com.zerotime.zerotime2020.databinding.ActivitySignUpBinding;

import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class SignUp extends AppCompatActivity {
    SharedPreferences.Editor editor;
    AlphaAnimation inAnimation;
    AlphaAnimation outAnimation;
    private ActivitySignUpBinding binding;
    private DatabaseReference usersRef;
    private HashMap<String, Object> usersMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        detectLanguage();

        // Check Internet State
        if (!haveNetworkConnection()) {
            goToNoConnection();
        }
        checkInternetConnection();
        //-----------------------------------


        //animation
        inAnimation = new AlphaAnimation(0f, 2f);
        outAnimation = new AlphaAnimation(2f, 0f);
        animation();

    }

    private void detectLanguage() {
        String language = Locale.getDefault().getDisplayLanguage();
        if (language.equals("العربية")) {
            binding.signUpUserNameEditTxt.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            binding.signUpUserPasswordEditTxt.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            binding.signUpUserConfirmPasswordEditTxt.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            binding.signUpUserPrimaryPhoneEditTxt.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            binding.signUpUserSecondaryPhoneEditTxt.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            binding.signUpUserAddressEditTxt.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        } else {
            binding.signUpUserNameEditTxt.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
            binding.signUpUserPasswordEditTxt.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
            binding.signUpUserConfirmPasswordEditTxt.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
            binding.signUpUserPrimaryPhoneEditTxt.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
            binding.signUpUserSecondaryPhoneEditTxt.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
            binding.signUpUserAddressEditTxt.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
        }
    }


    private void checkData() {
        //User Name Validation
        if (TextUtils.isEmpty(binding.signUpUserNameEditTxt.getText())) {
            binding.signUpUserNameEditTxt.setError("ادخل الاسم من فضلك !");
            binding.signUpUserNameEditTxt.requestFocus();
            return;
        }
        //User Password Validation
        if (TextUtils.isEmpty(binding.signUpUserPasswordEditTxt.getText())) {
            binding.signUpUserPasswordEditTxt.setError("ادخل كلمة السر من فضلك !");
            binding.signUpUserPasswordEditTxt.requestFocus();
            return;
        }
        if (Objects.requireNonNull(binding.signUpUserPasswordEditTxt.getText()).length() < 8) {
            binding.signUpUserPasswordEditTxt.setError("كلمة السر يجب ان تكون اكثر من او تساوي 7 حروف او ارقام !");
            binding.signUpUserPasswordEditTxt.requestFocus();
            return;
        }
        //User Confirm Password Validation
        if (TextUtils.isEmpty(binding.signUpUserConfirmPasswordEditTxt.getText())) {
            binding.signUpUserConfirmPasswordEditTxt.setError("ادخل كلمة السر من فضلك !");
            binding.signUpUserConfirmPasswordEditTxt.requestFocus();
            return;
        }
        if (Objects.requireNonNull(binding.signUpUserConfirmPasswordEditTxt.getText()).length() < 8) {
            binding.signUpUserConfirmPasswordEditTxt.setError("كلمة السر يجب ان تكون اكثر من او تساوي 7 حروف او ارقام !");
            binding.signUpUserConfirmPasswordEditTxt.requestFocus();
            return;
        }
        //password and confirm password validation
        String password = binding.signUpUserPasswordEditTxt.getText().toString();
        String confirmPassword = binding.signUpUserConfirmPasswordEditTxt.getText().toString();
        if (!password.equals(confirmPassword)) {
            binding.signUpUserConfirmPasswordEditTxt.setError("كلمتا السر غير متطابقتين !");
            binding.signUpUserConfirmPasswordEditTxt.requestFocus();
            return;
        }
        //Primary Phone Validation
        if (TextUtils.isEmpty(binding.signUpUserPrimaryPhoneEditTxt.getText())) {
            binding.signUpUserPrimaryPhoneEditTxt.setError("ادخل رقم الهاتف الاول من فضلك !");
            binding.signUpUserPrimaryPhoneEditTxt.requestFocus();
            return;
        }
        if (Objects.requireNonNull(binding.signUpUserPrimaryPhoneEditTxt.getText()).length() != 11) {
            binding.signUpUserPrimaryPhoneEditTxt.setError("رقم الهاتف يجب ان يتكون من 11 رقم فقط !");
            binding.signUpUserPrimaryPhoneEditTxt.requestFocus();
            return;
        }


        if (!binding.signUpUserPrimaryPhoneEditTxt.getText().toString().startsWith("010") &&
                !binding.signUpUserPrimaryPhoneEditTxt.getText().toString().startsWith("011") &&
                !binding.signUpUserPrimaryPhoneEditTxt.getText().toString().startsWith("012") &&
                !binding.signUpUserPrimaryPhoneEditTxt.getText().toString().startsWith("015")) {
            binding.signUpUserPrimaryPhoneEditTxt.setError("رقم الهاتف يجب ان يكون تابع لاحدى شركات المحمول المصرية !");
            binding.signUpUserPrimaryPhoneEditTxt.requestFocus();
            return;
        }

        //Secondary Phone Validation
        if (TextUtils.isEmpty(binding.signUpUserSecondaryPhoneEditTxt.getText())) {
            binding.signUpUserSecondaryPhoneEditTxt.setError("ادخل رقم الهاتف الثانى من فضلك !");
            binding.signUpUserSecondaryPhoneEditTxt.requestFocus();
            return;
        }
        if (Objects.requireNonNull(binding.signUpUserSecondaryPhoneEditTxt.getText()).length() != 11) {
            binding.signUpUserSecondaryPhoneEditTxt.setError("رقم الهاتف يجب ان يتكون من 11 رقم فقط !");
            binding.signUpUserSecondaryPhoneEditTxt.requestFocus();
            return;
        }


        if (!binding.signUpUserSecondaryPhoneEditTxt.getText().toString().startsWith("010") &&
                !binding.signUpUserSecondaryPhoneEditTxt.getText().toString().startsWith("011") &&
                !binding.signUpUserSecondaryPhoneEditTxt.getText().toString().startsWith("012") &&
                !binding.signUpUserSecondaryPhoneEditTxt.getText().toString().startsWith("015")) {
            binding.signUpUserSecondaryPhoneEditTxt.setError("رقم الهاتف يجب ان يكون تابع لاحدى شركات المحمول المصرية !");
            binding.signUpUserSecondaryPhoneEditTxt.requestFocus();
            return;
        }

        //different numbers validation
        String primaryPhone = binding.signUpUserPrimaryPhoneEditTxt.getText().toString();
        String secondaryPhone = binding.signUpUserSecondaryPhoneEditTxt.getText().toString();
        if (primaryPhone.equals(secondaryPhone)) {
            binding.signUpUserSecondaryPhoneEditTxt.setError("من فضلك قم باختيار رقمين مختلفين !");
            binding.signUpUserSecondaryPhoneEditTxt.requestFocus();
            return;
        }
        //User Address Validation
        if (TextUtils.isEmpty(binding.signUpUserAddressEditTxt.getText())) {
            binding.signUpUserAddressEditTxt.setError("ادخل العنوان بالتفصيل من فضلك !");
            binding.signUpUserAddressEditTxt.requestFocus();
            return;
        }

        createNewUser();
    }

    private void createNewUser() {
        //check internet
        if (!haveNetworkConnection()) {
            Toasty.error(this, "انت لست متصلاً", Toasty.LENGTH_SHORT, true).show();
            return;
        }
        editor.putString("UserType", "User");
        editor.apply();

        //Progress Bar
        binding.signUpProgressBarHolder.setAnimation(inAnimation);
        binding.signUpProgressBarHolder.setVisibility(View.VISIBLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child("Users").orderByChild("UserPrimaryPhone")
                .equalTo(Objects.requireNonNull(binding.signUpUserPrimaryPhoneEditTxt.getText()).toString());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    binding.signUpUserPrimaryPhoneEditTxt.setError("عذرا لقد تم التسجيل بهذا الهاتف من قبل..");
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    binding.signUpProgressBarHolder.setVisibility(View.GONE);

                } else {

                    usersMap.put("UserName", Objects.requireNonNull(binding.signUpUserNameEditTxt.getText()).toString());
                    usersMap.put("UserPrimaryPhone", Objects.requireNonNull(binding.signUpUserPrimaryPhoneEditTxt.getText()).toString());
                    usersMap.put("UserSecondaryPhone", Objects.requireNonNull(binding.signUpUserSecondaryPhoneEditTxt.getText()).toString());
                    usersMap.put("UserPassword", Objects.requireNonNull(binding.signUpUserPasswordEditTxt.getText()).toString());
                    usersMap.put("UserAddress", Objects.requireNonNull(binding.signUpUserAddressEditTxt.getText()).toString());
                    usersMap.put("UserId", Objects.requireNonNull(binding.signUpUserPrimaryPhoneEditTxt.getText()).toString());

                    usersRef.child(binding.signUpUserPrimaryPhoneEditTxt.getText().toString())
                            .setValue(usersMap).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            //clear progress bar
                            binding.signUpProgressBarHolder.setAnimation(outAnimation);
                            binding.signUpProgressBarHolder.setVisibility(View.GONE);

                            Toasty.success(SignUp.this.getApplicationContext(),
                                    "تم تسجيل الحساب بنجاح",
                                    Toasty.LENGTH_SHORT,
                                    true).show();

                            editor.putString("isLogged", Objects.requireNonNull(binding.signUpUserPrimaryPhoneEditTxt.getText()).toString());
                            editor.apply();

                            goToHome();
                        } else {
                            //clear progress bar
                            binding.signUpProgressBarHolder.setAnimation(outAnimation);
                            binding.signUpProgressBarHolder.setVisibility(View.GONE);

                            Toasty.error(SignUp.this.getApplicationContext(),
                                    Objects.requireNonNull(Objects.requireNonNull(task.getException()).getMessage()),
                                    Toasty.LENGTH_SHORT,
                                    true).show();
                        }
                    });


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


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

    private void goToLogin() {
        Intent intent = new Intent(SignUp.this, Login.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        this.finish();
    }
    private void goToHome() {
        Intent intent = new Intent(SignUp.this, Home.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        this.finish();
    }

    private void goToNoConnection() {
        Intent intent = new Intent(SignUp.this, No_Internet_Connection.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        this.finish();
    }

    private void animation() {
        inAnimation.setDuration(200);
        outAnimation.setDuration(200);

        // app logo animation
        binding.signUpAppLogoImageView.setTranslationX(400f);
        binding.signUpAppLogoImageView.setAlpha(0f);
        binding.signUpAppLogoImageView.animate().translationX(0f).alpha(1f).setDuration(600).setStartDelay(500).start();
        //---------------------------------------------------------------------
        // user name animation
        binding.signUpUserNameEditTxt.setTranslationX(600f);
        binding.signUpUserNameEditTxt.setAlpha(0f);
        binding.signUpUserNameEditTxt.animate().translationX(0f).alpha(1f).setDuration(800).setStartDelay(500).start();
        //---------------------------------------------------------------------
        // user password animation
        binding.signUpUserPasswordEditTxt.setTranslationX(800f);
        binding.signUpUserPasswordEditTxt.setAlpha(0f);
        binding.signUpUserPasswordEditTxt.animate().translationX(0f).alpha(1f).setDuration(1000).setStartDelay(500).start();
        //---------------------------------------------------------------------
        // user confirm password animation
        binding.signUpUserConfirmPasswordEditTxt.setTranslationX(1000f);
        binding.signUpUserConfirmPasswordEditTxt.setAlpha(0f);
        binding.signUpUserConfirmPasswordEditTxt.animate().translationX(0f).alpha(1f).setDuration(1200).setStartDelay(500).start();
        //---------------------------------------------------------------------
        // user primary phone animation
        binding.signUpUserPrimaryPhoneEditTxt.setTranslationX(1200f);
        binding.signUpUserPrimaryPhoneEditTxt.setAlpha(0f);
        binding.signUpUserPrimaryPhoneEditTxt.animate().translationX(0f).alpha(1f).setDuration(1400).setStartDelay(500).start();
        //---------------------------------------------------------------------
        // user secondary phone animation
        binding.signUpUserSecondaryPhoneEditTxt.setTranslationX(1400f);
        binding.signUpUserSecondaryPhoneEditTxt.setAlpha(0f);
        binding.signUpUserSecondaryPhoneEditTxt.animate().translationX(0f).alpha(1f).setDuration(1600).setStartDelay(500).start();
        //---------------------------------------------------------------------
        // user address animation
        binding.signUpUserAddressEditTxt.setTranslationX(1600f);
        binding.signUpUserAddressEditTxt.setAlpha(0f);
        binding.signUpUserAddressEditTxt.animate().translationX(0f).alpha(1f).setDuration(1800).setStartDelay(500).start();
        //---------------------------------------------------------------------
        // user sign up button animation
        binding.signUpSignUpBtn.setTranslationX(1800f);
        binding.signUpSignUpBtn.setAlpha(0f);
        binding.signUpSignUpBtn.animate().translationX(0f).alpha(1f).setDuration(2000).setStartDelay(500).start();
        //---------------------------------------------------------------------
        // user login text animation
        binding.signUpLoginTextView.setTranslationX(2000);
        binding.signUpLoginTextView.setAlpha(0f);
        binding.signUpLoginTextView.animate().translationX(0f).alpha(1f).setDuration(2200).setStartDelay(500).start();
        //---------------------------------------------------------------------

    }

    @Override
    protected void onStart() {
        super.onStart();

        //Check Internet State
        if (!haveNetworkConnection()) {
            Intent i = new Intent(SignUp.this, No_Internet_Connection.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra("UniqueID", "SignUp");
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        }
        checkInternetConnection();
        //---------------------------------------
        editor = getSharedPreferences("UserState", MODE_PRIVATE).edit();
        editor.putString("isLogged", "null");
        editor.apply();

        usersRef = FirebaseDatabase.getInstance().getReference("Users");

        usersMap = new HashMap<>();

        //Sign In Text
        binding.signUpLoginTextView.setOnClickListener(view1 -> goToLogin());
        //Sign Up Button
        binding.signUpSignUpBtn.setOnClickListener(view12 -> checkData());

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}