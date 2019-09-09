package droid.smart.com.tamilkuripugal.ui.main

import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.smart.droid.tamil.tips.BuildConfig
import com.smart.droid.tamil.tips.R
import com.smart.droid.tamil.tips.databinding.MainFragmentBinding
import droid.smart.com.tamilkuripugal.AppExecutors
import droid.smart.com.tamilkuripugal.binding.FragmentDataBindingComponent
import droid.smart.com.tamilkuripugal.di.Injectable
import droid.smart.com.tamilkuripugal.ui.common.CategoryListAdapter
import droid.smart.com.tamilkuripugal.ui.common.RetryCallback
import droid.smart.com.tamilkuripugal.util.*
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class MainFragment : Fragment(), Injectable {

    val paramUser = "user"

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

    lateinit var mAdView: AdView

    private lateinit var menuScheduled: MenuItem

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
            navController().navigate(
                MainFragmentDirections.draftKuripugal()
            )
        }

        //Authorize with google
        processSignIn()


        binding = dataBinding
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mainViewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(MainViewModel::class.java)
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
        menuScheduled  = menu.findItem(R.id.action_scheduled)
        if (BuildConfig.DEBUG) {
            menuScheduled.isVisible = true
            Timber.i("Test Device - Display test device controls")
        }
    }

    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()

    fun processSignIn() {
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
            Timber.i("Google sign in : %s", googleSignInAccount.displayName)
            checkFirebaseAuth(googleSignInAccount)
        } else {
            navController().navigate(R.id.signin)
        }

    }

    private fun checkSkipTSExpiry() {
        val signInSkipTS = sharedPreferences.getLong(PREFKEY_GSIGN_SKIPTS, 0L)
        val currentMS = System.currentTimeMillis()
        val milliSecSinceSkipped = currentMS - signInSkipTS
        Timber.i("Sign in skipped since %s seconds", TimeUnit.MILLISECONDS.toSeconds(milliSecSinceSkipped))
        /* FIXME - Change this to 7 days */
        if (TimeUnit.MILLISECONDS.toSeconds(milliSecSinceSkipped) > 60) {
            navController().navigate(R.id.signin)
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

    //TODO - Duplicate code in SigninFragment
    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        Timber.d("firebaseAuthWithGoogle : %s", account.id)
        //showProgressDialog()  //FIXME https://github.com/firebase/quickstart-android/blob/4967bbf6fd51d65b3b2085e32d41b05112e57b52/auth/app/src/main/java/com/google/firebase/quickstart/auth/kotlin/GoogleSignInActivity.kt#L71-L89
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Timber.d("signInWithCredential:success")
                    val user = auth.currentUser
                    user?.let { checkCreateUserModule(it) }
                } else {
                    // If sign in fails, display a message to the user.
                    Timber.e(task.exception)
                    //Snackbar.make(main_layout, "Authentication Failed.", Snackbar.LENGTH_SHORT).show()
                }

                // [START_EXCLUDE]
                //hideProgressDialog()
                // [END_EXCLUDE]
            }
    }

    //TODO - Duplicate code in SigninFragment
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


}




