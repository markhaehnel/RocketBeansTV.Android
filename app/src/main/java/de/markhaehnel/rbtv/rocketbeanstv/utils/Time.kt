package de.markhaehnel.rbtv.rocketbeanstv.utils

import android.annotation.SuppressLint


import java.text.ParseException
import java.text.SimpleDateFormat

object Time {
    @SuppressLint("SimpleDateFormat")
    fun getShortTimeFromISO(timeString: String): String {
        try {
            val start = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZ").parse(timeString)
            return SimpleDateFormat("HH:mm").format(start)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return "-"
    }
}
