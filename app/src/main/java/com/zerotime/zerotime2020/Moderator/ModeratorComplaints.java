package com.zerotime.zerotime2020.Moderator;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zerotime.zerotime2020.Moderator.Adapters.ComplaintAdapter;
import com.zerotime.zerotime2020.Moderator.Pojos.Complaint_Pojo;
import com.zerotime.zerotime2020.Receiver.MyBroadCast;
import com.zerotime.zerotime2020.No_Internet_Connection;
import com.zerotime.zerotime2020.R;
import com.zerotime.zerotime2020.Room.Data.UserDao;
import com.zerotime.zerotime2020.Room.UserDataBase;
import com.zerotime.zerotime2020.databinding.ModeratorActivityComplaintsBinding;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;

public class ModeratorComplaints extends AppCompatActivity {
    private ModeratorActivityComplaintsBinding binding;
    private ArrayList<Complaint_Pojo> complaintList;
    private DatabaseReference complaintsRef;
    // Room DB
    UserDao db;
    UserDataBase dataBase;
    ComplaintAdapter adapter;

    private void layoutAnimation(RecyclerView recyclerView) {

        Context context = recyclerView.getContext();
        LayoutAnimationController animationController =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_slide_right);
        recyclerView.setLayoutAnimation(animationController);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ModeratorActivityComplaintsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        complaintList = new ArrayList<>();

        complaintsRef = FirebaseDatabase.getInstance().getReference("Complaints");

        binding.recyclerComplaints.setAdapter(adapter);
        binding.recyclerComplaints.setItemAnimator(new DefaultItemAnimator());

        // Check Internet State
        if (!haveNetworkConnection()) {
            Intent i = new Intent(ModeratorComplaints.this, No_Internet_Connection.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        }
        checkInternetConnection();
        //-----------------------------------
        //Room DB
        dataBase = Room.databaseBuilder(this, UserDataBase.class, "Complaint")
                .allowMainThreadQueries().build();
        db = dataBase.getUserDao();

        binding.recyclerComplaints.setLayoutManager(new LinearLayoutManager(this));
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        binding.recyclerComplaints.setLayoutManager(mLayoutManager);


        binding.complaintsFragmentProgress.setVisibility(View.VISIBLE);
        complaintsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    binding.complaintsFragmentProgress.setVisibility(View.GONE);
                    binding.deleteAllComplaints.setVisibility(View.GONE);
                    binding.recyclerComplaints.setVisibility(View.GONE);
                    binding.moderatorComplaintNoResult.setVisibility(View.VISIBLE);
                }
                if (snapshot.exists()) {
                    if (!snapshot.hasChildren()) {
                        binding.complaintsFragmentProgress.setVisibility(View.GONE);
                        binding.deleteAllComplaints.setVisibility(View.GONE);
                        binding.recyclerComplaints.setVisibility(View.GONE);
                        binding.moderatorComplaintNoResult.setVisibility(View.VISIBLE);

                    } else {
                        binding.complaintsFragmentProgress.setVisibility(View.GONE);
                        binding.deleteAllComplaints.setVisibility(View.VISIBLE);
                        binding.recyclerComplaints.setVisibility(View.VISIBLE);
                        binding.moderatorComplaintNoResult.setVisibility(View.GONE);

                        complaintList.clear();

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                            String complaint = dataSnapshot.child("Complaint").getValue(String.class);
                            String complaintDate = dataSnapshot.child("ComplaintDate").getValue(String.class);
                            String userName = dataSnapshot.child("UserName").getValue(String.class);
                            String userPhone = dataSnapshot.child("UserPhone").getValue(String.class);

                            Complaint_Pojo complaintPojo = new Complaint_Pojo();
                            complaintPojo.setComplaint(complaint);
                            complaintPojo.setComplaintDate(complaintDate);
                            complaintPojo.setUserName(userName);
                            complaintPojo.setUserPhone(userPhone);

                            complaintList.add(complaintPojo);

                        }
                        adapter = new ComplaintAdapter(complaintList, ModeratorComplaints.this);
                        binding.recyclerComplaints.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        binding.deleteAllComplaints.setOnClickListener(view1 -> {
            SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
            pDialog.
                    setTitleText("هل انت متأكد ؟")
                    .setContentText("لن تستطيع اعادة هذه البيانات مجدداً !")
                    .setConfirmText("نعم ، متأكد")

                    .setConfirmClickListener(sweetAlertDialog -> {

                        complaintsRef.removeValue().addOnSuccessListener(aVoid -> {
                            complaintList.clear();
                            adapter.notifyDataSetChanged();
                            Toasty.success(getApplicationContext(), "تم الحذف بنجاح", Toasty.LENGTH_SHORT, true).show();
                            sweetAlertDialog.cancel();

                        }).addOnFailureListener(e -> {

                            Toasty.error(getApplicationContext(), "لقد حدث خطأ ما برجاء المحاولة لاحقاً", Toasty.LENGTH_SHORT, true).show();
                            sweetAlertDialog.cancel();
                        });
                    })

                    .setCancelText("التراجع")

                    .setCancelClickListener(SweetAlertDialog::cancel);

            pDialog.setCancelable(false);
            pDialog.setCanceledOnTouchOutside(false);
            pDialog.show();
        });

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
        Intent i = new Intent(ModeratorComplaints.this, ModeratorHome.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }

}

