package droid.smart.com.tamilkuripugal.ui.main

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.analytics.FirebaseAnalytics
import droid.smart.com.tamilkuripugal.di.Injectable
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

    private val RC_SIGN_IN: Int = 75

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(com.smart.droid.tamil.tips.R.layout.signin_fragment, container, false)

        // Build a GoogleSignInClient with the options specified by gso.
        googleSignInClient = context?.let { GoogleSignIn.getClient(it, googleSignInOptions) }!!

        //sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

        val signinButton = rootView.sign_in_button
        signinButton.setOnClickListener(View.OnClickListener {
            Toast.makeText(this.context, "Google Signin clicked", Toast.LENGTH_SHORT).show()
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
            navController().navigate(SigninFragmentDirections.main())
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


    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()

}