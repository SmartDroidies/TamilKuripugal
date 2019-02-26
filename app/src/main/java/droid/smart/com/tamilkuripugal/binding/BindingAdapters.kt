/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package droid.smart.com.tamilkuripugal.binding

import android.text.Html
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import timber.log.Timber
import java.util.*

/**
 * Data Binding adapters specific to the app.
 */
object BindingAdapters {
    @JvmStatic
    @BindingAdapter("visibleGone")
    fun showHide(view: View, show: Boolean) {
        view.visibility = if (show) View.VISIBLE else View.GONE
    }

    @JvmStatic
    @BindingAdapter("imageSrc")
    fun showImageSrc(view: ImageView, imageSrc: Int) {
        view.setImageResource(imageSrc)
    }

    @JvmStatic
    @BindingAdapter("touchListener")
    fun attachTouchListener(view: View, touchLister: View.OnTouchListener) {
        view.setOnTouchListener(touchLister)
    }

    @JvmStatic
    @BindingAdapter("millis")
    fun showTime(view: TextView, unixTime: Long) {
        Timber.d("Tip update timestamp %s", unixTime)
        val kurippuDate = Date(unixTime * 1000)
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss a", Locale.US)
        view.setText(sdf.format(kurippuDate))
    }

    @JvmStatic
    @BindingAdapter("htmlText")
    fun showHtmlText(view: TextView, htmlText: String?) {
        if (htmlText != null && !(htmlText!!.isNullOrEmpty())) {
            view.text = Html.fromHtml(htmlText)
        }
    }

}


