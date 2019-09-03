package droid.smart.com.tamilkuripugal.repo

import androidx.lifecycle.LiveData
import com.google.firebase.firestore.FirebaseFirestore
import droid.smart.com.tamilkuripugal.AppExecutors
import droid.smart.com.tamilkuripugal.api.ApiResponse
import droid.smart.com.tamilkuripugal.data.FavouriteDao
import droid.smart.com.tamilkuripugal.util.AbsentLiveData
import droid.smart.com.tamilkuripugal.util.RateLimiter
import droid.smart.com.tamilkuripugal.vo.Favourite
import droid.smart.com.tamilkuripugal.vo.Kurippu
import droid.smart.com.tamilkuripugal.vo.Resource
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class FavouriteRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val favouriteDao: FavouriteDao,
    private val firebaseFirestore: FirebaseFirestore,
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

    fun insertFavourite(favourite: Favourite) {
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
            override fun saveCallResult(favourite: Favourite) {
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
    fun loadFavouriteKuripugal(): LiveData<Resource<List<Kurippu>>> {
        return object : NetworkBoundResource<List<Kurippu>, Favourite>(appExecutors) {
            override fun saveCallResult(favourite: Favourite) {
                //No Action required
            }

            override fun shouldFetch(data: List<Kurippu>?): Boolean {
                return false //no cloud syncing
            }

            override fun loadFromDb() = favouriteDao.loadFavourites()

            override fun createCall(): LiveData<ApiResponse<Favourite>> {
                return AbsentLiveData.create() //no cloud syncing
            }
        }.asLiveData()
    }

    fun loadFavouriteKuripugal(userid: String): LiveData<Resource<List<Kurippu>>> {
        return object : NetworkBoundResource<List<Kurippu>, Favourite>(appExecutors) {
            override fun saveCallResult(favourite: Favourite) {
                favouriteDao.insert(favourite)
            }

            override fun shouldFetch(data: List<Kurippu>?): Boolean {
                //TODO - Should fetch if favourites is empty, last synced is more than 12 hours
                return userid != null
            }

            override fun loadFromDb() = favouriteDao.loadFavourites()

            override fun createCall(): LiveData<ApiResponse<Favourite>> {
                Timber.i("Load favourites fro m firebase for user %s", userid)
                firebaseFirestore.collection("users")
                    .document(userid)
                    .collection("kuripugal")
                    .get()
                    .addOnSuccessListener { result ->
                        for (document in result) {
                            Timber.i("Kurippu : %s - %s", document.id, document.data)
                        }
                    }
                    .addOnFailureListener { exception ->
                        Timber.w(exception, "Error getting documents")
                    }
                    .addOnCompleteListener {
                        Timber.i("Completed collecting favourites")
                    }
                return AbsentLiveData.create() //TODO - Later load from firebase
            }
        }.asLiveData()
    }

}