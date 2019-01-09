package droid.smart.com.tamilkuripugal.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import droid.smart.com.tamilkuripugal.AppExecutors
import droid.smart.com.tamilkuripugal.api.ApiResponse
import droid.smart.com.tamilkuripugal.api.KuripugalService
import droid.smart.com.tamilkuripugal.data.KurippuDao
import droid.smart.com.tamilkuripugal.util.RateLimiter
import droid.smart.com.tamilkuripugal.vo.Kurippu
import droid.smart.com.tamilkuripugal.vo.Resource
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class KurippuRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val kurippuDao: KurippuDao,
    private val kuripugalService: KuripugalService
) {

    private val kurippuListRateLimit = RateLimiter<String>(30, TimeUnit.MINUTES)

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
        }.asLiveData()
    }

    fun loadKurippu(kurippuId: MutableLiveData<String>): LiveData<Resource<Kurippu>> {
        return object : NetworkBoundResource<Kurippu, Kurippu>(appExecutors) {
            override fun saveCallResult(item: Kurippu) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun shouldFetch(data: Kurippu?): Boolean {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun loadFromDb(): LiveData<Kurippu> {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun createCall(): LiveData<ApiResponse<Kurippu>> {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }.asLiveData()
    }
}