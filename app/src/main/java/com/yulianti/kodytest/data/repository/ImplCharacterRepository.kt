package com.yulianti.kodytest.data.repository

import com.yulianti.kodytest.data.datasource.local.LocalCharacterDataSource
import com.yulianti.kodytest.data.datasource.network.NetworkChecker
import com.yulianti.kodytest.data.datasource.network.NetworkDataSource
import com.yulianti.kodytest.data.model.Character
import com.yulianti.kodytest.data.model.CustomResult
import com.yulianti.kodytest.data.model.DataError
import com.yulianti.kodytest.data.model.PaginatedResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class ImplCharacterRepository @Inject constructor(
    private val localDataSource: LocalCharacterDataSource,
    private val networkDataSource: NetworkDataSource,
    private val networkUtil: NetworkChecker,
    @Named("Dispatcher.IO")
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : CharacterRepository {
    override suspend fun getCharacter(
        name: String?,
        limit: Int,
        offset: Int
    ): CustomResult<PaginatedResult<Character>, DataError> {
        var result: CustomResult<PaginatedResult<Character>, DataError>
        withContext(ioDispatcher) {
            if (networkUtil.isNetworkAvailable()) {
                result = networkDataSource.fetchCharacters(name, limit, offset)
                if (result is CustomResult.Success) {
                    localDataSource.saveCharacter((result as CustomResult.Success).data)
                }

            } else {
                val resultPaginatedList =
                    (localDataSource.getAllCharacter(name) as CustomResult.Success).data
                result = CustomResult.Success(resultPaginatedList)
            }
        }
        return result
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