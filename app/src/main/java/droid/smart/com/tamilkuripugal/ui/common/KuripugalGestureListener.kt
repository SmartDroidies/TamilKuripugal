package droid.smart.com.tamilkuripugal.ui.common

import android.view.GestureDetector
import android.view.MotionEvent
import timber.log.Timber

abstract class KuripugalGestureListener : GestureDetector.SimpleOnGestureListener() {

    override fun onDown(event: MotionEvent): Boolean {
        Timber.d("Kuripugal OnDown Event")
        return true
    }

    override fun onFling(
        event1: MotionEvent, event2: MotionEvent,
        velocityX: Float, velocityY: Float
    ): Boolean {

        val result = false
        try {
            val diffY = event2.y - event1.y
            val diffX = event2.x - event1.x
            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        onSwipeRight()
                    } else {
                        onSwipeLeft()
                    }
                }
            } else {
                if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        onSwipeBottom()
                    } else {
                        onSwipeTop()
                    }
                }
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
        }

        return result
    }

    open fun onSwipeRight(): Boolean {
        return false
    }

    open fun onSwipeLeft(): Boolean {
        return false
    }

    fun onSwipeTop(): Boolean {
        return false
    }

    fun onSwipeBottom(): Boolean {
        return false
    }

    companion object {
        private val SWIPE_THRESHOLD = 100
        private val SWIPE_VELOCITY_THRESHOLD = 100
    }

}





