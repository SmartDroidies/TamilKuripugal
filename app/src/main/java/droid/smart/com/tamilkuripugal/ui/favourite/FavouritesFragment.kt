package droid.smart.com.tamilkuripugal.ui.favourite

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
import com.google.firebase.auth.FirebaseAuth
import com.smart.droid.tamil.tips.R
import com.smart.droid.tamil.tips.databinding.FavouritesFragmentBinding
import droid.smart.com.tamilkuripugal.AppExecutors
import droid.smart.com.tamilkuripugal.binding.FragmentDataBindingComponent
import droid.smart.com.tamilkuripugal.di.Injectable
import droid.smart.com.tamilkuripugal.ui.common.DividerItemDecoration
import droid.smart.com.tamilkuripugal.ui.kuripugal.KuripugalFragmentDirections
import droid.smart.com.tamilkuripugal.util.autoCleared
import timber.log.Timber
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

    @Inject
    lateinit var auth: FirebaseAuth

    private var adapter by autoCleared<FavouritesAdapter>()

    @Inject
    lateinit var firebaseAnalytics: FirebaseAnalytics

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        favouritesViewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(FavouritesViewModel::class.java)

        val firebaseUserId = auth.currentUser?.uid
        Timber.i("Display favourite kuripugal for : %s ", firebaseUserId)
        firebaseUserId?.let { favouritesViewModel.setUserId(it) }
        binding.lifecycleOwner = viewLifecycleOwner
        binding.kurippugal = favouritesViewModel.kuripugal

        val adapter = FavouritesAdapter(
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


        initKuripugalList(favouritesViewModel)

        firebaseAnalytics.setCurrentScreen(activity!!, this.javaClass.simpleName, this.javaClass.simpleName)

    }

    private fun initKuripugalList(favouritesViewModel: FavouritesViewModel) {
        favouritesViewModel.kuripugal.observe(viewLifecycleOwner, Observer { listResource ->
            Timber.i("Favourite list resource : %s", listResource)
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