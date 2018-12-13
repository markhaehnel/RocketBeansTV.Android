package de.markhaehnel.rbtv.rocketbeanstv.vo

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.SerializedName
import de.markhaehnel.rbtv.rocketbeanstv.db.RbtvTypeConverters

@Entity(primaryKeys = ["items"])
@TypeConverters(RbtvTypeConverters::class)
data class Schedule(
    @field:SerializedName("schedule")
    val items: List<ScheduleItem>
)