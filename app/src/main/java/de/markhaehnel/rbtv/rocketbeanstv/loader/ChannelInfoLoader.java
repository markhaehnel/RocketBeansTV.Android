package de.markhaehnel.rbtv.rocketbeanstv.loader;

import com.google.firebase.crash.FirebaseCrash;
import org.greenrobot.eventbus.EventBus;
import de.markhaehnel.rbtv.rocketbeanstv.events.ChannelInfoUpdateEvent;
import de.markhaehnel.rbtv.rocketbeanstv.utils.Enums.EventStatus;

public class ChannelInfoLoader extends Thread {

    public void run() {
        //noinspection LoopStatementThatDoesntLoop
        while (true) {
            try {
                EventBus.getDefault().post(new ChannelInfoUpdateEvent("Diese Funktion kehrt mit dem nächsten Update zurück!", 0, EventStatus.OK));
                //sleep(15000);
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
