package droid.smart.com.tamilkuripugal.extensions

import android.widget.FrameLayout
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import droid.smart.com.tamilkuripugal.ads.AdConstant
import droid.smart.com.tamilkuripugal.ads.KuripugalBannerAdListener

fun FrameLayout.loadAd() {

    this.removeAllViews()

    val adRequest = AdRequest.Builder()
        .addTestDevice(AdConstant.testDeviceId)
        .build()

    val adView = AdView(this.context!!)
    adView.adSize = AdSize.BANNER
    adView.adUnitId = AdConstant.adUnitBanner
    adView.adListener = KuripugalBannerAdListener()
    adView.loadAd(adRequest)

    this.addView(adView)


}
