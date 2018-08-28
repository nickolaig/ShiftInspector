package com.ortaib.shiftinspector.Logic;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 * Created by Ortaib on 02/08/2018.
 */

public class Shift implements Serializable, Comparable<Shift> {
    private MyDate startTime,endTime;
    private Double moneyEarned,wage,timeWorked;
    private String employee,employer;
    private String id;
    private Double startLat,startLon,finishLat,finishLon;

    public Double getStartLat() {
        return startLat;
    }

    public void setStartLat(Double startLat) {
        this.startLat = startLat;
    }

    public Double getStartLon() {
        return startLon;
    }

    public void setStartLon(Double startLon) {
        this.startLon = startLon;
    }

    public Double getFinishLat() {
        return finishLat;
    }

    public void setFinishLat(Double finishLat) {
        this.finishLat = finishLat;
    }

    public Double getFinishLon() {
        return finishLon;
    }

    public void setFinishLon(Double finishLon) {
        this.finishLon = finishLon;
    }

    public String getId() {
        return id;
    }

    public Double getWage() {
        return wage;
    }

    public void setWage(Double wage) {
        this.wage = wage;
    }

    public Double getTimeWorked() {
        return timeWorked;
    }

    public void setTimeWorked(Double timeWorked) {
        this.timeWorked = timeWorked;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Shift(MyDate startTime, MyDate endTime, String employee,Double wage) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.employee = employee;
        this.wage = 44.0;
        this.moneyEarned = 0.0;

    }
    public Shift() {
        this.wage = 44.0;
        this.moneyEarned = 0.0;
    }

    public MyDate getStartTime() {
        return startTime;
    }

    public void setStartTime(MyDate startTime) {

        this.startTime = startTime;
        this.id = makeUniqueID(startTime);
    }

    public MyDate getEndTime() {
        return endTime;
    }

    public void setEndTime(MyDate endTime) {
        this.endTime = endTime;
    }

    public Double getMoneyEarned() {
        return moneyEarned;
    }

    private void setMoneyEarned(Double moneyEarned) {this.moneyEarned=moneyEarned;}
    public void calculateMoneyEarned(){
        this.timeWorked = this.substract();
        this.moneyEarned=0.0;
        if(timeWorked <24) {
            if (timeWorked > 8) {
                if (timeWorked > 10) {
                    moneyEarned += (timeWorked-10)*wage*1.5+(2*wage*1.25)+(8*wage);
                }else {
                    moneyEarned += (timeWorked - 8) * wage * 1.25+(8*wage);
                }
            }else {
                moneyEarned += timeWorked * wage;
            }
        }
        moneyEarned = (double)Math.round(moneyEarned*10)/10;
    }

    public String getEmployee() {
        return employee;
    }

    public void setEmployee(String employee) {
        this.employee = employee;
    }

    public String getEmployer() {
        return employer;
    }

    public void setEmployer(String employer) {
        this.employer = employer;
    }
    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();

        char tempChar;
        for (int i = 0; i < 15; i++){
            tempChar = (char) (generator.nextInt(26) + 65);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }
    @Override
    public int compareTo(@NonNull Shift o) {
        return 0;
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
        if (d.getDay() >= 10){
            randomStringBuilder.append(Integer.toString(d.getDay()));
        }else{
            randomStringBuilder.append("0");
            randomStringBuilder.append(Integer.toString(d.getDay()));
        }
        if (d.getHour() >= 10){
            randomStringBuilder.append(Integer.toString(d.getHour()));
        }else{
            randomStringBuilder.append("0");
            randomStringBuilder.append(Integer.toString(d.getHour()));
        }
        if (d.getMinute() >= 10){
            randomStringBuilder.append(Integer.toString(d.getMinute()));
        }else{
            randomStringBuilder.append("0");
            randomStringBuilder.append(Integer.toString(d.getMinute()));
        }
        if (d.getSecond() >= 10){
            randomStringBuilder.append(Integer.toString(d.getSecond()));
        }else{
            randomStringBuilder.append("0");
            randomStringBuilder.append(Integer.toString(d.getSecond()));
        }

        return randomStringBuilder.toString();
    }
    public  double substract(){
        int hour,minute,second;
        double timeInHours=0;
        hour = endTime.getHour() - startTime.getHour();
        minute = endTime.getMinute() - startTime.getMinute();
        second = endTime.getSecond() - startTime.getSecond();
        if(hour<0){
            return 0;
        }
        if(minute < 0){
            if(hour ==0)
                return 0;
            minute+=60;
            hour--;
        }
        if(second < 0){
            if(minute == 0 && hour == 0)
                return 0;
            second +=60;
            minute--;
        }
        timeInHours += hour +minute/60.0 +second/(60*60.0);
        timeInHours = (double) Math.round(timeInHours*100)/100;
        return timeInHours;
    }
}
