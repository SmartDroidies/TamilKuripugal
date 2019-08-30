package droid.smart.com.tamilkuripugal

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import droid.smart.com.tamilkuripugal.ui.AppExitDialogFragment
import timber.log.Timber

const val PERMISSION_EXTERNAL_WRITE = 0
const val PERMISSION_EXTERNAL_WRITE_KURIPPU = 5
const val PREFKEY_UPDATE_VERSION = "pref_update_version"

open class BaseActivity : AppCompatActivity(), AppExitDialogFragment.AppExitDialogListener {

    override fun onExitConfirm(dialog: DialogFragment) {
        FirebaseAuth.getInstance().signOut()
        super.onBackPressed()
    }

    override fun onExitCancel(dialog: DialogFragment) {
        Timber.i("User cancelled window")
    }

    override fun onExitRateme(dialog: DialogFragment) {
        rateApp()
        super.onBackPressed()
    }

    private fun rateApp() {
        try {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + this.packageName)
                )
            )
        } catch (e: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + this.packageName)
                )
            )
        }
    }

}