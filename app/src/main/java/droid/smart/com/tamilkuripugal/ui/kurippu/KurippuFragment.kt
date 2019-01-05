package droid.smart.com.tamilkuripugal.ui.kurippu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.smart.droid.tamil.tips.R


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
