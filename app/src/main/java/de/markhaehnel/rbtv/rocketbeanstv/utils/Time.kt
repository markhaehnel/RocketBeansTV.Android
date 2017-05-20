package de.markhaehnel.rbtv.rocketbeanstv.utils

import android.annotation.SuppressLint

import com.google.firebase.crash.FirebaseCrash

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date

object Time {
    @SuppressLint("SimpleDateFormat")
    fun getShortTimeFromISO(timeString: String): String {
        try {
            val start = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZ").parse(timeString)
            return SimpleDateFormat("HH:mm").format(start)
        } catch (e: ParseException) {
            FirebaseCrash.report(e)
            e.printStackTrace()
        }

        return "-"
    }
}
