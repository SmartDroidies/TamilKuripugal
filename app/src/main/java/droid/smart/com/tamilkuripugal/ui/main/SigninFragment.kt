package droid.smart.com.tamilkuripugal.ui.main

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import droid.smart.com.tamilkuripugal.di.Injectable
import droid.smart.com.tamilkuripugal.util.PREFKEY_GSIGN_CHOICE
import droid.smart.com.tamilkuripugal.util.PREFKEY_GSIGN_SKIPTS
import droid.smart.com.tamilkuripugal.util.PREFVAL_GSIGN_GOOGLE
import droid.smart.com.tamilkuripugal.util.PREFVAL_GSIGN_SKIP
import kotlinx.android.synthetic.main.main_activity.*
import kotlinx.android.synthetic.main.signin_fragment.view.*
import timber.log.Timber
import javax.inject.Inject

//https://medium.com/a-problem-like-maria/a-problem-like-navigation-part-2-63e46a565d4b
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
            firebaseAuthWithGoogle(account)
        } catch (e: ApiException) {
            Timber.e(e)
        }
    }

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

    /*
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
*/


    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()

}