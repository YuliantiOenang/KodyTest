package com.yulianti.kodytest.data.datasource.local.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.yulianti.kodytest.data.datasource.local.db.entities.CharacterEntity

@Dao
interface CharacterDao {
    @Insert
    suspend fun insertCharacter(user: CharacterEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllCharacter(users: List<CharacterEntity>)

    @Query("SELECT * FROM characters ORDER BY name ASC")
    suspend fun getAllCharacters(): List<CharacterEntity>

    @Query("SELECT * FROM characters WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    suspend fun getCharacterByQuery(query: String): List<CharacterEntity>

    @Delete
    suspend fun deleteUser(characterEntity: CharacterEntity)
}