package droid.smart.com.tamilkuripugal.ui.main

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
import androidx.recyclerview.widget.GridLayoutManager
import com.smart.droid.tamil.tips.R
import com.smart.droid.tamil.tips.databinding.MainFragmentBinding
import droid.smart.com.tamilkuripugal.AppExecutors
import droid.smart.com.tamilkuripugal.binding.FragmentDataBindingComponent
import droid.smart.com.tamilkuripugal.di.Injectable
import droid.smart.com.tamilkuripugal.testing.OpenForTesting
import droid.smart.com.tamilkuripugal.ui.common.CategoryListAdapter
import droid.smart.com.tamilkuripugal.ui.common.RetryCallback
import droid.smart.com.tamilkuripugal.util.autoCleared
import javax.inject.Inject


@OpenForTesting
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
        binding = dataBinding
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mainViewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(MainViewModel::class.java)
//        val params = UserFragmentArgs.fromBundle(arguments!!)
        mainViewModel.setUser(paramUser)
//        binding.user = userViewModel.user

        binding.categories = mainViewModel.categories
        binding.setLifecycleOwner(viewLifecycleOwner)
        val rvAdapter = CategoryListAdapter(
            dataBindingComponent = dataBindingComponent,
            appExecutors = appExecutors
        ) {category->
            navController().navigate(MainFragmentDirections.kuripugalList(category.category))
        }

        val layoutManager = GridLayoutManager(context, 2)
        binding.categoryList.layoutManager = layoutManager
        binding.categoryList.adapter = rvAdapter
        this.adapter = rvAdapter
        initCategories()
    }


    private fun initCategories() {
        mainViewModel.categories.observe(viewLifecycleOwner, Observer { categories ->
            adapter.submitList(categories?.data)
        })
    }

    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()

}
