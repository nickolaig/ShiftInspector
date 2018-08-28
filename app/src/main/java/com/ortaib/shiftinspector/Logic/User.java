package com.ortaib.shiftinspector.Logic;

import com.alamkanak.weekview.WeekViewEvent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class User implements Serializable {
    private String name;
    private String email;
    private double wage;
    private List<WeekViewEvent> schedule;

    public List<WeekViewEvent> getSchedule() {
        return schedule;
    }

    public void setSchedule(List<WeekViewEvent> schedule) {
        this.schedule = schedule;
    }

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(final String name, final String email, double wage){
        this.name = name;
        this.email = email;
        this.wage = wage;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public double getWage() {
        return wage;
    }

    public void setWage(double wage) {
        this.wage = wage;
    }

    public boolean equals(Object obj) {// Two users equals if they have the same email and type
        User otherUser = (User)obj;
        return this.getEmail().equals(otherUser.getEmail());
    }
}
