package droid.smart.com.tamilkuripugal.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import droid.smart.com.tamilkuripugal.vo.Favourite

@Dao
abstract class FavouriteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(favourite: Favourite)

    //TODO - Check for active flag also
    @Query("SELECT * FROM favourite WHERE kurippu_id = :kurippuId")
    abstract fun loadFavourite(kurippuId: String): LiveData<Favourite>

}