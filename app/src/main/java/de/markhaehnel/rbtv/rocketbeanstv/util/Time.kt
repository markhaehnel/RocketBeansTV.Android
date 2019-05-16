package de.markhaehnel.rbtv.rocketbeanstv.util

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

object Time {


    val DAY_IN_SECONDS = 86400L

    @SuppressLint("SimpleDateFormat")
    fun getShortTime(time: Date): String {
        return SimpleDateFormat("HH:mm").format(time)
    }

    fun getUnixTime() : Long = System.currentTimeMillis() / 1000L

    fun getDayBefore(timestamp: Long = getUnixTime()) = timestamp - DAY_IN_SECONDS
    fun getDaysBefore(timestamp: Long = getUnixTime(), n: Int) = timestamp - (n * DAY_IN_SECONDS)

    fun getDayAfter(timestamp: Long = getUnixTime()) = timestamp + DAY_IN_SECONDS
    fun getDaysAfter(timestamp: Long = getUnixTime(), n: Int) = timestamp + (n * DAY_IN_SECONDS)

}