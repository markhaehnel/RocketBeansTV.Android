package de.markhaehnel.rbtv.rocketbeanstv.binding

import android.view.View
import androidx.databinding.BindingAdapter

/**
 * Data Binding adapters specific to the app.
 */
object BindingAdapters {
    @JvmStatic
    @BindingAdapter("visibleGone")
    fun showHide(view: View, show: Boolean) {
        view.visibility = if (show) View.VISIBLE else View.GONE
    }
}