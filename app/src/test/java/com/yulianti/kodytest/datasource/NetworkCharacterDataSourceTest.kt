package com.yulianti.kodytest.datasource

import com.yulianti.kodytest.data.datasource.network.NetworkCharacterDataSource
import com.yulianti.kodytest.data.datasource.network.service.CharacterService
import com.yulianti.kodytest.data.model.CharacterDataWrapper
import com.yulianti.kodytest.data.model.CustomResult
import com.yulianti.kodytest.data.model.Data
import com.yulianti.kodytest.data.model.DataError
import com.yulianti.kodytest.data.model.Result
import com.yulianti.kodytest.data.model.Thumbnail
import com.yulianti.kodytest.data.model.Url
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

@ExperimentalCoroutinesApi
class NetworkCharacterDataSourceTest {

    @MockK
    private lateinit var mockService: CharacterService

    private lateinit var dataSource: NetworkCharacterDataSource

    private val testDispatcher = StandardTestDispatcher()

    private val mockResponse = CharacterDataWrapper(
        attributionHTML = "<a href=\\\"http://marvel.com\\\">Data provided by Marvel. © 2024 MARVEL</a>",
        attributionText = "Data provided by Marvel. © 2024 MARVEL",
        code = 200,
        copyright = "© 2024 MARVEL",
        data = Data(
            count = 1564,
            limit = 20,
            offset = 0,
            results = listOf(
                Result(
                    description = "",
                    id = 1011334,
                    modified = "2014-04-29T14:18:17-0400",
                    name = "3-D Man",
                    resourceURI = "http://gateway.marvel.com/v1/public/characters/1011334",
                    thumbnail = Thumbnail(
                        extension = "jpg",
                        path = "http://i.annihil.us/u/prod/marvel/i/mg/c/e0/535fecbbb9784"
                    ),
                    urls = listOf(
                        Url(
                            type = "detail",
                            url = "http://marvel.com/characters/74/3-d_man?utm_campaign=apiRef&utm_source=a547256986a81c75ebbdc75a29a1a1d0"
                        )
                    )
                )
            ),
            total = 1
        ), etag = "e6414f6e209f93d3c6c193ba2c6a12cf49d7dfc9", status = "Ok"
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        MockKAnnotations.init(this, relaxUnitFun = true)
        dataSource = NetworkCharacterDataSource(mockService)
    }

    @Test
    fun `fetchCharacters should return success when service returns valid data`() = runTest {
        coEvery { mockService.getCharacter(any()) } returns mockResponse

        val result = dataSource.fetchCharacters(name = null, limit = 10, offset = 0)

        assertTrue(result is CustomResult.Success)
        val data = (result as CustomResult.Success).data
        assertEquals(1, data.totalSize)
        assertEquals(1, data.items.size)
        assertEquals("3-D Man", data.items.first().name)
    }

    @Test
    fun `fetchCharacters should return timeout error on HTTP 408`() = runTest {
        coEvery { mockService.getCharacter(any()) } throws HttpException(
            Response.error<Any>(408, "".toResponseBody(null))
        )

        val result = dataSource.fetchCharacters(name = null, limit = 10, offset = 0)

        assertTrue(result is CustomResult.Error)
        val error = (result as CustomResult.Error).error
        assertEquals(DataError.Network.REQUEST_TIMEOUT, error)
    }

    @Test
    fun `fetchCharacters should return payload too large error on HTTP 413`() = runTest {
        coEvery { mockService.getCharacter(any()) } throws HttpException(
            Response.error<Any>(413, "".toResponseBody(null))
        )

        val result = dataSource.fetchCharacters(name = null, limit = 10, offset = 0)

        assertTrue(result is CustomResult.Error)
        val error = (result as CustomResult.Error).error
        assertEquals(DataError.Network.PAYLOAD_TOO_LARGE, error)
    }

    @Test
    fun `fetchCharacters should return unknown error on unexpected exception`() = runTest {
        coEvery { mockService.getCharacter(any()) } throws Exception("Unexpected error")

        val result = dataSource.fetchCharacters(name = null, limit = 10, offset = 0)

        assertTrue(result is CustomResult.Error)
        val error = (result as CustomResult.Error).error
        assertEquals(DataError.Network.UNKNOWN, error)
    }

    @Test
    fun `fetchCharacterDetail should return success when service returns valid data`() = runTest {
        coEvery { mockService.getCharacterDetail(any(), any(), any(), any()) } returns mockResponse

        val result = dataSource.fetchCharacterDetail(characterId = 1)

        assertTrue(result is CustomResult.Success)
        val character = (result as CustomResult.Success).data
        assertEquals(1011334, character.id)
        assertEquals("3-D Man", character.name)
    }

    @Test
    fun `fetchCharacterDetail should return unknown error on exception`() = runTest {
        coEvery {
            mockService.getCharacterDetail(
                any(),
                any(),
                any(),
                any()
            )
        } throws Exception("Unexpected error")

        val result = dataSource.fetchCharacterDetail(characterId = 1)

        assertTrue(result is CustomResult.Error)
        val error = (result as CustomResult.Error).error
        assertEquals(DataError.Network.UNKNOWN, error)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}
