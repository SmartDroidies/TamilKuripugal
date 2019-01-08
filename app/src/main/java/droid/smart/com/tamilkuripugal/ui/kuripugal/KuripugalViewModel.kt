package droid.smart.com.tamilkuripugal.ui.kuripugal

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import droid.smart.com.tamilkuripugal.repo.KurippuRepository
import droid.smart.com.tamilkuripugal.testing.OpenForTesting
import droid.smart.com.tamilkuripugal.util.AbsentLiveData
import droid.smart.com.tamilkuripugal.vo.Kurippu
import droid.smart.com.tamilkuripugal.vo.Resource
import javax.inject.Inject

@OpenForTesting
class KuripugalViewModel @Inject constructor(kurippuRepository: KurippuRepository) : ViewModel() {

    private val _category = MutableLiveData<Int>()

    val category: LiveData<Int>
        get() = _category

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

}
