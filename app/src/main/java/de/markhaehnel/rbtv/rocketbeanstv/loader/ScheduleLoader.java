package de.markhaehnel.rbtv.rocketbeanstv.loader;

import android.annotation.SuppressLint;
import android.util.Base64;

import com.google.firebase.crash.FirebaseCrash;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.markhaehnel.rbtv.rocketbeanstv.events.ScheduleLoadEvent;
import de.markhaehnel.rbtv.rocketbeanstv.objects.schedule.Schedule;
import de.markhaehnel.rbtv.rocketbeanstv.objects.schedule.ScheduleItem;
import de.markhaehnel.rbtv.rocketbeanstv.utils.Enums.EventStatus;
import de.markhaehnel.rbtv.rocketbeanstv.utils.NetworkHelper;
import de.markhaehnel.rbtv.rocketbeanstv.utils.RandomString;

import static de.markhaehnel.rbtv.rocketbeanstv.utils.AuthHelper.SHA1;

public class ScheduleLoader extends Thread {

    private String key, secret;

    public ScheduleLoader(String key, String secret) {
        this.key = key;
        this.secret = secret;
    }

    public void run() {
        final String id = "00000000-0000-0000-0000-000000000000";

        try {
            @SuppressLint("SimpleDateFormat")
            String created = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZ").format(new Date());
            created = created.substring(0, created.length()-2) + ":" + created.substring(created.length()-2, created.length());

            String nonce = id + created + RandomString.generate(10);
            String sha1 = SHA1(nonce + created + secret);
            String url = "https://api.rocketmgmt.de/schedule";
            String b64sha1 = Base64.encodeToString(sha1.getBytes(), Base64.NO_WRAP);
            String b64nonce = Base64.encodeToString(nonce.getBytes(), Base64.NO_WRAP);

            Map<String, String> headers = new HashMap<>();
            headers.put("Accept", "application/json");
            headers.put("Authorization", "WSSE profile=\"UsernameToken\"");
            headers.put("X-WSSE", "UsernameToken Username=\"" + key + "\", PasswordDigest=\"" + b64sha1 + "\", Nonce=\"" + b64nonce + "\", Created=\"" + created + "\"");

            String data = NetworkHelper.getContentFromUrl(url, headers);
            Gson gson = new Gson();
            Schedule scheduleData = gson.fromJson(data, Schedule.class);

            EventBus.getDefault().post(new ScheduleLoadEvent(scheduleData.getNextShows(5), EventStatus.OK));
        } catch(Exception e) {
            FirebaseCrash.report(e);
            e.printStackTrace();
            EventBus.getDefault().post(new ScheduleLoadEvent(EventStatus.FAILED));
        }
    }
}
