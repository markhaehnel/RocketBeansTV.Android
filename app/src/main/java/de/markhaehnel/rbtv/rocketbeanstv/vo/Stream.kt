package de.markhaehnel.rbtv.rocketbeanstv.vo

import androidx.room.Entity
import androidx.room.TypeConverters
import com.google.gson.annotations.SerializedName
import de.markhaehnel.rbtv.rocketbeanstv.db.RbtvTypeConverters

@Entity(primaryKeys = ["videoId"])
@TypeConverters(RbtvTypeConverters::class)
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