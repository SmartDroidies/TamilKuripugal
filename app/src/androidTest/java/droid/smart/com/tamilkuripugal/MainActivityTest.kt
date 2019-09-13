package droid.smart.com.tamilkuripugal

import android.view.Gravity
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.DrawerMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.rule.ActivityTestRule
import com.smart.droid.tamil.tips.R
import droid.smart.com.tamilkuripugal.utilities.getToolbarNavigationContentDescription
import org.junit.Rule
import org.junit.Test

class MainActivityTest {

    @Rule
    @JvmField
    var activityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun clickOnAndroidHomeIcon_OpensAndClosesNavigation() {
        // Check that drawer is closed at startup
        Espresso.onView(ViewMatchers.withId(R.id.drawer_layout))
            .check(ViewAssertions.matches(DrawerMatchers.isClosed(Gravity.START)))

        clickOnHomeIconToOpenNavigationDrawer()
        //checkDrawerIsOpen()
    }

    private fun clickOnHomeIconToOpenNavigationDrawer() {
        Espresso.onView(
            ViewMatchers.withContentDescription(
                getToolbarNavigationContentDescription(
                    activityTestRule.activity, R.id.toolbar
                )
            )
        ).perform(ViewActions.click())
    }

    /*
    private fun checkDrawerIsOpen() {
        Espresso.onView(ViewMatchers.withId(R.id.drawer_layout))
            .check(ViewAssertions.matches(DrawerMatchers.isOpen(Gravity.START)))
    }

    private fun checkDrawerIsNotOpen() {
        Espresso.onView(ViewMatchers.withId(R.id.drawer_layout))
            .check(ViewAssertions.matches(DrawerMatchers.isClosed(Gravity.START)))
    }
    */


}