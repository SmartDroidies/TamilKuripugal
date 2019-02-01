package droid.smart.com.tamilkuripugal.repo

import androidx.lifecycle.LiveData
import droid.smart.com.tamilkuripugal.AppExecutors
import droid.smart.com.tamilkuripugal.api.KuripugalService
import droid.smart.com.tamilkuripugal.data.CategoryDao
import droid.smart.com.tamilkuripugal.vo.Category
import droid.smart.com.tamilkuripugal.vo.Resource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val categoryDao: CategoryDao,
    private val kuripugalService: KuripugalService
) {

    //private val repoListRateLimit = RateLimiter<String>(30, TimeUnit.MINUTES)

    fun loadCategories(): LiveData<Resource<List<Category>>> {
        return object : NetworkBoundResource<List<Category>, List<Category>>(appExecutors) {
            override fun saveCallResult(item: List<Category>) {
                categoryDao.insertCategories(item)
            }

            override fun shouldFetch(data: List<Category>?): Boolean {
                return data == null || data.isEmpty() /*|| repoListRateLimit.shouldFetch("category")*/
            }

            override fun loadFromDb() = categoryDao.loadCategories()

            override fun createCall() = kuripugalService.getCategories()
        }.asLiveData()
    }

}