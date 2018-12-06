package de.markhaehnel.rbtv.rocketbeanstv.di

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import de.markhaehnel.rbtv.rocketbeanstv.api.RbtvService
import de.markhaehnel.rbtv.rocketbeanstv.db.RbtvDb
import de.markhaehnel.rbtv.rocketbeanstv.db.StreamDao
import de.markhaehnel.rbtv.rocketbeanstv.util.LiveDataCallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module(includes = [ViewModelModule::class])
class AppModule {
    @Singleton
    @Provides
    fun provideRbtvService(): RbtvService {
        return Retrofit.Builder()
            .baseUrl("https://rbtvapi-production.server.ezhub.de/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .build()
            .create(RbtvService::class.java)
    }

    @Singleton
    @Provides
    fun provideDb(app: Application): RbtvDb {
        return Room
            .databaseBuilder(app, RbtvDb::class.java, "rbtv.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideStreamDao(db: RbtvDb): StreamDao {
        return db.streamDao()
    }
}