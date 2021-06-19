package de.markhaehnel.rbtv.rocketbeanstv.di

import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import de.markhaehnel.rbtv.rocketbeanstv.BuildConfig
import de.markhaehnel.rbtv.rocketbeanstv.api.*
import de.markhaehnel.rbtv.rocketbeanstv.repository.ChatRepository
import de.markhaehnel.rbtv.rocketbeanstv.util.LiveDataCallAdapterFactory
import de.markhaehnel.rbtv.rocketbeanstv.util.UserAgentInterceptor
import okhttp3.OkHttpClient
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
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
            .create()

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(UserAgentInterceptor("RocketBeansTV.Android", BuildConfig.VERSION_NAME))
            .build()

        return Retrofit.Builder()
            .baseUrl("https://api.rocketbeans.tv/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .client(okHttpClient)
            .build()
            .create(RbtvService::class.java)
    }

    @Singleton
    @Provides
    fun provideTwitchGraphQLService(): TwitchGraphQLService {
        return Retrofit.Builder()
            .baseUrl("https://gql.twitch.tv/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .build()
            .create(TwitchGraphQLService::class.java)
    }

    @Singleton
    @Provides
    fun provideTwitchUsherService(): TwitchUsherService {
        return Retrofit.Builder()
            .baseUrl("https://usher.ttvnw.net/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .build()
            .create(TwitchUsherService::class.java)
    }

   @Singleton
   @Provides
   fun provideChatRepository(): ChatRepository {
       return ChatRepository()
   }
}