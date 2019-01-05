package droid.smart.com.tamilkuripugal.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import droid.smart.com.tamilkuripugal.R
import droid.smart.com.tamilkuripugal.binding.FragmentBindingAdapters
import droid.smart.com.tamilkuripugal.testing.SingleFragmentActivity
import droid.smart.com.tamilkuripugal.util.CountingAppExecutorsRule
import droid.smart.com.tamilkuripugal.util.DataBindingIdlingResourceRule
import droid.smart.com.tamilkuripugal.util.TaskExecutorWithIdlingResourceRule
import droid.smart.com.tamilkuripugal.util.mock
import droid.smart.com.tamilkuripugal.vo.Category
import droid.smart.com.tamilkuripugal.vo.Resource
import org.hamcrest.CoreMatchers
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainFragmentTest {

    @Rule
    @JvmField
    val activityRule = ActivityTestRule(SingleFragmentActivity::class.java, true, true)
    @Rule
    @JvmField
    val executorRule = TaskExecutorWithIdlingResourceRule()
    @Rule
    @JvmField
    val countingAppExecutors = CountingAppExecutorsRule()
    @Rule
    @JvmField
    val dataBindingIdlingResourceRule = DataBindingIdlingResourceRule(activityRule)

    private lateinit var viewModel: MainViewModel
    private lateinit var mockBindingAdapter: FragmentBindingAdapters
    //private val userData = MutableLiveData<Resource<User>>()
    private val categoryListData = MutableLiveData<Resource<List<Category>>>()
    private val testFragment = TestMainFragment().apply {
        //arguments = UserFragmentArgs.Builder("foo").build().toBundle()
    }


    @Test
    fun loading() {
        categoryListData.postValue(Resource.loading(null))
        Espresso.onView(ViewMatchers.withId(R.id.progress_bar))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.retry))
            .check(ViewAssertions.matches(CoreMatchers.not(ViewMatchers.isDisplayed())))
    }

    class TestMainFragment : MainFragment() {
        val navController = mock<NavController>()
        override fun navController() = navController
    }

}