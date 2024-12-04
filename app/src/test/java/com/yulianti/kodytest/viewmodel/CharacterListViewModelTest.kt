package com.yulianti.kodytest.viewmodel

import app.cash.turbine.test
import com.yulianti.kodytest.data.model.CustomResult
import com.yulianti.kodytest.data.repository.CharacterRepository
import com.yulianti.kodytest.errorResult
import com.yulianti.kodytest.successCharacterListResult
import com.yulianti.kodytest.successLoadMoreResult
import com.yulianti.kodytest.ui.list.CharacterListViewModel
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
class CharacterListViewModelTest {

    private val repository: CharacterRepository = mockk()
    private val testDispatcher = StandardTestDispatcher()
    private val requestLimit = 10
    private val viewModel = CharacterListViewModel(repository, requestLimit)

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
        coEvery { repository.getCharacter(any(), any(), any()) } returns successCharacterListResult
        viewModel.getCharacter()

        viewModel.characterFlow.test {
            assertEquals(null, awaitItem())
            assertEquals(CharacterListViewModel.CharacterUiState(isLoading = true), awaitItem())

            testDispatcher.scheduler.advanceUntilIdle()
            assertEquals(
                CharacterListViewModel.CharacterUiState(
                    items = (successCharacterListResult as CustomResult.Success).data,
                    isLoading = false
                ),
                awaitItem()
            )

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `test load more appends data on success`() = runTest(testDispatcher) {
        coEvery { repository.getCharacter(any(), any(), 0) } returns successCharacterListResult
        coEvery { repository.getCharacter(any(), any(), 1) } returns successLoadMoreResult

        viewModel.getCharacter()
        viewModel.loadMore()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.characterFlow.test {
            val state = awaitItem()
            assertEquals(2, state?.items?.items?.size)
            assertEquals(false, state?.isLoading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `test load more handles error`() = runTest(testDispatcher) {
        coEvery { repository.getCharacter(any(), any(), 0) } returns successCharacterListResult
        coEvery { repository.getCharacter(any(), any(), 1) } returns errorResult

        viewModel.getCharacter()
        viewModel.loadMore()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.characterFlow.test {
            val state = awaitItem()
            assertEquals((errorResult as CustomResult.Error).error, state?.error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `test state flow emits error on initial load`() = runTest(testDispatcher) {
        coEvery { repository.getCharacter(any(), any(), any()) } returns errorResult

        viewModel.getCharacter()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.characterFlow.test {
            val state = awaitItem()
            assertEquals((errorResult as CustomResult.Error).error, state?.error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `test empty when the list is empty`() = runTest(testDispatcher) {
        coEvery { repository.getCharacter(any(), any(), any()) } returns successCharacterListResult

        viewModel.getCharacter()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(false, viewModel.isEmpty())

        coEvery { repository.getCharacter(any(), any(), any()) } returns errorResult
        viewModel.loadMore()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(false, viewModel.isEmpty())
        viewModel.characterFlow.test {
            val state = awaitItem()
            assertEquals((successCharacterListResult as CustomResult.Success).data.items.size, state?.items?.items?.size)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
