package com.example.auraenglish.practice.utils

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Activity
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.ProgressBar
import androidx.core.view.isVisible
import com.example.auraenglish.R

// Hiệu ứng UI practice
object PracticeUiEffects {

    fun playEnterTransition(activity: Activity) {
        activity.overridePendingTransition(
            R.anim.practice_activity_enter,
            R.anim.practice_activity_exit
        )
    }

    // Fade+slide từng child
    fun staggerChildren(container: android.view.ViewGroup, startDelay: Long = 40L) {
        for (i in 0 until container.childCount) {
            val child = container.getChildAt(i)
            child.alpha = 0f
            child.translationY = 28f
            child.animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(startDelay * i)
                .setDuration(320)
                .setInterpolator(DecelerateInterpolator())
                .start()
        }
    }

    fun popIn(view: View) {
        view.startAnimation(AnimationUtils.loadAnimation(view.context, R.anim.practice_pop_in))
    }

    fun slideUp(view: View) {
        view.startAnimation(AnimationUtils.loadAnimation(view.context, R.anim.practice_slide_up))
    }

    fun showMenu(view: View) {
        view.isVisible = true
        view.startAnimation(AnimationUtils.loadAnimation(view.context, R.anim.practice_menu_in))
    }

    fun hideMenu(view: View) {
        view.animate()
            .alpha(0f)
            .scaleX(0.94f)
            .scaleY(0.94f)
            .setDuration(140)
            .withEndAction {
                view.isVisible = false
                view.alpha = 1f
                view.scaleX = 1f
                view.scaleY = 1f
            }
            .start()
    }

    fun pulse(view: View) {
        view.animate()
            .scaleX(0.94f)
            .scaleY(0.94f)
            .setDuration(80)
            .withEndAction {
                view.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(140)
                    .setInterpolator(OvershootInterpolator())
                    .start()
            }
            .start()
    }

    fun animateProgress(bar: ProgressBar, to: Int, duration: Long = 700L) {
        val animator = ObjectAnimator.ofInt(bar, "progress", 0, to)
        animator.duration = duration
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.start()
    }

    fun animateWidth(view: View, targetWidth: Int, duration: Long = 550L) {
        val params = view.layoutParams
        val start = params.width.coerceAtLeast(0)
        ValueAnimator.ofInt(start, targetWidth).apply {
            this.duration = duration
            interpolator = DecelerateInterpolator()
            addUpdateListener {
                params.width = it.animatedValue as Int
                view.layoutParams = params
            }
            start()
        }
    }

    fun bookmarkToggle(view: View, active: Boolean) {
        view.animate()
            .scaleX(0.7f)
            .scaleY(0.7f)
            .setDuration(90)
            .withEndAction {
                view.alpha = if (active) 1f else 0.45f
                view.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(180)
                    .setInterpolator(OvershootInterpolator())
                    .start()
            }
            .start()
    }
}
