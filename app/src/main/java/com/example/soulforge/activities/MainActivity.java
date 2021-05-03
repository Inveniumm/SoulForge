package com.example.soulforge.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.soulforge.R;
import com.example.soulforge.adapter.ViewPagerAdapter;
import com.example.soulforge.fragments.HomeFragment;
import com.example.soulforge.fragments.NotificationFragment;
import com.example.soulforge.fragments.SearchFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

//import static com.example.soulforge.R.id.logoutTv;
import static com.example.soulforge.R.layout.activity_main;
import static com.example.soulforge.utils.Constants.IS_FROM_HOME;
import static com.example.soulforge.utils.Constants.IS_FROM_NOTIFICATION;
import static com.example.soulforge.utils.Constants.IS_SEARCHED_USER;
import static com.example.soulforge.utils.Constants.PREF_DIRECTORY;
import static com.example.soulforge.utils.Constants.PREF_NAME;
import static com.example.soulforge.utils.Constants.USER_ID;

public class MainActivity extends AppCompatActivity implements SearchFragment.OnDataPass, HomeFragment.DataFromHome, NotificationFragment.DataFromNotification {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_main);

        init();
    }

    private void init() {
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);

        addTabs();
    }

    // Here we are setup bottom tab layout and viewpager
    private void addTabs() {
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_home));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_search));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_add));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_heart));

        SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String directory = preferences.getString(PREF_DIRECTORY, "");
        Bitmap bitmap = loadProfileImage(directory);
        Drawable drawable = new BitmapDrawable(getResources(), bitmap);
        if (bitmap == null)
            tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_person_outline));
        else
            tabLayout.addTab(tabLayout.newTab().setIcon(drawable));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

                switch (tab.getPosition()) {
                    case 0:
                        tabLayout.getTabAt(0).setIcon(R.drawable.ic_home_fill);
                        break;
                    case 1:
                        tabLayout.getTabAt(1).setIcon(R.drawable.ic_search_fill);
                        break;
                    case 2:
                        tabLayout.getTabAt(2).setIcon(R.drawable.ic_add_fill);
                        break;
                    case 3:
                        tabLayout.getTabAt(3).setIcon(R.drawable.ic_heart_fill);
                        break;
                    case 4:
                        tabLayout.getTabAt(4).setIcon(R.drawable.ic_person_fill);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        tabLayout.getTabAt(0).setIcon(R.drawable.ic_home);
                        break;
                    case 1:
                        tabLayout.getTabAt(1).setIcon(R.drawable.ic_search);
                        break;
                    case 2:
                        tabLayout.getTabAt(2).setIcon(R.drawable.ic_add);
                        break;
                    case 3:
                        tabLayout.getTabAt(3).setIcon(R.drawable.ic_heart);
                        break;
                    case 4:
                        tabLayout.getTabAt(4).setIcon(R.drawable.ic_person_outline);
                        break;
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        tabLayout.getTabAt(0).setIcon(R.drawable.ic_home_fill);
                        break;
                    case 1:
                        tabLayout.getTabAt(1).setIcon(R.drawable.ic_search_fill);
                        break;
                    case 2:
                        tabLayout.getTabAt(2).setIcon(R.drawable.ic_add_fill);
                        break;
                    case 3:
                        tabLayout.getTabAt(3).setIcon(R.drawable.ic_heart_fill);
                        break;
                    case 4:
                        tabLayout.getTabAt(4).setIcon(R.drawable.ic_person_fill);
                        break;
                }
            }
        });

        setViewPager(0);
    }

    private Bitmap loadProfileImage(String directory) {
        try {
            File file = new File(directory, "profile.png");
            return BitmapFactory.decodeStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onChange(String uid) {
        USER_ID = uid;
        IS_SEARCHED_USER = true;
        viewPager.setCurrentItem(4);
    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 4) {
            if (IS_FROM_NOTIFICATION) {
                IS_FROM_NOTIFICATION = false;
                viewPager.setCurrentItem(3);
            } else if (IS_FROM_HOME) {
                IS_FROM_HOME = false;
                viewPager.setCurrentItem(0);
            } else if (IS_SEARCHED_USER) {
                IS_SEARCHED_USER = false;
                viewPager.setCurrentItem(1);
            }
        } else
            super.onBackPressed();
    }


    @Override
    public void onReceiveUid(String uid) {
        USER_ID = uid;
        IS_FROM_HOME = true;
        viewPager.setCurrentItem(4);
    }

    @Override
    public void UidFromNotification(String uid) {
        USER_ID = uid;
        IS_FROM_NOTIFICATION = true;

        setViewPager(4);
    }

    // Its initializing Main screen pager
    private void setViewPager(int index) {
        pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        if (index == 0)
            tabLayout.getTabAt(index).setIcon(R.drawable.ic_home_fill);
        viewPager.setCurrentItem(index);
    }
}