package droid.smart.com.tamilkuripugal.ui.favourite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.smart.droid.tamil.tips.R
import com.smart.droid.tamil.tips.databinding.FavouritesFragmentBinding
import droid.smart.com.tamilkuripugal.AppExecutors
import droid.smart.com.tamilkuripugal.binding.FragmentDataBindingComponent
import droid.smart.com.tamilkuripugal.di.Injectable
import droid.smart.com.tamilkuripugal.util.autoCleared
import javax.inject.Inject

class FavouritesFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var favouritesViewModel: FavouritesViewModel

    @Inject
    lateinit var appExecutors: AppExecutors

    // mutable for testing
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    var binding by autoCleared<FavouritesFragmentBinding>()

    //private var adapter by autoCleared<NewKuripugalAdapter>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<FavouritesFragmentBinding>(
            inflater,
            R.layout.favourites_fragment,
            container,
            false
        )

        binding = dataBinding
        return dataBinding.root
    }

}