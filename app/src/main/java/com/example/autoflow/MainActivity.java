package com.example.autoflow;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.autoflow.adapter.ViewPagerAdapter;
import com.example.autoflow.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private BottomNavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewPager = binding.viewPager;
        navigationView = binding.navView;

        viewPager.setAdapter(new ViewPagerAdapter(this));

        // Synchronize the ViewPager2 and the BottomNavigationView
        navigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_home) {
                viewPager.setCurrentItem(0);
                return true;
            } else if (item.getItemId() == R.id.navigation_dashboard) {
                viewPager.setCurrentItem(1);
                return true;
            } else if (item.getItemId() == R.id.navigation_notifications) {
                viewPager.setCurrentItem(2);
                return true;
            }
            return false;
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                navigationView.getMenu().getItem(position).setChecked(true);
            }
        });
    }
}
