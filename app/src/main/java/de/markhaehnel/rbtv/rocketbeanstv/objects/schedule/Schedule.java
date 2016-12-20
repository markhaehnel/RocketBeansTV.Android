package de.markhaehnel.rbtv.rocketbeanstv.objects.schedule;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

@Getter
public class Schedule {

    @SerializedName("schedule")
    @Expose
    private List<ScheduleItem> schedule = new ArrayList<ScheduleItem>();
}
