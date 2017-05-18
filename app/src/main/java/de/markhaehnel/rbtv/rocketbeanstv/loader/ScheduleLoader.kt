package de.markhaehnel.rbtv.rocketbeanstv.loader

import com.google.firebase.crash.FirebaseCrash
import com.google.gson.Gson

import org.greenrobot.eventbus.EventBus

import de.markhaehnel.rbtv.rocketbeanstv.events.ScheduleLoadEvent
import de.markhaehnel.rbtv.rocketbeanstv.objects.schedule.Schedule
import de.markhaehnel.rbtv.rocketbeanstv.utils.Enums.EventStatus
import de.markhaehnel.rbtv.rocketbeanstv.utils.NetworkHelper

class ScheduleLoader : Thread() {

    override fun run() {
        try {
            val url = "https://rbtvapi.markhaehnel.de/schedule/next/5"

            val data = NetworkHelper.getContentFromUrl(url)
            val gson = Gson()
            val scheduleData = gson.fromJson(data, Schedule::class.java)

            EventBus.getDefault().post(ScheduleLoadEvent(scheduleData.schedule, EventStatus.OK))
        } catch (e: Exception) {
            FirebaseCrash.report(e)
            e.printStackTrace()
            EventBus.getDefault().post(ScheduleLoadEvent(EventStatus.FAILED))
        }

    }
}
