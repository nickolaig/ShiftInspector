package com.ortaib.shiftinspector.Tabs;

import android.app.DatePickerDialog;
import android.app.FragmentManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.alamkanak.weekview.DateTimeInterpreter;
import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;
import com.jaredrummler.android.colorpicker.ColorPickerView;
import com.ortaib.shiftinspector.Activities.MainActivity;
import com.ortaib.shiftinspector.Logic.MyDate;
import com.ortaib.shiftinspector.R;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

/**
 * Based on WeekView by Raquib-ul-Alam Kanak http://alamkanak.github.io.
 */
public class ShiftScheduleFragment extends Fragment  implements WeekView.EventClickListener, MonthLoader.MonthChangeListener, WeekView.EventLongPressListener, WeekView.EmptyViewLongPressListener {
    private Context context;
    private WeekView mWeekView;
    private TextView mDisplayDate;
    private FirebaseAuth mAuth;
    private String emailKey;
    private DatePickerDialog.OnDateSetListener mDatePickerListener;
    private  MyDate finish = new MyDate();
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private ColorPickerView colorPickerView;
    private int pickColor;
    private FirebaseUser currentUser;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_shift_schedule,container,false);
        context = getActivity();
        mAuth = FirebaseAuth.getInstance();
        mWeekView = (WeekView) view.findViewById(R.id.weekView);
        currentUser = mAuth.getCurrentUser();
        emailKey = currentUser.getEmail().replace(".","_dot_");
        mWeekView.setOnEventClickListener(this);
        mWeekView.setMonthChangeListener(this);
        mWeekView.setEventLongPressListener(this);
        mWeekView.setEmptyViewLongPressListener(this);
        setupDateTimeInterpreter(false);
        setHasOptionsMenu(false);
        loadData();
        mWeekView.goToHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
        return view;
    }




    /**
     * Set up a date time interpreter which will show short date values when in week view and long
     * date values otherwise.
     * @param shortDate True if the date values should be short.
     */
    private void setupDateTimeInterpreter(final boolean shortDate) {
        mWeekView.setDateTimeInterpreter(new DateTimeInterpreter() {
            @Override
            public String interpretDate(Calendar date) {
                SimpleDateFormat weekdayNameFormat = new SimpleDateFormat("EEE", Locale.getDefault());
                String weekday = weekdayNameFormat.format(date.getTime());
                SimpleDateFormat format = new SimpleDateFormat(" d/M", Locale.getDefault());

                if (shortDate)
                    weekday = String.valueOf(weekday.charAt(0));
                return weekday.toUpperCase() + format.format(date.getTime());
            }

            @Override
            public String interpretTime(int hour) {
                //return hour > 11 ? (hour - 12) + " PM" : (hour == 0 ? "12 AM" : hour + " AM");
                return hour+ ":00";
            }
        });
    }

    protected String getEventTitle(Calendar time) {
        return String.format("Event of %02d:%02d %s/%d", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), time.get(Calendar.MONTH)+1, time.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {
        Toast.makeText(context, "Clicked " + event.getName(), Toast.LENGTH_SHORT).show();
        Calendar cal = Calendar.getInstance();
        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra("beginTime", event.getStartTime().getTimeInMillis());
        intent.putExtra("allDay", false);
        intent.putExtra("endTime", event.getEndTime().getTimeInMillis());
        intent.putExtra("title", event.getName());
        //intent.putExtra("color",event.getColor());
        startActivity(intent);
    }

    @Override
    public void onEventLongPress(final WeekViewEvent event, RectF eventRect) {
        Toast.makeText(context, "Long pressed event: " + event.getName(), Toast.LENGTH_SHORT).show();
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle("Delete");
        alert.setMessage("Are you sure you want to delete the entry?");
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                events.remove(event);
                mWeekView.notifyDatasetChanged();
                return;
            }
        });
        alert.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
        alert.show();

    }
    private String name_value;private Calendar sTime;
    @Override
    public void onEmptyViewLongPress(Calendar time) {
        TextView mDisplayDate;
        sTime=time;
        DatePickerDialog.OnDateSetListener mDateSetListener;
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle("Shift Name :");
        alert.setMessage("Enter shift description :");

        // Set an EditText view to get user input
        final EditText input = new EditText(context);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                name_value = input.getText().toString();
                Log.d("", "Pin Value : " + name_value);
                addFinishDateListener();
                return;
            }
        });
        alert.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
        alert.show();


        //event.setColor(getResources().getColor(R.color.event_color_01));

        // Toast.makeText(context, "Empty view long pressed: " + getEventTitle(time), Toast.LENGTH_SHORT).show();
    }

    public WeekView getWeekView() {
        return mWeekView;
    }
    private List<WeekViewEvent> events;

    @Override
    public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {
        List<WeekViewEvent> matchedEvents = new ArrayList<WeekViewEvent>();
        for (WeekViewEvent event : events) {
            if (eventMatches(event, newYear, newMonth)) {
                matchedEvents.add(event);
            }
        }
        return matchedEvents;
    }

    /**
     * Checks if an event falls into a specific year and month.
     * @param event The event to check for.
     * @param year The year.
     * @param month The month.
     * @return True if the event matches the year and month.
     */
    private boolean eventMatches(WeekViewEvent event, int year, int month) {
        return (event.getStartTime().get(Calendar.YEAR) == year && event.getStartTime().get(Calendar.MONTH) == month - 1) || (event.getEndTime().get(Calendar.YEAR) == year && event.getEndTime().get(Calendar.MONTH) == month - 1);
    }

    public void addFinishDateListener(){
        ColorPickerDialog colorPickerDialog = ColorPickerDialog.newBuilder()
                .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                .setDialogId(0)
                .setColor(Color.RED).create();
        colorPickerDialog.setColorPickerDialogListener(new ColorPickerDialogListener() {
            @Override public void onColorSelected(int dialogId, int color) {
                pickColor=color;
                //Toast.makeText(getActivity(), "Color: #" + Integer.toHexString(color), Toast.LENGTH_LONG).show();
                int year = sTime.get(Calendar.YEAR);
                int month = sTime.get(Calendar.MONTH);
                int day = sTime.get(Calendar.DAY_OF_MONTH);
                mDatePickerListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        int hour = sTime.get(Calendar.HOUR_OF_DAY);
                        int minute = sTime.get(Calendar.MINUTE);
                        finish = new MyDate(year,month,dayOfMonth,0,0,0);
                        TimePickerDialog timePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener(){

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                finish.setHour(hourOfDay);
                                finish.setMinute(minute);
                                Calendar endTime = (Calendar) sTime.clone();
                                endTime.set(Calendar.HOUR_OF_DAY, finish.getHour());
                                endTime.set(Calendar.MINUTE, finish.getMinute());
                                endTime.set(Calendar.DAY_OF_MONTH, finish.getDay());
                                endTime.set(Calendar.YEAR, finish.getYear());
                                endTime.set(Calendar.MONTH, finish.getMonth());



                                if(sTime.compareTo(endTime)>-1){
                                    Toast.makeText(context, "Shift was not formated propertly please try again", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    WeekViewEvent event =new WeekViewEvent(events.size(),name_value,sTime,endTime);
                                    event.setColor(pickColor);
                                    events.add(event);
                                    mWeekView.notifyDatasetChanged();
                                    saveData();
                                    Toast.makeText(context, "Shift added", Toast.LENGTH_SHORT).show();
                                }
                                //here
                            }
                        },hour,minute,true);
                        timePicker.setTitle("Select Finish Time");
                        timePicker.show();

                    }
                };
                DatePickerDialog mdatePicker = new DatePickerDialog(context,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,mDatePickerListener,year,month,day);
                mdatePicker.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                mdatePicker.setTitle("Pick finish date");
                mdatePicker.show();

            }

            @Override public void onDialogDismissed(int dialogId) {

            }
        });
        colorPickerDialog.show(getActivity().getFragmentManager(), "ColorPicker");

    }


    private void saveData(){
        SharedPreferences sharedPreferences= context.getSharedPreferences(emailKey,MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        Gson gson= new Gson();
        String json= gson.toJson(events);
        editor.putString("Events",json);
        editor.apply();
    }
    private void loadData(){
        SharedPreferences sharedPreferences= context.getSharedPreferences(emailKey,MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        Gson gson= new Gson();
        String json= sharedPreferences.getString("Events",null);
        Type type= new TypeToken<ArrayList<WeekViewEvent>>(){}.getType();
        events=gson.fromJson(json,type);
        if(events==null)
        {
            events= new ArrayList<WeekViewEvent>();
        }
        mWeekView.notifyDatasetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

}
