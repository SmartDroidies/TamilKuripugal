package droid.smart.com.tamilkuripugal.ui.settings


import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.google.firebase.messaging.FirebaseMessaging
import droid.smart.com.tamilkuripugal.repo.CategoryRepository
import droid.smart.com.tamilkuripugal.ui.util.Helper
import timber.log.Timber


class SettingsFragment : PreferenceFragmentCompat() {

    val categories = CategoryRepository.CATEGORY_DATA

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {

        val context = preferenceManager.context
        val screen = preferenceManager.createPreferenceScreen(context)

        val notificationCategory = PreferenceCategory(context)
        notificationCategory.key = "notifications_category"
        notificationCategory.title = "Notifications"
        screen.addPreference(notificationCategory)

        for (category in categories) {
            Timber.d("Category : %s", category)
            val notificationPreference = SwitchPreferenceCompat(context)
            notificationPreference.key = category.topic
            notificationPreference.title = Helper.localeText(this.context!!, category.code)
            notificationCategory.addPreference(notificationPreference)
        }

        var prefListener = OnSharedPreferenceChangeListener { prefs, key ->
            Timber.d("Pref Changed : %s - %s", key, prefs.getBoolean(key, false))
            if (prefs.getBoolean(key, true)) {
                FirebaseMessaging.getInstance().subscribeToTopic(key)
                    .addOnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            Timber.w("Subscription failed for topic : %s", key)
                        } else {
                            Timber.i("Subscription successful for topic : %s", key)
                        }
                    }
            } else {
                FirebaseMessaging.getInstance().unsubscribeFromTopic(key)
                    .addOnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            Timber.w("UnSubscription failed for topic : %s", key)
                        } else {
                            Timber.i("UnSubscription successful for topic : %s", key)
                        }
                    }
            }
        }
        screen.sharedPreferences.registerOnSharedPreferenceChangeListener(prefListener)

        preferenceScreen = screen
    }

}
