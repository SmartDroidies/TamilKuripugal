package droid.smart.com.tamilkuripugal.vo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "kurippu")
data class Kurippu(
    @PrimaryKey @ColumnInfo(name = "kurippu_id") val kurippuId: String,
    val title: String,
    val category: Int,
    val postDate: Long,
    val updatedDate: Long,
    val status: String,
    val content: String
) {
}