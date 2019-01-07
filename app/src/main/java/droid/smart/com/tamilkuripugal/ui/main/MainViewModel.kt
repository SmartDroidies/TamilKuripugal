package droid.smart.com.tamilkuripugal.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.smart.droid.tamil.tips.R
import droid.smart.com.tamilkuripugal.repo.CategoryRepository
import droid.smart.com.tamilkuripugal.testing.OpenForTesting
import droid.smart.com.tamilkuripugal.vo.Category
import droid.smart.com.tamilkuripugal.vo.Resource
import javax.inject.Inject

@OpenForTesting
class MainViewModel @Inject constructor(categoryRepository: CategoryRepository) : ViewModel() {

    //FIXME - Collect this from database
    val CATEGORY_DATA = listOf(
        Category("CTGRY01", "HEALTH", "Health Tips", 1, 5, null, R.drawable.arokiyam, "health"),
        Category("CTGRY02", "BEAUTY", "Beauty Tips", 2, 6, null, R.drawable.azagu, "beauty")
    )

    private val _user = MutableLiveData<String>()
    private val _categories = MutableLiveData<Resource<List<Category>>>()
    val user: LiveData<String>
        get() = _user

    val categories: LiveData<Resource<List<Category>>>
        get() = _categories

    init {
        _categories.value = Resource.success(CATEGORY_DATA)
    }
    /*
    val categories: MutableLiveData<Resource<List<Category>>> = Transformations.switchMap(_user) { user ->
        Resource.success(CATEGORY_DATA)
        /*
        if (user == null) {
            AbsentLiveData.create()
        } else {
            categoryRepository.loadCategories()
        }
        */
    }
    */

    fun setUser(user: String?) {
        if (_user.value != user) {
            _user.value = user
        }
    }

    fun retry() {
        _user.value?.let {
            _user.value = it
        }
    }
}
