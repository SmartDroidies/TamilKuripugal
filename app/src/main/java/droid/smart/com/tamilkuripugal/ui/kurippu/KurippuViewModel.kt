package droid.smart.com.tamilkuripugal.ui.kurippu

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import droid.smart.com.tamilkuripugal.repo.KurippuRepository
import droid.smart.com.tamilkuripugal.util.AbsentLiveData
import droid.smart.com.tamilkuripugal.vo.Kurippu
import droid.smart.com.tamilkuripugal.vo.Resource
import javax.inject.Inject

class KurippuViewModel @Inject constructor(kurippuRepository: KurippuRepository) : ViewModel() {

    private val _kurippuId: MutableLiveData<String> = MutableLiveData()

    val kurippu: LiveData<Resource<Kurippu>> = Transformations
        .switchMap(_kurippuId) { kurippu ->
            if (kurippu == null) {
                AbsentLiveData.create()
            } else {
                kurippuRepository.loadKurippu(kurippu)
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
}




