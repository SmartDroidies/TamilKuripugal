package droid.smart.com.tamilkuripugal.repo

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import droid.smart.com.tamilkuripugal.api.KuripugalService
import droid.smart.com.tamilkuripugal.data.CategoryDao
import droid.smart.com.tamilkuripugal.util.ApiUtil.successCall
import droid.smart.com.tamilkuripugal.util.InstantAppExecutors
import droid.smart.com.tamilkuripugal.util.TestUtil
import droid.smart.com.tamilkuripugal.util.argumentCaptor
import droid.smart.com.tamilkuripugal.util.mock
import droid.smart.com.tamilkuripugal.vo.Category
import droid.smart.com.tamilkuripugal.vo.Resource
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.*

@RunWith(JUnit4::class)
class CategoryRepositoryTest {

    private lateinit var repository: CategoryRepository
    private val dao = mock(CategoryDao::class.java)
    private val service = mock(KuripugalService::class.java)

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun init() {
        repository = CategoryRepository(InstantAppExecutors(), dao, service)
    }

    @Test
    fun loadCategories() {
        val dbData = MutableLiveData<List<Category>>()
        `when`(dao.loadCategories()).thenReturn(dbData)

        val data = repository.loadCategories()
        verify(dao).loadCategories()

        verify(service, never()).getCategories()

        val category = TestUtil.createCategory("1", "BEAUTY", "Beauty Tips")
        val categories = listOf(category)
        val call = successCall(categories)
        `when`(service.getCategories()).thenReturn(call)

        val observer = mock<Observer<Resource<List<Category>>>>()
        data.observeForever(observer)

        verify(observer).onChanged(Resource.loading(null))

        val updatedDbData = MutableLiveData<List<Category>>()
        `when`(dao.loadCategories()).thenReturn(updatedDbData)
        dbData.value = emptyList()


        verify(service).getCategories()
        val inserted = argumentCaptor<List<Category>>()
        // empty list is a workaround for null capture return
        verify(dao).insertCategories(inserted.capture() ?: emptyList())


        MatcherAssert.assertThat(inserted.value.size, CoreMatchers.`is`(1))
        val first = inserted.value[0]
        //MatcherAssert.assertThat(first.repoName, CoreMatchers.`is`("bar"))
        //MatcherAssert.assertThat(first.repoOwner, CoreMatchers.`is`("foo"))

        updatedDbData.value = categories
        verify(observer).onChanged(Resource.success(categories))

    }
}