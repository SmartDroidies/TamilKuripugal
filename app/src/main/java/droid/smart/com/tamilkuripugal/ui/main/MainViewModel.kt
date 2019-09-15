package droid.smart.com.tamilkuripugal.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import droid.smart.com.tamilkuripugal.repo.CategoryRepository
import droid.smart.com.tamilkuripugal.repo.FavouriteRepository
import droid.smart.com.tamilkuripugal.vo.Category
import droid.smart.com.tamilkuripugal.vo.Resource
import timber.log.Timber
import javax.inject.Inject

class MainViewModel @Inject constructor(
    categoryRepository: CategoryRepository,
    favouriteRepository: FavouriteRepository,
    firebaseAuth: FirebaseAuth,
    firebaseFirestore: FirebaseFirestore
) : ViewModel() {

    private val favouriteRepository = favouriteRepository

    private val firebaseAuth = firebaseAuth

    private val firebaseFirestore = firebaseFirestore

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
            Timber.i("User Logged in : %s", _user.value)
            syncLocalToCloud()
        }
    }

    private fun syncLocalToCloud() {
        favouriteRepository.syncLocalWithCloud(firebaseAuth, firebaseFirestore)
    }

    fun retry() {
        _user.value?.let {
            _user.value = it
        }
    }


}
