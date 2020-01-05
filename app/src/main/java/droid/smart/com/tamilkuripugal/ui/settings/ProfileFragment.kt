package droid.smart.com.tamilkuripugal.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.smart.droid.tamil.tips.R
import com.smart.droid.tamil.tips.databinding.MainFragmentBinding
import com.smart.droid.tamil.tips.databinding.ProfileFragmentBinding
import droid.smart.com.tamilkuripugal.binding.FragmentDataBindingComponent
import droid.smart.com.tamilkuripugal.di.Injectable
import droid.smart.com.tamilkuripugal.util.autoCleared

class ProfileFragment : Fragment(), Injectable {

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    var binding by autoCleared<MainFragmentBinding>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val dataBinding = DataBindingUtil.inflate<ProfileFragmentBinding>(
            inflater,
            R.layout.profile_fragment,
            container,
            false,
            dataBindingComponent
        )

        /*
        dataBinding.retryCallback = object : RetryCallback {
            override fun retry() {
                mainViewModel.retry()
            }
        }

        dataBinding.btnDraftKuripugal.setOnClickListener {
            this.navController().navigate(
                MainFragmentDirections.draftKuripugal()
            )
        }

        //Authorize with google
        processSignIn()
        */

        //binding = dataBinding
        return dataBinding.root

    }
}