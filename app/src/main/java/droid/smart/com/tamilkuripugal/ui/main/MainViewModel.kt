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
        Category("CTGRY02", "BEAUTY", "Beauty Tips", 2, 6, null, R.drawable.azagu, "beauty"),
        Category("CTGRY03", "TREATMENT", "Home Remedies", 3, 10, null, R.drawable.naattu, "home-remedies"),
        Category("CTGRY04", "COOKING", "Cooking Tips", 4, 3, null, R.drawable.samayal, "cooking"),
        Category("CTGRY05", "VETTU", "House Tips", 5, 1096, null, R.drawable.vettu, "house-keeping"),
        Category("CTGRY06", "MARUTHUVAM", "Medical Tips", 6, 1095, null, R.drawable.maruthuvam, "medicine"),
        Category("CTGRY07", "AANMEEGAM", "Divine Tips", 7, 7236, null, R.drawable.aanmeega, "aanmeegam"),
        Category("CTGRY08", "GENERAL", "General Tips", 8, 2920, null, R.drawable.general, "general-tip")
    )


    //        {"code": "HEALTH", "id": "5", "label": "pirivu.health", "order": 3, "color" : "blue", "imageUrl": "images/arokiyam.png", "data" : "files/health.json", "topic" : "health"},
//        {"code": "BEAUTY", "id": "6", "label": "pirivu.beauty", "order": 4, "color" : "pink", "imageUrl": "images/azagu.png", "data" : "files/beauty.json", "topic" : "beauty"},
//        {"code": "TREATMENT", "id": "10", "label": "pirivu.treatment", "order": 5, "color" : "purple", "imageUrl": "images/naattu.png", "data" : "files/treatment.json", "topic" : "home-remedies"},
//        {"code": "COOKING", "id": "3", "label": "pirivu.cooking", "order": 6, "color" : "brown", "imageUrl": "images/samayal.png", "data" : "files/cooking.json", "topic" : "cooking"},
//        {"code": "VETTU", "id": "1096", "label": "pirivu.vettu", "order": 7, "color" : "blue-grey", "imageUrl": "images/vettu.png", "data" : "files/vettu.json", "topic" : "house-keeping"},
//        {"code": "MARUTHUVAM", "id": "1095", "label": "pirivu.maruthuvam", "order": 8, "color" : "red", "imageUrl": "images/maruthuvam.png", "data" : "files/maruthuvam.json", "topic" : "medicine"},
//        {"code": "AANMEEGAM", "id": "7236", "label": "pirivu.aanmeegam", "order": 2, "color" : "yellow", "imageUrl": "images/aanmeega.png", "data" : "files/aanmeegam.json", "topic" : "aanmeegam"},
//        {"code": "GENERAL", "id": "2920", "label": "pirivu.general", "order": 9, "color" : "orange", "imageUrl": "images/general.png", "data" : "files/general.json", "topic" : "general-tip"},
//        {"code": "AGRICULTURE", "id": "9479", "label": "pirivu.agriculture", "order": 10, "color" : "green", "imageUrl": "images/agriculture.gif", "data" : "files/agriculture.json", "topic" : "general-tip"}];


    private val _user = MutableLiveData<String>()
    private val _categories = MutableLiveData<Resource<List<Category>>>()
    val user: LiveData<String>
        get() = _user

    val categories: LiveData<Resource<List<Category>>>
        get() = _categories

    init {
        _categories.value = Resource.success(CATEGORY_DATA)
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
