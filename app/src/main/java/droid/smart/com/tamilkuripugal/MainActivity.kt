package droid.smart.com.tamilkuripugal

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
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
import com.google.android.gms.ads.MobileAds
import com.smart.droid.tamil.tips.BuildConfig
import com.smart.droid.tamil.tips.R
import com.smart.droid.tamil.tips.databinding.MainActivityBinding
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import timber.log.Timber
import javax.inject.Inject


/**
 * FIXME - Based on existing app capability
 *  Share Menu Link  - Share an image
 *  Settings Menu Link
 *  Add Agriculture Tips
 */
class MainActivity : AppCompatActivity(), HasSupportFragmentInjector {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: MainActivityBinding = DataBindingUtil.setContentView(this, R.layout.main_activity)

        drawerLayout = binding.drawerLayout

        navController = Navigation.findNavController(this, R.id.kuripugal_nav_fragment)
        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)

        // Set up ActionBar
        setSupportActionBar(binding.toolbar)
        setupActionBarWithNavController(navController, appBarConfiguration)

        // Set up navigation menu
        binding.navigationView.setupWithNavController(navController)

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
                finish();
                return true
            }
            R.id.action_share -> {
                //FIXME - Check for permission before this share
/*
                var imageUri: Uri? = null
                try {
                    imageUri = Uri.parse(
                        MediaStore.Images.Media.insertImage(
                            this.contentResolver,
                            BitmapFactory.decodeResource(resources, R.drawable.banner), null, null
                        )
                    )
                } catch (e: NullPointerException) {
                    Timber.e("Failed to load share app image")
                }
*/
                val shareIntent = Intent()
                shareIntent.action = Intent.ACTION_SEND
                shareIntent.type = "text/plain"
//                shareIntent.type = "image/*"
//                shareIntent.putExtra(
//                    Intent.EXTRA_STREAM,
//                    imageUri
//                )
                shareIntent.putExtra(
                    Intent.EXTRA_TEXT,
                    "Try this great Tamil App - https://play.google.com/store/apps/details?id=" + this.packageName
                );
                startActivity(Intent.createChooser(shareIntent, getString(R.string.action_share)))
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
                } catch (e: android.content.ActivityNotFoundException) {
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
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.overflow_menu, menu)
        return true
    }

}
