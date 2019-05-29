package droid.smart.com.tamilkuripugal.ui.kurippu

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import droid.smart.com.tamilkuripugal.repo.FavouriteRepository
import droid.smart.com.tamilkuripugal.repo.KurippuRepository
import droid.smart.com.tamilkuripugal.util.AbsentLiveData
import droid.smart.com.tamilkuripugal.vo.Favourite
import droid.smart.com.tamilkuripugal.vo.Kurippu
import droid.smart.com.tamilkuripugal.vo.Resource
import timber.log.Timber
import javax.inject.Inject

class KurippuViewModel @Inject constructor(kurippuRepository: KurippuRepository,
                                           favouriteRepository: FavouriteRepository) : ViewModel() {

    private val _kurippuId: MutableLiveData<String> = MutableLiveData()

    private val favouriteRepository = favouriteRepository

    val kurippu: LiveData<Resource<Kurippu>> = Transformations
        .switchMap(_kurippuId) { kurippu ->
            if (kurippu == null) {
                AbsentLiveData.create()
            } else {
                kurippuRepository.loadKurippu(kurippu)
            }
        }

    val favourite: LiveData<Resource<Favourite>> = Transformations
        .switchMap(_kurippuId) { favourite ->
            if (favourite == null) {
                AbsentLiveData.create()
            } else {
                favouriteRepository.loadFavourite(favourite)
            }
        }


    fun retry() {
        _kurippuId.value?.let {
            _kurippuId.value = it
        }
    }

    fun setKurippuId(kurippuId: String) {
        if (_kurippuId.value != kurippuId) {
            _kurippuId.value = kurippuId
        }
    }

    fun getKurippuId(): String {
        return _kurippuId.value!!
    }

    fun favourite() {
        Timber.i("Add kurippu to favourite : %s", _kurippuId.value)
        favouriteRepository.insertFavourite(_kurippuId.value!!)

        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        /*
        val favourites = firestore.collection("favourites")
        favourites.add(Favourite(kurippuViewModel.getKurippuId(), Date().time))
            .addOnSuccessListener { documentReference ->
                Timber.i("DocumentSnapshot added with ID: ${documentReference.id}")
                //FIXME - Report in Firebase Events
            }
            .addOnFailureListener { e ->
                Timber.e("Error adding document : %s", e)
                //FIXME - Report in Firebase Events
            };
            */

    }

    fun unfavourite() {
        Timber.i("Remove kurippu to favourite : %s", _kurippuId.value)
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}




