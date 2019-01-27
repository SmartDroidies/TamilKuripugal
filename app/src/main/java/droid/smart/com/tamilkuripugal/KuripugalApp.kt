package droid.smart.com.tamilkuripugal

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.text.TextUtils
import android.util.Log
import androidx.annotation.Nullable
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import com.smart.droid.tamil.tips.BuildConfig
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import droid.smart.com.tamilkuripugal.di.AppInjector
import io.fabric.sdk.android.Fabric
import timber.log.Timber
import java.util.*
import javax.inject.Inject


class KuripugalApp : Application(), HasActivityInjector {

    private val defaultLocale = "ta"

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

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




