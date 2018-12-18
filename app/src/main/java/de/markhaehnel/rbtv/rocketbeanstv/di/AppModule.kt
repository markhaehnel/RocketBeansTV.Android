package de.markhaehnel.rbtv.rocketbeanstv.di

import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import de.markhaehnel.rbtv.rocketbeanstv.api.RbtvService
import de.markhaehnel.rbtv.rocketbeanstv.api.YouTubeService
import de.markhaehnel.rbtv.rocketbeanstv.util.LiveDataCallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Singleton

@Module(includes = [ViewModelModule::class])
class AppModule {
    @Singleton
    @Provides
    fun provideRbtvService(): RbtvService {
        val gson = GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ssX")
            .create()

        return Retrofit.Builder()
            .baseUrl("https://rbtvapi-production.server.ezhub.de/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .build()
            .create(RbtvService::class.java)
    }

    @Singleton
    @Provides
    fun provideYouTubeService(): YouTubeService {
        return Retrofit.Builder()
            .baseUrl("https://www.youtube.com/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .build()
            .create(YouTubeService::class.java)
    }

}