package droid.smart.com.tamilkuripugal.ui.newkuripugal

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel;
import droid.smart.com.tamilkuripugal.repo.KurippuRepository
import droid.smart.com.tamilkuripugal.util.AbsentLiveData
import droid.smart.com.tamilkuripugal.vo.Kurippu
import droid.smart.com.tamilkuripugal.vo.Resource
import javax.inject.Inject

class NewKuripugalViewModel @Inject constructor(kurippuRepository: KurippuRepository) : ViewModel() {

    private val _lastViewed = MutableLiveData<Long>()

    val kuripugal: LiveData<Resource<List<Kurippu>>> = Transformations.switchMap(_lastViewed) { lastViewed ->
        if (lastViewed == null) {
            AbsentLiveData.create()
        } else {
            kurippuRepository.loadNewKuripugal(lastViewed)
        }
    }

    fun setLastViewed(lastViewedTime: Long) {
        if (_lastViewed.value != lastViewedTime) {
            _lastViewed.value = lastViewedTime
        }
    }

    fun retry() {
        _lastViewed.value?.let {
            _lastViewed.value = it
        }
    }

}
