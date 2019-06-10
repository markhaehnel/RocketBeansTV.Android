package de.markhaehnel.rbtv.rocketbeanstv.vo

import com.google.gson.annotations.SerializedName
import de.markhaehnel.rbtv.rocketbeanstv.util.Time
import java.util.*

data class Schedule(
    @field:SerializedName("data")
    val days: List<ScheduleDay>,
    val success: Boolean
)

data class ScheduleDay(
    val date: String,
    @field:SerializedName("elements")
    val items: List<ScheduleItem>
)

data class ScheduleItem(
    val bohnen: List<Beans>,
    val duration: Int,
    val durationClass: Int,
    val episodeId: Int,
    val episodeImage: String,
    val id: Int,
    val showId: Int,
    val streamExclusive: Boolean,
    val timeEnd: Date,
    val timeStart: Date,
    val title: String,
    val topic: String,
    val type: String
) {
    fun isCurrentlyRunning() : Boolean {
        val currentTime = System.currentTimeMillis()
        return this.timeStart.time <= currentTime && this.timeEnd.time > currentTime
    }

    fun isLive() : Boolean = type == "live"
    fun isPremiere() : Boolean = type == "premiere"
    fun isRerun() : Boolean = type == "rerun"

    fun getShortTimeStart() : String {
        return Time.getShortTime(timeStart)
    }
}

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