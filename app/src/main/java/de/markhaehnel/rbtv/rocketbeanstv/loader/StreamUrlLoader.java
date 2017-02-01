package de.markhaehnel.rbtv.rocketbeanstv.loader;


import com.google.firebase.crash.FirebaseCrash;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import de.markhaehnel.rbtv.rocketbeanstv.events.StreamUrlChangeEvent;
import de.markhaehnel.rbtv.rocketbeanstv.objects.Stream;
import de.markhaehnel.rbtv.rocketbeanstv.objects.RBTV;

import de.markhaehnel.rbtv.rocketbeanstv.utils.Enums.EventStatus;
import de.markhaehnel.rbtv.rocketbeanstv.utils.NetworkHelper;
import de.markhaehnel.rbtv.rocketbeanstv.utils.PlaylistHelper;


public class StreamUrlLoader extends Thread {

    private String mResolution;

    public StreamUrlLoader(String resolution) {
        mResolution = resolution;
    }

    public void run() {

        try {
            String response = NetworkHelper.getContentFromUrl("https://rbtvapi.markhaehnel.de/stream");

            Gson gson = new Gson();
            RBTV data = gson.fromJson(response, RBTV.class);

            if (data.getError() == null) {
                String url = "https://www.youtube.com/get_video_info?&video_id=" + data.getVideoId();
                Map<String, String> headers = new HashMap<>();
                headers.put("User-Agent", "Mozilla/5.0");

                String ytResponse = NetworkHelper.getContentFromUrl(url, headers);

                HashMap<String, String> parameters = new HashMap<>();
                for (String param : ytResponse.split(Pattern.quote("&"))) {
                    String[] line = param.split(Pattern.quote("="));
                    if (line.length == 2)
                        parameters.put(line[0], URLDecoder.decode(line[1], "UTF-8"));
                }

                String hlsUrl = parameters.get("hlsvp");

                List<Stream> streams = PlaylistHelper.getStreamsFromM3U(NetworkHelper.getContentFromUrl(hlsUrl));

                Stream stream = PlaylistHelper.getStreamByResolution(streams, mResolution);


                EventBus.getDefault().post(new StreamUrlChangeEvent(stream, data.getVideoId(), EventStatus.OK));
            } else {
                EventBus.getDefault().post(new StreamUrlChangeEvent(EventStatus.FAILED));
            }
        } catch (Exception e) {
            FirebaseCrash.report(e);
            e.printStackTrace();
            EventBus.getDefault().post(new StreamUrlChangeEvent(EventStatus.FAILED));
        }


    }
}
