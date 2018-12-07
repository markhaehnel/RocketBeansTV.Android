package de.markhaehnel.rbtv.rocketbeanstv.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import de.markhaehnel.rbtv.rocketbeanstv.MainActivity

@Suppress("unused")
@Module
abstract class MainActivityModule {
    @ContributesAndroidInjector(modules = [FragmentBuildersModule::class])
    abstract fun contributeMainActivity(): MainActivity
}