
package droid.smart.com.tamilkuripugal.binding

import android.webkit.WebView
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import droid.smart.com.tamilkuripugal.extensions.loadKurippu
import droid.smart.com.tamilkuripugal.ui.util.Helper
import droid.smart.com.tamilkuripugal.vo.Kurippu
import timber.log.Timber
import javax.inject.Inject

/**
 * Binding adapters that work with a fragment instance.
 */
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
            webView.loadKurippu(kurippu)
        }
    }

}

