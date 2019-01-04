package droid.smart.com.tamilkuripugal.data

import androidx.room.Database
import androidx.room.RoomDatabase
import droid.smart.com.tamilkuripugal.vo.Category
import droid.smart.com.tamilkuripugal.vo.Kurippu

@Database(
    entities = [
        Category::class,
        Kurippu::class],
    version = 1,
    exportSchema = false
)

abstract class KuripugalDb : RoomDatabase() {

    abstract fun categoryDao(): CategoryDao

    abstract fun KurippuDao(): KurippuDao

}