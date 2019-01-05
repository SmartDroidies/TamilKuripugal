package droid.smart.com.tamilkuripugal.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import droid.smart.com.tamilkuripugal.repo.CategoryRepository
import droid.smart.com.tamilkuripugal.testing.OpenForTesting
import droid.smart.com.tamilkuripugal.util.AbsentLiveData
import droid.smart.com.tamilkuripugal.vo.Category
import droid.smart.com.tamilkuripugal.vo.Resource
import javax.inject.Inject

@OpenForTesting
class MainViewModel @Inject constructor(categoryRepository: CategoryRepository) : ViewModel() {
    private val _user = MutableLiveData<String>()
    val user: LiveData<String>
        get() = _user

    val categories: LiveData<Resource<List<Category>>> = Transformations.switchMap(_user) { user ->
        if (user == null) {
            AbsentLiveData.create()
        } else {
            categoryRepository.loadCategories()
        }
    }

    fun setUser(user: String?) {
        if (_user.value != null) {
            _user.value = user
        }
    }

    fun retry() {
        _user.value?.let {
            _user.value = it
        }
    }
}
