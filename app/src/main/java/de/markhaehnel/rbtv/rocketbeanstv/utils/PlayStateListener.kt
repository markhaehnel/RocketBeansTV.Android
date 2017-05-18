package de.markhaehnel.rbtv.rocketbeanstv.utils

import com.devbrackets.android.exomedia.ui.widget.VideoView
import com.google.firebase.crash.FirebaseCrash

import org.greenrobot.eventbus.EventBus

import de.markhaehnel.rbtv.rocketbeanstv.events.BufferUpdateEvent
import de.markhaehnel.rbtv.rocketbeanstv.events.BufferUpdateEvent.BufferState

class PlayStateListener(private val mVideoView: VideoView) : Thread() {

    override fun run() {
        while (true) {
            try {
                EventBus.getDefault().post(BufferUpdateEvent(BufferState.BUFFERING_PROGRESS))
                if (mVideoView.isPlaying) {
                    EventBus.getDefault().post(BufferUpdateEvent(BufferState.BUFFERING_END))
                }
                Thread.sleep(500)
            } catch (e: Exception) {
                FirebaseCrash.report(e)
                e.printStackTrace()
                EventBus.getDefault().post(BufferUpdateEvent(BufferState.BUFFERING_END))
                if (e is InterruptedException) {
                    Thread.currentThread().interrupt()
                }
                return
            }

        }
    }
}
