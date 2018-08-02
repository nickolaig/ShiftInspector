package com.ortaib.shiftinspector.Logic;

import java.io.Serializable;


public class User implements Serializable {
    private String name;
    private String email;
    private double wage;
    private String profileType;
    private User employee;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(final String name, final String email, double wage, final String type){
        this.name = name;
        this.email = email;
        this.wage = wage;
        this.profileType = type;
        this.employee=null;
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

    public User getEmployee() {
        return employee;
    }

    public void setEmployee(User employee) {
        this.employee = employee;
    }
    public boolean equals(Object obj) {// Two users equals if they have the same email and type
        User otherUser = (User)obj;
        return this.getEmail().equals(otherUser.getEmail()) && this.getProfileType().equals(otherUser.getProfileType());
    }

    public String getProfileType() {
        return profileType;
    }
}
