package com.zerotime.zerotime2020.User;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.zerotime.zerotime2020.Fragments.ContactFragment;
import com.zerotime.zerotime2020.Fragments.HomeFragment;
import com.zerotime.zerotime2020.Fragments.ProfileFragment;
import com.zerotime.zerotime2020.Fragments.SettingsFragment;
import com.zerotime.zerotime2020.R;
import com.zerotime.zerotime2020.databinding.UserActivityHomeBinding;

public class Home extends AppCompatActivity {
    String userId;
    private UserActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = UserActivityHomeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.bottomNav.setCurrentActiveItem(3);

        getSupportFragmentManager().beginTransaction().replace(R.id.Frame_Content, new HomeFragment()).commit();


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();



        if (binding.bottomNav.getCurrentActiveItemPosition() == 3) {
            Toast.makeText(getApplicationContext(), "Home  Back", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            int pid = android.os.Process.myPid();
            android.os.Process.killProcess(pid);

                return;
            }

        if (binding.bottomNav.getCurrentActiveItemPosition() == 2) {

            binding.bottomNav.setCurrentActiveItem(3);
            return;
        }
        if (binding.bottomNav.getCurrentActiveItemPosition() == 1) {
            binding.bottomNav.setCurrentActiveItem(3);
            return;
        }
        if (binding.bottomNav.getCurrentActiveItemPosition() == 0) {
            binding.bottomNav.setCurrentActiveItem(3);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences prefs = getSharedPreferences("UserState", MODE_PRIVATE);
        userId = prefs.getString("isLogged", "");

        //Attach Listener To Bottom Nav
        binding.bottomNav.setCurrentActiveItem(3);
        binding.bottomNav.setNavigationChangeListener((view1, position) -> {

            //navigation changed, do something

            switch (position) {
                case 3:
                    Fragment fragment1 = new HomeFragment();
                    FragmentTransaction transaction1 = getSupportFragmentManager().beginTransaction();
                    transaction1.replace(R.id.Frame_Content, fragment1);
                    transaction1.addToBackStack(null);
                    transaction1.commit();
                    break;

                case 2:
                    ProfileFragment fragment2 = new ProfileFragment();
                    FragmentTransaction fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction2.replace(R.id.Frame_Content, fragment2);
                    fragmentTransaction2.addToBackStack(null);
                    fragmentTransaction2.commit();
                    break;

                case 1:
                    ContactFragment fragment3 = new ContactFragment();
                    FragmentTransaction fragmentTransaction3 = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction3.replace(R.id.Frame_Content, fragment3);
                    fragmentTransaction3.addToBackStack(null);
                    fragmentTransaction3.commit();
                    break;

                case 0:
                    SettingsFragment fragment4 = new SettingsFragment();
                    FragmentTransaction fragmentTransaction4 = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction4.replace(R.id.Frame_Content, fragment4);
                    fragmentTransaction4.addToBackStack(null);
                    fragmentTransaction4.commit();
                    break;

            }
        });
    }
}