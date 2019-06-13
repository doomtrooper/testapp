package com.anandp.application.di

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.room.Room
import com.anandp.application.App
import com.anandp.application.api.NewsApiService
import com.anandp.application.persistance.NewsDao
import com.anandp.application.persistance.NewsDatabase
import com.anandp.persistence.Config
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.squareup.picasso.Picasso
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
class AppModule {

    @Provides
    @Singleton
    fun context(app: App): Context = app.applicationContext

    @Provides
    @Singleton
    fun providesConfig() = Config(1)


    @Provides
    @Singleton
    fun providesSharedPreferences(application: App): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(application)
    }

    @Provides
    @Singleton
    fun provideOkHttpCache(application: App): Cache {
        val cacheSize = 10 * 1024 * 1024 // 10 MiB
        return Cache(application.cacheDir, cacheSize.toLong())
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        val gsonBuilder = GsonBuilder()
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        return gsonBuilder.create()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(cache: Cache): OkHttpClient {
        val client = OkHttpClient.Builder()
        client.cache(cache)
        return client.build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(gson: Gson, okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .baseUrl("https://newsapi.org/")
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun providesApi(retrofit: Retrofit): NewsApiService {
        return retrofit.create(NewsApiService::class.java)
    }

    @Provides
    @Singleton
    fun providesPicasso(app: App) = Picasso.with(app)

    @Provides
    @Singleton
    fun providesDb(app: App): NewsDatabase =
        Room.databaseBuilder(app, NewsDatabase::class.java, "newsDatabase.db").build()

    @Provides
    @Singleton
    fun providesNewsRepo(db: NewsDatabase): NewsDao = db.getRepoDao()
}