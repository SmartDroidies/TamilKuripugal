package droid.smart.com.tamilkuripugal.ui.settings


import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import droid.smart.com.tamilkuripugal.ui.main.MainViewModel
import droid.smart.com.tamilkuripugal.ui.util.Helper
import timber.log.Timber


class SettingsFragment : PreferenceFragmentCompat() {

    val categories = MainViewModel.CATEGORY_DATA


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
            notificationPreference.key = category.code
            notificationPreference.title = Helper.localeText(this.context!!, category.code)
            notificationCategory.addPreference(notificationPreference)
        }

        var prefListener: OnSharedPreferenceChangeListener =
            OnSharedPreferenceChangeListener { prefs, key ->
                Timber.d("Pref Changed : %s - %s", key, prefs.getBoolean(key, false))
            }
        screen.sharedPreferences.registerOnSharedPreferenceChangeListener(prefListener)

        preferenceScreen = screen
    }

}
