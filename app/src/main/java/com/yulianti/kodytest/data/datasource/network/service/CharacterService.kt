package com.yulianti.kodytest.data.datasource.network.service

import com.yulianti.kodytest.data.model.CharacterDataWrapper
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CharacterService {
    @GET("characters")
    suspend fun getCharacter(
        @Query("ts") ts: String,
        @Query("apikey") apiKey: String = "a547256986a81c75ebbdc75a29a1a1d0",
        @Query("hash") hash: String,
    ): CharacterDataWrapper

    @GET("characters/{characterId}")
    suspend fun getCharacterDetail(
        @Path("characterId") characterId: Int,
        @Query("ts") ts: String,
        @Query("apikey") apiKey: String,
        @Query("hash") hash: String,
    ): CharacterDataWrapper
}