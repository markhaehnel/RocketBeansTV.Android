package de.markhaehnel.rbtv.rocketbeanstv.utils;

import android.annotation.SuppressLint;

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
            e.printStackTrace();
        }
        return "-";
    }
}
