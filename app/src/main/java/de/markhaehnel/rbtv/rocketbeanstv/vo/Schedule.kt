package de.markhaehnel.rbtv.rocketbeanstv.vo

import com.google.gson.annotations.SerializedName

data class Schedule(
    val `data`: List<ScheduleDay>,
    val success: Boolean
)

data class ScheduleDay(
    val date: String,
    @field:SerializedName("elements")
    val shows: List<ScheduleElement>
)

data class ScheduleElement(
    val bohnen: List<Beans>,
    val duration: Int,
    val durationClass: Int,
    val episodeId: Int,
    val episodeImage: String,
    val id: Int,
    val showId: Int,
    val streamExclusive: Boolean,
    val timeEnd: String,
    val timeStart: String,
    val title: String,
    val topic: String,
    val type: String
)

data class Beans(
    val episodeCount: Int,
    val images: List<Image>,
    val mgmtid: Int,
    val name: String,
    val role: String
)

data class Image(
    val height: Int,
    val name: String,
    val url: String,
    val width: Int
)