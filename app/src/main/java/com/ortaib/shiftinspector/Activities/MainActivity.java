package com.ortaib.shiftinspector.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ortaib.shiftinspector.Tabs.AddShiftFragment;
import com.ortaib.shiftinspector.FireBase.DBHelper;
import com.ortaib.shiftinspector.Logic.User;
import com.ortaib.shiftinspector.Logic.ViewPagerAdapter;
import com.ortaib.shiftinspector.R;
import com.ortaib.shiftinspector.Tabs.ProfileFragment;
import com.ortaib.shiftinspector.Tabs.ShiftScheduleFragment;
import com.ortaib.shiftinspector.Tabs.ViewShiftsFragment;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    final private String TAG = "MainActivity";
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference mDatabase;
    private DBHelper databaseHelper;
    static User user;
    private AppBarLayout appBarLayout;
    private ViewPager mViewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        databaseHelper = new DBHelper(getApplicationContext());
        currentUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        if(currentUser == null){
            //move to login activity
            Intent loginIntent = new Intent(MainActivity.this,LoginActivity.class);
            startActivity(loginIntent);
            finish();
        }
        else {
            tabLayout = (TabLayout) findViewById(R.id.tabs);
            mViewPager = (ViewPager) findViewById(R.id.container);
            appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
            ViewPagerAdapter adapter = new ViewPagerAdapter( getSupportFragmentManager());

            setupViewPager(mViewPager);
            //mViewPager.setAdapter(adapter);
            tabLayout.setupWithViewPager(mViewPager);
            tabLayout.setupWithViewPager(mViewPager);
            tabLayout.getTabAt(0).setIcon(R.mipmap.profile);
            tabLayout.getTabAt(1).setIcon(R.mipmap.checkin);
            tabLayout.getTabAt(2).setIcon(R.mipmap.schedule);
            tabLayout.getTabAt(3).setIcon(R.mipmap.shift_list);
            // Read from the database
            mDatabase.addValueEventListener(new ValueEventListener() {
                //String ml = currentUser.getEmail().replace(".","_dot_");
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    Log.d(TAG, "Value is: " );
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", error.toException());
                }
            });

        }
    }
    private void setupViewPager(ViewPager viewPager){
        ViewPagerAdapter adapter = new ViewPagerAdapter( getSupportFragmentManager());
        adapter.addFragment(new ProfileFragment(),"");
        adapter.addFragment(new ShiftScheduleFragment(),"");
        adapter.addFragment(new AddShiftFragment(),"");
        adapter.addFragment(new ViewShiftsFragment(),"");
        viewPager.setAdapter(adapter);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case 1:
                break;
        }
    }
    protected void getUserFromDB(){
        final String email = currentUser.getEmail().replace(".","_dot_");

    }
    protected void changeData(DataSnapshot dataSnapshot){
        final String email = currentUser.getEmail().replace(".","_dot_");
        User currentUser = dataSnapshot.child(email).getValue(User.class);
        String name = currentUser.getName();
        String mail = currentUser.getEmail();
        Double wage = currentUser.getWage();
        Log.d(TAG, "changeData: name  : "+name);
        Log.d(TAG, "changeData: email : "+mail);
        Log.d(TAG, "changeData: wage  : "+wage);

    }
    public void alertme(){
        Log.d(TAG, "alertme: ");
    }

}
