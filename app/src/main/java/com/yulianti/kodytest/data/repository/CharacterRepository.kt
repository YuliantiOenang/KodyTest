package com.yulianti.kodytest.data.repository

import com.yulianti.kodytest.data.model.Character
import com.yulianti.kodytest.data.model.CustomResult
import com.yulianti.kodytest.data.model.DataError

interface CharacterRepository {
    suspend fun getCharacter(name: String?, limit: Int, offset: Int): CustomResult<List<Character>, DataError>
}