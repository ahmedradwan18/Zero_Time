package com.zerotime.zerotime2020.ForgotPassword;

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

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zerotime.zerotime2020.Receiver.MyBroadCast;
import com.zerotime.zerotime2020.No_Internet_Connection;
import com.zerotime.zerotime2020.R;
import com.zerotime.zerotime2020.databinding.ActivitySetNewPasswordBinding;

import java.util.Locale;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class SetNewPassword extends AppCompatActivity {
    AlphaAnimation inAnimation;
    AlphaAnimation outAnimation;
    private ActivitySetNewPasswordBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySetNewPasswordBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        detectLanguage();


        //animation
        inAnimation = new AlphaAnimation(0f, 2f);
        outAnimation = new AlphaAnimation(2f, 0f);
        animation();


    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check Internet State
        if (haveNetworkConnection()) {
            goToNoConnection();
        }
        checkInternetConnection();
        //-----------------------------------

        binding.setNewPasswordPasswordUpdatePasswordBtn.setOnClickListener(view1 -> validateData());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        goToForgotPassword();
    }

    private void detectLanguage(){
        String language = Locale.getDefault().getDisplayLanguage();
        if (language.equals("العربية")) {
            binding.setNewPasswordPasswordOne.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            binding.setNewPasswordPasswordTwo.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        } else {
            binding.setNewPasswordPasswordOne.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
            binding.setNewPasswordPasswordTwo.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
        }
    }

    private void validateData() {
        //password Validation
        if (TextUtils.isEmpty(Objects.requireNonNull(binding.setNewPasswordPasswordOne.getText()).toString().trim())) {
            binding.setNewPasswordPasswordOne.setError(" من فضلك ادخل كلمه المرور الجديده !");
            binding.setNewPasswordPasswordOne.requestFocus();
            return;
        }
        if (Objects.requireNonNull(binding.setNewPasswordPasswordOne.getText()).length() < 8) {
            binding.setNewPasswordPasswordOne.setError("كلمة المرور يجب ان تكون اكثر من 7 حروف او ارقام !");
            binding.setNewPasswordPasswordOne.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(Objects.requireNonNull(binding.setNewPasswordPasswordTwo.getText()).toString().trim())) {
            binding.setNewPasswordPasswordTwo.setError(" من فضلك اعد كتابه كلمه المرور الجديده !");
            binding.setNewPasswordPasswordTwo.requestFocus();
            return;
        }
        if (Objects.requireNonNull(binding.setNewPasswordPasswordTwo.getText()).length() < 8) {
            binding.setNewPasswordPasswordTwo.setError("كلمة المرور يجب ان تكون اكثر من 7 حروف او ارقام !");
            binding.setNewPasswordPasswordTwo.requestFocus();
            return;
        }
        if (!binding.setNewPasswordPasswordOne.getText().toString().trim().equals(binding.setNewPasswordPasswordTwo.getText().toString().trim())) {
            Toasty.error(this, "يجب ان تتطابق الكلمتان !", Toasty.LENGTH_SHORT, true).show();
            return;
        }

        updatePassword();

    }

    private void updatePassword() {
        if (haveNetworkConnection()){
            Toasty.error(getApplicationContext(),"انت لست متصلاً !",Toasty.LENGTH_SHORT,true).show();
            return;
        }
        //Progress bar
        binding.setNewPasswordProgress.setAnimation(inAnimation);
        binding.setNewPasswordProgress.setVisibility(View.VISIBLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        String newPassword = Objects.requireNonNull(binding.setNewPasswordPasswordOne.getText()).toString().trim();
        String phone = getIntent().getStringExtra("phoneNo");

        // update data in firebase
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        assert phone != null;
        reference.child(phone).child("UserPassword").setValue(newPassword).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                //clear progress
                binding.setNewPasswordProgress.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                goToSuccess();
            } else {
                //clear progress
                binding.setNewPasswordProgress.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                Toasty.error(SetNewPassword.this, "لقد حدث خطأ ما برجاء المحاولة لاحقاً", Toasty.LENGTH_SHORT, true).show();
            }
        });


    }

    private void goToNoConnection() {
        Intent i = new Intent(SetNewPassword.this, No_Internet_Connection.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("UniqueID", "SetNewPassword");
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }

    private void goToSuccess() {
        Intent i = new Intent(SetNewPassword.this, ForgotPasswordSuccess.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }

    private void goToForgotPassword() {
        Intent i = new Intent(SetNewPassword.this, ForgotPassword.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
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
        return !haveConnectedWifi && !haveConnectedMobile;
    }

    private void checkInternetConnection() {
        MyBroadCast broadCast = new MyBroadCast();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(broadCast, intentFilter);

    }

    private void animation() {
        inAnimation.setDuration(200);
        outAnimation.setDuration(200);

        // image animation
        binding.setNewPasswordImg.setTranslationX(400f);
        binding.setNewPasswordImg.setAlpha(0f);
        binding.setNewPasswordImg.animate().translationX(0f).alpha(1f).setDuration(600).setStartDelay(500).start();
        //---------------------------------------------------------------------
        // password text animation
        binding.setNewPasswordTxt.setTranslationX(600f);
        binding.setNewPasswordTxt.setAlpha(0f);
        binding.setNewPasswordTxt.animate().translationX(0f).alpha(1f).setDuration(800).setStartDelay(500).start();
        //---------------------------------------------------------------------
        // new password animation
        binding.setNewPasswordPasswordOne.setTranslationX(800f);
        binding.setNewPasswordPasswordOne.setAlpha(0f);
        binding.setNewPasswordPasswordOne.animate().translationX(0f).alpha(1f).setDuration(1000).setStartDelay(500).start();
        //---------------------------------------------------------------------
        // confirm password btn animation
        binding.setNewPasswordPasswordTwo.setTranslationX(1000f);
        binding.setNewPasswordPasswordTwo.setAlpha(0f);
        binding.setNewPasswordPasswordTwo.animate().translationX(0f).alpha(1f).setDuration(1200).setStartDelay(500).start();
        //---------------------------------------------------------------------
        // update password btn animation
        binding.setNewPasswordPasswordUpdatePasswordBtn.setTranslationX(1200f);
        binding.setNewPasswordPasswordUpdatePasswordBtn.setAlpha(0f);
        binding.setNewPasswordPasswordUpdatePasswordBtn.animate().translationX(0f).alpha(1f).setDuration(1400).setStartDelay(500).start();
        //---------------------------------------------------------------------
    }
}