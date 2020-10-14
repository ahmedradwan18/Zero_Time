package com.zerotime.zerotime2020.Moderator;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zerotime.zerotime2020.Receiver.MyBroadCast;
import com.zerotime.zerotime2020.No_Internet_Connection;
import com.zerotime.zerotime2020.R;
import com.zerotime.zerotime2020.databinding.ModeratorActivityAddOfferBinding;

import java.util.HashMap;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class ModeratorAddOffer extends AppCompatActivity {
    AlphaAnimation inAnimation;
    AlphaAnimation outAnimation;
    private ModeratorActivityAddOfferBinding binding;
    private DatabaseReference offersRef;
    private HashMap<String, String> offersMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ModeratorActivityAddOfferBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // Check Internet State
        if (!haveNetworkConnection()) {
            Intent i = new Intent(ModeratorAddOffer.this, No_Internet_Connection.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        }
        checkInternetConnection();
        //-----------------------------------
        //animation
        inAnimation = new AlphaAnimation(0f, 2f);
        outAnimation = new AlphaAnimation(2f, 0f);

        binding.addOfferProgressBarHolder.setVisibility(View.VISIBLE);

        offersRef = FirebaseDatabase.getInstance().getReference("Offers");

        offersRef.child("Offers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot.hasChildren()) {
                        String firstOffer = snapshot.child("FirstOffer").getValue(String.class);
                        String secondOffer = snapshot.child("SecondOffer").getValue(String.class);
                        String thirdOffer = snapshot.child("ThirdOffer").getValue(String.class);
                        String fourthOffer = snapshot.child("FourthOffer").getValue(String.class);
                        String fifthOffer = snapshot.child("FifthOffer").getValue(String.class);
                        String sixthOffer = snapshot.child("SixthOffer").getValue(String.class);
                        binding.moderatorAddOfferFirstFixedOfferEditText.setText(firstOffer);
                        binding.moderatorAddOfferSecondFixedOfferEditText.setText(secondOffer);
                        binding.moderatorAddOfferThirdFixedOfferEditText.setText(thirdOffer);
                        binding.moderatorAddOfferFourthFixedOfferEditText.setText(fourthOffer);
                        binding.moderatorAddOfferFifthFixedOfferEditText.setText(fifthOffer);
                        binding.moderatorAddOfferSixthFixedOfferEditText.setText(sixthOffer);

                        binding.addOfferProgressBarHolder.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.moderatorAddOfferSixthUpdateBtn.setOnClickListener(view1 -> {
            addOffer();

        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(ModeratorAddOffer.this, ModeratorHome.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }

    private void addOffer() {
        if (haveNetworkConnection()){
            //Progress Bar
            binding.addOfferProgressBarHolder.setAnimation(inAnimation);
            binding.addOfferProgressBarHolder.setVisibility(View.VISIBLE);
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            offersMap.put("FirstOffer", Objects.requireNonNull(binding.moderatorAddOfferFirstFixedOfferEditText.getText()).toString());
            offersMap.put("SecondOffer", Objects.requireNonNull(binding.moderatorAddOfferSecondFixedOfferEditText.getText()).toString());
            offersMap.put("ThirdOffer", Objects.requireNonNull(binding.moderatorAddOfferThirdFixedOfferEditText.getText()).toString());
            offersMap.put("FourthOffer", Objects.requireNonNull(binding.moderatorAddOfferFourthFixedOfferEditText.getText()).toString());
            offersMap.put("FifthOffer", Objects.requireNonNull(binding.moderatorAddOfferFifthFixedOfferEditText.getText()).toString());
            offersMap.put("SixthOffer", Objects.requireNonNull(binding.moderatorAddOfferSixthFixedOfferEditText.getText()).toString());
            offersRef.child("Offers").setValue(offersMap).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    new Handler().postDelayed(() -> {
                        binding.addOfferProgressBarHolder.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        Toasty.success(getApplicationContext(), "تم بنجاح ..", Toasty.LENGTH_SHORT, true).show();
                        goToHome();
                    }, 1000);
                } else {
                    binding.addOfferProgressBarHolder.setVisibility(View.GONE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    Toasty.error(getApplicationContext(), "لقد حدث خطأ ما برجاء المحاولة لاحقاً", Toasty.LENGTH_SHORT, true).show();
                }
            });

        }else Toasty.error(getApplicationContext(),"انت لست متصلاً",Toasty.LENGTH_SHORT,true).show();



    }

    private void goToHome() {
        Intent intent = new Intent(ModeratorAddOffer.this, ModeratorHome.class);
        startActivity(intent);
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

    private void checkInternetConnection() {
        MyBroadCast broadCast = new MyBroadCast();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(broadCast, intentFilter);

    }
}
