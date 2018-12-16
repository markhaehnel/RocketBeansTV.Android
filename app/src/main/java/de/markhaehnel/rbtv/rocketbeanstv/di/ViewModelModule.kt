package de.markhaehnel.rbtv.rocketbeanstv.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import de.markhaehnel.rbtv.rocketbeanstv.viewmodel.RbtvViewModelFactory
import de.markhaehnel.rbtv.rocketbeanstv.ui.player.PlayerViewModel
import de.markhaehnel.rbtv.rocketbeanstv.ui.startup.StartupViewModel

@Suppress("unused")
@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(StartupViewModel::class)
    abstract fun bindStartupViewModel(userViewModel: StartupViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PlayerViewModel::class)
    abstract fun bindPlayerViewModel(userViewModel: PlayerViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: RbtvViewModelFactory): ViewModelProvider.Factory
}