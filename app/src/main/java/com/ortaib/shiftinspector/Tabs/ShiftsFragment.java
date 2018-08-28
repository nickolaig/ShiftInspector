package com.ortaib.shiftinspector.Tabs;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ortaib.shiftinspector.Logic.ViewPagerAdapter;
import com.ortaib.shiftinspector.R;


public class ShiftsFragment extends Fragment {
    private static final String TAG = "ShiftsFragment";
    private Context context;
    private View view;
    private ViewPager mViewPager;
    private TabLayout tabLayout;
    private ViewPagerAdapter adapter;
    private ViewShiftsFragment viewShiftsFragment;
    private MapFragment mapFragment;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_shifts,container,false);
        mViewPager = (ViewPager) view.findViewById(R.id.vp);

        tabLayout = (TabLayout) view.findViewById(R.id.fragments_tab);
        context = getActivity();
        mapFragment = new MapFragment();
        viewShiftsFragment = new ViewShiftsFragment();
        adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(viewShiftsFragment,"");
        adapter.addFragment(mapFragment,"");
        mViewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.getTabAt(0).setIcon(R.mipmap.book);
        tabLayout.getTabAt(1).setIcon(R.mipmap.bluelocation);

        return view;
    }
    public void changeToMapTab(Double sLon, Double sLat,Double fLon,Double fLat){
        mViewPager.setCurrentItem(1);
        if(sLon != null) {
            mapFragment.putStartMarker(sLon,sLat);
        }
        if(fLat != null){
            mapFragment.putFinishMarker(fLon,fLat);
        }



    }

    @Override
    public void onResume() {
        super.onResume();
        mViewPager.setCurrentItem(0);
    }
}
