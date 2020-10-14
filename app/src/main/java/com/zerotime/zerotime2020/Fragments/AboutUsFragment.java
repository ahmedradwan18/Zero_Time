package com.zerotime.zerotime2020.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.zerotime.zerotime2020.Receiver.MyBroadCast;
import com.zerotime.zerotime2020.No_Internet_Connection;
import com.zerotime.zerotime2020.R;
import com.zerotime.zerotime2020.databinding.UserFragmentAboutUsBinding;


public class AboutUsFragment extends Fragment implements OnMapReadyCallback {
    private UserFragmentAboutUsBinding binding;
    ExpandableTextView expandableTextView;
    String longText = "شركه زيرو تايم للنقل والشحن السريع , تأسست عام 2016 , لو عندك بيزنس اونلاين بتحتاج شركه شحن تحافظ على مستوى البراند وتوصل شحناتك بأمان وسرعه وثقه من غير اي حيره زيرو تايم هي راحتك وراحه عميلك , زيرو تايم شركه مرخصه بريدياً ولديها سجل تجاري وبطاقه ضريبيه , زيرو تايم بتوصل لأغلب محافظات مصر  ولسه هنكمل لمحافظات مصر كلها ," +
            "زيرو تايم بتستلم وتسلم الشحنات من الباب للباب وده بيكون من خلال مندوبينا المتدربين , زيرو تايم بتوصلك تحصيلك بأكثر من طريقه وفي الميعاد اللى بتحدده وانت اختار اللى يناسبك , زيرو تايم بتساعدك  تريح عميلك من خلال خدمات اختياريه زي خدمه طرد مقابل طرد او خدمه فتح الشحنات وده بيكون بناءا على اختيارك انت , زيرو تايم بتسلم مرتجعاتك وده بيكون على مدار ثلاثه ايام في الأسبوع. \n" +
            " خدمة عملاء متاحه لمساعدتك في الرد على جميع الاستفسارات وحل جميع المشاكل الي بتواجهها .";
    Context context;
    View view;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = UserFragmentAboutUsBinding.inflate(getLayoutInflater());
        view = binding.getRoot();
        context = container.getContext();

        binding.aboutUsMap.onCreate(savedInstanceState);
        binding.aboutUsMap.getMapAsync(this);

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

    @Override
    public void onResume() {
        super.onResume();
        // Check Internet State
        if (!haveNetworkConnection()) {
            Intent i = new Intent(context, No_Internet_Connection.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            ((Activity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            ((Activity) context).finish();
        }
        checkInternetConnection();
        //-----------------------------------
        expandableTextView = view.findViewById(R.id.expand_text_view);
        expandableTextView.setText(longText);


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

    @Override
    public void onMapReady(GoogleMap googleMap) {

        LatLng sydney = new LatLng(30.1195601, 31.3677548);
        googleMap.addMarker(new MarkerOptions().position(sydney).title("شارع المدينة المنورة"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(30.1195601, 31.3677548), 10));
        googleMap.getMaxZoomLevel();
        binding.aboutUsMap.onResume();
    }
}