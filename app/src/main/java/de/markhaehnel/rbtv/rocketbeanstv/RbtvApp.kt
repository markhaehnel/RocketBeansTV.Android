package de.markhaehnel.rbtv.rocketbeanstv

import android.app.Application
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import de.markhaehnel.rbtv.rocketbeanstv.di.AppInjector
import de.markhaehnel.rbtv.rocketbeanstv.util.DetailDebugTree
import timber.log.Timber
import javax.inject.Inject

class RbtvApp : Application(), HasAndroidInjector {
    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(DetailDebugTree())
        }

        AppInjector.init(this)
    }

    override fun androidInjector() = dispatchingAndroidInjector
}