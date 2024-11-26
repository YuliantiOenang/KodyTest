package com.yulianti.kodytest.di

import com.yulianti.kodytest.data.datasource.network.NetworkCharacterDataSource
import com.yulianti.kodytest.data.datasource.network.NetworkDataSource
import com.yulianti.kodytest.data.repository.CharacterRepository
import com.yulianti.kodytest.data.repository.ImplCharacterRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindCharacterRepository(
        impl: ImplCharacterRepository
    ): CharacterRepository?

    @Binds
    abstract fun bindNetworkCharacterDataSource(
        impl: NetworkCharacterDataSource
    ): NetworkDataSource?
}