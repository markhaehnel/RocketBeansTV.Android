package de.markhaehnel.rbtv.rocketbeanstv.vo

import com.google.gson.annotations.SerializedName

data class Schedule(
    @field:SerializedName("schedule")
    val items: List<ScheduleItem>
)