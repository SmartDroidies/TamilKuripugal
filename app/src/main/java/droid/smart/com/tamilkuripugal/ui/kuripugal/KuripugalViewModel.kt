package droid.smart.com.tamilkuripugal.ui.kuripugal

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import droid.smart.com.tamilkuripugal.repo.CategoryRepository
import droid.smart.com.tamilkuripugal.repo.KurippuRepository
import droid.smart.com.tamilkuripugal.util.AbsentLiveData
import droid.smart.com.tamilkuripugal.vo.Category
import droid.smart.com.tamilkuripugal.vo.Kurippu
import droid.smart.com.tamilkuripugal.vo.Resource
import timber.log.Timber
import javax.inject.Inject

class KuripugalViewModel @Inject constructor(kurippuRepository: KurippuRepository,
                                             categoryRepository: CategoryRepository) : ViewModel() {

    private val _category = MutableLiveData<Int>()

    private val categories = categoryRepository.loadCategories()

    val category: MutableLiveData<Category>
        get() = collectCategoryObj(_category.value!!)


    val kuripugal:  LiveData<Resource<List<Kurippu>>> = Transformations.switchMap(_category) { category ->
            if (category == null) {
                AbsentLiveData.create()
            } else {
                kurippuRepository.loadKuripugal(category)
            }
        }

    fun setCategoryId(categoryId: Int) {
        if (_category.value != categoryId) {
            _category.value = categoryId
        }
    }

    fun retry() {
        _category.value?.let {
            _category.value = it
        }
    }

    private fun collectCategoryObj(kurippuCategory: Int): MutableLiveData<Category> {
        val iteratorCategory = categories!!.iterator()
        while (iteratorCategory.hasNext()) {
            val category = iteratorCategory.next()
            Timber.d("Category comparision : %s vs %s", kurippuCategory, category.category)
            if(category.category == kurippuCategory) {
                return MutableLiveData(category)
            }
        }
        return MutableLiveData()
    }

}
