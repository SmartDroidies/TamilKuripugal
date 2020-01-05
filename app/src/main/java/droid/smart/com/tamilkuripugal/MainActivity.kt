package droid.smart.com.tamilkuripugal

import android.Manifest
import android.annotation.TargetApi
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.messaging.FirebaseMessaging
import com.smart.droid.tamil.tips.BuildConfig
import com.smart.droid.tamil.tips.R
import com.smart.droid.tamil.tips.databinding.MainActivityBinding
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import droid.smart.com.tamilkuripugal.ads.AdConstant
import droid.smart.com.tamilkuripugal.extensions.*
import droid.smart.com.tamilkuripugal.repo.CategoryRepository
import droid.smart.com.tamilkuripugal.ui.AppExitDialogFragment
import droid.smart.com.tamilkuripugal.ui.main.MainFragmentDirections
import droid.smart.com.tamilkuripugal.util.RateLimiter
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MainActivity : BaseActivity(), HasAndroidInjector {

    private val RC_SIGN_IN: Int = 75
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private lateinit var layout: View

    private lateinit var interstitialAd: InterstitialAd

    @Inject
    lateinit var googleSignInOptions: GoogleSignInOptions

    @Inject
    lateinit var adRequest: AdRequest

    @Inject
    lateinit var interstitialRateLimit: RateLimiter

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: MainActivityBinding =
            DataBindingUtil.setContentView(this, R.layout.main_activity)

        navController = Navigation.findNavController(this, R.id.kuripugal_nav_fragment)
        appBarConfiguration = AppBarConfiguration(navController.graph)

        layout = binding.container

        // Set up ActionBar
        setSupportActionBar(binding.toolbar)
        setupActionBarWithNavController(navController, appBarConfiguration)

        // Set up navigation menu
        binding.navigation.setupWithNavController(navController)

        //Init pref on first start
        initOnFirstStart()

        //FIXME - Drive this from Main Fragment
        if (intent.extras != null && !intent.extras.isEmpty && intent.extras.containsKey("id")) {
            Timber.d("Extras : %s ", intent!!.extras.get("id"))
            val kurippuId = intent!!.extras.get("id") as String
            val bundle = Bundle().also { it.putString("kurippuId", kurippuId) }
            navController.navigate(R.id.kurippu_fragment, bundle)
        }

        showHideBottomNavigation(binding.navigation)

        initializeAd()

    }

    private fun initializeAd() {
        // Kuripugal Ad initialization
        MobileAds.initialize(this, AdConstant.ADMOB_APP_ID)

        interstitialRateLimit.shouldFetch("interstitial_ad", 60, TimeUnit.SECONDS)
        interstitialAd = InterstitialAd(this).apply {
            adUnitId = AdConstant.adUnitInterstitial
            adListener = (object : AdListener() {
                override fun onAdLoaded() {
                    Timber.i("Admob Interstitial - Ad Loaded")
                }

                override fun onAdFailedToLoad(errorCode: Int) {
                    Timber.i("Admob Interstitial - Ad failed to load - %s", errorCode)
                }

                override fun onAdClosed() {
                    interstitialAd.loadAd(adRequest)
                }
            })
        }
    }

    private fun showHideBottomNavigation(navigation: BottomNavigationView) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            Timber.i("Destination : %s", destination.id)
            when (destination.id) {
                R.id.main_fragment -> showBottomNavigation(navigation)
                R.id.newKuripugalFragment -> showBottomNavigation(navigation)
                R.id.favouriteKuripugalFragment -> showBottomNavigation(navigation)
                R.id.profile -> showBottomNavigation(navigation)
                else -> hideBottomNavigation(navigation)
            }
        }

    }


    override fun onSupportNavigateUp(): Boolean {
        showInterstitial(false)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        Timber.i(
            "Nav Controller : %s vs %s",
            navController.currentDestination!!.id,
            navController.graph.startDestination
        )
        if (navController.graph.startDestination == navController.currentDestination!!.id) {
            AppExitDialogFragment().show(supportFragmentManager, "ExitDialogFragment")
        } else {
            super.onBackPressed()
        }
    }

    override fun androidInjector() = dispatchingAndroidInjector

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
                        layout.showSnackbar(
                            R.string.write_storage_permission_available,
                            Snackbar.LENGTH_SHORT
                        )
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
                intent.data =
                    Uri.parse("mailto:careerwrap@gmail.com") // only email apps should handle this
                intent.putExtra(
                    Intent.EXTRA_SUBJECT,
                    "Feedback on Tamil Kuripugal - " + BuildConfig.VERSION_NAME
                )
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
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
    fun requestExternalWritePermission() {
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
            requestPermissionsCompat(
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PERMISSION_EXTERNAL_WRITE
            )
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


    private fun initOnFirstStart() {
        if (!sharedPreferences.contains(PREFKEY_UPDATE_VERSION)) {
            val categories = CategoryRepository.CATEGORY_DATA
            for (category in categories) {
                Timber.d("Initialize preference for category : %s", category)
                FirebaseMessaging.getInstance().subscribeToTopic(category.topic)
                    .addOnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            Timber.w(
                                "InitOnFirstStart - Subscription failed for topic : %s",
                                category.topic
                            )
                            Timber.e(task.exception)
                        } else {
                            Timber.d(
                                "InitOnFirstStart - Subscription success for topic : %s",
                                category.topic
                            )
                            sharedPreferences.edit().putBoolean(category.topic, true).apply()
                        }
                    }
            }
            sharedPreferences.edit().putInt(PREFKEY_UPDATE_VERSION, BuildConfig.VERSION_CODE)
                .apply()
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.GROUP_ID, "notify_subscribe")
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.JOIN_GROUP, bundle)
        }
    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        Timber.i("Configuration changed initiate locale change")
        this.setDefaultLocale()
    }

    /**
     * Displays ad after every 90 Seconds
     */
    fun showInterstitial(bForce: Boolean) {
        if (interstitialAd.isLoaded) {
            if (interstitialRateLimit.shouldFetch(
                    "interstitial_ad",
                    60,
                    TimeUnit.SECONDS
                ) || bForce
            ) {
                interstitialAd.show()
            } else {
                Timber.i(
                    "showInterstitial : Time interval not met - %s",
                    interstitialRateLimit.elapsed("interstitial_ad")
                )
            }
        } else {
            Timber.i("showInterstitial : Ad not loaded yet - %s", interstitialAd.isLoading)
        }

    }

    fun setActionBarTitle(title: String) {
        supportActionBar!!.title = title
    }

    /*
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                Timber.i("User succesfully logged into to google - MainActivity")
                // Google Sign In was successful, authenticate with Firebase
                //val account = task.getResult(ApiException::class.java)
                //firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                Timber.e(e)
            }
        }
    }
    */

    private fun hideBottomNavigation(navigation: BottomNavigationView) {
        // bottom_navigation is BottomNavigationView
        with(navigation) {
            if (visibility == View.VISIBLE && alpha == 1f) {
                animate()
                    .alpha(0f)
                    .withEndAction { visibility = View.GONE }
                    .duration = 5
            }
        }
    }

    private fun showBottomNavigation(navigation: BottomNavigationView) {
        // bottom_navigation is BottomNavigationView
        with(navigation) {
            visibility = View.VISIBLE
            animate().alpha(1f).duration = 5
        }
    }


}


