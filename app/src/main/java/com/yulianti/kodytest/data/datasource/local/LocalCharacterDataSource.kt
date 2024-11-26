package com.yulianti.kodytest.data.datasource.local

import com.yulianti.kodytest.data.datasource.local.db.dao.CharacterDao
import com.yulianti.kodytest.data.model.Character
import com.yulianti.kodytest.data.model.CustomResult
import com.yulianti.kodytest.data.model.DataError
import javax.inject.Inject

class LocalCharacterDataSource @Inject constructor(
    private val characterDao: CharacterDao
) {
    suspend fun getCharacter(
        name: String?,
        limit: Int,
        offset: Int
    ): CustomResult<List<Character>, DataError> {
        return try {
            val result = characterDao.getAllCharacters().map {
                Character(
                    it.name
                )
            }
            CustomResult.Success(result)
        } catch(e: Exception) {
            CustomResult.Error(DataError.Local.DISK_FULL)
        }
    }

    suspend fun saveCharacter(
        name: String?,
        limit: Int,
        offset: Int
    ): CustomResult<List<Character>, DataError> {
        return try {
            val result = characterDao.getAllCharacters().map {
                Character(
                    it.name
                )
            }
            CustomResult.Success(result)
        } catch(e: Exception) {
            CustomResult.Error(DataError.Local.DISK_FULL)
        }
    }
}