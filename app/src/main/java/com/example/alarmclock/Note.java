package com.example.alarmclock;

import java.io.Serializable;

public class Note implements Serializable {
    private int id, hour, minute;
    private String message;
    private boolean state;

    public Note(int id, int hour, int minute, String message, boolean state) {
        this.id = id;
        this.hour = hour;
        this.minute = minute;
        this.message = message;
        this.state = state;
    }

    public Note(int hour, int minute, String message) {
        this.hour = hour;
        this.minute = minute;
        this.message = message;
        this.state = true;
    }

    public int getId() {
        return id;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public String getTime(){
        String hour = String.valueOf(this.hour), minute = String.valueOf(this.minute);
        if (this.hour < 10){
            hour = "0" + this.hour;
        }
        if (this.minute < 10){
            minute = "0" + this.minute;
        }
        return hour + ":" + minute;
    }

    public String getMessage() {
        return message;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }
}
//Класс для создание списка напоминаний