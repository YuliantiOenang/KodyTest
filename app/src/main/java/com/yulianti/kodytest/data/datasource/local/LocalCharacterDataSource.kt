package com.yulianti.kodytest.data.datasource.local

import android.database.sqlite.SQLiteFullException
import com.yulianti.kodytest.data.datasource.local.db.dao.CharacterDao
import com.yulianti.kodytest.data.datasource.local.db.entities.CharacterEntity
import com.yulianti.kodytest.data.model.Character
import com.yulianti.kodytest.data.model.CustomResult
import com.yulianti.kodytest.data.model.DataError
import com.yulianti.kodytest.data.model.PaginatedResult
import javax.inject.Inject

class LocalCharacterDataSource @Inject constructor(
    private val characterDao: CharacterDao
) {
    suspend fun getAllCharacter(
        name: String?
    ): CustomResult<List<Character>, DataError> {
        return try {
            val result = if (name?.isNotEmpty() == true) {
                characterDao.getCharacterByQuery(name).map {
                    Character(
                        it.id,
                        it.name,
                        it.coverUrl,
                        it.description
                    )
                }
            } else {
                characterDao.getAllCharacters().map {
                    Character(
                        it.id,
                        it.name,
                        it.coverUrl,
                        it.description
                    )
                }
            }
            CustomResult.Success(result)
        } catch(e: Exception) {
            CustomResult.Error(DataError.Local.UNKNOWN)
        }
    }

    suspend fun saveCharacter(
        characters: PaginatedResult<Character>
    ): CustomResult<List<Character>, DataError> {
        return try {
            characterDao.insertAllCharacter(characters.items.map { CharacterEntity(it.id, it.name, it.coverUrl, it.description) })
            CustomResult.Success(listOf())
        }  catch (e: SQLiteFullException) {
            CustomResult.Error(DataError.Local.DISK_FULL)
        } catch (e: Exception) {
            CustomResult.Error(DataError.Local.UNKNOWN)
        }
    }
}