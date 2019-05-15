package de.markhaehnel.rbtv.rocketbeanstv.util

import android.os.Handler
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

class IntervalTask(
    lifecycle: Lifecycle,
    interval: Long,
    task: Runnable
) : LifecycleObserver {

    private val handler = Handler()
    private lateinit var intervalRunnable : Runnable

    init {
        intervalRunnable = Runnable {
            try {
                task.run()
            } finally {
                handler.postDelayed(intervalRunnable, interval)
            }
        }
        lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun start() {
        intervalRunnable.run()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun stop() {
        handler.removeCallbacks(intervalRunnable)
    }
}
