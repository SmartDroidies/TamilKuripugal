package droid.smart.com.tamilkuripugal

import android.Manifest
import android.annotation.TargetApi
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.google.android.gms.ads.MobileAds
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.messaging.FirebaseMessaging
import com.smart.droid.tamil.tips.BuildConfig
import com.smart.droid.tamil.tips.R
import com.smart.droid.tamil.tips.databinding.MainActivityBinding
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import droid.smart.com.tamilkuripugal.extensions.checkSelfPermissionCompat
import droid.smart.com.tamilkuripugal.extensions.requestPermissionsCompat
import droid.smart.com.tamilkuripugal.extensions.shouldShowRequestPermissionRationaleCompat
import droid.smart.com.tamilkuripugal.extensions.showSnackbar
import droid.smart.com.tamilkuripugal.ui.main.MainFragmentDirections
import droid.smart.com.tamilkuripugal.ui.main.MainViewModel
import timber.log.Timber
import javax.inject.Inject

const val PERMISSION_EXTERNAL_WRITE = 0
const val PREFKEY_UPDATE_VERSION = "pref_update_version"

/**
 * FIXME - Based on existing app capability
 *  Timber Tree for Crashanalitics
 */
class MainActivity : AppCompatActivity(), HasSupportFragmentInjector {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private lateinit var layout: View

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: MainActivityBinding = DataBindingUtil.setContentView(this, R.layout.main_activity)

        drawerLayout = binding.drawerLayout

        navController = Navigation.findNavController(this, R.id.kuripugal_nav_fragment)
        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)

        layout = binding.mainLayout

        // Set up ActionBar
        setSupportActionBar(binding.toolbar)
        setupActionBarWithNavController(navController, appBarConfiguration)

        // Set up navigation menu
        binding.navigationView.setupWithNavController(navController)

        //Init pref on first start
        initOnFirstStart()

        if (intent.extras != null && !intent.extras.isEmpty) {
            if (intent.extras.containsKey("id")) {
                Timber.d("Extras : %s ", intent.extras.get("id"))
                val kurippuId = intent.extras.get("id") as String
                val bundle = Bundle().also { it.putString("kurippuId", kurippuId) }
                navController.navigate(R.id.kurippu_fragment, bundle)
            }
        }

        // Kuripugal Ad initialization
        MobileAds.initialize(this, "ca-app-pub-8439744074965483~7727700457")
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun supportFragmentInjector() = dispatchingAndroidInjector

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.action_exit -> {
                finish()
                return true
            }
            R.id.action_share -> {
                Timber.d("Android Version : %s", Build.VERSION.SDK_INT)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermissionCompat(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        layout.showSnackbar(R.string.write_storage_permission_available, Snackbar.LENGTH_SHORT)
                        shareApp(true)
                    } else {
                        requestExternalWritePermission()
                    }
                } else {
                    shareApp(true)
                }
                return true
            }
            R.id.action_rateme -> {
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
                return true
            }
            R.id.action_feedback -> {
                val intent = Intent(Intent.ACTION_SENDTO)
                intent.data = Uri.parse("mailto:careerwrap@gmail.com") // only email apps should handle this
                intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback on Tamil Kuripugal - " + BuildConfig.VERSION_NAME)
                if (intent.resolveActivity(this.packageManager) != null) {
                    startActivity(intent)
                }
                return true
            }
            R.id.action_settings -> {
                navController.navigate(MainFragmentDirections.settings())
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_EXTERNAL_WRITE) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                shareApp(true)
            } else {
                shareApp(false)
            }
        }
    }


    /**
     * Requests the [android.Manifest.permission.WRITE_EXTERNAL_STORAGE] permission.
     * If an additional rationale should be displayed, the user has to launch the request from
     * a SnackBar that includes additional information.
     */
    @TargetApi(Build.VERSION_CODES.M)
    private fun requestExternalWritePermission() {
        // Permission has not been granted and must be requested.
        if (shouldShowRequestPermissionRationaleCompat(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            layout.showSnackbar(
                R.string.storage_permission_required,
                Snackbar.LENGTH_INDEFINITE, R.string.ok
            ) {
                requestPermissionsCompat(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    PERMISSION_EXTERNAL_WRITE
                )
            }
        } else {
            layout.showSnackbar(R.string.storage_permission_not_available, Snackbar.LENGTH_SHORT)
            requestPermissionsCompat(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_EXTERNAL_WRITE)
        }
    }

    private fun collectShareImage(): Uri? {
        var imageUri: Uri? = null
        try {
            imageUri = Uri.parse(
                MediaStore.Images.Media.insertImage(
                    this.contentResolver,
                    BitmapFactory.decodeResource(resources, R.drawable.banner), null, null
                )
            )
        } catch (e: NullPointerException) {
            Timber.e("Failed to store share app image")
        }
        return imageUri
    }

    private fun shareApp(indBanner: Boolean) {
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.type = "text/plain"
        shareIntent.putExtra(
            Intent.EXTRA_TEXT,
            "Try this great Tamil App - https://play.google.com/store/apps/details?id=" + this.packageName
        )
        if (indBanner) {
            val imageUri = collectShareImage()
            shareIntent.type = "image/*"
            if (imageUri != null) {
                shareIntent.putExtra(
                    Intent.EXTRA_STREAM,
                    imageUri
                )
            }
        }
        startActivity(Intent.createChooser(shareIntent, getString(R.string.action_share)))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.overflow_menu, menu)
        return true
    }


    private fun initOnFirstStart() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        if (!sharedPreferences.contains(PREFKEY_UPDATE_VERSION)) {
            val categories = MainViewModel.CATEGORY_DATA
            val editor = sharedPreferences.edit()
            for (category in categories) {
                Timber.d("Initialize preference for category : %s", category)
                FirebaseMessaging.getInstance().subscribeToTopic(category.topic)
                    .addOnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            Timber.w("InitOnFirstStart - Subscription failed for topic : %s", category.topic)
                            Timber.e(task.exception)
                        } else {
                            editor.putBoolean(category.topic, true)
                        }
                    }
            }
            editor.putInt(PREFKEY_UPDATE_VERSION, BuildConfig.VERSION_CODE)
            editor.apply()
        }
    }

}


