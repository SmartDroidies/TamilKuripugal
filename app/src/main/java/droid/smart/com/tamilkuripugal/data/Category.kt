package droid.smart.com.tamilkuripugal.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "category")
data class Category(
    @PrimaryKey @ColumnInfo(name = "id") val plantId: String,
    val code: String,
    val description: String,
    val order: Int,
    val category: Int,
    val image: String,
    val defaultImage: String,
    val topic: String
) {
    override fun toString() = code
}