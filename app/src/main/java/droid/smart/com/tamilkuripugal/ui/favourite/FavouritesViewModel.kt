package droid.smart.com.tamilkuripugal.ui.favourite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import droid.smart.com.tamilkuripugal.repo.FavouriteRepository
import droid.smart.com.tamilkuripugal.vo.Favourite
import droid.smart.com.tamilkuripugal.vo.Resource
import timber.log.Timber
import javax.inject.Inject

class FavouritesViewModel @Inject constructor(favouriteRepository: FavouriteRepository) : ViewModel() {

    private val _userId = MutableLiveData<String>()

    val favourites: LiveData<Resource<List<Favourite>>> = Transformations.switchMap(_userId) { userid ->
        if (userid == null) {
            Timber.d("Load favourite kuripugal from device")
            favouriteRepository.loadFavouriteKuripugal()
        } else {
            Timber.d("Load favourite kuripugal for user %s", userid)
            favouriteRepository.loadFavouriteKuripugal(userid)
        }
    }

    fun setUserId(userId: String?) {
        _userId.value = userId
    }


}