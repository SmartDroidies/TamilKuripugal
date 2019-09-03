package droid.smart.com.tamilkuripugal.ui.kurippu

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import droid.smart.com.tamilkuripugal.repo.FavouriteRepository
import droid.smart.com.tamilkuripugal.repo.KurippuRepository
import droid.smart.com.tamilkuripugal.util.AbsentLiveData
import droid.smart.com.tamilkuripugal.util.cloudStatusModified
import droid.smart.com.tamilkuripugal.util.cloudStatusSynced
import droid.smart.com.tamilkuripugal.vo.Favourite
import droid.smart.com.tamilkuripugal.vo.Kurippu
import droid.smart.com.tamilkuripugal.vo.Resource
import timber.log.Timber
import javax.inject.Inject

class KurippuViewModel @Inject constructor(kurippuRepository: KurippuRepository,
                                           favouriteRepository: FavouriteRepository,
                                           firebaseAuth: FirebaseAuth,
                                           firebaseFirestore: FirebaseFirestore
) : ViewModel() {

    private val _kurippuId: MutableLiveData<String> = MutableLiveData()

    private val favouriteRepository = favouriteRepository

    private val firebaseAuth = firebaseAuth

    private val firestore = firebaseFirestore

    val kurippu: LiveData<Resource<Kurippu>> = Transformations
        .switchMap(_kurippuId) { kurippu ->
            if (kurippu == null) {
                AbsentLiveData.create()
            } else {
                kurippuRepository.loadKurippu(kurippu)
            }
        }

    val favourite: LiveData<Resource<Favourite>> = Transformations
        .switchMap(_kurippuId) { favourite ->
            if (favourite == null) {
                AbsentLiveData.create()
            } else {
                favouriteRepository.loadFavourite(favourite)
            }
        }


    fun retry() {
        _kurippuId.value?.let {
            _kurippuId.value = it
        }
    }

    fun setKurippuId(kurippuId: String) {
        if (_kurippuId.value != kurippuId) {
            _kurippuId.value = kurippuId
        }
    }

    fun getKurippuId(): String {
        return _kurippuId.value!!
    }

    fun favourite() {
        Timber.d("Add kurippu %s to favourite of %s ", _kurippuId.value, firebaseAuth.currentUser?.uid)
        if (firebaseAuth.currentUser != null) {

            val favourite = hashMapOf(
                "fav" to "Y",
                "updated" to System.currentTimeMillis()
            )

            firestore.collection("users")
                .document(firebaseAuth.currentUser!!.uid)
                .collection("kuripugal")
                .document(_kurippuId.value!!)
                .set(favourite)
                .addOnSuccessListener { documentReference ->
                    favouriteRepository.insertFavourite(_kurippuId.value!!, cloudStatusSynced)
                    Timber.d(
                        "Cloud favourite %s succesfully updated for %s",
                        _kurippuId.value,
                        firebaseAuth.currentUser?.uid
                    )
                }
                .addOnFailureListener { e ->
                    favouriteRepository.insertFavourite(_kurippuId.value!!, cloudStatusModified)
                    Timber.w(
                        e, "Firestore Error adding favourite %s for %s",
                        _kurippuId.value,
                        firebaseAuth.currentUser?.uid
                    )
                }
        } else {
            favouriteRepository.insertFavourite(_kurippuId.value!!, cloudStatusModified)
        }
    }

    fun unfavourite() {
        Timber.d("Remove kurippu %s from favourites of %s ", _kurippuId.value, firebaseAuth.currentUser?.uid)
        if (firebaseAuth.currentUser != null) {

            val favourite = hashMapOf(
                "fav" to "N",
                "updated" to System.currentTimeMillis()
            )

            firestore.collection("users")
                .document(firebaseAuth.currentUser!!.uid)
                .collection("kuripugal")
                .document(_kurippuId.value!!)
                .set(favourite)
                .addOnSuccessListener { documentReference ->
                    favouriteRepository.removeFavourite(_kurippuId.value!!, cloudStatusSynced)
                    Timber.d(
                        "Cloud unfavourite %s succesfully updated for %s",
                        _kurippuId.value,
                        firebaseAuth.currentUser?.uid
                    )
                }
                .addOnFailureListener { e ->
                    favouriteRepository.removeFavourite(_kurippuId.value!!, cloudStatusModified)
                    Timber.w(
                        e, "Firestore Error removing favourite %s for %s",
                        _kurippuId.value,
                        firebaseAuth.currentUser?.uid
                    )
                }
        } else {
            favouriteRepository.removeFavourite(_kurippuId.value!!, cloudStatusModified)
        }
    }
}




