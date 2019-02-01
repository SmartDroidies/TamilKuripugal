package droid.smart.com.tamilkuripugal.extensions

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import com.google.android.material.snackbar.Snackbar


fun View.showSnackbar(msgId: Int, length: Int) {
    showSnackbar(context.getString(msgId), length)
}

fun View.showSnackbar(msg: String, length: Int) {
    showSnackbar(msg, length, null, {})
}

fun View.showSnackbar(
    msgId: Int,
    length: Int,
    actionMessageId: Int,
    action: (View) -> Unit
) {
    showSnackbar(context.getString(msgId), length, context.getString(actionMessageId), action)
}

fun View.showSnackbar(
    msg: String,
    length: Int,
    actionMessage: CharSequence?,
    action: (View) -> Unit
) {
    val snackbar = Snackbar.make(this, msg, length)
    if (actionMessage != null) {
        snackbar.setAction(actionMessage) {
            action(this)
        }.show()
    }
}

fun View.extractBitmap() : Bitmap? {

    //Get the dimensions of the view so we can re-layout the view at its current size
    //and create a bitmap of the same size
    val width = this.getWidth()
    val height = this.getHeight()

    val measuredWidth = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY)
    val measuredHeight = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)

    //Cause the view to re-layout
    this.measure(measuredWidth, measuredHeight)
    this.layout(0, 0, this.getMeasuredWidth(), this.getMeasuredHeight())

    //Create a bitmap backed Canvas to draw the view into
    val b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val c = Canvas(b)

    //Now that the view is laid out and we have a canvas, ask the view to draw itself into the canvas
    this.draw(c)

    return b

/*
    private val SHARE_FOLDER_NAME = "Tamil Kuripugal"

    this.clearFocus()
    this.setPressed(false)

    val willNotCache = this.willNotCacheDrawing()
    this.setWillNotCacheDrawing(false)

    // Reset the drawing cache background color to fully transparent
    // for the duration of this operation
    val color = this.getDrawingCacheBackgroundColor()
    this.setDrawingCacheBackgroundColor(0)

    if (color != 0) {
        this.destroyDrawingCache()
    }
    this.buildDrawingCache()
    val cacheBitmap = this.getDrawingCache()
    if (cacheBitmap == null) {
        Timber.e("failed getViewBitmap($this)")
        return null
    }

    val bitmap = Bitmap.createBitmap(cacheBitmap!!)

    // Restore the view
    this.destroyDrawingCache()
    this.setWillNotCacheDrawing(willNotCache)
    this.setDrawingCacheBackgroundColor(color)

    return bitmap*/
}

