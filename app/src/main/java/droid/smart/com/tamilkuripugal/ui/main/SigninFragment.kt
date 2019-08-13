package droid.smart.com.tamilkuripugal.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.smart.droid.tamil.tips.R
import droid.smart.com.tamilkuripugal.di.Injectable

class SigninFragment : Fragment(), Injectable {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.signin_fragment, container, false)
    }

}