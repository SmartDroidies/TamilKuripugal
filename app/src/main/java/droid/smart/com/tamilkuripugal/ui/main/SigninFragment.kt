package droid.smart.com.tamilkuripugal.ui.main

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import droid.smart.com.tamilkuripugal.di.Injectable
import droid.smart.com.tamilkuripugal.extensions.googleAuth
import droid.smart.com.tamilkuripugal.util.PREFKEY_GSIGN_CHOICE
import droid.smart.com.tamilkuripugal.util.PREFKEY_GSIGN_SKIPTS
import droid.smart.com.tamilkuripugal.util.PREFVAL_GSIGN_GOOGLE
import droid.smart.com.tamilkuripugal.util.PREFVAL_GSIGN_SKIP
import kotlinx.android.synthetic.main.signin_fragment.view.*
import timber.log.Timber
import javax.inject.Inject

class SigninFragment : Fragment(), Injectable {

    private lateinit var googleSignInClient: GoogleSignInClient

    @Inject
    lateinit var googleSignInOptions: GoogleSignInOptions

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var firebaseAnalytics: FirebaseAnalytics

    @Inject
    lateinit var firestore: FirebaseFirestore

    @Inject
    lateinit var auth: FirebaseAuth

    private val RC_SIGN_IN: Int = 75

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(com.smart.droid.tamil.tips.R.layout.signin_fragment, container, false)

        // Build a GoogleSignInClient with the options specified by gso.
        googleSignInClient = context?.let { GoogleSignIn.getClient(it, googleSignInOptions) }!!

        val signinButton = rootView.sign_in_button
        signinButton.setOnClickListener(View.OnClickListener {
            signIn()
        })

        val signInSkipButton = rootView.signin_skip_button
        signInSkipButton.setOnClickListener {
            sharedPreferences.edit()
                .putString(PREFKEY_GSIGN_CHOICE, PREFVAL_GSIGN_SKIP)
                .putLong(PREFKEY_GSIGN_SKIPTS, System.currentTimeMillis())
                .apply()
            Timber.i("Google Signin Skipped")
            firebaseAnalytics.logEvent("signin_skipped", null)
            val popped = navController().popBackStack()
            Timber.i("Popped back stack : %s ", popped)
        }

        return rootView
    }

    fun signIn() {
        sharedPreferences.edit()
            .putString(PREFKEY_GSIGN_CHOICE, PREFVAL_GSIGN_GOOGLE)
            .apply()
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Timber.i("User succesfully logged into to google - SigninFragment")
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            // Google Sign In was successful, authenticate with Firebase
            val account = task.getResult(ApiException::class.java)
            Timber.i("Logged in user - %s", account!!.id)
            auth.googleAuth(account, firestore)
            navController().popBackStack()
        } catch (e: ApiException) {
            Timber.e(e)
            navController().popBackStack()
        }
    }

    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()

}