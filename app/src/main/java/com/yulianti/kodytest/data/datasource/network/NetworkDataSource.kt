package com.yulianti.kodytest.data.datasource.network

import com.yulianti.kodytest.data.model.Character
import com.yulianti.kodytest.data.model.CustomResult
import com.yulianti.kodytest.data.model.DataError
import com.yulianti.kodytest.data.model.PaginatedResult

interface NetworkDataSource {
    suspend fun fetchCharacters(name: String?, limit: Int, offset: Int): CustomResult<PaginatedResult<Character>, DataError>
    suspend fun fetchCharacterDetail(characterId: Int): CustomResult<Character, DataError>
}