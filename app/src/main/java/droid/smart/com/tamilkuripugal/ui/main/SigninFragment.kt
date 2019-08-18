package droid.smart.com.tamilkuripugal.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import droid.smart.com.tamilkuripugal.di.Injectable
import kotlinx.android.synthetic.main.signin_fragment.view.*
import javax.inject.Inject


class SigninFragment : Fragment(), Injectable {

    private lateinit var googleSignInClient: GoogleSignInClient

    @Inject
    lateinit var googleSignInOptions: GoogleSignInOptions

    private val RC_SIGN_IN: Int = 75

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(com.smart.droid.tamil.tips.R.layout.signin_fragment, container, false)

        // Build a GoogleSignInClient with the options specified by gso.
        googleSignInClient = context?.let { GoogleSignIn.getClient(it, googleSignInOptions) }!!

        val signinButton = rootView.sign_in_button
        signinButton.setOnClickListener(View.OnClickListener {
            Toast.makeText(this.context, "Google Signin clicked", Toast.LENGTH_SHORT).show()
            signIn()
        })
        return rootView
    }

    fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }


}