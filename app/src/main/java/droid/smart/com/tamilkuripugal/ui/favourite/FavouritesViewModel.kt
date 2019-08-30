package droid.smart.com.tamilkuripugal.ui.favourite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import droid.smart.com.tamilkuripugal.repo.FavouriteRepository
import droid.smart.com.tamilkuripugal.util.AbsentLiveData
import droid.smart.com.tamilkuripugal.vo.Kurippu
import droid.smart.com.tamilkuripugal.vo.Resource
import timber.log.Timber
import javax.inject.Inject

class FavouritesViewModel @Inject constructor(favouriteRepository: FavouriteRepository) : ViewModel() {

    private val _userId = MutableLiveData<String>()

    val kuripugal: LiveData<Resource<List<Kurippu>>> = Transformations.switchMap(_userId) { userid ->
        if (userid == null) {
            Timber.i("Load favourite kuripugal from device")
            AbsentLiveData.create()
        } else {
            Timber.i("Load favourite kuripugal for user %s", userid)
            favouriteRepository.loadFavouriteKuripugal(userid)
        }
    }

    fun setUserId(userId: String) {
        if (_userId.value != userId) {
            _userId.value = userId
        }
    }


}