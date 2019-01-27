package droid.smart.com.tamilkuripugal

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.text.TextUtils
import com.smart.droid.tamil.tips.BuildConfig
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import droid.smart.com.tamilkuripugal.di.AppInjector
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class KuripugalApp : Application(), HasActivityInjector {

    private val defaultLocale = "ta"

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            //FIXME Create a Crashanalytics tree for Timber
        }
        AppInjector.init(this)
        setDefaultLocale(this)
    }

    private fun setDefaultLocale(context: Context) {
        val configuration = Configuration()
        if (!TextUtils.isEmpty(defaultLocale))
            configuration.locale = Locale(defaultLocale)
        else
            configuration.locale = Locale.getDefault();
        context.getResources().updateConfiguration(configuration, null);
    }

    override fun activityInjector() = dispatchingAndroidInjector

}