package de.markhaehnel.rbtv.rocketbeanstv.loader;


import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import de.markhaehnel.rbtv.rocketbeanstv.utils.Enums.EventStatus;
import static de.markhaehnel.rbtv.rocketbeanstv.utils.Enums.Quality;
import de.markhaehnel.rbtv.rocketbeanstv.events.StreamUrlChangeEvent;
import de.markhaehnel.rbtv.rocketbeanstv.objects.twitch.AccessToken;
import de.markhaehnel.rbtv.rocketbeanstv.utils.NetworkHelper;


public class StreamUrlLoader extends Thread {

    Quality mQuality;

    public StreamUrlLoader(Quality quality) {
        mQuality = quality;
    }

    public void run() {
        String qualityString;
        switch (mQuality) {
            case MOBILE:
                qualityString = "mobile";
                break;
            case LOW:
                qualityString = "low";
                break;
            case MEDIUM:
                qualityString = "medium";
                break;
            case HIGH:
                qualityString = "high";
                break;
            case CHUNKED:
            default:
                qualityString = "chunked";
                break;
        }

        try {
            String data = NetworkHelper.getContentFromUrl("http://api.twitch.tv/api/channels/rocketbeanstv/access_token");

            Gson gson = new Gson();
            AccessToken accessToken = gson.fromJson(data, AccessToken.class);

            String token = accessToken.token;
            String sig = accessToken.sig;

            String playlistUrl = "http://usher.twitch.tv/api/channel/hls/rocketbeanstv.m3u8?player=twitchweb&token=" + token + "&sig=" + sig + "&allow_audio_only=true&allow_source=true&type=any&p=" + Math.round(Math.random() * 10000);

            InputStream is = new URL(playlistUrl).openStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(is));
            String line = "";
            while ((line = in.readLine()) != null) {
                if (line.contains("VIDEO=\"" + qualityString + "\"")) {
                    EventBus.getDefault().post(new StreamUrlChangeEvent(in.readLine(), EventStatus.OK));
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            EventBus.getDefault().post(new StreamUrlChangeEvent(EventStatus.FAILED));
        }
    }
}
