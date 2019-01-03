package droid.smart.com.tamilkuripugal.ui.kuripugal

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import droid.smart.com.tamilkuripugal.R


class KuripugalFragment : Fragment() {

    companion object {
        fun newInstance() = KuripugalFragment()
    }

    private lateinit var viewModel: KuripugalViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.kuripugal_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(KuripugalViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
