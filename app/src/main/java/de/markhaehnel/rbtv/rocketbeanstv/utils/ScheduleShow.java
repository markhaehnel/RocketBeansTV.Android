package de.markhaehnel.rbtv.rocketbeanstv.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScheduleShow {

    public ScheduleShow(String title, String topic, String show, String timeStart, String timeEnd, int duration, String type) {
        this.title = title;
        this.topic = topic;
        this.show = show;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.duration = duration;
        this.type = type;
    }

    private String title = "";
    private String topic = "";
    private String show = "";
    private String timeStart = "";
    private String timeEnd = "";
    private int duration = 0;
    private String type = "";

    public String getTitle() {
        return title;
    }

    public String getTopic() {
        return topic;
    }

    public String getShow() {
        return show;
    }

    public String getTimeStart() {
        try {
            Date start = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZ").parse(timeStart);
            return new SimpleDateFormat("HH:mm").format(start);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "- ";
    }

    public String getTimeEnd() {
        return timeEnd;
    }

    public int getDuration() {
        return duration;
    }

    public String getType() {
        if (type.length() > 1) {
            return type.substring(0, 1).toUpperCase() + type.substring(1);
        }
        return type;
    }
}
