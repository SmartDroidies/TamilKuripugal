package droid.smart.com.tamilkuripugal.extensions

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

fun FirebaseAnalytics.kurippuView(kurippuId: String) {
    val params = Bundle()
    params.putString("kurippu_id", kurippuId)
    this.logEvent("kurippu_view", params)
}