package de.markhaehnel.rbtv.rocketbeanstv.loader;

import com.google.gson.Gson;
import org.greenrobot.eventbus.EventBus;
import java.io.IOException;
import de.markhaehnel.rbtv.rocketbeanstv.events.ChannelInfoUpdateEvent;
import de.markhaehnel.rbtv.rocketbeanstv.objects.twitch.Streams;
import de.markhaehnel.rbtv.rocketbeanstv.utils.Enums.EventStatus;
import de.markhaehnel.rbtv.rocketbeanstv.utils.NetworkHelper;

public class ChannelInfoLoader extends Thread {

    public void run() {
        //noinspection InfiniteLoopStatement
        while (true) {
            try {
                String data = NetworkHelper.getContentFromUrl("https://api.twitch.tv/kraken/streams/rocketbeanstv");
                Gson gson = new Gson();
                Streams streams = gson.fromJson(data, Streams.class);
                if (streams != null && streams.stream != null && streams.stream.channel != null) {
                    EventBus.getDefault().post(new ChannelInfoUpdateEvent(streams.stream.channel.status, streams.stream.viewers, EventStatus.OK));
                }
                sleep(15000);
            } catch (Exception e) {
                e.printStackTrace();
                EventBus.getDefault().post(new ChannelInfoUpdateEvent(EventStatus.FAILED));
                if (e instanceof InterruptedException) {
                    Thread.currentThread().interrupt();
                }
                return;
            }
        }
    }
}
