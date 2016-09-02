package de.markhaehnel.rbtv.rocketbeanstv.utils;

import android.annotation.SuppressLint;

import com.google.firebase.crash.FirebaseCrash;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Time {
    @SuppressLint("SimpleDateFormat")
    public static String getShortTimeFromISO(String timeString) {
        try {
            Date start = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZ").parse(timeString);
            return new SimpleDateFormat("HH:mm").format(start);
        } catch (ParseException e) {
            FirebaseCrash.report(e);
            e.printStackTrace();
        }
        return "-";
    }
}
