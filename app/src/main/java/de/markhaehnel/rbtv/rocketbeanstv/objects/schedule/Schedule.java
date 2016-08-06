package de.markhaehnel.rbtv.rocketbeanstv.objects.schedule;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Schedule {

    @SerializedName("schedule")
    @Expose
    private List<ScheduleItem> schedule = new ArrayList<ScheduleItem>();

    public List<ScheduleItem> getNextShows(int count) {
        int howMuch  = (count < schedule.size()) ? count : schedule.size();
        return schedule.subList(0, howMuch);
    }
}
