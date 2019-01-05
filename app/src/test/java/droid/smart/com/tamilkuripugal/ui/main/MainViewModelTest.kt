package droid.smart.com.tamilkuripugal.ui.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import droid.smart.com.tamilkuripugal.repo.CategoryRepository
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.mock

@RunWith(JUnit4::class)
class MainViewModelTest {

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    private val categoryRepository = mock(CategoryRepository::class.java)
    private val mainViewModel = MainViewModel(categoryRepository)

    @Test
    fun testNull() {
        MatcherAssert.assertThat(mainViewModel.categories, CoreMatchers.notNullValue())
        //Mockito.verify(userRepository, Mockito.never()).loadUser(Mockito.anyString())
        //userViewModel.setLogin("foo")
        //Mockito.verify(userRepository, Mockito.never()).loadUser(Mockito.anyString())
        //FIXME - Check why mocking is not available for Repository
    }

    @Test
    fun getCategories() {
    }
}