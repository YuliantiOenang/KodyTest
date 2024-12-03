package com.yulianti.kodytest.viewmodel

import app.cash.turbine.test
import com.yulianti.kodytest.data.model.Character
import com.yulianti.kodytest.data.model.CustomResult
import com.yulianti.kodytest.data.model.DataError
import com.yulianti.kodytest.data.repository.CharacterRepository
import com.yulianti.kodytest.errorCharacterDetail
import com.yulianti.kodytest.sampleCharacter
import com.yulianti.kodytest.successCharacterDetailResult
import com.yulianti.kodytest.ui.detail.CharacterDetailViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CharacterDetailViewModelTest {

    private val repository: CharacterRepository = mockk()
    private val testDispatcher = StandardTestDispatcher()
    private val viewModel = CharacterDetailViewModel(repository)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test state flow emits loading and success`() = runTest(testDispatcher) {
        coEvery { repository.getCharacterDetail(any()) } returns successCharacterDetailResult
        viewModel.getCharacterDetail(1)
        viewModel.characterFlow.test {
            assertEquals(null, awaitItem())
            assertEquals(
                CharacterDetailViewModel.CharacterDetailUiState(isLoading = true),
                awaitItem()
            )
            testDispatcher.scheduler.advanceUntilIdle()
            assertEquals(
                CharacterDetailViewModel.CharacterDetailUiState(
                    isLoading = false,
                    feed = sampleCharacter
                ), awaitItem()
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `test state flow emits error`() = runTest(testDispatcher) {
        coEvery { repository.getCharacterDetail(any()) } returns errorCharacterDetail
        viewModel.getCharacterDetail(1)
        viewModel.characterFlow.test {
            assertEquals(null, awaitItem())
            assertEquals(
                CharacterDetailViewModel.CharacterDetailUiState(isLoading = true),
                awaitItem()
            )
            testDispatcher.scheduler.advanceUntilIdle()
            assertEquals(
                CharacterDetailViewModel.CharacterDetailUiState(
                    isLoading = false,
                    error = (errorCharacterDetail as CustomResult.Error<Character, DataError>).error
                ),
                awaitItem()
            )
            cancelAndIgnoreRemainingEvents()
        }
    }
}