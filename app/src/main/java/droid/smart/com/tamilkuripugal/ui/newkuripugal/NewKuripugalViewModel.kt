package droid.smart.com.tamilkuripugal.ui.newkuripugal

import androidx.lifecycle.*
import droid.smart.com.tamilkuripugal.repo.CategoryRepository
import droid.smart.com.tamilkuripugal.repo.KurippuRepository
import droid.smart.com.tamilkuripugal.util.AbsentLiveData
import droid.smart.com.tamilkuripugal.vo.Category
import droid.smart.com.tamilkuripugal.vo.Kurippu
import droid.smart.com.tamilkuripugal.vo.Resource
import droid.smart.com.tamilkuripugal.vo.Status
import timber.log.Timber
import javax.inject.Inject

class NewKuripugalViewModel @Inject constructor(kurippuRepository: KurippuRepository,
                                                categoryRepository: CategoryRepository) : ViewModel() {

    private val _lastViewed = MutableLiveData<Long>()

    private val categories = categoryRepository.loadCategories()

    val kuripugal = MediatorLiveData<Resource<List<Kurippu>>>()

    val db_kuripugal: LiveData<Resource<List<Kurippu>>> = Transformations.switchMap(_lastViewed) { lastViewed ->
        if (lastViewed == null) {
            AbsentLiveData.create()
        } else {
            kurippuRepository.loadNewKuripugal(lastViewed)
        }
    }

    init {
        kuripugal.value = Resource.loading(null)
        kuripugal.addSource(db_kuripugal) { result: Resource<List<Kurippu>> ->
            result.let {
                kuripugal.value = processKuripugal(db_kuripugal.value)
            }
        }
    }

    private fun processKuripugal(kuripugalResource: Resource<List<Kurippu>>?): Resource<List<Kurippu>>? {
        return if (kuripugalResource!!.status == Status.SUCCESS) {
            Resource.success(attachCategory(kuripugalResource.data))
        } else {
            kuripugalResource
        }
    }

    private fun attachCategory(kuripugal: List<Kurippu>?): List<Kurippu>? {
        val listKuripugalMutable = kuripugal!!.toMutableList()

        if (listKuripugalMutable.size > 0) {
            val iterator = listKuripugalMutable.iterator()
            while (iterator.hasNext()) {
                val kurippu  = iterator.next();
                Timber.d("Attach Category : %s", kurippu.kurippuId)
                kurippu.categoryObj = collectCategoryObj(kurippu.category)
            }
        }
        return listKuripugalMutable.toList()
    }

    private fun collectCategoryObj(kurippuCategory: Int): Category? {
        val iteratorCategory = categories!!.iterator()
        while (iteratorCategory.hasNext()) {
            val category = iteratorCategory.next()
            Timber.d("Category comparision : %s vs %s", kurippuCategory, category.category)
            if(category.category == kurippuCategory) {
                return category
            }
        }
        return null
    }

    fun setLastViewed(lastViewedTime: Long) {
        if (_lastViewed.value != lastViewedTime) {
            _lastViewed.value = lastViewedTime
        }
    }

    fun retry() {
        _lastViewed.value?.let {
            _lastViewed.value = it
        }
    }

}
