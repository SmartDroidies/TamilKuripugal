package droid.smart.com.tamilkuripugal.ui.kuripugal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.smart.droid.tamil.tips.R
import com.smart.droid.tamil.tips.databinding.KuripugalFragmentBinding
import droid.smart.com.tamilkuripugal.AppExecutors
import droid.smart.com.tamilkuripugal.binding.FragmentDataBindingComponent
import droid.smart.com.tamilkuripugal.di.Injectable
import droid.smart.com.tamilkuripugal.ui.common.DividerItemDecoration
import droid.smart.com.tamilkuripugal.ui.common.KuripugalAdapter
import droid.smart.com.tamilkuripugal.ui.common.RetryCallback
import droid.smart.com.tamilkuripugal.util.autoCleared
import javax.inject.Inject

class KuripugalFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var kuripugalViewModel: KuripugalViewModel

    @Inject
    lateinit var appExecutors: AppExecutors

    // mutable for testing
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    var binding by autoCleared<KuripugalFragmentBinding>()

    private var adapter by autoCleared<KuripugalAdapter>()

    @Inject
    lateinit var adRequest: AdRequest

    lateinit var mAdView: AdView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<KuripugalFragmentBinding>(
            inflater,
            R.layout.kuripugal_fragment,
            container,
            false
        )
        dataBinding.retryCallback = object : RetryCallback {
            override fun retry() {
                kuripugalViewModel.retry()
            }
        }
        binding = dataBinding
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        kuripugalViewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(KuripugalViewModel::class.java)
        val params = KuripugalFragmentArgs.fromBundle(arguments!!)
        kuripugalViewModel.setCategoryId(params.category)
        binding.setLifecycleOwner(viewLifecycleOwner)
        binding.kurippugal = kuripugalViewModel.kuripugal

        val adapter = KuripugalAdapter(dataBindingComponent, appExecutors) { kurippu ->
            navController().navigate(
                KuripugalFragmentDirections.kurippu(kurippu.kurippuId)
            )
        }
        this.adapter = adapter
        binding.kuripugalList.adapter = adapter
        val mLayoutManager = LinearLayoutManager(context)
        binding.kuripugalList.layoutManager = mLayoutManager
        val itemDecor = DividerItemDecoration(context!!)
        binding.kuripugalList.addItemDecoration(itemDecor)

        initKuripugalList(kuripugalViewModel)

        mAdView = binding.adView
        mAdView.loadAd(adRequest)
    }

    private fun initKuripugalList(viewModel: KuripugalViewModel) {
        viewModel.kuripugal.observe(viewLifecycleOwner, Observer { listResource ->
            if (listResource?.data != null) {
                adapter.submitList(listResource.data)
            } else {
                adapter.submitList(emptyList())
            }
        })
    }

    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()


}
