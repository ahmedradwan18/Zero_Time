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
import androidx.room.Room;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zerotime.zerotime2020.Receiver.MyBroadCast;
import com.zerotime.zerotime2020.No_Internet_Connection;
import com.zerotime.zerotime2020.R;
import com.zerotime.zerotime2020.Room.Data.UserDao;
import com.zerotime.zerotime2020.Room.Model.Complaint;
import com.zerotime.zerotime2020.Room.UserDataBase;
import com.zerotime.zerotime2020.databinding.UserFragmentComplaintsBinding;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;
import io.reactivex.CompletableObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static android.content.Context.MODE_PRIVATE;
import static com.zerotime.zerotime2020.Room.Data.UserDao.MIGRATION_1_2;


public class ComplaintsFragment extends Fragment {

    HashMap<String, String> complaintMap;
    Context context;
    View view;
    UserDao userDao;
    String name;
    SharedPreferences preferences;
    String userPhone, userComplaint;
    AlphaAnimation inAnimation;
    AlphaAnimation outAnimation;
    private UserFragmentComplaintsBinding binding;
    private DatabaseReference usersRef, complaintsRef;
    SweetAlertDialog pDialog;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = UserFragmentComplaintsBinding.inflate(getLayoutInflater());
        view = binding.getRoot();
        context = container.getContext();


        return view;
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

        //Room DB
        userDao = Room.databaseBuilder(context, UserDataBase.class, "Complaint")
                .allowMainThreadQueries().addMigrations(MIGRATION_1_2)
                .build().getUserDao();

        //Firebase
        preferences = context.getSharedPreferences("UserState", MODE_PRIVATE);
        userPhone = preferences.getString("isLogged", "");

        usersRef = FirebaseDatabase.getInstance().getReference("Users");
        complaintsRef = FirebaseDatabase.getInstance().getReference("Complaints");
        complaintMap = new HashMap<>();

        //animation
        inAnimation = new AlphaAnimation(0f, 2f);
        outAnimation = new AlphaAnimation(2f, 0f);

    }

    @Override
    public void onResume() {
        super.onResume();

        Objects.requireNonNull(getActivity()).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        binding.complaintsFragmentComplaintEditText.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                binding.complaintsFragmentComplaintEditText.clearFocus();
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
        //Send Complaint Button
        binding.complaintsFragmentSendComplaintBtn.setOnClickListener(view1 -> {

            if (TextUtils.isEmpty(binding.complaintsFragmentComplaintEditText.getText())) {
                binding.complaintsFragmentComplaintEditText.setError("من فضلك قم بادخال شكوتك");
                binding.complaintsFragmentComplaintEditText.requestFocus();

            } else {

                pDialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE);
                pDialog.setTitleText("هل انت متأكد ؟")
                        .setConfirmText("نعم")
                        .setConfirmClickListener(view5 -> {
                            pDialog.cancel();
                            //Progress Bar
                            binding.complaintProgressBarHolder.setAnimation(inAnimation);
                            binding.complaintProgressBarHolder.setVisibility(View.VISIBLE);
                            ((Activity) context).getWindow().setFlags(
                                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                            @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm:ss");
                            String currentTime = df.format(Calendar.getInstance().getTime());

                            usersRef.child(userPhone).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    name = snapshot.child("UserName").getValue(String.class);
                                    complaintMap.put("UserName", name);
                                    complaintMap.put("UserPhone", userPhone);
                                    complaintMap.put("ComplaintDate", currentTime);
                                    complaintMap.put("Complaint", Objects.requireNonNull(binding.complaintsFragmentComplaintEditText.getText()).toString());

                                    complaintsRef.child(currentTime).setValue(complaintMap)
                                            .addOnSuccessListener(aVoid -> {
                                                //clear progress bar
                                                binding.complaintProgressBarHolder.setAnimation(outAnimation);
                                                binding.complaintProgressBarHolder.setVisibility(View.GONE);
                                                ((Activity) context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                                Toasty.success(context,
                                                        "تم ارسال الشكوى بنجاح سوف نرد عليك قريباً",
                                                        Toasty.LENGTH_LONG,
                                                        true).show();
                                                binding.complaintsFragmentComplaintEditText.setText("");

                                            }).addOnFailureListener(e -> {
                                        //clear progress bar
                                        binding.complaintProgressBarHolder.setAnimation(outAnimation);
                                        binding.complaintProgressBarHolder.setVisibility(View.GONE);
                                        ((Activity) context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                        Toasty.error(context,
                                                "لقد حدث خطأ ما برجاء المحاولة لاحقاً",
                                                Toasty.LENGTH_SHORT,
                                                true).show();
                                    });

                                    userComplaint = Objects.requireNonNull(binding.complaintsFragmentComplaintEditText.getText()).toString();
                                    Complaint complaint = new Complaint(userPhone, userComplaint, currentTime, name);
                                    userDao.insertComplaint(complaint).subscribeOn(Schedulers.computation())
                                            .subscribe(new CompletableObserver() {
                                                @Override
                                                public void onSubscribe(Disposable d) {

                                                }

                                                @Override
                                                public void onComplete() {

                                                }

                                                @Override
                                                public void onError(Throwable e) {

                                                }
                                            });


                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        })
                        .setCancelText("التراجع")
                        .setCancelClickListener(view5 -> pDialog.cancel());


                pDialog.setCancelable(false);
                pDialog.setCanceledOnTouchOutside(false);
                pDialog.show();

            }
        });
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
}