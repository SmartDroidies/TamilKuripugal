package droid.smart.com.tamilkuripugal.ui.favourite

import androidx.lifecycle.*
import droid.smart.com.tamilkuripugal.repo.FavouriteRepository
import droid.smart.com.tamilkuripugal.repo.KurippuRepository
import droid.smart.com.tamilkuripugal.util.AbsentLiveData
import droid.smart.com.tamilkuripugal.vo.FavouriteKurippu
import droid.smart.com.tamilkuripugal.vo.Resource
import droid.smart.com.tamilkuripugal.vo.Status
import timber.log.Timber
import javax.inject.Inject

class FavouritesViewModel @Inject constructor(
    favouriteRepository: FavouriteRepository,
    pKurippuRepository: KurippuRepository
) : ViewModel() {

    private var kurippuRepository: KurippuRepository

    private val _userId = MutableLiveData<String>()

    private val _tobeSynced = MutableLiveData<List<String>>()

    val favourites = MediatorLiveData<Resource<List<FavouriteKurippu>>>()

    private val _favourites: LiveData<Resource<List<FavouriteKurippu>>> = Transformations.switchMap(_userId) { userid ->
        if (userid == null) {
            Timber.d("Load favourite kuripugal from device")
            favouriteRepository.loadFavouriteKuripugal()
        } else {
            Timber.d("Load favourite kuripugal for user %s", userid)
            favouriteRepository.loadFavouriteKuripugal(userid)
        }
    }

    private val _syncedFavourites: LiveData<Resource<List<FavouriteKurippu>>> =
        Transformations.switchMap(_tobeSynced) { tobeSynced ->
            if (tobeSynced == null) {
                AbsentLiveData.create()
            } else {
                Timber.d("Load kuripugal for list %s", tobeSynced)
                favouriteRepository.loadKuripugal(tobeSynced)
            }
        }

    init {

        kurippuRepository = pKurippuRepository
        favourites.addSource(_favourites) { result: Resource<List<FavouriteKurippu>> ->
            if (result.status == Status.SUCCESS) {
                favourites.value = result
                _tobeSynced.value = tobeSyncedItems(result)
            }
        }

        favourites.addSource(_syncedFavourites) { result: Resource<List<FavouriteKurippu>> ->
            if (result.status == Status.SUCCESS) {
                Timber.i("Merge synced favourites : %s ", result.data!!.size)
                favourites.value = result
            }
        }
    }

    private fun tobeSyncedItems(favourites: Resource<List<FavouriteKurippu>>): List<String>? {
        return if (favourites.status == Status.SUCCESS) {
            val tobeSycedList = ArrayList<String>()
            for (fav: FavouriteKurippu in favourites.data!!) {
                Timber.i("Check & Load details for favourite : %s - %s", fav.kurippuId, fav.title)
                if (fav.title == null) {
                    tobeSycedList.add(fav.kurippuId)
                }
            }
            return tobeSycedList
        } else {
            return null
        }
    }

    fun setUserId(userId: String?) {
        _userId.value = userId
    }

}