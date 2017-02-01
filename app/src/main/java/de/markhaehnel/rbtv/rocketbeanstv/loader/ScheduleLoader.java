package de.markhaehnel.rbtv.rocketbeanstv.loader;

import com.google.firebase.crash.FirebaseCrash;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import de.markhaehnel.rbtv.rocketbeanstv.events.ScheduleLoadEvent;
import de.markhaehnel.rbtv.rocketbeanstv.objects.schedule.Schedule;
import de.markhaehnel.rbtv.rocketbeanstv.utils.Enums.EventStatus;
import de.markhaehnel.rbtv.rocketbeanstv.utils.NetworkHelper;

public class ScheduleLoader extends Thread {

    public void run() {
        try {
            String url = "https://rbtvapi.markhaehnel.de/schedule/next/5";

            String data = NetworkHelper.getContentFromUrl(url);
            Gson gson = new Gson();
            Schedule scheduleData = gson.fromJson(data, Schedule.class);

            EventBus.getDefault().post(new ScheduleLoadEvent(scheduleData.getSchedule(), EventStatus.OK));
        } catch(Exception e) {
            FirebaseCrash.report(e);
            e.printStackTrace();
            EventBus.getDefault().post(new ScheduleLoadEvent(EventStatus.FAILED));
        }
    }
}
