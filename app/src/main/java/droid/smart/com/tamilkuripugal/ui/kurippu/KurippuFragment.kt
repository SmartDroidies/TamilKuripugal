package droid.smart.com.tamilkuripugal.ui.kurippu

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.smart.droid.tamil.tips.R
import com.smart.droid.tamil.tips.databinding.KurippuFragmentBinding
import droid.smart.com.tamilkuripugal.AppExecutors
import droid.smart.com.tamilkuripugal.binding.FragmentDataBindingComponent
import droid.smart.com.tamilkuripugal.di.Injectable
import droid.smart.com.tamilkuripugal.testing.OpenForTesting
import droid.smart.com.tamilkuripugal.ui.common.RetryCallback
import droid.smart.com.tamilkuripugal.util.autoCleared
import timber.log.Timber
import javax.inject.Inject

@OpenForTesting
class KurippuFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var kurippuViewModel: KurippuViewModel

    @Inject
    lateinit var appExecutors: AppExecutors

    // mutable for testing
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    var binding by autoCleared<KurippuFragmentBinding>()

    @Inject
    lateinit var adRequest: AdRequest

    lateinit var mAdView: AdView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        val dataBinding = DataBindingUtil.inflate<KurippuFragmentBinding>(
            inflater,
            R.layout.kurippu_fragment,
            container,
            false,
            dataBindingComponent
        )
        dataBinding.retryCallback = object : RetryCallback {
            override fun retry() {
                kurippuViewModel.retry()
            }
        }
        binding = dataBinding
        return dataBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        kurippuViewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(KurippuViewModel::class.java)
        val params = KurippuFragmentArgs.fromBundle(arguments!!)
        Timber.i("Display Kurippu details for : " + params.kurippuId)
        kurippuViewModel.setKurippuId(params.kurippuId)
        binding.setLifecycleOwner(viewLifecycleOwner)
        binding.kurippu = kurippuViewModel.kurippu

        mAdView = binding.adView
        mAdView.loadAd(adRequest)
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        //super.onCreateOptionsMenu(menu, menuInflater)
        menuInflater.inflate(R.menu.kurippu_overflow_menu, menu)
    }

    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()


}
