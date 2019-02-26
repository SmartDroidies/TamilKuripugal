package droid.smart.com.tamilkuripugal.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import droid.smart.com.tamilkuripugal.vo.Kurippu

@Dao
abstract class KurippuDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(kurippu: Kurippu)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertKuripugal(item: List<Kurippu>)

    @Query("SELECT * FROM kurippu WHERE category = :categoryId and status = 'publish' ORDER BY updatedDate DESC limit 500")
    abstract fun loadKuripugal(categoryId: Int): LiveData<List<Kurippu>>

    @Query("SELECT * FROM kurippu WHERE status = 'publish' ORDER BY updatedDate desc limit 200")
    abstract fun loadNewKuripugal(): LiveData<List<Kurippu>>

    @Query("SELECT * FROM kurippu WHERE status != 'publish' ORDER BY postDate asc limit 200")
    abstract fun loadScheduledKuripugal(): LiveData<List<Kurippu>>

    @Query("SELECT * FROM kurippu WHERE kurippu_id = :kurippuId")
    abstract fun loadKurippu(kurippuId: String): LiveData<Kurippu>

    @Query("DELETE FROM kurippu WHERE status != 'publish'")
    abstract fun clearDraft()

}