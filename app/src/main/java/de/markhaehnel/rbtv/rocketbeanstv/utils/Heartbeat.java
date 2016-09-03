package de.markhaehnel.rbtv.rocketbeanstv.utils;

import android.os.Bundle;
import com.google.firebase.analytics.FirebaseAnalytics;

public final class Heartbeat {
    public static void doHeartbeat(FirebaseAnalytics firebaseAnalytics) {
        final String HEARTBEAT = "HEARTBEAT";
        Bundle bundle = new Bundle();
        bundle.putString(HEARTBEAT, HEARTBEAT);
        firebaseAnalytics.logEvent(HEARTBEAT, bundle);
    }
}
