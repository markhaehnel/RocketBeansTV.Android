package de.markhaehnel.rbtv.rocketbeanstv.util

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Date

object Time {


    val DAY_IN_SECONDS = 86400L

    @SuppressLint("SimpleDateFormat")
    fun getShortTime(time: Date): String {
        return SimpleDateFormat("HH:mm").format(time)
    }

    fun getUnixTime() : Long = System.currentTimeMillis() / 1000L

    fun getDayBefore(timestamp: Long) = timestamp - DAY_IN_SECONDS

    fun getDayAfter(timestamp: Long) = timestamp + DAY_IN_SECONDS

}