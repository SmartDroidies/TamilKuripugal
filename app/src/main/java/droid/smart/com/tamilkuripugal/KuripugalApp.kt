package droid.smart.com.tamilkuripugal

import android.app.Application
import android.util.Log
import androidx.annotation.Nullable
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import com.smart.droid.tamil.tips.BuildConfig
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import droid.smart.com.tamilkuripugal.di.AppInjector
import droid.smart.com.tamilkuripugal.extensions.setDefaultLocale
import io.fabric.sdk.android.Fabric
import timber.log.Timber
import javax.inject.Inject


class KuripugalApp : Application(), HasAndroidInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>

    override fun onCreate() {
        super.onCreate()

        val core = CrashlyticsCore.Builder()
            .disabled(BuildConfig.DEBUG)
            .build()
        Fabric.with(this, Crashlytics.Builder().core(core).build())

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        Timber.plant(CrashlyticsTree())
        AppInjector.init(this)
        this.setDefaultLocale()
    }

    override fun androidInjector(): AndroidInjector<Any> {
        return dispatchingAndroidInjector
    }

    inner class CrashlyticsTree : Timber.Tree() {

        private val CRASHLYTICS_KEY_PRIORITY = "priority"
        private val CRASHLYTICS_KEY_TAG = "tag"
        private val CRASHLYTICS_KEY_MESSAGE = "message"

        override fun log(priority: Int, @Nullable tag: String?, @Nullable message: String, @Nullable t: Throwable?) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG || priority == Log.INFO) {
                return
            }

            Crashlytics.setInt(CRASHLYTICS_KEY_PRIORITY, priority)
            Crashlytics.setString(CRASHLYTICS_KEY_TAG, tag)
            Crashlytics.setString(CRASHLYTICS_KEY_MESSAGE, message)

            if (t == null) {
                Crashlytics.logException(Exception(message))
            } else {
                Crashlytics.logException(t)
            }
        }
    }
}




