package droid.smart.com.tamilkuripugal.ui.kuripugal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.smart.droid.tamil.tips.R


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
