import app.cash.turbine.test
import com.yulianti.kodytest.data.model.CustomResult
import com.yulianti.kodytest.data.repository.CharacterRepository
import com.yulianti.kodytest.successCharacterListResult
import com.yulianti.kodytest.ui.viewmodel.CharacterListViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CharacterListViewModelTest {

    private val repository: CharacterRepository = mockk()
    private val testDispatcher = StandardTestDispatcher()
    private val viewModel = CharacterListViewModel(repository, 10)

    @Test
    fun `test state flow emits loading and success`() = runTest {
        // Given
        coEvery { repository.getCharacter(null, 0, 0) } returns successCharacterListResult

        // When & Then
        viewModel.characterFlow.test {
            // Initial state
            assertEquals(CharacterListViewModel.CharacterUiState(isLoading = true), awaitItem())

            // Advance time to let coroutine execute
            testDispatcher.scheduler.advanceUntilIdle()

            // After fetching data
            assertEquals(CharacterListViewModel.CharacterUiState(items = (successCharacterListResult as CustomResult.Success).data), awaitItem())

//            cancelAndIgnoreRemainingEvents()
        }
    }
}
