package de.markhaehnel.rbtv.rocketbeanstv.objects.schedule

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import de.markhaehnel.rbtv.rocketbeanstv.utils.Time

class ScheduleItem {

    @SerializedName("id")
    @Expose
    val id: Long? = null

    @SerializedName("title")
    @Expose
    val title: String? = null

    @SerializedName("topic")
    @Expose
    val topic: String? = null

    @SerializedName("show")
    @Expose
    val show: String? = null

    @SerializedName("timeStart")
    @Expose
    val timeStart: String? = null

    @SerializedName("timeEnd")
    @Expose
    val timeEnd: String? = null

    @SerializedName("length")
    @Expose
    val length: Long? = null

    @SerializedName("type")
    @Expose
    val type: String? = null

    @SerializedName("game")
    @Expose
    val game: String? = null

    val timeStartShort: String
        get() = Time.getShortTimeFromISO(timeStart!!)
}
