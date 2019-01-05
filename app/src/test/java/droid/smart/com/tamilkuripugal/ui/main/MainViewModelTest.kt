package droid.smart.com.tamilkuripugal.ui.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import droid.smart.com.tamilkuripugal.repo.CategoryRepository
import droid.smart.com.tamilkuripugal.util.mock
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.*

@RunWith(JUnit4::class)
class MainViewModelTest {

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    private val categoryRepository = mock(CategoryRepository::class.java)
    private val mainViewModel = MainViewModel(categoryRepository)

    @Test
    fun testNull() {
        assertThat(mainViewModel.categories, CoreMatchers.notNullValue())
        verify(categoryRepository, never()).loadCategories()
        mainViewModel.setUser("sakthi")
        verify(categoryRepository, never()).loadCategories()
    }

    @Test
    fun loadCategories() {
        mainViewModel.categories.observeForever(mock())
        verifyNoMoreInteractions(categoryRepository)
        mainViewModel.setUser("sakthi")
        verify(categoryRepository).loadCategories()
        reset(categoryRepository)
        mainViewModel.setUser("meenakshi")
        verify(categoryRepository).loadCategories()
        verifyNoMoreInteractions(categoryRepository)
    }

    //FIXME - Send Results to UI
    //FIXME - Retury
    //FIXME - Null Category List
    //FIXME - Dont Refresh on Same Data
    //FIXME - No Retry without user
}