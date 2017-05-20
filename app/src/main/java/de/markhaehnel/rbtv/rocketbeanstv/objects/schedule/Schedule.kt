package de.markhaehnel.rbtv.rocketbeanstv.objects.schedule

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.util.ArrayList


class Schedule {
    @SerializedName("schedule")
    @Expose
    public val schedule = ArrayList<ScheduleItem>()
}
