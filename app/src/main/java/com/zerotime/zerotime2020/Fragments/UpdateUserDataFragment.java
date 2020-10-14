package com.zerotime.zerotime2020.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zerotime.zerotime2020.Receiver.MyBroadCast;
import com.zerotime.zerotime2020.No_Internet_Connection;
import com.zerotime.zerotime2020.R;
import com.zerotime.zerotime2020.databinding.UserFragmentUpdateUserDataBinding;

import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;

import static android.content.Context.MODE_PRIVATE;


public class UpdateUserDataFragment extends Fragment {

    UserFragmentUpdateUserDataBinding binding;
    DatabaseReference usersRef;
    HashMap<String, Object> usersMap;

    String userPhone;

    AlphaAnimation inAnimation;
    AlphaAnimation outAnimation;
    View view;
    Context context;
    SweetAlertDialog pDialog;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = UserFragmentUpdateUserDataBinding.inflate(inflater, container, false);
        view = binding.getRoot();
        context = container.getContext();

        detectLanguage();

        return view;
    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
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
        context.registerReceiver(broadCast, intentFilter);


    }
    private void detectLanguage(){
        String language = Locale.getDefault().getDisplayLanguage();
        if (language.equals("العربية")){
            binding.updateDataFragmentNameEditTxt.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            binding.updateDataFragmentPasswordEditTxt.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            binding.updateDataFragmentPrimaryPhoneEditTxt.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            binding.updateDataFragmentSecondaryPhoneEditTxt.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            binding.updateDataFragmentAddressEditTxt.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        }else {
            binding.updateDataFragmentNameEditTxt.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
            binding.updateDataFragmentPasswordEditTxt.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
            binding.updateDataFragmentPrimaryPhoneEditTxt.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
            binding.updateDataFragmentSecondaryPhoneEditTxt.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
            binding.updateDataFragmentAddressEditTxt.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
        }
    }

    private void checkData() {
        //User Name Validation
        if (TextUtils.isEmpty(binding.updateDataFragmentNameEditTxt.getText())) {
            binding.updateDataFragmentNameEditTxt.setError("ادخل الاسم من فضلك !");
            binding.updateDataFragmentNameEditTxt.requestFocus();
            return;
        }
        //User Password Validation
        if (TextUtils.isEmpty(binding.updateDataFragmentPasswordEditTxt.getText())) {
            binding.updateDataFragmentPasswordEditTxt.setError("ادخل كلمة السر من فضلك !");
            binding.updateDataFragmentPasswordEditTxt.requestFocus();
            return;
        }
        if (Objects.requireNonNull(binding.updateDataFragmentPasswordEditTxt.getText()).length() < 8) {
            binding.updateDataFragmentPasswordEditTxt.setError("كلمة السر يجب ان تكون اكثر من 8 حروف او ارقام !");
            binding.updateDataFragmentPasswordEditTxt.requestFocus();
            return;
        }
        //Primary Phone Validation
        if (TextUtils.isEmpty(binding.updateDataFragmentPrimaryPhoneEditTxt.getText())) {
            binding.updateDataFragmentPrimaryPhoneEditTxt.setError("ادخل رقم الهاتف الاول من فضلك !");
            binding.updateDataFragmentPrimaryPhoneEditTxt.requestFocus();
            return;
        }
        if (Objects.requireNonNull(binding.updateDataFragmentPrimaryPhoneEditTxt.getText()).length() != 11) {
            binding.updateDataFragmentPrimaryPhoneEditTxt.setError("رقم الهاتف يجب ان يتكون من 11 رقم فقط !");
            binding.updateDataFragmentPrimaryPhoneEditTxt.requestFocus();
            return;
        }
        if (!binding.updateDataFragmentPrimaryPhoneEditTxt.getText().toString().startsWith("01")) {
            binding.updateDataFragmentPrimaryPhoneEditTxt.setError("رقم الهاتف يجب ان يبدأ بـ 01 !");
            binding.updateDataFragmentPrimaryPhoneEditTxt.requestFocus();
            return;
        }
        if (!binding.updateDataFragmentPrimaryPhoneEditTxt.getText().toString().startsWith("010") &&
                !binding.updateDataFragmentPrimaryPhoneEditTxt.getText().toString().startsWith("011") &&
                !binding.updateDataFragmentPrimaryPhoneEditTxt.getText().toString().startsWith("012") &&
                !binding.updateDataFragmentPrimaryPhoneEditTxt.getText().toString().startsWith("015")) {
            binding.updateDataFragmentPrimaryPhoneEditTxt.setError("رقم الهاتف يجب ان يكون تابع لاحدى شركات المحمول المصرية !");
            binding.updateDataFragmentPrimaryPhoneEditTxt.requestFocus();
            return;
        }

        //Secondary Phone Validation
        if (TextUtils.isEmpty(binding.updateDataFragmentSecondaryPhoneEditTxt.getText())) {
            binding.updateDataFragmentSecondaryPhoneEditTxt.setError("ادخل رقم الهاتف الثانى من فضلك !");
            binding.updateDataFragmentSecondaryPhoneEditTxt.requestFocus();
            return;
        }
        if (Objects.requireNonNull(binding.updateDataFragmentSecondaryPhoneEditTxt.getText()).length() != 11) {
            binding.updateDataFragmentSecondaryPhoneEditTxt.setError("رقم الهاتف يجب ان يتكون من 11 رقم فقط !");
            binding.updateDataFragmentSecondaryPhoneEditTxt.requestFocus();
            return;
        }
        if (!binding.updateDataFragmentSecondaryPhoneEditTxt.getText().toString().startsWith("01")) {
            binding.updateDataFragmentSecondaryPhoneEditTxt.setError("رقم الهاتف يجب ان يبدأ بـ 01 !");
            binding.updateDataFragmentSecondaryPhoneEditTxt.requestFocus();
            return;
        }
        if (!binding.updateDataFragmentSecondaryPhoneEditTxt.getText().toString().startsWith("010") &&
                !binding.updateDataFragmentSecondaryPhoneEditTxt.getText().toString().startsWith("011") &&
                !binding.updateDataFragmentSecondaryPhoneEditTxt.getText().toString().startsWith("012") &&
                !binding.updateDataFragmentSecondaryPhoneEditTxt.getText().toString().startsWith("015")) {
            binding.updateDataFragmentSecondaryPhoneEditTxt.setError("رقم الهاتف يجب ان يكون تابع لاحدى شركات المحمول المصرية !");
            binding.updateDataFragmentSecondaryPhoneEditTxt.requestFocus();
            return;
        }

        String primaryPhone = binding.updateDataFragmentPrimaryPhoneEditTxt.getText().toString();
        String secondaryPhone = binding.updateDataFragmentSecondaryPhoneEditTxt.getText().toString();
        if (primaryPhone.equals(secondaryPhone)) {
            Toasty.warning(context.getApplicationContext(), "من فضلك ادخل رقمين مختلفين !", Toasty.LENGTH_SHORT, true).show();
            return;
        }
        //User Address Validation
        if (TextUtils.isEmpty(binding.updateDataFragmentAddressEditTxt.getText())) {
            binding.updateDataFragmentAddressEditTxt.setError("ادخل العنوان بالتفصيل من فضلك !");
            binding.updateDataFragmentAddressEditTxt.requestFocus();
            return;
        }

        updateData();
    }

    private void updateData() {
        if (!haveNetworkConnection()){
            Toasty.error(context.getApplicationContext(),"انت لست متصلاً",Toasty.LENGTH_SHORT,true).show();
            return;
        }

        pDialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE);
        pDialog.setTitleText("هل تريد تعديل البيانات ؟")
                .setConfirmText("نعم")
                .setConfirmClickListener(view -> {
                    pDialog.cancel();
                    //Progress Bar
                    binding.updateDataFragmentProgressBarHolder.setAnimation(inAnimation);
                    binding.updateDataFragmentProgressBarHolder.setVisibility(View.VISIBLE);
                    Objects.requireNonNull(getActivity()).getWindow().setFlags(
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                    usersRef.child(userPhone).child("UserName").setValue(Objects.requireNonNull(binding.updateDataFragmentNameEditTxt.getText()).toString());
                    usersRef.child(userPhone).child("UserPassword").setValue(Objects.requireNonNull(binding.updateDataFragmentPasswordEditTxt.getText()).toString());
                    usersRef.child(userPhone).child("UserSecondaryPhone").setValue(Objects.requireNonNull(binding.updateDataFragmentSecondaryPhoneEditTxt.getText()).toString());
                    usersRef.child(userPhone).child("UserAddress").setValue(Objects.requireNonNull(binding.updateDataFragmentAddressEditTxt.getText()).toString())
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    //clear progress bar
                                    binding.updateDataFragmentProgressBarHolder.setAnimation(outAnimation);
                                    binding.updateDataFragmentProgressBarHolder.setVisibility(View.GONE);
                                    Objects.requireNonNull(getActivity()).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                    Toasty.success(context, "تم تعديل البيانات بنجاح", Toasty.LENGTH_SHORT, true).show();
                                } else {
                                    //clear progress bar
                                    binding.updateDataFragmentProgressBarHolder.setAnimation(outAnimation);
                                    binding.updateDataFragmentProgressBarHolder.setVisibility(View.GONE);
                                    Objects.requireNonNull(getActivity()).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                    Toasty.error(context, Objects.requireNonNull(Objects.requireNonNull(task.getException()).getMessage()), Toasty.LENGTH_SHORT, true).show();
                                }
                            });

                })
                .setCancelText("التراجع")
                .setCancelClickListener(view -> pDialog.cancel());


        pDialog.setCancelable(false);
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.show();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Check Internet State
        if (!haveNetworkConnection()) {
            Intent i = new Intent(context, No_Internet_Connection.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
            ((Activity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            ((Activity) context).finish();
        }
        checkInternetConnection();
        //-----------------------------------

        usersMap = new HashMap<>();
        binding.updateDataFragmentPrimaryPhoneEditTxt.setEnabled(false);
        //animation
        inAnimation = new AlphaAnimation(0f, 2f);
        outAnimation = new AlphaAnimation(2f, 0f);

        usersRef = FirebaseDatabase.getInstance().getReference("Users");
        SharedPreferences prefs = Objects.requireNonNull(getContext()).getSharedPreferences("UserState", MODE_PRIVATE);
        userPhone = prefs.getString("isLogged", "No phone defined");//"No name defined" is the default value.

        //Progress Bar
        binding.updateDataFragmentProgressBarHolder.setAnimation(inAnimation);
        binding.updateDataFragmentProgressBarHolder.setVisibility(View.VISIBLE);
        Objects.requireNonNull(getActivity()).getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);


    }

    @Override
    public void onResume() {
        super.onResume();
        Objects.requireNonNull(getActivity()).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        binding.updateDataFragmentNameEditTxt.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                binding.updateDataFragmentNameEditTxt.clearFocus();
                view.requestFocus();

            }
            return false;
        });
        binding.updateDataFragmentPasswordEditTxt.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                binding.updateDataFragmentPasswordEditTxt.clearFocus();
                view.requestFocus();

            }
            return false;
        });
        binding.updateDataFragmentPrimaryPhoneEditTxt.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                binding.updateDataFragmentPrimaryPhoneEditTxt.clearFocus();
                view.requestFocus();

            }
            return false;
        });
        binding.updateDataFragmentSecondaryPhoneEditTxt.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                binding.updateDataFragmentSecondaryPhoneEditTxt.clearFocus();
                view.requestFocus();

            }
            return false;
        });
        binding.updateDataFragmentAddressEditTxt.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                binding.updateDataFragmentAddressEditTxt.clearFocus();
                view.requestFocus();

            }
            return false;
        });

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener((v, keyCode, event) -> {

            if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                assert getFragmentManager() != null;
                getFragmentManager().popBackStackImmediate();
                return true;
            }

            return false;
        });
        usersRef.child(Objects.requireNonNull(userPhone)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot.hasChildren()) {
                        binding.updateDataFragmentProgress.setVisibility(View.GONE);
                        binding.scrollLayout.setVisibility(View.VISIBLE);

                        binding.updateDataFragmentNameEditTxt
                                .setText(snapshot.child("UserName").getValue(String.class));
                        binding.updateDataFragmentPasswordEditTxt
                                .setText(snapshot.child("UserPassword").getValue(String.class));
                        binding.updateDataFragmentPrimaryPhoneEditTxt
                                .setText(snapshot.child("UserPrimaryPhone").getValue(String.class));
                        binding.updateDataFragmentSecondaryPhoneEditTxt
                                .setText(snapshot.child("UserSecondaryPhone").getValue(String.class));
                        binding.updateDataFragmentAddressEditTxt
                                .setText(snapshot.child("UserAddress").getValue(String.class));

                        //clear progress bar
                        binding.updateDataFragmentProgressBarHolder.setAnimation(outAnimation);
                        binding.updateDataFragmentProgressBarHolder.setVisibility(View.GONE);
                        Objects.requireNonNull(getActivity()).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.updateDataFragmentUpdateBtn.setOnClickListener(view1 -> checkData());


    }

}