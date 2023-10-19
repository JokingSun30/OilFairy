package com.jokingsun.oilfairy.widget.manager;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Handler;
import android.view.View;

/**
 * @author Joshua
 */
public class AnimatorManager {

    private Handler handler;
    private Runnable runnable;

    public void heartBeatAnim(View view) {
        handler = new Handler();

        runnable = () -> {
            ObjectAnimator animatorY = ObjectAnimator.ofFloat(view,
                    "scaleY", 1f, 1.2f, 1f);

            ObjectAnimator animatorX = ObjectAnimator.ofFloat(view,
                    "scaleX", 1f, 1.2f, 1f);

            AnimatorSet animSet = new AnimatorSet();
            animSet.play(animatorY).with(animatorX);

            animSet.setDuration(500);
            animSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (handler != null) {
                        handler.postDelayed(runnable, 1000);
                    }
                }
            });

            animSet.start();
        };

        handler.postDelayed(runnable, 1000);
    }
}
