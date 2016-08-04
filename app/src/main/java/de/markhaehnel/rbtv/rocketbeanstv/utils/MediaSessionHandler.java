package de.markhaehnel.rbtv.rocketbeanstv.utils;

import android.content.Context;
import android.content.Intent;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.KeyEvent;

import org.greenrobot.eventbus.EventBus;

import de.markhaehnel.rbtv.rocketbeanstv.MainActivity;
import de.markhaehnel.rbtv.rocketbeanstv.events.TogglePlayStateEvent;

public class MediaSessionHandler {

    private static MediaSessionCompat mMediaSession;
    private static PlaybackStateCompat mPlaybackState;

    public static void setupMediaSession(Context context) {
        mMediaSession = new MediaSessionCompat(context, "rbtv");
        mMediaSession.setCallback(new MediaSessionCompat.Callback() {
            @Override
            public boolean onMediaButtonEvent(Intent mediaButtonEvent) {
                KeyEvent key = mediaButtonEvent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);

                if (key.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (key.getKeyCode()) {
                        case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                            EventBus.getDefault().post(new TogglePlayStateEvent());
                            return true;
                    }
                }
                return super.onMediaButtonEvent(mediaButtonEvent);
            }
        });
        mMediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mMediaSession.setActive(true);

        mPlaybackState = new PlaybackStateCompat.Builder()
                .setActions(PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_PLAY_PAUSE |
                            PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID | PlaybackStateCompat.ACTION_PAUSE |
                            PlaybackStateCompat.ACTION_SKIP_TO_NEXT | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)
                .setState(PlaybackStateCompat.STATE_PLAYING, 0, 1)
                .build();
        mMediaSession.setPlaybackState(mPlaybackState);
    }
}
