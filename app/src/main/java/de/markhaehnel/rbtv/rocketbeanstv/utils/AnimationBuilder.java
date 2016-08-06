package de.markhaehnel.rbtv.rocketbeanstv.utils;

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

public class AnimationBuilder {
    public static Animation getFadeOutAnimation() {
        AlphaAnimation anim = new AlphaAnimation(1.0f, 0.0f);
        anim.setDuration(500);
        return anim;
    }

    public static Animation getDelayedFadeOutAnimation() {
        AlphaAnimation anim = new AlphaAnimation(1.0f, 0.0f);
        anim.setStartOffset(8000);
        anim.setDuration(500);
        return anim;
    }
}
