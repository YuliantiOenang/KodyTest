package com.yulianti.kodytest.data.repository

import com.yulianti.kodytest.data.datasource.local.LocalCharacterDataSource
import com.yulianti.kodytest.data.datasource.network.NetworkDataSource
import com.yulianti.kodytest.data.datasource.network.NetworkUtil
import com.yulianti.kodytest.data.model.Character
import com.yulianti.kodytest.data.model.CustomResult
import com.yulianti.kodytest.data.model.DataError
import com.yulianti.kodytest.data.model.PaginatedResult
import timber.log.Timber
import javax.inject.Inject

class ImplCharacterRepository @Inject constructor(
    private val localDataSource: LocalCharacterDataSource,
    private val networkDataSource: NetworkDataSource,
    private val networkUtil: NetworkUtil
) : CharacterRepository {
    override suspend fun getCharacter(
        name: String?,
        limit: Int,
        offset: Int
    ): CustomResult<PaginatedResult<Character>, DataError> {
        return try {
            if (networkUtil.isNetworkAvailable()) {
                val result = networkDataSource.fetchCharacters(name, limit, offset)
                localDataSource.saveCharacter((result as CustomResult.Success).data)
                result

            } else {
                val localResult =
                    (localDataSource.getAllCharacter(name) as CustomResult.Success).data
                return if (localResult.isNotEmpty()) {
                    CustomResult.Success(PaginatedResult(localResult, localResult.size, 0, 0))
                } else {
                    CustomResult.Error(DataError.Local.UNKNOWN)
                }

            }
        } catch (e: Exception) {
            Timber.e("${e.stackTrace}")
            CustomResult.Error(DataError.Local.UNKNOWN)
        }
    }

    override suspend fun getCharacterDetail(id: Int): CustomResult<Character, DataError> {
        return try {
            networkDataSource.fetchCharacterDetail(id)
        } catch (e: Exception) {
            Timber.e("${e.stackTrace}")
            CustomResult.Error(DataError.Local.UNKNOWN)
        }
    }
}