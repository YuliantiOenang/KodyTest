package com.yulianti.kodytest.data.repository

import com.yulianti.kodytest.data.datasource.local.LocalCharacterDataSource
import com.yulianti.kodytest.data.datasource.network.NetworkDataSource
import com.yulianti.kodytest.data.model.Character
import com.yulianti.kodytest.data.model.CustomResult
import com.yulianti.kodytest.data.model.DataError
import javax.inject.Inject

class ImplCharacterRepository @Inject constructor(
    private val localDataSource: LocalCharacterDataSource,
    private val networkDataSource: NetworkDataSource
): CharacterRepository {
    override suspend fun getCharacter(
        name: String?,
        limit: Int,
        offset: Int
    ): CustomResult<List<Character>, DataError> {
        val local = localDataSource.getCharacter(name, limit, offset)
//        if (local != null) {
//            return local
//        } else {
        return networkDataSource.fetchCharacters(name, limit, offset)
//        }
    }

    override suspend fun getCharacterDetail(id: Int): CustomResult<Character, DataError> {
        return networkDataSource.fetchCharacterDetail(id)
    }
}