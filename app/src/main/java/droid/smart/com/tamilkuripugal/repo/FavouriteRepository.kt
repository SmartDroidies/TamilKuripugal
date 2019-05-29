package droid.smart.com.tamilkuripugal.repo

import androidx.lifecycle.LiveData
import droid.smart.com.tamilkuripugal.AppExecutors
import droid.smart.com.tamilkuripugal.api.ApiResponse
import droid.smart.com.tamilkuripugal.api.KuripugalService
import droid.smart.com.tamilkuripugal.data.FavouriteDao
import droid.smart.com.tamilkuripugal.util.AbsentLiveData
import droid.smart.com.tamilkuripugal.util.RateLimiter
import droid.smart.com.tamilkuripugal.vo.Favourite
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
                favouriteDao.insert(favourite)
            }

            override fun shouldFetch(data: Favourite?): Boolean {
                return false
            }

            override fun loadFromDb() = favouriteDao.loadFavourite(kurippuId)

            override fun createCall(): LiveData<ApiResponse<Favourite>> {
                return AbsentLiveData.create() //TODO - Later load from firebase
            }
        }.asLiveData()
    }

}