package droid.smart.com.tamilkuripugal.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import droid.smart.com.tamilkuripugal.repo.CategoryRepository
import droid.smart.com.tamilkuripugal.vo.Category
import droid.smart.com.tamilkuripugal.vo.Resource
import javax.inject.Inject

class MainViewModel @Inject constructor(categoryRepository: CategoryRepository) : ViewModel() {

    /*companion object {
        val CATEGORY_DATA = listOf(
            Category("CTGRY01", "HEALTH", "Health Tips", 1, 5, null, R.drawable.arokiyam, "native-health"),
            Category("CTGRY02", "BEAUTY", "Beauty Tips", 2, 6, null, R.drawable.azagu, "native-beauty"),
            Category("CTGRY03", "TREATMENT", "Home Remedies", 3, 10, null, R.drawable.naattu, "native-home-remedies"),
            Category("CTGRY04", "COOKING", "Cooking Tips", 4, 3, null, R.drawable.samayal, "native-cooking"),
            Category("CTGRY05", "VETTU", "House Tips", 5, 1096, null, R.drawable.vettu, "native-house-keeping"),
            Category("CTGRY06", "MARUTHUVAM", "Medical Tips", 6, 1095, null, R.drawable.maruthuvam, "native-medicine"),
            Category("CTGRY07", "AANMEEGAM", "Divine Tips", 7, 7236, null, R.drawable.aanmeega, "native-aanmeegam"),
            Category("CTGRY08", "GENERAL", "General Tips", 8, 2920, null, R.drawable.general, "native-general-tip"),
            Category(
                "CTGRY09",
                "AGRICULTURE",
                "Agricultrue Tips",
                9,
                9479,
                null,
                R.drawable.agriculture,
                "native-agriculture"
            )
        )
    }
*/
    private val _user = MutableLiveData<String>()
    private val _categories = MutableLiveData<Resource<List<Category>>>()
    val user: LiveData<String>
        get() = _user

    val categories: LiveData<Resource<List<Category>>>
        get() = _categories

    init {
        _categories.value = Resource.success(categoryRepository.loadCategories())
    }

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
