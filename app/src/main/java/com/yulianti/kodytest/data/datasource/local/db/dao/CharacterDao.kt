package com.yulianti.kodytest.data.datasource.local.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.yulianti.kodytest.data.datasource.local.db.entities.CharacterEntity

@Dao
interface CharacterDao {
    @Insert
    suspend fun insertCharacter(user: CharacterEntity)

    @Insert
    suspend fun insertAllCharacter(users: List<CharacterEntity>)

    @Query("SELECT * FROM characters")
    suspend fun getAllCharacters(): List<CharacterEntity>

    @Delete
    suspend fun deleteUser(characterEntity: CharacterEntity)
}