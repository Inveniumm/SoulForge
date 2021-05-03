package com.example.soulforge.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.soulforge.fragments.AddPostFragment;
import static com.example.soulforge.utils.Constants.IS_FROM_HOME;
import static com.example.soulforge.utils.Constants.IS_FROM_NOTIFICATION;
import static com.example.soulforge.utils.Constants.IS_SEARCHED_USER;
import com.example.soulforge.fragments.HomeFragment;
import com.example.soulforge.fragments.NotificationFragment;
import com.example.soulforge.fragments.ProfileFragment;
import com.example.soulforge.fragments.SearchFragment;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    int noOfTabs;

    public ViewPagerAdapter(@NonNull FragmentManager fm, int noOfTabs) {
        super(fm);
        this.noOfTabs = noOfTabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new HomeFragment();
            case 1:
                return new SearchFragment();
            case 2:
                return new AddPostFragment();
            case 3:
                return new NotificationFragment();
            case 4:
                return new ProfileFragment();

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return noOfTabs;
    }
}
