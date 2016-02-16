package de.markhaehnel.rbtv.rocketbeanstv;

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

public class AnimationBuilder {
    public static Animation getFadeInAnimation() {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(500);
        return anim;
    }

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

    public static Animation getTransparentFadeInAnimation() {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 0.75f);
        anim.setDuration(500);
        return anim;
    }

    public static Animation createDelayedFadeInAnimation(int offset) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setStartOffset(offset);
        anim.setDuration(500);
        anim.setFillAfter(true);
        return anim;
    }
}
