package droid.smart.com.tamilkuripugal.ui.newkuripugal

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
import com.google.firebase.analytics.FirebaseAnalytics
import com.smart.droid.tamil.tips.R
import com.smart.droid.tamil.tips.databinding.NewKuripugalFragmentBinding
import droid.smart.com.tamilkuripugal.AppExecutors
import droid.smart.com.tamilkuripugal.binding.FragmentDataBindingComponent
import droid.smart.com.tamilkuripugal.di.Injectable
import droid.smart.com.tamilkuripugal.ui.common.DividerItemDecoration
import droid.smart.com.tamilkuripugal.ui.common.RetryCallback
import droid.smart.com.tamilkuripugal.ui.kuripugal.KuripugalFragmentDirections
import droid.smart.com.tamilkuripugal.util.autoCleared
import java.util.*
import javax.inject.Inject

class NewKuripugalFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var newKuripugalViewModel: NewKuripugalViewModel

    @Inject
    lateinit var appExecutors: AppExecutors

    // mutable for testing
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    var binding by autoCleared<NewKuripugalFragmentBinding>()

    private var adapter by autoCleared<NewKuripugalAdapter>()

    @Inject
    lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<NewKuripugalFragmentBinding>(
            inflater,
            R.layout.new_kuripugal_fragment,
            container,
            false
        )
        dataBinding.retryCallback = object : RetryCallback {
            override fun retry() {
                newKuripugalViewModel.retry()
            }
        }
        binding = dataBinding
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        newKuripugalViewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(NewKuripugalViewModel::class.java)
        newKuripugalViewModel.setLastViewed(Date().time) //FIXME - Collect time for shared preferences
        binding.lifecycleOwner = viewLifecycleOwner
        binding.kurippugal = newKuripugalViewModel.kuripugal


        val adapter = NewKuripugalAdapter(
            dataBindingComponent,
            appExecutors
        ) { kurippu ->
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

        initKuripugalList(newKuripugalViewModel)

        firebaseAnalytics.setCurrentScreen(activity!!, this.javaClass.simpleName, this.javaClass.simpleName)

    }


    private fun initKuripugalList(viewModel: NewKuripugalViewModel) {
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
