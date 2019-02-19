package de.markhaehnel.rbtv.rocketbeanstv.vo

import com.google.gson.annotations.SerializedName
import java.util.*

data class RbtvServiceInfo(
    @field:SerializedName("data")
    val service: Data
)

data class Data(
    @field:SerializedName("streamInfo")
    val streamInfo: StreamInfo,
    @field:SerializedName("websocket")
    val webSocket: WebSocket
)

data class StreamInfo(
    @field:SerializedName("info")
    val showInfo: ShowInfo,
    val twitchChannel: String,
    val youtubeToken: String
)

data class WebSocket(
    val url: String,
    val path: String
)

data class ShowInfo(
    val progress: Double,
    val showId: Int,
    val timeEnd: Date,
    val timeStart: Date,
    val title: String,
    val topic: String,
    val type: String,
    val viewers: Viewers
) {
    fun isLive() : Boolean = type == "live"
    fun isRerun() : Boolean = type == "rerun"
}

data class Viewers(
    val total: Int,
    val twitch: Int,
    val youtube: Int
)
