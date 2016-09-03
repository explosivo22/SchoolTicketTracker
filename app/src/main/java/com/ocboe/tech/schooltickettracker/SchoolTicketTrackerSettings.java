package com.ocboe.tech.schooltickettracker;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Brad on 9/3/2016.
 */
public class SchoolTicketTrackerSettings {

    public static final String PREFS_SCHOOL_TICKET_TRACKER_SETTINGS = "SchoolTicketTracker_settings";
    public static final String AUTO_UPDATE_ENABLED = "autoUpdateEnabled";

    private static SchoolTicketTrackerSettings instance;

    private final SharedPreferences prefs;

    public static SchoolTicketTrackerSettings getInstance(Context context){
        if (instance == null){
            instance = new SchoolTicketTrackerSettings(context.getApplicationContext());
        }
        return instance;
    }

    private SchoolTicketTrackerSettings(Context context){
        prefs = context.getSharedPreferences(PREFS_SCHOOL_TICKET_TRACKER_SETTINGS, Context.MODE_PRIVATE);
    }

    public boolean isAutoUpdateEnabled() {
        return prefs.getBoolean(AUTO_UPDATE_ENABLED, true);
    }
}
