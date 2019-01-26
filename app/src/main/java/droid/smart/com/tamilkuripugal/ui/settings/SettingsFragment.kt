package droid.smart.com.tamilkuripugal.ui.settings


import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.smart.droid.tamil.tips.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }
}
