package droid.smart.com.tamilkuripugal.ui.main

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.smart.droid.tamil.tips.BuildConfig
import com.smart.droid.tamil.tips.R
import com.smart.droid.tamil.tips.databinding.MainFragmentBinding
import droid.smart.com.tamilkuripugal.AppExecutors
import droid.smart.com.tamilkuripugal.binding.FragmentDataBindingComponent
import droid.smart.com.tamilkuripugal.di.Injectable
import droid.smart.com.tamilkuripugal.ui.common.CategoryListAdapter
import droid.smart.com.tamilkuripugal.ui.common.RetryCallback
import droid.smart.com.tamilkuripugal.util.autoCleared
import timber.log.Timber
import javax.inject.Inject


class MainFragment : Fragment(), Injectable {

    val paramUser = "user"

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var appExecutors: AppExecutors

    var binding by autoCleared<MainFragmentBinding>()
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    private lateinit var mainViewModel: MainViewModel
    private var adapter by autoCleared<CategoryListAdapter>()

    @Inject
    lateinit var adRequest: AdRequest

    lateinit var mAdView: AdView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        val dataBinding = DataBindingUtil.inflate<MainFragmentBinding>(
            inflater,
            R.layout.main_fragment,
            container,
            false,
            dataBindingComponent
        )
        dataBinding.retryCallback = object : RetryCallback {
            override fun retry() {
                mainViewModel.retry()
            }
        }

        dataBinding.btnDraftKuripugal.setOnClickListener {
            navController().navigate(
                MainFragmentDirections.draftKuripugal()
            )
        }

        if (BuildConfig.DEBUG) {
            Timber.i("Test Device - Display test device controls")
            //dataBinding.btnDraftKuripugal.visibility = View.VISIBLE
        }

        binding = dataBinding
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mainViewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(MainViewModel::class.java)
        mainViewModel.setUser(paramUser)

        binding.categories = mainViewModel.categories
        binding.setLifecycleOwner(viewLifecycleOwner)
        val rvAdapter = CategoryListAdapter(
            dataBindingComponent = dataBindingComponent,
            appExecutors = appExecutors
        ) { category ->
            navController().navigate(MainFragmentDirections.kuripugalList(category.category))
        }

        val layoutManager = GridLayoutManager(context, 3)
        binding.categoryList.layoutManager = layoutManager
        binding.categoryList.adapter = rvAdapter
        this.adapter = rvAdapter
        initCategories()

        mAdView = binding.adView
        mAdView.loadAd(adRequest)

    }


    private fun initCategories() {
        mainViewModel.categories.observe(viewLifecycleOwner, Observer { categories ->
            adapter.submitList(categories?.data)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater)
        menuInflater.inflate(R.menu.overflow_menu, menu)
    }


    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()

}




