package com.qerat.fstweekadmin;

import java.io.Serializable;

public class MeetUpClass implements Serializable {
    private String location;
    private String date;
    private String time;


    MeetUpClass(String date, String time, String location){

        this.date=date;
        this.time=time;
        this.location=location;
    }

    MeetUpClass(){

    }


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
