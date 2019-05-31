package droid.smart.com.tamilkuripugal.repo

import androidx.lifecycle.LiveData
import droid.smart.com.tamilkuripugal.AppExecutors
import droid.smart.com.tamilkuripugal.api.ApiResponse
import droid.smart.com.tamilkuripugal.api.KuripugalService
import droid.smart.com.tamilkuripugal.data.FavouriteDao
import droid.smart.com.tamilkuripugal.util.AbsentLiveData
import droid.smart.com.tamilkuripugal.util.RateLimiter
import droid.smart.com.tamilkuripugal.vo.Favourite
import droid.smart.com.tamilkuripugal.vo.Kurippu
import droid.smart.com.tamilkuripugal.vo.Resource
import java.util.*
import javax.inject.Inject

class FavouriteRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val favouriteDao: FavouriteDao,
    private val kuripugalService: KuripugalService,
    private val rateLimiter: RateLimiter
) {

    val cloudStatusModified = "M"

    val cloudStatusSynced = "S"

    fun insertFavourite(kurippuId: String) {
        var favourite = Favourite(kurippuId, Date().time, true, cloudStatusModified)
        insertFavourite(favourite)
    }

    fun insertFavourite(favourite: Favourite) {
        appExecutors.diskIO().execute {
            favouriteDao.insert(favourite)
            //FIXME - If cloud sync is required sync here
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

    fun loadFavouriteKuripugal(userid: String?): LiveData<Resource<List<Kurippu>>> {
        return object : NetworkBoundResource<List<Kurippu>, Favourite>(appExecutors) {
            override fun saveCallResult(favourite: Favourite) {
                favouriteDao.insert(favourite)
            }

            override fun shouldFetch(data: List<Kurippu>?): Boolean {
                //TODO - Should fetch if favourites is empty, last synced is more than 12 hours
                return false
            }

            override fun loadFromDb() = favouriteDao.loadFavourites()

            override fun createCall(): LiveData<ApiResponse<Favourite>> {
                return AbsentLiveData.create() //TODO - Later load from firebase
            }
        }.asLiveData()
    }

}