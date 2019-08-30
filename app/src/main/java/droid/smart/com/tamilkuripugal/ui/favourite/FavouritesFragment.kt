package droid.smart.com.tamilkuripugal.ui.favourite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.smart.droid.tamil.tips.R
import com.smart.droid.tamil.tips.databinding.FavouritesFragmentBinding
import droid.smart.com.tamilkuripugal.AppExecutors
import droid.smart.com.tamilkuripugal.binding.FragmentDataBindingComponent
import droid.smart.com.tamilkuripugal.di.Injectable
import droid.smart.com.tamilkuripugal.ui.common.DividerItemDecoration
import droid.smart.com.tamilkuripugal.ui.kuripugal.KuripugalFragmentDirections
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

    @Inject
    lateinit var auth: FirebaseAuth

    private var adapter by autoCleared<FavouritesAdapter>()

    @Inject
    lateinit var firebaseAnalytics: FirebaseAnalytics

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

        val firebaseUserId = auth.currentUser?.uid
        Timber.i("Display favourite kuripugal for : %s ", firebaseUserId)
        firebaseUserId?.let { favouritesViewModel.setUserId(it) }
        binding.lifecycleOwner = viewLifecycleOwner
        binding.kurippugal = favouritesViewModel.kuripugal

        val adapter = FavouritesAdapter(
            dataBindingComponent,
            appExecutors
        ) { kurippu ->
            navController().navigate(
                KuripugalFragmentDirections.kurippu(kurippu.kurippuId)
            )
        }
        this.adapter = adapter
        binding.kuripugalList.adapter = adapter
        val mLayoutManager = LinearLayoutManager(context)
        binding.kuripugalList.layoutManager = mLayoutManager
        val itemDecor = DividerItemDecoration(context!!)
        binding.kuripugalList.addItemDecoration(itemDecor)


        initKuripugalList(favouritesViewModel)

        firebaseAnalytics.setCurrentScreen(activity!!, this.javaClass.simpleName, this.javaClass.simpleName)

    }

    private fun initKuripugalList(favouritesViewModel: FavouritesViewModel) {
        favouritesViewModel.kuripugal.observe(viewLifecycleOwner, Observer { listResource ->
            //Timber.i("Favourite list resource : %s", listResource)
            if (listResource?.data != null) {
                adapter.submitList(listResource.data)
            } else {
                adapter.submitList(emptyList())
            }
        })
    }


/*
    override fun onStart() {
        super.onStart()
        //val account = GoogleSignIn.getLastSignedInAccount(this.context)
        //updateUI(account)

//        val currentUser = firebaseAuth.currentUser
//        updateUI(currentUser)
    }
*/


    private fun updateUI(account: FirebaseUser?) {
        if (account != null) {
            Timber.d("Favourites Screen - User Identified : %s, %s ", account.displayName, account.email)
            binding.signinContent.visibility = View.GONE
            binding.kuripugalList.visibility = View.VISIBLE

            //FIXME - Display favourites for user

        } else {
            Timber.d("Favourites Screen - Display User Signin Button")
            binding.signinContent.visibility = View.VISIBLE
            binding.kuripugalList.visibility = View.GONE
        }
    }

/*    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }*/

    /*private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account!!)
            // Signed in successfully, show authenticated UI.
            //updateUI(account)
        } catch (e: ApiException) {
            Timber.w("signInResult:failed code - %s , %s ", e.statusCode, e.statusMessage)
            updateUI(null)
        }
    }*/

    /*private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Timber.d("firebaseAuthWithGoogle: %s", acct.id!!)

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Timber.d("signInWithCredential:success")
                    val user = firebaseAuth.currentUser
                    updateUI(user)
                } else {
                    Timber.w("signInWithCredential:failure - %s", it.exception)
                    Snackbar.make(main_layout, "Authentication Failed.", Snackbar.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }

    }*/


    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()

}