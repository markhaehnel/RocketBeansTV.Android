package de.markhaehnel.rbtv.rocketbeanstv.utils

import android.view.View
import android.widget.TextView

class NotificationManager(val container: View, val titleView: TextView, val contentView: TextView) {

    fun ShowNotification(title: String, content: String, duration: Long) {
        titleView.text = title
        contentView.text = content

        container.clearAnimation()
        container.visibility = View.VISIBLE
        container.alpha = 0.0f
        container.animate()
                .setDuration(250)
                .alpha(1.0f)
                .withEndAction { container.animate()
                        .setStartDelay(duration)
                        .alpha(0.0f)
                        .withEndAction { container.visibility = View.INVISIBLE} }

    }
}