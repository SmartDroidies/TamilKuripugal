package droid.smart.com.tamilkuripugal.extensions

import android.content.Context
import android.content.res.Configuration
import android.text.TextUtils
import timber.log.Timber
import java.util.*

const val defaultLocale = "ta"

fun Context.setDefaultLocale() {
    setLocale(defaultLocale)
}

fun Context.setLocale(locale : String) {
    Timber.i("SetLocale : %s ", locale)
    val configuration = Configuration()
    if (!TextUtils.isEmpty(defaultLocale))
        configuration.locale = Locale(defaultLocale)
    else
        configuration.locale = Locale.getDefault();
    this.getResources().updateConfiguration(configuration, null);
}
