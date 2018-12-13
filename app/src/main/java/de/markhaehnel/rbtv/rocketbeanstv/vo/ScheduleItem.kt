package de.markhaehnel.rbtv.rocketbeanstv.vo

import com.google.gson.annotations.SerializedName

data class ScheduleItem(
    @field:SerializedName("id")
    val id: Int,

    @field:SerializedName("title")
    val title: String,

    @field:SerializedName("topic")
    val topic: String,

    @field:SerializedName("show")
    val show: String,

    @field:SerializedName("timeStart")
    val timeStart: String,

    @field:SerializedName("timeEnd")
    val timeEnd: String,

    @field:SerializedName("length")
    val length: Int,

    @field:SerializedName("type")
    val type: String,

    @field:SerializedName("game")
    val game: String
)