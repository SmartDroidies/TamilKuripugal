package droid.smart.com.tamilkuripugal.ui.favourite

import androidx.lifecycle.*
import droid.smart.com.tamilkuripugal.repo.FavouriteRepository
import droid.smart.com.tamilkuripugal.repo.KurippuRepository
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

    init {

        kurippuRepository = pKurippuRepository
        favourites.addSource(_favourites) { result: Resource<List<FavouriteKurippu>> ->
            if (result.status == Status.SUCCESS) {
                favourites.value = loadFavDetails(result)
            }
        }
    }

    private fun loadFavDetails(favourites: Resource<List<FavouriteKurippu>>): Resource<List<FavouriteKurippu>> {
        return if (favourites.status == Status.SUCCESS) {
            for (fav: FavouriteKurippu in favourites.data!!) {
                Timber.i("Check & Load details for favourite : %s - %s", fav.kurippuId, fav.title)
                if (fav.title == null) {
                    fav.title = "Populate title here"
                }
                //val kurippuResource = kurippuRepository.loadKurippu(fav.kurippuId)
                //fav.kurippu = kurippuResource.value!!.data
            }
            Resource.success(favourites.data)
        } else {
            favourites
        }
    }

    fun setUserId(userId: String?) {
        _userId.value = userId
    }

}