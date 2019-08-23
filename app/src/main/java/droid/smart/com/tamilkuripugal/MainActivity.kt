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
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.mopub.common.MoPub
import com.mopub.common.SdkConfiguration
import com.smart.droid.tamil.tips.BuildConfig
import com.smart.droid.tamil.tips.R
import com.smart.droid.tamil.tips.databinding.MainActivityBinding
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import droid.smart.com.tamilkuripugal.extensions.*
import droid.smart.com.tamilkuripugal.ui.AppExitDialogFragment
import droid.smart.com.tamilkuripugal.ui.main.MainFragmentDirections
import droid.smart.com.tamilkuripugal.ui.main.MainViewModel
import droid.smart.com.tamilkuripugal.util.RateLimiter
import kotlinx.android.synthetic.main.main_activity.*
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject


/**
 * FIXME - Functionalities Planned
 *  Velanmai Icon Needs to be changed
 *  Icon for Notification
 *  Share Icon Background
 *  Alert on Exit using back
 *  Featured kurippu listing
 *  Favourite kurippu listing
 *  Scheduled kuripugal for test device
 *  Banner ad between recycle view
 *  Google Sign in  https://developers.google.com/identity/sign-in/android/sign-in?authuser=0
 */
class MainActivity : BaseActivity(), HasSupportFragmentInjector {

    private val RC_SIGN_IN: Int = 75
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private lateinit var layout: View

    private lateinit var interstitialAd: InterstitialAd

    @Inject
    lateinit var adRequest: AdRequest

    @Inject
    lateinit var interstitialRateLimit: RateLimiter

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    lateinit var sharedPreferences: SharedPreferences

    private lateinit var googleSignInClient: GoogleSignInClient

    @Inject
    lateinit var googleSignInOptions: GoogleSignInOptions

    private lateinit var auth: FirebaseAuth

    @Inject
    lateinit var firestore: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: MainActivityBinding = DataBindingUtil.setContentView(this, R.layout.main_activity)

        drawerLayout = binding.drawerLayout

        navController = Navigation.findNavController(this, R.id.kuripugal_nav_fragment)
        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)

        layout = binding.mainLayout

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        // Set up ActionBar
        setSupportActionBar(binding.toolbar)
        setupActionBarWithNavController(navController, appBarConfiguration)

        // Set up navigation menu
        binding.navigationView.setupWithNavController(navController)

        //Init pref on first start
        initOnFirstStart()

        // Build a GoogleSignInClient with the options specified by gso.
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)


        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        if (intent.extras != null && !intent.extras.isEmpty && intent.extras.containsKey("id")) {
            Timber.d("Extras : %s ", intent!!.extras.get("id"))
            val kurippuId = intent!!.extras.get("id") as String
            val bundle = Bundle().also { it.putString("kurippuId", kurippuId) }
            navController.navigate(R.id.kurippu_fragment, bundle)
        }

        //Authorize with google
        checkGoogleSignIn()

        // Kuripugal Ad initialization
        MobileAds.initialize(this, "ca-app-pub-8439744074965483~7727700457")

        //MoPub Ad initialization
        val sdkConfiguration = SdkConfiguration.Builder("c1ac415d0bae4c088f7ed79f72f71628").build()
        MoPub.initializeSdk(this, sdkConfiguration, null)

        //Initialize interstitial
        interstitialAd = InterstitialAd(this)
        interstitialAd.adUnitId = "ca-app-pub-8439744074965483/5473553264"
        interstitialAd.loadAd(adRequest)
        interstitialAd.adListener = object : AdListener() {
            override fun onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                //FIXME - Report it on Firebase Analytics Event
            }

            override fun onAdFailedToLoad(errorCode: Int) {
                // Code to be executed when an ad request fails.
            }

            override fun onAdOpened() {
                // Code to be executed when the ad is displayed.
            }

            override fun onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            override fun onAdClosed() {
                interstitialAd.loadAd(adRequest)
                // Code to be executed when when the interstitial ad is closed.
            }
        }

    }


    override fun onSupportNavigateUp(): Boolean {
        showInterstitial(false)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
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
    }


