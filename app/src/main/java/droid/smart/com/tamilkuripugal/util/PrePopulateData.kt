package droid.smart.com.tamilkuripugal.util

import android.content.Context
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.smart.droid.tamil.tips.R
import droid.smart.com.tamilkuripugal.data.CategoryDao
import droid.smart.com.tamilkuripugal.data.KuripugalDb
import droid.smart.com.tamilkuripugal.vo.Category
import timber.log.Timber
import javax.inject.Inject

//class PrePopulateData @Inject constructor(private val categoryDao: CategoryDao) : RoomDatabase.Callback() {
class PrePopulateData(applicationContext: Context) : RoomDatabase.Callback() {

    @Inject
    lateinit var categoryDao: CategoryDao

    @Volatile
    private var INSTANCE: KuripugalDb? = null

    val PREPOPULATE_DATA = listOf(
        Category("CTGRY01", "HEALTH", "Health Tips", 1, 5, null, R.drawable.arokiyam, "health"),
        Category("CTGRY02", "BEAUTY", "Beauty Tips", 2, 6, null, R.drawable.azagu, "beauty")
    )

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        Timber.i("Prepopulate Data")
        categoryDao.insertCategories(PREPOPULATE_DATA)
    }

//    var categories = [
//        {"code": "HEALTH", "id": "5", "label": "pirivu.health", "order": 3, "color" : "blue", "imageUrl": "images/arokiyam.png", "data" : "files/health.json", "topic" : "health"},
//        {"code": "BEAUTY", "id": "6", "label": "pirivu.beauty", "order": 4, "color" : "pink", "imageUrl": "images/azagu.png", "data" : "files/beauty.json", "topic" : "beauty"},
//        {"code": "TREATMENT", "id": "10", "label": "pirivu.treatment", "order": 5, "color" : "purple", "imageUrl": "images/naattu.png", "data" : "files/treatment.json", "topic" : "home-remedies"},
//        {"code": "COOKING", "id": "3", "label": "pirivu.cooking", "order": 6, "color" : "brown", "imageUrl": "images/samayal.png", "data" : "files/cooking.json", "topic" : "cooking"},
//        {"code": "VETTU", "id": "1096", "label": "pirivu.vettu", "order": 7, "color" : "blue-grey", "imageUrl": "images/vettu.png", "data" : "files/vettu.json", "topic" : "house-keeping"},
//        {"code": "MARUTHUVAM", "id": "1095", "label": "pirivu.maruthuvam", "order": 8, "color" : "red", "imageUrl": "images/maruthuvam.png", "data" : "files/maruthuvam.json", "topic" : "medicine"},
//        {"code": "AANMEEGAM", "id": "7236", "label": "pirivu.aanmeegam", "order": 2, "color" : "yellow", "imageUrl": "images/aanmeega.png", "data" : "files/aanmeegam.json", "topic" : "aanmeegam"},
//        {"code": "GENERAL", "id": "2920", "label": "pirivu.general", "order": 9, "color" : "orange", "imageUrl": "images/general.png", "data" : "files/general.json", "topic" : "general-tip"},
//        {"code": "AGRICULTURE", "id": "9479", "label": "pirivu.agriculture", "order": 10, "color" : "green", "imageUrl": "images/agriculture.gif", "data" : "files/agriculture.json", "topic" : "general-tip"}];


}
