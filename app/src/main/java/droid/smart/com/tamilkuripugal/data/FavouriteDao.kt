package droid.smart.com.tamilkuripugal.data

import androidx.lifecycle.LiveData
import androidx.room.*
import droid.smart.com.tamilkuripugal.vo.Favourite
import droid.smart.com.tamilkuripugal.vo.FavouriteKurippu

@Dao
abstract class FavouriteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(favourite: Favourite)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract fun update(favourite: Favourite)

    @Query("SELECT * FROM favourite WHERE kurippu_id = :kurippuId")
    abstract fun loadById(kurippuId: String): Favourite?

    @Query("SELECT * FROM favourite WHERE kurippu_id = :kurippuId and active = 1")
    abstract fun loadFavourite(kurippuId: String): LiveData<Favourite>

    /*
        @Query("SELECT favourite.kurippu_id, kurippu.title, kurippu.image,kurippu.category,kurippu.postDate,favourite.updatedDate,kurippu.status,kurippu.content " +
                " FROM favourite " +
                " LEFT JOIN kurippu on favourite.kurippu_id = kurippu.kurippu_Id " +
                " WHERE favourite.active = 1 " +
                " order by favourite.updatedDate desc")
    */
    @Query(
        "SELECT favourite.kurippu_id as kurippuId, favourite.updatedDate, favourite.active, favourite.cloudStatus, kurippu.title, kurippu.image " +
                "FROM favourite " +
                "LEFT JOIN kurippu on favourite.kurippu_id = kurippu.kurippu_Id " +
                "WHERE active = 1 " +
                "order by favourite.updatedDate desc"
    )
    abstract fun loadFavourites(): LiveData<List<FavouriteKurippu>>

}