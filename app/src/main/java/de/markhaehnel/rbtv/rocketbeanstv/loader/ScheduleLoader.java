package de.markhaehnel.rbtv.rocketbeanstv.loader;

import com.google.firebase.crash.FirebaseCrash;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import de.markhaehnel.rbtv.rocketbeanstv.events.ScheduleLoadEvent;
import de.markhaehnel.rbtv.rocketbeanstv.objects.schedule.Schedule;
import de.markhaehnel.rbtv.rocketbeanstv.utils.Authentication;
import de.markhaehnel.rbtv.rocketbeanstv.utils.Enums.EventStatus;
import de.markhaehnel.rbtv.rocketbeanstv.utils.NetworkHelper;

public class ScheduleLoader extends Thread {

    private String key, secret;

    public ScheduleLoader(String key, String secret) {
        this.key = key;
        this.secret = secret;
    }

    public void run() {
        final String id = "00000000-0000-0000-0000-000000000000";

        try {
            String url = "https://api.rocketmgmt.de/schedule/next/5";

            String data = NetworkHelper.getContentFromUrl(url, Authentication.getAuthenticationHeaders(key, secret));
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
