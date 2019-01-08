/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package droid.smart.com.tamilkuripugal.di

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import droid.smart.com.tamilkuripugal.api.KuripugalService
import droid.smart.com.tamilkuripugal.data.CategoryDao
import droid.smart.com.tamilkuripugal.data.KurippuDao
import droid.smart.com.tamilkuripugal.data.KuripugalDb
import droid.smart.com.tamilkuripugal.util.LiveDataCallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import javax.inject.Singleton

@Module(includes = [ViewModelModule::class])
class AppModule {

    @Singleton
    @Provides
    fun provideOkhttpClient(): OkHttpClient {

        val interceptor =  HttpLoggingInterceptor {message -> Timber.d(message) }
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()
    }

    @Singleton
    @Provides
    fun provideKuripugalService(okHttpClient: OkHttpClient): KuripugalService {
        return Retrofit.Builder()
            .baseUrl("http://tamil.tips2stayhealthy.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .client(okHttpClient)
            .build()
            .create(KuripugalService::class.java)
    }


    @Singleton
    @Provides
    fun provideDb(app: Application): KuripugalDb {
        return Room
            .databaseBuilder(app, KuripugalDb::class.java, "kuripugal.db")
            .fallbackToDestructiveMigration()
            //FIXME - Pre Populate Data here
            //.addCallback(PrePopulateData(app.applicationContext))
//            .addCallback(object : RoomDatabase.Callback() {
//                override fun onCreate(db: SupportSQLiteDatabase) {
//                    super.onCreate(db)
//                    ioThread {
//                        prepopulate(app)
//
//                    }
//                    //db.categoryDao()
//                }
//            })
            .build()
    }


    @Singleton
    @Provides
    fun provideCategoryDao(db: KuripugalDb): CategoryDao {
        return db.categoryDao()
    }

    @Singleton
    @Provides
    fun provideKurippuDao(db: KuripugalDb): KurippuDao {
        return db.kurippuDao()
    }

}
