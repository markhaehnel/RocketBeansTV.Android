package de.markhaehnel.rbtv.rocketbeanstv.utils

import android.content.Context
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat

object MediaSessionHandler {

    private var mMediaSession: MediaSessionCompat? = null
    private var mPlaybackState: PlaybackStateCompat? = null

    fun setupMediaSession(context: Context) {
        mMediaSession = MediaSessionCompat(context, "rbtv")
        mMediaSession!!.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
        mMediaSession!!.isActive = true

        mPlaybackState = PlaybackStateCompat.Builder()
                .setActions(PlaybackStateCompat.ACTION_PLAY or PlaybackStateCompat.ACTION_PLAY_PAUSE or
                        PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID or PlaybackStateCompat.ACTION_PAUSE or
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)
                .setState(PlaybackStateCompat.STATE_PLAYING, 0, 1f)
                .build()
        mMediaSession!!.setPlaybackState(mPlaybackState)
    }
}
