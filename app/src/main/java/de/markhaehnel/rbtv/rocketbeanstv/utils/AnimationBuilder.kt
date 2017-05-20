package de.markhaehnel.rbtv.rocketbeanstv.utils

import android.view.animation.AlphaAnimation
import android.view.animation.Animation

object AnimationBuilder {
    val fadeOutAnimation: Animation
        get() {
            val anim = AlphaAnimation(1.0f, 0.0f)
            anim.duration = 500
            return anim
        }

    val delayedFadeOutAnimation: Animation
        get() {
            val anim = AlphaAnimation(1.0f, 0.0f)
            anim.startOffset = 8000
            anim.duration = 500
            return anim
        }
}
