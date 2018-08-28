package com.ortaib.shiftinspector.Tabs;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ortaib.shiftinspector.Activities.MainActivity;
import com.ortaib.shiftinspector.FireBase.DBHelper;
import com.ortaib.shiftinspector.Logic.MyDate;
import com.ortaib.shiftinspector.Logic.Shift;
import com.ortaib.shiftinspector.Logic.User;
import com.ortaib.shiftinspector.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import static android.content.Context.LOCATION_SERVICE;
import static com.firebase.ui.auth.AuthUI.getApplicationContext;


public class AddShiftFragment extends Fragment {
    private static final String TAG = " AddShiftFragment";
    private LocationListener locationListener;
    private LocationManager locationManager;
    private final static int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private boolean locationPermissionGranted=false;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private View view;
    private DatabaseReference mDatabase;
    private Context context;
    private String emailKey;
    private TextView message;
    private MyDate start = new MyDate();
    private MyDate finish = new MyDate();
    private Calendar cal;
    private Button startBtn, finishBtn, addManualyBtn;
    private Shift lastShift, manualShift;
    private DatePickerDialog.OnDateSetListener mDatePickerListenerStart, mDatePickerListenerFinish;
    private Boolean isWorking, loaded;
    private Double lat, lon;

    Location myLocation;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_shift, container, false);
        context = getActivity();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        emailKey = currentUser.getEmail().replace(".", "_dot_");
        cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        loaded = false;
        manualShift = new Shift();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("shifts").child(emailKey)
                .child(Integer.toString(year)).child(Integer.toString(month));
        Query lastQuery = mDatabase.limitToLast(1);
        message = (TextView) view.findViewById(R.id.message);
        lastQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loaded = true;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    lastShift = (Shift) ds.getValue(Shift.class);
                }
                if (lastShift != null) {
                    if (lastShift.getEndTime() == null) {
                        isWorking = true;
                        message.setText("You are working");
                    } else {
                        isWorking = false;
                        message.setText("You are not working");
                    }
                } else {
                    isWorking = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: ");
            }
        });
        startBtn = (Button) view.findViewById(R.id.start_btn);
        finishBtn = (Button) view.findViewById(R.id.finish_btn);
        addManualyBtn = (Button) view.findViewById(R.id.add_manualy_btn);
        //set btn listeners

        addStartShiftListener();
        addFinishShiftListener();
        addShiftManuallyListener();
        locationManager = (LocationManager) ((MainActivity)getActivity()).getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                lat = location.getLatitude();
                lon = location.getLongitude();
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
            }

            @Override
            public void onProviderEnabled(String s) {
            }

            @Override
            public void onProviderDisabled(String s) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };
        getLocationPermission();

        return view;
    }

    public String makeUniqueID(MyDate d) {
        StringBuilder randomStringBuilder = new StringBuilder();
        randomStringBuilder.append(Integer.toString(d.getYear()));
        if (d.getMonth() > 9) {
            randomStringBuilder.append(Integer.toString(d.getMonth()));
        } else {
            randomStringBuilder.append("0");
            randomStringBuilder.append(Integer.toString(d.getMonth()));

        }
        if (d.getDay() >= 10) {
            randomStringBuilder.append(Integer.toString(d.getDay()));
        } else {
            randomStringBuilder.append("0");
            randomStringBuilder.append(Integer.toString(d.getDay()));
        }
        if (d.getHour() >= 10) {
            randomStringBuilder.append(Integer.toString(d.getHour()));
        } else {
            randomStringBuilder.append("0");
            randomStringBuilder.append(Integer.toString(d.getHour()));
        }
        if (d.getMinute() >= 10) {
            randomStringBuilder.append(Integer.toString(d.getMinute()));
        } else {
            randomStringBuilder.append("0");
            randomStringBuilder.append(Integer.toString(d.getMinute()));
        }
        if (d.getSecond() >= 10) {
            randomStringBuilder.append(Integer.toString(d.getSecond()));
        } else {
            randomStringBuilder.append("0");
            randomStringBuilder.append(Integer.toString(d.getSecond()));
        }

        return randomStringBuilder.toString();
    }

    public void addShiftManuallyListener() {
        addStartDateListener();

    }

    public void addFinishShiftListener() {
        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loaded == true) {
                    if (isWorking) {
                        cal = Calendar.getInstance();
                        int year = cal.get(Calendar.YEAR);
                        int month = cal.get(Calendar.MONTH) + 1;
                        int day = cal.get(Calendar.DAY_OF_MONTH);
                        int hour = cal.get(Calendar.HOUR_OF_DAY);
                        int minute = cal.get(Calendar.MINUTE);
                        int second = cal.get(Calendar.SECOND);
                        MyDate finishDate = new MyDate(year, month, day, hour, minute, second);
                        lastShift.setEndTime(finishDate);
                        lastShift.calculateMoneyEarned();

                        getDeviceLocation();
                        if (lat != null) {
                            lastShift.setFinishLat(lat);
                            lastShift.setFinishLon(lon);
                        }
                        mDatabase = FirebaseDatabase.getInstance().getReference().child("shifts").child(emailKey);
                        mDatabase.child(Integer.toString(lastShift.getStartTime().getYear()))
                                .child(Integer.toString(lastShift.getStartTime().getMonth())).child(lastShift.getId()).setValue(lastShift);
                        Toast.makeText(context, "Shift Ended", Toast.LENGTH_SHORT).show();
                        isWorking = false;
                        message.setText("You are not working");

                    } else {
                        Toast.makeText(context, "You need to start a shift in order to end one", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public void addStartShiftListener() {
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loaded) {
                    if (isWorking == false) {
                        mDatabase = FirebaseDatabase.getInstance().getReference().child("shifts").child(emailKey);
                        cal = Calendar.getInstance();
                        int year = cal.get(Calendar.YEAR);
                        int month = cal.get(Calendar.MONTH) + 1;
                        int day = cal.get(Calendar.DAY_OF_MONTH);
                        int hour = cal.get(Calendar.HOUR_OF_DAY);
                        int minute = cal.get(Calendar.MINUTE);
                        int second = cal.get(Calendar.SECOND);
                        MyDate startDate = new MyDate(year, month, day, hour, minute, second);
                        lastShift = new Shift();
                        lastShift.setStartTime(startDate);
                        getDeviceLocation();
                        if (lat != null) {
                            lastShift.setStartLat(lat);
                            lastShift.setStartLon(lon);
                        }
                        mDatabase.child(Integer.toString(year)).child(Integer.toString(month)).child(lastShift.getId()).setValue(lastShift);
                        Toast.makeText(context, "Shift started", Toast.LENGTH_SHORT).show();
                        isWorking = true;
                        message.setText("You are working");

                    } else {
                        Toast.makeText(context, "You need to finish a shift inorder to start new one", Toast.LENGTH_SHORT).show();
                    }
                }
            }

        });
    }

    public void addStartDateListener() {
        addManualyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog mdatePicker = new DatePickerDialog(context,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth, mDatePickerListenerStart, year, month, day);
                mdatePicker.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                mdatePicker.setTitle("Pick starting date");
                mdatePicker.show();
            }
        });
        mDatePickerListenerStart = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar cal = Calendar.getInstance();
                int hour = cal.get(Calendar.HOUR_OF_DAY);
                int minute = cal.get(Calendar.MINUTE);
                start = new MyDate(year, month + 1, dayOfMonth, 0, 0, 0);
                TimePickerDialog timePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        start.setHour(hourOfDay);
                        start.setMinute(minute);
                        if (checkDate(start) == true) {
                            manualShift.setStartTime(start);
                            addFinishDateListener();
                        } else {
                            Toast.makeText(context, "you cant add future shift", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, hour, minute, true);
                timePicker.setTitle("Select Starting Time");
                timePicker.show();
            }
        };
    }

    public void addFinishDateListener() {
        TimePickerDialog timePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                finish.setYear(manualShift.getStartTime().getYear());
                finish.setDay(manualShift.getStartTime().getDay());
                finish.setMonth(manualShift.getStartTime().getMonth());
                finish.setHour(hourOfDay);
                finish.setMinute(minute);
                if (checkDate(finish) == true) {
                    manualShift.setEndTime(finish);
                    manualShift.calculateMoneyEarned();
                    manualShift.setEmployee(emailKey);
                    mDatabase = FirebaseDatabase.getInstance().getReference().child("shifts").child(emailKey);
                    mDatabase.child(Integer.toString(start.getYear())).child(Integer.toString(start.getMonth())).child(manualShift.getId()).setValue(manualShift);
                    Toast.makeText(context, "shift added", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "you cant add future shift", Toast.LENGTH_SHORT).show();
                }
                //here
            }
        }, 0, 0, true);
        timePicker.setTitle("Select Finish Time");
        timePicker.show();
    }

    public boolean checkDate(MyDate date) {
        cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);
        if (date.getYear() < year) {
            return true;
        } else if (date.getYear() == year) {
            if (date.getMonth() > month) {
                return false;
            } else if (date.getMonth() < month) {
                return true;
            } else {
                if (date.getDay() > day) {
                    return false;
                } else if (date.getDay() < day) {
                    return true;
                } else {
                    if (date.getHour() > hour) {
                        return false;
                    } else if (date.getHour() < hour) {
                        return true;
                    } else {
                        if (date.getMinute() > minute) {
                            return false;
                        } else if (date.getMinute() < minute) {
                            return true;
                        } else {
                            if (date.getSecond() > second) {
                                return false;
                            } else if (date.getSecond() <= second) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private void getDeviceLocation() {
        /*lat = null;
        lon = null;*/
        Log.d(TAG, "getDeviceLocation: getting the device current location");
        try {
            @SuppressLint("MissingPermission") Location currentLocation = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
            //Location currentLocation = getLastKnownLocation();
            if(currentLocation != null) {
                lat = currentLocation.getLatitude();
                lon = currentLocation.getLongitude();
            }
            else{
                Toast.makeText(getActivity(),"ERROR WHILE TRYING TO GET LOCATION",Toast.LENGTH_SHORT).show();
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }
    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: requesting permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
            locationManager.requestLocationUpdates("gps", 50000, 1000, locationListener);

        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionResult: called");

        locationPermissionGranted = false;
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
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        locationManager.requestLocationUpdates("gps", 50000, 1000, locationListener);
                    }

                }
        }
    }

}
