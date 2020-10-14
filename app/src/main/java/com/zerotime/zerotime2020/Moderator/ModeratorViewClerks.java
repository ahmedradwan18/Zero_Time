package com.zerotime.zerotime2020.Moderator;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zerotime.zerotime2020.Helper.ClerksRecyclerItemTouchHelper;
import com.zerotime.zerotime2020.Helper.RecyclerItemTouchHelperListener;
import com.zerotime.zerotime2020.Moderator.Adapters.ClerkAdapter;
import com.zerotime.zerotime2020.Moderator.Pojos.Clerks;
import com.zerotime.zerotime2020.Receiver.MyBroadCast;
import com.zerotime.zerotime2020.No_Internet_Connection;
import com.zerotime.zerotime2020.R;
import com.zerotime.zerotime2020.databinding.ModeratorActivityViewClerksBinding;

import java.util.ArrayList;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;

public class ModeratorViewClerks extends AppCompatActivity implements RecyclerItemTouchHelperListener {
    ArrayList<Clerks> clerksList;
    String clerkPrimaryPhone;
    private ModeratorActivityViewClerksBinding binding;
    private DatabaseReference clerksRef;
    private ClerkAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //view binding
        binding = ModeratorActivityViewClerksBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // Check Internet State
        if (!haveNetworkConnection()) {
            Intent i = new Intent(ModeratorViewClerks.this, No_Internet_Connection.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        }
        checkInternetConnection();

        //recycler view initialization
        binding.recycler.setLayoutManager(new LinearLayoutManager(this));
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        binding.recycler.setLayoutManager(mLayoutManager);
        binding.recycler.setItemAnimator(new DefaultItemAnimator());

        clerksList = new ArrayList<>();

        binding.moderatorViewClerkProgress.setVisibility(View.VISIBLE);
        //Firebase Database Reference Initialization
        clerksRef = FirebaseDatabase.getInstance().getReference().child("Clerks");
        clerksRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    binding.recycler.setVisibility(View.GONE);
                    binding.moderatorViewClerkProgress.setVisibility(View.GONE);
                    binding.viewClerksNoResult.setVisibility(View.VISIBLE);
                }
                if (snapshot.exists()) {
                    if (!snapshot.hasChildren()) {
                        binding.recycler.setVisibility(View.GONE);
                        binding.moderatorViewClerkProgress.setVisibility(View.GONE);
                        binding.viewClerksNoResult.setVisibility(View.VISIBLE);
                    }
                    if (snapshot.hasChildren()) {
                        binding.moderatorViewClerkProgress.setVisibility(View.GONE);
                        binding.recycler.setVisibility(View.VISIBLE);
                        binding.viewClerksNoResult.setVisibility(View.GONE);

                        clerksList.clear();

                        for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {


                            String name = (String) dataSnapshot1.child("ClerkName").getValue();
                            String address = (String) dataSnapshot1.child("ClerkAddress").getValue();
                            String hasVehicle = (String) dataSnapshot1.child("hasVehicle").getValue();
                            String phone1 = (String) dataSnapshot1.child("ClerkPhone1").getValue();
                            clerkPrimaryPhone = (String) dataSnapshot1.child("ClerkPhone1").getValue();
                            String phone2 = (String) dataSnapshot1.child("ClerkPhone2").getValue();
                            String age = (String) (dataSnapshot1.child("ClerkAge").getValue());

                            Clerks clerks = new Clerks();
                            clerks.setName(name);
                            clerks.setAddress(address);
                            clerks.setPhone1(phone1);
                            clerks.setPhone2(phone2);
                            clerks.setAge(Integer.parseInt(Objects.requireNonNull(age)));

                            clerks.setAge(Integer.parseInt(Objects.requireNonNull(age)));
                            clerks.setHasVehicle(hasVehicle);

                            clerksList.add(clerks);


                        }
                        adapter = new ClerkAdapter(clerksList, ModeratorViewClerks.this);
                        binding.recycler.setAdapter(adapter);

                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback
                = new ClerksRecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.recycler);

    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof ClerkAdapter.ClerkViewHolder) {
            String clerkName = clerksList.get(viewHolder.getAdapterPosition()).getName();
            String clerkID = clerksList.get(viewHolder.getAdapterPosition()).getPhone1();

            final Clerks deletedClerk = clerksList.get(viewHolder.getAdapterPosition());
            final int deleteIndex = viewHolder.getAdapterPosition();
            adapter.removeClerk(deleteIndex);


            SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
            pDialog.
                    setTitleText("هل انت متأكد ؟")
                    .setContentText("لن تستطيع اعادة هذه البيانات مجدداً !")
                    .setConfirmText("نعم ، متأكد")

                    .setConfirmClickListener(sweetAlertDialog -> clerksRef.child(clerkID)
                            .removeValue()
                            .addOnCompleteListener(task -> {

                                if (task.isSuccessful()) {
                                    Toasty.success(getApplicationContext(),
                                            "تم حذف المندوب " + clerkName + " بنجاح",
                                            Toasty.LENGTH_SHORT,
                                            true)
                                            .show();

                                } else {
                                    Toasty.error(getApplicationContext(),
                                            "لقد حدث خطأ ما برجاء المحاولة لاحقاً",
                                            Toasty.LENGTH_SHORT,
                                            true)
                                            .show();

                                }
                                sweetAlertDialog.cancel();
                            }))

                    .setCancelText("التراجع")

                    .setCancelClickListener(sweetAlertDialog -> {
                        adapter.restoreClerk(deletedClerk, deleteIndex);
                        sweetAlertDialog.cancel();
                    });

            pDialog.setCancelable(false);
            pDialog.setCanceledOnTouchOutside(false);
            pDialog.show();

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
        Intent i = new Intent(ModeratorViewClerks.this, ModeratorHome.class);
        startActivity(i);
        finish();
    }
}