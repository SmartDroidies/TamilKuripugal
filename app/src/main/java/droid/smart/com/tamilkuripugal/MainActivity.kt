package droid.smart.com.tamilkuripugal

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
import com.smart.droid.tamil.tips.R
import com.smart.droid.tamil.tips.databinding.MainActivityBinding
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import timber.log.Timber
import javax.inject.Inject

/**
 * FIXME - Based on existing app capability
 *  Share Menu Link
 *  Rate Me Menu Link
 *  Feedback Menu Link
 *  Settings Menu Link
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
        Timber.i("Menu item selected : %s", item!!.itemId);
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.overflow_menu, menu)
        return true
    }

}
