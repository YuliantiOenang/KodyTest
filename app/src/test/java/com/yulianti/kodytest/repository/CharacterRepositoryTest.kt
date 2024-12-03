package com.yulianti.kodytest.repository

import com.yulianti.kodytest.data.datasource.local.LocalCharacterDataSource
import com.yulianti.kodytest.data.datasource.network.NetworkChecker
import com.yulianti.kodytest.data.datasource.network.NetworkDataSource
import com.yulianti.kodytest.data.model.CustomResult
import com.yulianti.kodytest.data.model.DataError
import com.yulianti.kodytest.data.repository.ImplCharacterRepository
import com.yulianti.kodytest.errorCharacterDetail
import com.yulianti.kodytest.errorResult
import com.yulianti.kodytest.sampleCharacter
import com.yulianti.kodytest.samplePaginatedResult
import com.yulianti.kodytest.successCharacterDetailResult
import com.yulianti.kodytest.successCharacterListResult
import kotlinx.coroutines.test.runTest
import org.junit.Test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.StandardTestDispatcher
import org.junit.Assert.assertEquals

class CharacterRepositoryTest {


    private val remoteDataSource: NetworkDataSource = mockk()
    private val localDataSource: LocalCharacterDataSource = mockk()
    private val networkUtil: NetworkChecker = mockk()
    private val testDispatcher = StandardTestDispatcher()
    private val repository = ImplCharacterRepository(localDataSource, remoteDataSource, networkUtil, testDispatcher)

    @Test
    fun `getCharacter returns success result`() = runTest(testDispatcher) {
        val name = "Test"
        val limit = 10
        val offset = 0

        coEvery {
            networkUtil.isNetworkAvailable()
        } returns true

        coEvery {
            remoteDataSource.fetchCharacters(name, limit, offset)
        } returns successCharacterListResult

        coEvery { localDataSource.saveCharacter(any()) } returns  CustomResult.Success(Unit)

        val result = repository.getCharacter(name, limit, offset)

        assert(result is CustomResult.Success)
        assertEquals(samplePaginatedResult, (result as CustomResult.Success).data)

        // Verify
        coVerify { remoteDataSource.fetchCharacters(name, limit, offset) }
    }

    @Test
    fun `getCharacter returns error result on exception`() = runTest(testDispatcher) {
        val name = "Test"
        val limit = 10
        val offset = 0

        coEvery {
            remoteDataSource.fetchCharacters(name, limit, offset)
        } returns errorResult

        coEvery {
            networkUtil.isNetworkAvailable()
        } returns true

        val result = repository.getCharacter(name, limit, offset)

        assert(result is CustomResult.Error)
        assertEquals(DataError.Network.SERVER_ERROR, (result as CustomResult.Error).error)

        coVerify { remoteDataSource.fetchCharacters(name, limit, offset) }
    }

    @Test
    fun `getCharacterDetail returns success result`() = runTest(testDispatcher) {
        val id = 1

        coEvery { remoteDataSource.fetchCharacterDetail(id) } returns successCharacterDetailResult

        val result = repository.getCharacterDetail(id)

        assert(result is CustomResult.Success)
        assertEquals(sampleCharacter, (result as CustomResult.Success).data)

        coVerify { remoteDataSource.fetchCharacterDetail(id) }
    }

    @Test
    fun `getCharacterDetail returns error result on exception`() = runTest(testDispatcher) {
        val id = 1

        coEvery { remoteDataSource.fetchCharacterDetail(id) } returns errorCharacterDetail

        val result = repository.getCharacterDetail(id)

        assert(result is CustomResult.Error)
        assertEquals(DataError.Network.SERVER_ERROR, (result as CustomResult.Error).error)

        coVerify { remoteDataSource.fetchCharacterDetail(id) }
    }
}
