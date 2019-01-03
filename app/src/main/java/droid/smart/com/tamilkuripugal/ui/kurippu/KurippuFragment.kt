package droid.smart.com.tamilkuripugal.ui.kurippu

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import droid.smart.com.tamilkuripugal.R

class KurippuFragment : Fragment() {

    companion object {
        fun newInstance() = KurippuFragment()
    }

    private lateinit var viewModel: KurippuViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.kurippu_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(KurippuViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
