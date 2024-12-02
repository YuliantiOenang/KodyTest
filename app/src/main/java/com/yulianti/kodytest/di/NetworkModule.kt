package com.yulianti.kodytest.di

import android.app.Application
import android.content.Context
import com.yulianti.kodytest.data.datasource.local.db.AppDatabase
import com.yulianti.kodytest.data.datasource.local.db.dao.CharacterDao
import com.yulianti.kodytest.data.datasource.network.NetworkChecker
import com.yulianti.kodytest.data.datasource.network.NetworkCheckerImpl
import com.yulianti.kodytest.data.datasource.network.service.CharacterService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    fun provideContext(application: Application): Context {
        return application
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("https://gateway.marvel.com:443/v1/public/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): CharacterService {
        return retrofit.create(CharacterService::class.java)
    }

    @Provides
    @Singleton
    fun provideAppDatabase(context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    fun provideCharacterDao(appDatabase: AppDatabase): CharacterDao {
        return appDatabase.characterDao()
    }

    @Provides
    @RequestLimit
    fun provideRequestLimit(): Int = 20

    @Provides
    @Singleton
    fun getNetworkChecker(networkCheckerImpl: NetworkCheckerImpl): NetworkChecker {
        return networkCheckerImpl
    }

    @Provides
    @Named("Dispatcher.IO")
    fun provideIoDispatcher(): CoroutineDispatcher {
        return Dispatchers.IO
    }
}