/*
    fun signIn() {
        val signInIntent = googleSignInClient.getSignInIntent()
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
*/

    /* override fun onStart() {
         super.onStart()
         // Check if user is signed in (non-null) and update UI accordingly.

     }*/

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


    private fun initOnFirstStart() {
        if (!sharedPreferences.contains(PREFKEY_UPDATE_VERSION)) {
            //FIXME - Collect this from categoryRepository
            val categories = MainViewModel.CATEGORY_DATA
            for (category in categories) {
                Timber.d("Initialize preference for category : %s", category)
                FirebaseMessaging.getInstance().subscribeToTopic(category.topic)
                    .addOnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            Timber.w("InitOnFirstStart - Subscription failed for topic : %s", category.topic)
                            Timber.e(task.exception)
                        } else {
                            Timber.d("InitOnFirstStart - Subscription success for topic : %s", category.topic)
                            sharedPreferences.edit().putBoolean(category.topic, true).apply()
                        }
                    }
            }
            sharedPreferences.edit().putInt(PREFKEY_UPDATE_VERSION, BuildConfig.VERSION_CODE).apply()
        }
    }

    fun checkGoogleSignIn() {
        if (!sharedPreferences.contains(PREFKEY_GSIGN_CHOICE)) {
            val googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this)
            if (googleSignInAccount != null) {
                Timber.i("Google sign in : %s", googleSignInAccount.displayName)
                checkFirebaseAuth(googleSignInAccount)
            } else {
                navController.navigate(MainFragmentDirections.signin())
            }
        }
    }

    private fun checkFirebaseAuth(googleSignInAccount: GoogleSignInAccount) {
        if (auth.currentUser == null) {
            firebaseAuthWithGoogle(googleSignInAccount)
        } else {
            Timber.i("FirebaseAuth Provider : %s", auth.currentUser!!.providerId)
            Timber.i("Firebase User : %s", auth.currentUser!!.uid)
            //linkAuthWithGoogle(googleSignInAccount)
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
            if (interstitialRateLimit.shouldFetch("interstitial_ad", 60, TimeUnit.SECONDS) || bForce) {
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                Timber.e(e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        Timber.d("firebaseAuthWithGoogle : %s", account.id)
        //showProgressDialog()  //FIXME https://github.com/firebase/quickstart-android/blob/4967bbf6fd51d65b3b2085e32d41b05112e57b52/auth/app/src/main/java/com/google/firebase/quickstart/auth/kotlin/GoogleSignInActivity.kt#L71-L89
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Timber.d("signInWithCredential:success")
                    val user = auth.currentUser
                    user?.let { checkCreateUserModule(it) }
                } else {
                    // If sign in fails, display a message to the user.
                    Timber.e(task.exception)
                    Snackbar.make(main_layout, "Authentication Failed.", Snackbar.LENGTH_SHORT).show()
                }

                // [START_EXCLUDE]
                //hideProgressDialog()
                // [END_EXCLUDE]
            }
    }

    private fun checkCreateUserModule(firebaseUser: FirebaseUser) {

        val user = hashMapOf(
            "name" to firebaseUser.displayName,
            "mail" to firebaseUser.email
        )

        firestore.collection("users")
            .document(firebaseUser.uid)
            .set(user)
            .addOnSuccessListener { documentReference ->
                Timber.d("User document succesfully updated for %s", firebaseUser.uid)
            }
            .addOnFailureListener { e ->
                Timber.w(e, "Error adding user document for %s", firebaseUser.uid)
            }

    }

    private fun linkAuthWithGoogle(account: GoogleSignInAccount) {
        auth.currentUser?.linkWithCredential(GoogleAuthProvider.getCredential(account.idToken, null))
            ?.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Timber.d("linkWithCredential:success")
                    val user = task.result?.user
                } else {
                    Timber.e(task.exception)
                }
            }

    }


}


