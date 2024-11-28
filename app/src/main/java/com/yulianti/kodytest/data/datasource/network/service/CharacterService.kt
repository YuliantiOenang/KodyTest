package com.yulianti.kodytest.data.datasource.network.service

import com.yulianti.kodytest.data.model.CharacterDataWrapper
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface CharacterService {
    @GET("characters")
    suspend fun getCharacter(
        @QueryMap params: MutableMap<String, Any>
    ): CharacterDataWrapper

    @GET("characters/{characterId}")
    suspend fun getCharacterDetail(
        @Path("characterId") characterId: Int,
        @Query("ts") ts: String,
        @Query("apikey") apiKey: String,
        @Query("hash") hash: String,
    ): CharacterDataWrapper
}