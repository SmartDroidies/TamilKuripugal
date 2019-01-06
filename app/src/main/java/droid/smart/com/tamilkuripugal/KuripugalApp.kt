package droid.smart.com.tamilkuripugal

import android.app.Activity
import android.app.Application
import com.smart.droid.tamil.tips.BuildConfig
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import droid.smart.com.tamilkuripugal.di.AppInjector
import timber.log.Timber
import javax.inject.Inject

class KuripugalApp : Application(), HasActivityInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        AppInjector.init(this)
    }

    override fun activityInjector() = dispatchingAndroidInjector

}