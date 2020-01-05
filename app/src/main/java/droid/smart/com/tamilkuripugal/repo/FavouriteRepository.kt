package droid.smart.com.tamilkuripugal.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import droid.smart.com.tamilkuripugal.AppExecutors
import droid.smart.com.tamilkuripugal.api.ApiResponse
import droid.smart.com.tamilkuripugal.api.ApiSuccessResponse
import droid.smart.com.tamilkuripugal.api.KuripugalService
import droid.smart.com.tamilkuripugal.data.FavouriteDao
import droid.smart.com.tamilkuripugal.data.KurippuDao
import droid.smart.com.tamilkuripugal.util.AbsentLiveData
import droid.smart.com.tamilkuripugal.util.RateLimiter
import droid.smart.com.tamilkuripugal.util.cloudStatusModified
import droid.smart.com.tamilkuripugal.util.cloudStatusSynced
import droid.smart.com.tamilkuripugal.vo.Favourite
import droid.smart.com.tamilkuripugal.vo.FavouriteKurippu
import droid.smart.com.tamilkuripugal.vo.Kurippu
import droid.smart.com.tamilkuripugal.vo.Resource
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class FavouriteRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val favouriteDao: FavouriteDao,
    private val kurippuDao: KurippuDao,
    private val firebaseFirestore: FirebaseFirestore,
    private val kuripugalService: KuripugalService,
    private val rateLimiter: RateLimiter
) {

    fun insertFavourite(kurippuId: String, cloudStatus: String) {
        val favourite = Favourite(kurippuId, Date().time, true, cloudStatus)
        insertFavourite(favourite)
    }

    fun removeFavourite(kurippuId: String, cloudStatus: String) {
        val favourite = Favourite(kurippuId, Date().time, false, cloudStatus)
        updateFavourite(favourite)
    }

    private fun insertFavourite(favourite: Favourite) {
        appExecutors.diskIO().execute {
            favouriteDao.insert(favourite)
        }
    }

    fun updateFavourite(favourite: Favourite) {
        appExecutors.diskIO().execute {
            favouriteDao.update(favourite)
        }
    }


    fun loadFavourite(kurippuId: String): LiveData<Resource<Favourite>> {
        return object : NetworkBoundResource<Favourite, Favourite>(appExecutors) {
            override fun saveCallResult(item: Favourite) {
                // favouriteDao.insert(favourite)
            }

            override fun shouldFetch(data: Favourite?): Boolean {
                return false
            }

            override fun loadFromDb() = favouriteDao.loadFavourite(kurippuId)

            override fun createCall(): LiveData<ApiResponse<Favourite>> {
                // Note: No action required as the favourites are synced when the favourites are loaded
                return AbsentLiveData.create()
            }
        }.asLiveData()
    }

    //When user skips signin then return favourites only from device
    fun loadFavouriteKuripugal(): LiveData<Resource<List<FavouriteKurippu>>> {
        return object : NetworkBoundResource<List<FavouriteKurippu>, Favourite>(appExecutors) {
            override fun saveCallResult(item: Favourite) {
                //No Action required
            }

            override fun shouldFetch(data: List<FavouriteKurippu>?): Boolean {
                return false //no cloud syncing
            }

            override fun loadFromDb() = favouriteDao.loadFavourites()

            override fun createCall(): LiveData<ApiResponse<Favourite>> {
                return AbsentLiveData.create() //no cloud syncing
            }
        }.asLiveData()
    }

    fun loadFavouriteKuripugal(userid: String): LiveData<Resource<List<FavouriteKurippu>>> {
        return object : NetworkBoundResource<List<FavouriteKurippu>, QuerySnapshot>(appExecutors) {
            override fun saveCallResult(item: QuerySnapshot) {
                for (document in item) {
                    Timber.i("Sync Firestore favourite Kurippu : %s - %s", document.id, document.data)
                    var dbfavourite = favouriteDao.loadById(document.id)
                    Timber.i("Locale favourite Kurippu : %s", dbfavourite)
                    if (dbfavourite != null) {
                        favouriteDao.update(
                            Favourite(
                                document.id,
                                document.data.get("updated") as Long,
                                true,
                                cloudStatusSynced
                            )
                        )
                    } else {
                        favouriteDao.insert(
                            Favourite(
                                document.id,
                                document.data.get("updated") as Long,
                                true,
                                cloudStatusSynced
                            )
                        )
                    }
                }
            }

            override fun shouldFetch(data: List<FavouriteKurippu>?): Boolean {
                return data == null || data.isEmpty() || rateLimiter.shouldFetch("fav_kurippugal", 6, TimeUnit.HOURS)
            }

            override fun loadFromDb() = favouriteDao.loadFavourites()

            override fun createCall(): LiveData<ApiResponse<QuerySnapshot>> {
                Timber.i("Load favourites fro m firebase for user %s", userid)
                val cloudFavourites: MutableLiveData<ApiResponse<QuerySnapshot>> = MutableLiveData()

                firebaseFirestore.collection("users")
                    .document(userid)
                    .collection("kuripugal")
                    .whereEqualTo("fav", "Y")
                    .addSnapshotListener { value, e ->
                        if (e != null) {
                            Timber.w(e, "Firestore favourites listen failed")
                            return@addSnapshotListener
                        }
                        cloudFavourites.value = ApiSuccessResponse(value!!, "")

                    }
                return cloudFavourites
            }
        }.asLiveData()
    }


    fun loadKuripugal(kurippuIds: List<String>): LiveData<Resource<List<FavouriteKurippu>>> {
        return object : NetworkBoundResource<List<FavouriteKurippu>, List<Kurippu>>(appExecutors) {
            override fun saveCallResult(item: List<Kurippu>) {
                Timber.d("Update store Kuripugal for %s - %s", kurippuIds, item.size)
                kurippuDao.insertKuripugal(item)
            }

            override fun shouldFetch(data: List<FavouriteKurippu>?): Boolean {
                return kurippuIds.isNotEmpty()
            }

            override fun loadFromDb() = favouriteDao.loadFavourites()

            override fun createCall() = kuripugalService.getKuripugal("y", kurippuIds.joinToString())

            override fun onFetchFailed() {
                Timber.w("Error is collecting Kuripugal for kurippu ids")
                super.onFetchFailed()
            }
        }.asLiveData()
    }

    fun syncLocalWithCloud(firebaseAuth: FirebaseAuth, firestore: FirebaseFirestore) {
        if (firebaseAuth.currentUser != null) {
            appExecutors.diskIO().execute {
                val localFavourites = favouriteDao.loadFavouritesByStatus(cloudStatusModified)
                for (favourite: Favourite in localFavourites) {
                    Timber.i("Sync with cloud : %s", favourite.kurippuId)
                    val fav = if (favourite.active) "Y" else "N"
                    val favouriteCloud = hashMapOf(
                        "fav" to fav,
                        "updated" to favourite.updatedDate
                    )

                    firestore.collection("users")
                        .document(firebaseAuth.currentUser!!.uid)
                        .collection("kuripugal")
                        .document(favourite.kurippuId)
                        .set(favouriteCloud)
                        .addOnSuccessListener { _ ->
                            favourite.cloudStatus = cloudStatusSynced
                            appExecutors.networkIO().execute {
                                favouriteDao.update(favourite)
                            }
                            Timber.d(
                                "Cloud favourite %s succesfully updated for %s",
                                favourite.kurippuId,
                                firebaseAuth.currentUser?.uid
                            )
                        }
                        .addOnFailureListener { e ->
                            Timber.w(
                                e, "Firestore Error adding favourite %s for %s",
                                favourite.kurippuId,
                                firebaseAuth.currentUser?.uid
                            )
                        }

                }
            }
        }
    }
}