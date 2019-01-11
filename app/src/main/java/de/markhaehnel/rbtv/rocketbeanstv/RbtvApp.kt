package de.markhaehnel.rbtv.rocketbeanstv

import android.app.Activity
import android.app.Application
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import de.markhaehnel.rbtv.rocketbeanstv.di.AppInjector
import de.markhaehnel.rbtv.rocketbeanstv.util.DetailDebugTree
import timber.log.Timber
import javax.inject.Inject

class RbtvApp : Application(), HasActivityInjector {
    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(DetailDebugTree())
        }

        AppInjector.init(this)
    }

    override fun activityInjector() = dispatchingAndroidInjector
}