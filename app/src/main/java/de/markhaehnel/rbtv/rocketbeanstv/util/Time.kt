package de.markhaehnel.rbtv.rocketbeanstv.util

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Date

object Time {
    @SuppressLint("SimpleDateFormat")
    fun getShortTime(time: Date): String {
        return SimpleDateFormat("HH:mm").format(time)
    }
}