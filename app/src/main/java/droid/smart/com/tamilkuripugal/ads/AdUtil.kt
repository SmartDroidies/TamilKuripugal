package com.smart.droid.thalaivargal.ads

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.smart.droid.tamil.tips.R
import timber.log.Timber

object AdUtil {

    val ADMOB_APP_ID = "ca-app-pub-5575552359884924~6805519895" //Smartdroidies
    //val ADMOB_APP_ID =  "ca-app-pub-8439744074965483~7727700457" //CareerWrap
    val TEST_DEVICE_ID = "DC14F1DAAD21C69EF0EE884173C21F66"
    val ADMOB_BANNER_ID = "ca-app-pub-5575552359884924/7339325353"
    //Smartdroidies //ca-app-pub-3940256099942544/6300978111  - Test ID
    val ADMOB_INTER_ID = "ca-app-pub-5575552359884924/2657961097"
    //Smartdroidies //ca-app-pub-3940256099942544/1033173712 - Test ID

    val interstitialAdRequest: AdRequest
        get() = AdRequest.Builder()
            .addTestDevice(TEST_DEVICE_ID)
            .build()

    /**
     * Display Banner Ad
     *
     * @param view    View that holds the AdContainer
     * @param context Content
     */
    fun displayBannerAd(view: View, context: Context) {
        val adContainer = view.findViewById<FrameLayout>(R.id.adContainer)
        val adView = AdView(context)
        adView.adSize = AdSize.SMART_BANNER
        adView.adUnitId = ADMOB_BANNER_ID

        adContainer.addView(adView)
        loadAd(adView)

        //Setting listener
        adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                Timber.i("Banner Ad Activity : Ad Loaded")
            }

            override fun onAdFailedToLoad(errorCode: Int) {
                Timber.e("Banner Ad Activity : Ad Failed to Load - %s", errorCode)
            }

            override fun onAdOpened() {
                Timber.i("Banner Ad Activity : Ad Opened")
            }

            override fun onAdLeftApplication() {
                Timber.i("Banner Ad Activity : Ad Left Application")
            }

            override fun onAdClosed() {
                Timber.i("Banner Ad Activity : Ad Closed")
            }
        }
    }

    /**
     * Load Admob Banner Ad
     *
     * @param adView Adview to load the ad
     */
    private fun loadAd(adView: AdView) {
        val adRequest = AdRequest.Builder()
            .addTestDevice(TEST_DEVICE_ID)
            .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
            .build()
        adView.loadAd(adRequest)
    }

}
