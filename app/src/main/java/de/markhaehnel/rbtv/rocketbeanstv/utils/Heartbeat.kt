package de.markhaehnel.rbtv.rocketbeanstv.utils

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

object Heartbeat {
    fun doHeartbeat(firebaseAnalytics: FirebaseAnalytics) {
        val HEARTBEAT = "HEARTBEAT"
        val bundle = Bundle()
        bundle.putString(HEARTBEAT, HEARTBEAT)
        firebaseAnalytics.logEvent(HEARTBEAT, bundle)
    }
}
