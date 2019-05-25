package droid.smart.com.tamilkuripugal.ui.favourite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.smart.droid.tamil.tips.R
import com.smart.droid.tamil.tips.databinding.FavouritesFragmentBinding
import droid.smart.com.tamilkuripugal.AppExecutors
import droid.smart.com.tamilkuripugal.binding.FragmentDataBindingComponent
import droid.smart.com.tamilkuripugal.di.Injectable
import droid.smart.com.tamilkuripugal.util.autoCleared
import timber.log.Timber
import javax.inject.Inject

class FavouritesFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var favouritesViewModel: FavouritesViewModel

    @Inject
    lateinit var appExecutors: AppExecutors

    // mutable for testing
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    var binding by autoCleared<FavouritesFragmentBinding>()

    //private var adapter by autoCleared<NewKuripugalAdapter>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<FavouritesFragmentBinding>(
            inflater,
            R.layout.favourites_fragment,
            container,
            false
        )

        binding = dataBinding
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        favouritesViewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(FavouritesViewModel::class.java)
        //val params = KurippuFragmentArgs.fromBundle(arguments!!)
        //Timber.i("Display Kurippu details for : %s ", params.kurippuId)
        //kurippuViewModel.setKurippuId(params.kurippuId)
        binding.setLifecycleOwner(viewLifecycleOwner)



        //val currentUser = auth.currentUser
        // if (currentUser != null) Timber.i("User logged in : %s", currentUser.email)


/*
        binding.fabShare.setOnClickListener {
            Timber.d("Android Version : %s", Build.VERSION.SDK_INT)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermissionCompat(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    shareKurippu(true)
                } else {
                    requestPermissions(
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        PERMISSION_EXTERNAL_WRITE_KURIPPU
                    )
                }
            } else {
                shareKurippu(false)
            }
        }
*/

        //mAdView = binding.adView
        //mAdView.loadAd(adRequest)

        //firebaseAnalytics.setCurrentScreen(activity!!, this.javaClass.simpleName, this.javaClass.simpleName)
    }

    override fun onStart() {
        super.onStart()
        val account = GoogleSignIn.getLastSignedInAccount(this.context)
        updateUI(account)
    }


    private fun updateUI(account: GoogleSignInAccount?) {
        if (account != null) {
            Timber.d("Favourites Screen - User Identified : %s ", account.displayName);
            binding.signinContent.visibility = View.GONE
            binding.kuripugalList.visibility = View.VISIBLE
        } else {
            Timber.d("Favourites Screen - Display User Signin Button");
            binding.signinContent.visibility = View.VISIBLE
            binding.kuripugalList.visibility = View.GONE
        }
    }


}