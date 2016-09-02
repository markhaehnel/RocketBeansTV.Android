package de.markhaehnel.rbtv.rocketbeanstv.loader;


import android.util.Log;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import de.markhaehnel.rbtv.rocketbeanstv.BuildConfig;
import de.markhaehnel.rbtv.rocketbeanstv.events.StreamUrlChangeEvent;
import de.markhaehnel.rbtv.rocketbeanstv.objects.Stream;
import de.markhaehnel.rbtv.rocketbeanstv.objects.VideoId;

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
            String response = NetworkHelper.getContentFromUrl("https://node.markhaehnel.de/rbtv/");

            Gson gson = new Gson();
            VideoId videoId = gson.fromJson(response, VideoId.class);

            String url = "https://www.youtube.com/get_video_info?&video_id=" + videoId.videoId;
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


            EventBus.getDefault().post(new StreamUrlChangeEvent(stream, videoId.videoId, EventStatus.OK));
        } catch (Exception e) {
            e.printStackTrace();
            EventBus.getDefault().post(new StreamUrlChangeEvent(EventStatus.FAILED));
        }


    }
}
