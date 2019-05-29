package droid.smart.com.tamilkuripugal.data

import androidx.room.Database
import androidx.room.RoomDatabase
import droid.smart.com.tamilkuripugal.vo.Category
import droid.smart.com.tamilkuripugal.vo.Favourite
import droid.smart.com.tamilkuripugal.vo.Kurippu

@Database(
    entities = [
        Category::class,
        Kurippu::class,
        Favourite::class],
        version = 2,
        exportSchema = true
)

abstract class KuripugalDb : RoomDatabase() {

    abstract fun categoryDao(): CategoryDao

    abstract fun kurippuDao(): KurippuDao

    abstract fun favouriteDao(): FavouriteDao

}