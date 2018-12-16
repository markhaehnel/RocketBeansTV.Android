package de.markhaehnel.rbtv.rocketbeanstv.vo

import com.google.gson.annotations.SerializedName

data class Stream(
    @field:SerializedName("videoId")
    val videoId: String,

    @field:SerializedName("viewerCount")
    val viewerCount: Int,

    @field:SerializedName("cameras")
    val cameras: List<String>,

    @field:SerializedName("error")
    val error: String?
)