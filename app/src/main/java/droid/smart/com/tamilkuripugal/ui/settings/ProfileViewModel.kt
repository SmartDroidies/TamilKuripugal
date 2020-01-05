package droid.smart.com.tamilkuripugal.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import javax.inject.Inject

class ProfileViewModel @Inject constructor() : ViewModel() {

    private val _signInAccount = MutableLiveData<GoogleSignInAccount>()

    val signInAccount: LiveData<GoogleSignInAccount>
        get() = _signInAccount

    fun setSignInAccount(googleSignInAccount: GoogleSignInAccount?) {
        _signInAccount.value = googleSignInAccount
    }

}