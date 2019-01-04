package droid.smart.com.tamilkuripugal.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import droid.smart.com.tamilkuripugal.testing.OpenForTesting
import droid.smart.com.tamilkuripugal.vo.Category

@Dao
@OpenForTesting
abstract class CategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(vararg categories: Category)

    @Query("SELECT * FROM category WHERE code = :code")
    abstract fun load(code: String): LiveData<Category>


    @Query("SELECT * FROM category ORDER BY `order` DESC")
    abstract fun loadCategories(): LiveData<List<Category>>

}