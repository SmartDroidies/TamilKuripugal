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

import android.webkit.WebView
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import droid.smart.com.tamilkuripugal.testing.OpenForTesting
import droid.smart.com.tamilkuripugal.ui.util.Helper
import droid.smart.com.tamilkuripugal.vo.Kurippu
import timber.log.Timber
import javax.inject.Inject

/**
 * Binding adapters that work with a fragment instance.
 */
@OpenForTesting
class FragmentBindingAdapters @Inject constructor(val fragment: Fragment) {
    @BindingAdapter("imageUrl")
    fun bindImage(imageView: ImageView, url: String?) {
        Timber.d("Image URL %s", url)
        Glide.with(fragment).load(url).into(imageView)
    }

    @BindingAdapter("localeText")
    fun localeText(view: TextView, text: String?) {
        if (fragment.context != null) {
            view.text = Helper.localeText(fragment.context!!, text)
        } else {
            view.text = text
        }
    }

    @BindingAdapter("webData")
    fun showWebData(webView: WebView, kurippu: Kurippu?) {
        if (kurippu != null && !(kurippu!!.content.isNullOrEmpty())) {
            webView.loadData(kurippu.content, "text/html", null)
        }
    }


}
