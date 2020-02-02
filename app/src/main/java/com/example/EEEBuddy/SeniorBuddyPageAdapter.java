package com.example.EEEBuddy;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;


public class SeniorBuddyPageAdapter extends FragmentPagerAdapter {

    private int numOfTabs;

    //SeniorBuddyPageAdapter constructer is use to communicate between this class and SeniorBuddyPage
    public SeniorBuddyPageAdapter(@NonNull FragmentManager fm, int numOfTabs) {
        super(fm);
        this.numOfTabs = numOfTabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) { //initialize the fragements for Android TabLayout (Recomm and All)

        Fragment fragment = null;

        switch (position){
            case 0:
                fragment = new RecommFragment();
                break;

            case 1:
                fragment =  new AllFragment();
                break;

        }
        return fragment;

    }

    @Override
    public int getCount() {//return number of tabs appear in tabLayout.
        return numOfTabs;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }
}
