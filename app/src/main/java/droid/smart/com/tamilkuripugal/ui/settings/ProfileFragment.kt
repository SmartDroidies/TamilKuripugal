package droid.smart.com.tamilkuripugal.ui.settings

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import com.smart.droid.tamil.tips.BuildConfig
import com.smart.droid.tamil.tips.R
import com.smart.droid.tamil.tips.databinding.ProfileFragmentBinding
import droid.smart.com.tamilkuripugal.BaseActivity
import droid.smart.com.tamilkuripugal.binding.FragmentDataBindingComponent
import droid.smart.com.tamilkuripugal.di.Injectable
import droid.smart.com.tamilkuripugal.util.autoCleared
import timber.log.Timber
import javax.inject.Inject

class ProfileFragment : Fragment(), Injectable {

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    @Inject
    lateinit var profileViewModel: ProfileViewModel

    @Inject
    lateinit var firebaseAnalytics: FirebaseAnalytics

    var binding by autoCleared<ProfileFragmentBinding>()

    @Inject
    lateinit var googleSignInOptions: GoogleSignInOptions

    private lateinit var googleSignInClient: GoogleSignInClient

    private val RC_SIGN_IN: Int = 75

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val dataBinding = DataBindingUtil.inflate<ProfileFragmentBinding>(
            inflater,
            R.layout.profile_fragment,
            container,
            false,
            dataBindingComponent
        )

        googleSignInClient = GoogleSignIn.getClient(context!!, googleSignInOptions)

        dataBinding.textRateApp.setOnClickListener {
            (activity as BaseActivity).rateApp()
        }

        dataBinding.textShareApp.setOnClickListener {
            shareApp(true)
        }

        dataBinding.textFeedback.setOnClickListener {
            feedback()
        }

        dataBinding.textPrivacy.setOnClickListener {
            showPrivacy()
        }

        dataBinding.logout.setOnClickListener {
            //userLogout()
        }

        dataBinding.signInButton.setOnClickListener {
            signIn()
        }

        binding = dataBinding
        return dataBinding.root

    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun showPrivacy() {
        findNavController().navigate(ProfileFragmentDirections.privacy())
        //findNavController().navigate(ProfileFragmentDirections.)
    }

    private fun feedback() {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data =
            Uri.parse("mailto:smartdroidies@gmail.com") // only email apps should handle this
        intent.putExtra(
            Intent.EXTRA_SUBJECT,
            "Feedback on Tamil Kuripugal - " + BuildConfig.VERSION_NAME
        )
        if (intent.resolveActivity(activity!!.packageManager) != null) {
            startActivity(intent)
        }
    }

    private fun shareApp(indBanner: Boolean) {
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.type = "text/plain"
        shareIntent.putExtra(
            Intent.EXTRA_TEXT,
            "Try this great Tamil App - https://play.google.com/store/apps/details?id=" + activity!!.packageName
        )
        if (indBanner) {
            val imageUri = collectShareImage()
            shareIntent.type = "image/*"
            if (imageUri != null) {
                shareIntent.putExtra(
                    Intent.EXTRA_STREAM,
                    imageUri
                )
            }
        }
        startActivity(Intent.createChooser(shareIntent, getString(R.string.action_share)))
    }

    private fun collectShareImage(): Uri? {
        var imageUri: Uri? = null
        try {
            imageUri = Uri.parse(
                MediaStore.Images.Media.insertImage(
                    activity!!.contentResolver,
                    BitmapFactory.decodeResource(resources, R.drawable.banner), null, null
                )
            )
        } catch (e: NullPointerException) {
            Timber.e("Failed to store share app image")
        }
        return imageUri
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.lifecycleOwner = viewLifecycleOwner
        //binding.signInAccount = profileViewModel.signInAccount
        binding.appVersion.text = appVersion

        firebaseAnalytics.setCurrentScreen(
            activity!!,
            this.javaClass.simpleName,
            this.javaClass.simpleName
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Timber.d("User succesfully logged into to google")
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)
            Timber.d("Logged in user - %s", account!!.id)
            Snackbar.make(view!!, "Login completed", Snackbar.LENGTH_SHORT).show()
            profileViewModel.setSignInAccount(account)
            //(activity as MainActivity).updateUI(account)
        } catch (e: ApiException) {
            Snackbar.make(view!!, "Login failed", Snackbar.LENGTH_LONG).show()
            Timber.e(e)
        }
    }


    // initialize String
    private val appVersion: String
        get() {
            val context = context!!
            val packageManager = context.packageManager
            val packageName = context.packageName
            var appVersion = "Version x.x.x"
            try {
                appVersion = "Version " + packageManager.getPackageInfo(packageName, 0).versionName
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }

            return appVersion
        }

}