package droid.smart.com.tamilkuripugal.extensions

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import timber.log.Timber

fun FirebaseAuth.googleAuth(googleSignInAccount: GoogleSignInAccount, firestore: FirebaseFirestore) {
    Timber.d("FirebaseAuth with Google from Ext : %s", googleSignInAccount.id)
    val credential = GoogleAuthProvider.getCredential(googleSignInAccount.idToken, null)
    this.signInWithCredential(credential)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Timber.d("Firebase Authentication Success : %s", this.currentUser)
                val user = this.currentUser
                user?.let { this.checkCreateUserModule(firestore, user) }
            } else {
                Timber.e(task.exception)
            }
        }
}

fun FirebaseAuth.checkCreateUserModule(firestore: FirebaseFirestore, firebaseUser: FirebaseUser) {
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
