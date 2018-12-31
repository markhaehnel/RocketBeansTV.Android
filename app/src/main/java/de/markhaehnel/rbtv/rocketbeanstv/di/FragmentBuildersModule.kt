package de.markhaehnel.rbtv.rocketbeanstv.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import de.markhaehnel.rbtv.rocketbeanstv.ui.player.PlayerFragment
import de.markhaehnel.rbtv.rocketbeanstv.ui.schedule.ScheduleFragment
import de.markhaehnel.rbtv.rocketbeanstv.ui.startup.StartupFragment

@Suppress("unused")
@Module
abstract class FragmentBuildersModule {
    @ContributesAndroidInjector
    abstract fun contributeStartupFragment(): StartupFragment
    @ContributesAndroidInjector
    abstract fun contributePlayerFragment(): PlayerFragment
    @ContributesAndroidInjector
    abstract fun contributeScheduleFragment(): ScheduleFragment
}