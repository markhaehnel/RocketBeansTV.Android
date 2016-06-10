package de.markhaehnel.rbtv.rocketbeanstv.utility;

import android.content.Context;
import android.content.Intent;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.KeyEvent;

import de.markhaehnel.rbtv.rocketbeanstv.MainActivity;

public class MediaSessionHandler {

    private static MediaSessionCompat mediaSession;
    private static PlaybackStateCompat playbackState;

    public static void setupMediaSession(Context context) {
        mediaSession = new MediaSessionCompat(context, "rbtv");
        mediaSession.setCallback(new MediaSessionCompat.Callback() {
            @Override
            public boolean onMediaButtonEvent(Intent mediaButtonEvent) {
                KeyEvent key = mediaButtonEvent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);

                if (key.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (key.getKeyCode()) {
                        case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                            MainActivity.getInstance().togglePlayState();
                            return true;
                    }
                }
                return super.onMediaButtonEvent(mediaButtonEvent);
            }
        });
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mediaSession.setActive(true);

        playbackState = new PlaybackStateCompat.Builder()
                .setActions(PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_PLAY_PAUSE |
                            PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID | PlaybackStateCompat.ACTION_PAUSE |
                            PlaybackStateCompat.ACTION_SKIP_TO_NEXT | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)
                .setState(PlaybackStateCompat.STATE_PLAYING, 0, 1)
                .build();
        mediaSession.setPlaybackState(playbackState);
    }
}
