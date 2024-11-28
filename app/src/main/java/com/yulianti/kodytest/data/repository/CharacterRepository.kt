package com.yulianti.kodytest.data.repository

import com.yulianti.kodytest.data.model.Character
import com.yulianti.kodytest.data.model.CustomResult
import com.yulianti.kodytest.data.model.DataError
import com.yulianti.kodytest.data.model.PaginatedResult

interface CharacterRepository {
    suspend fun getCharacter(name: String?, limit: Int, offset: Int): CustomResult<PaginatedResult<Character>, DataError>
    suspend fun getCharacterDetail(id: Int): CustomResult<Character, DataError>
}