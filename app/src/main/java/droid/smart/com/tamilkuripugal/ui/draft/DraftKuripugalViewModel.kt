package droid.smart.com.tamilkuripugal.ui.draft

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel;
import droid.smart.com.tamilkuripugal.repo.KurippuRepository
import droid.smart.com.tamilkuripugal.util.AbsentLiveData
import droid.smart.com.tamilkuripugal.vo.Kurippu
import droid.smart.com.tamilkuripugal.vo.Resource
import javax.inject.Inject

class DraftKuripugalViewModel @Inject constructor(kurippuRepository: KurippuRepository) : ViewModel() {

    private val _user = MutableLiveData<String>()

    val kuripugal: LiveData<Resource<List<Kurippu>>> = Transformations.switchMap(_user) { user ->
        if (user == null) {
            AbsentLiveData.create()
        } else {
            kurippuRepository.getScheduledKuripugal()
        }
    }

    fun setUser(user: String) {
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
