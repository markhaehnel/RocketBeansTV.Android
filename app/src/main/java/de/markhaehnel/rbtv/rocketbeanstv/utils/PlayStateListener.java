package de.markhaehnel.rbtv.rocketbeanstv.utils;

import com.devbrackets.android.exomedia.ui.widget.VideoView;
import com.google.firebase.crash.FirebaseCrash;

import org.greenrobot.eventbus.EventBus;

import de.markhaehnel.rbtv.rocketbeanstv.events.BufferUpdateEvent;
import de.markhaehnel.rbtv.rocketbeanstv.events.BufferUpdateEvent.BufferState;

public class PlayStateListener extends Thread {

    private VideoView mVideoView;

    public PlayStateListener(VideoView videoView) {
        mVideoView = videoView;
    }

    public void run() {
        while (true) {
            try {
                EventBus.getDefault().post(new BufferUpdateEvent(BufferState.BUFFERING_PROGRESS));
                if (mVideoView.isPlaying()) {
                    EventBus.getDefault().post(new BufferUpdateEvent(BufferState.BUFFERING_END));
                }
                sleep(500);
            } catch (Exception e) {
                FirebaseCrash.report(e);
                e.printStackTrace();
                EventBus.getDefault().post(new BufferUpdateEvent(BufferState.BUFFERING_END));
                if (e instanceof InterruptedException) {
                    Thread.currentThread().interrupt();
                }
                return;
            }
        }
    }
}
