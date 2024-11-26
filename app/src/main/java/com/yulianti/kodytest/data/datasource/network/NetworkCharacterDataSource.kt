package com.yulianti.kodytest.data.datasource.network

import com.yulianti.kodytest.BuildConfig
import com.yulianti.kodytest.data.model.Character
import com.yulianti.kodytest.data.model.CustomResult
import com.yulianti.kodytest.data.model.DataError
import com.yulianti.kodytest.data.datasource.network.service.CharacterService
import com.yulianti.kodytest.util.MD5Util
import retrofit2.HttpException
import javax.inject.Inject

class NetworkCharacterDataSource @Inject constructor(private val service: CharacterService) :
    NetworkDataSource {
    override suspend fun fetchCharacters(
        name: String?,
        limit: Int,
        offset: Int
    ): CustomResult<List<Character>, DataError> {
        val timestamp = System.currentTimeMillis().toString()
        return try {
            val result = service.getCharacter(
                timestamp,
                BuildConfig.PUBLIC_KEY,
                MD5Util.md5(timestamp + BuildConfig.PRIVATE_KEY + BuildConfig.PUBLIC_KEY)
            ).data.results.map {
                Character(it.name)
            }
            return CustomResult.Success(result)
        } catch (e: HttpException) {
            when (e.code()) {
                408 -> CustomResult.Error(DataError.Network.REQUEST_TIMEOUT)
                413 -> CustomResult.Error(DataError.Network.PAYLOAD_TOO_LARGE)
                else -> CustomResult.Error(DataError.Network.UNKNOWN)
            }
        }
    }
}