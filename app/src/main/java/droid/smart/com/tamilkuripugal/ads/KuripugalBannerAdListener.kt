package droid.smart.com.tamilkuripugal.ads

import com.google.android.gms.ads.AdListener
import timber.log.Timber

class KuripugalBannerAdListener : AdListener() {

    override fun onAdLoaded() {
        Timber.i("Admob - Ad Loaded")
    }

    override fun onAdFailedToLoad(errorCode: Int) {
        Timber.i("Admob - Ad failed to load - %s", errorCode)
    }

    override fun onAdOpened() {
        Timber.i("Admob - Ad Opoened")
    }

    override fun onAdLeftApplication() {
        // Code to be executed when the user has left the app.
    }

    override fun onAdClosed() {
        Timber.i("Admob - Ad Closed")
    }

}
