package com.zerotime.zerotime2020.User;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.zerotime.zerotime2020.Adapters.SliderAdapter;
import com.zerotime.zerotime2020.R;
import com.zerotime.zerotime2020.databinding.ActivityStartingScreenBinding;

public class StartingScreen extends AppCompatActivity {
    private ActivityStartingScreenBinding binding;

    private TextView[] mDots;
    private int mCurrentPage;

    @Override
    protected void onStart() {
        super.onStart();

        SliderAdapter adapter = new SliderAdapter(this);
        binding.viewPager.setAdapter(adapter);
        addDotsIndicator(0);
        binding.viewPager.addOnPageChangeListener(listener);

        binding.nextBtn.setOnClickListener(view12 -> binding.viewPager.setCurrentItem(mCurrentPage+1));

        binding.backBtn.setOnClickListener(view1 -> binding.viewPager.setCurrentItem(mCurrentPage-1));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityStartingScreenBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

    }

    public void addDotsIndicator(int position) {
        mDots = new TextView[3];
        binding.dotLayout.removeAllViews();
        for (int i = 0; i < mDots.length; i++) {
            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226;"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            binding.dotLayout.addView(mDots[i]);


        }

        if (mDots.length > 0) {
            mDots[position].setTextColor(getResources().getColor(R.color.colorAccent));
        }

    }

    ViewPager.OnPageChangeListener listener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onPageSelected(int position) {
            addDotsIndicator(position);
            mCurrentPage = position;
            if (position == 0) {
                binding.nextBtn.setEnabled(true);
                binding.backBtn.setEnabled(false);
                binding.backBtn.setVisibility(View.INVISIBLE);

                binding.nextBtn.setText("Next");
                binding.backBtn.setText("");

            } else if (position == mDots.length - 1) {
                binding.nextBtn.setEnabled(true);
                binding.backBtn.setEnabled(false);
                binding.backBtn.setVisibility(View.INVISIBLE);

                binding.nextBtn.setText("Finish");
                binding.nextBtn.setOnClickListener(view -> goToLogin());

            }

            else {
                binding.nextBtn.setEnabled(true);
                binding.backBtn.setEnabled(true);
                binding.backBtn.setVisibility(View.VISIBLE);

                binding.nextBtn.setText("Next");
                binding.backBtn.setText("Back");
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();

    }
    private void goToLogin(){
        Intent intent=new Intent(StartingScreen.this, Login.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }
}