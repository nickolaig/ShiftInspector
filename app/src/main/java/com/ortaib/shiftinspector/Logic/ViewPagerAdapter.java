package com.ortaib.shiftinspector.Logic;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ortaib on 28/07/2018.
 */

public class ViewPagerAdapter extends FragmentPagerAdapter {
    
        private final List<Fragment> myFragmentsList = new ArrayList<>();
        private final List<String> myFragmentsTitleList = new ArrayList<>();


        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return super.getPageTitle(position);
    }

    public void addFragment(Fragment fragment, String title){
            myFragmentsList.add(fragment);
            myFragmentsTitleList.add(title);

        }

        @Override
        public Fragment getItem(int position) {
            return myFragmentsList.get(position);
        }

        @Override
        public int getCount() {
            return myFragmentsList.size();
        }
}


