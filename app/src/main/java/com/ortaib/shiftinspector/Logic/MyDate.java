package com.ortaib.shiftinspector.Logic;

import java.io.Serializable;

/**
 * Created by Ortaib on 02/08/2018.
 */

public class MyDate implements Serializable {
    private int year,month,day,hour,minute,second;

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }
    public MyDate(){}
    public MyDate(int year, int month, int day, int hour, int minute, int second) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;

        this.minute = minute;
        this.second = second;
    }

    public int getYear() {

        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getHour() {
        return hour;
    }

    @Override
    public String toString() {
        StringBuilder toString = new StringBuilder();
        if (day >= 10){
            toString.append(Integer.toString(day));
            toString.append("-");
        }else{
            toString.append("0");
            toString.append(Integer.toString(day));
            toString.append("-");
        }
        if (month > 9) {
            toString.append(Integer.toString(month));
            toString.append("-");
        } else {
            toString.append("0");
            toString.append(Integer.toString(month));
            toString.append("-");

        }
        toString.append(Integer.toString(year));
        toString.append(" ");


        if (hour >= 10){
            toString.append(Integer.toString(hour));
            toString.append(":");
        }else{
            toString.append("0");
            toString.append(Integer.toString(hour));
            toString.append(":");

        }
        if (minute >= 10){
            toString.append(Integer.toString(minute));
            toString.append(":");

        }else{
            toString.append("0");
            toString.append(Integer.toString(minute));
            toString.append(":");

        }
        if (second >= 10){
            toString.append(Integer.toString(second));

        }else{
            toString.append("0");
            toString.append(Integer.toString(second));
        }

        return toString.toString();
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }
}
