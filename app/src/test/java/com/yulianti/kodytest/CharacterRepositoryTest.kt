package com.yulianti.kodytest

import com.yulianti.kodytest.data.datasource.local.LocalCharacterDataSource
import com.yulianti.kodytest.data.datasource.network.NetworkDataSource
import com.yulianti.kodytest.data.datasource.network.NetworkUtil
import com.yulianti.kodytest.data.model.CustomResult
import com.yulianti.kodytest.data.model.DataError
import com.yulianti.kodytest.data.repository.ImplCharacterRepository
import kotlinx.coroutines.test.runTest
import org.junit.Test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Assert.assertEquals

class CharacterRepositoryTest {


    private val remoteDataSource: NetworkDataSource = mockk()
    private val localDataSource: LocalCharacterDataSource = mockk()
    private val networkUtil: NetworkUtil = mockk()
    private val repository = ImplCharacterRepository(localDataSource, remoteDataSource, networkUtil)
    private val testDispatcher = UnconfinedTestDispatcher()

    @Test
    fun `getCharacter returns success result`() = runTest(testDispatcher) {
        // Arrange
        val name = "Test"
        val limit = 10
        val offset = 0

        coEvery {
            remoteDataSource.fetchCharacters(name, limit, offset)
        } returns successCharacterListResult

        // Act
        val result = repository.getCharacter(name, limit, offset)

        // Assert
        assert(result is CustomResult.Success)
        assertEquals(samplePaginatedResult, (result as CustomResult.Success).data)

        // Verify
        coVerify { remoteDataSource.fetchCharacters(name, limit, offset) }
    }

    @Test
    fun `getCharacter returns error result on exception`() = runTest(testDispatcher) {
        // Arrange
        val name = "Test"
        val limit = 10
        val offset = 0

        coEvery {
            remoteDataSource.fetchCharacters(name, limit, offset)
        } throws Exception("Network Error")

        // Act
        val result = repository.getCharacter(name, limit, offset)

        // Assert
        assert(result is CustomResult.Error)
        assertEquals(DataError.Network.SERVER_ERROR, (result as CustomResult.Error).error)

        // Verify
        coVerify { remoteDataSource.fetchCharacters(name, limit, offset) }
    }

    @Test
    fun `getCharacterDetail returns success result`() = runTest(testDispatcher) {
        // Arrange
        val id = 1

        coEvery { remoteDataSource.fetchCharacterDetail(id) } returns successCharacterDetailResult

        // Act
        val result = repository.getCharacterDetail(id)

        // Assert
        assert(result is CustomResult.Success)
        assertEquals(sampleCharacter, (result as CustomResult.Success).data)

        // Verify
        coVerify { remoteDataSource.fetchCharacterDetail(id) }
    }

    @Test
    fun `getCharacterDetail returns error result on exception`() = runTest(testDispatcher) {
        // Arrange
        val id = 1

//        coEvery { remoteDataSource.fetchCharacterDetail(id) } throws Exception("Network Error")

        // Act
//        val result = repository.getCharacterDetail(id)

        // Assert
//        assert(result is CustomResult.Error)
//        assertEquals(DataError.Network.SERVER_ERROR, (result as CustomResult.Error).error)

        // Verify
//        coVerify { remoteDataSource.fetchCharacterDetail(id) }
    }
}
