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
import android.content.Context
import androidx.room.Room
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import droid.smart.com.tamilkuripugal.api.KuripugalService
import droid.smart.com.tamilkuripugal.data.CategoryDao
import droid.smart.com.tamilkuripugal.data.FavouriteDao
import droid.smart.com.tamilkuripugal.data.KurippuDao
import droid.smart.com.tamilkuripugal.data.KuripugalDb
import droid.smart.com.tamilkuripugal.util.LiveDataCallAdapterFactory
import droid.smart.com.tamilkuripugal.util.RateLimiter
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
        val interceptor = HttpLoggingInterceptor { message -> Timber.d(message) }
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
    fun provideAdRequest(/*app: Application*/): AdRequest {
        return AdRequest.Builder()
            .addTestDevice("DC14F1DAAD21C69EF0EE884173C21F66")
            .build()
    }

    @Singleton
    @Provides
    fun provideRateLimiter(): RateLimiter {
        return RateLimiter();
    }

    @Singleton
    @Provides
    fun provideDb(app: Application): KuripugalDb {
        return Room
            .databaseBuilder(app, KuripugalDb::class.java, "kuripugal.db")
            .fallbackToDestructiveMigration()
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

    @Singleton
    @Provides
    fun provideFavouriteDao(db: KuripugalDb): FavouriteDao {
        return db.favouriteDao()
    }

    @Singleton
    @Provides
    fun provideFirebaseAnalytics(application: Application): FirebaseAnalytics {
        return FirebaseAnalytics.getInstance(application)
    }

    @Singleton
    @Provides
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Singleton
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Singleton
    @Provides
    fun provideGoogleSignInOptions(): GoogleSignInOptions {
        return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("781897556091-5a1kh9qra1021rdr5h2btcp1a595s4tu.apps.googleusercontent.com")
            .requestEmail()
            .build()
    }


}
