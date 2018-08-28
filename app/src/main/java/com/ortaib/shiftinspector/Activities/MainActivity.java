package com.ortaib.shiftinspector.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ortaib.shiftinspector.Logic.ViewPagerNoKinetic;
import com.ortaib.shiftinspector.Tabs.AddShiftFragment;
import com.ortaib.shiftinspector.FireBase.DBHelper;
import com.ortaib.shiftinspector.Logic.User;
import com.ortaib.shiftinspector.Logic.ViewPagerAdapter;
import com.ortaib.shiftinspector.R;
import com.ortaib.shiftinspector.Tabs.MapFragment;
import com.ortaib.shiftinspector.Tabs.ProfileFragment;
import com.ortaib.shiftinspector.Tabs.ShiftScheduleFragment;
import com.ortaib.shiftinspector.Tabs.ShiftsFragment;
import com.ortaib.shiftinspector.Tabs.ViewShiftsFragment;

public class MainActivity extends AppCompatActivity implements ViewShiftsFragment.ViewShiftFragmentListener{
    private final static int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    final private String TAG = "MainActivity";
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference mDatabase;
    private DBHelper databaseHelper;
    static User user;
    private AppBarLayout appBarLayout;
    private ViewPagerNoKinetic mViewPager;
    private TabLayout tabLayout;
    private LocationManager locationManager;
    private ViewShiftsFragment viewShiftsFragment;
    private ShiftsFragment shiftsFragment;
    private MapFragment mapFragment;
    private boolean locationPermissionGranted = false;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        while (!locationPermissionGranted) {
            getLocationPermission();
        }
        verifyStoragePermissions();
        mAuth = FirebaseAuth.getInstance();
        databaseHelper = new DBHelper(getApplicationContext());
        currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            //move to login activity
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        } else {
            mapFragment = new MapFragment();
            viewShiftsFragment = new ViewShiftsFragment();
            shiftsFragment = new ShiftsFragment();
            tabLayout = (TabLayout) findViewById(R.id.tabs);
            mViewPager = (ViewPagerNoKinetic) findViewById(R.id.container);
            appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
            ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

            setupViewPager(mViewPager);
            mViewPager.setPagingEnabled(false);
            tabLayout.setupWithViewPager(mViewPager);
            tabLayout.getTabAt(0).setIcon(R.mipmap.profile);
            tabLayout.getTabAt(1).setIcon(R.mipmap.checkin);
            tabLayout.getTabAt(2).setIcon(R.mipmap.schedule);
            tabLayout.getTabAt(3).setIcon(R.mipmap.shift_list);
            // Read from the database
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ProfileFragment(), "");
        adapter.addFragment(new AddShiftFragment(), "");
        adapter.addFragment(new ShiftScheduleFragment(), "");
        adapter.addFragment(shiftsFragment, "");
        viewPager.setAdapter(adapter);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                break;
        }
    }

    protected void getUserFromDB() {
        final String email = currentUser.getEmail().replace(".", "_dot_");

    }

    protected void changeData(DataSnapshot dataSnapshot) {
        final String email = currentUser.getEmail().replace(".", "_dot_");
        User currentUser = dataSnapshot.child(email).getValue(User.class);
        String name = currentUser.getName();
        String mail = currentUser.getEmail();
        Double wage = currentUser.getWage();
        Log.d(TAG, "changeData: name  : " + name);
        Log.d(TAG, "changeData: email : " + mail);
        Log.d(TAG, "changeData: wage  : " + wage);

    }

    public  void verifyStoragePermissions() {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: requesting permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionResult: called");
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            locationPermissionGranted = false;
                            Log.d(TAG, "onRequestPermissionResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionResult: permission granted ");
                    locationPermissionGranted = true;
                }
        }
    }

    @Override
    public void onLocationSend(Double longitude, Double latitude,Double fLon,Double fLat) {
        shiftsFragment.changeToMapTab(longitude,latitude,fLon,fLat);
        //mapFragment.putMarker(longitude,latitude);
    }

}
