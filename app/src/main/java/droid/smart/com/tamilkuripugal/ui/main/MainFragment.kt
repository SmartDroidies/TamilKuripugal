package droid.smart.com.tamilkuripugal.ui.main

import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.smart.droid.tamil.tips.BuildConfig
import com.smart.droid.tamil.tips.R
import com.smart.droid.tamil.tips.databinding.MainFragmentBinding
import droid.smart.com.tamilkuripugal.AppExecutors
import droid.smart.com.tamilkuripugal.binding.FragmentDataBindingComponent
import droid.smart.com.tamilkuripugal.di.Injectable
import droid.smart.com.tamilkuripugal.extensions.googleAuth
import droid.smart.com.tamilkuripugal.ui.common.CategoryListAdapter
import droid.smart.com.tamilkuripugal.ui.common.RetryCallback
import droid.smart.com.tamilkuripugal.util.*
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class MainFragment : Fragment(), Injectable {

    private val paramUser = "user"

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var appExecutors: AppExecutors

    var binding by autoCleared<MainFragmentBinding>()
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    private lateinit var mainViewModel: MainViewModel
    private var adapter by autoCleared<CategoryListAdapter>()

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var adRequest: AdRequest

    private lateinit var mAdView: AdView

    private lateinit var menuScheduled: MenuItem

    @Inject
    lateinit var googleSignInOptions: GoogleSignInOptions

    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        val dataBinding = DataBindingUtil.inflate<MainFragmentBinding>(
            inflater,
            R.layout.main_fragment,
            container,
            false,
            dataBindingComponent
        )
        dataBinding.retryCallback = object : RetryCallback {
            override fun retry() {
                mainViewModel.retry()
            }
        }

        dataBinding.btnDraftKuripugal.setOnClickListener {
            this.navController().navigate(
                MainFragmentDirections.draftKuripugal()
            )
        }

        //Authorize with google
        processSignIn()

        binding = dataBinding
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mainViewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
        mainViewModel.setUser(paramUser)

        binding.categories = mainViewModel.categories
        binding.lifecycleOwner = viewLifecycleOwner
        val rvAdapter = CategoryListAdapter(
            dataBindingComponent = dataBindingComponent,
            appExecutors = appExecutors
        ) { category ->
            navController().navigate(MainFragmentDirections.kuripugalList(category.category))
        }

        val layoutManager = GridLayoutManager(context, 3)
        binding.categoryList.layoutManager = layoutManager
        binding.categoryList.adapter = rvAdapter
        this.adapter = rvAdapter
        initCategories()

        mAdView = binding.adView
        mAdView.loadAd(adRequest)

    }


    private fun initCategories() {
        mainViewModel.categories.observe(viewLifecycleOwner, Observer { categories ->
            adapter.submitList(categories?.data)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater)
        menuInflater.inflate(R.menu.overflow_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_scheduled -> {
                navController().navigate(
                    MainFragmentDirections.draftKuripugal()
                )
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onPrepareOptionsMenu(menu: Menu) {
        menuScheduled = menu.findItem(R.id.action_scheduled)
        if (BuildConfig.DEBUG) {
            menuScheduled.isVisible = true
            Timber.i("Test Device - Display test device controls")
        }
    }

    /**
     * Created to be able to override in tests
     */
    private fun navController() = findNavController()

    private fun processSignIn() {
        if (!sharedPreferences.contains(PREFKEY_GSIGN_CHOICE)) {
            navController().navigate(R.id.signin)
        } else {
            val signinChoice = sharedPreferences.getString(PREFKEY_GSIGN_CHOICE, "")
            Timber.i("Signin Choice - %s", signinChoice)
            when (signinChoice) {
                PREFVAL_GSIGN_SKIP -> checkSkipTSExpiry()
                PREFVAL_GSIGN_GOOGLE -> validateGSignin()
            }
        }

    }

    private fun validateGSignin() {
        val googleSignInAccount = GoogleSignIn.getLastSignedInAccount(context)
        if (googleSignInAccount != null) {
            refreshGoogleToken()
        } else {
            navController().navigate(R.id.signin)
        }

    }

    private fun refreshGoogleToken() {
        // Build a GoogleSignInClient with the options specified by gso.
        val googleSignInClient = context?.let { GoogleSignIn.getClient(it, googleSignInOptions) }!!
        val task = googleSignInClient.silentSignIn()
        if (task.isSuccessful) {
            val googleSignInAccount = task.result!!
            Timber.d("Google sign in readily available : %s", googleSignInAccount.displayName)
            checkFirebaseAuth(googleSignInAccount)
        } else {
            task.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val googleSignInAccount = task.result!!
                    Timber.d("Google sign in completed silently : %s", googleSignInAccount.displayName)
                    checkFirebaseAuth(googleSignInAccount)
                } else {
                    Timber.w("Google silent sign in failed")
                }
            }
        }
    }

    private fun checkSkipTSExpiry() {
        val signInSkipTS = sharedPreferences.getLong(PREFKEY_GSIGN_SKIPTS, 0L)
        val currentMS = System.currentTimeMillis()
        val milliSecSinceSkipped = currentMS - signInSkipTS
        Timber.i("Sign in skipped since %s seconds", TimeUnit.MILLISECONDS.toSeconds(milliSecSinceSkipped))
        if (TimeUnit.MILLISECONDS.toDays(milliSecSinceSkipped) > 7) {
            navController().navigate(R.id.signin)
        }
    }

    private fun checkFirebaseAuth(googleSignInAccount: GoogleSignInAccount) {
        if (auth.currentUser == null) {
            auth.googleAuth(googleSignInAccount, firestore)
        } else {
            Timber.i("FirebaseAuth Provider : %s", auth.currentUser!!.providerId)
            Timber.i("Firebase User : %s", auth.currentUser!!.uid)
            //linkAuthWithGoogle(googleSignInAccount)
        }
    }

}




