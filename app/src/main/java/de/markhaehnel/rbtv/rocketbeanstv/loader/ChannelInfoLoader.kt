package de.markhaehnel.rbtv.rocketbeanstv.loader

import com.google.firebase.crash.FirebaseCrash
import com.google.gson.Gson

import org.greenrobot.eventbus.EventBus
import de.markhaehnel.rbtv.rocketbeanstv.events.ChannelInfoUpdateEvent
import de.markhaehnel.rbtv.rocketbeanstv.objects.RBTV
import de.markhaehnel.rbtv.rocketbeanstv.objects.schedule.ScheduleItem
import de.markhaehnel.rbtv.rocketbeanstv.utils.Enums.EventStatus
import de.markhaehnel.rbtv.rocketbeanstv.utils.NetworkHelper

class ChannelInfoLoader : Thread() {

    override fun run() {

        while (true) {
            try {

                val url = "https://rbtvapi.markhaehnel.de/schedule/current"
                val urlViewer = "https://rbtvapi.markhaehnel.de/stream"

                val response = NetworkHelper.getContentFromUrl(url)
                val responseViewer = NetworkHelper.getContentFromUrl(urlViewer)

                val gson = Gson()
                val scheduleItem = gson.fromJson(response, ScheduleItem::class.java)
                val rbtv = gson.fromJson(responseViewer, RBTV::class.java)

                EventBus.getDefault().post(ChannelInfoUpdateEvent(scheduleItem, rbtv, EventStatus.OK))

                Thread.sleep(30000)
            } catch (e: Exception) {
                FirebaseCrash.report(e)
                e.printStackTrace()
                EventBus.getDefault().post(ChannelInfoUpdateEvent(EventStatus.FAILED))
                if (e is InterruptedException) {
                    Thread.currentThread().interrupt()
                }
                return
            }

        }
    }
}
