package droid.smart.com.tamilkuripugal.repo

import com.smart.droid.tamil.tips.R
import droid.smart.com.tamilkuripugal.vo.Category
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepository @Inject constructor() {

    //FIXME - This needs to be removed
    companion object {
        val CATEGORY_DATA = listOf(
            Category("CTGRY01", "HEALTH", "Health Tips", 1, 5, null, R.drawable.arokiyam, "native-health"),
            Category("CTGRY02", "BEAUTY", "Beauty Tips", 2, 6, null, R.drawable.azagu, "native-beauty"),
            Category("CTGRY03", "TREATMENT", "Home Remedies", 3, 10, null, R.drawable.naattu, "native-home-remedies"),
            Category("CTGRY04", "COOKING", "Cooking Tips", 4, 3, null, R.drawable.samayal, "native-cooking"),
            Category("CTGRY05", "VETTU", "House Tips", 5, 1096, null, R.drawable.vettu, "native-house-keeping"),
            Category("CTGRY06", "MARUTHUVAM", "Medical Tips", 6, 1095, null, R.drawable.maruthuvam, "native-medicine"),
            Category("CTGRY07", "AANMEEGAM", "Divine Tips", 7, 7236, null, R.drawable.aanmeega, "native-aanmeegam"),
            Category("CTGRY08", "GENERAL", "General Tips", 8, 2920, null, R.drawable.general, "native-general-tip"),
            Category("CTGRY09","AGRICULTURE","Agricultrue Tips",9,9479,null,R.drawable.agriculture,"native-agriculture")
        )
    }

    fun loadCategories(): List<Category>? {
        return CATEGORY_DATA
    }

    fun findCategory(categoryId: Int): Category {
        return CATEGORY_DATA.get(0)
    }



    /*
    fun loadCategories(): LiveData<Resource<List<Category>>> {
        return object : NetworkBoundResource<List<Category>, List<Category>>(appExecutors) {
            override fun saveCallResult(item: List<Category>) {
                categoryDao.insertCategories(item)
            }
            
            override fun shouldFetch(data: List<Category>?): Boolean {
                return data == null || data.isEmpty() /*|| repoListRateLimit.shouldFetch("category")*/
            }

            override fun loadFromDb() = categoryDao.loadCategories()

            override fun createCall() = kuripugalService.getCategories()
        }.asLiveData()
    }
    */

}