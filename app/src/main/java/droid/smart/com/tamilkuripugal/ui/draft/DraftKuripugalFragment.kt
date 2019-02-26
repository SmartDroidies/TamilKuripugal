package droid.smart.com.tamilkuripugal.ui.draft

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
import com.smart.droid.tamil.tips.R
import com.smart.droid.tamil.tips.databinding.DraftKuripugalFragmentBinding
import droid.smart.com.tamilkuripugal.AppExecutors
import droid.smart.com.tamilkuripugal.binding.FragmentDataBindingComponent
import droid.smart.com.tamilkuripugal.di.Injectable
import droid.smart.com.tamilkuripugal.ui.common.DividerItemDecoration
import droid.smart.com.tamilkuripugal.ui.common.DraftKuripugalAdapter
import droid.smart.com.tamilkuripugal.ui.common.RetryCallback
import droid.smart.com.tamilkuripugal.ui.kuripugal.KuripugalFragmentDirections
import droid.smart.com.tamilkuripugal.util.autoCleared
import javax.inject.Inject

class DraftKuripugalFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var draftKuripugalViewModel: DraftKuripugalViewModel

    @Inject
    lateinit var appExecutors: AppExecutors

    // mutable for testing
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    var binding by autoCleared<DraftKuripugalFragmentBinding>()

    private var adapter by autoCleared<DraftKuripugalAdapter>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<DraftKuripugalFragmentBinding>(
            inflater,
            R.layout.draft_kuripugal_fragment,
            container,
            false
        )
        dataBinding.retryCallback = object : RetryCallback {
            override fun retry() {
                draftKuripugalViewModel.retry()
            }
        }
        binding = dataBinding
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        draftKuripugalViewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(DraftKuripugalViewModel::class.java)
        draftKuripugalViewModel.setUser("admin")
        binding.setLifecycleOwner(viewLifecycleOwner)
        binding.kurippugal = draftKuripugalViewModel.kuripugal


        val adapter = DraftKuripugalAdapter(dataBindingComponent, appExecutors) { kurippu ->
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

        initDraftKuripugalList(draftKuripugalViewModel)
    }


    private fun initDraftKuripugalList(viewModel: DraftKuripugalViewModel) {
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
