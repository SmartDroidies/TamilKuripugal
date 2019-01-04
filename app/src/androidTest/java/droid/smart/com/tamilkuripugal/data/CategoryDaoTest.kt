package droid.smart.com.tamilkuripugal.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.runner.AndroidJUnit4
import droid.smart.com.tamilkuripugal.util.LiveDataTestUtil.getValue
import droid.smart.com.tamilkuripugal.util.TestUtil
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CategoryDaoTest : DbTest() {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun insertAndReadAll() {
        val category = TestUtil.createCategory("1", "BEAUTY", "Beauty Tips");
        db.categoryDao().insert(category)

        val loaded = getValue(db.categoryDao().load("BEAUTY"))
        MatcherAssert.assertThat(loaded, CoreMatchers.notNullValue())
        MatcherAssert.assertThat(loaded.categoryId, CoreMatchers.`is`("1"))
        MatcherAssert.assertThat(loaded.description, CoreMatchers.`is`("Beauty Tips"))
        MatcherAssert.assertThat(loaded.image, CoreMatchers.notNullValue())
        MatcherAssert.assertThat(loaded.category, CoreMatchers.`is`(22))
    }

}