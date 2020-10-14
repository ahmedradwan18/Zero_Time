package com.zerotime.zerotime2020.Fragments;

import android.annotation.SuppressLint;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zerotime.zerotime2020.Receiver.MyBroadCast;
import com.zerotime.zerotime2020.No_Internet_Connection;
import com.zerotime.zerotime2020.R;
import com.zerotime.zerotime2020.databinding.UserFragmentAddOrderBinding;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

import static android.content.Context.MODE_PRIVATE;


public class AddOrderFragment extends Fragment {

    UserFragmentAddOrderBinding binding;
    DatabaseReference ordersRef, deliveredOrdersCountRef, deliveredOrdersRef;
    String userPhone;
    HashMap<String, String> ordersMap = new HashMap<>();
    Context context;
    View view;
    AlphaAnimation inAnimation;
    AlphaAnimation outAnimation;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = UserFragmentAddOrderBinding.inflate(inflater, container, false);
        view = binding.getRoot();
        context = container.getContext();

        detectLanguage();

        // Check Internet State
        if (!haveNetworkConnection()) {
            Intent i = new Intent(getActivity(), No_Internet_Connection.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
            ((Activity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            ((Activity) context).finish();
        }
        checkInternetConnection();
        //-----------------------------------

        //animation
        inAnimation = new AlphaAnimation(0f, 2f);
        outAnimation = new AlphaAnimation(2f, 0f);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Getting User Phone
        SharedPreferences prefs = Objects.requireNonNull(getContext()).getSharedPreferences("UserState", MODE_PRIVATE);
        userPhone = prefs.getString("isLogged", "No phone defined");
        ordersRef = FirebaseDatabase.getInstance().getReference("PendingOrders");
        deliveredOrdersCountRef = FirebaseDatabase.getInstance().getReference("OrdersCount");
        deliveredOrdersRef = FirebaseDatabase.getInstance().getReference("DeliveredOrders");

        binding.addOrderRequestBtn.setOnClickListener(view1 -> {
            checkData();
        });
    }

    private void detectLanguage(){
        String language = Locale.getDefault().getDisplayLanguage();
        if (language.equals("العربية")){
            binding.addOrderOrderDescriptionEditText.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            binding.addOrderOrderPriceEditText.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            binding.addOrderArrivalDateNotesEditText.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            binding.addOrderReceiverNameEditText.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            binding.addOrderReceiverPrimaryPhoneEditText.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            binding.addOrderReceiverSecondaryPhoneEditText.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            binding.addOrderReceiverAddressEditText.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        }else {
            binding.addOrderOrderDescriptionEditText.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
            binding.addOrderOrderPriceEditText.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
            binding.addOrderArrivalDateNotesEditText.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
            binding.addOrderReceiverNameEditText.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
            binding.addOrderReceiverPrimaryPhoneEditText.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
            binding.addOrderReceiverSecondaryPhoneEditText.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
            binding.addOrderReceiverAddressEditText.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
        }
    }

    private void checkData() {
        //Order Description Validation
        if (TextUtils.isEmpty(binding.addOrderOrderDescriptionEditText.getText())) {
            binding.addOrderOrderDescriptionEditText.setError("من فضلك ادخل وصف الطلب !");
            binding.addOrderOrderDescriptionEditText.requestFocus();
            return;
        }
        //Order Price Validation
        if (TextUtils.isEmpty(binding.addOrderOrderPriceEditText.getText())) {
            binding.addOrderOrderPriceEditText.setError("من فضلك ادخل مبلغ الطلب !");
            binding.addOrderOrderPriceEditText.requestFocus();
            return;
        }
        //Receiver Name Validation
        if (TextUtils.isEmpty(binding.addOrderReceiverNameEditText.getText())) {
            binding.addOrderReceiverNameEditText.setError("من فضلك ادخل اسم المستلم");
            binding.addOrderReceiverNameEditText.requestFocus();
            return;
        }
        //Primary Phone Validation
        if (TextUtils.isEmpty(binding.addOrderReceiverPrimaryPhoneEditText.getText())) {
            binding.addOrderReceiverPrimaryPhoneEditText.setError("ادخل رقم الهاتف الاول للمستلم من فضلك !");
            binding.addOrderReceiverPrimaryPhoneEditText.requestFocus();
            return;
        }
        if (Objects.requireNonNull(binding.addOrderReceiverPrimaryPhoneEditText.getText()).length() != 11) {
            binding.addOrderReceiverPrimaryPhoneEditText.setError("رقم الهاتف يجب ان يتكون من 11 رقم فقط !");
            binding.addOrderReceiverPrimaryPhoneEditText.requestFocus();
            return;
        }
        if (!binding.addOrderReceiverPrimaryPhoneEditText.getText().toString().startsWith("01")) {
            binding.addOrderReceiverPrimaryPhoneEditText.setError("رقم الهاتف يجب ان يبدأ بـ 01 !");
            binding.addOrderReceiverPrimaryPhoneEditText.requestFocus();
            return;
        }

        if (binding.addOrderReceiverPrimaryPhoneEditText.getText().toString().equals(userPhone)) {
            binding.addOrderReceiverPrimaryPhoneEditText.setError("لا يمكن إرسال طلب إلي نفسك");
            binding.addOrderReceiverPrimaryPhoneEditText.requestFocus();
            return;
        }

        //Secondary Phone Validation
        if (TextUtils.isEmpty(binding.addOrderReceiverSecondaryPhoneEditText.getText())) {
            binding.addOrderReceiverSecondaryPhoneEditText.setError("ادخل رقم الهاتف الثانى للمستلم من فضلك !");
            binding.addOrderReceiverSecondaryPhoneEditText.requestFocus();
            return;
        }
        if (Objects.requireNonNull(binding.addOrderReceiverSecondaryPhoneEditText.getText()).length() != 11) {
            binding.addOrderReceiverSecondaryPhoneEditText.setError("رقم الهاتف يجب ان يتكون من 11 رقم فقط !");
            binding.addOrderReceiverSecondaryPhoneEditText.requestFocus();
            return;
        }
        if (!binding.addOrderReceiverSecondaryPhoneEditText.getText().toString().startsWith("01")) {
            binding.addOrderReceiverSecondaryPhoneEditText.setError("رقم الهاتف يجب ان يبدأ بـ 01 !");
            binding.addOrderReceiverSecondaryPhoneEditText.requestFocus();
            return;
        }
        String primaryPhone = binding.addOrderReceiverPrimaryPhoneEditText.getText().toString();
        String secondaryPhone = binding.addOrderReceiverSecondaryPhoneEditText.getText().toString();
        if (primaryPhone.equals(secondaryPhone)) {
            Toasty.error(context, "من فضلك ادخل رقمين مختلفين !", Toasty.LENGTH_SHORT, true).show();
            return;
        }
        //User Address Validation
        if (TextUtils.isEmpty(binding.addOrderReceiverAddressEditText.getText())) {
            binding.addOrderReceiverAddressEditText.setError("ادخل عنوان المستلم بالتفصيل من فضلك !");
            binding.addOrderReceiverAddressEditText.requestFocus();
            return;
        }

        //Order Size Validation
        if (!binding.addOrderBigOrderRadioBtn.isChecked() && !binding.addOrderMediumOrderRadioBtn.isChecked() && !binding.addOrderSmallOrderRadioBtn.isChecked()) {
            Toasty.error(context, "من فضلك قم باختيار حجم الطلب !", Toasty.LENGTH_SHORT, true).show();
            return;
        }
        getOrderSize();
        requestOrder();
    }

    private void getOrderSize() {
        if (binding.addOrderBigOrderRadioBtn.isChecked()) {
            ordersMap.put("OrderSize", "كبير");
        } else if (binding.addOrderMediumOrderRadioBtn.isChecked()) {
            ordersMap.put("OrderSize", "متوسط");
        } else if (binding.addOrderSmallOrderRadioBtn.isChecked()) {
            ordersMap.put("OrderSize", "صغير");
        }

    }

    private void requestOrder() {
        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm:ss");
        String currentTime = df.format(Calendar.getInstance().getTime());

        //Progress Bar
        binding.addOrderProgressBarHolder.setAnimation(inAnimation);
        binding.addOrderProgressBarHolder.setVisibility(View.VISIBLE);
        ((Activity) context).getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        ordersMap.put("OrderDescription", Objects.requireNonNull(binding.addOrderOrderDescriptionEditText.getText()).toString());
        ordersMap.put("ReceiverName", Objects.requireNonNull(binding.addOrderReceiverNameEditText.getText()).toString());
        ordersMap.put("ReceiverPrimaryPhone", Objects.requireNonNull(binding.addOrderReceiverPrimaryPhoneEditText.getText()).toString());
        ordersMap.put("ReceiverSecondaryPhone", Objects.requireNonNull(binding.addOrderReceiverSecondaryPhoneEditText.getText()).toString());
        ordersMap.put("ReceiverAddress", Objects.requireNonNull(binding.addOrderReceiverAddressEditText.getText()).toString());
        ordersMap.put("OrderPrice", Objects.requireNonNull(binding.addOrderOrderPriceEditText.getText()).toString());
        ordersMap.put("OrderDate", currentTime);
        ordersMap.put("OrderState", "لم يتم الاستلام");
        ordersMap.put("UserPrimaryPhone", userPhone);

        if (binding.addOrderArrivalDateNotesEditText.getText() == null || TextUtils.isEmpty(binding.addOrderArrivalDateNotesEditText.getText())) {
            ordersMap.put("ArrivalNotes", "لا توجد");

        } else {
            ordersMap.put("ArrivalNotes", Objects.requireNonNull(binding.addOrderArrivalDateNotesEditText.getText()).toString());
        }
        ordersRef.child(currentTime).setValue(ordersMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    //clear progress bar
                    binding.addOrderProgressBarHolder.setAnimation(outAnimation);
                    binding.addOrderProgressBarHolder.setVisibility(View.GONE);
                    ((Activity) context).getWindow()
                            .clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    Toasty.success(context, "تم ارسال الطلب بنجاح", Toasty.LENGTH_SHORT, true).show();
                    clearTools();
                } else {
                    //clear progress bar
                    binding.addOrderProgressBarHolder.setAnimation(outAnimation);
                    binding.addOrderProgressBarHolder.setVisibility(View.GONE);
                    ((Activity) context).getWindow()
                            .clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                    Toasty.error(context,
                            Objects.requireNonNull(Objects.requireNonNull(task.getException()).getMessage())
                            , Toasty.LENGTH_SHORT, true).show();
                }
            }
        });

    }

    private void clearTools() {
        Objects.requireNonNull(binding.addOrderOrderDescriptionEditText.getText()).clear();
        Objects.requireNonNull(binding.addOrderReceiverNameEditText.getText()).clear();
        Objects.requireNonNull(binding.addOrderReceiverPrimaryPhoneEditText.getText()).clear();
        Objects.requireNonNull(binding.addOrderReceiverSecondaryPhoneEditText.getText()).clear();
        Objects.requireNonNull(binding.addOrderReceiverAddressEditText.getText()).clear();
        Objects.requireNonNull(binding.addOrderOrderPriceEditText.getText()).clear();
        if (!TextUtils.isEmpty(binding.addOrderArrivalDateNotesEditText.getText())) {
            Objects.requireNonNull(binding.addOrderArrivalDateNotesEditText.getText()).clear();
        }
        binding.addOrderSmallOrderRadioBtn.setChecked(false);
        binding.addOrderMediumOrderRadioBtn.setChecked(false);
        binding.addOrderBigOrderRadioBtn.setChecked(false);
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

    @Override
    public void onResume() {
        super.onResume();
        Objects.requireNonNull(getActivity()).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        binding.addOrderOrderDescriptionEditText.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                binding.addOrderOrderDescriptionEditText.clearFocus();
                view.requestFocus();
            }
            return false;
        });
        binding.addOrderOrderPriceEditText.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                binding.addOrderOrderPriceEditText.clearFocus();
                view.requestFocus();

            }
            return false;
        });
        binding.addOrderReceiverAddressEditText.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                binding.addOrderReceiverAddressEditText.clearFocus();
                view.requestFocus();

            }
            return false;
        });
        binding.addOrderReceiverNameEditText.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                binding.addOrderReceiverNameEditText.clearFocus();
                view.requestFocus();

            }
            return false;
        });
        binding.addOrderReceiverPrimaryPhoneEditText.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                binding.addOrderReceiverPrimaryPhoneEditText.clearFocus();
                view.requestFocus();

            }
            return false;
        });
        binding.addOrderReceiverSecondaryPhoneEditText.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                binding.addOrderReceiverSecondaryPhoneEditText.clearFocus();
                view.requestFocus();

            }
            return false;
        });
        binding.addOrderArrivalDateNotesEditText.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                binding.addOrderArrivalDateNotesEditText.clearFocus();
                view.requestFocus();

            }
            return false;
        });
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener((v, keyCode, event) -> {

            if (keyCode == KeyEvent.KEYCODE_BACK) {
                assert getFragmentManager() != null;
                getFragmentManager().popBackStackImmediate();
                return true;
            }

            return false;
        });
    }

}