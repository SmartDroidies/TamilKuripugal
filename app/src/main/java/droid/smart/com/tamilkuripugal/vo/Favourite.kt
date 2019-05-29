package droid.smart.com.tamilkuripugal.vo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favourite")
data class Favourite (

    @PrimaryKey @ColumnInfo(name = "kurippu_id") val kurippuId: String,
    val updatedDate: Long,
    val active: Boolean,
    val cloudStatus: String
)