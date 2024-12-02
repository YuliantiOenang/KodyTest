package com.yulianti.kodytest.data.datasource.network

import com.yulianti.kodytest.BuildConfig
import com.yulianti.kodytest.data.model.Character
import com.yulianti.kodytest.data.model.CustomResult
import com.yulianti.kodytest.data.model.DataError
import com.yulianti.kodytest.data.datasource.network.service.CharacterService
import com.yulianti.kodytest.data.model.PaginatedResult
import com.yulianti.kodytest.util.MD5Util
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import javax.inject.Inject

class NetworkCharacterDataSource @Inject constructor(private val service: CharacterService) :
    NetworkDataSource {
    override suspend fun fetchCharacters(
        name: String?,
        limit: Int,
        offset: Int
    ): CustomResult<PaginatedResult<Character>, DataError> {
        val timestamp = System.currentTimeMillis().toString()
        return try {
            withContext(Dispatchers.IO) {
                val allResult = service.getCharacter(
                    toMap(
                        name,
                        timestamp,
                        BuildConfig.PUBLIC_KEY,
                        MD5Util.md5(timestamp + BuildConfig.PRIVATE_KEY + BuildConfig.PUBLIC_KEY),
                        limit, offset
                    )
                )
                val result = allResult.data.results.map {
                    Character(
                        it.id,
                        it.name,
                        it.thumbnail.path + "." + it.thumbnail.extension,
                        it.description
                    )
                }
                CustomResult.Success(
                    PaginatedResult(
                        result,
                        allResult.data.total,
                        allResult.data.offset,
                        allResult.data.count
                    )
                )
            }
        } catch (e: HttpException) {
            when (e.code()) {
                408 -> CustomResult.Error(DataError.Network.REQUEST_TIMEOUT)
                413 -> CustomResult.Error(DataError.Network.PAYLOAD_TOO_LARGE)
                else -> CustomResult.Error(DataError.Network.UNKNOWN)
            }
        } catch (e: Exception) {
            CustomResult.Error(DataError.Network.UNKNOWN)
        }
    }

    fun toMap(
        name: String?,
        ts: String,
        apiKey: String,
        hash: String,
        limit: Int,
        offset: Int
    ): MutableMap<String, Any> {
        return if (name.isNullOrEmpty()) {
            mutableMapOf(
                "ts" to ts,
                "apikey" to apiKey,
                "hash" to hash,
                "limit" to limit,
                "offset" to offset
            )
        } else {
            mutableMapOf(
                "name" to name,
                "ts" to ts,
                "apikey" to apiKey,
                "hash" to hash,
                "limit" to limit,
                "offset" to offset
            )
        }
    }

    override suspend fun fetchCharacterDetail(characterId: Int): CustomResult<Character, DataError> {
        val timestamp = System.currentTimeMillis().toString()
        return try {
            withContext(Dispatchers.IO) {
                val result = service.getCharacterDetail(
                    characterId,
                    timestamp,
                    BuildConfig.PUBLIC_KEY,
                    MD5Util.md5(timestamp + BuildConfig.PRIVATE_KEY + BuildConfig.PUBLIC_KEY)
                ).data.results.first()
                val newResult = Character(
                    result.id,
                    result.name,
                    result.thumbnail.path + "." + result.thumbnail.extension,
                    result.description
                )
                CustomResult.Success(newResult)
            }
        } catch (e: HttpException) {
            when (e.code()) {
                408 -> CustomResult.Error(DataError.Network.REQUEST_TIMEOUT)
                413 -> CustomResult.Error(DataError.Network.PAYLOAD_TOO_LARGE)
                else -> CustomResult.Error(DataError.Network.UNKNOWN)
            }
        } catch (e: Exception) {
            CustomResult.Error(DataError.Network.UNKNOWN)
        }
    }
}