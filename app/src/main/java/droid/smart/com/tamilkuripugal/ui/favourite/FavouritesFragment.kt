package droid.smart.com.tamilkuripugal.ui.favourite

import android.content.Intent
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
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
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

    lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 9001

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

        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this.context!!, googleSignInOptions)

        //val params = KurippuFragmentArgs.fromBundle(arguments!!)
        //Timber.i("Display Kurippu details for : %s ", params.kurippuId)
        //kurippuViewModel.setKurippuId(params.kurippuId)
        binding.setLifecycleOwner(viewLifecycleOwner)


        //val currentUser = auth.currentUser
        // if (currentUser != null) Timber.i("User logged in : %s", currentUser.email)

        binding.signInButton.setOnClickListener {
            val signInIntent = googleSignInClient.getSignInIntent()
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

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

            //FIXME - Display favourites for user

        } else {
            Timber.d("Favourites Screen - Display User Signin Button");
            binding.signinContent.visibility = View.VISIBLE
            binding.kuripugalList.visibility = View.GONE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            // Signed in successfully, show authenticated UI.
            updateUI(account)
        } catch (e: ApiException) {
            Timber.w("signInResult:failed code - %s ", e.statusCode)
            updateUI(null)
        }
    }


}