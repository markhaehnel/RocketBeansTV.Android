package de.markhaehnel.rbtv.rocketbeanstv.loader;

import com.google.firebase.crash.FirebaseCrash;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import de.markhaehnel.rbtv.rocketbeanstv.events.ChannelInfoUpdateEvent;
import de.markhaehnel.rbtv.rocketbeanstv.objects.RBTV;
import de.markhaehnel.rbtv.rocketbeanstv.utils.Enums.EventStatus;
import de.markhaehnel.rbtv.rocketbeanstv.utils.NetworkHelper;

public class ChannelInfoLoader extends Thread {

    public void run() {
        //noinspection LoopStatementThatDoesntLoop
        while (true) {
            try {
                String response = NetworkHelper.getContentFromUrl("https://node.markhaehnel.de/rbtv/");
                Gson gson = new Gson();
                RBTV data = gson.fromJson(response, RBTV.class);
                if (data.getError() == null) {
                    EventBus.getDefault().post(new ChannelInfoUpdateEvent(data.getTitle(), data.getViewerCount(), EventStatus.OK));
                } else {
                    EventBus.getDefault().post(EventStatus.FAILED);
                }
                sleep(30000);
                break;
            } catch (Exception e) {
                FirebaseCrash.report(e);
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
