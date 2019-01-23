package droid.smart.com.tamilkuripugal.repo

import androidx.lifecycle.LiveData
import droid.smart.com.tamilkuripugal.AppExecutors
import droid.smart.com.tamilkuripugal.api.ApiResponse
import droid.smart.com.tamilkuripugal.api.KuripugalService
import droid.smart.com.tamilkuripugal.data.KurippuDao
import droid.smart.com.tamilkuripugal.util.RateLimiter
import droid.smart.com.tamilkuripugal.vo.Kurippu
import droid.smart.com.tamilkuripugal.vo.Resource
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class KurippuRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val kurippuDao: KurippuDao,
    private val kuripugalService: KuripugalService
) {

    private val kurippuListRateLimit = RateLimiter<String>(30, TimeUnit.MINUTES)

    private val kurippuRateLimit = RateLimiter<String>(120, TimeUnit.MINUTES)

    fun loadKuripugal(categoryId: Int): LiveData<Resource<List<Kurippu>>> {
        return object : NetworkBoundResource<List<Kurippu>, List<Kurippu>>(appExecutors) {
            override fun saveCallResult(item: List<Kurippu>) {
                kurippuDao.insertKuripugal(item)
            }

            override fun shouldFetch(data: List<Kurippu>?): Boolean {
                return data == null || data.isEmpty() || kurippuListRateLimit.shouldFetch("kuripugal-" + categoryId)
            }

            override fun loadFromDb() = kurippuDao.loadKuripugal(categoryId)

            override fun createCall() = kuripugalService.getKuripugal(categoryId)

            override fun onFetchFailed() {
                Timber.w("Error is collecting latest data from server for Kuripugal by Category")
                super.onFetchFailed()
            }
        }.asLiveData()
    }

    fun loadKurippu(kurippuId: String): LiveData<Resource<Kurippu>> {
        return object : NetworkBoundResource<Kurippu, Kurippu>(appExecutors) {
            override fun saveCallResult(kurippu: Kurippu) {
                kurippuDao.insert(kurippu)
            }

            override fun shouldFetch(data: Kurippu?): Boolean {
                return data == null || data.content == null || kurippuRateLimit.shouldFetch("kuripu-" + kurippuId)
            }

            override fun loadFromDb() = kurippuDao.loadKurippu(kurippuId)

            override fun createCall(): LiveData<ApiResponse<Kurippu>> {
                return kuripugalService.getKurippu("y", kurippuId);
            }
        }.asLiveData()
    }
}


