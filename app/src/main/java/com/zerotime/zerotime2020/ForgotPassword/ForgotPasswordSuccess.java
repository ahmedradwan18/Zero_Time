package com.zerotime.zerotime2020.ForgotPassword;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;

import androidx.appcompat.app.AppCompatActivity;

import com.zerotime.zerotime2020.R;
import com.zerotime.zerotime2020.User.Login;
import com.zerotime.zerotime2020.databinding.ActivityForgotPasswordSuccessBinding;

public class ForgotPasswordSuccess extends AppCompatActivity {
    AlphaAnimation inAnimation;
    AlphaAnimation outAnimation;
    private ActivityForgotPasswordSuccessBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordSuccessBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //animation
        inAnimation = new AlphaAnimation(0f, 2f);
        outAnimation = new AlphaAnimation(2f, 0f);
        animation();

    }

    @Override
    protected void onStart() {
        super.onStart();

        binding.forgotPasswordSuccessGoToLogin.setOnClickListener(view1 -> goToLogin());

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        goToLogin();
    }

    private void goToLogin() {
        Intent intent = new Intent(ForgotPasswordSuccess.this, Login.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }

    private void animation() {
        inAnimation.setDuration(200);
        outAnimation.setDuration(200);

        // forgot lock image animation
        binding.forgotPasswordSuccessImg.setTranslationX(400f);
        binding.forgotPasswordSuccessImg.setAlpha(0f);
        binding.forgotPasswordSuccessImg.animate().translationX(0f).alpha(1f).setDuration(600).setStartDelay(500).start();
        //---------------------------------------------------------------------
        // primary phone animation
        binding.forgotPasswordSuccessTxt.setTranslationX(600f);
        binding.forgotPasswordSuccessTxt.setAlpha(0f);
        binding.forgotPasswordSuccessTxt.animate().translationX(0f).alpha(1f).setDuration(800).setStartDelay(500).start();
        //---------------------------------------------------------------------
        // secondary phone animation
        binding.forgotPasswordSuccessGoToLogin.setTranslationX(800f);
        binding.forgotPasswordSuccessGoToLogin.setAlpha(0f);
        binding.forgotPasswordSuccessGoToLogin.animate().translationX(0f).alpha(1f).setDuration(1000).setStartDelay(500).start();
        //---------------------------------------------------------------------
    }
}