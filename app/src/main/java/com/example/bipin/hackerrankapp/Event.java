package com.example.bipin.hackerrankapp;

/**
 * Created by bipin on 11/8/17.
 */

public class Event {
    public final String title;
    public final String pleadge;
    public final String backers;
    public final int days;

    public Event(String ProjectTitle,String ProjectPleadge,String ProjectBackers,int ProjectDays){
        title=ProjectTitle;
        pleadge=ProjectPleadge;
        backers=ProjectBackers;
        days=ProjectDays;
    }
}
