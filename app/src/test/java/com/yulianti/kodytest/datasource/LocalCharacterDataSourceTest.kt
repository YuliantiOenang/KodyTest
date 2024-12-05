package com.yulianti.kodytest.datasource

import android.database.sqlite.SQLiteFullException
import com.yulianti.kodytest.data.datasource.local.LocalCharacterDataSource
import com.yulianti.kodytest.data.datasource.local.db.dao.CharacterDao
import com.yulianti.kodytest.data.datasource.local.db.entities.CharacterEntity
import com.yulianti.kodytest.data.model.CustomResult
import com.yulianti.kodytest.data.model.DataError
import com.yulianti.kodytest.samplePaginatedResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

import io.mockk.*
import org.junit.After

@OptIn(ExperimentalCoroutinesApi::class)
class LocalCharacterDataSourceTest {

    private lateinit var characterDao: CharacterDao
    private lateinit var localCharacterDataSource: LocalCharacterDataSource
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        characterDao = mockk()
        localCharacterDataSource = LocalCharacterDataSource(characterDao)
    }

    @Test
    fun `getAllCharacter returns success when characters are fetched without a name`() = runTest {
        val mockEntities = listOf(
            CharacterEntity(1, "John Doe", "url1", "Description 1"),
            CharacterEntity(2, "Jane Doe", "url2", "Description 2")
        )
        coEvery { characterDao.getAllCharacters() } returns mockEntities

        val result = localCharacterDataSource.getAllCharacter(null)

        assertTrue(result is CustomResult.Success)
        val success = result as CustomResult.Success
        assertEquals(2, success.data.items.size)
        assertEquals("John Doe", success.data.items[0].name)
        coVerify { characterDao.getAllCharacters() }
    }

    @Test
    fun `getAllCharacter returns success when characters are fetched with a name`() = runTest {
        val mockEntities = listOf(
            CharacterEntity(3, "Filtered Name", "url3", "Description 3")
        )
        coEvery { characterDao.getCharacterByQuery("Filtered") } returns mockEntities

        val result = localCharacterDataSource.getAllCharacter("Filtered")

        assertTrue(result is CustomResult.Success)
        val success = result as CustomResult.Success
        assertEquals(1, success.data.items.size)
        assertEquals("Filtered Name", success.data.items[0].name)
        coVerify { characterDao.getCharacterByQuery("Filtered") }
    }

    @Test
    fun `getAllCharacter returns error when an exception occurs`() = runTest {
        coEvery { characterDao.getAllCharacters() } throws RuntimeException("DB Error")

        val result = localCharacterDataSource.getAllCharacter(null)

        assertTrue(result is CustomResult.Error)
        val error = result as CustomResult.Error
        assertEquals(DataError.Local.UNKNOWN, error.error)
        coVerify { characterDao.getAllCharacters() }
    }

    @Test
    fun `saveCharacter returns success when characters are saved successfully`() = runTest {
        coEvery { characterDao.insertAllCharacter(any()) } just Runs

        val result = localCharacterDataSource.saveCharacter(samplePaginatedResult)

        assertTrue(result is CustomResult.Success)
        coVerify { characterDao.insertAllCharacter(any()) }
    }

    @Test
    fun `saveCharacter returns disk full error when SQLiteFullException is thrown`() = runTest {
        coEvery { characterDao.insertAllCharacter(any()) } throws SQLiteFullException()

        val result = localCharacterDataSource.saveCharacter(samplePaginatedResult)

        assertTrue(result is CustomResult.Error)
        val error = result as CustomResult.Error
        assertEquals(DataError.Local.DISK_FULL, error.error)
        coVerify { characterDao.insertAllCharacter(any()) }
    }

    @Test
    fun `saveCharacter returns unknown error when a general exception is thrown`() = runTest {
        coEvery { characterDao.insertAllCharacter(any()) } throws RuntimeException("General Error")

        val result = localCharacterDataSource.saveCharacter(samplePaginatedResult)

        assertTrue(result is CustomResult.Error)
        val error = result as CustomResult.Error
        assertEquals(DataError.Local.UNKNOWN, error.error)
        coVerify { characterDao.insertAllCharacter(any()) }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}
